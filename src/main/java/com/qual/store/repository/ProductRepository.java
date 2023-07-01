package com.qual.store.repository;

import com.qual.store.model.Order;
import com.qual.store.model.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends ShopRepository<Product, Long> {
    @Query("select distinct p from Product p")
    @EntityGraph(value = "productWithCategory", type = EntityGraph.EntityGraphType.LOAD)
    List<Product> findAllWithCategory();
    @Query("select distinct o from Order o where o.user.id = ?1")
    @EntityGraph(value = "productWithUser", type = EntityGraph.EntityGraphType.LOAD)
    List<Order> findAllByAppUserId(Long id);
}
