package com.example.lavaeolus.security;

import com.example.lavaeolus.database.domain.User;
import com.example.lavaeolus.security.domain.TokenUser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class TokenAuthenticationService {
    private static final Logger LOG = LoggerFactory.getLogger(TokenAuthenticationService.class);


    // 10 days
    private static final long VALIDITY_TIME_MS = 10 * 24 * 60 * 60 * 1000;
    private static final String AUTH_HEADER_NAME = "x-auth-token";

    @Value("${lavaeolus.token.secret}")
    private String secret;

    void addAuthentication(HttpServletResponse response, TokenUser tokenUser) {
        String token = createTokenForUser(tokenUser);
        response.addHeader(AUTH_HEADER_NAME, token);
    }

    Authentication getAuthentication(HttpServletRequest request) {
        final String token = request.getHeader(AUTH_HEADER_NAME);
        try {
            if (token != null && !token.isEmpty()) {
                final TokenUser user = parseUserFromToken(token);
                return new UserAuthentication(user);
            }
        } catch (JwtException e) {
            LOG.error("Encountered an error while parsing JWT token: {}", e.getMessage());
            return null;
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

    Map<String, String> createTokenHeaderForUser(TokenUser user) {
        Map<String, String> header = new HashMap<>();
        header.put(AUTH_HEADER_NAME, createTokenForUser(user));
        return header;
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
