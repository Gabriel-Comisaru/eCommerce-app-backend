package com.qual.store.converter.lazyConverter;

import com.qual.store.converter.OrderItemConverter;
import com.qual.store.converter.base.BaseConverter;
import com.qual.store.dto.OrderDto;
import com.qual.store.dto.OrderItemDto;
import com.qual.store.dto.lazyDto.OrderItemWithProductDto;
import com.qual.store.dto.lazyDto.OrderWithOrderItemDto;
import com.qual.store.model.Order;
import com.qual.store.model.OrderItem;
import com.qual.store.model.base.BaseEntity;
import com.qual.store.model.enums.OrderStatus;
import com.qual.store.repository.AppUserRepository;
import com.qual.store.repository.OrderItemRepository;
import com.qual.store.service.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderWithOrderItemsConverter extends BaseConverter<Order, OrderWithOrderItemDto> {
    private final AppUserRepository appUserRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderItemService orderItemService;
    private final OrderItemConverter orderItemConverter;

    @Override
    public Order convertDtoToModel(OrderWithOrderItemDto dto) {
        Order model = Order.builder()
                .deliveryPrice(dto.getDeliveryPrice())
                .startDate(dto.getStartDate())
                .deliveryDate(dto.getDeliveryDate())
                .status(OrderStatus.valueOf(dto.getStatus()))
                .user(appUserRepository.findById(dto.getUserId()).orElse(null))
                .orderItems(dto.getOrderItems().stream().map(id -> orderItemRepository.findById(id.getId()).orElse(null)).collect(Collectors.toSet()))
                .build();
        model.setId(dto.getId());
        return model;
    }

    @Override
    public OrderWithOrderItemDto convertModelToDto(Order order) {
        OrderWithOrderItemDto orderDto = OrderWithOrderItemDto.builder()
                .deliveryPrice(order.getDeliveryPrice())
                .startDate(order.getStartDate())
                .deliveryDate(order.getDeliveryDate())
                .status(order.getStatus().name())
                .userId(order.getUser().getId())
                .userName(order.getUser().getFirstName() + " " + order.getUser().getLastName())
                .userEmail(order.getUser().getEmail())
                .build();
        List<OrderItem> orderItems = order.getOrderItems().stream().toList();
        // get all orderItems with products that match orderItemDtos
        List<OrderItemDto> orderItemDtosToAdd = orderItemService.getAllOrderItems()
                .stream()
                .filter(orderItem -> orderItems.stream().anyMatch(orderItemDto -> orderItemDto.getId().equals(orderItem.getId())))
                .map(orderItemConverter::convertModelToDto)
                .toList();
        orderDto.setOrderItems(orderItemDtosToAdd);
        orderDto.setId(order.getId());
        return orderDto;
    }
}
