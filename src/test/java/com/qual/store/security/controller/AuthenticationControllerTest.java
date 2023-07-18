package com.qual.store.security.controller;

import com.qual.store.model.AppUser;
import com.qual.store.model.enums.RoleName;
import com.qual.store.repository.AppUserRepository;
import com.qual.store.security.service.JwtUserDetailsService;
import com.qual.store.security.util.JwtTokenUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthenticationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AppUserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUserDetailsService userDetailsService;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @InjectMocks
    private AuthenticationController authenticationController;

    private AutoCloseable closeable;

    @BeforeEach
    public void init() {
        closeable = MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(authenticationController)
                .build();
    }

    @Test
    public void loginUserTest() throws Exception {
        // given
        String username = "testuser";
        String password = "testpassword";
        String token = "testtoken";

        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);

        // when
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtTokenUtil.generateToken(userDetails)).thenReturn(token);

        // then
        mockMvc.perform(post("/auth/login")
                        .param("username", username)
                        .param("password", password))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value(false))
                .andExpect(jsonPath("$.message").value("Logged In"))
                .andExpect(jsonPath("$.token").value(token));

        verify(authenticationManager, times(1)).authenticate(any());
        verify(userDetailsService, times(1)).loadUserByUsername(username);
        verify(jwtTokenUtil, times(1)).generateToken(userDetails);
    }

    @Test
    public void loginUserIsAuthenticatedReturnsFalseTest() throws Exception {
        // given
        String username = "testuser";
        String password = "testpassword";

        Authentication authentication = mock(Authentication.class);

        // when
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);

        // then
        mockMvc.perform(post("/auth/login")
                        .param("username", username)
                        .param("password", password))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value(true))
                .andExpect(jsonPath("$.message").value("Invalid Credentials"));

        verify(authenticationManager, times(1)).authenticate(any());
    }

    @Test
    public void loginUserInvalidCredentialsTest() throws Exception {
        // given
        String username = "testuser";
        String password = "testpassword";

        // when
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Invalid credentials"));

        // then
        mockMvc.perform(post("/auth/login")
                        .param("username", username)
                        .param("password", password))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value(true))
                .andExpect(jsonPath("$.message").value("Invalid Credentials"));

        verify(authenticationManager, times(1)).authenticate(any());
    }

    @Test
    public void loginUserDisabledUserTest() throws Exception {
        // given
        String username = "testuser";
        String password = "testpassword";

        // when
        when(authenticationManager.authenticate(any())).thenThrow(new DisabledException("User is disabled"));

        // then
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .param("username", username)
                        .param("password", password))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value(true))
                .andExpect(jsonPath("$.message").value("User is disabled"));

        verify(authenticationManager, times(1)).authenticate(any());
    }

    @Test
    public void loginUserThrowsExceptionTest() throws Exception {
        String username = "testuser";
        String password = "testpassword";

        when(authenticationManager.authenticate(any())).thenThrow(new RuntimeException("Something went wrong"));

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .param("username", username)
                        .param("password", password))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value(true))
                .andExpect(jsonPath("$.message").value("Something went wrong"));

        verify(authenticationManager, times(1)).authenticate(any());
    }

    @Test
    public void saveUserWithAdminRoleTest() throws Exception {
        String firstName = "John";
        String lastName = "Doe";
        String username = "johndoe";
        String email = "johndoe@example.com";
        String password = "password";
        String token = "testtoken";
        String role = "admin";

        AppUser user = new AppUser();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(RoleName.ADMIN);
        user.setUsername(username);

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetailsService.createUserDetails(username, password, RoleName.ADMIN)).thenReturn(userDetails);
        when(jwtTokenUtil.generateToken(any())).thenReturn(token);

        mockMvc.perform(post("/auth/register")
                        .param("first_name", firstName)
                        .param("last_name", lastName)
                        .param("username", username)
                        .param("email", email)
                        .param("password", password)
                        .param("role", role))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value(false))
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.message").value("Account created successfully"))
                .andExpect(jsonPath("$.token").value(token))
                .andReturn();

        verify(userRepository, times(1)).save(any(AppUser.class));
        verify(userDetailsService, times(1))
                .createUserDetails(eq(username), anyString(), any(RoleName.class));
    }
    @Test
    public void saveUserWithUserRoleTest() throws Exception {
        String firstName = "John";
        String lastName = "Doe";
        String username = "johndoe";
        String email = "johndoe@example.com";
        String password = "password";
        String token = "testtoken";
        String role = "user";

        AppUser user = new AppUser();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(RoleName.ADMIN);
        user.setUsername(username);

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetailsService.createUserDetails(username, password, RoleName.ADMIN)).thenReturn(userDetails);
        when(jwtTokenUtil.generateToken(any())).thenReturn(token);

        mockMvc.perform(post("/auth/register")
                        .param("first_name", firstName)
                        .param("last_name", lastName)
                        .param("username", username)
                        .param("email", email)
                        .param("password", password)
                        .param("role", role))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value(false))
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.message").value("Account created successfully"))
                .andExpect(jsonPath("$.token").value(token))
                .andReturn();

        verify(userRepository, times(1)).save(any(AppUser.class));
        verify(userDetailsService, times(1))
                .createUserDetails(eq(username), anyString(), any(RoleName.class));
    }

    @AfterEach
    public void closeService() throws Exception {
        closeable.close();
    }
}