package com.example.lavaeolus.service;

import com.example.lavaeolus.Greeting;
import com.example.lavaeolus.client.INGClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class INGService {
    private static final Logger LOG = LoggerFactory.getLogger(INGService.class);

    @Resource
    private INGClient ingClient;

    public Greeting showcase() {
        LOG.info("ING showcase");

        return ingClient.viewGreeting();
    }
    
}
