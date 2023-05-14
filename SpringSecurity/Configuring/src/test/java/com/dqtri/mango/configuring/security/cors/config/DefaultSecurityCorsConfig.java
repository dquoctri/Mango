package com.dqtri.mango.configuring.security.cors.config;

import com.dqtri.mango.configuring.security.UnauthorizedEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;

@EnableWebSecurity
@EnableMethodSecurity
@Configuration
public class DefaultSecurityCorsConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // @formatter:off
        http.csrf().disable();
        // by default uses a Bean by the name of corsConfigurationSource
        http.cors().and()
                .anonymous().disable()
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/users/me", "/login").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin().disable()
                .httpBasic().disable()
                .logout().disable()
                .exceptionHandling().authenticationEntryPoint(new UnauthorizedEntryPoint()).and()
                .exceptionHandling().accessDeniedHandler(accessDeniedHandler());
        // @formatter:on
        return http.build();
    }


    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .build();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler(){
        return new AccessDeniedHandlerImpl();
    }

}
