package com.qual.store.service.impl;

import com.qual.store.converter.OrderConverter;
import com.qual.store.dto.OrderDto;
import com.qual.store.dto.paginated.PaginatedOrderResponse;
import com.qual.store.exceptions.InvalidOrderStatusException;
import com.qual.store.exceptions.OrderItemNotFoundException;
import com.qual.store.exceptions.OrderNotFoundException;
import com.qual.store.exceptions.UpdateOrderStatusException;
import com.qual.store.logger.Log;
import com.qual.store.model.AppUser;
import com.qual.store.model.Order;
import com.qual.store.model.OrderItem;
import com.qual.store.model.Product;
import com.qual.store.model.enums.OrderStatus;
import com.qual.store.repository.AppUserRepository;
import com.qual.store.repository.OrderItemRepository;
import com.qual.store.repository.OrderRepository;
import com.qual.store.repository.ProductRepository;
import com.qual.store.service.OrderItemService;
import com.qual.store.service.OrderService;
import com.qual.store.utils.validators.Validator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final AppUserRepository appUserRepository;
    private final Validator<Order> validator;
    private final OrderItemService orderItemService;
    private final OrderConverter orderConverter;
    private final ProductRepository productRepository;


    @Override
    @Log
    public List<Order> getAllOrders() {
        return orderRepository.findAllWithOrderItemsAndProducts();
    }

    @Transactional
    @Override
    @Log
    public Order addToOrder(Long orderItemId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
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
                    .deliveryDate(LocalDate.now().plusDays(7))
                    .status(orderStatus)
                    .user(appUser)
                    .build();
            validator.validate(order);
            OrderItem orderItem = orderItemRepository.findById(orderItemId)
                    .orElseThrow(() -> new OrderItemNotFoundException("No order item found with id = " + orderItemId));
            orderItem.setOrder(order);
            order.addOrderItem(orderItem);
            appUser.addOrder(order);
        } else {
            order = orders.get(0);
            order.setDeliveryPrice(order.getDeliveryPrice() + orderItemService.priceOfOrderItem(orderItemId));
            OrderItem orderItem = orderItemRepository.findById(orderItemId)
                    .orElseThrow(() -> new OrderItemNotFoundException("No order item found with id = " + orderItemId));
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

        for (OrderItem orderItem : orderItems) {
            orderItemService.deleteOrderItemById(orderItem.getId());
        }

        orderRepository.deleteById(id);
    }

    @Override
    @Log
    public Order findOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("No order found with id %s = " + id));
    }

    @Override
    @Transactional
    @Log
    public Order updateOrderStatus(Long id, String status) {
        String uppStatus = status.toUpperCase();
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
            if (!(uppStatus.equals("CHECKOUT") || uppStatus.equals("PLACED"))) {
                throw new InvalidOrderStatusException("Invalid status: " + uppStatus);
            }
            if (uppStatus.equals("CHECKOUT") && !existingOrder.get().getStatus().name().equals("ACTIVE")
                    || uppStatus.equals("PLACED") && !existingOrder.get().getStatus().name().equals("CHECKOUT")) {
                throw new UpdateOrderStatusException("You are not allowed to change the status of this order");
            }

            List<Product> productsToUpdate = new ArrayList<>();
            Order order = existingOrder.get();

            if (uppStatus.equals("PLACED")) {
                for (OrderItem orderItem : order.getOrderItems()) {
                    Product product = orderItem.getProduct();
                    product.setUnitsInStock(product.getUnitsInStock() - orderItem.getQuantity());
                    productsToUpdate.add(product);
                }
            }
            productRepository.saveAll(productsToUpdate);

            existingOrder.ifPresent(updateOrder -> {
                OrderStatus orderStatus = getOrderStatusFromString(uppStatus);
                if (!updateOrder.getUser().equals(appUser)) {
                    throw new UpdateOrderStatusException("You are not allowed to change the status of another user's order");
                } else if (orderStatus != null) {
                    updateOrder.setStatus(orderStatus);
                } else {
                    throw new InvalidOrderStatusException("Invalid status: " + uppStatus);
                }
            });
        } else {
            // if the status is placed or cancelled, modify the stock of the products
            Order order = existingOrder.get();
            List<Product> productsToUpdate = new ArrayList<>();

            if (uppStatus.equalsIgnoreCase("placed") && !order.getStatus().equals(OrderStatus.PLACED)) {
                for (OrderItem orderItem : order.getOrderItems()) {
                    Product product = orderItem.getProduct();
                    product.setUnitsInStock(product.getUnitsInStock() - orderItem.getQuantity());
                    productsToUpdate.add(product);
                }
            } else if (uppStatus.equalsIgnoreCase("cancelled") && !order.getStatus().equals(OrderStatus.CANCELLED)) {
                for (OrderItem orderItem : order.getOrderItems()) {
                    Product product = orderItem.getProduct();
                    product.setUnitsInStock(product.getUnitsInStock() + orderItem.getQuantity());
                    productsToUpdate.add(product);
                }
            }

            // Update product stock outside of the loop
            productRepository.saveAll(productsToUpdate);

            existingOrder.ifPresent(updateOrder -> {
                OrderStatus orderStatus = getOrderStatusFromString(uppStatus);
                if (orderStatus != null) {
                    updateOrder.setStatus(orderStatus);
                } else {
                    throw new InvalidOrderStatusException("Invalid status: " + uppStatus);
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
                .map(orderConverter::convertModelToDto)
                .toList();
        return ordersDto.stream()
                .filter(o -> o.getUserId().equals(appUser.getId()))
                .map(orderConverter::convertDtoToModel)
                .toList();
    }

    @Override
    @Log
    public Order getBasket() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        AppUser appUser = appUserRepository.findUserByUsername(currentUsername);
        Order order = orderRepository.findAllWithOrderItems().stream()
                .filter(o -> o.getUser().equals(appUser) && o.getStatus().equals(OrderStatus.ACTIVE))
                .findFirst()
                .orElse(null);
        if (order == null) {
            return new Order();
        }
        return order;
    }

    @Override
    @Log
    public List<OrderItem> getBasketAsOrderItems() {
        return orderItemRepository.findAllWithProduct().stream()
                .filter(oi -> oi.getOrder().getId().equals(getBasket().getId()))
                .toList();
    }

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

    @Override
    public PaginatedOrderResponse getOrders(Integer pageNumber, Integer pageSize, String sortBy) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));

        Page<Order> page = orderRepository.findAllWithOrderItems(pageable);

        return PaginatedOrderResponse.builder()
                .orders(page.getContent().stream()
                        .map(orderConverter::convertModelToDto)
                        .collect(Collectors.toList()))
                .numberOfItems(page.getTotalElements())
                .numberOfPages(page.getTotalPages())
                .build();
    }

    @Override
    public List<OrderDto> searchOrdersByUsername(String username) {
        return orderRepository.findAllWithOrderItems().stream()
                .filter(o -> o.getUser().getUsername().equals(username))
                .map(orderConverter::convertModelToDto)
                .toList();
    }
}
