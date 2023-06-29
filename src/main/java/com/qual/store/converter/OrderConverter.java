package com.qual.store.converter;

import com.qual.store.converter.base.BaseConverter;
import com.qual.store.dto.OrderDto;
import com.qual.store.model.base.BaseEntity;
import com.qual.store.model.Order;
import com.qual.store.model.enums.OrderStatus;
import com.qual.store.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class OrderConverter extends BaseConverter<Order, OrderDto> {

    @Autowired
    private AppUserRepository appUserRepository;
    @Override
    public Order convertDtoToModel(OrderDto dto) {
        return Order.builder()
                .deliveryPrice(dto.getDeliveryPrice())
                .startDate(dto.getStartDate())
                .deliveryDate(dto.getDeliveryDate())
                .status(OrderStatus.valueOf(dto.getStatus()))
                .user(appUserRepository.findById(dto.getUserId()).orElse(null))
                .build();
    }

    @Override
    public OrderDto convertModelToDto(Order order) {
        OrderDto orderDto = OrderDto.builder()
                .deliveryPrice(order.getDeliveryPrice())
                .startDate(order.getStartDate())
                .deliveryDate(order.getDeliveryDate())
                .status(order.getStatus().name())
                .userId(order.getUser().getId())
                .orderItems(order.getOrderItems().stream().map(BaseEntity::getId).collect(Collectors.toList()))
                .build();
        orderDto.setId(order.getId());
        return orderDto;
    }
}
