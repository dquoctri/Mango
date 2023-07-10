/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.authentication.security;

import com.dqtri.mango.authentication.exception.LoginFailedException;
import com.dqtri.mango.authentication.model.LoginAttempt;
import com.dqtri.mango.authentication.repository.LoginAttemptRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@RequiredArgsConstructor
@Component
public class AuthenticationEvents {

    private final LoginAttemptRepository loginAttemptRepository;

    @Value("${safeguard.auth.login.maximum-failed-attempt}")
    private int maxFailedAttempt;

    @EventListener
    public void onSuccess(AuthenticationSuccessEvent success) {
        //loginAttemptRepository.
        // ...
        Authentication authentication = success.getAuthentication();
        if (authentication instanceof UsernamePasswordAuthenticationToken userAuthentication) {
            UserDetails principal = (UserDetails) userAuthentication.getPrincipal();
            resetLoginAttempt(principal.getUsername());
        }
        log.info("onSuccess(AuthenticationSuccessEvent");
        log.info("onSuccess(AuthenticationSuccessEvent{}", authentication);
    }

    @EventListener
    public void onFailure(AbstractAuthenticationFailureEvent failures) {
        Authentication authentication = failures.getAuthentication();
        if (authentication instanceof UsernamePasswordAuthenticationToken userAuthentication) {
            tryLoginAttempt(userAuthentication.getPrincipal().toString());
        }
        // ...

        log.info("onSuccess(AuthenticationSuccessEvent");
        log.info("onSuccess(AuthenticationSuccessEvent{}", authentication);
    }

    @Transactional
    private void tryLoginAttempt(String email) {
        LoginAttempt loginAttempt = loginAttemptRepository.findByEmail(email).orElse(new LoginAttempt(email));
        if (maxFailedAttempt > 0 && loginAttempt.isLockout()) {
            throw new LoginFailedException(String.format("%s has been locked due to multiple failed login attempts", email));
        }
        int nextFailedAttempt = loginAttempt.getFailedAttempts() + 1;
        if (maxFailedAttempt > 0 && nextFailedAttempt >= maxFailedAttempt){
            loginAttempt.setLockout(true);
        }
        loginAttempt.setFailedAttempts(nextFailedAttempt);
        loginAttempt.setLastFailedTimestamp(new Date().getTime());
        loginAttemptRepository.save(loginAttempt);
    }

    @Transactional
    private void resetLoginAttempt(String email){
        if (maxFailedAttempt <= 0) {
            return;
        }
        LoginAttempt loginAttempt = loginAttemptRepository.findByEmail(email).orElse(new LoginAttempt(email));
        loginAttempt.setFailedAttempts(0);
        loginAttempt.setLockout(false);
        loginAttempt.setLastFailedTimestamp(new Date().getTime());
        loginAttemptRepository.save(loginAttempt);
    }
}
