package com.example.lavaeolus.service;

import com.example.lavaeolus.controller.domain.Account;
import com.example.lavaeolus.controller.domain.Transaction;
import com.example.lavaeolus.dao.CryptoCompareClient;
import com.example.lavaeolus.dao.EtherScanClient;
import com.example.lavaeolus.dao.domain.CryptoCompareReply;
import com.example.lavaeolus.dao.domain.EtherScanReply;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class EthereumService implements AccountService {

    private static final BigDecimal weiToEtherRatio = new BigDecimal("1000000000000000000");

    @Autowired
    private EtherScanClient etherScanClient;

    @Autowired
    private CryptoCompareClient cryptoCompareClient;

    @Value("${etherscan.address}")
    private String[] addresses;

    public List<Account> getAccounts() {
        List<Account> accounts = new ArrayList<>();

        CryptoCompareReply cryptoCompareReply = cryptoCompareClient.getPrice();

        for(String address : addresses) {
            EtherScanReply etherScanReply = etherScanClient.getBalance(address);

            Account account = new Account(Account.AccountType.ethereum);

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

            accounts.add(account);
        }

        return accounts;
    }

    @Override
    public List<Transaction> getTransactions(String accountIdentifier) {
        return null;
    }
}
