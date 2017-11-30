package com.example.lavaeolus.controller.domain;

import java.math.BigDecimal;

public class APIResponse {

    private String accountAddress;

    private BigDecimal accountBalanceInEther;

    private BigDecimal accountBalanceInEuros;

    public String getAccountAddress() {
        return accountAddress;
    }

    public void setAccountAddress(String accountAddress) {
        this.accountAddress = accountAddress;
    }

    public BigDecimal getAccountBalanceInEther() {
        return accountBalanceInEther;
    }

    public void setAccountBalanceInEther(BigDecimal accountBalance) {
        this.accountBalanceInEther = accountBalance;
    }

    public BigDecimal getAccountBalanceInEuros() {
        return accountBalanceInEuros;
    }

    public void setAccountBalanceInEuros(BigDecimal accountBalanceInEuros) {
        this.accountBalanceInEuros = accountBalanceInEuros;
    }
}
