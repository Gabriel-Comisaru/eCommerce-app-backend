package com.qual.store.controller;

import com.qual.store.converter.OrderItemConverter;
import com.qual.store.dto.OrderItemDto;
import com.qual.store.model.OrderItem;
import com.qual.store.service.OrderItemService;
import org.junit.AfterClass;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.print.attribute.standard.Media;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class OrderItemControllerTest {
    private MockMvc mockMvc;

    @Mock
    private OrderItemService orderItemService;

    @Mock
    private OrderItemConverter orderItemConverter;

    @InjectMocks
    private OrderItemController orderItemController;

    private AutoCloseable closeable;

    @BeforeEach
    void setup() {
        closeable = MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(orderItemController).build();
    }

    @Test
    public void getAllOrderItemsTest() throws Exception {
        // given
        OrderItem orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setQuantity(3);

        OrderItemDto orderItemDto = new OrderItemDto();
        orderItemDto.setId(1L);
        orderItemDto.setQuantity(3);

        List<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(orderItem);

        // when
        when(orderItemService.getAllOrderItems()).thenReturn(orderItems);
        when(orderItemConverter.convertModelToDto(orderItem)).thenReturn(orderItemDto);

        // then
        mockMvc.perform(get("/api/orderItems")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(orderItemDto.getId()))
                .andExpect(jsonPath("$[0].quantity").value(orderItemDto.getQuantity()))
                .andExpect(jsonPath("$.length()").value(orderItems.size()));

        verify(orderItemService).getAllOrderItems();
        verify(orderItemConverter).convertModelToDto(orderItem);
    }

    @Test
    public void getOrderItemByIdTest() throws Exception {
        // given
        Long orderItemId = 1L;

        OrderItemDto orderItemDto = new OrderItemDto();
        orderItemDto.setId(orderItemId);
        orderItemDto.setQuantity(3);

        // when
        when(orderItemService.getOrderItemById(orderItemId)).thenReturn(orderItemDto);

        // then
        mockMvc.perform(get("/api/orderItems/{orderItemId}", orderItemId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(orderItemDto.getId()))
                .andExpect(jsonPath("$.quantity").value(orderItemDto.getQuantity()));

        verify(orderItemService).getOrderItemById(orderItemId);
    }

    @Test
    void addOrderItemTest() throws Exception {
        // given
        Long productId = 1L;
        OrderItemDto orderItemDto = new OrderItemDto();
        orderItemDto.setId(1L);
        orderItemDto.setQuantity(3);

        OrderItem orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setQuantity(3);

        // when
        when(orderItemService.addOrderItem(productId, 3)).thenReturn(orderItem);
        when(orderItemConverter.convertModelToDto(orderItem)).thenReturn(orderItemDto);

        // then
        mockMvc.perform(post("/api/orderItems/{productId}", productId)
                        .param("quantity", "3")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(orderItemDto.getId()))
                .andExpect(jsonPath("$.quantity").value(orderItemDto.getQuantity()));

        verify(orderItemService).addOrderItem(productId, 3);
        verify(orderItemConverter).convertModelToDto(orderItem);
    }

    @Test
    void deleteOrderItemTest() throws Exception {
        mockMvc.perform(delete("/api/orderItems/{orderItemId}", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("OrderItem deleted"));

        verify(orderItemService).deleteOrderItemById(1L);
    }

    @Test
    void decreaseQuantityTest() throws Exception {

        mockMvc.perform(put("/api/orderItems/{orderItemId}/quantity", 1L)
                        .param("quantity", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Quantity modified"));

        verify(orderItemService).modifyQuantity(1L, 1);
    }

    @AfterEach
    public void closeService() throws Exception {
        closeable.close();
    }
}
