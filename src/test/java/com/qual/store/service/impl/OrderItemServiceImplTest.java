package com.qual.store.service.impl;

import com.qual.store.converter.OrderItemConverter;
import com.qual.store.dto.OrderItemDto;
import com.qual.store.exceptions.OrderItemNotFoundException;
import com.qual.store.exceptions.ProductNotFoundException;
import com.qual.store.model.Category;
import com.qual.store.model.OrderItem;
import com.qual.store.model.Product;
import com.qual.store.repository.OrderItemRepository;
import com.qual.store.repository.ProductRepository;
import com.qual.store.utils.validators.Validator;
import org.junit.After;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrderItemServiceImplTest {
    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderItemConverter orderItemConverter;

    @Mock
    private Validator<OrderItem> validator;

    @InjectMocks
    private OrderItemServiceImpl orderItemService;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getAllOrderItemsTest() {
        // given
        OrderItem orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setQuantity(1);

        List<OrderItem> expectedResult = new ArrayList<>();
        expectedResult.add(orderItem);

        // when
        when(orderItemRepository.findAllWithProduct()).thenReturn(expectedResult);
        List<OrderItem> actualResult = orderItemService.getAllOrderItems();

        // then
        assertEquals(expectedResult, actualResult);
        verify(orderItemRepository, times(1)).findAllWithProduct();
    }

    @Test
    public void addOrderItemTest() {
        // given
        int quantity = 1;
        Long productId = 1L;

        OrderItem orderItem = OrderItem.builder()
                .quantity(quantity)
                .build();

        Product product = Product.builder().build();
        product.setId(productId);

        // when
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(orderItemRepository.save(orderItem)).thenReturn(orderItem);
        OrderItem actualResult = orderItemService.addOrderItem(productId, quantity);

        // then
        assertEquals(orderItem, actualResult);
        verify(validator, times(1)).validate(orderItem);
        verify(orderItemRepository, times(1)).save(orderItem);
    }

    @Test
    public void saveOrderItemThrowsProductNotFoundExceptionTest() {
        // given
        int quantity = 1;
        Long productId = 1L;

        OrderItem orderItem = OrderItem.builder()
                .quantity(quantity)
                .build();

        // when
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // then
        assertThrows(ProductNotFoundException.class, () ->
                orderItemService.addOrderItem(productId, quantity)
        );
        verify(validator, times(1)).validate(orderItem);
        verify(orderItemRepository, times(0)).save(orderItem);
        verify(orderItemRepository, times(0)).findAllWithProduct();
    }

    @Test
    void getOrderItemByIdTest() {
        // given
        Long orderItemId = 1L;
        OrderItem orderItem = new OrderItem();
        orderItem.setId(orderItemId);
        OrderItemDto expectedDto = new OrderItemDto();

        // when
        when(orderItemRepository.findAllWithProduct()).thenReturn(Collections.singletonList(orderItem));
        when(orderItemConverter.convertModelToDto(orderItem)).thenReturn(expectedDto);
        OrderItemDto actualDto = orderItemService.getOrderItemById(orderItemId);

        // then
        assertEquals(expectedDto, actualDto);

        verify(orderItemRepository, times(1)).findAllWithProduct();
        verify(orderItemConverter, times(1)).convertModelToDto(orderItem);
        verifyNoMoreInteractions(orderItemRepository, orderItemConverter);

    }

    @Test
    void getOrderItemByIdThrowsOrderItemNotFoundExceptionTest() {
        // given
        Long orderItemId = 1L;

        // when
        when(orderItemRepository.findAllWithProduct()).thenReturn(Collections.emptyList());

        // then
        assertThrows(OrderItemNotFoundException.class,
                () -> orderItemService.getOrderItemById(orderItemId)
        );

        verify(orderItemRepository, times(1)).findAllWithProduct();
        verifyNoMoreInteractions(orderItemRepository);
        verifyNoInteractions(orderItemConverter);
    }

    @Test
    void deleteOrderItemByIdTest() {
        // given
        OrderItem orderItem = OrderItem.builder()
                .quantity(1)
                .build();
        orderItem.setId(1L);
        Product product = Product.builder().build();
        product.setId(1L);
        product.setOrderItems(new HashSet<>());
        product.getOrderItems().add(orderItem);
        orderItem.setProduct(product);

        // when
        when(orderItemRepository.findById(orderItem.getId())).thenReturn(Optional.of(orderItem));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);
        orderItemService.deleteOrderItemById(orderItem.getId());

        // then
        verify(orderItemRepository, times(1)).findById(orderItem.getId());
        verify(productRepository, times(1)).findById(product.getId());
        verify(productRepository, times(1)).save(any(Product.class));
        verify(orderItemRepository, times(1)).deleteById(orderItem.getId());
    }

    @Test
    void deleteOrderItemByIdThrowsOrderItemNotFoundExceptionTest() {
        // given
        Long orderItemId = 1L;

        // when
        when(orderItemRepository.findById(orderItemId)).thenReturn(Optional.empty());

        // then
        assertThrows(OrderItemNotFoundException.class,
                () -> orderItemService.deleteOrderItemById(orderItemId));
        verify(orderItemRepository, times(1)).findById(orderItemId);
        verify(productRepository, times(0)).findById(anyLong());
        verify(productRepository, times(0)).save(any(Product.class));
        verify(orderItemRepository, times(0)).deleteById(anyLong());
    }

    @Test
    void findOrderItemByIdTest() {
        // given
        Long orderItemId = 1L;
        OrderItem orderItem = OrderItem.builder()
                .quantity(1)
                .build();
        orderItem.setId(orderItemId);

        // when
        when(orderItemRepository.findById(orderItemId)).thenReturn(Optional.of(orderItem));
        OrderItem result = orderItemService.findOrderItemById(orderItemId);

        // then
        assertNotNull(result);
        assertEquals(orderItemId, result.getId());
        assertEquals(orderItem.getQuantity(), result.getQuantity());
        verify(orderItemRepository, times(1)).findById(orderItemId);
    }

    @Test
    void findOrderItemByIdThrowsOrderItemNotFoundExceptionTest() {
        // given
        Long orderItemId = 1L;

        // when
        when(orderItemRepository.findById(orderItemId)).thenReturn(Optional.empty());

        // then
        assertThrows(OrderItemNotFoundException.class,
                () -> orderItemService.findOrderItemById(orderItemId)
        );
        verify(orderItemRepository, times(1)).findById(orderItemId);
    }

    @Test
    void modifyQuantityTest() {
        // given
        Long orderItemId = 1L;
        int quantity = 1;
        OrderItem orderItem = OrderItem.builder()
                .quantity(quantity)
                .build();
        orderItem.setId(orderItemId);

        // when
        when(orderItemRepository.findById(orderItemId)).thenReturn(Optional.of(orderItem));
        when(orderItemRepository.save(orderItem)).thenReturn(orderItem);
        orderItemService.modifyQuantity(orderItemId, quantity);

        // then
        verify(orderItemRepository, times(1)).findById(orderItemId);
        verify(orderItemRepository, times(1)).save(orderItem);
    }

    @Test
    void modifyQuantityThrowsOrderItemNotFoundExceptionTest() {
        // given
        Long orderItemId = 1L;
        int quantity = 1;

        // when
        when(orderItemRepository.findById(orderItemId)).thenReturn(Optional.empty());

        // then
        assertThrows(OrderItemNotFoundException.class,
                () -> orderItemService.modifyQuantity(orderItemId, quantity)
        );
        verify(orderItemRepository, times(1)).findById(orderItemId);
        verify(orderItemRepository, times(0)).save(any(OrderItem.class));
    }

    @AfterEach
    public void closeService() throws Exception{
        closeable.close();
    }
}
