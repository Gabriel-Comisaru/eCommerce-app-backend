package com.qual.store.converter;

import com.qual.store.dto.OrderItemDto;
import com.qual.store.model.OrderItem;
import com.qual.store.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderItemConverter extends BaseConverter<OrderItem, OrderItemDto> {

    @Autowired
    private ProductService productService;
    @Override
    public OrderItem convertDtoToModel(OrderItemDto dto) {
        OrderItem orderItem = new OrderItem();
        orderItem.setQuantity(dto.getQuantity());
        productService.findProductById(dto.getProductId()).ifPresent(orderItem::setProduct);
        orderItem.setId(dto.getId());
        return orderItem;
    }

    @Override
    public OrderItemDto convertModelToDto(OrderItem orderItem) {
       OrderItemDto orderItemDto = OrderItemDto.builder()
               .quantity(orderItem.getQuantity())
               .productId(orderItem.getProduct().getId())
               .build();
       orderItemDto.setId(orderItem.getId());
       return orderItemDto;
    }
}