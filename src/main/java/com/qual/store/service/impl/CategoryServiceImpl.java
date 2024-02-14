package com.qual.store.service.impl;

import com.qual.store.exceptions.CategoryNotFoundException;
import com.qual.store.logger.Log;
import com.qual.store.model.Category;
import com.qual.store.repository.CategoryRepository;
import com.qual.store.service.CategoryService;
import com.qual.store.utils.validators.Validator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final Validator<Category> validator;

    @Override
    @Log
    public List<Category> getAllCategories() {
        return categoryRepository.findAllWithProducts();
    }

    @Override
    @Log
    public Category saveCategory(String categoryName) {
        Category category = Category.builder()
                .name(categoryName)
                .build();

        validator.validate(category);
        Category savedCategory = categoryRepository.save(category);

        return categoryRepository.findAllWithProducts().stream()
                .filter(c -> c.getId().equals(savedCategory.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("category not saved"));
    }

    @Transactional
    @Override
    @Log
    public Category updateCategory(Long id, Category category) {
        validator.validate(category);
        Optional<Category> optionalCategory = categoryRepository.findById(id);

        optionalCategory
                .orElseThrow(() -> new CategoryNotFoundException(String.format("No category found with id %s", id)));

        optionalCategory
                .ifPresent(updateCategory -> updateCategory.setName(category.getName()));

        return categoryRepository.findAllWithProducts().stream()
                .filter(c -> c.getId().equals(optionalCategory.get().getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("category not saved"));
    }

    @Override
    @Log
    public Category findCategoryById(Long id) {
        return categoryRepository.findCategoryWithProducts(id)
                .orElseThrow(() -> new CategoryNotFoundException(String.format("No category found with id %s", id)));
    }

    @Override
    @Log
    public void deleteCategoryById(Long id) {
        categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(String.format("No category found with id %s", id)));

        categoryRepository.deleteById(id);
    }
}
