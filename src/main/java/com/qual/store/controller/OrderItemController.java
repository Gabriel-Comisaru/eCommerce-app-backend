package com.qual.store.controller;

import com.qual.store.converter.OrderItemConverter;
import com.qual.store.dto.OrderItemDto;
import com.qual.store.model.BaseEntity;
import com.qual.store.model.OrderItem;
import com.qual.store.model.Product;
import com.qual.store.service.OrderItemService;
import com.qual.store.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/orderItems")
public class OrderItemController {
    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private OrderItemConverter orderItemConverter;
    @Autowired
    private ProductService productService;

    @GetMapping
    public List<OrderItemDto> getAllOrderItems() {
        return orderItemService.getAllOrderItems().stream()
                .map(orderItem -> orderItemConverter.convertModelToDto(orderItem))
                .collect(Collectors.toList());
    }

    @PostMapping(value = "/{productId}")
    public OrderItemDto addOrderItem(@PathVariable("productId") Long productId, @RequestBody OrderItem orderItem) {
        OrderItem savedOrderItem = orderItemService.addOrderItem(productId, orderItem);
        System.out.println(savedOrderItem);
        return orderItemConverter.convertModelToDto(savedOrderItem);
    }

    @DeleteMapping(value = "/{productId}")
    public ResponseEntity<?> deleteOrderItemById(@PathVariable("productId") Long id) {
        Optional<Product> product = productService.findProductById(id);
        if (product.isPresent()) {
            Long orderId = orderItemService.getAllOrderItems().stream()
                    .filter(orderItem -> orderItem.getProduct().getId().equals(id))
                    .map(BaseEntity::getId)
                    .findFirst()
                    .orElse(null);
            orderItemService.deleteOrderItemById(orderId);
            return ResponseEntity.status(HttpStatus.OK)
                    .body("OrderItem deleted");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("OrderItem not found");
    }

    @PutMapping(value = "/{productId}")
    public ResponseEntity<?> decreaseQuantity(@PathVariable("productId") Long id, @RequestBody OrderItem orderItem) {
        Optional<Product> product = productService.findProductById(id);
        if (product.isPresent()) {
            orderItemService.decreaseQuantity(id, orderItem);
            return ResponseEntity.status(HttpStatus.OK)
                    .body("Quantity decreased");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("OrderItem not found");
    }
}
