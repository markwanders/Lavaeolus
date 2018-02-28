package com.example.lavaeolus.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Date;

@Service
public class TokenAuthenticationService {
    // 10 days
    private static final long VALIDITY_TIME_MS = 10 * 24 * 60 * 60 * 1000;
    private static final String AUTH_HEADER_NAME = "x-auth-token";

    @Value("${lavaeolus.token.secret}")
    private String secret;

    public void addAuthentication(HttpServletResponse response, String tokenUser) {
        String token = createTokenForUser(tokenUser);
        response.addHeader(AUTH_HEADER_NAME, token);
    }

    public Authentication getAuthentication(HttpServletRequest request) {
        final String token = request.getHeader(AUTH_HEADER_NAME);
        if (token != null && !token.isEmpty()) {
            final String user = parseUserFromToken(token);
            if (user != null) {
                return new UserAuthentication(user);
            }
        }
        return null;
    }

    private String parseUserFromToken(String token) {
        String userJSON = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
        return userJSON;
    }

    private String createTokenForUser(String user) {
        return Jwts.builder()
                .setExpiration(new Date(System.currentTimeMillis() + VALIDITY_TIME_MS))
                .setSubject(user)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }
}
