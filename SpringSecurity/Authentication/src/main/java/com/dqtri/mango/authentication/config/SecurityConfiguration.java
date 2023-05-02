/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.authentication.config;

import com.dqtri.mango.authentication.model.MangoUser;
import com.dqtri.mango.authentication.repository.UserRepository;
import com.dqtri.mango.authentication.security.AuthenticationFilter;
import com.dqtri.mango.authentication.security.MyCustomDsl;
import com.dqtri.mango.authentication.security.models.MangoUserDetails;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.dqtri.mango.authentication.security.MyCustomDsl.customDsl;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfiguration {

    private final UserRepository userRepository;
    private final AuthenticationProvider authenticationProvider;
    private final UserDetailsService userDetailsService;
    /**
     * This function configures the security filter chain for HTTP requests
     * The WebSecurityConfigurerAdapter was deprecated In Spring Security 5.7.0-M2
     * @link <a href="https://spring.io/blog/2022/02/21/spring-security-without-the-websecurityconfigureradapter">
     *     spring-security-without-the-websecurityconfigureradapter</a>
     *
     * @param http The `http` parameter is an instance of `HttpSecurity`, which is a configuration
     *             object that allows you to configure security settings for your application.
     * @return A SecurityFilterChain object is being returned.
     */
    @Bean
    @Order(1)
    public SecurityFilterChain securityAnonymousFilterChain(HttpSecurity http) throws Exception {
        // @formatter:off
        http.csrf().disable()
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/register", "/login").permitAll()
                        .anyRequest().authenticated()
                );
//        http.apply(customDsl());
        http.addFilterBefore(new AuthenticationFilter(authenticationManager(http)), UsernamePasswordAuthenticationFilter.class);
        // @formatter:on
        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain securityFormFilterChain(HttpSecurity http) throws Exception {
        // @formatter:off
        http

                .formLogin().disable()
                .httpBasic().disable()
                .logout().disable();
        // @formatter:on
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
        builder.authenticationProvider(authenticationProvider);
        return builder.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetails users() {
        UserDetails user = User.withDefaultPasswordEncoder()
                .username("user1")
                .password("password1")
                .roles("USER")
                .build();
        return user;
    }

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        UserDetails user = User.withDefaultPasswordEncoder()
                .username("user2")
                .password("password2")
                .roles("USER")
                .build();
        return new InMemoryUserDetailsManager(user);
    }
}
