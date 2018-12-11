package com.example.lavaeolus.client;

import com.example.lavaeolus.AccessTokenResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;

@Component
public class RabobankClient {
    private static final Logger LOG = LoggerFactory.getLogger(RabobankClient.class);

    public static final String RABOBANK_URL = "https://api-sandbox.rabobank.nl/openapi/sandbox/oauth2";

    @Autowired
    private RestTemplate restTemplate;

    @Value("${lavaeolus.root}")
    private String root;

    @Value("${rabobank.client_id}")
    private String clientID;

    @Value("${rabobank.client_secret}")
    private String clientSecret;

    public String getAccessToken(String authorizationCode) {
        String rabobankRedirectURI = root + "/register/account/rabobank/";
        String requestURL = RABOBANK_URL + "/token?code=" + authorizationCode + "&grant_type=authorization_code&redirect_uri=" + URLEncoder.encode(rabobankRedirectURI);

        LOG.info("Sending request to {}", requestURL);
        ResponseEntity<String> responseEntity = restTemplate.exchange(requestURL, HttpMethod.POST, new HttpEntity<>(createHeaders(clientID, clientSecret)), String.class);

        LOG.info("Received response: {}", responseEntity);
        try {
            return new ObjectMapper().readValue(responseEntity.getBody(), AccessTokenResponse.class).getAccessToken();
        } catch (IOException e) {
            LOG.error("An error occurred mapping the JSON response to an AccessTokenResponse: ", e);
            return null;
        }
    }

    private HttpHeaders createHeaders(String username, String password){
        return new HttpHeaders() {{
            String auth = username + ":" + password;
            byte[] encodedAuth = Base64.encodeBase64(
                    auth.getBytes(Charset.forName("US-ASCII")) );
            String authHeader = "Basic " + new String( encodedAuth );
            set( "Authorization", authHeader );
        }};
    }
}
