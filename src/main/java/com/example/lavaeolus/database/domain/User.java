package com.example.lavaeolus.database.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;

@Entity
@Table(name = "\"User\"")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(unique = true)
    private String username;

    // Default to empty string for Auths created by JWT requests
    @JsonIgnore
    private String password = "";

    @Enumerated(EnumType.STRING)
    private Role role;

    private String bunqKey;

    @JsonProperty("bunqKey")
    public String getObfuscatedBunqKey() {
        //Obfuscate secrets in API response
        if (this.bunqKey != null && !this.bunqKey.isEmpty()) {
            int length = this.bunqKey.length();
            return StringUtils.repeat("*", Math.max(length - 4, 0)) + this.bunqKey.substring(Math.max(length - 4, 0), length);
        } else return null;
    }

    @OneToOne
    @JoinColumn(name = "rabobankTokenId")
    private RabobankToken rabobankToken;

    //todo: use private key instead so we can also do payments etc.
    private String ethereumAddress;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBunqKey() {
        return bunqKey;
    }

    public void setBunqKey(String bunqKey) {
        this.bunqKey = bunqKey;
    }

    public String getEthereumAddress() {
        return ethereumAddress;
    }

    public void setEthereumAddress(String ethereumAddress) {
        this.ethereumAddress = ethereumAddress;
    }

    public RabobankToken getRabobankToken() {
        return rabobankToken;
    }

    public void setRabobankToken(RabobankToken rabobankToken) {
        this.rabobankToken = rabobankToken;
    }
}
