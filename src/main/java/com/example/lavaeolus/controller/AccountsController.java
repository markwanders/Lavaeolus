package com.example.lavaeolus.controller;

import com.example.lavaeolus.controller.domain.Account;
import com.example.lavaeolus.service.BunqService;
import com.example.lavaeolus.service.EthereumService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RequestMapping("/api/accounts")
@RestController
public class AccountsController {
    private static final Logger LOG = LoggerFactory.getLogger(AccountsController.class);

    @Autowired
    private EthereumService ethereumService;

    @Autowired
    private BunqService bunqService;

    @RequestMapping(method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity getAccounts() throws IOException {
        LOG.info("Received request on getAccounts endpoint");

        List<Account> accounts = new ArrayList<>();
        accounts.addAll(bunqService.getAccounts());
        accounts.addAll(ethereumService.getAccounts());

        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

}
