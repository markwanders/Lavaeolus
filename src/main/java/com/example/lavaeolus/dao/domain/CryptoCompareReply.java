package com.example.lavaeolus.dao.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class CryptoCompareReply {

    @JsonProperty
    private BigDecimal EUR;

}
