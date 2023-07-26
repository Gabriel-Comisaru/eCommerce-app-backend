package com.qual.store.service.impl;

import com.qual.store.converter.UserAdressConverter;
import com.qual.store.dto.UserAdressDto;
import com.qual.store.logger.Log;
import com.qual.store.model.AppUser;
import com.qual.store.model.UserAdress;
import com.qual.store.repository.AppUserRepository;
import com.qual.store.repository.UserAdressRepository;
import com.qual.store.service.UserAdressService;
import com.qual.store.utils.validators.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserAdressImpl implements UserAdressService {
    private final UserAdressRepository userAdressRepository;
    private final AppUserRepository appUserRepository;
    private final Validator<UserAdress> validator;
    private final UserAdressConverter userAdressConverter;

    @Override
    @Log
    public List<UserAdress> getAllUserAdresses(){
        return userAdressRepository.findAll();
    }

    @Override
    public void saveUserAdress(UserAdressDto userAdressDto) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUsername = authentication.getName();
            AppUser appUser = appUserRepository.findUserByUsername(currentUsername);
            userAdressDto.setUser_id(appUser.getId());
            // Convert UserAdressDto to UserAdress entity
            UserAdress userAdress = userAdressConverter.convertDtoToModel(userAdressDto);
            validator.validate(userAdress);

            // Save the UserAdress to the database
            userAdressRepository.save(userAdress);
        } catch (Exception e) {
            // Handle any exception that might occur during the process.
            throw new RuntimeException("Error saving user address: " + e.getMessage(), e);
        }

    }

    @Override
    public void deleteUserAdress(Long id) {
        userAdressRepository.deleteById(id);
    }

    @Override
    public UserAdress getUserAdressById(Long id) {
        return userAdressRepository.findById(id).orElse(null);
    }


    @Override
    public void updateUserAdress(Long id, UserAdressDto updatedUserAdressDto) {
        try {
            // Check if the user address with the given ID exists
            UserAdress existingUserAdress = userAdressRepository.findById(id).orElse(null);
            if (existingUserAdress == null) {
                throw new NotFoundException("User address not found.");
            }
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUsername = authentication.getName();
            AppUser appUser = appUserRepository.findUserByUsername(currentUsername);
            updatedUserAdressDto.setUser_id(appUser.getId());

            // Convert UserAdressDto to UserAdress entity
            UserAdress updatedUserAdress = userAdressConverter.convertDtoToModel(updatedUserAdressDto);


            existingUserAdress.setAdress(updatedUserAdress.getAdress());
            existingUserAdress.setCity(updatedUserAdress.getCity());
            existingUserAdress.setCounty(updatedUserAdress.getCounty());

            existingUserAdress.setFirst_name(updatedUserAdress.getFirst_name());
            existingUserAdress.setLast_name(updatedUserAdress.getLast_name());
            existingUserAdress.setPhone_number(updatedUserAdress.getPhone_number());

            // Save the updated UserAdress to the database
            userAdressRepository.save(existingUserAdress);
        } catch (NotFoundException  e) {
            throw e; // Re-throw known exceptions to be handled at the controller level
        } catch (Exception e) {
            throw new RuntimeException("Error updating user address: " + e.getMessage(), e);
        }
    }

}
