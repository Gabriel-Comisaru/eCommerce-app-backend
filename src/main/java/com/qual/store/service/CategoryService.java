package com.qual.store.service;

import com.qual.store.model.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryService {
    List<Category> getAllCategories();

    Category saveCategory(String categoryName);

    Category updateCategory(Long id, Category category);

    Category findCategoryById(Long id);

    void deleteCategoryById(Long id);

}
