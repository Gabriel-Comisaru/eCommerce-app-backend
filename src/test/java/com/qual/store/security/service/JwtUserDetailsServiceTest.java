package com.qual.store.security.service;

import com.qual.store.model.AppUser;
import com.qual.store.model.enums.RoleName;
import com.qual.store.repository.AppUserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static com.qual.store.model.enums.RoleName.ADMIN;
import static com.qual.store.model.enums.RoleName.USER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtUserDetailsServiceTest {

    @Mock
    private AppUserRepository userRepository;

    @InjectMocks
    private JwtUserDetailsService jwtUserDetailsService;

    private AutoCloseable closeable;

    @BeforeEach
    public void init() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @Test
    public void loadUserByUsernameTest() {
        // given
        String username = "testuser";
        String password = "testpassword";

        AppUser user = new AppUser();
        user.setUsername(username);
        user.setPassword(password);
        user.setRole(USER);

        // when
        when(userRepository.findUserByUsername(username)).thenReturn(user);
        UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(username);

        // then
        verify(userRepository, times(1)).findUserByUsername(username);
        assertEquals(username, userDetails.getUsername());
        assertEquals(password, userDetails.getPassword());

        List<GrantedAuthority> authorities = new ArrayList<>(userDetails.getAuthorities());
        assertEquals(1, authorities.size());
        assertEquals(USER.name(), authorities.get(0).getAuthority());
    }

    @Test
    public void createUserDetailsTest() {
        String username = "testuser";
        String password = "testpassword";
        RoleName role = ADMIN;

        UserDetails userDetails = jwtUserDetailsService.createUserDetails(username, password, role);

        assertEquals(username, userDetails.getUsername());
        assertEquals(password, userDetails.getPassword());

        List<GrantedAuthority> authorities = new ArrayList<>(userDetails.getAuthorities());
        assertEquals(1, authorities.size());
        assertEquals(role.name(), authorities.get(0).getAuthority());
    }

    @AfterEach
    public void closeService() throws Exception {
        closeable.close();
    }
}