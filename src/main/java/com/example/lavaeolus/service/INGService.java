package com.example.lavaeolus.service;

import com.example.lavaeolus.AccessTokenResponse;
import com.example.lavaeolus.client.INGClient;
import com.example.lavaeolus.security.TokenUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class INGService {
    private static final Logger LOG = LoggerFactory.getLogger(INGService.class);

    @Resource
    private INGClient ingClient;

    @Resource
    private TokenUserDetailsService tokenUserDetailsService;

    @Value("${lavaeolus.root}")
    private String root;
    
    @Autowired
    private AccessTokenResponse ingAccessToken;

    public String redirect(String state) {
        LOG.info("Building redirect URL for ING OAuth2 flow: {}", ingAccessToken);
        String ingRedirectURI = root + "/register/account/ing/";
        String authorizationServerUrl = ingClient.getAuthorizationServerUrl();
        return "";
    }
    
}
