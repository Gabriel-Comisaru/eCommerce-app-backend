package com.qual.store.controller;

import com.github.javafaker.Faker;
import com.qual.store.converter.CategoryConverter;
import com.qual.store.converter.ProductConverter;
import com.qual.store.dto.CategoryDto;
import com.qual.store.dto.ProductDto;
import com.qual.store.model.Category;
import com.qual.store.service.CategoryService;
import com.qual.store.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/categories")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryConverter categoryConverter;
    @GetMapping()
    public List<CategoryDto> getAllCategories() {
        return categoryService.getAllCategories().stream()
                .map(category -> categoryConverter.convertModelToDto(category))

                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<?> addCategory(@RequestBody Category category) {
        try {
           Category savedCategory = categoryService.saveCategory(category)
                    .orElseThrow(() -> new IllegalArgumentException("category not saved"));
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(categoryConverter.convertModelToDto(savedCategory));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<?> updateCategory(@PathVariable Long categoryId, @RequestBody Category category) {
        try {
            Category categoryUpdated = categoryService.updateCategory(categoryId,category)
                    .orElseThrow(() -> new IllegalArgumentException("category not saved"));

            CategoryDto responseCategoryDto = categoryConverter.convertModelToDto(categoryUpdated);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(responseCategoryDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long categoryId) {
        try {
            categoryService.deleteCategoryById(categoryId);
            return ResponseEntity.status(HttpStatus.OK)
                    .body("category deleted");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    @PostMapping("/populate")
    public ResponseEntity<?> populateCategories() {
        try {
            Faker faker = new Faker();
            for (int i = 0; i < 10; i++) {
                Category category = new Category();
                category.setName(faker.commerce().department());
                categoryService.saveCategory(category);
            }

            return ResponseEntity.status(HttpStatus.OK)
                    .body("categories populated");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }
}