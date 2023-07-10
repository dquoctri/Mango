/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.core.security;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authorization.event.AuthorizationDeniedEvent;
import org.springframework.security.authorization.event.AuthorizationGrantedEvent;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Slf4j
@Component
public class AuthorizationEvents {
    @EventListener
    public void onSuccess(AuthorizationGrantedEvent success) {
        // ...
        Supplier<Authentication> authentication = success.getAuthentication();
        log.info("onSuccess(AuthenticationSuccessEvent");
        log.info("onSuccess(AuthenticationSuccessEvent{}", authentication);
    }

    @EventListener
    public void onFailure(AuthorizationDeniedEvent failure) {
        // ...
        Supplier<Authentication> authentication = failure.getAuthentication();
        log.info("onSuccess(AuthenticationSuccessEvent");
        log.info("onSuccess(AuthenticationSuccessEvent{}", authentication);
    }
}
