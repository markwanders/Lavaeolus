package com.example.lavaeolus.security;

import com.example.lavaeolus.dao.domain.Role;
import com.example.lavaeolus.dao.domain.LavaeolusUser;
import org.springframework.security.core.authority.AuthorityUtils;

public class TokenUser extends org.springframework.security.core.userdetails.User {
    private LavaeolusUser user;

    public TokenUser(LavaeolusUser user) {
        super(user.getUsername(), user.getPassword(), AuthorityUtils.createAuthorityList(user.getRole().toString()));
        this.user = user;
    }

    public LavaeolusUser getUser() {
        return user;
    }

    public Long getId() {
        return user.getId();
    }

    public Role getRole() {
        return user.getRole();
    }
}
