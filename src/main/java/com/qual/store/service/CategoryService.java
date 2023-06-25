package com.qual.store.service;

import com.qual.store.model.Category;
import com.qual.store.model.Product;

import java.util.List;
import java.util.Optional;

public interface CategoryService {
    List<Category> getAllCategories();
    Optional<Category> saveCategory(Category category);
    Optional<Category> updateCategory(Long id,Category category);

    Optional<Category> findCategoryById(Long id);

    void deleteCategoryById(Long id);

}
