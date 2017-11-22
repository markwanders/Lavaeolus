package com.example.lavaeolus.controller;

import com.example.lavaeolus.controller.domain.APIResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api")
@RestController
public class APIController {

    @RequestMapping(method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity get() {

        APIResponse apiResponse = new APIResponse();

        apiResponse.setAccountBalance(100);
        apiResponse.setAccountAddress("0x407d73d8a49eeb85d32cf465507dd71d507100c1");

        return new ResponseEntity(apiResponse, HttpStatus.OK);
    }

}
