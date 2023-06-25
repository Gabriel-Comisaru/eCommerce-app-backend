package com.qual.store.service.impl;

import com.qual.store.exceptions.ProductNotFoundException;
import com.qual.store.model.OrderItem;
import com.qual.store.model.Product;
import com.qual.store.repository.OrderItemRepository;
import com.qual.store.repository.ProductRepository;
import com.qual.store.service.OrderItemService;
import com.qual.store.service.ProductService;
import com.qual.store.utils.validators.Validator;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderItemImpl implements OrderItemService {
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private ProductService productService;
    @Autowired
    private Validator<OrderItem> validator;
    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<OrderItem> getAllOrderItems() {
        return orderItemRepository.findAll();
    }

    @Override
    public void deleteOrderItemById(Long id) {
        orderItemRepository.deleteById(id);
    }

    @Override
    public void decreaseQuantity(Long id, OrderItem orderItem) {
        List<OrderItem> orderItems = orderItemRepository.findAll().stream()
                .filter(item -> item.getProduct().getId().equals(id))
                .toList();
        if (orderItems.size() == 0) {
            throw new ProductNotFoundException(String.format("No product found with id %s", id));
        }
        if (orderItems.get(0).getQuantity() > 1) {
            orderItems.get(0).setQuantity(orderItems.get(0).getQuantity() - orderItem.getQuantity());
            orderItemRepository.save(orderItems.get(0));
        } else if (orderItems.get(0).getQuantity() == 1) {
            orderItemRepository.delete(orderItems.get(0));
        }
    }

    @Transactional
    @Override
    public OrderItem addOrderItem(Long id, OrderItem orderItem) {
        validator.validate(orderItem);
        // verify if the item is already in the cart
        List<OrderItem> orderItems = orderItemRepository.findAll().stream()
                .filter(item -> item.getProduct().getId().equals(id))
                .toList();
        if (orderItems.size() > 0) {
            orderItems.get(0).setQuantity(orderItems.get(0).getQuantity() + orderItem.getQuantity());
            orderItemRepository.save(orderItems.get(0));
            return orderItems.get(0);
        }
        Product product = productRepository.findById(id).
                orElseThrow(() -> new ProductNotFoundException(String.format("No product with is found:%s", id)));
        orderItem.setProduct(product);
        orderItemRepository.save(orderItem);
        return orderItem;
    }

    @Override
    public Optional<OrderItem> findOrderItemById(Long id) {
        return orderItemRepository.findById(id);
    }

}
