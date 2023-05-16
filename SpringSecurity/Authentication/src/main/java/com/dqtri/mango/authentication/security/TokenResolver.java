package com.dqtri.mango.authentication.security;

import org.springframework.security.core.Authentication;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public interface TokenResolver {
    Authentication verifyToken(String token) throws NoSuchAlgorithmException, InvalidKeySpecException;
}
