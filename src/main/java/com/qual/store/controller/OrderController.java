package com.qual.store.controller;

import com.qual.store.converter.OrderConverter;
import com.qual.store.dto.OrderDto;
import com.qual.store.logger.Log;
import com.qual.store.model.Order;
import com.qual.store.model.OrderStatus;
import com.qual.store.service.OrderItemService;
import com.qual.store.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    @Log
    public List<OrderDto> getAllOrders() {
        return orderService.getAllOrders().stream()
                .map(order -> orderConverter.convertModelToDto(order))
                .collect(Collectors.toList());
    }

    @PostMapping(value = "/{orderItemId}")
    @Log
    public OrderDto addToOrder(@PathVariable("orderItemId") Long orderItemId) {
        Order savedOrder = orderService.addToOrder(orderItemId);
        System.out.println(savedOrder);
        return orderConverter.convertModelToDto(savedOrder);
    }

    @DeleteMapping(value = "/{id}")
    @Log
    public ResponseEntity<?> deleteOrderById(@PathVariable("id") Long id) {
        Order order = orderService.findOrderById(id);
        orderService.deleteOrderById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body("Order deleted");
    }

    @PutMapping("/{orderId}")
    @Log
    public ResponseEntity<?> updateOrderStatus(@PathVariable("orderId") Long id,
                                               @RequestParam("status") String status) {

        return ResponseEntity.ok(orderConverter.convertModelToDto(orderService.updateOrderStatus(id, status)));
    }
}
