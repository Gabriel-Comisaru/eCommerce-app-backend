package com.qual.store.converter;

import com.qual.store.dto.OrderItemDto;
import com.qual.store.model.Category;
import com.qual.store.model.Order;
import com.qual.store.model.OrderItem;
import com.qual.store.model.Product;
import com.qual.store.repository.AppUserRepository;
import com.qual.store.repository.OrderRepository;
import com.qual.store.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class OrderItemConverterTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderItemConverter orderItemConverter;

    private AutoCloseable closeable;

    @BeforeEach
    public void setup() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @Test
    public void convertDtoToModelTest() {
        // given
        Long productId = 1L;
        Long orderId = 2L;
        Long categoryId = 3L;

        OrderItemDto orderItemDto = OrderItemDto.builder()
                .productId(productId)
                .orderId(orderId)
                .quantity(3)
                .categoryId(categoryId)
                .build();

        Product product = Product.builder()
                .name("Product")
                .build();
        product.setId(productId);

        OrderItem expectedOrderItem = OrderItem.builder()
                .product(product)
                .quantity(3)
                .order(null)
                .build();

        // when
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());
        OrderItem actualOrderItem = orderItemConverter.convertDtoToModel(orderItemDto);

        // then
        assertEquals(expectedOrderItem, actualOrderItem);

        verify(productRepository, times(1)).findById(productId);
        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    public void convertModelToDtoTest() {
        // given
        Long orderItemId = 1L;
        Long productId = 2L;
        Long orderId = 3L;
        Order order = new Order();
        order.setId(orderId);

        Category category = Category.builder()
                .name("Category")
                .build();
        Product product = Product.builder()
                .name("Product")
                .category(category)
                .build();
        product.setId(productId);

        OrderItem orderItem = OrderItem.builder()
                .product(product)
                .quantity(3)
                .order(order)
                .build();
        orderItem.setId(orderItemId);

        OrderItemDto expectedOrderItemDto = OrderItemDto.builder()
                .productId(productId)
                .orderId(orderId)
                .quantity(3)
                .categoryId(null)
                .build();
        expectedOrderItemDto.setId(orderItemId);

        // when
        OrderItemDto actualOrderItemDto = orderItemConverter.convertModelToDto(orderItem);

        // then
        assertEquals(expectedOrderItemDto.getQuantity(), actualOrderItemDto.getQuantity());
        assertEquals(expectedOrderItemDto.getProductId(), actualOrderItemDto.getProductId());
        assertEquals(expectedOrderItemDto.getOrderId(), actualOrderItemDto.getOrderId());
        assertEquals(expectedOrderItemDto.getId(), actualOrderItemDto.getId());
    }

    @AfterEach
    public void closeService() throws Exception {
        closeable.close();
    }
}
