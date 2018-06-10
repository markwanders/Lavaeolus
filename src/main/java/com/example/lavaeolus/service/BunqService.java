package com.example.lavaeolus.service;

import com.bunq.sdk.model.generated.endpoint.MonetaryAccount;
import com.bunq.sdk.model.generated.endpoint.Payment;
import com.bunq.sdk.model.generated.object.Pointer;
import com.example.lavaeolus.controller.domain.Account;
import com.example.lavaeolus.controller.domain.Transaction;
import com.example.lavaeolus.dao.BunqClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class BunqService implements AccountService {
    private static final Logger LOG = LoggerFactory.getLogger(BunqService.class);

    private static final String IBAN = "IBAN";

    @Autowired
    private BunqClient bunqClient;

    public List<Account> getAccounts() {
        List<Account> accounts = new ArrayList<>();

        try {
            List<MonetaryAccount> monetaryAccounts = bunqClient.fetchAccounts();

            for (MonetaryAccount monetaryAccount : monetaryAccounts) {
                Account account = createAccountFromBunqAccount(monetaryAccount);
                accounts.add(account);
            }
        } catch (Exception e) {
            LOG.error("Something went wrong fetching Bunq accounts: ", e);
        }

        return accounts;
    }

    public List<Transaction> getTransactions(String accountIdentifier) {
        List<Transaction> transactions = new ArrayList<>();

        try {
            List<Payment> bunqPayments = bunqClient.fetchPayments(Integer.valueOf(accountIdentifier));

            for (Payment bunqPayment : bunqPayments) {
                BigDecimal amount = new BigDecimal(bunqPayment.getAmount().getValue());
                Transaction transaction = new Transaction();
                transaction.setAmount(amount);
                transaction.setCurrency(bunqPayment.getAmount().getCurrency());

                String displayName = bunqPayment.getCounterpartyAlias().getLabelMonetaryAccount().getDisplayName();
                String iban = bunqPayment.getCounterpartyAlias().getLabelMonetaryAccount().getIban();

                transaction.setCounterParty(displayName == null ? iban : displayName);
                transaction.setDateTime(LocalDateTime.parse(bunqPayment.getCreated(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")));
                transaction.setDescription(bunqPayment.getDescription());

                transactions.add(transaction);
            }
        } catch (Exception e) {
            LOG.error("Something went wrong fetching Bunq transactions: ", e);
        }

        return transactions;
    }

    private Account createAccountFromBunqAccount(MonetaryAccount monetaryAccount) {
        Account account = new Account(Account.AccountType.bunq);

        Account.Identifier primaryIdentifier = new Account.Identifier();
        primaryIdentifier.setName("id");
        primaryIdentifier.setValue(monetaryAccount.getMonetaryAccountBank().getId().toString());
        account.addIdentifier(primaryIdentifier);

        account.addIdentifiers(createIdentifiersFromAlias(monetaryAccount.getMonetaryAccountBank().getAlias()));

        Account.Balance balance = new Account.Balance();
        balance.setAmount(new BigDecimal(monetaryAccount.getMonetaryAccountBank().getBalance().getValue()));
        balance.setCurrency(monetaryAccount.getMonetaryAccountBank().getBalance().getCurrency());
        account.addBalance(balance);

        return account;
    }

    private List<Account.Identifier> createIdentifiersFromAlias(List<Pointer> pointers) {
        List<Account.Identifier> identifiers = new ArrayList<>();

        Account.Identifier IBANIdentifier = new Account.Identifier();
        IBANIdentifier.setName(IBAN);

        Account.Identifier nameIdentifier = new Account.Identifier();
        nameIdentifier.setName("Name");

        for (Pointer alias : pointers) {
            if(IBAN.equals(alias.getType())) {
                IBANIdentifier.setValue(alias.getValue());
                nameIdentifier.setValue(alias.getName());
            }
        }

        identifiers.add(nameIdentifier);
        identifiers.add(IBANIdentifier);

        return identifiers;
    }
}
