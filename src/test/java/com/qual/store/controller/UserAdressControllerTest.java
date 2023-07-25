package com.qual.store.controller;

import com.qual.store.converter.UserAdressConverter;
import com.qual.store.dto.UserAdressDto;
import com.qual.store.model.UserAdress;
import com.qual.store.service.UserAdressService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserAdressControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserAdressService userAdressService;

    @Mock
    private UserAdressConverter userAdressConverter;

    @InjectMocks
    private UserAdressController userAdressController;

    private AutoCloseable closeable;

    @BeforeEach
    void setup() {
        closeable = MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userAdressController).build();
    }

    @Test
    void getAllUserAdressesTest() throws Exception {
        // given
        UserAdress userAdress = new UserAdress();
        userAdress.setId(1L);
        userAdress.setFirst_name("John");
        userAdress.setLast_name("Doe");

        UserAdressDto userAdressDto = new UserAdressDto();
        userAdressDto.setId(1L);
        userAdressDto.setFirst_name("John");
        userAdressDto.setLast_name("Doe");

        List<UserAdress> userAdressList = new ArrayList<>();
        userAdressList.add(userAdress);

        // when
        when(userAdressService.getAllUserAdresses()).thenReturn(userAdressList);
        when(userAdressConverter.convertModelToDto(userAdress)).thenReturn(userAdressDto);

        // then
        mockMvc.perform(get("/api/adresses")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(userAdressDto.getId()))
                .andExpect(jsonPath("$[0].first_name").value(userAdressDto.getFirst_name()))
                .andExpect(jsonPath("$[0].last_name").value(userAdressDto.getLast_name()))
                .andExpect(jsonPath("$.length()").value(userAdressList.size()));

        verify(userAdressService, times(1)).getAllUserAdresses();
        verify(userAdressConverter, times(1)).convertModelToDto(userAdress);
    }

    @Test
    void addUserAdressTest() throws Exception {
        // given
        UserAdressDto userAdressDto = new UserAdressDto();
        userAdressDto.setFirst_name("John");
        userAdressDto.setLast_name("Doe");

        // when
        doNothing().when(userAdressService).saveUserAdress(any(UserAdressDto.class));

        // then
        mockMvc.perform(post("/api/adresses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userAdressDto)))
                .andExpect(status().isCreated())
                .andExpect(content().string("User address added successfully."));

        verify(userAdressService, times(1)).saveUserAdress(any(UserAdressDto.class));
    }

    @Test
    void deleteUserAdressTest() throws Exception {
        // given
        Long userId = 1L;

        UserAdress userAdress = new UserAdress();
        userAdress.setId(userId);
        userAdress.setFirst_name("John");
        userAdress.setLast_name("Doe");

        // when
        when(userAdressService.getUserAdressById(userId)).thenReturn(userAdress);
        doNothing().when(userAdressService).deleteUserAdress(userId);

        // then
        mockMvc.perform(delete("/api/adresses/{id}", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("User address deleted successfully."));

        verify(userAdressService, times(1)).getUserAdressById(userId);
        verify(userAdressService, times(1)).deleteUserAdress(userId);
    }

    @Test
    void updateUserAdressTest() throws Exception {
        // given
        Long userId = 1L;

        UserAdressDto updatedUserAdressDto = new UserAdressDto();
        updatedUserAdressDto.setId(userId);
        updatedUserAdressDto.setFirst_name("Updated John");
        updatedUserAdressDto.setLast_name("Updated Doe");

        // when
        doNothing().when(userAdressService).updateUserAdress(userId, updatedUserAdressDto);

        // then
        mockMvc.perform(post("/api/adresses/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(updatedUserAdressDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("User address updated successfully."));

        verify(userAdressService, times(1)).updateUserAdress(userId, updatedUserAdressDto);
    }

    @Test
    void getUserAdressByIdTest() throws Exception {
        // given
        Long userId = 1L;

        UserAdress userAdress = new UserAdress();
        userAdress.setId(userId);
        userAdress.setFirst_name("John");
        userAdress.setLast_name("Doe");

        UserAdressDto userAdressDto = new UserAdressDto();
        userAdressDto.setId(userId);
        userAdressDto.setFirst_name("John");
        userAdressDto.setLast_name("Doe");

        // when
        when(userAdressService.getUserAdressById(userId)).thenReturn(userAdress);
        when(userAdressConverter.convertModelToDto(userAdress)).thenReturn(userAdressDto);

        // then
        mockMvc.perform(get("/api/adresses/{id}", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userAdressDto.getId()))
                .andExpect(jsonPath("$.first_name").value(userAdressDto.getFirst_name()))
                .andExpect(jsonPath("$.last_name").value(userAdressDto.getLast_name()));

        verify(userAdressService, times(1)).getUserAdressById(userId);
        verify(userAdressConverter, times(1)).convertModelToDto(userAdress);
    }

    // Helper method to convert object to JSON string
    private String asJsonString(Object obj) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
