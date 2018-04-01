package com.example.lavaeolus.controller;

import com.example.lavaeolus.dao.domain.User;
import com.example.lavaeolus.security.UserAuthentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AbstractController {
    User getCurrentUser() {
        return ((UserAuthentication) SecurityContextHolder.getContext().getAuthentication()).getDetails().getUser();
    }
}
