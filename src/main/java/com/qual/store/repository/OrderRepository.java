package com.qual.store.repository;

import com.qual.store.model.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends ShopRepository<Order, Long> {
    @Query("select distinct o from Order o")
    @EntityGraph(value = "orderWithOrderItems", type = EntityGraph.EntityGraphType.LOAD)
    List<Order> findAllWithOrderItems();
}
