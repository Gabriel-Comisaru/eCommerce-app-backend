package com.qual.store.lazyConverter;


import com.qual.store.converter.BaseConverter;
import com.qual.store.dto.ProductDto;
import com.qual.store.lazyDto.ProductDtoWithCategory;
import com.qual.store.model.BaseEntity;
import com.qual.store.model.Product;
import com.qual.store.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ProductLazyConverter extends BaseConverter<Product, ProductDtoWithCategory> {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public Product convertDtoToModel(ProductDtoWithCategory dto) {
        return Product.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .category(categoryRepository.findById(dto.getCategoryId()).orElse(null))
                .build();
    }

    @Override
    public ProductDtoWithCategory convertModelToDto(Product product) {
        ProductDtoWithCategory productDtoLazy = ProductDtoWithCategory.builder()
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .categoryId(product.getCategory().getId())
                .build();
        productDtoLazy.setId(product.getId());

        return productDtoLazy;
    }
}
