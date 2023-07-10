/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.core.security;

import org.springframework.security.core.Authentication;

public interface TokenProvider {
    String generateToken(Authentication authentication);
}
