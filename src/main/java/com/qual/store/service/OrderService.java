package com.qual.store.service;

import com.qual.store.model.Order;

import java.util.List;

public interface OrderService {
    List<Order> getAllOrders();

    Order addToOrder(Long orderItemId);

    void deleteOrderById(Long id);

    Order findOrderById(Long id);

    Order updateOrderStatus(Long id, String status);

    List<Order> getAllOrdersByUser();
}
