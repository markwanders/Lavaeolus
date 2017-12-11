package com.example.lavaeolus.service;

import com.example.lavaeolus.controller.domain.APIResponse;
import com.example.lavaeolus.dao.BunqClient;
import com.example.lavaeolus.dao.CryptoCompareClient;
import com.example.lavaeolus.dao.EtherScanClient;
import com.example.lavaeolus.dao.domain.BunqResponse;
import com.example.lavaeolus.dao.domain.CryptoCompareReply;
import com.example.lavaeolus.dao.domain.EtherScanReply;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class APIService {

    private static final BigDecimal weiToEtherRatio = new BigDecimal("1000000000000000000");

    @Autowired
    private EtherScanClient etherScanClient;

    @Autowired
    private CryptoCompareClient cryptoCompareClient;

    @Autowired
    private BunqClient bunqClient;

    @Autowired
    private Environment env;

    public APIResponse createAPIResponse() {
        String address = env.getProperty("etherscan.address");

        EtherScanReply etherScanReply = etherScanClient.getBalance(address);

        CryptoCompareReply cryptoCompareReply = cryptoCompareClient.getPrice();

        BunqResponse bunqResponse = bunqClient.fetchAccount();

        APIResponse apiResponse = new APIResponse();

        BigDecimal etherBalance = new BigDecimal(etherScanReply.getResult()).divide(weiToEtherRatio);
        BigDecimal euroBalance = cryptoCompareReply.getEUR().multiply(etherBalance).setScale(2, RoundingMode.HALF_UP);

        apiResponse.setAccountBalanceInEther(etherBalance);
        apiResponse.setAccountAddress(address);
        apiResponse.setAccountBalanceInEuros(euroBalance);
        apiResponse.setBunqResponse(bunqResponse);

        return apiResponse;
    }
}
