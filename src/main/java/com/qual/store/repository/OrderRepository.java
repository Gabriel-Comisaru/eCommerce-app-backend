package com.qual.store.repository;

import com.qual.store.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends ShopRepository<Order, Long> {
    @Query("select distinct o from Order o join fetch o.user")
    @EntityGraph(value = "orderWithOrderItems", type = EntityGraph.EntityGraphType.LOAD)
    List<Order> findAllWithOrderItems();

    @Query("select distinct o from Order o join fetch o.orderItems oi join fetch oi.product p where o.id = :id")
    @EntityGraph(value = "orderWithOrderItems", type = EntityGraph.EntityGraphType.LOAD)
    Optional<Order> findOrderById(Long id);
    
    @Query("select distinct o from Order o where o.user.id = ?1")
    @EntityGraph(value = "orderWithOrderItems", type = EntityGraph.EntityGraphType.LOAD)
    List<Order> findAllByAppUserId(Long id);

    @Query("select distinct o from Order o join fetch o.user")
    @EntityGraph(value = "orderWithOrderItems", type = EntityGraph.EntityGraphType.LOAD)
    Page<Order> findAllWithOrderItems(Pageable pageable);

    @Query("select distinct o from Order o join fetch o.user")
    @EntityGraph(value = "orderWithOrderItemsAndProducts", type = EntityGraph.EntityGraphType.LOAD)
    List<Order> findAllWithOrderItemsAndProducts();
}
