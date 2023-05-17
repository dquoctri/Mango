/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.submission.config;

import com.dqtri.mango.submission.security.CoreAuthenticationFilter;
import com.dqtri.mango.submission.security.CoreUnauthorizedEntryPoint;
import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity
@Configuration
public class SecurityConfig {

    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // @formatter:off
        http
                .csrf().disable()
                .cors().and()
                .anonymous().disable()
                .authorizeHttpRequests((requests) -> requests
                        .anyRequest().authenticated()
                )
                .formLogin().disable()
                .httpBasic().disable()
                .logout().disable()
                .addFilterBefore(authenticationFilter(http), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler()).and()
                .exceptionHandling().accessDeniedHandler(accessDeniedHandler());
        // @formatter:on
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("*"));
        config.setAllowedHeaders(List.of("Content-Type", "X-Frame-Options", "X-XSS-Protection",
                "X-Content-Type-Options", "Authorization"));
        config.setAllowedMethods(List.of("OPTIONS", "GET", "POST", "PUT", "DELETE"));
        config.setExposedHeaders(List.of("ERROR_CODE", "CONTENT_DISPOSITION"));
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder.authenticationProvider(authenticationProvider);
        return builder.build();
    }

    @Bean
    public Filter authenticationFilter(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        return new CoreAuthenticationFilter(builder.build());
    }

    @Bean
    public AuthenticationEntryPoint unauthorizedHandler() {
        return new CoreUnauthorizedEntryPoint();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new AccessDeniedHandlerImpl();
    }
}
