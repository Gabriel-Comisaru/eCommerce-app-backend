package com.qual.store.converter;

import com.qual.store.converter.base.BaseConverter;
import com.qual.store.dto.ReviewDto;
import com.qual.store.dto.request.ReviewRequestDto;
import com.qual.store.model.Review;
import com.qual.store.repository.AppUserRepository;
import com.qual.store.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ReviewConverter extends BaseConverter<Review, ReviewDto> {

    private final ProductRepository productRepository;
    private final AppUserRepository appUserRepository;

    @Override
    public Review convertDtoToModel(ReviewDto dto) {
        return Review.builder()
                .rating(dto.getRating())
                .title(dto.getTitle())
                .comment(dto.getComment())
                .date(dto.getDate())
                .product(productRepository.findById(dto.getProductId()).orElse(null))
                .user(appUserRepository.findById(dto.getUserId()).orElse(null))
                .build();
    }

    @Override
    public ReviewDto convertModelToDto(Review review) {
        ReviewDto reviewDto = ReviewDto.builder()
                .rating(review.getRating())
                .title(review.getTitle())
                .comment(review.getComment())
                .date(review.getDate())
                .productId(review.getProduct().getId())
                .userId(review.getUser().getId())
                .username(review.getUser().getUsername())
                .fullName(review.getUser().getFirstName() + " " + review.getUser().getLastName())
                .build();

        reviewDto.setId(review.getId());
        return reviewDto;
    }

    public Review convertRequestDtoToModel(ReviewRequestDto reviewRequestDto) {
        return Review.builder()
                .rating(reviewRequestDto.getRating())
                .title(reviewRequestDto.getTitle())
                .comment(reviewRequestDto.getComment())
                .date(LocalDateTime.now())
                .build();
    }
}
