package com.example.lavaeolus.client;

import com.example.lavaeolus.client.domain.EtherScanBalance;
import com.example.lavaeolus.client.domain.EtherScanTransactions;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${etherscan.api-key}")
    private String apiKey;

    public EtherScanBalance getBalance(String address) {
        String requestURL = ETHERSCAN_URL + "?module=account&action=balance&tag=latest&address=" + address + "&apikey=" + apiKey;

        LOG.info("Sending request to {}", requestURL);
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(requestURL, String.class);

        LOG.info("Received response: {}", responseEntity);

        try {
            return new ObjectMapper().readValue(responseEntity.getBody(), EtherScanBalance.class);
        } catch (IOException e) {
            LOG.error("An error occurred mapping the JSON response to an EtherScanBalance: ", e);
            return new EtherScanBalance();
        }
    }

    public EtherScanTransactions getTransactions(String address) {
        String requestURL = ETHERSCAN_URL + "?module=account&action=txlist&sort=asc&address=" + address + "&apikey=" + apiKey;

        LOG.info("Sending request to {}", requestURL);
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(requestURL, String.class);

        LOG.info("Received response: {}", responseEntity);

        try {
            return new ObjectMapper().readValue(responseEntity.getBody(), EtherScanTransactions.class);
        } catch (IOException e) {
            LOG.error("An error occurred mapping the JSON response to an EtherScanBalance: ", e);
            return new EtherScanTransactions();
        }
    }
}
