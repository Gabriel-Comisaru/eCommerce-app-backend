package com.qual.store.controller;

import com.qual.store.converter.OrderConverter;
import com.qual.store.converter.OrderItemConverter;
import com.qual.store.converter.lazyConverter.OrderWithOrderItemsConverter;
import com.qual.store.dto.OrderDto;
import com.qual.store.dto.OrderItemDto;
import com.qual.store.dto.ProductDto;
import com.qual.store.dto.lazyDto.OrderWithOrderItemDto;
import com.qual.store.dto.paginated.PaginatedOrderResponse;
import com.qual.store.logger.Log;
import com.qual.store.model.Order;
import com.qual.store.model.OrderItem;
import com.qual.store.service.OrderItemService;
import com.qual.store.service.OrderService;
import lombok.RequiredArgsConstructor;
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
    private final OrderItemService orderItemService;
    private final OrderConverter orderConverter;
    private final OrderWithOrderItemsConverter orderWithOrderItemsConverter;
    private final OrderItemConverter orderItemConverter;


    @GetMapping("/display")
    @Log
    public ResponseEntity<PaginatedOrderResponse> getOrders(@RequestParam(defaultValue = "0") Integer pageNumber,
                                                            @RequestParam(defaultValue = "10") Integer pageSize,
                                                            @RequestParam(defaultValue = "id") String sortBy) {

        return ResponseEntity.ok(orderService.getOrders(pageNumber, pageSize, sortBy));
    }

    @GetMapping
    @Log
    public List<OrderDto> getAllOrders() {
        return orderService.getAllOrders().stream()
                .map(orderConverter::convertModelToDto)
                .collect(Collectors.toList());
    }

    @PostMapping(value = "/{productId}")
    @Log
    public ResponseEntity<?> addToOrder(@PathVariable("productId") Long productId, @RequestParam Integer quantity) {

        OrderItem orderItem = orderItemService.addOrderItem(productId, quantity);
        Order order = orderService.addToOrder(orderItem.getId());
        orderItem.setOrder(order);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderItemConverter.convertModelToDto(orderItem)
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

    @GetMapping(value = "/me/lazy")
    @Log
    public List<OrderWithOrderItemDto> getAllOrdersWithOrderItemsByUsername() {
        return orderService.getAllOrdersByUser().stream()
                .map(orderWithOrderItemsConverter::convertModelToDto)
                .collect(Collectors.toList());
    }

    @GetMapping(value = "/me/basket")
    @Log
    public List<OrderItemDto> getBasket() {
        return orderService.getBasketAsOrderItems().stream()
                .map(orderItemConverter::convertModelToDto)
                .collect(Collectors.toList());
    }


    @GetMapping(value = "/products")
    @Log
    public Map<Long, Integer> getProductsQuantity() {
        return orderService.getProductsQuantity();
    }

    @GetMapping("/search")
    @Log
    public ResponseEntity<List<OrderDto>> searchOrdersByUsername(@RequestParam("user") String username) {
        return ResponseEntity.ok(orderService.searchOrdersByUsername(username));
    }
}
