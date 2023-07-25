package com.qual.store.service;

import com.qual.store.dto.UserAdressDto;
import com.qual.store.logger.Log;
import com.qual.store.model.UserAdress;
import jakarta.transaction.Transactional;

import java.util.List;

public interface UserAdressService {
    List<UserAdress> getAllUserAdresses();

    void saveUserAdress(UserAdressDto userAdress);

    void deleteUserAdress(Long id);

    UserAdress getUserAdressById(Long id);

    void updateUserAdress(Long id, UserAdressDto updatedUserAdressDto);


    // UserAdress addUserAdress(UserAdressDto userAdressDto);
}
