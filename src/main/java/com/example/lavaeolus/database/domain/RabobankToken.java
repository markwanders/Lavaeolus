package com.example.lavaeolus.database.domain;

import com.example.lavaeolus.AccessTokenResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

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

}
