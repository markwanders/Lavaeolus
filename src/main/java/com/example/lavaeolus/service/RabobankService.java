package com.example.lavaeolus.service;

import com.example.lavaeolus.client.RabobankClient;
import com.example.lavaeolus.controller.domain.Account;
import com.example.lavaeolus.security.TokenUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.net.URLEncoder;

@Service
public class RabobankService {
    private static final Logger LOG = LoggerFactory.getLogger(RabobankService.class);

    @Resource
    private RabobankClient rabobankClient;

    @Resource
    private TokenUserDetailsService tokenUserDetailsService;

    @Value("${lavaeolus.root}")
    private String root;

    @Value("${rabobank.client_id}")
    private String rabobankClientID;

    public String redirect(String state) {
        LOG.info("Building redirect URL for Rabobank OAuth2 flow");
        String rabobankRedirectURI = root + "/register/account/rabobank/";
        return RabobankClient.RABOBANK_URL + "/authorize?client_id=" + rabobankClientID + "&scope=AIS-Transactions-v2&response_type=code&state=" + state + "&redirect_uri=" + URLEncoder.encode(rabobankRedirectURI);
    }

    public User register(String authorizationCode, String state) {
        LOG.info("Registering new Rabobank account for user: {}", state);
        String accessToken = rabobankClient.getAccessToken(authorizationCode);

        return tokenUserDetailsService.changeKeyByUsername(state, accessToken, Account.AccountType.rabobank);
    }
}
