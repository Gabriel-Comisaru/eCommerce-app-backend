package com.qual.store.repository;

import com.qual.store.model.Product;
import com.qual.store.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<Review> findByTitle(String title);

    List<Review> findByProduct(Product product);
}
