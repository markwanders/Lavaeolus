package com.example.lavaeolus.database.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.util.ArrayList;

@Entity
@Table(name="\"User\"")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(unique=true)
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
        if(this.bunqKey != null && !this.bunqKey.isEmpty()) {
            int length = this.bunqKey.length();
            return StringUtils.repeat("*", length - 4) + this.bunqKey.substring(length - 4, length);
        } else return null;
    }


    private ArrayList<String> ethereumAddresses;

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

    @ElementCollection
    @OneToMany(fetch=FetchType.EAGER, mappedBy = "id")
    public ArrayList<String> getEthereumAddresses() {
        return ethereumAddresses;
    }

    public void setEthereumAddresses(ArrayList<String> ethereumAddresses) {
        this.ethereumAddresses = ethereumAddresses;
    }
}
