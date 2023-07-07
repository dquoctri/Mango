/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.submission.security.impl;

import com.dqtri.mango.submission.model.SafeguardUser;
import com.dqtri.mango.submission.model.enums.Role;
import com.dqtri.mango.submission.repository.UserRepository;
import com.dqtri.mango.submission.security.AppUserDetails;
import com.dqtri.mango.submission.security.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {UserDetailsServiceImpl.class})
public class UserDetailsServiceImplTest {

    @MockBean
    UserRepository userRepository;

    private UserDetailsService userDetailsService;

    @BeforeEach
    public void setup(){
        userDetailsService = new UserDetailsServiceImpl(userRepository);
    }

    @Test
    public void loadUserByUsername_givenExistUsername_returnsConfigUser() {
        SafeguardUser safeguardUser = createSubmitterUser();
        when(userRepository.findByEmail("submitter@dqtri.com")).thenReturn(Optional.of(safeguardUser));
        UserDetails userDetails = userDetailsService.loadUserByUsername("submitter@dqtri.com");
        assertThat(userDetails).isNotNull();
        assertThat(userDetails).isInstanceOf(AppUserDetails.class);
        AppUserDetails basicUserDetails = (AppUserDetails)userDetails;
        assertThat(basicUserDetails.getSafeguardUser()).isNotNull();
        assertThat(basicUserDetails.getSafeguardUser().getRole()).isEqualTo(Role.SUBMITTER);
    }

    private SafeguardUser createSubmitterUser() {
        SafeguardUser safeguardUser = new SafeguardUser();
        safeguardUser.setEmail("submitter@dqtri.com");
        safeguardUser.setRole(Role.SUBMITTER);
        return safeguardUser;
    }

    @Test
    public void loadUserByUsername_givenNOTExistUsername_thenThrowNotFound() {
        Executable executable = () -> userDetailsService.loadUserByUsername("no@dqtri.com");
        //test
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                executable,
                String.format("Username not found: %s", "no@dqtri.com")
        );
        assertThat(exception.getMessage()).isEqualTo("Username not found: no@dqtri.com");
    }

    @Test
    public void loadUserByUsername_givenNullUsername_thenThrowNotFound() {
        when(userRepository.findByEmail("submitter@dqtri.com"))
                .thenThrow(new UsernameNotFoundException(String.format("Username not found: %s", "null")));
        Executable executable = () -> userDetailsService.loadUserByUsername(null);
        //test
        assertThrows(
                UsernameNotFoundException.class,
                executable,
                String.format("Username not found: %s", "null")
        );
    }
}
