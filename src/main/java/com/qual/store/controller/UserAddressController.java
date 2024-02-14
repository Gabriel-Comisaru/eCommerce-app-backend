package com.qual.store.controller;

import com.github.javafaker.Faker;
import com.qual.store.converter.UserAddressConverter;
import com.qual.store.dto.UserAddressDto;
import com.qual.store.logger.Log;
import com.qual.store.model.AppUser;
import com.qual.store.model.UserAddress;
import com.qual.store.repository.AppUserRepository;
import com.qual.store.service.UserAddressService;
import com.qual.store.utils.validators.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.webjars.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/addresses")
@RequiredArgsConstructor
@CrossOrigin("*")
public class UserAddressController {
    private final Validator<UserAddress> validator;

    private final UserAddressService userAddressService;
    private final UserAddressConverter userAddressConverter;
    private final AppUserRepository appUserRepository;

    @GetMapping()
    @Log
    public List<UserAddressDto> getAllUserAddresses()
    {
        return userAddressService.getAllUserAddresses().
                stream().map(userAddressConverter::convertModelToDto)
                .collect(Collectors.toList());
    }


    @PutMapping("/{id}")
    @Log
    public ResponseEntity<?> updateUserAddress(@PathVariable Long id, @RequestBody UserAddressDto updatedUserAddressDto) {
        try {
            userAddressService.updateUserAddress(id, updatedUserAddressDto);
            return ResponseEntity.status(HttpStatus.OK).body("User address updated successfully.");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Log
    public ResponseEntity<?> deleteUserAddress(@PathVariable Long id) {
        try {
            // Check if the user address with the given ID exists
            UserAddress userAddress = userAddressService.getUserAddressById(id);
            if (userAddress == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User address not found.");
            }
            // Delete the user address
            userAddressService.deleteUserAddress(id);

            return ResponseEntity.status(HttpStatus.OK).body("User address deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping()
    @Log
    public ResponseEntity<?> addUserAddress(@RequestBody UserAddressDto userAddressDto) {

        try {
            userAddressService.saveUserAddress(userAddressDto);
            return ResponseEntity.status(HttpStatus.CREATED).body("User address added successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    @GetMapping("/{id}")
    @Log
    public ResponseEntity<?> getUserAddressById(@PathVariable Long id) {
        try {
            // Find the user address with the given ID
            UserAddress userAddress = userAddressService.getUserAddressById(id);

            if (userAddress == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User address not found.");
            }

            // Convert UserAddress entity to UserAddressDto and return it in the response body
            UserAddressDto userAddressDto = userAddressConverter.convertModelToDto(userAddress);
            return ResponseEntity.status(HttpStatus.OK).body(userAddressDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    //get all user addresses by user id
    @GetMapping("/user/{id}")
    @Log
    public ResponseEntity<?> getUserAddressesByUserId(@PathVariable Long id) {
        try {
            // Find the user address with the given ID
            List<UserAddress> userAddresses = userAddressService.getUserAddressByUserId(id);

            if (userAddresses == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User address not found.");
            }

            // Convert UserAddress entity to UserAddressDto and return it in the response body
            List<UserAddressDto> userAddressDtos = userAddresses.stream().map(userAddressConverter::convertModelToDto)
                    .collect(Collectors.toList());
            return ResponseEntity.status(HttpStatus.OK).body(userAddressDtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/populate")
    @Log
    public ResponseEntity<?> populateDatabase() {
        try {
            Faker faker = new Faker();

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUsername = authentication.getName();
            AppUser appUser = appUserRepository.findUserByUsername(currentUsername);

            for (int i = 0; i < 20; i++) {
                String firstName = faker.name().firstName();
                String lastName = faker.name().lastName();
                String phoneNumber = faker.numerify("###########");
                String address = faker.address().streetAddress();
                String city = faker.address().city();
                String county = faker.address().state();

                UserAddressDto userAddressDto = new UserAddressDto();
                userAddressDto.setFirst_name(firstName);
                userAddressDto.setLast_name(lastName);
                userAddressDto.setPhone_number(phoneNumber);
                userAddressDto.setAddress(address);
                userAddressDto.setCity(city);
                userAddressDto.setCounty(county);

                userAddressDto.setUser_id(appUser.getId());

                userAddressService.saveUserAddress(userAddressDto);
            }

            return ResponseEntity.status(HttpStatus.CREATED).body("Database populated with fake data");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


}
