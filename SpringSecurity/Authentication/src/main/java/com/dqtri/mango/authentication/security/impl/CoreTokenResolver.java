package com.dqtri.mango.authentication.security.impl;

import com.dqtri.mango.authentication.security.CoreAuthenticationToken;
import com.dqtri.mango.authentication.security.CoreUserDetails;
import com.dqtri.mango.authentication.security.TokenResolver;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.List;

import static com.fasterxml.jackson.databind.type.LogicalType.Collection;

@Slf4j
@RequiredArgsConstructor
@Component
public class CoreTokenResolver implements TokenResolver {

    private final UserDetailsService userDetailsService;

    @Value("${core.auth.publicKey}")
    private String publicKey;

    @Override
    public Authentication verifyToken(String accessToken) {
        Jws<Claims> claimsJws = validateToken(accessToken);
        Claims body = claimsJws.getBody();
        Object authorities = body.get("authorities");
        String subject = body.getSubject();
        UserDetails userDetails = userDetailsService.loadUserByUsername(subject);
        if (userDetails instanceof  CoreUserDetails coreUser){

        }
        return new CoreAuthenticationToken(userDetails, userDetails.getAuthorities());
    }

    private PublicKey getPublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec KeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(this.publicKey));
        return keyFactory.generatePublic(KeySpec);
    }

    public Jws<Claims> validateToken(String accessToken) {
        try {
            PublicKey publicKey = getPublicKey();
            return Jwts.parser().setSigningKey(publicKey).parseClaimsJws(accessToken);
        } catch (SignatureException ex) {
            log.error("Invalid JWT signature", ex);
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token", ex);
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token", ex);
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token", ex);
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.", ex);
        } catch (NoSuchAlgorithmException e) {
            log.error("No such algorithm", e);
        } catch (InvalidKeySpecException e) {
            log.error("invalid key spec", e);
        }
        return null;
    }

}
