package com.dqtri.mango.submission.security.impl;

import com.dqtri.mango.submission.model.SubmissionUser;
import com.dqtri.mango.submission.security.CoreAuthenticationToken;
import com.dqtri.mango.submission.security.CoreUserDetails;
import com.dqtri.mango.submission.security.TokenResolver;
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
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Slf4j
@RequiredArgsConstructor
@Primary
@Component
public class CoreTokenResolver implements TokenResolver {

    private final UserDetailsService userDetailsService;

    @Value("${submission.auth.publicKey}")
    private String publicKey;

    @Override
    public Authentication verifyToken(String accessToken) {
        Jws<Claims> claimsJws = validateToken(accessToken);
        Claims body = claimsJws.getBody();
        Object authorities = body.get("authorities");
        log.debug("authorities: [{}]", authorities);
        String subject = body.getSubject();
        UserDetails userDetails = userDetailsService.loadUserByUsername(subject);
        if (userDetails instanceof CoreUserDetails coreUser) {
            //TODO:
            SubmissionUser submissionUser = coreUser.getSubmissionUser();
            log.debug("submissionUser: [{}]", submissionUser);
            return new CoreAuthenticationToken(coreUser, coreUser.getAuthorities());
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
        } catch (Exception ex) {
            log.error("Invalid JWTs", ex);
        }
        return null;
    }

}
