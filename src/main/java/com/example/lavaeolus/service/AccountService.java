package com.example.lavaeolus.service;

import com.example.lavaeolus.controller.domain.Account;
import com.example.lavaeolus.controller.domain.Transaction;

import java.util.List;

public interface AccountService {
    List<Account> getAccounts();

    List<Transaction> getTransactions(String accountIdentifier);
}
