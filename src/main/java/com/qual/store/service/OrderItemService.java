package com.qual.store.service;

import com.qual.store.dto.OrderItemDto;
import com.qual.store.model.OrderItem;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface OrderItemService {
    List<OrderItem> getAllOrderItems();

    OrderItemDto getOrderItemById(Long orderItemId);

    OrderItem addOrderItem(Long id, Integer quantity);

    void modifyQuantity(Long id, Integer quantity);

    void deleteOrderItemById(Long id);

    OrderItem findOrderItemById(Long id);

    double priceOfOrderItem(Long id);
}
