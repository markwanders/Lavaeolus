package com.example.lavaeolus.service;

import com.example.lavaeolus.controller.domain.Account;
import com.example.lavaeolus.dao.BunqClient;
import com.example.lavaeolus.dao.domain.BunqAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class BunqService {

    private static final BigDecimal weiToEtherRatio = new BigDecimal("1000000000000000000");

    @Autowired
    private BunqClient bunqClient;

    public List<Account> getAccounts() {
        List<Account> accounts = new ArrayList<>();

        List<BunqAccount> bunqAccounts = bunqClient.fetchAccounts();

        for(BunqAccount bunqAccount : bunqAccounts) {
            Account account = new Account("Bunq");

            Account.Identifier IBANIdentifier = new Account.Identifier();
            IBANIdentifier.setName("IBAN");
            IBANIdentifier.setValue(bunqAccount.getIBAN());
            account.addIdentifier(IBANIdentifier);

            Account.Identifier nameIdentifier = new Account.Identifier();
            nameIdentifier.setName("Name");
            nameIdentifier.setValue(bunqAccount.getName());
            account.addIdentifier(nameIdentifier);

            Account.Balance balance = new Account.Balance();
            balance.setAmount(new BigDecimal(bunqAccount.getBalance()));
            balance.setCurrency(bunqAccount.getCurrency());
            account.addBalance(balance);

            accounts.add(account);
        }

        return accounts;
    }
}
