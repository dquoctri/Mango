package com.dqtri.mango.authentication.security.jwt;

import com.dqtri.mango.authentication.security.MSGraphService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.Date;
import java.util.concurrent.ExecutionException;

@Slf4j
@RequiredArgsConstructor
public class JwtTokenService {

    private final MSGraphService msGraphService;

    /**
     * Verify the access token.
     *
     * @param token JWT access token.
     * @return {@link JwtAuthenticationToken}
     */
    public JwtAuthenticationToken verifyToken(String token) throws ExecutionException, JWTVerificationException {
        verifyTokenExpiration(token);
        UserDetails userDetails = msGraphService.getUserDetails(token);
        return new JwtAuthenticationToken(userDetails, userDetails.getAuthorities());
    }

    private void verifyTokenExpiration(String token) throws JWTVerificationException {
        //Note: The token used for MS Graph, We should not validate signature of this token. Leave that for MS Graph.
        //https://docs.microsoft.com/en-us/answers/questions/318741/graphapi-cannot-validate-access-token-signature.html
        DecodedJWT tokenDecoded = JWT.decode(token);
        if (tokenDecoded.getExpiresAt().before(new Date())) {
            throw new JWTVerificationException("Access token is expired");
        }
    }
}
