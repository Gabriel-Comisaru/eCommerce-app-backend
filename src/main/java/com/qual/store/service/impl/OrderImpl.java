package com.qual.store.service.impl;

import com.qual.store.exceptions.InvalidOrderStatusException;
import com.qual.store.logger.Log;
import com.qual.store.model.Order;
import com.qual.store.model.OrderItem;
import com.qual.store.model.OrderStatus;
import com.qual.store.repository.OrderItemRepository;
import com.qual.store.repository.OrderRepository;
import com.qual.store.service.OrderItemService;
import com.qual.store.service.OrderService;
import com.qual.store.utils.validators.Validator;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderImpl implements OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private Validator<Order> validator;
    @Autowired
    private Validator<OrderItem> orderItemValidator;
    @Autowired
    private OrderItemService orderItemService;

    @Override
    @Log
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Transactional
    @Override
    @Log
    public Order addToOrder(Long orderItemId) {
        OrderStatus orderStatus = OrderStatus.ACTIVE;
        List<Order> orders = getAllOrders().stream()
                .filter(order -> order.getStatus().equals(orderStatus))
                .toList();
        if (orders.size() == 0) {
            Order order = Order.builder()
                    .deliveryPrice(orderItemService.priceOfOrderItem(orderItemId))
                    .startDate(LocalDate.now())
                    .deliveryDate(null)
                    .status(orderStatus)
                    .userId(1)
                    .build();
            validator.validate(order);
            OrderItem orderItem = orderItemRepository.findById(orderItemId)
                    .orElseThrow(() -> new RuntimeException("No orderItem found with id:" + orderItemId));
            orderItem.setOrder(order);
            order.addOrderItem(orderItem);
            orderRepository.save(order);
            return order;
        } else {
            Order order = orders.get(0);
            order.setDeliveryPrice(order.getDeliveryPrice() + orderItemService.priceOfOrderItem(orderItemId));
            OrderItem orderItem = orderItemRepository.findById(orderItemId)
                    .orElseThrow(() -> new RuntimeException("No orderItem found with id:" + orderItemId));
            orderItem.setOrder(order);
            order.addOrderItem(orderItem);
            orderRepository.save(order);
            return order;
        }
    }

    @Override
    @Log
    public void deleteOrderById(Long id) {
        orderRepository.deleteById(id);
    }


    @Override
    @Log
    public Order findOrderById(Long id) {
        return orderRepository.findById(id).get();
    }

    @Override
    @Transactional
    @Log
    public Order updateOrderStatus(Long id, String status){
        Optional<Order> existingOrder = orderRepository.findById(id);

        existingOrder.orElseThrow(() -> new RuntimeException("No order found with id:" + id));

        existingOrder.ifPresent(updateOrder -> {
            OrderStatus orderStatus = getOrderStatusFromString(status);
            if (orderStatus != null) {
                updateOrder.setStatus(orderStatus);
            } else {
                throw new InvalidOrderStatusException("Invalid status: " + status);
            }
        });

        return existingOrder.get();
    }

    private OrderStatus getOrderStatusFromString(String status) {
        try {
            return OrderStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
