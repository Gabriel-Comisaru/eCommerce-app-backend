package com.qual.store.service.impl;

import com.qual.store.converter.UserAddressConverter;
import com.qual.store.dto.UserAddressDto;
import com.qual.store.logger.Log;
import com.qual.store.model.AppUser;
import com.qual.store.model.UserAddress;
import com.qual.store.repository.AppUserRepository;
import com.qual.store.repository.UserAddressRepository;
import com.qual.store.service.UserAddressService;
import com.qual.store.utils.validators.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserAddressImpl implements UserAddressService {
    private final UserAddressRepository userAddressRepository;
    private final AppUserRepository appUserRepository;
    private final Validator<UserAddress> validator;
    private final UserAddressConverter userAddressConverter;

    @Override
    @Log
    public List<UserAddress> getAllUserAddresses(){
        return userAddressRepository.findAll();
    }

    @Override
    public void saveUserAddress(UserAddressDto userAddressDto) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUsername = authentication.getName();
            AppUser appUser = appUserRepository.findUserByUsername(currentUsername);
            userAddressDto.setUser_id(appUser.getId());
            // Convert UserAddressDto to UserAddress entity
            UserAddress userAddress = userAddressConverter.convertDtoToModel(userAddressDto);
            validator.validate(userAddress);

            // Save the UserAddress to the database
            userAddressRepository.save(userAddress);
        } catch (Exception e) {
            // Handle any exception that might occur during the process.
            throw new RuntimeException("Error saving user address: " + e.getMessage(), e);
        }

    }

    @Override
    public void deleteUserAddress(Long id) {
        userAddressRepository.deleteById(id);
    }

    @Override
    public UserAddress getUserAddressById(Long id) {
        return userAddressRepository.findById(id).orElse(null);
    }

    @Override
    public List<UserAddress> getUserAddressByUserId(Long id) {
        return userAddressRepository.findByUserId(id);
    }


    @Override
    public void updateUserAddress(Long id, UserAddressDto updatedUserAddressDto) {
        try {
            // Check if the user address with the given ID exists
            UserAddress existingUserAddress = userAddressRepository.findById(id).orElse(null);
            if (existingUserAddress == null) {
                throw new NotFoundException("User address not found.");
            }
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUsername = authentication.getName();
            AppUser appUser = appUserRepository.findUserByUsername(currentUsername);
            updatedUserAddressDto.setUser_id(appUser.getId());

            // Convert UserAddressDto to UserAddress entity
            UserAddress updatedUserAddress = userAddressConverter.convertDtoToModel(updatedUserAddressDto);


            existingUserAddress.setAddress(updatedUserAddress.getAddress());
            existingUserAddress.setCity(updatedUserAddress.getCity());
            existingUserAddress.setCounty(updatedUserAddress.getCounty());

            existingUserAddress.setFirst_name(updatedUserAddress.getFirst_name());
            existingUserAddress.setLast_name(updatedUserAddress.getLast_name());
            existingUserAddress.setPhone_number(updatedUserAddress.getPhone_number());

            // Save the updated UserAddress to the database
            userAddressRepository.save(existingUserAddress);
        } catch (NotFoundException  e) {
            throw e; // Re-throw known exceptions to be handled at the controller level
        } catch (Exception e) {
            throw new RuntimeException("Error updating user address: " + e.getMessage(), e);
        }
    }

}
