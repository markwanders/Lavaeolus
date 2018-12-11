package com.example.lavaeolus.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import com.example.lavaeolus.AccessTokenResponse;

import java.io.IOException;

@Component
public class RabobankClient {
    private static final Logger LOG = LoggerFactory.getLogger(RabobankClient.class);

    public static final String RABOBANK_URL = "https://api-sandbox.rabobank.nl/openapi/sandbox/oauth2";

    @Autowired
    private RestTemplate restTemplate;

    public String getAccessToken(String authorizationCode) {
        String requestURL = RABOBANK_URL + "/token?code=" + authorizationCode;

        LOG.info("Sending request to {}", requestURL);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(requestURL, null, String.class);

        LOG.info("Received response: {}", responseEntity);
        try {
            return new ObjectMapper().readValue(responseEntity.getBody(), AccessTokenResponse.class).getAccessToken();
        } catch (IOException e) {
            LOG.error("An error occurred mapping the JSON response to an AccessTokenResponse: ", e);
            return null;
        }
    }
}
