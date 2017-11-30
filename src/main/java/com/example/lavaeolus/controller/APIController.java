package com.example.lavaeolus.controller;

import com.example.lavaeolus.controller.domain.APIResponse;
import com.example.lavaeolus.dao.EtherScanClient;
import com.example.lavaeolus.dao.domain.EtherScanReply;
import com.example.lavaeolus.service.APIService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RequestMapping("/api")
@RestController
public class APIController {
    private static final Logger LOG = LoggerFactory.getLogger(APIController.class);

    @Autowired
    private APIService apiService;

    @RequestMapping(method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity getBalance() throws IOException {
        LOG.info("Received request on getBalance endpoint");

        APIResponse apiResponse = apiService.createAPIResponse();

        return new ResponseEntity(apiResponse, HttpStatus.OK);
    }

}
