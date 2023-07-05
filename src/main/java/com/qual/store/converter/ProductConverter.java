package com.qual.store.converter;

import com.qual.store.converter.base.BaseConverter;
import com.qual.store.dto.ProductDto;
import com.qual.store.model.base.BaseEntity;
import com.qual.store.model.Product;
import com.qual.store.repository.AppUserRepository;
import com.qual.store.repository.CategoryRepository;
import com.qual.store.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductConverter extends BaseConverter<Product, ProductDto> {

    private final CategoryRepository categoryRepository;
    private final AppUserRepository appUserRepository;
    private final ReviewRepository reviewRepository;

    @Override
    public Product convertDtoToModel(ProductDto dto) {
        return Product.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .category(categoryRepository.findById(dto.getCategoryId()).orElse(null))
                .user(appUserRepository.findById(dto.getUserId()).orElse(null))
                .reviews(dto.getReviewsId().stream().map(revId -> reviewRepository.findById(revId).orElseThrow()).collect(Collectors.toList()))
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
                .userId(product.getUser().getId())
                .reviewsId(product.getReviews().stream().map(BaseEntity::getId).collect(Collectors.toList()))
                .imageName(product.getImage().getName())
                .build();

        productDto.setId(product.getId());

        return productDto;
    }
}
