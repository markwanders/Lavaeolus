package com.example.lavaeolus.security;

import com.example.lavaeolus.security.domain.TokenUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginFilter extends AbstractAuthenticationProcessingFilter {
    private static final Logger LOG = LoggerFactory.getLogger(LoginFilter.class);

    private TokenAuthenticationService tokenAuthenticationService;

    public LoginFilter(String defaultFilterProcessesUrl, AuthenticationManager authenticationManager, TokenAuthenticationService tokenAuthenticationService) {
        super(new AntPathRequestMatcher(defaultFilterProcessesUrl));
        setAuthenticationManager(authenticationManager);
        this.tokenAuthenticationService = tokenAuthenticationService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws AuthenticationException {
        LOG.info("Attempting authentication: {}", httpServletRequest.getParameter("username"));
        return getAuthenticationManager()
                .authenticate(new UsernamePasswordAuthenticationToken(
                        httpServletRequest.getParameter("username"), httpServletRequest.getParameter("password")));
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authentication) {
        LOG.info("Successful authentication: {}", authentication);
        tokenAuthenticationService.addAuthentication(response, (TokenUser) authentication.getPrincipal());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
