package com.example.lavaeolus.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class TokenAuthenticationFilter extends GenericFilterBean {

    private final TokenAuthenticationService tokenAuthenticationService;

    public TokenAuthenticationFilter(TokenAuthenticationService authService) {
        this.tokenAuthenticationService = authService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        Authentication authentication = tokenAuthenticationService.getAuthentication((HttpServletRequest) request);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
        SecurityContextHolder.getContext().setAuthentication(null);
    }
}
