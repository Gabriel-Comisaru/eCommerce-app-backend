package com.qual.store.repository;

import com.qual.store.model.OrderItem;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderItemRepository extends ShopRepository<OrderItem, Long> {
    @Query("select distinct oi from OrderItem oi")
    @EntityGraph(value = "orderItemWithProduct", type = EntityGraph.EntityGraphType.LOAD)
    List<OrderItem> findAllWithProduct();
}
