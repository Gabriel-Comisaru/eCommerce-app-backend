package com.qual.store.controller;

import com.qual.store.converter.UserAddressConverter;
import com.qual.store.dto.UserAddressDto;
import com.qual.store.model.UserAddress;
import com.qual.store.service.UserAddressService;
import org.junit.jupiter.api.AfterEach;
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

        UserAddressDto userAddressDto = new UserAddressDto();
        userAddressDto.setId(1L);
        userAddressDto.setFirst_name("John");
        userAddressDto.setLast_name("Doe");

        List<UserAddress> userAddressList = new ArrayList<>();
        userAddressList.add(userAddress);

        // when
        when(userAddressService.getAllUserAddresses()).thenReturn(userAddressList);
        when(userAddressConverter.convertModelToDto(userAddress)).thenReturn(userAddressDto);

        // then
        mockMvc.perform(get("/api/addresses")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(userAddressDto.getId()))
                .andExpect(jsonPath("$[0].first_name").value(userAddressDto.getFirst_name()))
                .andExpect(jsonPath("$[0].last_name").value(userAddressDto.getLast_name()))
                .andExpect(jsonPath("$.length()").value(userAddressList.size()));

        verify(userAddressService, times(1)).getAllUserAddresses();
        verify(userAddressConverter, times(1)).convertModelToDto(userAddress);
    }

    @Test
    void addUserAddressTest() throws Exception {
        // given
        UserAddressDto userAddressDto = new UserAddressDto();
        userAddressDto.setFirst_name("John");
        userAddressDto.setLast_name("Doe");

        // when
        doNothing().when(userAddressService).saveUserAddress(any(UserAddressDto.class));

        // then
        mockMvc.perform(post("/api/addresses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userAddressDto)))
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
        mockMvc.perform(delete("/api/addresses/{id}", userId)
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

        UserAddressDto updatedUserAddressDto = new UserAddressDto();
        updatedUserAddressDto.setId(userId);
        updatedUserAddressDto.setFirst_name("Updated John");
        updatedUserAddressDto.setLast_name("Updated Doe");

        // when
        doNothing().when(userAddressService).updateUserAddress(userId, updatedUserAddressDto);

        // then
        mockMvc.perform(put("/api/addresses/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(updatedUserAddressDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("User address updated successfully."));

        verify(userAddressService, times(1))
                .updateUserAddress(eq(userId), any(UserAddressDto.class));
    }

    @Test
    void getUserAddressByIdTest() throws Exception {
        // given
        Long userId = 1L;

        UserAddress userAddress = new UserAddress();
        userAddress.setId(userId);
        userAddress.setFirst_name("John");
        userAddress.setLast_name("Doe");

        UserAddressDto userAddressDto = new UserAddressDto();
        userAddressDto.setId(userId);
        userAddressDto.setFirst_name("John");
        userAddressDto.setLast_name("Doe");

        // when
        when(userAddressService.getUserAddressById(userId)).thenReturn(userAddress);
        when(userAddressConverter.convertModelToDto(userAddress)).thenReturn(userAddressDto);

        // then
        mockMvc.perform(get("/api/addresses/{id}", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userAddressDto.getId()))
                .andExpect(jsonPath("$.first_name").value(userAddressDto.getFirst_name()))
                .andExpect(jsonPath("$.last_name").value(userAddressDto.getLast_name()));

        verify(userAddressService, times(1)).getUserAddressById(userId);
        verify(userAddressConverter, times(1)).convertModelToDto(userAddress);
    }

    @AfterEach
    public void closeService() throws Exception {
        closeable.close();
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
