package com.qual.store.converter;

import com.qual.store.converter.base.BaseConverter;
import com.qual.store.dto.OrderItemDto;
import com.qual.store.model.OrderItem;
import com.qual.store.repository.OrderRepository;
import com.qual.store.repository.ProductRepository;
import com.qual.store.service.OrderService;
import com.qual.store.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderItemConverter extends BaseConverter<OrderItem, OrderItemDto> {

    private final ProductRepository productRepository;

    private final OrderRepository orderRepository;

    @Override
    public OrderItem convertDtoToModel(OrderItemDto dto) {
        OrderItem orderItem = new OrderItem();
        orderItem.setQuantity(dto.getQuantity());
        orderItem.setProduct(productRepository.findById(dto.getProductId()).orElse(null));
        orderItem.setOrder(orderRepository.findById(dto.getOrderId()).orElse(null));
        orderItem.setId(dto.getId());
        return orderItem;
    }

    @Override
    public OrderItemDto convertModelToDto(OrderItem orderItem) {
        OrderItemDto orderItemDto = OrderItemDto.builder()
                .quantity(orderItem.getQuantity())
                .productId(orderItem.getProduct().getId())
                .productName(orderItem.getProduct().getName())
                .unitsInStock(orderItem.getProduct().getUnitsInStock())
                .productPrice(orderItem.getProduct().getPrice())
                .categoryId(orderItem.getProduct().getCategory().getId())
                .categoryName(orderItem.getProduct().getCategory().getName())
                .build();
        
        if (orderItem.getOrder() != null) {
            orderItemDto.setOrderId(orderItem.getOrder().getId());
        }

        orderItemDto.setId(orderItem.getId());
        return orderItemDto;
    }
}
