package com.example.lavaeolus.security;

import com.example.lavaeolus.controller.domain.Account;
import com.example.lavaeolus.database.UserRepository;
import com.example.lavaeolus.database.domain.BunqToken;
import com.example.lavaeolus.database.domain.RabobankToken;
import com.example.lavaeolus.database.domain.Role;
import com.example.lavaeolus.database.domain.User;
import com.example.lavaeolus.security.domain.TokenUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class TokenUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenAuthenticationService tokenAuthenticationService;

    private final AccountStatusUserDetailsChecker accountStatusUserDetailsChecker = new AccountStatusUserDetailsChecker();

    @Override
    public TokenUser loadUserByUsername(String username) throws UsernameNotFoundException {
        final User user = userRepository.findOneByUsername(username).orElseThrow(() -> new UsernameNotFoundException(""));

        TokenUser tokenUser = new TokenUser(user);
        accountStatusUserDetailsChecker.check(tokenUser);

        return tokenUser;
    }

    public TokenUser changePasswordByUsername(String username, String newPassword) throws UsernameNotFoundException {
        final User user = userRepository.findOneByUsername(username).orElseThrow(() -> new UsernameNotFoundException(""));

        TokenUser tokenUser = new TokenUser(user);
        accountStatusUserDetailsChecker.check(tokenUser);

        user.setPassword(new BCryptPasswordEncoder().encode(newPassword));
        tokenUser = new TokenUser(userRepository.save(user));

        return tokenUser;
    }

    public TokenUser changeKeyByUsername(String username, Object newKey, Account.AccountType accountType) throws UsernameNotFoundException {
        final User user = userRepository.findOneByUsername(username).orElseThrow(() -> new UsernameNotFoundException(""));

        TokenUser tokenUser = new TokenUser(user);
        accountStatusUserDetailsChecker.check(tokenUser);

        switch (accountType) {
            case bunq:
//                user.setBunqKey((String) newKey);
                user.setBunqToken((BunqToken) newKey);
                break;
            case ethereum:
                user.setEthereumAddress((String) newKey);
                break;
            case rabobank:
                user.setRabobankToken((RabobankToken) newKey);
                break;
        }
        tokenUser = new TokenUser(userRepository.save(user));

        return tokenUser;
    }

    public TokenUser deleteAccountByUsername(String username, Account.AccountType accountType) throws UsernameNotFoundException {
        final User user = userRepository.findOneByUsername(username).orElseThrow(() -> new UsernameNotFoundException(""));

        TokenUser tokenUser = new TokenUser(user);
        accountStatusUserDetailsChecker.check(tokenUser);

        switch (accountType) {
            case bunq:
                user.setBunqToken(null);
                break;
            case ethereum:
                user.setEthereumAddress(null);
                break;
            case rabobank:
                user.setRabobankToken(null);
                break;
        }
        tokenUser = new TokenUser(userRepository.save(user));

        return tokenUser;
    }

    public Map<String, String> registerNewUserAndReturnTokenHeader(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(new BCryptPasswordEncoder().encode(password));
        user.setRole(Role.USER);
        user = userRepository.save(user);

        return createTokenHeader(user);
    }

    public Map<String, String> createTokenHeader(User user) {
        return tokenAuthenticationService.createTokenHeaderForUser(new TokenUser(user));
    }
}
