package com.qual.store.controller;

import com.qual.store.converter.OrderConverter;
import com.qual.store.dto.OrderDto;
import com.qual.store.model.Order;
import com.qual.store.model.enums.OrderStatus;
import com.qual.store.service.OrderService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class OrderControllerTest {

    private MockMvc mockMvc;

    @Mock
    private OrderService orderService;

    @Mock
    private OrderConverter orderConverter;

    @InjectMocks
    private OrderController orderController;

    private AutoCloseable closeable;

    @BeforeEach
    void setup() {
        closeable = MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(orderController)
                .build();
    }

    @Test
    public void getAllOrdersTest() throws Exception {
        // given
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.ACTIVE);

        OrderDto orderDto = new OrderDto();
        orderDto.setId(1L);
        orderDto.setStatus("ACTIVE");

        List<Order> orderList = new ArrayList<>();
        orderList.add(order);

        // when
        when(orderService.getAllOrders()).thenReturn(orderList);
        when(orderConverter.convertModelToDto(order)).thenReturn(orderDto);

        // then
        mockMvc.perform(get("/api/orders")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(orderDto.getId()))
                .andExpect(jsonPath("$[0].status").value(orderDto.getStatus()))
                .andExpect(jsonPath("$.length()").value(orderList.size()));

        verify(orderService, times(1)).getAllOrders();
        verify(orderConverter, times(1)).convertModelToDto(order);
    }

    @Test
    public void addToOrderTest() throws Exception {
        // given
        Long orderItemId = 1L;
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.ACTIVE);


        OrderDto orderDto = new OrderDto();
        orderDto.setId(1L);
        orderDto.setStatus("ACTIVE");

        // when
        when(orderService.addToOrder(orderItemId)).thenReturn(order);
        when(orderConverter.convertModelToDto(order)).thenReturn(orderDto);

        // then
        mockMvc.perform(post("/api/orders/{orderItemId}", orderItemId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(orderDto.getId()))
                .andExpect(jsonPath("$.status").value(orderDto.getStatus()));

        verify(orderService, times(1)).addToOrder(orderItemId);
    }

    @Test
    void deleteOrderByIdTest() throws Exception {
        // given
        Long orderId = 1L;

        // when
        mockMvc.perform(delete("/api/orders/{id}", orderId))
                .andExpect(status().isOk())
                .andExpect(content().string("Order deleted"));

        // then
        verify(orderService, times(1)).deleteOrderById(orderId);
    }

    @Test
    void updateOrderStatusTest() throws Exception {
        // given
        Long orderId = 1L;
        String status = "SHIPPED";

        Order updatedOrder = new Order();
        updatedOrder.setId(orderId);
        updatedOrder.setStatus(OrderStatus.SHIPPED);

        OrderDto orderDto = new OrderDto();
        orderDto.setId(1L);
        orderDto.setStatus("SHIPPED");

        // when
        when(orderService.updateOrderStatus(orderId, status)).thenReturn(updatedOrder);
        when(orderConverter.convertModelToDto(updatedOrder)).thenReturn(orderDto);

        // then
        mockMvc.perform(put("/api/orders/{orderId}", orderId)
                        .param("status", status)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(orderId))
                .andExpect(jsonPath("$.status").value(orderDto.getStatus()));

        verify(orderService, times(1)).updateOrderStatus(orderId, status);
    }

    @Test
    public void getAllOrdersByUsernameTest() throws Exception {
        // given
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.ACTIVE);

        OrderDto orderDto = new OrderDto();
        orderDto.setId(1L);
        orderDto.setStatus("ACTIVE");

        List<Order> orderList = new ArrayList<>();
        orderList.add(order);

        // when
        when(orderService.getAllOrdersByUser()).thenReturn(orderList);
        when(orderConverter.convertModelToDto(order)).thenReturn(orderDto);

        // then
        mockMvc.perform(get("/api/orders/me")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(orderDto.getId()))
                .andExpect(jsonPath("$[0].status").value(orderDto.getStatus()))
                .andExpect(jsonPath("$.length()").value(orderList.size()));

        verify(orderService, times(1)).getAllOrdersByUser();
    }

    @AfterEach
    public void closeService() throws Exception {
        closeable.close();
    }
}
