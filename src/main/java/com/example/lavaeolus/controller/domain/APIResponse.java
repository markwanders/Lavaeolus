package com.example.lavaeolus.controller.domain;

public class APIResponse {

    private String accountAddress;

    private long accountBalance;

    public String getAccountAddress() {
        return accountAddress;
    }

    public void setAccountAddress(String accountAddress) {
        this.accountAddress = accountAddress;
    }

    public long getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(long accountBalance) {
        this.accountBalance = accountBalance;
    }
}
