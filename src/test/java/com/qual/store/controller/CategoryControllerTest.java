package com.qual.store.controller;

import com.qual.store.converter.CategoryConverter;
import com.qual.store.dto.CategoryDto;
import com.qual.store.model.Category;
import com.qual.store.service.CategoryService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CategoryControllerTest {
    private MockMvc mockMvc;

    @Mock
    private CategoryService categoryService;

    @Mock
    private CategoryConverter categoryConverter;

    @InjectMocks
    private CategoryController categoryController;

    private AutoCloseable closeable;

    @BeforeEach
    public void init() {
        closeable = MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(categoryController)
                .build();
    }

    @Test
    public void getAllCategoriesTest() throws Exception {
        // given
        Category category = new Category();
        category.setId(1L);
        category.setName("Test Category");

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(1L);
        categoryDto.setName("Test Category");

        List<Category> categoryList = new ArrayList<>();
        categoryList.add(category);

        // when
        when(categoryService.getAllCategories()).thenReturn(categoryList);
        when(categoryConverter.convertModelToDto(category)).thenReturn(categoryDto);

        // then
        mockMvc.perform(get("/api/categories")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(categoryDto.getId()))
                .andExpect(jsonPath("$[0].name").value(categoryDto.getName()));

        verify(categoryService, times(1)).getAllCategories();
        verify(categoryConverter, times(1)).convertModelToDto(category);
    }

    @Test
    public void getCategoryByIdTest() throws Exception {
        // given
        Category category = new Category();
        category.setId(1L);
        category.setName("Test Category");

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(1L);
        categoryDto.setName("Test Category");

        // when
        when(categoryService.findCategoryById(1L)).thenReturn(category);
        when(categoryConverter.convertModelToDto(category)).thenReturn(categoryDto);

        // then
        mockMvc.perform(get("/api/categories/{categoryId}", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(categoryDto.getId()))
                .andExpect(jsonPath("$.name").value(categoryDto.getName()));

        verify(categoryService, times(1)).findCategoryById(1L);
        verify(categoryConverter, times(1)).convertModelToDto(category);
    }

    @Test
    public void addCategoryTest() throws Exception {
        // given
        Category category = new Category();
        category.setId(1L);
        category.setName("Test Category");

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(1L);
        categoryDto.setName("Test Category");

        // when
        when(categoryService.saveCategory(anyString())).thenReturn(category);
        when(categoryConverter.convertModelToDto(category)).thenReturn(categoryDto);

        mockMvc.perform(post("/api/categories")
                        .param("categoryName", "Test Category")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(categoryDto.getId()))
                .andExpect(jsonPath("$.name").value(categoryDto.getName()));

        verify(categoryService, times(1)).saveCategory("Test Category");
        verify(categoryConverter, times(1)).convertModelToDto(category);
    }

    @Test
    void updateCategoryTest() throws Exception {
        // given
        Category updatedCategory = new Category();
        updatedCategory.setId(1L);
        updatedCategory.setName("Updated Category");

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(1L);
        categoryDto.setName("Updated Category");

        // when
        when(categoryService.updateCategory(eq(1L), any(Category.class))).thenReturn(updatedCategory);
        when(categoryConverter.convertModelToDto(updatedCategory)).thenReturn(categoryDto);

        // then
        mockMvc.perform(put("/api/categories/{categoryId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"name\":\"Updated Category\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(categoryDto.getId()))
                .andExpect(jsonPath("$.name").value(categoryDto.getName()));

        verify(categoryService, times(1)).updateCategory(eq(1L), any(Category.class));
        verify(categoryConverter, times(1)).convertModelToDto(updatedCategory);
    }

    @Test
    void deleteCategoryTest() throws Exception {
        mockMvc.perform(delete("/api/categories/{categoryId}", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("category with id 1 deleted"));

        verify(categoryService, times(1)).deleteCategoryById(1L);
    }

    @Test
    void populateCategoriesTest() throws Exception {
        mockMvc.perform(post("/api/categories/populate")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("categories populated"));

        verify(categoryService, times(10)).saveCategory(anyString());
    }

    @AfterEach
    public void closeService() throws Exception {
        closeable.close();
    }
}