package com.qual.store.controller;

import com.qual.store.converter.OrderConverter;
import com.qual.store.converter.OrderItemConverter;
import com.qual.store.converter.lazyConverter.OrderWithOrderItemsConverter;
import com.qual.store.dto.OrderDto;
import com.qual.store.dto.OrderItemDto;
import com.qual.store.dto.lazyDto.OrderWithOrderItemDto;
import com.qual.store.dto.paginated.PaginatedOrderResponse;
import com.qual.store.model.Order;
import com.qual.store.model.OrderItem;
import com.qual.store.model.enums.OrderStatus;
import com.qual.store.service.OrderItemService;
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

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class OrderControllerTest {

    private MockMvc mockMvc;

    @Mock
    private OrderService orderService;

    @Mock
    private OrderItemService orderItemService;

    @Mock
    private OrderConverter orderConverter;

    @Mock
    private OrderItemConverter orderItemConverter;

    @Mock
    private OrderWithOrderItemsConverter orderWithOrderItemConverter;

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
        Long productId = 1L;
        Integer quantity = 1;
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.ACTIVE);

        OrderItem orderItem = new OrderItem();
        orderItem.setId(orderItemId);
        orderItem.setQuantity(1);

        OrderItemDto orderItemDto = new OrderItemDto();
        orderItemDto.setId(orderItemId);
        orderItemDto.setQuantity(1);

        // when
        when(orderItemService.addOrderItem(productId, quantity)).thenReturn(orderItem);
        when(orderService.addToOrder(orderItemId)).thenReturn(order);
        when(orderItemConverter.convertModelToDto(orderItem)).thenReturn(orderItemDto);

        // then
        mockMvc.perform(post("/api/orders/{productId}", productId)
                        .param("quantity", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(orderItemDto.getId()))
                .andExpect(jsonPath("$.quantity").value(orderItemDto.getQuantity()));

        verify(orderItemService, times(1)).addOrderItem(productId, quantity);
        verify(orderService, times(1)).addToOrder(orderItemId);
        verify(orderItemConverter, times(1)).convertModelToDto(orderItem);
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

    @Test
    public void getAllOrdersWithOrderItemsByUsernameTest() throws Exception {
        // given
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.ACTIVE);

        OrderWithOrderItemDto orderDto = OrderWithOrderItemDto.builder()
                .deliveryPrice(200)
                .orderItems(new ArrayList<>())
                .deliveryDate(LocalDate.now().plus(2, ChronoUnit.DAYS))
                .status("ACTIVE")
                .build();

        OrderItemDto orderItemDto = OrderItemDto.builder()
                .quantity(1)
                .build();
        orderItemDto.setId(1L);

        orderDto.getOrderItems().add(orderItemDto);

        List<Order> orderList = new ArrayList<>();
        orderList.add(order);

        // when
        when(orderService.getAllOrdersByUser()).thenReturn(orderList);
        when(orderWithOrderItemConverter.convertModelToDto(order)).thenReturn(orderDto);

        // then
        mockMvc.perform(get("/api/orders/me/lazy")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(orderDto.getId()))
                .andExpect(jsonPath("$[0].status").value(orderDto.getStatus()))
                .andExpect(jsonPath("$[0].orderItems[0].id").value(orderItemDto.getId()))
                .andExpect(jsonPath("$[0].orderItems[0].quantity").value(orderItemDto.getQuantity()))
                .andExpect(jsonPath("$.length()").value(orderList.size()))
                .andExpect(jsonPath("$[0].orderItems.length()").value(orderDto.getOrderItems().size()));

        verify(orderService, times(1)).getAllOrdersByUser();
    }

    @Test
    void getBasketTest() throws Exception {
        // given
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.ACTIVE);
        order.setOrderItems(new HashSet<>());

        OrderItem orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setQuantity(1);

        OrderItemDto orderItemDto = new OrderItemDto();
        orderItemDto.setId(1L);
        orderItemDto.setQuantity(1);

        orderItem.setOrder(order);
        orderItemDto.setOrderId(1L);
        order.addOrderItem(orderItem);

        List<OrderItem> orderItemList = new ArrayList<>();
        orderItemList.add(orderItem);

        // when
        when(orderService.getBasketAsOrderItems()).thenReturn(orderItemList);
        when(orderItemConverter.convertModelToDto(orderItem)).thenReturn(orderItemDto);

        // then
        mockMvc.perform(get("/api/orders/me/basket")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(orderItemDto.getId()))
                .andExpect(jsonPath("$[0].orderId").value(order.getId()))
                .andExpect(jsonPath("$[0].quantity").value(orderItemDto.getQuantity()))
                .andExpect(jsonPath("$.length()").value(order.getOrderItems().size()));

        verify(orderService, times(1)).getBasketAsOrderItems();
        verify(orderItemConverter, times(1)).convertModelToDto(orderItem);
    }

    @Test
    void getOrdersPaginatedTest() throws Exception {
        // given
        int pageNumber = 0;
        int pageSize = 10;
        String sortBy = "id";

        OrderDto firstOrderDto = OrderDto.builder()
                .orderItems(List.of(1L, 2L, 3L))
                .deliveryPrice(200)
                .deliveryDate(LocalDate.now().plus(2, ChronoUnit.DAYS))
                .status("ACTIVE")
                .build();

        OrderDto secondOrderDto = OrderDto.builder()
                .orderItems(List.of(4L, 5L, 6L))
                .deliveryPrice(400)
                .deliveryDate(LocalDate.now().minus(2, ChronoUnit.DAYS))
                .status("DELIVERED")
                .build();

        List<OrderDto> orders = new ArrayList<>();
        orders.add(firstOrderDto);
        orders.add(secondOrderDto);

        long numberOfItems = 2L;
        int numberOfPages = 1;
        PaginatedOrderResponse paginatedResponse = PaginatedOrderResponse.builder()
                .orders(orders)
                .numberOfItems(numberOfItems)
                .numberOfPages(numberOfPages)
                .build();

        // when
        when(orderService.getOrders(pageNumber, pageSize, sortBy)).thenReturn(paginatedResponse);

        // then
        mockMvc.perform(get("/api/orders/display")
                        .param("pageNumber", String.valueOf(pageNumber))
                        .param("pageSize", String.valueOf(pageSize))
                        .param("sortBy", sortBy))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.orders[0].deliveryPrice").value(200))
                .andExpect(jsonPath("$.orders[1].deliveryPrice").value(400))
                .andExpect(jsonPath("$.numberOfItems").value(numberOfItems))
                .andExpect(jsonPath("$.numberOfPages").value(numberOfPages));

        verify(orderService, times(1)).getOrders(pageNumber, pageSize, sortBy);
    }

    @Test
    void searchOrdersByUsernameTest() throws Exception {
        // given
        String username = "testuser";

        Order order1 = new Order();
        order1.setId(1L);
        order1.setStatus(OrderStatus.ACTIVE);

        Order order2 = new Order();
        order2.setId(2L);
        order2.setStatus(OrderStatus.DELIVERED);

        OrderDto orderDto1 = new OrderDto();
        orderDto1.setId(1L);
        orderDto1.setStatus("ACTIVE");

        OrderDto orderDto2 = new OrderDto();
        orderDto2.setId(2L);
        orderDto2.setStatus("DELIVERED");

        List<OrderDto> orderList = new ArrayList<>();
        orderList.add(orderDto1);
        orderList.add(orderDto2);

        // when
        when(orderService.searchOrdersByUsername(username)).thenReturn(orderList);

        // then
        mockMvc.perform(get("/api/orders/search")
                        .param("user", username)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(orderDto1.getId()))
                .andExpect(jsonPath("$[0].status").value(orderDto1.getStatus()))
                .andExpect(jsonPath("$[1].id").value(orderDto2.getId()))
                .andExpect(jsonPath("$[1].status").value(orderDto2.getStatus()))
                .andExpect(jsonPath("$.length()").value(orderList.size()));

        verify(orderService, times(1)).searchOrdersByUsername(username);
    }

    @AfterEach
    public void closeService() throws Exception {
        closeable.close();
    }
}
