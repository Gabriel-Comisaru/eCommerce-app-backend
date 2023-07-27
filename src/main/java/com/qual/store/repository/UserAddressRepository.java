package com.qual.store.repository;

import com.qual.store.model.UserAddress;

import java.util.List;

public interface UserAddressRepository extends ShopRepository<UserAddress,Long> {
    //find address by user id
    List<UserAddress> findByUserId(Long userId);
}
