package com.qual.store.controller;

import com.qual.store.dto.ReviewDto;
import com.qual.store.dto.request.ReviewRequestDto;
import com.qual.store.logger.Log;
import com.qual.store.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/reviews")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ReviewController {

    private final ReviewService reviewService;

    @Log
    @GetMapping
    public ResponseEntity<List<ReviewDto>> getAllReviews() {
        return ResponseEntity.ok(reviewService.getAllReviews());
    }

    @Log
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ReviewDto>> getReviewsByProductId(@PathVariable("productId") Long productId) {
        return ResponseEntity.ok(reviewService.getReviewsByProductId(productId));
    }

    @Log
    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewDto> getReviewById(@PathVariable("reviewId") Long reviewId) {
        return ResponseEntity.ok(reviewService.findReviewById(reviewId));
    }

    @Log
    @PostMapping("/save/{productId}")
    public ResponseEntity<ReviewDto> saveReview(@PathVariable("productId") Long productId,
                                                @RequestBody ReviewRequestDto reviewRequestDto) {

        return ResponseEntity.ok(reviewService.saveReview(productId, reviewRequestDto));
    }

    @Log
    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewDto> updateReview(@PathVariable("reviewId") Long reviewId,
                                                  @RequestBody ReviewRequestDto reviewRequestDto) {

        return ResponseEntity.ok(reviewService.updateReview(reviewId, reviewRequestDto));
    }

    @Log
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable("reviewId") Long reviewId) {
        reviewService.deleteReviewById(reviewId);

        return ResponseEntity.ok(String.format("review with id = %s deleted", reviewId));
    }
}
