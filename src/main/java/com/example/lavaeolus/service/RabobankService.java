package com.example.lavaeolus.service;

import com.example.lavaeolus.client.RabobankClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.net.URLEncoder;

@Service
public class RabobankService {
    private static final Logger LOG = LoggerFactory.getLogger(RabobankService.class);

    @Resource
    private RabobankClient rabobankClient;

    @Value("${rabobank.redirect_uri}")
    private String rabobankRedirectyURI;

    @Value("${rabobank.client_id}")
    private String rabobankClientID;

    public String redirect() {
        LOG.info("Building redirect URL for Rabobank OAuth2 flow");
        return RabobankClient.RABOBANK_URL + "/authorize?client_id=" + rabobankClientID + "&scope=AIS-Transactions-v2&response_type=code&redirect_uri=" + URLEncoder.encode(rabobankRedirectyURI);
    }

    public void register(String authorizationCode) {
        LOG.info("Registering new Rabobank user");
        String accessToken = rabobankClient.getAccessToken(authorizationCode);

        LOG.info("Received accessToken: {}", accessToken);
    }
}
