package com.qual.store.lazyConverter;

import com.qual.store.converter.BaseConverter;
import com.qual.store.dto.OrderItemDto;
import com.qual.store.lazyDto.OrderItemWithProductDto;
import com.qual.store.model.OrderItem;
import com.qual.store.service.OrderService;
import com.qual.store.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderItemLazyConverter extends BaseConverter<OrderItem, OrderItemWithProductDto> {

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

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
