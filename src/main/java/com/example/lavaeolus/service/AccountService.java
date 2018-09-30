package com.example.lavaeolus.service;

import com.example.lavaeolus.controller.domain.Account;
import com.example.lavaeolus.controller.domain.Transaction;
import com.example.lavaeolus.database.domain.User;

import java.util.List;

public interface AccountService {
    List<Account> getAccounts(User user);

    List<Transaction> getTransactions(User user, String accountIdentifier);
}
