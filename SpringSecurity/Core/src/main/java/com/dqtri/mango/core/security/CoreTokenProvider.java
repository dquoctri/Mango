/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.core.security;

import com.dqtri.mango.core.model.dto.response.TokenResponse;
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
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

import static org.apache.tomcat.util.codec.binary.Base64.decodeBase64;

@Component
@RequiredArgsConstructor
public class CoreTokenProvider implements TokenProvider {

    @Value("${core.auth.privateKey}")
    private String privateKey;

    @Value("${core.auth.expirationInMs}")
    private long expirationInMs;

    @Override
    public TokenResponse generateToken(Authentication authentication) throws Exception {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationInMs);
        KeyPair keyPair = createKeyPair();
        String accessToken = Jwts.builder().setSubject(userDetails.getUsername())
                .setExpiration(expiryDate)
                .setIssuer("mango@mango.dqtri.com")
                // RS256 with privateKey
                .signWith(SignatureAlgorithm.RS256, keyPair.getPrivate())
                .compact();
        Claims body = Jwts.parser().setSigningKey(keyPair.getPublic()).parseClaimsJws(accessToken).getBody();
        System.out.println(body);

        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(keyPair.getPublic())
                .parseClaimsJws(accessToken);
        System.out.println("Header     : " + claimsJws.getHeader());
        System.out.println("Body       : " + claimsJws.getBody());
        System.out.println("Signature  : " + claimsJws.getSignature());
        return new TokenResponse(accessToken);
    }

    public static PrivateKey getPrivateKey(String algorithm, String keyContent) throws Exception {
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

    public KeyPair createKeyPair() throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance("RSA");
        keyGenerator.initialize(1024);
        KeyPair keyPair = keyGenerator.genKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();
        String encodedPrivateKey = Base64.getEncoder().encodeToString(privateKey.getEncoded());

        KeyFactory kf = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpecPKCS8 = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(this.privateKey));
        PrivateKey privKey = kf.generatePrivate(keySpecPKCS8);
        String encodedPrivateKey2 = Base64.getEncoder().encodeToString(privKey.getEncoded());
        System.out.println(convertToPublicKey(encodedPrivateKey2));

        String encodedPublicKey = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        System.out.println(convertToPublicKey(encodedPublicKey));
        System.out.println(convertToPublicKey(encodedPrivateKey));
        String token = generateJwtToken(privateKey);
        return keyPair;
    }

    private String generateJwtToken(PrivateKey privateKey) {
        return null;
    }

    private String convertToPublicKey(String key) {
        StringBuilder result = new StringBuilder();
        result.append("-----BEGIN PUBLIC KEY-----\n");
        result.append(key);
        result.append("\n-----END PUBLIC KEY-----");
        return result.toString();
    }
}
