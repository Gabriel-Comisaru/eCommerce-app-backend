package com.qual.store.service;

import com.qual.store.dto.OrderDto;
import com.qual.store.dto.OrderItemDto;
import com.qual.store.dto.paginated.PaginatedOrderResponse;
import com.qual.store.model.Order;
import com.qual.store.model.OrderItem;

import java.util.List;
import java.util.Map;

public interface OrderService {
    List<Order> getAllOrders();
    List<OrderItem> getBasketAsOrderItems();

    Order addToOrder(Long orderItemId);

    void deleteOrderById(Long id);

    Order findOrderById(Long id);

    Order updateOrderStatus(Long id, String status);

    List<Order> getAllOrdersByUser();

    Map<Long, Integer> getProductsQuantity();

    PaginatedOrderResponse getOrders(Integer pageNumber, Integer pageSize, String sortBy);

    Order getBasket();

    List<OrderDto> searchOrdersByUsername(String username);
}
