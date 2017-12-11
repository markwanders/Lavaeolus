package com.example.lavaeolus.dao;

import com.bunq.sdk.context.ApiContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BunqClient {
    private static final Logger LOG = LoggerFactory.getLogger(BunqClient.class);

    @Autowired
    private ApiContext apiContext;

}
