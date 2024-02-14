package com.qual.store.converter;

import com.qual.store.dto.CategoryDto;
import com.qual.store.model.Category;
import com.qual.store.model.Product;
import com.qual.store.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CategoryConverterTest {
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CategoryConverter categoryConverter;

    private AutoCloseable closeable;

    @BeforeEach
    public void setup() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @Test
    public void convertDtoToModelTest() {
        // given
        String categoryName = "Category";
        List<Long> productIds = List.of(1L, 2L);

        CategoryDto categoryDto = CategoryDto.builder()
                .name(categoryName)
                .productIds(productIds)
                .build();

        Category expectedCategory = Category.builder()
                .name(categoryName)
                .products(new ArrayList<>())
                .build();

        // when
        when(productRepository.findAllById(productIds)).thenReturn(new ArrayList<>());
        Category actualCategory = categoryConverter.convertDtoToModel(categoryDto);

        // then
        assertEquals(expectedCategory, actualCategory);
        verify(productRepository, times(1)).findAllById(productIds);
    }

    @Test
    public void convertModelToDtoTest() {
        // given
        Long categoryId = 1L;
        String categoryName = "Category";
        List<Long> productIds = List.of(1L, 2L);

        Product productOne = new Product();
        productOne.setId(1L);

        Product productTwo = new Product();
        productTwo.setId(2L);

        Category category = Category.builder()
                .name(categoryName)
                .products(Arrays.asList(productOne, productTwo))
                .build();
        category.setId(categoryId);

        CategoryDto expectedCategoryDto = CategoryDto.builder()
                .name(categoryName)
                .productIds(productIds)
                .build();
        expectedCategoryDto.setId(categoryId);

        // when
        CategoryDto actualCategoryDto = categoryConverter.convertModelToDto(category);

        // then
        assertEquals(expectedCategoryDto, actualCategoryDto);
    }

    @AfterEach
    public void closeService() throws Exception {
        closeable.close();
    }
}