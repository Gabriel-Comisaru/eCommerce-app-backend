package com.qual.store.service.impl;

import com.qual.store.logger.Log;
import com.qual.store.model.AppUser;
import com.qual.store.model.Order;
import com.qual.store.repository.AppUserRepository;
import com.qual.store.repository.OrderRepository;
import com.qual.store.service.AppUserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppUserImpl implements AppUserService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    @Override
    @Log
    public List<AppUser> getAllUsers() {
        List<AppUser> users = appUserRepository.findAllWithOrders();
        return users;
    }

    @Override
    @Log
    public AppUser findUserByUsername(String username) {
       return appUserRepository.findUserByUsername(username);
    }

    @Override
    @Log
    @Transactional
    public void deleteUserByUsername(String username) {
        AppUser user = appUserRepository.findUserByUsername(username);

        List<Order> orders = user.getOrders().stream().toList();
        user.setOrders(null);
        orders.forEach(order ->  order.setUser(null));

        orderRepository.saveAll(orders);

        appUserRepository.delete(user);
    }

    @Override
    @Transactional
    @Log
    public AppUser updateUserByUsername(String username, AppUser user) {
        AppUser userToUpdate = appUserRepository.findUserByUsername(username);
        userToUpdate.setFirstName(user.getFirstName());
        userToUpdate.setLastName(user.getLastName());
        userToUpdate.setEmail(user.getEmail());
        return appUserRepository.save(userToUpdate);
    }
}
