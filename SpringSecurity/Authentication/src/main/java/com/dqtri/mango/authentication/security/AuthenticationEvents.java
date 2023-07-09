/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.authentication.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthenticationEvents {
    @EventListener
    public void onSuccess(AuthenticationSuccessEvent success) {
        // ...
        Authentication authentication = success.getAuthentication();
        log.info("onSuccess(AuthenticationSuccessEvent");
        log.info("onSuccess(AuthenticationSuccessEvent{}", authentication);
    }

    @EventListener
    public void onFailure(AbstractAuthenticationFailureEvent failures) {
        // ...
        Authentication authentication = failures.getAuthentication();
        log.info("onSuccess(AuthenticationSuccessEvent");
        log.info("onSuccess(AuthenticationSuccessEvent{}", authentication);
    }
}
