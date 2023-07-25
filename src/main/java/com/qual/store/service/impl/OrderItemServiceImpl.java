package com.qual.store.service.impl;

import com.qual.store.converter.OrderItemConverter;
import com.qual.store.dto.OrderItemDto;
import com.qual.store.exceptions.OrderItemNotFoundException;
import com.qual.store.exceptions.ProductNotFoundException;
import com.qual.store.logger.Log;
import com.qual.store.model.AppUser;
import com.qual.store.model.Order;
import com.qual.store.model.OrderItem;
import com.qual.store.model.Product;
import com.qual.store.repository.AppUserRepository;
import com.qual.store.repository.OrderItemRepository;
import com.qual.store.repository.OrderRepository;
import com.qual.store.repository.ProductRepository;
import com.qual.store.service.OrderItemService;
import com.qual.store.utils.validators.Validator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemRepository orderItemRepository;

    private final OrderItemConverter orderItemConverter;

    private final OrderRepository orderRepository;

    private final Validator<OrderItem> validator;

    private final AppUserRepository appUserRepository;

    private final ProductRepository productRepository;

//    @Override
//    @Log
//    public List<OrderItem> getAllOrderItems() {
//        return orderItemRepository.findAll();
//    }
    @Override
    @Log
    public List<OrderItem> getAllOrderItems() {
        List<OrderItem> orderItems = orderItemRepository.findAllWithProduct();
        return orderItems;
    }

    @Override
    @Log
    public OrderItemDto getOrderItemById(Long orderItemId) {
        OrderItem orderItem = orderItemRepository.findAllWithProduct()
                .stream()
                .filter(p -> p.getId().equals(orderItemId))
                .findFirst()
                .orElseThrow(
                        () -> new OrderItemNotFoundException(String.format("No orderItem found with id %s", orderItemId))
                );
        return orderItemConverter.convertModelToDto(orderItem);
    }

    @Override
    @Log
    @Transactional
    public void deleteOrderItemById(Long id) {
        OrderItem orderItem = orderItemRepository.findById(id)
                .orElseThrow(() -> new OrderItemNotFoundException(String.format("No order item with id = %s found", id)));

        Product product = productRepository.findById(orderItem.getProduct().getId())
                .orElseThrow();
        if (orderItem.getOrder() != null) {
            Order order = orderRepository.findById(orderItem.getOrder().getId())
                    .orElseThrow();
            order.getOrderItems().remove(orderItem);
        }

        product.getOrderItems().remove(orderItem);
        orderItem.setProduct(null);
        orderItem.setOrder(null);
        productRepository.save(product);

        orderItemRepository.deleteById(id);
    }

    @Override
    @Log
    public void modifyQuantity(Long idOrderItem, Integer newQuantity) {
        OrderItem orderItem1 = orderItemRepository.findById(idOrderItem).
                orElseThrow(() -> new OrderItemNotFoundException(String.format("No order item found with id %s", idOrderItem)));
        if (newQuantity >= 1) {
            orderItem1.setQuantity(newQuantity);
            orderItemRepository.save(orderItem1);
        } else {
            orderItemRepository.delete(orderItem1);
        }
    }

    @Transactional
    @Override
    @Log
    public OrderItem addOrderItem(Long id, Integer quantity) {
        OrderItem orderItem = new OrderItem();
        orderItem.setQuantity(quantity);
        // verify if the item is already in the cart
        Product product = productRepository.findById(id).
                orElseThrow(() -> new ProductNotFoundException(
                        String.format("No product with is found:%s", id)));
        orderItem.setProduct(product);
        validator.validate(orderItem);
        product.addOrderItem(orderItem);
        return orderItemRepository.save(orderItem);
    }

    @Override
    @Log
    public OrderItem findOrderItemById(Long id) {
        return orderItemRepository.findById(id)
                .orElseThrow(() -> new OrderItemNotFoundException(String.format("No order item with id = %s found", id)));
    }

    @Override
    @Log
    public double priceOfOrderItem(Long id) {
        OrderItem orderItem = orderItemRepository.findById(id).
                orElseThrow(() -> new OrderItemNotFoundException(String.format("No order item found with id %s", id)));
        return orderItem.getQuantity() * orderItem.getProduct().getPrice();
    }
}
