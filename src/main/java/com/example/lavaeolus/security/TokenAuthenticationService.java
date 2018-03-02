package com.example.lavaeolus.security;

import com.example.lavaeolus.dao.domain.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Date;

@Service
public class TokenAuthenticationService {
    // 10 days
    private static final long VALIDITY_TIME_MS = 10 * 24 * 60 * 60 * 1000;
    private static final String AUTH_HEADER_NAME = "x-auth-token";

    @Value("${lavaeolus.token.secret}")
    private String secret;

    public void addAuthentication(HttpServletResponse response, TokenUser tokenUser) {
        String token = createTokenForUser(tokenUser);
        response.addHeader(AUTH_HEADER_NAME, token);
    }

    public Authentication getAuthentication(HttpServletRequest request) {
        final String token = request.getHeader(AUTH_HEADER_NAME);
        if (token != null && !token.isEmpty()) {
            final TokenUser user = parseUserFromToken(token);
            if (user != null) {
                return new UserAuthentication(user);
            }
        }
        return null;
    }

    private TokenUser parseUserFromToken(String token) {
        String userJSON = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
        return new TokenUser(fromJSON(userJSON));
    }

    private String createTokenForUser(TokenUser user) {
        return Jwts.builder()
                .setExpiration(new Date(System.currentTimeMillis() + VALIDITY_TIME_MS))
                .setSubject(toJSON(user))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    private User fromJSON(final String userJSON) {
        try {
            return new ObjectMapper().readValue(userJSON, User.class);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private String toJSON(TokenUser user) {
        try {
            return new ObjectMapper().writeValueAsString(user);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }
}
