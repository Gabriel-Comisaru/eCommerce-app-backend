package com.qual.store.service;

import com.qual.store.dto.UserAddressDto;
import com.qual.store.model.UserAddress;

import java.util.List;

public interface UserAddressService {
    List<UserAddress> getAllUserAddresses();

    void saveUserAddress(UserAddressDto userAddress);

    void deleteUserAddress(Long id);

    UserAddress getUserAddressById(Long id);

    void updateUserAddress(Long id, UserAddressDto updatedUserAddressDto);

    List<UserAddress> getUserAddressByUserId(Long id);


    // UserAddress addUserAddress(UserAddressDto userAddressDto);
}
