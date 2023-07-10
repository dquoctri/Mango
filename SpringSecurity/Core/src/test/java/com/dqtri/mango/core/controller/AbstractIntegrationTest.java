/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.core.controller;

import com.dqtri.mango.core.repository.BlackListRefreshTokenRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@WebMvcTest
public abstract class AbstractIntegrationTest {

    @MockBean
    protected AuthenticationManager authenticationManager;
    @MockBean
    protected AuthenticationProvider authenticationProvider;
    @MockBean
    protected UserDetailsService userDetailsService;
    @MockBean
    protected BlackListRefreshTokenRepository blackListRefreshTokenRepository;

    @Autowired
    protected MockMvc mvc;

    protected String createPayloadJson(Object value) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(value);
    }

    protected List<GrantedAuthority> buildAuthorities(String... authorities){
        if (authorities == null) return new ArrayList<>();
        return Arrays.stream(authorities).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }
}
