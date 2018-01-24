package com.example.lavaeolus.controller;

import com.example.lavaeolus.controller.domain.Account;
import com.example.lavaeolus.controller.domain.Transaction;
import com.example.lavaeolus.service.BunqService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RequestMapping("/api/accounts/bunq")
@RestController
public class BunqController {
    private static final Logger LOG = LoggerFactory.getLogger(BunqController.class);

    @Autowired
    private BunqService bunqService;

    @RequestMapping(method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity getAccounts() throws IOException {
        LOG.info("Received request on Bunq accounts endpoint");

        List<Account> accounts = bunqService.getAccounts();

        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    @RequestMapping(value="/transactions/{id}", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity getTransactions(@PathVariable("id") final String id) throws IOException {
        LOG.info("Received request on Bunq transactions endpoint");

        List<Transaction> transactions = new ArrayList<>(bunqService.getTransactions(id));

        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

}
