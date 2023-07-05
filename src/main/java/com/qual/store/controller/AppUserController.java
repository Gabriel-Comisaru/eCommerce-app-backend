package com.qual.store.controller;

import com.qual.store.converter.AppUserConverter;
import com.qual.store.dto.AppUserDto;
import com.qual.store.logger.Log;
import com.qual.store.model.AppUser;
import com.qual.store.service.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/users")
@CrossOrigin("*")
public class AppUserController {

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private AppUserConverter appUserConverter;

    @GetMapping
    @Log
    public List<AppUserDto> getAllUsers() {
        return appUserService.getAllUsers().stream()
                .map(user -> appUserConverter.convertModelToDto(user))
                .collect(Collectors.toList());
    }


    @DeleteMapping(value = "/{username}")
    @Log
    public ResponseEntity<?> deleteUserByUsername(@PathVariable("username") String username) {
        appUserService.deleteUserByUsername(username);
        return ResponseEntity.status(HttpStatus.OK)
                .body("User deleted");
    }

    @PutMapping("/{username}")
    @Log
    public ResponseEntity<?> updateUserByUsername(@PathVariable("username") String username,
                                                   @RequestParam String password) {
        appUserService.updateUserByUsername(username, password);
        return ResponseEntity.ok("User updated");
    }
}
