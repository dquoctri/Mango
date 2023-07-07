/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.submission.controller;

import com.dqtri.mango.submission.common.WithMockAppUser;
import com.dqtri.mango.submission.config.SecurityConfig;
import com.dqtri.mango.submission.model.SafeguardUser;
import com.dqtri.mango.submission.model.dto.payload.UserCreatingPayload;
import com.dqtri.mango.submission.model.dto.payload.UserUpdatingPayload;
import com.dqtri.mango.submission.model.dto.response.UserResponse;
import com.dqtri.mango.submission.model.enums.Role;
import com.dqtri.mango.submission.repository.UserRepository;
import com.dqtri.mango.submission.security.AppUserDetails;
import com.dqtri.mango.submission.security.permissions.UpdatableResourcePermission;
import com.dqtri.mango.submission.util.PageDeserializer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Nested
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = {UserController.class},
        includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = {SecurityConfig.class, UpdatableResourcePermission.class}))
public class UserControllerIntegrationTest extends AbstractIntegrationTest {
    @MockBean
    private PasswordEncoder passwordEncoder;
    @MockBean
    private UserRepository userRepository;

    @Nested
    class RouteGetAllUsersFeatureIntegrationTest {
        private static final String USER_ROUTE = "/users";
        private ObjectMapper objectMapper;

        @BeforeEach
        public void setup() {
            objectMapper = new ObjectMapper();
            SimpleModule module = new SimpleModule();
            module.addDeserializer(Page.class, new PageDeserializer<>(UserResponse.class));
            objectMapper.registerModule(module);
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void getAllUsers_hasNoUser_returnEmptyPagination() throws Exception {
            Pageable pageable = PageRequest.of(0, 25, Sort.by(Sort.DEFAULT_DIRECTION, "pk"));
            Page<SafeguardUser> usersPage = new PageImpl<>(new ArrayList<>(), pageable, 0);
            when(userRepository.findByRole(null, pageable)).thenReturn(usersPage);
            //then
            MvcResult mvcResult = performRequest(status().isOk());
            String json = mvcResult.getResponse().getContentAsString();
            Page<UserResponse> result = objectMapper.readValue(json, new TypeReference<>() {
            });
            //test
            assertUserPageResponse(usersPage, result);
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void getAllUsers_defaultOneUsers_returnPaginationWithSize() throws Exception {
            Pageable pageable = PageRequest.of(0, 25, Sort.by(Sort.DEFAULT_DIRECTION, "pk"));
            List<SafeguardUser> users = new ArrayList<>(createUserList(1));
            Page<SafeguardUser> usersPage = new PageImpl<>(users, pageable, 1);
            when(userRepository.findByRole(null, pageable)).thenReturn(usersPage);
            //then
            MvcResult mvcResult = performRequest(status().isOk());
            String json = mvcResult.getResponse().getContentAsString();
            Page<UserResponse> result = objectMapper.readValue(json, new TypeReference<>() {
            });
            //test
            assertUserPageResponse(usersPage, result);
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void getAllUsers_AllRoleUsers_returnPaginationWithSize5() throws Exception {
            int numberOfUsers = 5;
            Pageable pageable = PageRequest.of(0, 25, Sort.by(Sort.DEFAULT_DIRECTION, "pk"));
            List<SafeguardUser> users = new ArrayList<>(createUserList(numberOfUsers));
            Page<SafeguardUser> usersPage = new PageImpl<>(users, pageable, numberOfUsers);
            when(userRepository.findByRole(null, pageable)).thenReturn(usersPage);
            //then
            MvcResult mvcResult = performRequest(status().isOk());
            String json = mvcResult.getResponse().getContentAsString();
            Page<UserResponse> result = objectMapper.readValue(json, new TypeReference<>() {
            });
            //test
            assertUserPageResponse(usersPage, result);
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void getAllUsers_withPageNumberAndSize_returnPaginationWithSize5() throws Exception {
            int pageSize = 25;
            int pageNumber = 1;
            int numberOfUsers = 5;
            Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.DEFAULT_DIRECTION, "pk"));
            List<SafeguardUser> users = new ArrayList<>(createUserList(numberOfUsers));
            Page<SafeguardUser> usersPage = new PageImpl<>(users, pageable, numberOfUsers);
            when(userRepository.findByRole(null, pageable)).thenReturn(usersPage);
            //then
            MvcResult mvcResult = performRequest(pageNumber, pageSize, status().isOk());
            String json = mvcResult.getResponse().getContentAsString();
            Page<UserResponse> result = objectMapper.readValue(json, new TypeReference<>() {
            });
            //test
            assertUserPageResponse(usersPage, result);
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void getAllUsers_pageSizeZero_thenBadRequest() throws Exception {
            int pageSize = 0;
            int pageNumber = 152;
            //then
            MvcResult mvcResult = performRequest(pageNumber, pageSize, status().isBadRequest());
            String json = mvcResult.getResponse().getContentAsString();
            ProblemDetail problemDetail = objectMapper.readValue(json, ProblemDetail.class);
            //test
            assertBadRequest(problemDetail);
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void getAllUsers_negativePageSize_thenBadRequest() throws Exception {
            int pageSize = -25;
            int pageNumber = 152;
            //then
            MvcResult mvcResult = performRequest(pageNumber, pageSize, status().isBadRequest());
            String json = mvcResult.getResponse().getContentAsString();
            ProblemDetail problemDetail = objectMapper.readValue(json, ProblemDetail.class);
            //test
            assertBadRequest(problemDetail);
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void getAllUsers_negativePageNumber_thenBadRequest() throws Exception {
            int pageSize = 25;
            int pageNumber = -1;
            //then
            MvcResult mvcResult = performRequest(pageNumber, pageSize, status().isBadRequest());
            String json = mvcResult.getResponse().getContentAsString();
            ProblemDetail problemDetail = objectMapper.readValue(json, ProblemDetail.class);
            //test
            assertBadRequest(problemDetail);
        }

        private void assertBadRequest(ProblemDetail problemDetail){
            assertThat(problemDetail).isNotNull();
            assertThat(problemDetail.getStatus()).isEqualTo(400);
            assertThat(problemDetail.getTitle()).isEqualTo("Bad Request");
            assertThat(problemDetail.getDetail()).isEqualTo("Invalid request content.");
        }

        private MvcResult performRequest(ResultMatcher... matchers) throws Exception {
            return mvc.perform(get(USER_ROUTE)).andExpectAll(matchers).andReturn();
        }

        private MvcResult performRequest(int pageNumber, int pageSize, ResultMatcher... matchers) throws Exception {
            return mvc.perform(get(USER_ROUTE)
                            .param("pageNumber", "" + pageNumber)
                            .param("pageSize", "" + pageSize))
                    .andExpectAll(matchers).andReturn();
        }

        private void assertUserPageResponse(Page<SafeguardUser> usersPage, Page<UserResponse> result) {
            //test
            assertThat(result).isNotNull();
            assertThat(result.getSize()).isEqualTo(usersPage.getSize());
            assertThat(result.getNumber()).isEqualTo(usersPage.getNumber());
            assertThat(result.getTotalPages()).isEqualTo(usersPage.getTotalPages());
            assertThat(result.getTotalElements()).isEqualTo(usersPage.getTotalElements());
            int size = usersPage.getContent().size();
            assertThat(result.getContent()).hasSize(size);
            for (int i = 0; i < size; i++) {
                assertUserResponse(usersPage.getContent().get(i), result.getContent().get(i));
            }
        }

        private void assertUserResponse(SafeguardUser user, UserResponse response) {
            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(user.getPk());
            assertThat(response.getEmail()).isEqualTo(user.getEmail());
            assertThat(response.getRole()).isEqualTo(user.getRole());
        }

        private List<SafeguardUser> createUserList(int numberOfUsers) {
            List<SafeguardUser> users = new ArrayList<>();
            Role[] roles = Role.values();
            for (int i = 0; i < numberOfUsers; i++) {
                SafeguardUser safeguardUser = new SafeguardUser();
                safeguardUser.setPk((long) i);
                safeguardUser.setEmail(String.format("user%s@dqtri.com", i));
                safeguardUser.setRole(roles[i % 5]);
            }
            return users;
        }
    }

    @Nested
    class RouteGetUserFeatureIntegrationTest {
        private static final String USER_PROFILE_ROUTE = "/users/me";

        @Test
        @WithMockAppUser(email = "mango@dqtri.com", role = Role.SUBMITTER)
        void getProfiles_mockUser_thenUserResponse() throws Exception {
            MvcResult mvcResult = performRequest(status().isOk());
            String json = mvcResult.getResponse().getContentAsString();
            UserResponse userResponse = new ObjectMapper().readValue(json, UserResponse.class);
            assertThat(userResponse).isNotNull();
            assertThat(userResponse.getEmail()).isEqualTo("mango@dqtri.com");
            assertThat(userResponse.getRole()).isEqualTo(Role.SUBMITTER);
        }

        private MvcResult performRequest(ResultMatcher... matchers) throws Exception {
            return mvc.perform(get(USER_PROFILE_ROUTE))
                    .andExpectAll(matchers).andReturn();
        }
    }

    @Nested
    class RouteGetProfilesFeatureIntegrationTest {
        private static final String USER_PROFILE_ROUTE = "/users/me";

        @Test
        @WithMockAppUser(email = "admin@dqtri.com", role = Role.ADMIN)
        void getProfiles_givenAdmin_returnUserResponse() throws Exception {
            MvcResult result = performRequest(status().isOk());
            assertCurrentUserResponse(result, "admin@dqtri.com", Role.ADMIN);
        }

        @Test
        @WithMockAppUser(email = "manager@dqtri.com", role = Role.MANAGER)
        void getProfiles_givenManager_returnUserResponse() throws Exception {
            MvcResult result = performRequest(status().isOk());
            assertCurrentUserResponse(result, "manager@dqtri.com", Role.MANAGER);
        }

        @Test
        @WithMockAppUser(email = "specialist@dqtri.com", role = Role.SPECIALIST)
        void getProfiles_givenSpecialist_returnUserResponse() throws Exception {
            MvcResult result = performRequest(status().isOk());
            assertCurrentUserResponse(result, "specialist@dqtri.com", Role.SPECIALIST);
        }

        @Test
        @WithMockAppUser(email = "submitter@dqtri.com", role = Role.SUBMITTER)
        void getProfiles_givenSubmitter_returnUserResponse() throws Exception {
            MvcResult result = performRequest(status().isOk());
            assertCurrentUserResponse(result, "submitter@dqtri.com", Role.SUBMITTER);
        }

        @Test
        @WithMockAppUser(email = "none@dqtri.com", role = Role.NONE)
        void getProfiles_givenNone_returnUserResponse() throws Exception {
            MvcResult result = performRequest(status().isOk());
            assertCurrentUserResponse(result, "none@dqtri.com", Role.NONE);
        }

        @ParameterizedTest
        @CsvSource({
                "admin@dqtri.com, ADMIN",
                "manager@dqtri.com, MANAGER",
                "specialist@dqtri.com, SPECIALIST",
                "submitter@dqtri.com, SUBMITTER",
                "none@dqtri.com, NONE",
        })
        void getProfiles_withProcessor_thenOk(String email, Role role) throws Exception {
            passwordEncoder = new BCryptPasswordEncoder();
            SafeguardUser submitterUser = createSafeguardUser(email, role);
            AppUserDetails appUserDetails = new AppUserDetails(submitterUser);
            RequestPostProcessor user = user(appUserDetails);
            MvcResult result = performRequest(user, status().isOk());
            assertCurrentUserResponse(result, submitterUser.getEmail(), submitterUser.getRole());
        }

        private void assertCurrentUserResponse(MvcResult result, String email, Role role) throws UnsupportedEncodingException, JsonProcessingException {
            String json = result.getResponse().getContentAsString();
            UserResponse userResponse = new ObjectMapper().readValue(json, UserResponse.class);
            assertThat(userResponse).isNotNull();
            assertThat(userResponse.getEmail()).isEqualTo(email);
            assertThat(userResponse.getRole()).isEqualTo(role);
        }

        private MvcResult performRequest(ResultMatcher... matchers) throws Exception {
            return mvc.perform(get(USER_PROFILE_ROUTE))
                    .andExpectAll(matchers).andReturn();
        }

        private MvcResult performRequest(RequestPostProcessor processor,
                                    ResultMatcher... matchers) throws Exception {
            return mvc.perform(get(USER_PROFILE_ROUTE)
                    .with(processor)).andExpectAll(matchers).andReturn();
        }
    }

    @Nested
    class RouteCreateUserFeatureIntegrationTest {
        private static final String USER_ROUTE = "/users";

        @Captor
        ArgumentCaptor<SafeguardUser> userArgumentCaptor;

        @ParameterizedTest
        @CsvSource({
                "admin@dqtri.com, ADMIN",
                "manager@dqtri.com, MANAGER",
                "specialist@dqtri.com, SPECIALIST",
                "submitter@dqtri.com, SUBMITTER",
                "none@dqtri.com, NONE",
        })
        @WithMockUser(roles = "ADMIN")
        void createUser_createAppUser_returnCreated(String email, Role role) throws Exception {
            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            SafeguardUser submitterUser = createSafeguardUser(email, role);
            when(userRepository.save(any())).thenReturn(submitterUser);
            UserCreatingPayload userCreatingPayload = createUserCreatingPayload(email, role);
            //then
            MvcResult result = performRequest(userCreatingPayload, status().isCreated());
            //test
            assertUserResponse(result, email, role);
            verify(userRepository).save(userArgumentCaptor.capture());
            SafeguardUser value = userArgumentCaptor.getValue();
            assertThat(value.getEmail()).isEqualTo(email);
            assertThat(value.getRole()).isEqualTo(role);

        }

        @ParameterizedTest
        @CsvSource({
                "admin@dqtri.com, ADMIN",
                "manager@dqtri.com, MANAGER",
                "specialist@dqtri.com, SPECIALIST",
                "submitter@dqtri.com, SUBMITTER",
                "none@dqtri.com, NONE",
        })
        @WithMockUser(roles = "ADMIN")
        void createUser_givenExistedLoginAttempt_returnCreated(String email, Role role) throws Exception {
            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            SafeguardUser submitterUser = createSafeguardUser(email, role);
            when(userRepository.save(any())).thenReturn(submitterUser);
            UserCreatingPayload userCreatingPayload = createUserCreatingPayload(email, role);
            //then
            MvcResult result = performRequest(userCreatingPayload, status().isCreated());
            //test
            assertUserResponse(result, email, role);
            verify(userRepository).save(userArgumentCaptor.capture());
            SafeguardUser value = userArgumentCaptor.getValue();
            assertThat(value.getEmail()).isEqualTo(email);
            assertThat(value.getRole()).isEqualTo(role);
        }

        private void assertUserResponse(MvcResult result, String email, Role role) throws UnsupportedEncodingException, JsonProcessingException {
            String json = result.getResponse().getContentAsString();
            UserResponse userResponse = new ObjectMapper().readValue(json, UserResponse.class);
            assertThat(userResponse).isNotNull();
            assertThat(userResponse.getEmail()).isEqualTo(email);
            assertThat(userResponse.getRole()).isEqualTo(role);
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void createUser_givenEmptyPayload_thenBadRequest() throws Exception {
            UserCreatingPayload userCreatingPayload = new UserCreatingPayload();
            //then
            performRequest(userCreatingPayload, status().isBadRequest());
            //test
            verify(userRepository, never()).save(any());
        }

        @ParameterizedTest
        @ValueSource(strings = {"invalidEmailFormat", "invalidEmailFormat@", "@invalidEmailFormat", "mangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomangomango@dqtri.com"})
        @WithMockUser(roles = "ADMIN")
        void createUser_givenInvalidEmailFormat_thenBadRequest(String invalidEmail) throws Exception {
            UserCreatingPayload userCreatingPayload = new UserCreatingPayload();
            userCreatingPayload.setEmail(invalidEmail);
            //then
            performRequest(userCreatingPayload, status().isBadRequest());
            //test
            verify(userRepository, never()).save(any());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void createUser_givenNonPassword_thenBadRequest() throws Exception {
            UserCreatingPayload userCreatingPayload = new UserCreatingPayload();
            userCreatingPayload.setEmail("newcomer@mango.dqtri.com");
            //then
            performRequest(userCreatingPayload, status().isBadRequest());
            //test
            verify(userRepository, never()).save(any());
        }

        @ParameterizedTest
        @ValueSource(strings = {"st", "", "       ", "more_than_24_characters_too_long_password"})
        @WithMockUser(roles = "ADMIN")
        void createUser_givenInvalidPasswordFormat_thenBadRequest(String password) throws Exception {
            UserCreatingPayload userCreatingPayload = new UserCreatingPayload();
            userCreatingPayload.setEmail("newcomer@mango.dqtri.com");
            //then
            performRequest(userCreatingPayload, status().isBadRequest());
            //test
            verify(userRepository, never()).save(any());
        }


        @Test
        @WithMockUser(roles = "ADMIN")
        void createUser_mockExitedEmail_thenThrowConflictException() throws Exception {
            UserCreatingPayload userCreatingPayload = createUserCreatingPayload("mango@dqtri.com", Role.ADMIN);
            when(userRepository.existsByEmail(anyString())).thenReturn(true);
            //then
            performRequest(userCreatingPayload, status().isConflict());
            //test
            verify(userRepository, never()).save(any());
        }

        private UserCreatingPayload createUserCreatingPayload(String email, Role role){
            UserCreatingPayload userCreatingPayload = new UserCreatingPayload();
            userCreatingPayload.setEmail(email);
            userCreatingPayload.setRole(role);
            return userCreatingPayload;
        }

        private MvcResult performRequest(UserCreatingPayload userCreatingPayload,
                                         ResultMatcher... matchers) throws Exception {
            return mvc.perform(post(USER_ROUTE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(createPayloadJson(userCreatingPayload)))
                    .andExpectAll(matchers).andReturn();
        }
    }

    @Nested
    class RoutUpdateUserFeatureIntegrationTest {

        private static final String USER_ROUTE = "/users/{userId}";

        @ParameterizedTest
        @CsvSource({
                "1, existing@dqtri.com, ADMIN",
                "2, manager@dqtri.com, MANAGER",
                "3, specialist@dqtri.com, SPECIALIST",
                "4, submitter@dqtri.com, SUBMITTER",
                "5, none@dqtri.com, NONE",
        })
        @WithMockUser(roles = "ADMIN")
        void updateUser_updateRole_thenUpdated(long id, String email, Role role) throws Exception {
            SafeguardUser submitterUser = createSafeguardUser(email, Role.NONE);
            when(userRepository.findById(id)).thenReturn(Optional.of(submitterUser));
            when(userRepository.save(any())).thenReturn(submitterUser);
            UserUpdatingPayload userUpdatingPayload = createUserUpdatingPayload(role);
            //then
            MvcResult result = performRequest(id, userUpdatingPayload, status().isOk());
            assertUserResponse(result, email, role);
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void updateUser_nonExistingUser_thenNotFound() throws Exception {
            when(userRepository.findById(any())).thenReturn(Optional.empty());
            UserUpdatingPayload userUpdatingPayload = createUserUpdatingPayload(Role.SUBMITTER);
            //then
            performRequest(4L, userUpdatingPayload, status().isNotFound());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void updateUser_emptyBody_thenUpdated() throws Exception {
            UserUpdatingPayload userUpdatingPayload = new UserUpdatingPayload();
            //then
            MvcResult result = performRequest(5L, userUpdatingPayload, status().isBadRequest());
            //test
            assertBadRequest(result);
        }

        private UserUpdatingPayload createUserUpdatingPayload(Role role) {
            UserUpdatingPayload userUpdatingPayload = new UserUpdatingPayload();
            userUpdatingPayload.setRole(role);
            return userUpdatingPayload;
        }

        private void assertUserResponse(MvcResult result, String email, Role role) throws UnsupportedEncodingException, JsonProcessingException {
            String json = result.getResponse().getContentAsString();
            UserResponse userResponse = new ObjectMapper().readValue(json, UserResponse.class);
            assertThat(userResponse).isNotNull();
            assertThat(userResponse.getEmail()).isEqualTo(email);
            assertThat(userResponse.getRole()).isEqualTo(role);
        }

        private void assertBadRequest(MvcResult result) throws UnsupportedEncodingException, JsonProcessingException {
            String json = result.getResponse().getContentAsString();
            ProblemDetail problemDetail = new ObjectMapper().readValue(json, ProblemDetail.class);
            assertThat(problemDetail).isNotNull();
            assertThat(problemDetail.getStatus()).isEqualTo(400);
            assertThat(problemDetail.getTitle()).isEqualTo("Bad Request");
            assertThat(problemDetail.getDetail()).isEqualTo("Invalid request content.");
        }

        private MvcResult performRequest(long userId,
                                         UserUpdatingPayload userUpdatingPayload,
                                         ResultMatcher... matchers) throws Exception {
            return mvc.perform(put(USER_ROUTE, userId)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(createPayloadJson(userUpdatingPayload)))
                    .andExpectAll(matchers).andReturn();
        }
    }

    private SafeguardUser createSafeguardUser(String email, Role role) {
        SafeguardUser safeguardUser = new SafeguardUser();
        safeguardUser.setEmail(email);
        safeguardUser.setRole(role);
        return safeguardUser;
    }
}
