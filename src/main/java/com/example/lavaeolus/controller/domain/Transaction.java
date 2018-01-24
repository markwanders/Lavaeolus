package com.example.lavaeolus.controller.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Transaction {
    private String counterParty;

    private BigDecimal amount;

    private String currency;

    private LocalDateTime dateTime;

    private String description;
}
