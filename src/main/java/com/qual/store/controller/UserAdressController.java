package com.qual.store.controller;

import com.github.javafaker.Faker;
import com.qual.store.converter.UserAdressConverter;
import com.qual.store.dto.UserAdressDto;
import com.qual.store.logger.Log;
import com.qual.store.model.AppUser;
import com.qual.store.model.Category;
import com.qual.store.model.Product;
import com.qual.store.model.UserAdress;
import com.qual.store.repository.AppUserRepository;
import com.qual.store.service.UserAdressService;
import com.qual.store.utils.validators.UserAdressValidator;
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
@RequestMapping(value = "/api/adresses")
@RequiredArgsConstructor
@CrossOrigin("*")
public class UserAdressController {
    private final Validator<UserAdress> validator;

    private final UserAdressService userAdressService;
    private final UserAdressConverter userAdressConverter;
    private final AppUserRepository appUserRepository;

    @GetMapping()
    @Log
    public List<UserAdressDto> getAllUserAdresses()
    {
        return userAdressService.getAllUserAdresses().
                stream().map(userAdressConverter::convertModelToDto)
                .collect(Collectors.toList());
    }


    @PutMapping("/{id}")
    @Log
    public ResponseEntity<?> updateUserAdress(@PathVariable Long id, @RequestBody UserAdressDto updatedUserAdressDto) {
        try {
            userAdressService.updateUserAdress(id, updatedUserAdressDto);
            return ResponseEntity.status(HttpStatus.OK).body("User address updated successfully.");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Log
    public ResponseEntity<?> deleteUserAdress(@PathVariable Long id) {
        try {
            // Check if the user address with the given ID exists
            UserAdress userAdress = userAdressService.getUserAdressById(id);
            if (userAdress == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User address not found.");
            }
            // Delete the user address
            userAdressService.deleteUserAdress(id);

            return ResponseEntity.status(HttpStatus.OK).body("User address deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping()
    @Log
    public ResponseEntity<?> addUserAdress(@RequestBody UserAdressDto userAdressDto) {

        try {
            userAdressService.saveUserAdress(userAdressDto);
            return ResponseEntity.status(HttpStatus.CREATED).body("User address added successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    @GetMapping("/{id}")
    @Log
    public ResponseEntity<?> getUserAdressById(@PathVariable Long id) {
        try {
            // Find the user address with the given ID
            UserAdress userAdress = userAdressService.getUserAdressById(id);

            if (userAdress == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User address not found.");
            }

            // Convert UserAdress entity to UserAdressDto and return it in the response body
            UserAdressDto userAdressDto = userAdressConverter.convertModelToDto(userAdress);
            return ResponseEntity.status(HttpStatus.OK).body(userAdressDto);
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

                UserAdressDto userAddressDto = new UserAdressDto();
                userAddressDto.setFirst_name(firstName);
                userAddressDto.setLast_name(lastName);
                userAddressDto.setPhone_number(phoneNumber);
                userAddressDto.setAdress(address);
                userAddressDto.setCity(city);
                userAddressDto.setCounty(county);

                userAddressDto.setUser_id(appUser.getId());

                userAdressService.saveUserAdress(userAddressDto);
            }

            return ResponseEntity.status(HttpStatus.CREATED).body("Database populated with fake data");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


}
