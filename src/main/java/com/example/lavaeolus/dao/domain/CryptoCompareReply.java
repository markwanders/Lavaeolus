package com.example.lavaeolus.dao.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CryptoCompareReply {

    @JsonProperty
    private BigDecimal EUR;

    public BigDecimal getEUR() {
        return EUR;
    }

    public void setEUR(BigDecimal EUR) {
        this.EUR = EUR;
    }
}
