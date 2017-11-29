package com.example.lavaeolus.dao;

import com.example.lavaeolus.controller.APIController;
import com.example.lavaeolus.dao.domain.EtherscanReply;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Component
public class EtherscanClient {
    private static final Logger LOG = LoggerFactory.getLogger(EtherscanClient.class);

    private static final String ETHERSCAN_URL = "https://api.etherscan.io/api";

    @Bean
    private RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Autowired
    private Environment env;

    public EtherscanReply getBalance(String address) throws IOException {
        String apiKey = env.getProperty("etherscan.api-key");
        String requestURL = ETHERSCAN_URL + "?module=account&action=balance&tag=latest&address=" + address + "&apikey=" + apiKey;

        LOG.info("Sending request to {}", requestURL);
        ResponseEntity<String> responseEntity = restTemplate().getForEntity(requestURL, String.class);

        LOG.info("Received response: {}", responseEntity);
        return new ObjectMapper().readValue(responseEntity.getBody(), EtherscanReply.class);
    }
}
