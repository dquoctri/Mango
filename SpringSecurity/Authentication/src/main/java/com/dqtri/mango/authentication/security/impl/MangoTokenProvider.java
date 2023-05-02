/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.authentication.security.impl;

import com.dqtri.mango.authentication.security.TokenProvider;
import com.dqtri.mango.authentication.security.models.MangoUserDetails;
import com.dqtri.mango.authentication.security.models.TokenResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

import static org.apache.tomcat.util.codec.binary.Base64.decodeBase64;

@Component
@RequiredArgsConstructor
public class MangoTokenProvider implements TokenProvider {

    @Value("${app.auth.secret}")
    private String secret;

    @Value("${app.auth.expirationInMs}")
    private long expirationInMs;

    @Override
    public TokenResponse generateToken(Authentication authentication) throws Exception {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String encodedString = Base64.getEncoder().withoutPadding().encodeToString(secret.getBytes());
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationInMs);
        KeyPair keyPair = createKeyPair();
        String accessToken = Jwts.builder().setSubject(userDetails.getUsername())
                .setExpiration(expiryDate)
                .setIssuer("mango@mango.dqtri.com")
                // RS256 with privateKey
                .signWith(SignatureAlgorithm.RS256, keyPair.getPrivate())
                .compact();

        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(keyPair.getPublic())
                .parseClaimsJws(accessToken);
        System.out.println("Header     : " + claimsJws.getHeader());
        System.out.println("Body       : " + claimsJws.getBody());
        System.out.println("Signature  : " + claimsJws.getSignature());
        return new TokenResponse(accessToken);
    }

    public static PrivateKey getPrivateKey(String algorithm, String keyContent) throws Exception{
        try {
            byte[] encoded = decodeBase64(keyContent);
            KeyFactory kf = KeyFactory.getInstance(algorithm);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
            PrivateKey privateKey = kf.generatePrivate(keySpec);
            return privateKey;
        } catch (Exception ex) {
            //log error
            throw ex;
        }
    }

    public KeyPair createKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance("RSA");
        keyGenerator.initialize(1024);

        KeyPair keyPair = keyGenerator.genKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        String encodedPublicKey = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        System.out.println(convertToPublicKey(encodedPublicKey));
        String token = generateJwtToken(privateKey);
        return keyPair;
    }

    private String generateJwtToken(PrivateKey privateKey) {
        return null;
    }

    private String convertToPublicKey(String key){
        StringBuilder result = new StringBuilder();
        result.append("-----BEGIN PUBLIC KEY-----\n");
        result.append(key);
        result.append("\n-----END PUBLIC KEY-----");
        return result.toString();
    }
}
