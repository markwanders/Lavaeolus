package com.example.lavaeolus.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

@Component
public class AuthenticationFilter extends GenericFilterBean {
    private static final String KEY_PARAM = "api-key";

    @Autowired
    private AuthenticationService authenticationService;

    /**
     * Very basic authentication based solely on the presence of an API key query param
     *
     * @param servletRequest
     * @param servletResponse
     * @param filterChain
     * @throws IOException
     * @throws ServletException
     */

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        SecurityContext context = SecurityContextHolder.getContext();

        if(context.getAuthentication() == null || !context.getAuthentication().isAuthenticated()) {

            SecurityContextHolder.getContext().setAuthentication(
                    authenticationService.authenticationFromKey(
                            servletRequest.getParameter(KEY_PARAM)
                    )
            );
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

}
