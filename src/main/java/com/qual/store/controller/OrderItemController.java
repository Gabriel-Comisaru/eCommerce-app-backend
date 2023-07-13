package com.qual.store.controller;

import com.qual.store.converter.OrderItemConverter;
import com.qual.store.dto.OrderItemDto;
import com.qual.store.converter.lazyConverter.OrderItemLazyConverter;
import com.qual.store.dto.lazyDto.OrderItemWithProductDto;
import com.qual.store.logger.Log;
import com.qual.store.model.OrderItem;
import com.qual.store.service.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/orderItems")
@CrossOrigin("*")
@RequiredArgsConstructor
public class OrderItemController {

    private final OrderItemService orderItemService;

    private final OrderItemConverter orderItemConverter;

    private final OrderItemLazyConverter orderItemLazyConverter;

    @GetMapping
    @Log
    public List<OrderItemDto> getAllOrderItems() {
        return orderItemService.getAllOrderItems().stream()
                .map(orderItemConverter::convertModelToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{orderItemId}")
    @Log
    public ResponseEntity<OrderItemDto> getOrderItemById(@PathVariable Long orderItemId) {
        return ResponseEntity.ok(orderItemService.getOrderItemById(orderItemId));
    }

    @GetMapping("/lazy")
    @Log
    public List<OrderItemWithProductDto> getAllOrderItemsWithProduct() {
        return orderItemService.getAllOrderItems().stream()
                .map(orderItemLazyConverter::convertModelToDto)
                .collect(Collectors.toList());
    }


    @PostMapping(value = "/{productId}")
    @Log
    public ResponseEntity<?> addOrderItem(@PathVariable("productId") Long productId, @RequestParam Integer quantity) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderItemConverter.convertModelToDto(
                                orderItemService.addOrderItem(productId, quantity)
                        )
                );
    }

    @DeleteMapping(value = "/{id}")
    @Log
    public ResponseEntity<?> deleteOrderItemById(@PathVariable("id") Long id) {
        orderItemService.deleteOrderItemById(id);

        return ResponseEntity.status(HttpStatus.OK)
                .body("OrderItem deleted");
    }

    @PutMapping(value = "/{id}/quantity")
    @Log
    public ResponseEntity<?> decreaseQuantity(@PathVariable("id") Long id, @RequestParam Integer quantity) {
        orderItemService.modifyQuantity(id, quantity);
        return ResponseEntity.status(HttpStatus.OK)
                .body("Quantity modified");
    }
}
