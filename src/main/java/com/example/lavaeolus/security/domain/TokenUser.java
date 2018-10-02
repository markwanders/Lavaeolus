package com.example.lavaeolus.security.domain;

import com.example.lavaeolus.database.domain.Role;
import com.example.lavaeolus.database.domain.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.authority.AuthorityUtils;

public class TokenUser extends org.springframework.security.core.userdetails.User {
    private User user;

    public TokenUser(User user) {
        super(user.getUsername(), user.getPassword(), AuthorityUtils.createAuthorityList(user.getRole().toString()));
        this.user = user;
    }

    @JsonIgnore
    public User getUser() {
        return user;
    }

    public Long getId() {
        return user.getId();
    }

    public Role getRole() {
        return user.getRole();
    }
}
