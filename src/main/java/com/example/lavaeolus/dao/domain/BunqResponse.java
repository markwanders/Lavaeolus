package com.example.lavaeolus.dao.domain;

import lombok.Data;

@Data
public class BunqResponse {
    private String balance;

    private String currency;

    private String IBAN;

    private String name;
}
