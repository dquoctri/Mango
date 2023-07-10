/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.core.controller;


import com.dqtri.mango.core.config.SecurityConfig;
import com.dqtri.mango.core.model.LoginAttempt;
import com.dqtri.mango.core.model.SafeguardUser;
import com.dqtri.mango.core.model.dto.payload.LoginPayload;
import com.dqtri.mango.core.model.dto.payload.RegisterPayload;
import com.dqtri.mango.core.model.dto.response.AuthenticationResponse;
import com.dqtri.mango.core.model.dto.response.ErrorResponse;
import com.dqtri.mango.core.model.dto.response.RefreshResponse;
import com.dqtri.mango.core.model.enums.Role;
import com.dqtri.mango.core.repository.LoginAttemptRepository;
import com.dqtri.mango.core.repository.UserRepository;
import com.dqtri.mango.core.security.BasicUserDetails;
import com.dqtri.mango.core.security.TokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = {AuthController.class},
        includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class))
public class AuthControllerIntegrationTest extends AbstractIntegrationTest {

    @MockBean
    private PasswordEncoder passwordEncoder;
    @MockBean
    private TokenProvider tokenProvider;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private LoginAttemptRepository loginAttemptRepository;

    @Nested
    class RegisterIntegrationTest {
        private static final String REGISTER_ROUTE = "/auth/register";

        @Captor
        ArgumentCaptor<SafeguardUser> userArgumentCaptor;

        @BeforeEach
        public void setup() {
            when(userRepository.existsByEmail(anyString())).thenReturn(false);
        }

        @Test
        void register_givenUserCredentials_thenSuccess() throws Exception {
            when(loginAttemptRepository.findByEmail(anyString())).thenReturn(Optional.empty());
            SafeguardUser safeguardUser = createSafeguardUser();
            when(userRepository.save(any())).thenReturn(safeguardUser);
            RegisterPayload registerPayload = createRegisterPayload();
            //then
            performRegisterRequest(registerPayload, status().isOk());
            //test
            verify(userRepository).save(userArgumentCaptor.capture());
            SafeguardUser value = userArgumentCaptor.getValue();
            assertThat(value.getRole()).isEqualTo(Role.SUBMITTER);
            verify(userRepository).save(createSafeguardUser());
            verify(loginAttemptRepository, never()).save(any());
        }

        @Test
        void register_givenExistedLoginAttempt_thenSuccess() throws Exception {
            RegisterPayload registerPayload = createRegisterPayload();
            when(loginAttemptRepository.findByEmail(anyString()))
                    .thenReturn(Optional.of(new LoginAttempt(registerPayload.getEmail())));
            SafeguardUser safeguardUser = createSafeguardUser();
            when(userRepository.save(any())).thenReturn(safeguardUser);
            //then
            performRegisterRequest(registerPayload, status().isOk());
            //test
            verify(userRepository).save(userArgumentCaptor.capture());
            SafeguardUser value = userArgumentCaptor.getValue();
            assertThat(value.getRole()).isEqualTo(Role.SUBMITTER);
            verify(userRepository, times(1)).save(any());
            verify(loginAttemptRepository, times(1)).delete(any());
        }

        @Test
        void register_givenEmptyPayload_thenBadRequest() throws Exception {
            RegisterPayload registerPayload = new RegisterPayload();
            //then
            performRegisterRequest(registerPayload, status().isBadRequest());
            //test
            verify(userRepository, never()).save(userArgumentCaptor.capture());
        }

        @ParameterizedTest
        @ValueSource(strings = {"invalidEmailFormat", "invalidEmailFormat@", "@invalidEmailFormat", "mangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomango@dqtri.com"})
        void register_givenInvalidEmailFormat_thenBadRequest(String invalidEmail) throws Exception {
            RegisterPayload registerPayload = new RegisterPayload();
            registerPayload.setEmail(invalidEmail);
            registerPayload.setPassword("mango");
            //then
            performRegisterRequest(registerPayload, status().isBadRequest());
            //test
            verify(userRepository, never()).save(userArgumentCaptor.capture());
        }

        @Test
        void register_givenNonPassword_thenBadRequest() throws Exception {
            RegisterPayload registerPayload = new RegisterPayload();
            registerPayload.setEmail("newcomer@mango.dqtri.com");
            //then
            performRegisterRequest(registerPayload, status().isBadRequest());
            //test
            verify(userRepository, never()).save(userArgumentCaptor.capture());
        }

        @ParameterizedTest
        @ValueSource(strings = {"st", "", "       ", "more_than_24_characters_too_long_password"})
        void register_givenInvalidPasswordFormat_thenBadRequest(String password) throws Exception {
            RegisterPayload registerPayload = new RegisterPayload();
            registerPayload.setEmail("newcomer@mango.dqtri.com");
            registerPayload.setPassword(password);
            //then
            performRegisterRequest(registerPayload, status().isBadRequest());
            //test
            verify(userRepository, never()).save(userArgumentCaptor.capture());
        }


        @Test
        void register_mockExitedEmail_thenThrowConflictException() throws Exception {
            RegisterPayload registerPayload = createRegisterPayload();
            when(userRepository.existsByEmail(anyString())).thenReturn(true);
            //then
            performRegisterRequest(registerPayload, status().isConflict());
            //test
            verify(userRepository, never()).save(userArgumentCaptor.capture());
        }

        private RegisterPayload createRegisterPayload() {
            RegisterPayload registerPayload = new RegisterPayload();
            registerPayload.setEmail("newcomer@mango.dqtri.com");
            registerPayload.setPassword("newcomer");
            return registerPayload;
        }

        private SafeguardUser createSafeguardUser() {
            SafeguardUser safeguardUser = new SafeguardUser();
            safeguardUser.setEmail("newcomer@mango.dqtri.com");
            safeguardUser.setPassword(passwordEncoder.encode("newcomer"));
            safeguardUser.setRole(Role.SUBMITTER);
            return safeguardUser;
        }

        private void performRegisterRequest(RegisterPayload registerPayload, ResultMatcher... matchers) throws Exception {
            mvc.perform(post(REGISTER_ROUTE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(createPayloadJson(registerPayload)))
                    .andExpectAll(matchers);
        }
    }

    @Nested
    class LoginIntegrationTest {
        private static final String LOGIN_ROUTE = "/auth/login";

        @Test
        void login_givenUserCredentials_thenSuccess() throws Exception {
            LoginPayload loginPayload = createLoginPayload();
            var authentication = new UsernamePasswordAuthenticationToken(loginPayload.getEmail(),
                    loginPayload.getPassword());
            when(authenticationManager.authenticate(any())).thenReturn(authentication);
            when(tokenProvider.generateToken(any())).thenReturn("token_value");
            //then
            MvcResult mvcResult = performLoginRequest(loginPayload, status().isOk());
            String json = mvcResult.getResponse().getContentAsString();
            AuthenticationResponse authenticationResponse =
                    new ObjectMapper().readValue(json, AuthenticationResponse.class);
            //test
            assertThat(authenticationResponse).isNotNull();
            assertThat(authenticationResponse.getRefreshToken()).isEqualTo("token_value");
            assertThat(authenticationResponse.getAccessToken()).isEqualTo("token_value");
        }

        @Test
        void login_givenEmptyPayload_thenBadRequest() throws Exception {
            LoginPayload loginPayload = new LoginPayload();
            //then
            performLoginRequest(loginPayload, status().isBadRequest());
            //test
            verifyNoInteractions(authenticationManager);
            verifyNoInteractions(tokenProvider);
        }

        @ParameterizedTest
        @ValueSource(strings = {"invalidEmailFormat", "invalidEmailFormat@", "@invalidEmailFormat", "mangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomango@dqtri.com"})
        void login_givenInvalidEmailFormat_thenBadRequest(String invalidEmail) throws Exception {
            LoginPayload loginPayload = new LoginPayload();
            loginPayload.setEmail(invalidEmail);
            loginPayload.setPassword("******");
            //then
            performLoginRequest(loginPayload, status().isBadRequest());
            //test
            verifyNoInteractions(authenticationManager);
            verifyNoInteractions(tokenProvider);
        }

        @Test
        void login_givenNonPassword_thenBadRequest() throws Exception {
            LoginPayload loginPayload = new LoginPayload();
            loginPayload.setEmail("submitter@mango.dqtri.com");
            //then
            performLoginRequest(loginPayload, status().isBadRequest());
            //test
            verifyNoInteractions(authenticationManager);
            verifyNoInteractions(tokenProvider);
        }

        @ParameterizedTest
        @ValueSource(strings = {"st", "", "       ", "more_than_24_characters_too_long_password"})
        void login_givenInvalidPasswordFormat_thenBadRequest(String password) throws Exception {
            LoginPayload loginPayload = new LoginPayload();
            loginPayload.setEmail("submitter@mango.dqtri.com");
            loginPayload.setPassword(password);
            //then
            performLoginRequest(loginPayload, status().isBadRequest());
            //test
            verifyNoInteractions(authenticationManager);
            verifyNoInteractions(tokenProvider);
        }

        @Test
        void login_mockBadCredentials_thenUnauthorized() throws Exception {
            LoginPayload loginPayload = createLoginPayload();
            when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad credentials"));
            //then
            MvcResult mvcResult = performLoginRequest(loginPayload, status().isUnauthorized());
            //test
            verifyNoInteractions(tokenProvider);
            String json = mvcResult.getResponse().getContentAsString();
            ErrorResponse errorResponse = new ObjectMapper().readValue(json, ErrorResponse.class);
            assertThat(errorResponse).isNotNull();
            assertThat(errorResponse.getMessage()).isEqualTo("Bad credentials");

        }

        @Test
        void login_givenBadCredentialsAndMockNewLoginAttempt_thenUnauthorized() throws Exception {
            LoginPayload loginPayload = createLoginPayload();
            when(loginAttemptRepository.findByEmail(anyString()))
                    .thenReturn(Optional.of(new LoginAttempt(loginPayload.getEmail())));
            when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad credentials"));
            //then
            MvcResult mvcResult = performLoginRequest(loginPayload, status().isUnauthorized());
            //test
            verifyNoInteractions(tokenProvider);
            verify(loginAttemptRepository, never()).delete(any());
            String json = mvcResult.getResponse().getContentAsString();
            ErrorResponse errorResponse = new ObjectMapper().readValue(json, ErrorResponse.class);
            assertThat(errorResponse).isNotNull();
            assertThat(errorResponse.getMessage()).isEqualTo("Bad credentials");
        }

        @Test
        void login_givenBadCredentialsAndMockLockedLoginAttempt_thenUnprocessableEntity() throws Exception {

            LoginPayload loginPayload = createLoginPayload();
            LoginAttempt loginAttempt = new LoginAttempt(loginPayload.getEmail());
            loginAttempt.setLockout(true);
            when(loginAttemptRepository.findByEmail(anyString())).thenReturn(Optional.of(loginAttempt));
            //then
            MvcResult mvcResult = performLoginRequest(loginPayload, status().isUnprocessableEntity());
            //test
            verifyNoInteractions(authenticationManager);
            verifyNoInteractions(tokenProvider);
            verify(loginAttemptRepository, never()).delete(any());
            String json = mvcResult.getResponse().getContentAsString();
            ErrorResponse errorResponse = new ObjectMapper().readValue(json, ErrorResponse.class);
            assertThat(errorResponse).isNotNull();
            assertThat(errorResponse.getMessage())
                    .isEqualTo(loginPayload.getEmail()+ " has been locked due to multiple failed login attempts");
        }

        private LoginPayload createLoginPayload() {
            LoginPayload loginPayload = new LoginPayload();
            loginPayload.setEmail("submitter@mango.dqtri.com");
            loginPayload.setPassword("submitter");
            return loginPayload;
        }

        private MvcResult performLoginRequest(LoginPayload loginPayload, ResultMatcher... matchers) throws Exception {
            return mvc.perform(post(LOGIN_ROUTE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(createPayloadJson(loginPayload)))
                    .andExpectAll(matchers)
                    .andReturn();
        }
    }

    @Nested
    class RefreshTokenIntegrationTest {
        private static final String REFRESH_ROUTE = "/auth/refresh";

        @BeforeEach
        public void setup() {
            when(tokenProvider.generateToken(any())).thenReturn("token_value");
        }

        @Test
        @WithMockUser(roles = "REFRESH")
        void refreshToken_mockRefreshRoleCredentials_thenSuccess() throws Exception {
            MvcResult mvcResult = performRefreshRequest(status().isOk());
            String json = mvcResult.getResponse().getContentAsString();
            RefreshResponse refreshResponse = new ObjectMapper().readValue(json, RefreshResponse.class);
            //test
            assertThat(refreshResponse).isNotNull();
            assertThat(refreshResponse.getAccessToken()).isEqualTo("token_value");
        }

        @Test
        @WithMockUser(authorities = "REFRESH")
        void refreshToken_mockRefreshAuthorityCredentials_thenSuccess() throws Exception {
            MvcResult mvcResult = performRefreshRequest(status().isOk());
            String json = mvcResult.getResponse().getContentAsString();
            RefreshResponse refreshResponse = new ObjectMapper().readValue(json, RefreshResponse.class);
            assertThat(refreshResponse).isNotNull();
            assertThat(refreshResponse.getAccessToken()).isEqualTo("token_value");
        }

        @Test
        void refreshToken_withRefreshRoleCredentials_thenSuccess() throws Exception {
            RequestPostProcessor user = user("mango@dqtri.com").password("********").roles("REFRESH");
            MvcResult mvcResult = performRefreshRequest(user, status().isOk());
            String json = mvcResult.getResponse().getContentAsString();
            RefreshResponse refreshResponse = new ObjectMapper().readValue(json, RefreshResponse.class);
            //test
            assertThat(refreshResponse).isNotNull();
            assertThat(refreshResponse.getAccessToken()).isEqualTo("token_value");
        }

        @Test
        void refreshToken_withRefreshAuthorityCredentials_thenSuccess() throws Exception {
            RequestPostProcessor user = user("mango@dqtri.com").password("********")
                    .authorities(new SimpleGrantedAuthority("REFRESH"));
            MvcResult mvcResult = performRefreshRequest(user, status().isOk());
            String json = mvcResult.getResponse().getContentAsString();
            RefreshResponse refreshResponse = new ObjectMapper().readValue(json, RefreshResponse.class);
            //test
            assertThat(refreshResponse).isNotNull();
            assertThat(refreshResponse.getAccessToken()).isEqualTo("token_value");
        }

        @Test
        void refreshToken_withLackOfPermissions_thenForbidden() throws Exception {
            RequestPostProcessor user = user("app@dqtri.com").password("******")
                    .roles("MANAGER", "SUBMITTER", "SPECIALIST", "NONE", "INVALID")
                    .authorities(buildAuthorities("MANAGER", "SUBMITTER", "SPECIALIST", "NONE", "INVALID"));
            performRefreshRequest(user, status().isForbidden());
        }

        @Test
        @WithMockUser(roles = {"INVALID", "ADMIN", "SUBMITTER", "MANAGER", "SPECIALIST", "NONE"})
        void refreshToken_mockAppRoles_thenForbidden() throws Exception {
            performRefreshRequest(status().isForbidden());
            verify(tokenProvider, never()).generateToken(any());
        }

        @Test
        @WithMockUser(authorities = {"INVALID", "ADMIN", "SUBMITTER", "MANAGER", "SPECIALIST", "NONE"})
        void refreshToken_mockAppAuthorities_thenForbidden() throws Exception {
            performRefreshRequest(status().isForbidden());
            verify(tokenProvider, never()).generateToken(any());
        }

        private MvcResult performRefreshRequest(ResultMatcher... matchers) throws Exception {
            return mvc.perform(get(REFRESH_ROUTE)).andExpectAll(matchers).andReturn();
        }

        private MvcResult performRefreshRequest(RequestPostProcessor processor, ResultMatcher... matchers) throws Exception {
            return mvc.perform(get(REFRESH_ROUTE).with(processor)).andExpectAll(matchers).andReturn();
        }
    }

    @Nested
    class LogoutIntegrationTest {
        private static final String LOGOUT_ROUTE = "/auth/logout";

        @Test
        @WithMockUser(roles = "REFRESH")
        void logoutToken_mockRefreshRoleCredentials_thenSuccess() throws Exception {
            performLogoutRequest(status().isNoContent());
            verify(blackListRefreshTokenRepository, times(1)).save(any());
        }

        @Test
        @WithMockUser(authorities = "REFRESH")
        void logoutToken_mockRefreshAuthorityCredentials_thenSuccess() throws Exception {
            performLogoutRequest(status().isNoContent());
            verify(blackListRefreshTokenRepository, times(1)).save(any());
        }

        @Test
        void logoutToken_givenBasicUserDetails_thenSuccess() throws Exception {
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.AUTHORIZATION, "mock_token");
            //then
            mvc.perform(delete(LOGOUT_ROUTE).headers(headers).with(user(newBasicUserDetails())))
                    .andExpectAll(status().isNoContent());
            //test
            verify(blackListRefreshTokenRepository, times(1)).save(any());
        }

        private BasicUserDetails newBasicUserDetails(){
            SafeguardUser safeguardUser = new SafeguardUser();
            safeguardUser.setEmail("safeguard@dqtri.com");
            safeguardUser.setPassword("safeguard");
            return new BasicUserDetails(safeguardUser);
        }

        @Test
        void logout_givenNothing_thenUnauthorized() throws Exception {
            performLogoutRequest(status().isUnauthorized());
            verify(blackListRefreshTokenRepository, never()).save(any());
        }

        @Test
        @WithMockUser(roles = {"INVALID", "ADMIN", "SUBMITTER", "MANAGER", "SPECIALIST", "NONE"})
        void logout_mockAppRoles_thenForbidden() throws Exception {
            performLogoutRequest(status().isForbidden());
            verify(tokenProvider, never()).generateToken(any());
        }

        @Test
        @WithMockUser(authorities = {"INVALID", "ADMIN", "SUBMITTER", "MANAGER", "SPECIALIST", "NONE"})
        void logout_mockAppAuthorities_thenForbidden() throws Exception {
            performLogoutRequest(status().isForbidden());
            verify(tokenProvider, never()).generateToken(any());
        }

        @Test
        void logout_withLackOfPermissions_thenForbidden() throws Exception {
            RequestPostProcessor user = user("norefresh@dqtri.com").password("****")
                    .roles("INVALID", "ADMIN", "SUBMITTER", "MANAGER", "SPECIALIST", "NONE")
                    .authorities(buildAuthorities("INVALID", "ADMIN", "SUBMITTER", "MANAGER", "SPECIALIST", "NONE"));
            performLogoutRequest(user, status().isForbidden());
            verify(tokenProvider, never()).generateToken(any());
        }

        private void performLogoutRequest(RequestPostProcessor postProcessor, ResultMatcher... matchers) throws Exception {
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.AUTHORIZATION, "mock_token");
            mvc.perform(delete(LOGOUT_ROUTE).headers(headers).with(postProcessor)).andExpectAll(matchers);
        }

        private void performLogoutRequest(ResultMatcher... matchers) throws Exception {
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.AUTHORIZATION, "mock_token");
            mvc.perform(delete(LOGOUT_ROUTE).headers(headers)).andExpectAll(matchers);
        }
    }
}
