package com.qual.store.service;

import com.qual.store.model.OrderItem;

import java.util.List;
import java.util.Optional;

public interface OrderItemService {
    List<OrderItem> getAllOrderItems();

    OrderItem addOrderItem(Long id, OrderItem orderItem);

    void modifyQuantity(Long id, Integer quantity);

    void deleteOrderItemById(Long id);

    Optional<OrderItem> findOrderItemById(Long id);

    double priceOfOrderItem(Long id);
}
