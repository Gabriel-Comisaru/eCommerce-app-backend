package com.qual.store.converter.lazyConverter;


import com.qual.store.converter.base.BaseConverter;
import com.qual.store.dto.lazyDto.ProductDtoWithCategory;
import com.qual.store.model.Product;
import com.qual.store.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
