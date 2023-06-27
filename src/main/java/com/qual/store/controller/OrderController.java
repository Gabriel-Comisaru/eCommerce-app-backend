package com.qual.store.controller;

import com.qual.store.converter.OrderConverter;
import com.qual.store.dto.OrderDto;
import com.qual.store.model.Order;
import com.qual.store.model.OrderItem;
import com.qual.store.model.OrderStatus;
import com.qual.store.service.OrderItemService;
import com.qual.store.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderConverter orderConverter;
    @Autowired
    private OrderItemService orderItemService;

    @GetMapping
    public List<OrderDto> getAllOrders() {
        return orderService.getAllOrders().stream()
                .map(order -> orderConverter.convertModelToDto(order))
                .collect(Collectors.toList());
    }

    @PostMapping(value = "/{orderItemId}")
    public OrderDto addToOrder(@PathVariable("orderItemId") Long orderItemId) {
        OrderStatus orderStatus = OrderStatus.ACTIVE;
        List<Order> orders = orderService.getAllOrders().stream()
                .filter(ord -> ord.getStatus().equals(orderStatus)).toList();
        if (orders.size() == 0) {
            Order order = Order.builder()
                    .deliveryPrice(orderItemService.priceOfOrderItem(orderItemId))
                    .startDate(LocalDate.now())
                    .deliveryDate(null)
                    .status(orderStatus)
                    .userId(1)
                    .build();
            Order savedOrder = orderService.addToOrder(orderItemId, order);
            return orderConverter.convertModelToDto(savedOrder);
        } else {
            Order order = orders.get(0);
            order.setDeliveryPrice(order.getDeliveryPrice() + orderItemService.priceOfOrderItem(orderItemId));
            Order savedOrder = orderService.addToOrder(orderItemId, order);
            System.out.println(savedOrder);
            return orderConverter.convertModelToDto(savedOrder);
        }
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteOrderById(@PathVariable("id") Long id) {
        Order order = orderService.findOrderById(id);
        orderService.deleteOrderById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body("Order deleted");
    }

}
