package com.example.lavaeolus.dao;

import com.example.lavaeolus.dao.domain.EtherScanReply;
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
public class EtherScanClient {
    private static final Logger LOG = LoggerFactory.getLogger(EtherScanClient.class);

    private static final String ETHERSCAN_URL = "https://api.etherscan.io/api";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Environment env;

    public EtherScanReply getBalance(String address) {
        String apiKey = env.getProperty("etherscan.api-key");
        String requestURL = ETHERSCAN_URL + "?module=account&action=balance&tag=latest&address=" + address + "&apikey=" + apiKey;

        LOG.info("Sending request to {}", requestURL);
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(requestURL, String.class);

        LOG.info("Received response: {}", responseEntity);

        try {
            return new ObjectMapper().readValue(responseEntity.getBody(), EtherScanReply.class);
        } catch (IOException e) {
            LOG.error("An error occurred mapping the JSON response to an EtherScanReply: ", e);
            return new EtherScanReply();
        }
    }
}
