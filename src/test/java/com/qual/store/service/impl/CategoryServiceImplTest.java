package com.qual.store.service.impl;

import com.qual.store.exceptions.CategoryNotFoundException;
import com.qual.store.exceptions.ValidatorException;
import com.qual.store.model.Category;
import com.qual.store.repository.CategoryRepository;
import com.qual.store.utils.validators.Validator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private Validator<Category> validator;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getAllCategoriesTest() {
        // given
        Category category = new Category();
        category.setId(1L);
        category.setName("Test Category");

        List<Category> expectedResult = new ArrayList<>();
        expectedResult.add(category);

        // when
        when(categoryRepository.findAllWithProducts()).thenReturn(expectedResult);
        List<Category> actualResult = categoryService.getAllCategories();

        // then
        assertEquals(expectedResult, actualResult);
        verify(categoryRepository, times(1)).findAllWithProducts();
    }

    @Test
    public void saveCategoryTest() {
        // given
        String categoryName = "Test Category";
        Category category = Category.builder()
                .name(categoryName)
                .build();

        Category expectedResult = new Category();
        expectedResult.setId(1L);
        expectedResult.setName(categoryName);

        // when
        when(categoryRepository.save(category)).thenReturn(expectedResult);
        when(categoryRepository.findAllWithProducts()).thenReturn(List.of(expectedResult));
        Category actualResult = categoryService.saveCategory(categoryName);

        // then
        assertEquals(expectedResult, actualResult);
        verify(validator, times(1)).validate(category);
        verify(categoryRepository, times(1)).save(category);
        verify(categoryRepository, times(1)).findAllWithProducts();
    }

    @Test
    public void saveCategoryThrowsUnexpectedExceptionTest() {
        // given
        String categoryName = "Test Category";
        Category category = Category.builder()
                .name(categoryName)
                .build();

        // when
        when(categoryRepository.findAllWithProducts()).thenReturn(new ArrayList<>());

        // then
        assertThrows(IllegalArgumentException.class,
                () -> categoryService.saveCategory(categoryName));
        verify(validator, times(1)).validate(category);

    }

    @Test
    public void saveCategoryThrowsExceptionWhenValidateTest() {
        // given
        String categoryName = "Test Category";
        Category category = Category.builder()
                .name(categoryName)
                .build();

        // when
        doThrow(new ValidatorException("Validation failed")).when(validator).validate(category);

        // then
        Assertions.assertThrows(ValidatorException.class, () -> categoryService.saveCategory(categoryName));

        verify(validator, times(1)).validate(category);
        verify(categoryRepository, never()).save(any(Category.class));
        verify(categoryRepository, never()).findAllWithProducts();
    }

    @Test
    public void updateCategoryThrowsExceptionWhenValidateTest() {
        // given
        String categoryName = "Test Category";
        Category category = Category.builder()
                .name(categoryName)
                .build();

        // when
        doThrow(new ValidatorException("Validation failed")).when(validator).validate(category);

        // then
        Assertions.assertThrows(ValidatorException.class, () -> categoryService.saveCategory(categoryName));

        verify(validator, times(1)).validate(category);
        verify(categoryRepository, never()).findById(eq(1L));
        verify(categoryRepository, never()).findAllWithProducts();

    }

    @Test
    public void updateCategoryTest() {
        // given
        Long categoryId = 1L;

        Category existingCategory = new Category();
        existingCategory.setId(categoryId);
        existingCategory.setName("Existing Category");

        Category expectedResult = new Category();
        expectedResult.setId(categoryId);
        expectedResult.setName("Updated Category");

        // when
        when(categoryRepository.findById(eq(1L))).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.findAllWithProducts()).thenReturn(List.of(expectedResult));
        when(categoryRepository.save(any(Category.class))).thenReturn(expectedResult);
        Category actualResult = categoryService.updateCategory(categoryId, expectedResult);

        // then
        assertEquals(expectedResult, actualResult);
        verify(validator, times(1)).validate(expectedResult);
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, times(1)).findAllWithProducts();
    }

    @Test
    public void updateCategoryThrowsUnexpectedExceptionTest() {
        // given
        Long categoryId = 1L;

        Category existingCategory = new Category();
        existingCategory.setId(categoryId);
        existingCategory.setName("Existing Category");

        // when
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.findAllWithProducts()).thenReturn(new ArrayList<>());

        // then
        assertThrows(IllegalArgumentException.class,
                () -> categoryService.updateCategory(categoryId, existingCategory));
        verify(validator, times(1)).validate(existingCategory);
    }

    @Test
    public void updateCategoryThrowsCategoryNotFoundExceptionTest() {
        // given
        Long categoryId = 1L;

        Category updatedCategory = new Category();
        updatedCategory.setId(categoryId);
        updatedCategory.setName("Updated Category");

        // when
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // then
        Assertions.assertThrows(CategoryNotFoundException.class,
                () -> categoryService.updateCategory(categoryId, updatedCategory));

        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, never()).findAllWithProducts();
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void findCategoryByIdTest() {
        // given
        Long categoryId = 1L;

        Category expectedResult = new Category();
        expectedResult.setId(categoryId);
        expectedResult.setName("Test Category");

        // when
        when(categoryRepository.findCategoryWithProducts(categoryId)).thenReturn(Optional.of(expectedResult));
        Category actualResult = categoryService.findCategoryById(categoryId);

        // then
        assertEquals(expectedResult, actualResult);
        verify(categoryRepository, times(1)).findCategoryWithProducts(categoryId);
    }

    @Test
    void findCategoryByIdThrowsCategoryNotFoundExceptionTest() {
        // given
        Long categoryId = 1L;

        // when
        when(categoryRepository.findCategoryWithProducts(categoryId)).thenReturn(Optional.empty());

        // then
        assertThrows(CategoryNotFoundException.class,
                () -> categoryService.findCategoryById(categoryId));
        verify(categoryRepository, times(1)).findCategoryWithProducts(categoryId);
    }

    @Test
    void deleteCategoryByIdTest() {
        // given
        Long categoryId = 1L;

        Category category = new Category();
        category.setId(categoryId);
        category.setName("Test Category");

        // when
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        categoryService.deleteCategoryById(categoryId);

        // then
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, times(1)).deleteById(categoryId);
    }

    @Test
    void deleteCategoryByIdThrowsCategoryNotFoundExceptionTest() {
        // given
        Long categoryId = 1L;

        // when
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // then
        assertThrows(CategoryNotFoundException.class,
                () -> categoryService.deleteCategoryById(categoryId));
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, never()).deleteById(anyLong());
    }

    @AfterEach
    public void closeService() throws Exception {
        closeable.close();
    }
}