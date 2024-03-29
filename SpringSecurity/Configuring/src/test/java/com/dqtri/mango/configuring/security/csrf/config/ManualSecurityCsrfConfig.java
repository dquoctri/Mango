package com.dqtri.mango.configuring.security.csrf.config;

import com.dqtri.mango.configuring.security.UnauthorizedEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;

@EnableWebSecurity
@EnableMethodSecurity
@Configuration
public class ManualSecurityCsrfConfig {

    /**
     * config Cross-Site Request Forgery (CSRF) attacks
     * Starting from Spring Security 4.x, the CSRF protection is enabled by default.
     *
     * @param http
     * @return
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // @formatter:off
        http.csrf().disable();

        http.cors().disable()
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
    public AccessDeniedHandler accessDeniedHandler() {
        return new AccessDeniedHandlerImpl();
    }

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        UserDetails submitter = User.withUsername("submitter")
                .password("submitter")
                .authorities("ROLE_SUBMITTER")
                .build();
        UserDetails admin = User.withUsername("admin")
                .password("admin")
                .authorities("ROLE_ADMIN")
                .build();
        return new InMemoryUserDetailsManager(submitter, admin);
    }
}

