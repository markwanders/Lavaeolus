package com.example.lavaeolus.service;

import com.example.lavaeolus.controller.domain.Account;
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

    public Account getAccount() {
        String address = env.getProperty("etherscan.address");

        EtherScanReply etherScanReply = etherScanClient.getBalance(address);

        CryptoCompareReply cryptoCompareReply = cryptoCompareClient.getPrice();

        Account account = new Account("Ethereum");

        BigDecimal etherBalance = new BigDecimal(etherScanReply.getResult()).divide(weiToEtherRatio);
        BigDecimal euroBalance = cryptoCompareReply.getEUR().multiply(etherBalance).setScale(2, RoundingMode.HALF_UP);

        Account.Identifier identifier = new Account.Identifier();
        identifier.setName("Address");
        identifier.setValue(address);
        account.addIdentifier(identifier);

        Account.Balance balanceInEuro = new Account.Balance();
        balanceInEuro.setAmount(euroBalance);
        balanceInEuro.setCurrency("EUR");

        Account.Balance balanceInEther = new Account.Balance();
        balanceInEther.setAmount(etherBalance);
        balanceInEther.setCurrency("ether");

        account.addBalance(balanceInEther);
        account.addBalance(balanceInEuro);

        return account;
    }
}
