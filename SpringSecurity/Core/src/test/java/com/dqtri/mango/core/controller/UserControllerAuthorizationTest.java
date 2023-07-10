/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.core.controller;

import com.dqtri.mango.core.common.WithMockAppUser;
import com.dqtri.mango.core.config.SecurityConfig;
import com.dqtri.mango.core.model.LoginAttempt;
import com.dqtri.mango.core.model.SafeguardUser;
import com.dqtri.mango.core.model.dto.payload.ResetPasswordPayload;
import com.dqtri.mango.core.model.dto.payload.UserCreatingPayload;
import com.dqtri.mango.core.model.dto.payload.UserUpdatingPayload;
import com.dqtri.mango.core.model.dto.response.ErrorResponse;
import com.dqtri.mango.core.model.enums.Role;
import com.dqtri.mango.core.repository.LoginAttemptRepository;
import com.dqtri.mango.core.repository.UserRepository;
import com.dqtri.mango.core.security.AppUserDetails;
import com.dqtri.mango.core.security.permissions.UpdatableResourcePermission;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Nested
@ExtendWith({SpringExtension.class})
@WebMvcTest(controllers = {UserController.class},
        includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = {SecurityConfig.class, UpdatableResourcePermission.class}))
public class UserControllerAuthorizationTest extends AbstractIntegrationTest {
    @MockBean
    private PasswordEncoder passwordEncoder;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private LoginAttemptRepository loginAttemptRepository;

    @Nested
    class RouteGetAllUsersAuthorizationIntegrationTest {
        private static final String USER_ROUTE = "/users";

        @Test
        @WithMockUser(roles = {"ADMIN"})
        void getAllUsers_defaultAdmin_returnPagination() throws Exception {
            assertOkRequest();
        }

        @Test
        @WithMockUser(roles = {"MANAGER"})
        void getAllUsers_mockManager_returnPagination() throws Exception {
            assertOkRequest();
        }

        @Test
        @WithMockUser(roles = {"SPECIALIST"})
        void getAllUsers_mockSpecialist_returnPagination() throws Exception {
            assertOkRequest();
        }

        @Test
        @WithMockUser(roles = {"SUBMITTER"})
        void getAllUsers_mockSubmitter_returnPagination() throws Exception {
            assertOkRequest();
        }

        private void assertOkRequest() throws Exception {
            Pageable pageable = PageRequest.of(0, 25, Sort.by(Sort.DEFAULT_DIRECTION, "pk"));
            Page<SafeguardUser> usersPage = new PageImpl<>(new ArrayList<>(), pageable, 0);
            when(userRepository.findByRole(null, pageable)).thenReturn(usersPage);
            //then
            performRequest(status().isOk());
        }

        @ParameterizedTest
        @ValueSource(strings = {"ADMIN", "MANAGER", "SPECIALIST", "SUBMITTER"})
        void getAllUsers_withProcessor_returnPagination(String role) throws Exception {
            Pageable pageable = PageRequest.of(0, 25, Sort.by(Sort.DEFAULT_DIRECTION, "pk"));
            Page<SafeguardUser> usersPage = new PageImpl<>(new ArrayList<>(), pageable, 0);
            when(userRepository.findByRole(null, pageable)).thenReturn(usersPage);
            RequestPostProcessor user = user("appuser@dqtri.com").password("******").roles(role);
            //then
            performRequest(user, status().isOk());
        }

        @Test
        void getAllUsers_nonMockUser_returnUnauthorized() throws Exception {
            performRequest(status().isUnauthorized());
        }

        @Test
        @WithMockUser(roles = {"NONE", "REFRESH", "INVALID"})
        void getAllUsers_giveNonAdminRoles_thenForbidden() throws Exception {
            MvcResult mvcResult = performRequest(status().isForbidden());
            String json = mvcResult.getResponse().getContentAsString();
            ErrorResponse errorResponse = new ObjectMapper().readValue(json, ErrorResponse.class);
            //test
            assertForbiddenResponse(errorResponse);
        }

        @Test
        @WithMockUser(authorities = {"NONE", "REFRESH", "INVALID"})
        void getAllUsers_giveNonAdminAuthority_thenForbidden() throws Exception {
            MvcResult mvcResult = performRequest(status().isForbidden());
            String json = mvcResult.getResponse().getContentAsString();
            ErrorResponse errorResponse = new ObjectMapper().readValue(json, ErrorResponse.class);
            //test
            assertForbiddenResponse(errorResponse);
        }

        @Test
        void getAllUsers_mockAuthorityOfOthers_thenForbidden() throws Exception {
            RequestPostProcessor user = user("appuser@dqtri.com").password("******")
                    .roles("NONE", "REFRESH", "INVALID")
                    .authorities(buildAuthorities("NONE", "REFRESH", "INVALID"));
            MvcResult mvcResult = performRequest(user, status().isForbidden());
            String json = mvcResult.getResponse().getContentAsString();
            ErrorResponse errorResponse = new ObjectMapper().readValue(json, ErrorResponse.class);
            //test
            assertForbiddenResponse(errorResponse);
        }

        private void assertForbiddenResponse(ErrorResponse errorResponse) {
            assertThat(errorResponse).isNotNull();
            assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.FORBIDDEN);
            assertThat(errorResponse.getMessage()).isEqualTo("Access Denied");
        }

        private MvcResult performRequest(ResultMatcher... matchers) throws Exception {
            return mvc.perform(get(USER_ROUTE)).andExpectAll(matchers).andReturn();
        }

        private MvcResult performRequest(RequestPostProcessor processor,
                                         ResultMatcher... matchers) throws Exception {
            return mvc.perform(get(USER_ROUTE).with(processor)).andExpectAll(matchers).andReturn();
        }
    }

    @Nested
    class RouteGetUserAuthorizationIntegrationTest {
        private static final String USER_ROUTE = "/users/{userId}";

        @Test
        @WithMockUser(roles = {"ADMIN"})
        void getUserById_defaultAdmin_thenOk() throws Exception {
            assertOkRequest();
        }

        @Test
        @WithMockUser(roles = {"MANAGER"})
        void getUserById_mockManager_thenOk() throws Exception {
            assertOkRequest();
        }

        @Test
        @WithMockUser(roles = {"SUBMITTER"})
        void getUserById_mockSubmitter_thenOk() throws Exception {
            assertOkRequest();
        }

        @Test
        @WithMockUser(roles = {"SPECIALIST"})
        void getUserById_mockSpecialist_thenOk() throws Exception {
            assertOkRequest();
        }

        private void assertOkRequest() throws Exception {
            SafeguardUser submitterUser = createSubmitterUser();
            when(userRepository.findById(1L)).thenReturn(Optional.of(submitterUser));
            //then
            performRequest(1L, status().isOk());
        }

        @ParameterizedTest
        @ValueSource(strings = {"ADMIN", "MANAGER", "SUBMITTER", "SPECIALIST"})
        void getUserById_withProcessor_thenOk(String role) throws Exception {
            SafeguardUser submitterUser = createSubmitterUser();
            when(userRepository.findById(4L)).thenReturn(Optional.of(submitterUser));
            RequestPostProcessor user = user("admin@dqtri.com").password("******").roles(role);
            performRequest(4L, user, status().isOk());
        }

        @Test
        void getUserById_nonMockUser_returnUnauthorized() throws Exception {
            performRequest(5L, status().isUnauthorized());
            verify(userRepository, never()).findById(5L);
        }

        @Test
        @WithMockUser(roles = {"NONE", "REFRESH", "INVALID"})
        void getUserById_giveNonAdminRoles_thenForbidden() throws Exception {
            MvcResult mvcResult = performRequest(4L, status().isForbidden());
            //test
            assertForbiddenResponse(mvcResult);
        }

        @Test
        @WithMockUser(authorities = {"NONE", "REFRESH", "INVALID"})
        void getUserById_giveNonAdminAuthority_thenForbidden() throws Exception {
            MvcResult mvcResult = performRequest(3L, status().isForbidden());
            //test
            assertForbiddenResponse(mvcResult);
        }

        @Test
        void getUserById_mockAuthorityOfOthers_thenForbidden() throws Exception {
            RequestPostProcessor user = user("appuser@dqtri.com").password("******")
                    .roles("NONE", "REFRESH", "INVALID")
                    .authorities(buildAuthorities("NONE", "REFRESH", "INVALID"));
            MvcResult mvcResult = performRequest(2L, user, status().isForbidden());
            //test
            assertForbiddenResponse(mvcResult);
        }

        private void assertForbiddenResponse(MvcResult mvcResult) throws UnsupportedEncodingException, JsonProcessingException {
            String json = mvcResult.getResponse().getContentAsString();
            ErrorResponse errorResponse = new ObjectMapper().readValue(json, ErrorResponse.class);
            assertThat(errorResponse).isNotNull();
            assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.FORBIDDEN);
            assertThat(errorResponse.getMessage()).isEqualTo("Access Denied");
        }

        private MvcResult performRequest(long userId, ResultMatcher... matchers) throws Exception {
            return mvc.perform(get(USER_ROUTE, userId))
                    .andExpectAll(matchers).andReturn();
        }

        private MvcResult performRequest(long userId, RequestPostProcessor processor,
                                         ResultMatcher... matchers) throws Exception {
            return mvc.perform(get(USER_ROUTE, userId)
                    .with(processor)).andExpectAll(matchers).andReturn();
        }
    }

    @Nested
    class RouteGetProfilesAuthorizationIntegrationTest {
        private static final String USER_PROFILE_ROUTE = "/users/me";

        @Test
        @WithMockAppUser(roles = {"ADMIN", "MANAGER", "SUBMITTER", "SPECIALIST", "NONE"})
        void getProfiles_givenAllRoles_returnOk() throws Exception {
            performRequest(status().isOk());
        }

        @Test
        @WithMockAppUser(authorities = {"MANAGER", "SUBMITTER", "SPECIALIST", "NONE"})
        void getProfiles_giveAllAuthorities_returnOk() throws Exception {
            performRequest(status().isOk());
        }

        @Test
        void getProfiles_nonMockUser_returnUnauthorized() throws Exception {
            performRequest(status().isUnauthorized());
        }

        @Test
        void getProfiles_withProcessor_thenOk() throws Exception {
            passwordEncoder = new BCryptPasswordEncoder();
            SafeguardUser submitterUser = createSubmitterUser();
            AppUserDetails appUserDetails = new AppUserDetails(submitterUser);
            RequestPostProcessor user = user(appUserDetails);
            performRequest(user, status().isOk());
        }

        private void performRequest(ResultMatcher... matchers) throws Exception {
            mvc.perform(get(USER_PROFILE_ROUTE)).andExpectAll(matchers);
        }

        private void performRequest(RequestPostProcessor processor, ResultMatcher... matchers) throws Exception {
            mvc.perform(get(USER_PROFILE_ROUTE).with(processor)).andExpectAll(matchers);
        }
    }

    @Nested
    class RouteCreateUserAuthorizationIntegrationTest {
        private static final String USER_ROUTE = "/users";

        @Captor
        ArgumentCaptor<SafeguardUser> userArgumentCaptor;

        @Test
        @WithMockUser(roles = "ADMIN")
        void createUser_defaultAdmin_returnCreated() throws Exception {
            when(loginAttemptRepository.findByEmail(any())).thenReturn(Optional.of(new LoginAttempt()));
            SafeguardUser submitterUser = createSubmitterUser();
            when(userRepository.save(any())).thenReturn(submitterUser);
            UserCreatingPayload userCreatingPayload = createUserCreatingPayload();
            //then
            performRequest(userCreatingPayload, status().isCreated());
            //test
            verify(userRepository).save(userArgumentCaptor.capture());
            SafeguardUser value = userArgumentCaptor.getValue();
            assertThat(value.getRole()).isEqualTo(Role.SUBMITTER);
            verify(loginAttemptRepository, times(1)).delete(any());
        }

        @Test
        void createUser_nonMockUser_returnUnauthorized() throws Exception {
            UserCreatingPayload userCreatingPayload = createUserCreatingPayload();
            performRequest(userCreatingPayload, status().isUnauthorized());
            verify(userRepository, never()).save(any());
        }

        @Test
        @WithMockUser(roles = {"MANAGER", "SUBMITTER", "SPECIALIST", "NONE", "REFRESH", "INVALID"})
        void createUser_giveNonAdminRoles_thenForbidden() throws Exception {
            UserCreatingPayload userCreatingPayload = createUserCreatingPayload();
            MvcResult mvcResult = performRequest(userCreatingPayload, status().isForbidden());
            //test
            assertForbiddenResponse(mvcResult);
        }

        @Test
        @WithMockUser(authorities = {"MANAGER", "SUBMITTER", "SPECIALIST", "NONE", "REFRESH", "INVALID"})
        void createUser_giveNonAdminAuthority_thenForbidden() throws Exception {
            UserCreatingPayload userCreatingPayload = createUserCreatingPayload();
            MvcResult mvcResult = performRequest(userCreatingPayload, status().isForbidden());
            //test
            assertForbiddenResponse(mvcResult);
        }

        @Test
        void createUser_mockAuthorityOfOthers_thenForbidden() throws Exception {
            UserCreatingPayload userCreatingPayload = createUserCreatingPayload();
            RequestPostProcessor user = user("appuser@dqtri.com").password("******")
                    .roles("MANAGER", "SUBMITTER", "SPECIALIST", "NONE", "REFRESH", "INVALID")
                    .authorities(buildAuthorities("MANAGER", "SUBMITTER", "SPECIALIST", "NONE", "REFRESH", "INVALID"));
            MvcResult mvcResult = performRequest(userCreatingPayload, user, status().isForbidden());
            //test
            assertForbiddenResponse(mvcResult);
        }

        private void assertForbiddenResponse(MvcResult mvcResult) throws UnsupportedEncodingException, JsonProcessingException {
            String json = mvcResult.getResponse().getContentAsString();
            ErrorResponse errorResponse = new ObjectMapper().readValue(json, ErrorResponse.class);
            assertThat(errorResponse).isNotNull();
            assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.FORBIDDEN);
            assertThat(errorResponse.getMessage()).isEqualTo("Access Denied");
        }

        private UserCreatingPayload createUserCreatingPayload() {
            UserCreatingPayload userCreatingPayload = new UserCreatingPayload();
            userCreatingPayload.setEmail("newcomer@mango.dqtri.com");
            userCreatingPayload.setPassword("newcomer");
            userCreatingPayload.setRole(Role.SUBMITTER);
            return userCreatingPayload;
        }

        private MvcResult performRequest(UserCreatingPayload userCreatingPayload,
                                         ResultMatcher... matchers) throws Exception {
            return mvc.perform(post(USER_ROUTE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(createPayloadJson(userCreatingPayload)))
                    .andExpectAll(matchers).andReturn();
        }

        private MvcResult performRequest(UserCreatingPayload userCreatingPayload,
                                         RequestPostProcessor processor,
                                         ResultMatcher... matchers) throws Exception {
            return mvc.perform(post(USER_ROUTE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(createPayloadJson(userCreatingPayload))
                            .with(processor))
                    .andExpectAll(matchers).andReturn();
        }
    }

    @Nested
    class RoutUpdateUserAuthorizationIntegrationTest {

        private static final String USER_ROUTE = "/users/{userId}";

        @Test
        @WithMockUser(roles = "ADMIN")
        void updateUser_defaultAdmin_thenUpdated() throws Exception {
            SafeguardUser submitterUser = createSubmitterUser();
            when(userRepository.findById(1L)).thenReturn(Optional.of(submitterUser));
            when(userRepository.save(any())).thenReturn(submitterUser);
            UserUpdatingPayload userUpdatingPayload = createUserUpdatingPayload();
            //then
            performRequest(1L, userUpdatingPayload, status().isOk());
        }

        @Test
        void updateUser_withAdmin_thenUpdated() throws Exception {
            SafeguardUser submitterUser = createSubmitterUser();
            when(userRepository.findById(2L)).thenReturn(Optional.of(submitterUser));
            when(userRepository.save(any())).thenReturn(submitterUser);
            RequestPostProcessor user = user("admin@dqtri.com").password("******").roles("ADMIN");
            UserUpdatingPayload userUpdatingPayload = createUserUpdatingPayload();
            //then
            performRequest(2L, userUpdatingPayload, user, status().isOk());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void updateUser_mockAdminUpdateANotUpdatable_thenForbidden() throws Exception {
            SafeguardUser adminUser = createAdminUser();
            when(userRepository.findById(1L)).thenReturn(Optional.of(adminUser));
            UserUpdatingPayload userUpdatingPayload = createUserUpdatingPayload();
            //then
            performRequest(1L, userUpdatingPayload, status().isForbidden());
        }

        @Test
        void updateUser_withAdminUpdateANotUpdatable_thenForbidden() throws Exception {
            SafeguardUser adminUser = createAdminUser();
            when(userRepository.findById(2L)).thenReturn(Optional.of(adminUser));
            RequestPostProcessor user = user("admin@dqtri.com").password("******").roles("ADMIN");
            UserUpdatingPayload userUpdatingPayload = createUserUpdatingPayload();
            //then
            performRequest(2L, userUpdatingPayload, user, status().isForbidden());
        }

        @Test
        void updateUser_nonMockUser_returnUnauthorized() throws Exception {
            UserUpdatingPayload userUpdatingPayload = createUserUpdatingPayload();
            performRequest(4L, userUpdatingPayload, status().isUnauthorized());
            verify(userRepository, never()).findById(4L);
        }

        @Test
        @WithMockUser(roles = {"MANAGER", "SUBMITTER", "SPECIALIST", "NONE", "REFRESH", "INVALID"})
        void updateUser_giveNonAdminRoles_thenForbidden() throws Exception {
            UserUpdatingPayload userUpdatingPayload = createUserUpdatingPayload();
            MvcResult mvcResult = performRequest(4L, userUpdatingPayload, status().isForbidden());
            //test
            assertForbiddenResponse(mvcResult);
        }

        @Test
        @WithMockUser(authorities = {"MANAGER", "SUBMITTER", "SPECIALIST", "NONE", "REFRESH", "INVALID"})
        void updateUser_giveNonAdminAuthority_thenForbidden() throws Exception {
            UserUpdatingPayload userUpdatingPayload = createUserUpdatingPayload();
            MvcResult mvcResult = performRequest(3L, userUpdatingPayload, status().isForbidden());
            //test
            assertForbiddenResponse(mvcResult);
        }

        @Test
        void updateUser_mockAuthorityOfOthers_thenForbidden() throws Exception {
            RequestPostProcessor user = user("appuser@dqtri.com").password("******")
                    .roles("MANAGER", "SUBMITTER", "SPECIALIST", "NONE", "REFRESH", "INVALID")
                    .authorities(buildAuthorities("MANAGER", "SUBMITTER", "SPECIALIST", "NONE", "REFRESH", "INVALID"));
            UserUpdatingPayload userUpdatingPayload = createUserUpdatingPayload();
            MvcResult mvcResult = performRequest(4L, userUpdatingPayload, user, status().isForbidden());
            //test
            assertForbiddenResponse(mvcResult);
        }

        private void assertForbiddenResponse(MvcResult mvcResult) throws UnsupportedEncodingException, JsonProcessingException {
            String json = mvcResult.getResponse().getContentAsString();
            ErrorResponse errorResponse = new ObjectMapper().readValue(json, ErrorResponse.class);
            assertThat(errorResponse).isNotNull();
            assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.FORBIDDEN);
            assertThat(errorResponse.getMessage()).isEqualTo("Access Denied");
        }

        private UserUpdatingPayload createUserUpdatingPayload() {
            UserUpdatingPayload userUpdatingPayload = new UserUpdatingPayload();
            userUpdatingPayload.setRole(Role.MANAGER);
            return userUpdatingPayload;
        }

        private MvcResult performRequest(long userId,
                                         UserUpdatingPayload userUpdatingPayload,
                                         ResultMatcher... matchers) throws Exception {
            return mvc.perform(put(USER_ROUTE, userId)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(createPayloadJson(userUpdatingPayload)))
                    .andExpectAll(matchers).andReturn();
        }

        private MvcResult performRequest(long userId,
                                         UserUpdatingPayload userUpdatingPayload,
                                         RequestPostProcessor processor,
                                         ResultMatcher... matchers) throws Exception {
            return mvc.perform(put(USER_ROUTE, userId)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(createPayloadJson(userUpdatingPayload))
                            .with(processor))
                    .andExpectAll(matchers).andReturn();
        }
    }

    @Nested
    class RoutUpdateUserPasswordAuthorizationIntegrationTest {

        private static final String USER_ROUTE = "/users/{userId}/password";

        @Test
        @WithMockUser(roles = "ADMIN")
        void updateUserPassword_defaultAdmin_thenUpdated() throws Exception {
            SafeguardUser submitterUser = createSubmitterUser();
            when(userRepository.findById(1L)).thenReturn(Optional.of(submitterUser));
            ResetPasswordPayload resetPasswordPayload = createResetPasswordPayload();
            //then
            performRequest(1L, resetPasswordPayload, status().isOk());
        }

        @Test
        void updateUserPassword_withAdmin_thenUpdated() throws Exception {
            SafeguardUser submitterUser = createSubmitterUser();
            when(userRepository.findById(2L)).thenReturn(Optional.of(submitterUser));
            RequestPostProcessor user = user("admin@dqtri.com").password("******").roles("ADMIN");
            ResetPasswordPayload resetPasswordPayload = createResetPasswordPayload();
            //then
            performRequest(2L, resetPasswordPayload, user, status().isOk());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void updateUserPassword_mockAdminUpdateANotUpdatable_thenForbidden() throws Exception {
            SafeguardUser adminUser = createAdminUser();
            when(userRepository.findById(1L)).thenReturn(Optional.of(adminUser));
            ResetPasswordPayload resetPasswordPayload = createResetPasswordPayload();
            //then
            performRequest(1L, resetPasswordPayload, status().isForbidden());
        }

        @Test
        void updateUserPassword_withAdminUpdateANotUpdatable_thenForbidden() throws Exception {
            SafeguardUser adminUser = createAdminUser();
            when(userRepository.findById(2L)).thenReturn(Optional.of(adminUser));
            RequestPostProcessor user = user("admin@dqtri.com").password("******").roles("ADMIN");
            ResetPasswordPayload resetPasswordPayload = createResetPasswordPayload();
            //then
            performRequest(2L, resetPasswordPayload, user, status().isForbidden());
        }

        @Test
        void updateUserPassword_nonMockUser_returnUnauthorized() throws Exception {
            ResetPasswordPayload resetPasswordPayload = createResetPasswordPayload();
            performRequest(4L, resetPasswordPayload, status().isUnauthorized());
            verify(userRepository, never()).findById(4L);
        }

        @Test
        @WithMockUser(roles = {"MANAGER", "SUBMITTER", "SPECIALIST", "NONE", "REFRESH", "INVALID"})
        void updateUserPassword_giveNonAdminRoles_thenForbidden() throws Exception {
            ResetPasswordPayload resetPasswordPayload = createResetPasswordPayload();
            MvcResult mvcResult = performRequest(4L, resetPasswordPayload, status().isForbidden());
            //test
            assertForbiddenResponse(mvcResult);
        }

        @Test
        @WithMockUser(authorities = {"MANAGER", "SUBMITTER", "SPECIALIST", "NONE", "REFRESH", "INVALID"})
        void updateUserPassword_giveNonAdminAuthority_thenForbidden() throws Exception {
            ResetPasswordPayload resetPasswordPayload = createResetPasswordPayload();
            MvcResult mvcResult = performRequest(3L, resetPasswordPayload, status().isForbidden());
            //test
            assertForbiddenResponse(mvcResult);
        }

        @Test
        void updateUserPassword_mockAuthorityOfOthers_thenForbidden() throws Exception {
            RequestPostProcessor user = user("appuser@dqtri.com").password("******")
                    .roles("MANAGER", "SUBMITTER", "SPECIALIST", "NONE", "REFRESH", "INVALID")
                    .authorities(buildAuthorities("MANAGER", "SUBMITTER", "SPECIALIST", "NONE", "REFRESH", "INVALID"));
            ResetPasswordPayload resetPasswordPayload = createResetPasswordPayload();
            MvcResult mvcResult = performRequest(4L, resetPasswordPayload, user, status().isForbidden());
            //test
            assertForbiddenResponse(mvcResult);
        }

        private void assertForbiddenResponse(MvcResult mvcResult) throws UnsupportedEncodingException, JsonProcessingException {
            String json = mvcResult.getResponse().getContentAsString();
            ErrorResponse errorResponse = new ObjectMapper().readValue(json, ErrorResponse.class);
            assertThat(errorResponse).isNotNull();
            assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.FORBIDDEN);
            assertThat(errorResponse.getMessage()).isEqualTo("Access Denied");
        }

        private ResetPasswordPayload createResetPasswordPayload() {
            ResetPasswordPayload resetPasswordPayload = new ResetPasswordPayload();
            resetPasswordPayload.setPassword("******");
            return resetPasswordPayload;
        }

        private MvcResult performRequest(long userId,
                                         ResetPasswordPayload resetPasswordPayload,
                                         ResultMatcher... matchers) throws Exception {
            return mvc.perform(put(USER_ROUTE, userId)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(createPayloadJson(resetPasswordPayload)))
                    .andExpectAll(matchers).andReturn();
        }

        private MvcResult performRequest(long userId,
                                         ResetPasswordPayload resetPasswordPayload,
                                         RequestPostProcessor processor,
                                         ResultMatcher... matchers) throws Exception {
            return mvc.perform(put(USER_ROUTE, userId)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(createPayloadJson(resetPasswordPayload))
                            .with(processor))
                    .andExpectAll(matchers).andReturn();
        }
    }

    @Nested
    class RoutDeleteUserAuthorizationIntegrationTest {

        private static final String USER_ROUTE = "/users/{userId}";

        @Test
        @WithMockUser(roles = "ADMIN")
        void deleteUser_defaultAdmin_thenDeleted() throws Exception {
            SafeguardUser submitterUser = createSubmitterUser();
            when(userRepository.findById(1L)).thenReturn(Optional.of(submitterUser));
            //then
            performRequest(1L, status().isNoContent());
        }

        @Test
        void deleteUser_withAdmin_thenDeleted() throws Exception {
            SafeguardUser submitterUser = createSubmitterUser();
            when(userRepository.findById(2L)).thenReturn(Optional.of(submitterUser));
            RequestPostProcessor user = user("admin@dqtri.com").password("******").roles("ADMIN");
            //then
            performRequest(2L, user, status().isNoContent());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void deleteUser_mockAdminUpdateANotUpdatable_thenForbidden() throws Exception {
            SafeguardUser adminUser = createAdminUser();
            when(userRepository.findById(1L)).thenReturn(Optional.of(adminUser));
            //then
            performRequest(1L, status().isForbidden());
        }

        @Test
        void deleteUser_withAdminUpdateANotUpdatable_thenForbidden() throws Exception {
            SafeguardUser adminUser = createAdminUser();
            when(userRepository.findById(2L)).thenReturn(Optional.of(adminUser));
            RequestPostProcessor user = user("admin@dqtri.com").password("******").roles("ADMIN");
            //then
            performRequest(2L, user, status().isForbidden());
        }

        @Test
        void deleteUser_nonMockUser_returnUnauthorized() throws Exception {
            performRequest(4L, status().isUnauthorized());
            verify(userRepository, never()).findById(4L);
        }

        @Test
        @WithMockUser(roles = {"MANAGER", "SUBMITTER", "SPECIALIST", "NONE", "REFRESH", "INVALID"})
        void deleteUser_giveNonAdminRoles_thenForbidden() throws Exception {
            MvcResult mvcResult = performRequest(4L, status().isForbidden());
            //test
            assertForbiddenResponse(mvcResult);
        }

        @Test
        @WithMockUser(authorities = {"MANAGER", "SUBMITTER", "SPECIALIST", "NONE", "REFRESH", "INVALID"})
        void deleteUser_giveNonAdminAuthority_thenForbidden() throws Exception {
            MvcResult mvcResult = performRequest(3L, status().isForbidden());
            //test
            assertForbiddenResponse(mvcResult);
        }

        @Test
        void deleteUser_mockAuthorityOfOthers_thenForbidden() throws Exception {
            RequestPostProcessor user = user("appuser@dqtri.com").password("******")
                    .roles("MANAGER", "SUBMITTER", "SPECIALIST", "NONE", "REFRESH", "INVALID")
                    .authorities(buildAuthorities("MANAGER", "SUBMITTER", "SPECIALIST", "NONE", "REFRESH", "INVALID"));
            MvcResult mvcResult = performRequest(4L, user, status().isForbidden());
            //test
            assertForbiddenResponse(mvcResult);
        }

        private void assertForbiddenResponse(MvcResult mvcResult) throws UnsupportedEncodingException, JsonProcessingException {
            String json = mvcResult.getResponse().getContentAsString();
            ErrorResponse errorResponse = new ObjectMapper().readValue(json, ErrorResponse.class);
            assertThat(errorResponse).isNotNull();
            assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.FORBIDDEN);
            assertThat(errorResponse.getMessage()).isEqualTo("Access Denied");
        }

        private MvcResult performRequest(long userId, ResultMatcher... matchers) throws Exception {
            return mvc.perform(delete(USER_ROUTE, userId))
                    .andExpectAll(matchers).andReturn();
        }

        private MvcResult performRequest(long userId, RequestPostProcessor processor, ResultMatcher... matchers) throws Exception {
            return mvc.perform(delete(USER_ROUTE, userId).with(processor))
                    .andExpectAll(matchers).andReturn();
        }
    }

    private SafeguardUser createSubmitterUser() {
        SafeguardUser safeguardUser = new SafeguardUser();
        safeguardUser.setEmail("newcomer@dqtri.com");
        safeguardUser.setPassword(passwordEncoder.encode("newcomer"));
        safeguardUser.setRole(Role.SUBMITTER);
        return safeguardUser;
    }

    private SafeguardUser createAdminUser() {
        SafeguardUser safeguardUser = new SafeguardUser();
        safeguardUser.setEmail("admin@dqtri.com");
        safeguardUser.setPassword(passwordEncoder.encode("newcomer"));
        safeguardUser.setRole(Role.ADMIN);
        return safeguardUser;
    }
}
