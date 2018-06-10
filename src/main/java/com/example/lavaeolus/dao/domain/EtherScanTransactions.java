package com.example.lavaeolus.dao.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class EtherScanTransactions {

    private List<EthereumTransaction> result;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class EthereumTransaction {
        private String blockNumber;

        private String timeStamp;

        private String from;

        private String to;

        private String value;
    }
}
