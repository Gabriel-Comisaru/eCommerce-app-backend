package com.qual.store.converter;

import com.qual.store.converter.base.BaseConverter;
import com.qual.store.dto.ProductDto;
import com.qual.store.dto.request.ProductRequestDto;
import com.qual.store.model.ImageModel;
import com.qual.store.model.Product;
import com.qual.store.model.base.BaseEntity;
import com.qual.store.repository.AppUserRepository;
import com.qual.store.repository.CategoryRepository;
import com.qual.store.repository.ImageRepository;
import com.qual.store.repository.ReviewRepository;
import com.qual.store.utils.ProductRatingCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductConverter extends BaseConverter<Product, ProductDto> {

    private final CategoryRepository categoryRepository;
    private final AppUserRepository appUserRepository;
    private final ReviewRepository reviewRepository;
    private final ImageRepository imageRepository;
    private final ProductRatingCalculator productRatingCalculator;

    @Override
    public Product convertDtoToModel(ProductDto dto) {
        return Product.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .unitsInStock(dto.getUnitsInStock())
                .discountPercentage(dto.getDiscountPercentage())
                .category(categoryRepository.findById(dto.getCategoryId()).orElse(null))
                .user(appUserRepository.findById(dto.getUserId()).orElse(null))
                .reviews(dto.getReviewsId().stream()
                        .map(revId -> reviewRepository.findById(revId).orElseThrow())
                        .collect(Collectors.toList()))
                .images(dto.getImagesName().stream()
                        .map(name -> imageRepository.findByName(name).orElse(null))
                        .collect(Collectors.toSet()))
                .build();
    }

    @Override
    public ProductDto convertModelToDto(Product product) {
        ProductDto productDto = ProductDto.builder()
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .unitsInStock(product.getUnitsInStock())
                .discountPercentage(product.getDiscountPercentage())
                .rating(productRatingCalculator.calculateRating(product))
                .createTime(product.getCreateTime())
                .updateTime(product.getUpdateTime())
                .orderItems(product.getOrderItems().stream().map(BaseEntity::getId).collect(Collectors.toList()))
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .userId(product.getUser().getId())
                .reviewsId(product.getReviews().stream().map(BaseEntity::getId).collect(Collectors.toList()))
                .imagesName(product.getImages().stream().map(ImageModel::getName).toList())
                .favUserIds(product.getFavoriteByUsers().stream().map(BaseEntity::getId).collect(Collectors.toList()))
                .build();

        productDto.setId(product.getId());

        return productDto;
    }

    public Product convertRequestToModel(ProductRequestDto productRequestDto) {
        return Product.builder()
                .name(productRequestDto.getName())
                .description(productRequestDto.getDescription())
                .price(productRequestDto.getPrice())
                .unitsInStock(productRequestDto.getUnitsInStock())
                .discountPercentage(productRequestDto.getDiscountPercentage())
                .images(new HashSet<>())
                .reviews(new ArrayList<>())
                .orderItems(new HashSet<>())
                .favoriteByUsers(new HashSet<>())
                .build();
    }

}
