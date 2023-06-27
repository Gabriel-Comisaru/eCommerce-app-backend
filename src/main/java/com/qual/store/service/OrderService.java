package com.qual.store.service;

import com.qual.store.model.Order;
import com.qual.store.model.OrderItem;

import java.util.List;
import java.util.Optional;

public interface OrderService {
    List<Order> getAllOrders();
    Order addToOrder(Long orderItemId, Order order);
    void deleteOrderById(Long id);
    Order findOrderById(Long id);
}
