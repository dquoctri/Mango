/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.core.security;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class KeyPairService {

    public KeyPair createKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance("RSA");
        keyGenerator.initialize(2048);
        return keyGenerator.genKeyPair();
    }

    public PrivateKey generateNewPrivateKey() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance("RSA");
        keyGenerator.initialize(2048);
        KeyPair keyPair = keyGenerator.genKeyPair();
        return keyPair.getPrivate();
    }

    public PublicKey generateNewPublicKey(PrivateKey privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory kf = KeyFactory.getInstance("RSA");
        RSAPrivateKeySpec keySpec = kf.getKeySpec(privateKey, RSAPrivateKeySpec.class);
        RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec(keySpec.getModulus(), keySpec.getPrivateExponent());
        return kf.generatePublic(rsaPublicKeySpec);
    }

    public PrivateKey decodePrivateKey(String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpecPKCS8 = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey));
        return keyFactory.generatePrivate(keySpecPKCS8);
    }

    public PublicKey decodePublicKey(String publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpecPKCS8 = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKey));
        return keyFactory.generatePublic(keySpecPKCS8);
    }

    public String encodePrivateKey(PrivateKey privateKey) {
        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }

    public String encodePublicKey(PublicKey publicKey) {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    public String convertToPublicKey(String key) {
        return"-----BEGIN PUBLIC KEY-----\n" +
                key +
                "\n-----END PUBLIC KEY-----";
    }
}
