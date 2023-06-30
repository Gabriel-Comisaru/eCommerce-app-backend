package com.qual.store.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qual.store.model.enums.RoleName;
import com.qual.store.security.util.JwtRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {
    @Autowired
    private final UserDetailsService jwtUserDetailsService;
    private final JwtRequestFilter jwtRequestFilter;

    public WebSecurityConfig(UserDetailsService jwtUserDetailsService, JwtRequestFilter jwtRequestFilter) {
        this.jwtUserDetailsService = jwtUserDetailsService;
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(jwtUserDetailsService).passwordEncoder(new BCryptPasswordEncoder());
    }

    @Bean
    public AuthenticationManager authenticationManagerBean(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults(""); // Remove the ROLE_ prefix
    }

    // without security
    @Bean
    public SecurityFilterChain filterChainWithoutSecurity(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

        return httpSecurity.build();
    }

    // with security
//    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/auth/*").permitAll();
                    auth.requestMatchers(
                            "/api/v1/auth/**",
                            "/v2/api-docs",
                            "/v3/api-docs",
                            "/v3/api-docs/**",
                            "/swagger-resources",
                            "/swagger-resources/**",
                            "/configuration/ui",
                            "/configuration/security",
                            "/swagger-ui/**",
                            "/webjars/**",
                            "/swagger-ui.html"
                    ).permitAll();

                    //only admin can create, update, delete categories
                    auth.requestMatchers(HttpMethod.POST, "/api/categories/**").hasAuthority(RoleName.ADMIN.name());
                    auth.requestMatchers(HttpMethod.PUT, "/api/categories/**").hasAuthority(RoleName.ADMIN.name());
                    auth.requestMatchers(HttpMethod.DELETE, "/api/categories/**").hasAuthority(RoleName.ADMIN.name());

                    //only admin can create, update, delete products
                    auth.requestMatchers(HttpMethod.POST, "/api/products/**").hasAuthority(RoleName.ADMIN.name());
                    auth.requestMatchers(HttpMethod.PUT, "/api/products/**").hasAuthority(RoleName.ADMIN.name());
                    auth.requestMatchers(HttpMethod.DELETE, "/api/products/**").hasAuthority(RoleName.ADMIN.name());

                    auth.requestMatchers(HttpMethod.GET, "/api/products/**").permitAll();
                    auth.requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll();
                    auth.requestMatchers(HttpMethod.GET, "/api/order-items/**").permitAll();
                    auth.requestMatchers(HttpMethod.GET, "/api/orders/**").permitAll();

                    //order item can be created, deleted, updated by any authenticated user
                    auth.requestMatchers(HttpMethod.POST, "/api/order-items/**").authenticated();
                    auth.requestMatchers(HttpMethod.PUT, "/api/order-items/**").authenticated();
                    auth.requestMatchers(HttpMethod.DELETE, "/api/order-items/**").authenticated();

                    //order can be created by user and updated, deleted by admin
                    auth.requestMatchers(HttpMethod.POST, "/api/orders/**").authenticated();
                    auth.requestMatchers(HttpMethod.PUT, "/api/orders/**").hasAuthority(RoleName.ADMIN.name());
                    auth.requestMatchers(HttpMethod.DELETE, "/api/orders/**").hasAuthority(RoleName.ADMIN.name());

                    //user can be created, updated, deleted, viewed by admin
                    auth.requestMatchers(HttpMethod.POST, "/api/users/**").hasAuthority(RoleName.ADMIN.name());
                    auth.requestMatchers(HttpMethod.PUT, "/api/users/**").hasAuthority(RoleName.ADMIN.name());
                    auth.requestMatchers(HttpMethod.DELETE, "/api/users/**").hasAuthority(RoleName.ADMIN.name());
                    auth.requestMatchers(HttpMethod.GET, "/api/users/**").hasAuthority(RoleName.ADMIN.name());

                    auth.anyRequest().authenticated();
                })
                .exceptionHandling(handler -> {
                    handler.authenticationEntryPoint((request, response, authException) -> {
                        Map<String, Object> responseMap = new HashMap<>();
                        ObjectMapper mapper = new ObjectMapper();
                        response.setStatus(401);
                        responseMap.put("error", true);
                        responseMap.put("message", "Unauthorized");
                        response.setHeader("content-type", "application/json");
                        String responseMsg = mapper.writeValueAsString(responseMap);
                        response.getWriter().write(responseMsg);
                    });
                })
                .sessionManagement(ses -> ses.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
        return source;
    }
}
