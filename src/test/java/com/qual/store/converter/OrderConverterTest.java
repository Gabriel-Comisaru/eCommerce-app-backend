package com.qual.store.converter;

import com.qual.store.dto.OrderDto;
import com.qual.store.model.AppUser;
import com.qual.store.model.Order;
import com.qual.store.model.OrderItem;
import com.qual.store.model.Product;
import com.qual.store.model.enums.OrderStatus;
import com.qual.store.repository.AppUserRepository;
import com.qual.store.repository.OrderItemRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class OrderConverterTest {

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private AppUserRepository appUserRepository;

    @InjectMocks
    private OrderConverter orderConverter;

    private AutoCloseable autoCloseable;

    @BeforeEach
    public void setup() {
        autoCloseable = org.mockito.MockitoAnnotations.openMocks(this);
    }

    @Test
    public void convertDtoToModelTest() {
        // given
        Long orderId = 1L;
        Long userId = 2L;
        Long orderItemId = 3L;

        OrderDto orderDto = OrderDto.builder()
                .status("ACTIVE")
                .deliveryPrice(10.0)
                .userId(userId)
                .orderItems(List.of(orderItemId))
                .build();

        OrderItem orderItem = OrderItem.builder()
                .quantity(3)
                .build();
        orderItem.setId(orderItemId);

        AppUser appUser = AppUser.builder()
                .username("username")
                .build();
        appUser.setId(userId);

        Order expectedOrder = Order.builder()
                .status(OrderStatus.ACTIVE)
                .deliveryPrice(10.0)
                .user(appUser)
                .orderItems(Set.of(orderItem))
                .build();

        // when
        when(appUserRepository.findById(userId)).thenReturn(Optional.of(appUser));
        when(orderItemRepository.findById(orderItemId)).thenReturn(Optional.of(orderItem));
        Order actualOrder = orderConverter.convertDtoToModel(orderDto);

        // then
        assertEquals(expectedOrder, actualOrder);

        verify(appUserRepository).findById(userId);
        verify(orderItemRepository).findById(orderItemId);
    }

    @Test
    public void convertModelToDtoTest() {
        // given
        Long orderId = 1L;
        Long orderItemId = 2L;
        Long userId = 3L;

        Product product = Product.builder()
                .name("name")
                .build();
        product.setId(1L);

        OrderItem orderItem = OrderItem.builder()
                .quantity(3)
                .product(product)
                .build();
        orderItem.setId(orderItemId);

        AppUser appUser = AppUser.builder()
                .username("username")
                .email("email")
                .firstName("fn")
                .lastName("ln")
                .build();
        appUser.setId(userId);

        Order order = Order.builder()
                .status(OrderStatus.ACTIVE)
                .deliveryPrice(10.0)
                .startDate(LocalDate.now())
                .deliveryDate(LocalDate.now().plus(2, ChronoUnit.DAYS))
                .user(appUser)
                .orderItems(Set.of(orderItem))
                .build();
        order.setId(orderId);

        OrderDto expectedOrderDto = OrderDto.builder()
                .status("ACTIVE")
                .deliveryPrice(10.0)
                .userId(userId)
                .orderItems(List.of(orderItemId))
                .build();
        expectedOrderDto.setId(orderId);

        // when
        OrderDto actualOrderDto = orderConverter.convertModelToDto(order);

        // then
        assertEquals(expectedOrderDto.getDeliveryPrice(), actualOrderDto.getDeliveryPrice());
        assertEquals(expectedOrderDto.getStatus(), actualOrderDto.getStatus());
        assertEquals(expectedOrderDto.getUserId(), actualOrderDto.getUserId());
        assertEquals(expectedOrderDto.getOrderItems(), actualOrderDto.getOrderItems());
        assertEquals(expectedOrderDto.getId(), actualOrderDto.getId());
    }

    @AfterEach
    public void close() throws Exception {
        autoCloseable.close();
    }
}
