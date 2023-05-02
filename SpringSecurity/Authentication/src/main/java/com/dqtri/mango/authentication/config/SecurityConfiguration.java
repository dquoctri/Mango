/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.authentication.config;

import com.dqtri.mango.authentication.repository.UserRepository;
import com.dqtri.mango.authentication.security.MyCustomDsl;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static com.dqtri.mango.authentication.security.MyCustomDsl.customDsl;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfiguration {

    private final UserRepository userRepository;
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
        http.apply(customDsl());
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
        return http.getSharedObject(AuthenticationManagerBuilder.class).build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    public UserDetailsManager users(DataSource dataSource) {
//        UserDetails user = User.withDefaultPasswordEncoder()
//                .username("user")
//                .password("password")
//                .roles("USER")
//                .build();
//
//        JdbcUserDetailsManager users = new JdbcUserDetailsManager(dataSource);
//        users.createUser(user);
//        return users;
//    }
//
//    @Bean
//    public InMemoryUserDetailsManager userDetailsService() {
//        UserDetails user = User.withDefaultPasswordEncoder()
//                .username("user")
//                .password("password")
//                .roles("USER")
//                .build();
//        return new InMemoryUserDetailsManager(user);
//    }
}
