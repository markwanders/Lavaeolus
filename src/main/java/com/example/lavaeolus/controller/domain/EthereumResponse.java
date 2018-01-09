package com.example.lavaeolus.controller.domain;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class EthereumResponse {

    private String accountAddress;

    private BigDecimal accountBalanceInEther;

    private BigDecimal accountBalanceInEuros;

}
