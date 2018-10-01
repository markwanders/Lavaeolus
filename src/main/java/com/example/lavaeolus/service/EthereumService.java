package com.example.lavaeolus.service;

import com.example.lavaeolus.controller.domain.Account;
import com.example.lavaeolus.controller.domain.Transaction;
import com.example.lavaeolus.client.CryptoCompareClient;
import com.example.lavaeolus.client.EtherScanClient;
import com.example.lavaeolus.client.domain.CryptoCompareReply;
import com.example.lavaeolus.client.domain.EtherScanBalance;
import com.example.lavaeolus.client.domain.EtherScanTransactions;
import com.example.lavaeolus.database.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

@Service
public class EthereumService implements AccountService {
    private static final Logger LOG = LoggerFactory.getLogger(EthereumService.class);

    private static final BigDecimal weiToEtherRatio = new BigDecimal("1000000000000000000");

    @Autowired
    private EtherScanClient etherScanClient;

    @Autowired
    private CryptoCompareClient cryptoCompareClient;

    @Value("${etherscan.address}")
    private String[] addresses;

    public List<Account> getAccounts(User user) {
        List<Account> accounts = new ArrayList<>();

        try {
            CryptoCompareReply cryptoCompareReply = cryptoCompareClient.getPrice();

            for (String address : addresses) {
                EtherScanBalance etherScanBalance = etherScanClient.getBalance(address);

                Account account = new Account(Account.AccountType.ethereum);

                BigDecimal etherBalance = new BigDecimal(etherScanBalance.getResult()).divide(weiToEtherRatio);
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

        } catch (Exception e) {
            LOG.error("Something went wrong fetching Ethereum accounts: ", e);
        }

        return accounts;
    }

    @Override
    public List<Transaction> getTransactions(User user, String accountIdentifier) {
        List<Transaction> transactions = new ArrayList<>();

        try {
            EtherScanTransactions etherScanTransactions = etherScanClient.getTransactions(accountIdentifier);

            for (EtherScanTransactions.EthereumTransaction ethereumTransaction : etherScanTransactions.getResult()) {
                Transaction transaction = new Transaction();
                transaction.setCurrency("ether");
                transaction.setAmount(new BigDecimal(ethereumTransaction.getValue()).divide(weiToEtherRatio));
                transaction.setDescription("Block: #" + ethereumTransaction.getBlockNumber());
                transaction.setCounterParty(ethereumTransaction.getFrom().equals(accountIdentifier) ? ethereumTransaction.getTo() : ethereumTransaction.getFrom());
                transaction.setDateTime(LocalDateTime.ofInstant(Instant.ofEpochSecond(new Long(ethereumTransaction.getTimeStamp())),
                        TimeZone.getDefault().toZoneId()));
                transactions.add(transaction);
            }
        } catch (Exception e) {
            LOG.error("Something went wrong fetching Ethereum transactions: {}", e);

        }

        return transactions;
    }
}
