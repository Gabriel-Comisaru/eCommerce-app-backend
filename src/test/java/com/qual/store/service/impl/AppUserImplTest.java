package com.qual.store.service.impl;

import com.qual.store.model.AppUser;
import com.qual.store.model.Order;
import com.qual.store.repository.AppUserRepository;
import com.qual.store.repository.OrderRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AppUserImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private AppUserRepository appUserRepository;

    @InjectMocks
    private AppUserImpl appUserImpl;

    private AutoCloseable closeable;

    @BeforeEach
    public void setup() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getAllUsersTest() {
        List<AppUser> users = new ArrayList<>();
        users.add(new AppUser());
        users.add(new AppUser());

        when(appUserRepository.findAllWithOrders()).thenReturn(users);

        List<AppUser> result = appUserImpl.getAllUsers();

        assertEquals(users, result);
        verify(appUserRepository, times(1)).findAllWithOrders();
    }

    @Test
    public void findUserByUsernameTest() {
        String username = "testuser";
        AppUser user = new AppUser();
        user.setUsername(username);

        when(appUserRepository.findUserByUsername(username)).thenReturn(user);

        AppUser result = appUserImpl.findUserByUsername(username);

        assertEquals(user, result);
        verify(appUserRepository, times(1)).findUserByUsername(username);
    }

    @Test
    public void deleteUserByUsernameTest() {
        String username = "testuser";
        AppUser user = new AppUser();
        user.setUsername(username);
        Set<Order> orders = new HashSet<>();
        Order orderOne = Order.builder()
                .user(user)
                .build();
        orders.add(orderOne);
        Order orderTwo = Order.builder()
                .user(user)
                .build();
        orders.add(orderTwo);
        user.setOrders(orders);

        List<Order> orderList = user.getOrders().stream().toList();

        when(appUserRepository.findUserByUsername(username)).thenReturn(user);

        appUserImpl.deleteUserByUsername(username);

        verify(appUserRepository, times(1)).findUserByUsername(username);
        verify(orderRepository, times(1)).saveAll(orderList);
        verify(appUserRepository, times(1)).delete(user);
    }

    @Test
    public void updateUserByUsernameTest() {
        String username = "testuser";
        String password = "newpassword";
        AppUser userToUpdate = new AppUser();
        userToUpdate.setUsername(username);

        when(appUserRepository.findUserByUsername(username)).thenReturn(userToUpdate);
        when(appUserRepository.save(userToUpdate)).thenReturn(userToUpdate);

        AppUser result = appUserImpl.updateUserByUsername(username, password);

        assertEquals(userToUpdate, result);
        verify(appUserRepository, times(1)).findUserByUsername(username);
        verify(appUserRepository, times(1)).save(userToUpdate);
    }

    @AfterEach
    public void closeResource() throws Exception {
        closeable.close();
    }
}