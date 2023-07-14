package com.qual.store.service.impl;

import com.github.javafaker.App;
import com.qual.store.converter.OrderConverter;
import com.qual.store.dto.OrderDto;
import com.qual.store.exceptions.InvalidOrderStatusException;
import com.qual.store.exceptions.OrderNotFoundException;
import com.qual.store.logger.Log;
import com.qual.store.model.*;
import com.qual.store.model.enums.OrderStatus;
import com.qual.store.repository.AppUserRepository;
import com.qual.store.repository.OrderItemRepository;
import com.qual.store.repository.OrderRepository;
import com.qual.store.service.OrderItemService;
import com.qual.store.service.OrderService;
import com.qual.store.utils.validators.Validator;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;


    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private Validator<Order> validator;

    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private OrderConverter orderConverter;

//    @Override
//    @Log
//    public List<Order> getAllOrders() {
//        return orderRepository.findAll();
//    }
    @Override
    @Log
    public List<Order> getAllOrders() {
        List<Order> orders = orderRepository.findAllWithOrderItems();
        return orders;
    }

    @Transactional
    @Override
    @Log
    public Order addToOrder(Long orderItemId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        System.out.println("currentUsername = " + currentUsername);
        AppUser appUser = appUserRepository.findUserByUsername(currentUsername);
        OrderStatus orderStatus = OrderStatus.ACTIVE;
        List<Order> orders = getAllOrders().stream()
                .filter(order -> order.getUser().equals(appUser))
                .filter(order -> order.getStatus().equals(orderStatus))
                .toList();
        Order order;
        if (orders.size() == 0) {
            order = Order.builder()
                    .deliveryPrice(orderItemService.priceOfOrderItem(orderItemId))
                    .startDate(LocalDate.now())
                    .deliveryDate(null)
                    .status(orderStatus)
                    .user(appUser)
                    .build();
            validator.validate(order);
            OrderItem orderItem = orderItemRepository.findById(orderItemId)
                    .orElseThrow(() -> new RuntimeException("No order item found with id = " + orderItemId));
            orderItem.setOrder(order);
            order.addOrderItem(orderItem);
            appUser.addOrder(order);
        } else {
            order = orders.get(0);
            order.setDeliveryPrice(order.getDeliveryPrice() + orderItemService.priceOfOrderItem(orderItemId));
            OrderItem orderItem = orderItemRepository.findById(orderItemId)
                    .orElseThrow(() -> new RuntimeException("No order item found with id = " + orderItemId));
            orderItem.setOrder(order);
            order.addOrderItem(orderItem);
            appUser.addOrder(order);
        }
        orderRepository.save(order);
        return order;
    }

    @Override
    @Log
    @Transactional
    public void deleteOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(String.format("order with id %s not found", id)));

        List<OrderItem> orderItems = order.getOrderItems().stream().toList();
        order.setOrderItems(null);
        order.setUser(null);
        orderItems.forEach(orderItem -> orderItem.setOrder(null));

        orderItemRepository.saveAll(orderItems);

        orderRepository.deleteById(id);
    }

    @Override
    @Log
    public Order findOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No order found with id %s = " + id));
    }

    @Override
    @Transactional
    @Log
    public Order updateOrderStatus(Long id, String status) {
        Optional<Order> existingOrder = orderRepository.findAllWithOrderItems().stream()
                .filter(o -> o.getId().equals(id))
                .findFirst();

        existingOrder.orElseThrow(() -> new OrderNotFoundException(
                String.format("No order found with id %s = ", id)
        ));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        AppUser appUser = appUserRepository.findUserByUsername(currentUsername);
        if (appUser.getRole().name().equals("USER")) {
            if (!status.equals("CHECKOUT")) {
                throw new RuntimeException("Invalid status: " + status);
            }
            if (!existingOrder.get().getStatus().name().equals("ACTIVE")) {
                throw new RuntimeException("You are not allowed to change the status of this order");
            }

            existingOrder.ifPresent(updateOrder -> {
                OrderStatus orderStatus = getOrderStatusFromString(status);
                if (!updateOrder.getUser().equals(appUser)) {
                    throw new RuntimeException("You are not allowed to change the status of another user's order");
                } else if (orderStatus != null) {
                    updateOrder.setStatus(orderStatus);
                } else {
                    throw new InvalidOrderStatusException("Invalid status: " + status);
                }
            });
        } else {
            existingOrder.ifPresent(updateOrder -> {
                OrderStatus orderStatus = getOrderStatusFromString(status);
                if (orderStatus != null) {
                    updateOrder.setStatus(orderStatus);
                } else {
                    throw new InvalidOrderStatusException("Invalid status: " + status);
                }
            });
        }

        return existingOrder.get();
    }

    private OrderStatus getOrderStatusFromString(String status) {
        try {
            return OrderStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    @Log
    public List<Order> getAllOrdersByUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        AppUser appUser = appUserRepository.findUserByUsername(currentUsername);
        List<OrderDto> ordersDto = getAllOrders().stream()
                .map(o -> orderConverter.convertModelToDto(o))
                .toList();
        List<Order> orders = ordersDto.stream()
                .filter(o -> o.getUserId().equals(appUser.getId()))
                .map(o -> orderConverter.convertDtoToModel(o))
                .toList();
        return orders;
    }
    //for all orders with status placed, create a map with product name and quantity
    @Override
    @Log
    public Map<Long, Integer> getProductsQuantity() {
        List<Order> orders = getAllOrders().stream()
                .filter(o -> o.getStatus().equals(OrderStatus.PLACED))
                .toList();
        Map<Long, Integer> productsQuantity = new HashMap<>();
        for (Order order : orders) {
            for (OrderItem orderItem : order.getOrderItems()) {
                if (productsQuantity.containsKey(orderItem.getProduct().getId())) {
                    productsQuantity.put(orderItem.getProduct().getId(),
                            productsQuantity.get(orderItem.getProduct().getId()) + orderItem.getQuantity());
                } else {
                    productsQuantity.put(orderItem.getProduct().getId(), orderItem.getQuantity());
                }
            }
        }
        System.out.println("productsQuantity = " + productsQuantity);
        return productsQuantity;
    }
}
