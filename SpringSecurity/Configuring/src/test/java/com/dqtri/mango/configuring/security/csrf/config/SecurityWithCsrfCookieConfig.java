package com.dqtri.mango.configuring.security.csrf.config;

import com.dqtri.mango.configuring.secirity.UnauthorizedHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@EnableWebSecurity
@EnableMethodSecurity
@Configuration
public class SecurityWithCsrfCookieConfig {

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
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Stateless API CSRF configuration
        http.csrf()
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());

        http.cors().disable()
                .anonymous().disable()
                .authorizeHttpRequests((authorize) -> authorize
                        .anyRequest().authenticated()
                )
                .formLogin().disable()
                .httpBasic().disable()
                .logout().disable()
                .exceptionHandling().authenticationEntryPoint(new UnauthorizedHandler()).and()
                .exceptionHandling().accessDeniedHandler(accessDeniedHandler());;
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
