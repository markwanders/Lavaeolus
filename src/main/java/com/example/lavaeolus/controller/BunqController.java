package com.example.lavaeolus.controller;

import com.example.lavaeolus.controller.domain.Account;
import com.example.lavaeolus.dao.BunqClient;
import com.example.lavaeolus.dao.domain.BunqReply;
import com.example.lavaeolus.service.BunqService;
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

@RequestMapping("/api/bunq")
@RestController
public class BunqController {
    private static final Logger LOG = LoggerFactory.getLogger(BunqController.class);

    @Autowired
    private BunqService bunqService;

    @RequestMapping(method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity getAccount() throws IOException {
        LOG.info("Received request on Bunq endpoint");

        Account account = bunqService.getAccount();

        return new ResponseEntity(account, HttpStatus.OK);
    }

}
