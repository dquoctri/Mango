package com.dqtri.mango.authentication.security.impl;

import com.dqtri.mango.authentication.model.MangoUser;
import com.dqtri.mango.authentication.repository.UserRepository;
import com.dqtri.mango.authentication.security.models.DlnUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        MangoUser userRole = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("Username not found: %s", email)));
        return DlnUserDetails.create(userRole);
    }
}
