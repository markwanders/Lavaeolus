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
public class BunqToken {
    @Id
    @GeneratedValue
    private long bunqTokenId;

    private String accessToken;

    private Integer expiresIn;

    private String refreshToken;

    private String scope;

    private AccessTokenResponse.TokenTypeEnum tokenType;

    @JsonProperty("accessToken")
    public String getObfuscatedAccessToken() {
        //Obfuscate secrets in API response
        if (this.accessToken != null && !this.accessToken.isEmpty()) {
            int length = this.accessToken.length();
            return StringUtils.repeat("*", Math.max(length - 4, 0)) + this.accessToken.substring(Math.max(length - 4, 0), length);
        } else return null;
    }

    public long getRabobankTokenId() {
        return bunqTokenId;
    }

    public void setRabobankTokenId(long rabobankTokenId) {
        this.bunqTokenId = rabobankTokenId;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Integer getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Integer expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public AccessTokenResponse.TokenTypeEnum getTokenType() {
        return tokenType;
    }

    public void setTokenType(AccessTokenResponse.TokenTypeEnum tokenType) {
        this.tokenType = tokenType;
    }
}
