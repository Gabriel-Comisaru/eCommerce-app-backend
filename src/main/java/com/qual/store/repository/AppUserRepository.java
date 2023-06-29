package com.qual.store.repository;


import com.qual.store.model.AppUser;


public interface AppUserRepository extends ShopRepository<AppUser, Long> {

    AppUser findUserByUsername(String username);
}
