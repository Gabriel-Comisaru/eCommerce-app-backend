package com.qual.store.controller;

import com.qual.store.converter.AppUserConverter;
import com.qual.store.dto.AppUserDto;
import com.qual.store.logger.Log;
import com.qual.store.model.AppUser;
import com.qual.store.service.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/users")
@CrossOrigin("*")
public class AppUserController {

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private AppUserConverter appUserConverter;

    @GetMapping("/loggedInUser")
    public Map<String, Object> getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        System.out.println("********** authorities: " + userDetails.getAuthorities());

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("username", authentication.getName());
        userMap.put("error", false);
        return userMap;
    }

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
