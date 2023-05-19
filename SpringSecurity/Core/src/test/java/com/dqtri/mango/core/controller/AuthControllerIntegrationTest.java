/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.core.controller;


import com.dqtri.mango.core.model.CoreUser;
import com.dqtri.mango.core.model.dto.payload.RegisterPayload;
import com.dqtri.mango.core.model.enums.Role;
import com.dqtri.mango.core.repository.UserRepository;
import com.dqtri.mango.core.security.TokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Nested
@WebMvcTest(controllers = {AuthController.class})
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerIntegrationTest extends AbstractIntegrationTest {

    @MockBean
    private PasswordEncoder passwordEncoder;
    @MockBean
    private AuthenticationManager authenticationManager;
    @MockBean
    private TokenProvider tokenProvider;
    @MockBean
    private UserRepository userRepository;

    private static final String REGISTER_ROUTE = "/register";

    @BeforeEach
    public void setup() {
//        authenticationFilter = new CustomAuthenticationFilter(authenticationManager);
        passwordEncoder = new BCryptPasswordEncoder();
    }

    @Nested
    class RegisterIntegrationTest {
        private static final String REGISTER_ROUTE = "/register";

        @Captor
        ArgumentCaptor<CoreUser> userArgumentCaptor;

        @BeforeEach
        public void setup() {

        }

        @Test
        public void register_givenUserCredentials_thenSuccess() throws Exception {
            RegisterPayload registerPayload = createRegisterPayload();
            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            assertOk(registerPayload);
            verify(userRepository).save(userArgumentCaptor.capture());
            CoreUser value = userArgumentCaptor.getValue();
            assertThat(value.getRole()).isEqualTo(Role.SUBMITTER);
            verify(userRepository).save(createCoreUser());
        }

        @Test
        public void register_givenEmptyPayload_thenBadRequest() throws Exception {
            RegisterPayload registerPayload = new RegisterPayload();
            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            assertBadRequest(registerPayload);
            verifyNoInteractions((userRepository));
        }

        @ParameterizedTest
        @ValueSource(strings = {"invalidEmailFormat", "invalidEmailFormat@", "@invalidEmailFormat"})
        public void register_givenInvalidEmailFormat_thenBadRequest(String invalidEmail) throws Exception {
            RegisterPayload registerPayload = new RegisterPayload();
            registerPayload.setEmail(invalidEmail);
            registerPayload.setPassword("mango");
            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            //then
            assertBadRequest(registerPayload);
            verifyNoInteractions((userRepository));
        }

        @Test
        public void register_givenNonPassword_thenBadRequest() throws Exception {
            RegisterPayload registerPayload = new RegisterPayload();
            registerPayload.setEmail("newcomer@mango.dqtri.com");
            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            //then
            assertBadRequest(registerPayload);
        }

        @ParameterizedTest
        @ValueSource(strings = {"st", "", "       ", "more_than_24_characters_too_long_password"})
        public void register_givenInvalidPasswordFormat_thenBadRequest() throws Exception {
            RegisterPayload registerPayload = new RegisterPayload();
            registerPayload.setEmail("newcomer@mango.dqtri.com");
            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            //then
            assertBadRequest(registerPayload);
        }

        private void assertOk(RegisterPayload registerPayload) throws Exception {
            mvc.perform(post(REGISTER_ROUTE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(createPayloadJson(registerPayload)))
                    .andExpect(status().isOk());
        }

        private void assertBadRequest(RegisterPayload registerPayload) throws Exception {
            mvc.perform(post(REGISTER_ROUTE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(createPayloadJson(registerPayload)))
                    .andExpect(status().isBadRequest());
            verifyNoInteractions((userRepository));
        }

        private RegisterPayload createRegisterPayload(){
            RegisterPayload registerPayload = new RegisterPayload();
            registerPayload.setEmail("newcomer@mango.dqtri.com");
            registerPayload.setPassword("newcomer");
            return registerPayload;
        }

        private CoreUser createCoreUser(){
            CoreUser coreUser = new CoreUser();
            coreUser.setEmail("newcomer@mango.dqtri.com");
            coreUser.setPassword(passwordEncoder.encode("newcomer"));
            coreUser.setRole(Role.SUBMITTER);
            return coreUser;
        }
    }

    @Nested
    class LoginIntegrationTest {
        private static final String REGISTER_ROUTE = "/login";

        @Captor
        ArgumentCaptor<CoreUser> userArgumentCaptor;

        @BeforeEach
        public void setup() {

        }

        //TODO:
    }
}
