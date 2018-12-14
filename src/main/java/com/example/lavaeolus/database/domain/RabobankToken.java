package com.example.lavaeolus.database.domain;

import com.example.lavaeolus.AccessTokenResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RabobankToken extends AccessTokenResponse {
    @Id
    @GeneratedValue
    private long rabobankTokenId;

    @JsonProperty("accessToken")
    public String getObfuscatedAccessToken() {
        //Obfuscate secrets in API response
        if (this.getAccessToken() != null && !this.getAccessToken().isEmpty()) {
            int length = this.getAccessToken().length();
            return StringUtils.repeat("*", Math.max(length - 4, 0)) + this.getAccessToken().substring(Math.max(length - 4, 0), length);
        } else return null;
    }

}
