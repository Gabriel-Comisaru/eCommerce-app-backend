package com.qual.store.service;

import com.qual.store.dto.AppUserDto;
import com.qual.store.model.AppUser;

import java.util.List;

public interface AppUserService {
    List<AppUser> getAllUsers();

    AppUser findUserByUsername(String username);

    void deleteUserByUsername(String username);

    AppUser updateUserByUsername(String username, String password);

    AppUserDto getUserByLoggedInUsername();
}
