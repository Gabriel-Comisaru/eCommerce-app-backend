package com.qual.store.service.impl;

import com.qual.store.converter.AppUserConverter;
import com.qual.store.dto.AppUserDto;
import com.qual.store.exceptions.AppUserNotFoundException;
import com.qual.store.logger.Log;
import com.qual.store.model.AppUser;
import com.qual.store.model.Order;
import com.qual.store.repository.AppUserRepository;
import com.qual.store.repository.OrderRepository;
import com.qual.store.service.AppUserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppUserImpl implements AppUserService {
    private final OrderRepository orderRepository;
    private final AppUserRepository appUserRepository;
    private final AppUserConverter appUserConverter;

    @Override
    @Log
    public List<AppUser> getAllUsers() {
        return appUserRepository.findAllWithOrders();
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
        orders.forEach(order -> order.setUser(null));

        orderRepository.saveAll(orders);

        appUserRepository.delete(user);
    }

    @Override
    @Transactional
    @Log
    public AppUser updateUserByUsername(String username, String password, String email) {
        AppUser userToUpdate = appUserRepository.findUserByUsername(username);
        userToUpdate.setPassword(new BCryptPasswordEncoder().encode(password));
        userToUpdate.setEmail(email);
        return appUserRepository.save(userToUpdate);
    }

    @Override
    @Log
    public AppUserDto getUserByLoggedInUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return appUserConverter.convertModelToDto(
                appUserRepository.findUserByUsername(authentication.getName())
        );
    }

    @Override
    @Log
    public AppUserDto getUserById(Long userId) {
        return appUserConverter.convertModelToDto(
                appUserRepository.findById(userId)
                        .orElseThrow(() -> new AppUserNotFoundException("no user with id " + userId))
        );
    }
}
