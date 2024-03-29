package com.dqtri.mango.configuring.security;

import com.dqtri.mango.configuring.model.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = {UserDetailsServiceImpl.class})
public class UserDetailsServiceImplTest {
    UserDetailsService userDetailsService = new UserDetailsServiceImpl();

    @Test
    public void loadUserByUsername_givenExistUsername_returnsConfigUser() {
        UserDetails userDetails = userDetailsService.loadUserByUsername("submitter@mango.dqtri.com");
        assertThat(userDetails).isNotNull();
        assertThat(userDetails).isInstanceOf(CustomUserDetails.class);
        CustomUserDetails customUserDetails = (CustomUserDetails)userDetails;
        assertThat(customUserDetails.getConfigUser()).isNotNull();
        assertThat(customUserDetails.getConfigUser().getRole()).isEqualTo(Role.SUBMITTER);
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
