package com.example.lavaeolus.service;

import com.example.lavaeolus.controller.domain.EthereumResponse;
import com.example.lavaeolus.dao.CryptoCompareClient;
import com.example.lavaeolus.dao.EtherScanClient;
import com.example.lavaeolus.dao.domain.CryptoCompareReply;
import com.example.lavaeolus.dao.domain.EtherScanReply;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class EthereumService {

    private static final BigDecimal weiToEtherRatio = new BigDecimal("1000000000000000000");

    @Autowired
    private EtherScanClient etherScanClient;

    @Autowired
    private CryptoCompareClient cryptoCompareClient;

    @Autowired
    private Environment env;

    public EthereumResponse createAPIResponse() {
        String address = env.getProperty("etherscan.address");

        EtherScanReply etherScanReply = etherScanClient.getBalance(address);

        CryptoCompareReply cryptoCompareReply = cryptoCompareClient.getPrice();

        EthereumResponse ethereumResponse = new EthereumResponse();

        BigDecimal etherBalance = new BigDecimal(etherScanReply.getResult()).divide(weiToEtherRatio);
        BigDecimal euroBalance = cryptoCompareReply.getEUR().multiply(etherBalance).setScale(2, RoundingMode.HALF_UP);

        ethereumResponse.setAccountBalanceInEther(etherBalance);
        ethereumResponse.setAccountAddress(address);
        ethereumResponse.setAccountBalanceInEuros(euroBalance);

        return ethereumResponse;
    }
}
