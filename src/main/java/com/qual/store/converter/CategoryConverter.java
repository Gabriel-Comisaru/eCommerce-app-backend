package com.qual.store.converter;

import com.qual.store.dto.CategoryDto;
import com.qual.store.model.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryConverter extends BaseConverter<Category, CategoryDto> {

    @Override
    public Category convertDtoToModel(CategoryDto dto) {
        return Category.builder()
                .name(dto.getName())
                .products(dto.getProducts())    // todo: convert product dto to model, or return list of product ids
                .build();
    }

    @Override
    public CategoryDto convertModelToDto(Category category) {
        CategoryDto categoryDto = CategoryDto.builder()
                .name(category.getName())
                .products(category.getProducts())   // todo: todo: convert product model to dto, or return list of product ids
                .build();
        categoryDto.setId(category.getId());
        return categoryDto;
    }
}
