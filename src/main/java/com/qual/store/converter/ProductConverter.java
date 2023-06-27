package com.qual.store.converter;

import com.qual.store.dto.ProductDto;
import com.qual.store.model.BaseEntity;
import com.qual.store.model.Product;
import com.qual.store.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ProductConverter extends BaseConverter<Product, ProductDto> {

    @Autowired
    private CategoryConverter categoryConverter;
    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public Product convertDtoToModel(ProductDto dto) {
        return Product.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .category(categoryRepository.findById(dto.getCategoryId()).orElse(null))
                .build();
    }

    @Override
    public ProductDto convertModelToDto(Product product) {
       ProductDto productDto = ProductDto.builder()
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .orderItems(product.getOrderItems().stream().map(BaseEntity::getId).collect(Collectors.toList()))
                .categoryId(product.getCategory().getId())
                .build();
       productDto.setId(product.getId());

        return productDto;
    }
}
