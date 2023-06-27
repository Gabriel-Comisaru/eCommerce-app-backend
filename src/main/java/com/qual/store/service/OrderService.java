package com.qual.store.service;

import com.qual.store.exceptions.InvalidOrderStatusException;
import com.qual.store.model.Order;
import com.qual.store.model.OrderItem;
import com.qual.store.model.OrderStatus;

import java.util.List;
import java.util.Optional;

public interface OrderService {
    List<Order> getAllOrders();
    Order addToOrder(Long orderItemId, Order order);
    void deleteOrderById(Long id);
    Order findOrderById(Long id);

    Order updateOrderStatus(Long id, String status);
}
