package com.qual.store.service;

import com.qual.store.dto.ReviewDto;
import com.qual.store.dto.request.ReviewRequestDto;

import java.util.List;

public interface ReviewService {
    List<ReviewDto> getAllReviews();
    public List<ReviewDto> getReviewsByProductId(Long productId);

    ReviewDto saveReview(Long productId, ReviewRequestDto reviewRequestDto);

    ReviewDto updateReview(Long id, ReviewRequestDto reviewRequestDto);

    ReviewDto findReviewById(Long id);

    void deleteReviewById(Long id);
}
