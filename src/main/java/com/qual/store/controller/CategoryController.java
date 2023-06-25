package com.qual.store.controller;

import com.qual.store.converter.CategoryConverter;
import com.qual.store.converter.ProductConverter;
import com.qual.store.dto.CategoryDto;
import com.qual.store.dto.ProductDto;
import com.qual.store.service.CategoryService;
import com.qual.store.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
