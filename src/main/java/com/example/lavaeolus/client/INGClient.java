package com.example.lavaeolus.client;

import com.example.lavaeolus.AccessTokenResponse;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Component
public class INGClient {
    private static final Logger LOG = LoggerFactory.getLogger(INGClient.class);

    private static final String ING_URL = "https://api.sandbox.ing.com/oauth2/";

    @Autowired
    private RestTemplate restTemplate;

    public AccessTokenResponse registerApplication() {
        MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
        map.add("grant_type", "client_credentials");
        map.add("scope", "create_order+granting+payment-requests+payment-requests%3Aview+payment-requests%3Acreate+payment-requests%3Aclose+virtual-ledger-accounts%3Afund-reservation%3Acreate+virtual-ledger-accounts%3Afund-reservation%3Adelete+virtual-ledger-accounts%3Abalance%3Aview");

        String requestURL = ING_URL + "token";

        LOG.info("Sending request to {}", requestURL);
        try {
            HttpHeaders headers = createHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    requestURL,
                    HttpMethod.POST,
                    request,
                    String.class);

            LOG.info("Received response: {}", responseEntity);

            return new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
                    .readValue(responseEntity.getBody(), AccessTokenResponse.class);
        } catch (
                HttpClientErrorException e) {
            LOG.error("Did not receive a correct response: {} {}", e.getStatusCode(), e.getResponseBodyAsString());
            return null;
        } catch (
                IOException e) {
            LOG.error("An error occurred mapping the JSON response to an AccessTokenResponse: ", e);
            return null;
        }
    }

    public String getAuthorizationServerUrl() {
        return "";
    }

    private HttpHeaders createHeaders() {
        return new HttpHeaders();
    }
}
