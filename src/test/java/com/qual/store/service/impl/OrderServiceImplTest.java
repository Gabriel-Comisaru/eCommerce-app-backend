package com.qual.store.service.impl;


import com.qual.store.converter.OrderConverter;
import com.qual.store.dto.OrderDto;
import com.qual.store.dto.ProductDto;
import com.qual.store.dto.paginated.PaginatedOrderResponse;
import com.qual.store.dto.paginated.PaginatedProductResponse;
import com.qual.store.exceptions.InvalidOrderStatusException;
import com.qual.store.exceptions.OrderItemNotFoundException;
import com.qual.store.exceptions.OrderNotFoundException;
import com.qual.store.exceptions.UpdateOrderStatusException;
import com.qual.store.model.AppUser;
import com.qual.store.model.Order;
import com.qual.store.model.OrderItem;
import com.qual.store.model.Product;
import com.qual.store.model.enums.OrderStatus;
import com.qual.store.model.enums.RoleName;
import com.qual.store.repository.AppUserRepository;
import com.qual.store.repository.OrderItemRepository;
import com.qual.store.repository.OrderRepository;
import com.qual.store.repository.ProductRepository;
import com.qual.store.service.OrderItemService;
import com.qual.store.utils.validators.Validator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrderServiceImplTest {
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private OrderConverter orderConverter;

    @Mock
    private Validator<Order> validator;

    @Mock
    private OrderItemService orderItemService;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getAllOrdersTest() {
        // given
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.ACTIVE);

        OrderItem orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setQuantity(1);
        orderItem.setOrder(order);

        order.getOrderItems().add(orderItem);

        AppUser appUser = new AppUser();
        appUser.setId(1L);
        appUser.setFirstName("John");
        appUser.setLastName("Doe");
        appUser.setEmail("johndoe@yahoo.com");

        appUser.setOrders(new HashSet<>());
        appUser.getOrders().add(order);

        order.setUser(appUser);

        List<Order> expectedResult = new ArrayList<>();
        expectedResult.add(order);

        // when
        when(orderRepository.findAllWithOrderItemsAndProducts()).thenReturn(expectedResult);
        List<Order> actualResult = orderService.getAllOrders();

        // then
        assertEquals(expectedResult, actualResult);
        verify(orderRepository, times(1)).findAllWithOrderItemsAndProducts();
    }

    @Test
    public void addToOrderTest() {
        // given
        OrderItem orderItem = OrderItem.builder().build();
        orderItem.setId(1L);

        Order order = Order.builder()
                .status(OrderStatus.ACTIVE)
                .deliveryPrice(200.0)
                .orderItems(new HashSet<>())
                .build();
        order.addOrderItem(orderItem);

        Authentication authentication = new UsernamePasswordAuthenticationToken(new AppUser(), new Object());
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        //when
        when(orderItemRepository.findById(orderItem.getId())).thenReturn(Optional.of(orderItem));
        when(appUserRepository.findUserByUsername(anyString())).thenReturn(new AppUser());
        Order savedOrder = orderService.addToOrder(orderItem.getId());

        //then
        assertEquals(order, savedOrder);
        verify(validator, times(1)).validate(order);
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    public void addToOrderThrowsOrderItemNotFoundExceptionTest() {
        // given
        Long orderItemId = 1L;

        Authentication authentication = new UsernamePasswordAuthenticationToken(new AppUser(), new Object());
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // when
        when(orderItemRepository.findById(orderItemId)).thenReturn(Optional.empty());

        // then
        assertThrows(OrderItemNotFoundException.class,
                () -> orderService.addToOrder(orderItemId)
        );
        verify(validator, times(1)).validate(any());
        verify(orderRepository, times(0)).save(any());
    }

    @Test
    public void deleteOrderByIdTest() {
        // given
        Order order = Order.builder()
                .status(OrderStatus.ACTIVE)
                .deliveryPrice(200.0)
                .orderItems(new HashSet<>())
                .build();
        OrderItem orderItem = OrderItem.builder().build();
        order.setId(1L);
        orderItem.setId(1L);
        orderItem.setOrder(order);
        order.addOrderItem(orderItem);

        List<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(orderItem);

        Authentication authentication = new UsernamePasswordAuthenticationToken(new AppUser(), new Object());
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // when
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(orderItemRepository.saveAll(order.getOrderItems())).thenReturn(new ArrayList<>());
        orderService.deleteOrderById(order.getId());

        // then
        verify(orderRepository, times(1)).findById(order.getId());
        verify(orderItemService, times(1)).deleteOrderItemById(orderItems.get(0).getId());
        verify(orderRepository, times(1)).deleteById(order.getId());

    }

    @Test
    void deleteOrderByIdThrowsOrderNotFoundExceptionTest() {
        // given
        Long orderId = 1L;

        // when
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // then
        assertThrows(OrderNotFoundException.class,
                () -> orderService.deleteOrderById(orderId)
        );
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderItemRepository, times(0)).saveAll(any());
        verify(orderRepository, times(0)).deleteById(any());
    }

    @Test
    void findOrderByIdTest() {
        // given
        Long orderId = 1L;
        Order order = Order.builder()
                .status(OrderStatus.ACTIVE)
                .deliveryPrice(200.0)
                .orderItems(new HashSet<>())
                .build();
        order.setId(orderId);

        // when
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        Order actualResult = orderService.findOrderById(orderId);

        // then
        assertNotNull(actualResult);
        assertEquals(orderId, actualResult.getId());
        assertEquals(order.getStatus(), actualResult.getStatus());
        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    void findOrderByIdThrowsOrderNotFoundException() {
        // given
        Long orderId = 1L;

        // when
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // then
        assertThrows(OrderNotFoundException.class,
                () -> orderService.findOrderById(orderId)
        );
        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    void updateOrderStatusAsAdminTest() {
        // given
        Long orderId = 1L;
        Order order = Order.builder()
                .status(OrderStatus.ACTIVE)
                .deliveryPrice(200.0)
                .orderItems(new HashSet<>())
                .build();
        order.setId(orderId);

        Optional<Order> optionalOrder = Optional.of(order);
        AppUser appUser = new AppUser();
        appUser.setUsername("username");
        appUser.setRole(RoleName.ADMIN);
        order.setUser(appUser);

        Authentication authentication = new UsernamePasswordAuthenticationToken(appUser, new Object());
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // when
        when(orderRepository.findAllWithOrderItems()).thenReturn(List.of(order));
        when(orderRepository.findById(orderId)).thenReturn(optionalOrder);
        when(appUserRepository.findUserByUsername(anyString())).thenReturn(appUser);
        Order actualResult = orderService.updateOrderStatus(orderId, "delivered");

        // then
        assertNotNull(actualResult);
        assertEquals(orderId, actualResult.getId());
        assertEquals(OrderStatus.DELIVERED, actualResult.getStatus());
        verify(orderRepository, times(1)).findAllWithOrderItems();
        verify(appUserRepository, times(1)).findUserByUsername(anyString());
    }

    @Test
    void updateOrderStatusAsUserTest() {
        // given
        Long orderId = 1L;
        Order order = Order.builder()
                .status(OrderStatus.ACTIVE)
                .deliveryPrice(200.0)
                .orderItems(new HashSet<>())
                .build();
        order.setId(orderId);

        Optional<Order> optionalOrder = Optional.of(order);
        AppUser appUser = new AppUser();
        appUser.setUsername("username");
        appUser.setRole(RoleName.USER);
        order.setUser(appUser);

        Authentication authentication = new UsernamePasswordAuthenticationToken(appUser, new Object());
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // when
        when(orderRepository.findAllWithOrderItems()).thenReturn(List.of(order));
        when(orderRepository.findById(orderId)).thenReturn(optionalOrder);
        when(appUserRepository.findUserByUsername(anyString())).thenReturn(appUser);
        Order actualResult = orderService.updateOrderStatus(orderId, "cHeckOut");

        // then
        assertNotNull(actualResult);
        assertEquals(orderId, actualResult.getId());
        assertEquals(OrderStatus.CHECKOUT, actualResult.getStatus());
        verify(orderRepository, times(1)).findAllWithOrderItems();
        verify(appUserRepository, times(1)).findUserByUsername(anyString());
    }

    @Test
    void updateOrderStatusThrowsOrderNotFoundExceptionTest() {
        // given
        Long orderId = 1L;

        // when
        when(orderRepository.findAllWithOrderItems()).thenReturn(new ArrayList<>());

        // then
        assertThrows(OrderNotFoundException.class,
                () -> orderService.updateOrderStatus(orderId, "DELIVERED")
        );
        verify(orderRepository, times(1)).findAllWithOrderItems();
        verify(appUserRepository, times(0)).findUserByUsername(anyString());
    }

    @Test
    void updateOrderStatusAsAdminThrowsInvalidOrderStatusExceptionTest() {
        // given
        Long orderId = 1L;
        Order order = Order.builder()
                .status(OrderStatus.ACTIVE)
                .deliveryPrice(200.0)
                .orderItems(new HashSet<>())
                .build();
        order.setId(orderId);

        Optional<Order> optionalOrder = Optional.of(order);
        AppUser appUser = new AppUser();
        appUser.setUsername("username");
        appUser.setRole(RoleName.ADMIN);
        order.setUser(appUser);

        Authentication authentication = new UsernamePasswordAuthenticationToken(appUser, new Object());
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // when
        when(orderRepository.findAllWithOrderItems()).thenReturn(List.of(order));
        when(orderRepository.findById(orderId)).thenReturn(optionalOrder);
        when(appUserRepository.findUserByUsername(anyString())).thenReturn(appUser);

        // then
        assertThrows(InvalidOrderStatusException.class,
                () -> orderService.updateOrderStatus(orderId, "FAKE_STATUS")
        );
        verify(orderRepository, times(1)).findAllWithOrderItems();
        verify(appUserRepository, times(1)).findUserByUsername(anyString());
    }

    @Test
    void updateOrderStatusAsUserThrowsInvalidOrderStatusExceptionTest() {
        // given
        Long orderId = 1L;
        Order order = Order.builder()
                .status(OrderStatus.ACTIVE)
                .deliveryPrice(200.0)
                .orderItems(new HashSet<>())
                .build();
        order.setId(orderId);

        Optional<Order> optionalOrder = Optional.of(order);
        AppUser appUser = new AppUser();
        appUser.setUsername("username");
        appUser.setRole(RoleName.USER);
        order.setUser(appUser);

        Authentication authentication = new UsernamePasswordAuthenticationToken(appUser, new Object());
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // when
        when(orderRepository.findAllWithOrderItems()).thenReturn(List.of(order));
        when(orderRepository.findById(orderId)).thenReturn(optionalOrder);
        when(appUserRepository.findUserByUsername(anyString())).thenReturn(appUser);

        // then
        assertThrows(InvalidOrderStatusException.class,
                () -> orderService.updateOrderStatus(orderId, "DELIVERED")
        );
        verify(orderRepository, times(1)).findAllWithOrderItems();
        verify(appUserRepository, times(1)).findUserByUsername(anyString());
    }

    @Test
    void updateOrderStatusAsUserThrowsUpdateOrderStatusExceptionTest() {
        // given
        Long orderId = 1L;
        Order order = Order.builder()
                .status(OrderStatus.PLACED)
                .deliveryPrice(200.0)
                .orderItems(new HashSet<>())
                .build();
        order.setId(orderId);

        Optional<Order> optionalOrder = Optional.of(order);
        AppUser appUser = new AppUser();
        appUser.setUsername("username");
        appUser.setRole(RoleName.USER);
        order.setUser(appUser);

        Authentication authentication = new UsernamePasswordAuthenticationToken(appUser, new Object());
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // when
        when(orderRepository.findAllWithOrderItems()).thenReturn(List.of(order));
        when(orderRepository.findById(orderId)).thenReturn(optionalOrder);
        when(appUserRepository.findUserByUsername(anyString())).thenReturn(appUser);

        // then
        assertThrows(UpdateOrderStatusException.class,
                () -> orderService.updateOrderStatus(orderId, "CHECKOUT")
        );
        verify(orderRepository, times(1)).findAllWithOrderItems();
        verify(appUserRepository, times(1)).findUserByUsername(anyString());
    }

    @Test
    void getAllOrdersByUserTest() {
        // given
        Long userId = 1L;
        AppUser appUser = new AppUser();
        appUser.setId(userId);
        appUser.setUsername("username");
        appUser.setRole(RoleName.USER);

        Order order = Order.builder()
                .status(OrderStatus.ACTIVE)
                .deliveryPrice(200.0)
                .orderItems(new HashSet<>())
                .build();
        order.setUser(appUser);

        OrderDto orderDto = new OrderDto();
        orderDto.setStatus("ACTIVE");
        orderDto.setDeliveryPrice(200.0);
        orderDto.setOrderItems(new ArrayList<>());
        orderDto.setUserId(userId);

        List<Order> orders = List.of(order);


        Authentication authentication = new UsernamePasswordAuthenticationToken(appUser, new Object());
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // when
        when(orderService.getAllOrders()).thenReturn(orders);
        when(appUserRepository.findUserByUsername(anyString())).thenReturn(appUser);
        when(orderConverter.convertModelToDto(any(Order.class))).thenReturn(orderDto);
        when(orderConverter.convertDtoToModel(any(OrderDto.class))).thenReturn(order);
        List<Order> actualResult = orderService.getAllOrdersByUser();

        // then
        assertNotNull(actualResult);
        assertEquals(1, actualResult.size());
        assertEquals(order, actualResult.get(0));
        verify(appUserRepository, times(1)).findUserByUsername(anyString());
        verify(orderConverter, times(1)).convertModelToDto(any(Order.class));
        verify(orderConverter, times(1)).convertDtoToModel(any(OrderDto.class));
    }

    @Test
    void getProductsQuantityTest() {
        // given
        Product product = Product.builder()
                .name("product")
                .price(100.0)
                .build();
        product.setId(1L);

        OrderItem orderItem = OrderItem.builder()
                .quantity(2)
                .product(product)
                .build();
        orderItem.setId(1L);

        Order order = Order.builder()
                .status(OrderStatus.PLACED)
                .deliveryPrice(200.0)
                .orderItems(new HashSet<>())
                .build();
        order.setId(1L);
        order.getOrderItems().add(orderItem);

        // when
        when(orderService.getAllOrders()).thenReturn(List.of(order));
        Map<Long, Integer> actualResult = orderService.getProductsQuantity();

        // then
        assertNotNull(actualResult);
        assertEquals(1, actualResult.size());
        assertEquals(2, actualResult.get(1L));

    }

    @Test
    public void getOrdersPaginatedTest() {
        // given
        int pageNumber = 0;
        int pageSize = 10;
        String sortBy = "id";
        Order order = new Order();
        List<Order> orders = List.of(order);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Order> orderPage = new PageImpl<>(orders, pageable, orders.size());
        OrderDto orderDto = new OrderDto();
        PaginatedOrderResponse expectedResponse = PaginatedOrderResponse.builder()
                .orders(List.of(orderDto))
                .numberOfItems((long) orders.size())
                .numberOfPages(1)
                .build();

        // when
        when(orderRepository.findAllWithOrderItems(pageable)).thenReturn(orderPage);
        when(orderConverter.convertModelToDto(any(Order.class))).thenReturn(orderDto);
        PaginatedOrderResponse actualResponse = orderService.getOrders(pageNumber, pageSize, sortBy);

        // then
        assertEquals(expectedResponse.getOrders(), actualResponse.getOrders());
        assertEquals(expectedResponse.getNumberOfItems(), actualResponse.getNumberOfItems());
        assertEquals(expectedResponse.getNumberOfPages(), actualResponse.getNumberOfPages());

        verify(orderRepository, times(1)).findAllWithOrderItems(pageable);
        verify(orderConverter, times(1)).convertModelToDto(order);
        verifyNoMoreInteractions(orderRepository);
    }

    @Test
    public void getBasketTest() {
        // given
        Long userId = 1L;
        AppUser appUser = new AppUser();
        appUser.setId(userId);
        appUser.setUsername("username");
        appUser.setRole(RoleName.USER);

        Order order = Order.builder()
                .status(OrderStatus.ACTIVE)
                .deliveryPrice(200.0)
                .orderItems(new HashSet<>())
                .build();
        order.setUser(appUser);


        Authentication authentication = new UsernamePasswordAuthenticationToken(appUser, new Object());
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // when
        when(appUserRepository.findUserByUsername(anyString())).thenReturn(appUser);
        when(orderRepository.findAllWithOrderItems()).thenReturn(List.of(order));
        Order actualResult = orderService.getBasket();

        // then
        assertNotNull(actualResult);
        assertEquals(order, actualResult);
        verify(appUserRepository, times(1)).findUserByUsername(anyString());
        verify(orderRepository, times(1)).findAllWithOrderItems();
    }

    @Test
    public void getBasketAsOrderItems() {
        // given
        OrderItem orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setQuantity(1);
        orderItem.setProduct(new Product());
        orderItem.getProduct().setId(1L);
        orderItem.getProduct().setName("product");
        orderItem.getProduct().setPrice(100.0);

        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.ACTIVE);
        order.setDeliveryPrice(200.0);
        order.setOrderItems(new HashSet<>());
        orderItem.setOrder(order);
        order.getOrderItems().add(orderItem);

        AppUser appUser = new AppUser();
        appUser.setId(1L);
        appUser.setUsername("username");
        appUser.setRole(RoleName.USER);
        order.setUser(appUser);

        Authentication authentication = new UsernamePasswordAuthenticationToken(appUser, new Object());
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // when
        when(appUserRepository.findUserByUsername(anyString())).thenReturn(appUser);
        when(orderRepository.findAllWithOrderItems()).thenReturn(List.of(order));
        when(orderItemRepository.findAllWithProduct()).thenReturn(List.of(orderItem));
        List<OrderItem> actualResult = orderService.getBasketAsOrderItems();

        // then
        assertNotNull(actualResult);
        assertEquals(1, actualResult.size());
        assertEquals(orderItem, actualResult.get(0));
        assertEquals(order.getId(), actualResult.get(0).getOrder().getId());
        verify(orderItemRepository, times(1)).findAllWithProduct();
    }

    @Test
    void searchOrdersByUsernameTest() {
        // given
        String username = "username";
        Long userId = 1L;
        AppUser appUser = new AppUser();
        appUser.setId(userId);
        appUser.setUsername(username);
        appUser.setRole(RoleName.USER);

        Order order1 = Order.builder()
                .status(OrderStatus.ACTIVE)
                .deliveryPrice(200.0)
                .orderItems(new HashSet<>())
                .build();
        order1.setId(1L);
        order1.setUser(appUser);

        Order order2 = Order.builder()
                .status(OrderStatus.PLACED)
                .deliveryPrice(300.0)
                .orderItems(new HashSet<>())
                .build();
        order2.setId(2L);
        order2.setUser(appUser);

        List<Order> orders = List.of(order1, order2);

        OrderDto orderDto1 = new OrderDto();
        orderDto1.setId(1L);
        orderDto1.setStatus("ACTIVE");
        orderDto1.setDeliveryPrice(200.0);
        orderDto1.setUserId(userId);

        OrderDto orderDto2 = new OrderDto();
        orderDto2.setId(2L);
        orderDto2.setStatus("PLACED");
        orderDto2.setDeliveryPrice(300.0);
        orderDto2.setUserId(userId);

        // when
        when(orderRepository.findAllWithOrderItems()).thenReturn(orders);
        when(orderConverter.convertModelToDto(order1)).thenReturn(orderDto1);
        when(orderConverter.convertModelToDto(order2)).thenReturn(orderDto2);
        List<OrderDto> actualResult = orderService.searchOrdersByUsername(username);

        // then
        assertNotNull(actualResult);
        assertEquals(2, actualResult.size());
        assertEquals(orderDto1.getId(), actualResult.get(0).getId());
        assertEquals(orderDto1.getStatus(), actualResult.get(0).getStatus());
        assertEquals(orderDto1.getDeliveryPrice(), actualResult.get(0).getDeliveryPrice());
        assertEquals(orderDto1.getUserId(), actualResult.get(0).getUserId());
        assertEquals(orderDto2.getId(), actualResult.get(1).getId());
        assertEquals(orderDto2.getStatus(), actualResult.get(1).getStatus());
        assertEquals(orderDto2.getDeliveryPrice(), actualResult.get(1).getDeliveryPrice());
        assertEquals(orderDto2.getUserId(), actualResult.get(1).getUserId());

        verify(orderRepository, times(1)).findAllWithOrderItems();
        verify(orderConverter, times(1)).convertModelToDto(order1);
        verify(orderConverter, times(1)).convertModelToDto(order2);
    }

    @AfterEach
    public void closeService() throws Exception {
        closeable.close();
    }
}
