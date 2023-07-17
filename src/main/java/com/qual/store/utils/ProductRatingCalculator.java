package com.qual.store.utils;

import com.qual.store.model.Product;
import com.qual.store.model.Review;
import org.springframework.stereotype.Component;

@Component
public class ProductRatingCalculator {

    public double calculateRating(Product product) {
        if (product.getReviews().size() == 0) {
            return 0;
        }

        double sum = product.getReviews().stream()
                .map(Review::getRating)
                .reduce(Double::sum)
                .orElse(Double.valueOf("0"));

        long count = product.getReviews().size();

        return sum / count;
    }

}
