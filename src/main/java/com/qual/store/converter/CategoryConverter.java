package com.qual.store.converter;

import com.qual.store.converter.base.BaseConverter;
import com.qual.store.dto.CategoryDto;
import com.qual.store.model.base.BaseEntity;
import com.qual.store.model.Category;
import com.qual.store.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CategoryConverter extends BaseConverter<Category, CategoryDto> {
    private final ProductRepository productRepository;

    @Override
    public Category convertDtoToModel(CategoryDto dto) {
        return Category.builder()
                .name(dto.getName())
                .products(productRepository.findAllById(dto.getProductIds()))
                .build();
    }

    @Override
    public CategoryDto convertModelToDto(Category category) {
        CategoryDto categoryDto = CategoryDto.builder()
                .name(category.getName())
                .productIds(category.getProducts().stream().map(BaseEntity::getId).collect(Collectors.toList()))
                .build();
        categoryDto.setId(category.getId());
        return categoryDto;
    }
}
