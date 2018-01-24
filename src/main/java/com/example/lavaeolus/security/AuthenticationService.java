package com.example.lavaeolus.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    @Value("${lavaeolus.api-key}")
    private String apiKey;

    public Authentication authenticationFromKey(String keyFromRequest) {
        PreAuthenticatedAuthenticationToken authenticationToken = null;

        if(apiKey.equals(keyFromRequest)) {
            authenticationToken = new PreAuthenticatedAuthenticationToken("key", keyFromRequest);
            authenticationToken.setAuthenticated(true);
        }

        return authenticationToken;
    }
}
