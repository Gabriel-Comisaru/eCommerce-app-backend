package com.qual.store.service.impl;

import com.qual.store.converter.ReviewConverter;
import com.qual.store.dto.ReviewDto;
import com.qual.store.dto.request.ReviewRequestDto;
import com.qual.store.exceptions.ProductNotFoundException;
import com.qual.store.exceptions.ReviewNotFoundException;
import com.qual.store.logger.Log;
import com.qual.store.model.Product;
import com.qual.store.model.Review;
import com.qual.store.repository.ProductRepository;
import com.qual.store.repository.ReviewRepository;
import com.qual.store.service.ReviewService;
import com.qual.store.utils.validators.ReviewValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private final ReviewRepository reviewRepository;

    @Autowired
    private final ProductRepository productRepository;

    @Autowired
    private final ReviewConverter reviewConverter;

    @Autowired
    private ReviewValidator validator;

    @Log
    @Override
    public List<ReviewDto> getAllReviews() {
        return reviewRepository.findAll()
                .stream()
                .map(reviewConverter::convertModelToDto)
                .toList();
    }

    @Log
    @Override
    @Transactional
    public ReviewDto saveReview(Long productId, ReviewRequestDto reviewRequestDto) {
        validator.validate(reviewRequestDto);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(String.format("no product with id = %s", productId)));

        Review review = reviewConverter.convertRequestDtoToModel(reviewRequestDto);

        review.setProduct(product);
        product.addReview(review);

        productRepository.save(product);

        return reviewConverter.convertModelToDto(reviewRepository
                .findByTitle(review.getTitle())
                .orElseThrow()
        );
    }

    @Log
    @Override
    @Transactional
    public ReviewDto updateReview(Long id, ReviewRequestDto reviewRequestDto) {
        validator.validate(reviewRequestDto);

        Optional<Review> reviewOptional = reviewRepository.findById(id);

        reviewOptional
                .orElseThrow(() -> new ProductNotFoundException(String.format("no review with id = %s", id)));

        reviewOptional.ifPresent(review -> {
            review.setTitle(reviewRequestDto.getTitle());
            review.setComment(reviewRequestDto.getComment());
            review.setRating(reviewRequestDto.getRating());
        });

        return reviewConverter.convertModelToDto(reviewRepository.findById(id).orElseThrow());
    }

    @Log
    @Override
    public ReviewDto findReviewById(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException(String.format("no review with id = %s", id)));

        return reviewConverter.convertModelToDto(review);
    }

    @Log
    @Override
    @Transactional
    public void deleteReviewById(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException(String.format("no review with id = %s", id)));

        Product product = productRepository.findById(review.getProduct().getId())
                .orElseThrow();

        product.getReviews().remove(review);

        productRepository.save(product);
        reviewRepository.delete(review);
    }
}
