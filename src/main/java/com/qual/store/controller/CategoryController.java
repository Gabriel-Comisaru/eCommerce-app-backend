package com.qual.store.controller;

import com.github.javafaker.Faker;
import com.qual.store.converter.CategoryConverter;
import com.qual.store.dto.CategoryDto;
import com.qual.store.logger.Log;
import com.qual.store.model.Category;
import com.qual.store.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/categories")
@RequiredArgsConstructor
@CrossOrigin("*")
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryConverter categoryConverter;

    @GetMapping()
    @Log
    public List<CategoryDto> getAllCategories() {
        return categoryService.getAllCategories().stream()
                .map(categoryConverter::convertModelToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{categoryId}")
    @Log
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable("categoryId") Long categoryId) {
        return ResponseEntity.ok(categoryConverter.convertModelToDto(
                categoryService.findCategoryById(categoryId)
        ));
    }

    @PostMapping
    @Log
    public ResponseEntity<CategoryDto> addCategory(@RequestParam("categoryName") String categoryName) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(categoryConverter.convertModelToDto(
                                categoryService.saveCategory(categoryName)
                        )
                );
    }

    @PutMapping("/{categoryId}")
    @Log
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable Long categoryId, @RequestBody Category category) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categoryConverter.convertModelToDto(
                                categoryService.updateCategory(categoryId, category)
                        )
                );
    }

    @DeleteMapping("/{categoryId}")
    @Log
    public ResponseEntity<String> deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategoryById(categoryId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(String.format("category with id %s deleted", categoryId));
    }

    @PostMapping("/populate")
    @Log
    public ResponseEntity<?> populateCategories() {
        try {
            Faker faker = new Faker();
            for (int i = 0; i < 10; i++) {
                categoryService.saveCategory(faker.commerce().department());
            }

            return ResponseEntity.status(HttpStatus.OK)
                    .body("categories populated");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }
}
