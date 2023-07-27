package com.qual.store.controller;

import com.qual.store.converter.UserAddressConverter;
import com.qual.store.dto.UserAddressDto;
import com.qual.store.model.UserAddress;
import com.qual.store.service.UserAddressService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserAddressControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserAddressService userAddressService;

    @Mock
    private UserAddressConverter userAddressConverter;

    @InjectMocks
    private UserAddressController userAddressController;

    private AutoCloseable closeable;

    @BeforeEach
    void setup() {
        closeable = MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userAddressController).build();
    }

    @Test
    void getAllUserAddressesTest() throws Exception {
        // given
        UserAddress userAddress = new UserAddress();
        userAddress.setId(1L);
        userAddress.setFirst_name("John");
        userAddress.setLast_name("Doe");

        UserAddressDto userAdressDto = new UserAddressDto();
        userAdressDto.setId(1L);
        userAdressDto.setFirst_name("John");
        userAdressDto.setLast_name("Doe");

        List<UserAddress> userAddressList = new ArrayList<>();
        userAddressList.add(userAddress);

        // when
        when(userAddressService.getAllUserAddresses()).thenReturn(userAddressList);
        when(userAddressConverter.convertModelToDto(userAddress)).thenReturn(userAdressDto);

        // then
        mockMvc.perform(get("/api/adresses")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(userAdressDto.getId()))
                .andExpect(jsonPath("$[0].first_name").value(userAdressDto.getFirst_name()))
                .andExpect(jsonPath("$[0].last_name").value(userAdressDto.getLast_name()))
                .andExpect(jsonPath("$.length()").value(userAddressList.size()));

        verify(userAddressService, times(1)).getAllUserAddresses();
        verify(userAddressConverter, times(1)).convertModelToDto(userAddress);
    }

    @Test
    void addUserAddressTest() throws Exception {
        // given
        UserAddressDto userAdressDto = new UserAddressDto();
        userAdressDto.setFirst_name("John");
        userAdressDto.setLast_name("Doe");

        // when
        doNothing().when(userAddressService).saveUserAddress(any(UserAddressDto.class));

        // then
        mockMvc.perform(post("/api/adresses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userAdressDto)))
                .andExpect(status().isCreated())
                .andExpect(content().string("User address added successfully."));

        verify(userAddressService, times(1)).saveUserAddress(any(UserAddressDto.class));
    }

    @Test
    void deleteUserAddressTest() throws Exception {
        // given
        Long userId = 1L;

        UserAddress userAddress = new UserAddress();
        userAddress.setId(userId);
        userAddress.setFirst_name("John");
        userAddress.setLast_name("Doe");

        // when
        when(userAddressService.getUserAddressById(userId)).thenReturn(userAddress);
        doNothing().when(userAddressService).deleteUserAddress(userId);

        // then
        mockMvc.perform(delete("/api/adresses/{id}", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("User address deleted successfully."));

        verify(userAddressService, times(1)).getUserAddressById(userId);
        verify(userAddressService, times(1)).deleteUserAddress(userId);
    }

    @Test
    void updateUserAddressTest() throws Exception {
        // given
        Long userId = 1L;

        UserAddressDto updatedUserAdressDto = new UserAddressDto();
        updatedUserAdressDto.setId(userId);
        updatedUserAdressDto.setFirst_name("Updated John");
        updatedUserAdressDto.setLast_name("Updated Doe");

        // when
        doNothing().when(userAddressService).updateUserAddress(userId, updatedUserAdressDto);

        // then
        mockMvc.perform(post("/api/adresses/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(updatedUserAdressDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("User address updated successfully."));

        verify(userAddressService, times(1)).updateUserAddress(userId, updatedUserAdressDto);
    }

    @Test
    void getUserAdressByIdTest() throws Exception {
        // given
        Long userId = 1L;

        UserAddress userAddress = new UserAddress();
        userAddress.setId(userId);
        userAddress.setFirst_name("John");
        userAddress.setLast_name("Doe");

        UserAddressDto userAdressDto = new UserAddressDto();
        userAdressDto.setId(userId);
        userAdressDto.setFirst_name("John");
        userAdressDto.setLast_name("Doe");

        // when
        when(userAddressService.getUserAddressById(userId)).thenReturn(userAddress);
        when(userAddressConverter.convertModelToDto(userAddress)).thenReturn(userAdressDto);

        // then
        mockMvc.perform(get("/api/adresses/{id}", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userAdressDto.getId()))
                .andExpect(jsonPath("$.first_name").value(userAdressDto.getFirst_name()))
                .andExpect(jsonPath("$.last_name").value(userAdressDto.getLast_name()));

        verify(userAddressService, times(1)).getUserAddressById(userId);
        verify(userAddressConverter, times(1)).convertModelToDto(userAddress);
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
