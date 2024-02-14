package com.qual.store.utils;

import com.qual.store.model.Product;
import com.qual.store.model.Review;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class ProductRatingCalculatorTest {
    private ProductRatingCalculator calculator;
    private Product product;

    @BeforeEach
    void setUp() {
        calculator = new ProductRatingCalculator();
        product = Product.builder()
                .reviews(new ArrayList<>())
                .build();
    }

    @Test
    void testCalculateRatingWithNoReviews() {
        double rating = calculator.calculateRating(product);
        Assertions.assertEquals(0, rating);
    }

    @Test
    void testCalculateRatingWithOneReview() {
        Review review = Review.builder()
                .rating(4.5)
                .build();
        product.addReview(review);

        double rating = calculator.calculateRating(product);
        Assertions.assertEquals(4.5, rating);
    }

    @Test
    void testCalculateRatingWithMultipleReviews() {
        List<Review> reviews = new ArrayList<>();
        reviews.add(Review.builder()
                .rating(5)
                .build());
        reviews.add(Review.builder()
                .rating(4)
                .build());
        reviews.add(Review.builder()
                .rating(3.5)
                .build());
        product.setReviews(reviews);

        double rating = calculator.calculateRating(product);
        Assertions.assertEquals(4.166666666666667, rating);
    }
}