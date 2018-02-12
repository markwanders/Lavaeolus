package com.example.lavaeolus.controller.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Data
public class Transaction {
    private String counterParty;

    private BigDecimal amount;

    private String currency;

    @Getter(AccessLevel.NONE)
    private LocalDateTime dateTime;

    private String description;

    @JsonProperty("dateTime")
    long getDateTime() {
        return dateTime.atZone(ZoneId.systemDefault()).toEpochSecond();
    }
}
