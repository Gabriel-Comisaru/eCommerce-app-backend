package com.qual.store.repository;

import com.qual.store.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends ShopRepository<Product, Long> {
    @Query("select distinct p from Product p")
    @EntityGraph(value = "productWithCategoryAndReviewsAndImages", type = EntityGraph.EntityGraphType.LOAD)
    List<Product> findAllWithCategoryAndReviewsAndImages();

    @Override
    @EntityGraph(value = "productWithCategoryAndReviewsAndImages", type = EntityGraph.EntityGraphType.LOAD)
    Page<Product> findAll(Pageable pageable);

    @EntityGraph(value = "productWithCategoryAndReviewsAndImages", type = EntityGraph.EntityGraphType.LOAD)
    Page<Product> findAllByNameContainingIgnoreCase(String matchingName, Pageable pageable);
}
