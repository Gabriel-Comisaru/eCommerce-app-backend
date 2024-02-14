package com.qual.store.repository;

import com.qual.store.model.Category;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends ShopRepository<Category, Long> {
    @Query("select distinct c from Category c")
    @EntityGraph(value = "categoryWithProducts", type = EntityGraph.EntityGraphType.LOAD)
    List<Category> findAllWithProducts();

    @Query("select distinct c from Category c where c.id = :categoryId")
    @EntityGraph(value = "categoryWithProducts", type = EntityGraph.EntityGraphType.LOAD)
    Optional<Category> findCategoryWithProducts(Long categoryId);
}

