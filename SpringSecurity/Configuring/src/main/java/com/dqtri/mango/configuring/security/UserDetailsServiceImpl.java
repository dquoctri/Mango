package com.dqtri.mango.configuring.security;

import com.dqtri.mango.configuring.model.ConfigUser;
import com.dqtri.mango.configuring.model.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        if (!"submitter@mango.dqtri.com".equals(email)) {
            throw new UsernameNotFoundException(String.format("Username not found: %s", email));
        }
        ConfigUser configUser = new ConfigUser();
        configUser.setEmail("submitter@mango.dqtri.com");
        configUser.setRole(Role.SUBMITTER);
        return new CustomUserDetails(configUser);
    }
}