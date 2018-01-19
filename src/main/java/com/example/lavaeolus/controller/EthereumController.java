package com.example.lavaeolus.controller;

import com.example.lavaeolus.controller.domain.Account;
import com.example.lavaeolus.service.EthereumService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RequestMapping("/api/accounts/ethereum")
@RestController
public class EthereumController {
    private static final Logger LOG = LoggerFactory.getLogger(EthereumController.class);

    @Autowired
    private EthereumService ethereumService;

    @RequestMapping(method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity getAccounts() throws IOException {
        LOG.info("Received request on Ethereum endpoint");

        List<Account> accounts = ethereumService.getAccounts();

        return new ResponseEntity(accounts, HttpStatus.OK);
    }

}
