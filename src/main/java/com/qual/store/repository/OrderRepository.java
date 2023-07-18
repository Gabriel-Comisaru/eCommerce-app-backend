package com.qual.store.repository;

import com.qual.store.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends ShopRepository<Order, Long> {
    @Query("select distinct o from Order o")
    @EntityGraph(value = "orderWithOrderItems", type = EntityGraph.EntityGraphType.LOAD)
    List<Order> findAllWithOrderItems();

    @Query("select distinct o from Order o where o.user.id = ?1")
    @EntityGraph(value = "orderWithOrderItems", type = EntityGraph.EntityGraphType.LOAD)
    List<Order> findAllByAppUserId(Long id);

    @Query("SELECT o FROM Order o JOIN FETCH o.orderItems")
    Page<Order> findAllWithOrderItems(Pageable pageable);
}
