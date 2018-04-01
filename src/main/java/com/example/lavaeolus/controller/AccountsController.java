package com.example.lavaeolus.controller;

import com.example.lavaeolus.controller.domain.Account;
import com.example.lavaeolus.service.BunqService;
import com.example.lavaeolus.service.EthereumService;
import com.launchdarkly.client.LDClient;
import com.launchdarkly.client.LDUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
public class AccountsController extends AbstractController {
    private static final Logger LOG = LoggerFactory.getLogger(AccountsController.class);

    private static final String ETHEREUM_ACCOUNTS_FLAG = "ethereum-accounts";

    @Autowired
    private EthereumService ethereumService;

    @Autowired
    private BunqService bunqService;

    @Autowired
    private LDClient ldClient;


    @RequestMapping(method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity getAccounts() throws IOException {
        LOG.info("Received request on getAccounts endpoint");

        List<Account> accounts = new ArrayList<>();
        accounts.addAll(bunqService.getAccounts());

        LDUser user = new LDUser(getCurrentUser().getUsername());

        if(ldClient.boolVariation(ETHEREUM_ACCOUNTS_FLAG, user, false)) {
            accounts.addAll(ethereumService.getAccounts());
        }

        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

}
