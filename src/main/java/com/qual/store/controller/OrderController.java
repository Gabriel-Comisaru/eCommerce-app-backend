package com.qual.store.controller;

import com.qual.store.converter.OrderConverter;
import com.qual.store.dto.OrderDto;
import com.qual.store.dto.ProductDto;
import com.qual.store.logger.Log;
import com.qual.store.model.Product;
import com.qual.store.model.enums.OrderStatus;
import com.qual.store.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/orders")
@RequiredArgsConstructor
@CrossOrigin("*")
public class OrderController {

    private final OrderService orderService;
    private final OrderConverter orderConverter;

    @GetMapping
    @Log
    public List<OrderDto> getAllOrders() {
        return orderService.getAllOrders().stream()
                .map(orderConverter::convertModelToDto)
                .collect(Collectors.toList());
    }

    @PostMapping(value = "/{orderItemId}")
    @Log
    public OrderDto addToOrder(@PathVariable("orderItemId") Long orderItemId) {
        return orderConverter.convertModelToDto(
                orderService.addToOrder(orderItemId)
        );
    }

    @DeleteMapping(value = "/{id}")
    @Log
    public ResponseEntity<?> deleteOrderById(@PathVariable("id") Long id) {
        orderService.deleteOrderById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body("Order deleted");
    }

    @PutMapping("/{orderId}")
    @Log
    public ResponseEntity<?> updateOrderStatus(@PathVariable("orderId") Long id,
                                               @RequestParam("status") String status) {

        status = status.toUpperCase();
        return ResponseEntity.ok(
                orderConverter.convertModelToDto(
                        orderService.updateOrderStatus(id, status)
                )
        );
    }

    @GetMapping(value = "/me")
    @Log
    public List<OrderDto> getAllOrdersByUsername() {
        return orderService.getAllOrdersByUser().stream()
                .map(orderConverter::convertModelToDto)
                .collect(Collectors.toList());
    }


    @GetMapping(value = "/products")
    @Log
    public Map<Long, Integer> getProductsQuantity() {
        return orderService.getProductsQuantity();
    }
}
