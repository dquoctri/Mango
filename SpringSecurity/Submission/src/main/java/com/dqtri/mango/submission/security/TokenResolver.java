/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.submission.security;

import org.springframework.security.core.Authentication;

public interface TokenResolver {
    Authentication verifyToken(String token);
}
