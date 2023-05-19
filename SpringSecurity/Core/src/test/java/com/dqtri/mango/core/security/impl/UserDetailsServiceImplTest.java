/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.core.security.impl;

import com.dqtri.mango.core.model.enums.Role;
import com.dqtri.mango.core.repository.UserRepository;
import com.dqtri.mango.core.security.CoreUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = {CoreUserDetailsServiceImpl.class})
public class UserDetailsServiceImplTest {

    @Mock
    UserRepository userRepository;

    private UserDetailsService userDetailsService;

    @BeforeEach
    public void setup(){
        userDetailsService = new CoreUserDetailsServiceImpl(userRepository);
    }

    @Test
    public void loadUserByUsername_givenExistUsername_returnsConfigUser() {
        UserDetails userDetails = userDetailsService.loadUserByUsername("submitter@mango.dqtri.com");
        assertThat(userDetails).isNotNull();
        assertThat(userDetails).isInstanceOf(CoreUserDetails.class);
        CoreUserDetails coreUserDetails = (CoreUserDetails)userDetails;
        assertThat(coreUserDetails.getCoreUser()).isNotNull();
        assertThat(coreUserDetails.getCoreUser().getRole()).isEqualTo(Role.SUBMITTER);
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
        Executable executable = () -> userDetailsService.loadUserByUsername(null);
        //test
        assertThrows(
                UsernameNotFoundException.class,
                executable,
                String.format("Username not found: %s", "null")
        );
    }
}
