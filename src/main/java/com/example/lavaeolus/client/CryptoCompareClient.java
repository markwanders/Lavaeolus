package com.example.lavaeolus.client;

import com.example.lavaeolus.client.domain.CryptoCompareReply;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Component
public class CryptoCompareClient {
    private static final Logger LOG = LoggerFactory.getLogger(EtherScanClient.class);

    private static final String CRYPTOCOMPARE_URL = "https://min-api.cryptocompare.com/data/price";

    @Autowired
    private RestTemplate restTemplate;

    public CryptoCompareReply getPrice() {
        String requestURL = CRYPTOCOMPARE_URL + "?fsym=ETH&tsyms=EUR";

        LOG.info("Sending request to {}", requestURL);
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(requestURL, String.class);

        LOG.info("Received response: {}", responseEntity);
        try {
            return new ObjectMapper().readValue(responseEntity.getBody(), CryptoCompareReply.class);
        } catch (IOException e) {
            LOG.error("An error occurred mapping the JSON response to an CryptoCompareReply: ", e);
            return new CryptoCompareReply();
        }
    }
}
