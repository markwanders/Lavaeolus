package com.example.lavaeolus.controller;

import com.example.lavaeolus.controller.domain.Account;
import com.example.lavaeolus.database.domain.User;
import com.example.lavaeolus.security.TokenUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/user")
@RestController
public class UserController extends AbstractController {
    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private TokenUserDetailsService tokenUserDetailsService;

    @RequestMapping(method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity getUser() {
        LOG.info("Received request on getUser endpoint");

        User user = getCurrentUser();
        if(user == null) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } else {
            return new ResponseEntity<>(user, HttpStatus.OK);
        }
    }

    @RequestMapping(path = "/password", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity changePassword(@RequestParam(value="newPassword") String newPassword) {
        LOG.info("Received request on changePassword endpoint");
        User user = null;

        if(newPassword != null && !newPassword.isEmpty() && newPassword.length() >= 8) {
            LOG.debug("Updating password");
            user = tokenUserDetailsService.changePasswordByUsername(getCurrentUser().getUsername(), newPassword).getUser();
        } else {
            throw new IllegalArgumentException("New password is invalid");
        }

        MultiValueMap<String, String> tokenHeader = new HttpHeaders();
        tokenHeader.setAll(tokenUserDetailsService.createTokenHeader(user));

        return new ResponseEntity<>(user, tokenHeader, HttpStatus.OK);

    }

    @RequestMapping(path = "/account", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity addAccount(@RequestParam(value="newKey") String newKey , @RequestParam(value = "accountType") String accountType) {
        LOG.info("Received request on addAccount endpoint: {}", accountType);
        User user = null;

        if(newKey != null && !newKey.isEmpty()) {
            LOG.debug("Adding key");
            user = tokenUserDetailsService.changeKeyByUsername(getCurrentUser().getUsername(), newKey, Account.AccountType.valueOf(accountType.toLowerCase())).getUser();
        } else {
            throw new IllegalArgumentException("New key is invalid");
        }

        MultiValueMap<String, String> tokenHeader = new HttpHeaders();
        tokenHeader.setAll(tokenUserDetailsService.createTokenHeader(user));

        return new ResponseEntity<>(user, tokenHeader, HttpStatus.OK);

    }

    @RequestMapping(path = "/account/{accountType}", method = RequestMethod.DELETE, produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity deleteAccount(@PathVariable(value = "accountType") String accountType) {
        //TODO: some sort of verification. People shouldn't be able to accidentally delete accounts this easily
        LOG.info("Received request on deleteAccount endpoint: {}", accountType);

        User user = tokenUserDetailsService.deleteAccountByUsername(getCurrentUser().getUsername(), Account.AccountType.valueOf(accountType.toLowerCase())).getUser();

        MultiValueMap<String, String> tokenHeader = new HttpHeaders();
        tokenHeader.setAll(tokenUserDetailsService.createTokenHeader(user));

        return new ResponseEntity<>(user, tokenHeader, HttpStatus.OK);

    }
}
