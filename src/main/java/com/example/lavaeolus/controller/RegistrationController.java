package com.example.lavaeolus.controller;

import com.example.lavaeolus.controller.domain.Account;
import com.example.lavaeolus.database.domain.User;
import com.example.lavaeolus.security.TokenUserDetailsService;
import com.example.lavaeolus.service.RabobankService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/register")
@RestController
public class RegistrationController {
    private static final Logger LOG = LoggerFactory.getLogger(RegistrationController.class);

    @Autowired
    private RabobankService rabobankService;

    @Autowired
    private TokenUserDetailsService tokenUserDetailsService;

    @Value("${lavaeolus.root}")
    private String root;

    @RequestMapping(method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public ResponseEntity registerUser(final User newUser) {
        LOG.info("Received request on registerUser endpoint: {}", newUser);

        MultiValueMap<String, String> tokenHeader = new HttpHeaders();
        tokenHeader.setAll(tokenUserDetailsService.registerNewUserAndReturnTokenHeader(newUser.getUsername(), newUser.getPassword()));

        return new ResponseEntity<>(tokenHeader, HttpStatus.OK);
    }

    @RequestMapping(path = "/account/{accountType}/", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity confirmRegistration(
            @PathVariable(value = "accountType") String accountType,
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "state", required = false) String state) {
        LOG.info("Received request on confirmRegistration endpoint: {} {} {}", accountType, code, state);

        if(Account.AccountType.rabobank.getName().equalsIgnoreCase(accountType)) {
            rabobankService.register(code, state);
        }

        return ResponseEntity
                .status(HttpStatus.TEMPORARY_REDIRECT)
                .header("location", root + "/user")
                .build();
    }

}
