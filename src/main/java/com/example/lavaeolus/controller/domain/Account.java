package com.example.lavaeolus.controller.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class Account {
    private AccountType type;

    private List<Identifier> identifiers;

    private List<Balance> balances;

    public Account(AccountType type) {
        this.type = type;
    }

    @Data
    public static class Identifier {
        private String name;

        private String value;
    }

    @Data
    public static class Balance {
        private String currency;

        private BigDecimal amount;
    }

    public void addIdentifier(Identifier identifier) {
        if(this.identifiers == null ) {
            this.identifiers = new ArrayList<>();
            this.identifiers.add(identifier);
        } else {
            this.identifiers.add(identifier);
        }
    }

    public void addIdentifiers(List<Identifier> identifiers) {
        if(this.identifiers == null ) {
            this.identifiers = new ArrayList<>();
            this.identifiers.addAll(identifiers);
        } else {
            this.identifiers.addAll(identifiers);
        }
    }

    public void addBalance(Balance balance) {
        if(this.balances == null) {
            this.balances = new ArrayList<>();
            balances.add(balance);
        } else {
            this.balances.add(balance);
        }
    }

    public enum AccountType {
        bunq, ethereum

    }
}
