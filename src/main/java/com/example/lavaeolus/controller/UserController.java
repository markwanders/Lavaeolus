package com.example.lavaeolus.controller;

import com.example.lavaeolus.dao.domain.User;
import com.example.lavaeolus.security.TokenUserDetailsService;
import com.example.lavaeolus.security.UserAuthentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RequestMapping("/api/user")
@RestController
public class UserController extends AbstractController {
    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private TokenUserDetailsService tokenUserDetailsService;

    @RequestMapping(method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity getUser() throws IOException {
        LOG.info("Received request on getUser endpoint");

        User user = tokenUserDetailsService.loadUserByUsername(getCurrentUser().getUsername()).getUser();

        return new ResponseEntity<>(user, HttpStatus.OK);

    }

    @RequestMapping(method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity changePassword(@RequestParam(value="newPassword") String newPassword) throws IOException {
        LOG.info("Received request on changePassword endpoint");

        User user = tokenUserDetailsService.changePasswordByUsername(getCurrentUser().getUsername(), newPassword).getUser();

        return new ResponseEntity<>(user, HttpStatus.OK);

    }

}
