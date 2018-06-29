package com.example.lavaeolus.security;

import com.example.lavaeolus.database.UserRepository;
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

    public Map<String,String> registerNewUserAndReturnTokenHeader(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setRole(Role.USER);
        user = userRepository.save(user);

        TokenUser tokenUser = new TokenUser(user);

        return tokenAuthenticationService.createTokenHeaderForUser(tokenUser);

    }
}
