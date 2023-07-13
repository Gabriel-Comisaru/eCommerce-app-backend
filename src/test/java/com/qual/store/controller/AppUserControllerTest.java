package com.qual.store.controller;

import com.qual.store.converter.AppUserConverter;
import com.qual.store.dto.AppUserDto;
import com.qual.store.model.AppUser;
import com.qual.store.service.AppUserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

class AppUserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AppUserService appUserService;

    @Mock
    private AppUserConverter appUserConverter;

    @InjectMocks
    private AppUserController appUserController;

    private AutoCloseable closeable;

    @BeforeEach
    public void setup() {
        closeable = MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(appUserController)
                .build();
    }


    @Test
    public void getAllUsersTest() throws Exception {
        List<AppUser> userList = new ArrayList<>();
        userList.add(new AppUser());
        userList.add(new AppUser());

        List<AppUserDto> userDtoList = new ArrayList<>();
        userDtoList.add(new AppUserDto());
        userDtoList.add(new AppUserDto());

        when(appUserService.getAllUsers()).thenReturn(userList);
        when(appUserConverter.convertModelToDto(any(AppUser.class))).thenReturn(new AppUserDto());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$[1]").exists());

        verify(appUserService, times(1)).getAllUsers();
        verify(appUserConverter, times(2)).convertModelToDto(any(AppUser.class));
    }


    @Test
    public void deleteUserByUsernameTest() throws Exception {
        String username = "testuser";

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/{username}", username))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("User deleted"));

        verify(appUserService, times(1)).deleteUserByUsername(username);
    }

    @Test
    public void updateUserByUsernameTest() throws Exception {
        String username = "testuser";
        String password = "newpassword";

        mockMvc.perform(MockMvcRequestBuilders.put("/api/users/{username}", username)
                        .param("password", password))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("User updated"));

        verify(appUserService, times(1)).updateUserByUsername(username, password);
    }

    @AfterEach
    public void closeResource() throws Exception {
        closeable.close();
    }
}