package com.example.lavaeolus.service;

import com.example.lavaeolus.controller.domain.Account;
import com.example.lavaeolus.dao.BunqClient;
import com.example.lavaeolus.dao.domain.BunqReply;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class BunqService {

    private static final BigDecimal weiToEtherRatio = new BigDecimal("1000000000000000000");

    @Autowired
    private BunqClient bunqClient;


    public Account getAccount() {
        BunqReply bunqReply = bunqClient.fetchAccount();

        Account account = new Account("Bunq");

        Account.Identifier IBANIdentifier = new Account.Identifier();
        IBANIdentifier.setName("IBAN");
        IBANIdentifier.setValue(bunqReply.getIBAN());
        account.addIdentifier(IBANIdentifier);

        Account.Identifier nameIdentifier = new Account.Identifier();
        nameIdentifier.setName("Name");
        nameIdentifier.setValue(bunqReply.getName());
        account.addIdentifier(nameIdentifier);

        Account.Balance balance = new Account.Balance();
        balance.setAmount(new BigDecimal(bunqReply.getBalance()));
        balance.setCurrency(bunqReply.getCurrency());
        account.addBalance(balance);

        return account;
    }
}
