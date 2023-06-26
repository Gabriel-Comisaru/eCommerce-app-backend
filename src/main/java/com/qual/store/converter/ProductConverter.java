package com.qual.store.converter;

import com.qual.store.dto.ProductDto;
import com.qual.store.model.Product;
import com.qual.store.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
                .category(dto.getCategory())
                .build();
    }

    @Override
    public ProductDto convertModelToDto(Product product) {
       ProductDto productDto = ProductDto.builder()
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .category(product.getCategory())
                .build();
       productDto.setId(product.getId());

        return productDto;
    }
}
