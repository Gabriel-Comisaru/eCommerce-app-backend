package com.qual.store.service.impl;

import com.qual.store.exceptions.CategoryNotFoundException;
import com.qual.store.logger.Log;
import com.qual.store.model.Category;
import com.qual.store.repository.CategoryRepository;
import com.qual.store.service.CategoryService;
import com.qual.store.utils.validators.Validator;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private Validator<Category> validator;

//    @Override
//    @Log
//    public List<Category> getAllCategories() {
//        return categoryRepository.findAll();
//    }

    @Override
    @Log
    public List<Category> getAllCategories() {
       List<Category> categories = categoryRepository.findAllWithProducts();
         return categories;
    }
    @Override
    @Log
    public Optional<Category> saveCategory(Category category) {
        validator.validate(category);
        Category savedCategory = categoryRepository.save(category);

        Category result = categoryRepository.findAllWithProducts().stream()
                .filter(c -> c.getId().equals(savedCategory.getId()))
                .findFirst().orElseThrow();

        return Optional.of(result);
    }
    
@Transactional
@Override
@Log
public Optional<Category> updateCategory(Long id, Category category) {
    validator.validate(category);
    Optional<Category> optionalCategory = categoryRepository.findById(id);

    optionalCategory
            .orElseThrow(() -> new CategoryNotFoundException(String.format("No category found with id %s", id)));

    optionalCategory
            .ifPresent(updateCategory -> updateCategory.setName(category.getName()));

    Category result = categoryRepository.findAllWithProducts().stream()
            .filter(c -> c.getId().equals(optionalCategory.get().getId()))
            .findFirst().orElseThrow();

    return Optional.of(result);
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
