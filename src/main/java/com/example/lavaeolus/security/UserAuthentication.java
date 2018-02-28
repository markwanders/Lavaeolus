package com.example.lavaeolus.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.Collections;


public class UserAuthentication implements Authentication {
    private final String user;
    private boolean authenticated = true;

    public UserAuthentication(String user) {
        this.user = user;
    }

    @Override
    public String getName() {
        return user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(this.user));
    }

    @Override
    public String getCredentials() {
        return user;
    }

    @Override
    public String getDetails() {
        return user;
    }

    @Override
    public String getPrincipal() {
        return user;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }
}
