package com.dqtri.mango.configuring.config;

import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final AuthenticationProvider authenticationProvider;

    private final Filter filter;

    /**
     * config Cross-Site Request Forgery (CSRF) attacks
     * Starting from Spring Security 4.x, the CSRF protection is enabled by default.
     * @param http
     * @return
     * @throws Exception
     */
    @Bean
    @Order(1)
    public SecurityFilterChain disableCsrfFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable();
        return http.build();
    }

    /**
     * <meta name="_csrf" content="${_csrf.token}"/>
     * <meta name="_csrf_header" content="${_csrf.headerName}"/>
     * <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
     * Our stateless API can't add the CSRF token like our MVC configuration because it doesn't generate any HTML view.
     * In that case, we can send the CSRF token in a cookie using CookieCsrfTokenRepository:
     * fetch(url, {
     *   method: 'POST',
     *   body: * data to send *,
     *   headers: { 'X-XSRF-TOKEN':csrfToken },
     * })
     * @param http
     * @return
     * @throws Exception
     */
    @Bean
    @Order(2)
    public SecurityFilterChain cookieCsrfFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf()
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain authorizeFilterChain(HttpSecurity http) throws Exception {
        // @formatter:off
        http
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/register", "/login").permitAll()
                        .anyRequest().authenticated()
                );
        http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
        // @formatter:on
        return http.build();
    }

    private CorsConfigurationSource configurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("*"));
        config.setAllowedHeaders(List.of("Content-Type", "Authorization", "X-Frame-Options", "X-XSS-Protection",
                "X-Content-Type-Options", "Ocp-Apim-Subscription-Key"));
        config.setAllowedMethods(List.of("OPTIONS", "GET", "POST", "PUT", "DELETE"));
        config.setExposedHeaders(List.of("ERROR_CODE", "GROUPS", "CONTENT_DISPOSITION"));
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
