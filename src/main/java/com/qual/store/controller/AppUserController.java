package com.qual.store.controller;

import com.qual.store.converter.AppUserConverter;
import com.qual.store.dto.AppUserDto;
import com.qual.store.logger.Log;
import com.qual.store.service.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/users")
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
}
