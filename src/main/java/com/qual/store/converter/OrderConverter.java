package com.qual.store.converter;

import com.qual.store.dto.OrderDto;
import com.qual.store.model.BaseEntity;
import com.qual.store.model.Order;
import com.qual.store.model.OrderStatus;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class OrderConverter extends BaseConverter<Order, OrderDto> {

    @Override
    public Order convertDtoToModel(OrderDto dto) {
        return Order.builder()
                .deliveryPrice(dto.getDeliveryPrice())
                .startDate(dto.getStartDate())
                .deliveryDate(dto.getDeliveryDate())
                .status(OrderStatus.valueOf(dto.getStatus()))
                .userId(dto.getUserId())
                .build();
    }

    @Override
    public OrderDto convertModelToDto(Order order) {
        OrderDto orderDto = OrderDto.builder()
                .deliveryPrice(order.getDeliveryPrice())
                .startDate(order.getStartDate())
                .deliveryDate(order.getDeliveryDate())
                .status(order.getStatus().name())
                .userId(order.getUserId())
                .orderItems(order.getOrderItems().stream().map(BaseEntity::getId).collect(Collectors.toList()))
                .build();
        orderDto.setId(order.getId());
        return orderDto;
    }
}
