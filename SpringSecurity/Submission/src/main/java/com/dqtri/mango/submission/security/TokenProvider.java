/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.submission.security;

import com.dqtri.mango.submission.model.dto.response.TokenResponse;
import org.springframework.security.core.Authentication;

public interface TokenProvider {
    TokenResponse generateToken(Authentication authentication) throws Exception;
}
