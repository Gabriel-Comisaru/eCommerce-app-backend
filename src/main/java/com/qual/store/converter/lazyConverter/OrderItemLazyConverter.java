package com.qual.store.converter.lazyConverter;

import com.qual.store.converter.base.BaseConverter;
import com.qual.store.dto.lazyDto.OrderItemWithProductDto;
import com.qual.store.model.OrderItem;
import com.qual.store.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderItemLazyConverter extends BaseConverter<OrderItem, OrderItemWithProductDto> {

    @Autowired
    private ProductService productService;

    @Override
    public OrderItem convertDtoToModel(OrderItemWithProductDto dto) {
        OrderItem orderItem = new OrderItem();
        orderItem.setQuantity(dto.getQuantity());
        orderItem.setProduct(productService.findProductById(dto.getProductId()));
        orderItem.setId(dto.getId());
        return orderItem;
    }

    @Override
    public OrderItemWithProductDto convertModelToDto(OrderItem orderItem) {
        OrderItemWithProductDto orderItemDto = OrderItemWithProductDto.builder()
                .quantity(orderItem.getQuantity())
                .productId(orderItem.getProduct().getId())
                .build();

        orderItemDto.setId(orderItem.getId());
        return orderItemDto;
    }
}
