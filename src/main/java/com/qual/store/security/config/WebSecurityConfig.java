package com.qual.store.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qual.store.model.enums.RoleName;
import com.qual.store.security.util.JwtRequestFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
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
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final UserDetailsService jwtUserDetailsService;
    private final JwtRequestFilter jwtRequestFilter;

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
//    @Bean
    public SecurityFilterChain filterChainWithoutSecurity(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

        return httpSecurity.build();
    }

    // with security
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .cors(Customizer.withDefaults())
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
                    auth.requestMatchers(HttpMethod.POST, "/api/products/fav").authenticated();
                    auth.requestMatchers(HttpMethod.POST, "/api/products/**").hasAuthority(RoleName.ADMIN.name());
                    auth.requestMatchers(HttpMethod.POST, "/api/adresses/**").hasAuthority(RoleName.ADMIN.name());
                    auth.requestMatchers(HttpMethod.PUT, "/api/products/**").hasAuthority(RoleName.ADMIN.name());
                    auth.requestMatchers(HttpMethod.DELETE, "/api/products/fav").authenticated();
                    auth.requestMatchers(HttpMethod.DELETE, "/api/products/**").hasAuthority(RoleName.ADMIN.name());
                    auth.requestMatchers(HttpMethod.DELETE, "/api/adresses/**").hasAuthority(RoleName.ADMIN.name());

                    auth.requestMatchers(HttpMethod.GET,"/api/orders/display").hasAuthority(RoleName.ADMIN.name());
                    auth.requestMatchers(HttpMethod.GET, "/api/products/**").permitAll();
                    auth.requestMatchers(HttpMethod.GET, "/api/addresses/**").permitAll();
                    auth.requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll();
                    auth.requestMatchers(HttpMethod.GET, "/api/orderItems/**").permitAll();
                    auth.requestMatchers(HttpMethod.GET, "/api/orders/**").permitAll();
                    auth.requestMatchers(HttpMethod.GET, "/api/categories/{categoryId}").permitAll();

                    auth.requestMatchers("/api/images/**").permitAll();

                    //order item can be created, deleted, updated by anyone
                    auth.requestMatchers(HttpMethod.POST, "/api/orderItems/**").permitAll();
                    auth.requestMatchers(HttpMethod.PUT, "/api/orderItems/**").permitAll();
                    auth.requestMatchers(HttpMethod.DELETE, "/api/orderItems/**").permitAll();

                    //order can be created by user and updated, deleted by admin
                    auth.requestMatchers(HttpMethod.POST, "/api/orders/**").authenticated();
//                    auth.requestMatchers(HttpMethod.PUT, "/api/orders/**").hasAuthority(RoleName.ADMIN.name());
                    auth.requestMatchers(HttpMethod.PUT, "/api/orders/**").authenticated();
                    auth.requestMatchers(HttpMethod.DELETE, "/api/orders/**").hasAuthority(RoleName.ADMIN.name());

                    //user can be created, updated, deleted, viewed by admin
                    auth.requestMatchers(HttpMethod.POST, "/api/users/**").hasAuthority(RoleName.ADMIN.name());
                    auth.requestMatchers(HttpMethod.PUT, "/api/users/**").hasAuthority(RoleName.ADMIN.name());
                    auth.requestMatchers(HttpMethod.DELETE, "/api/users/**").hasAuthority(RoleName.ADMIN.name());
                    auth.requestMatchers(HttpMethod.GET, "/api/users/loggedInUser").authenticated();
                    auth.requestMatchers(HttpMethod.GET, "/api/users/info/me").authenticated();
                    auth.requestMatchers(HttpMethod.GET, "/api/users/**").hasAuthority(RoleName.ADMIN.name());

                    //review can be created, updated, deleted, by logged-in user
                    auth.requestMatchers(HttpMethod.POST, "/api/reviews/**").authenticated();
                    auth.requestMatchers(HttpMethod.PUT, "/api/reviews/**").authenticated();
                    auth.requestMatchers(HttpMethod.DELETE, "/api/reviews/**").authenticated();
                    auth.requestMatchers(HttpMethod.GET, "/api/reviews").permitAll();
                    auth.requestMatchers(HttpMethod.GET, "/api/reviews/**").permitAll();
                    auth.requestMatchers(HttpMethod.GET, "/api/reviews/product/{productId}").permitAll();

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

        CorsConfiguration config = new CorsConfiguration().applyPermitDefaultValues();
        config.addAllowedMethod(HttpMethod.PUT);
        config.addAllowedMethod(HttpMethod.DELETE);
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
