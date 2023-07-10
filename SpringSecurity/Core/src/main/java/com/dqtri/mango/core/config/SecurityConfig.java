/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.core.config;

import com.dqtri.mango.core.security.CustomUnauthorizedEntryPoint;
import com.dqtri.mango.core.security.ResourceOwnerEvaluator;
import com.dqtri.mango.core.security.access.AccessAuthenticationFilter;
import com.dqtri.mango.core.security.refresh.RefreshAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authorization.AuthorizationEventPublisher;
import org.springframework.security.authorization.SpringAuthorizationEventPublisher;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityFilterChain authorizeFilterChain(HttpSecurity http) throws Exception {
        // @formatter:off
        ApplicationContext context = http.getSharedObject(ApplicationContext.class);
        HttpSessionRequestCache requestCache = new HttpSessionRequestCache();
	    requestCache.setMatchingRequestParameterName("continue");
        http
//                .csrf().disable()
//                .csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
//                .cors().configurationSource(corsConfigurationSource())
//                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                .formLogin().disable()
//                .httpBasic().disable()
//                .logout().disable()
//                .anonymous().disable()
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                //https://docs.spring.io/spring-security/reference/servlet/authentication/session-management.html
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .requestCache(cache -> cache.requestCache(requestCache))
                .anonymous(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/auth/register", "/auth/login").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                //https://docs.spring.io/spring-security/reference/servlet/authentication/logout.html
                .logout(AbstractHttpConfigurer::disable)
                .addFilterBefore(context.getBean(AccessAuthenticationFilter.class), UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(context.getBean(RefreshAuthenticationFilter.class), AccessAuthenticationFilter.class)
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(unauthorizedHandler())
                        .accessDeniedHandler(accessDeniedHandler())
                );

        // @formatter:on
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("*"));
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedHeaders(List.of("Content-Type", "X-Frame-Options", "X-XSS-Protection",
                "X-Content-Type-Options", "Strict-Transport-Security", "Authorization"));
        config.setAllowedMethods(List.of("OPTIONS", "GET", "POST", "PUT", "DELETE"));
        config.setExposedHeaders(List.of("ERROR_CODE"));
        config.setAllowCredentials(false);
        config.setMaxAge(3600L);
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    @ConditionalOnMissingBean(DaoAuthenticationProvider.class)
    public DaoAuthenticationProvider authProvider(UserDetailsService userDetailsService) {
//        CachingUserDetailsService cachingUserService = new CachingUserDetailsService(userDetailsService);
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManagerBean(HttpSecurity http,
                                                           UserDetailsService userDetailsService,
                                                           AuthenticationProvider... providers) throws Exception {
        AuthenticationManagerBuilder sharedObject = http.getSharedObject(AuthenticationManagerBuilder.class);
        sharedObject.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
        for (AuthenticationProvider provider : providers) {
            sharedObject.authenticationProvider(provider);
        }
        return sharedObject.build();
    }

    @Bean
    public AuthenticationEntryPoint unauthorizedHandler() {
        return new CustomUnauthorizedEntryPoint();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new AccessDeniedHandlerImpl();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @ConditionalOnMissingBean(UserDetailsService.class)
    public InMemoryUserDetailsManager inMemoryUserDetailsManager() {
        String generatedPassword = passwordEncoder().encode("password");
        return new InMemoryUserDetailsManager(User.withUsername("user@dqtri.com")
                .password(generatedPassword).roles("ROLE_USER").build());
    }

    @Bean
    public MethodSecurityExpressionHandler expressionHandler() {
        var expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setPermissionEvaluator(new ResourceOwnerEvaluator());
        return expressionHandler;
    }

    /***
     * https://docs.spring.io/spring-security/reference/servlet/authentication/events.html
     * */
    @Bean
    @ConditionalOnMissingBean(AuthenticationEventPublisher.class)
    public AuthenticationEventPublisher authenticationEventPublisher(ApplicationEventPublisher delegate) {
        return new DefaultAuthenticationEventPublisher(delegate);
    }

    /***
     * https://docs.spring.io/spring-security/reference/servlet/authorization/events.html
     * */
    @Bean
    @ConditionalOnMissingBean(SpringAuthorizationEventPublisher.class)
    public AuthorizationEventPublisher authorizationEventPublisher(ApplicationEventPublisher delegate) {
        return new SpringAuthorizationEventPublisher(delegate);
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }
}