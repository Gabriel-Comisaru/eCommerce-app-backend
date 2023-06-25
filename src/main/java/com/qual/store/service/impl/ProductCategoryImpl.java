package com.qual.store.service.impl;

import com.qual.store.model.Category;
import com.qual.store.repository.CategoryRepository;
import com.qual.store.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class ProductCategoryImpl implements CategoryService {
  @Autowired
  private CategoryRepository categoryRepository;

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Optional<Category> saveCategory(Category category) {
        return Optional.empty();
    }

    @Override
    public Optional<Category> updateCategory(Long id, Category category) {
        return Optional.empty();
    }

    @Override
    public Optional<Category> findCategoryById(Long id) {
        return Optional.empty();
    }

    @Override
    public void deleteCategoryById(Long id) {

    }
}
