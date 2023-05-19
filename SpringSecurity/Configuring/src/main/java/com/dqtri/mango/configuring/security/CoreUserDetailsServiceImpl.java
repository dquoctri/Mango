/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.configuring.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class CoreUserDetailsServiceImpl implements UserDetailsService {


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//        CoreUser user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new UsernameNotFoundException(String.format("Username not found: %s", email)));
//        return CoreUserDetails.create(user);
        return null;
    }
}
