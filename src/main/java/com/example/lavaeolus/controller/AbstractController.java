package com.example.lavaeolus.controller;

import com.example.lavaeolus.database.UserRepository;
import com.example.lavaeolus.database.domain.User;
import com.example.lavaeolus.security.UserAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

public class AbstractController {
    @Autowired
    private UserRepository userRepository;

    public User getCurrentUser() {
        User userFromToken = ((UserAuthentication) SecurityContextHolder.getContext().getAuthentication()).getDetails().getUser();
        return userRepository.findOne(userFromToken.getId());
    }
}
