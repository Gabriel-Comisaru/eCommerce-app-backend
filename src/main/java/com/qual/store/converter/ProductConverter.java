package com.qual.store.converter;

import com.qual.store.dto.ProductDto;
import com.qual.store.model.BaseEntity;
import com.qual.store.model.Product;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ProductConverter extends BaseConverter<Product, ProductDto> {

    @Override
    public Product convertDtoToModel(ProductDto dto) {
        return Product.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .build();
    }

    @Override
    public ProductDto convertModelToDto(Product product) {
       ProductDto productDto = ProductDto.builder()
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .orderItems(product.getOrderItems().stream().map(BaseEntity::getId).collect(Collectors.toList()))
                .build();
       productDto.setId(product.getId());
        return productDto;
    }
}
