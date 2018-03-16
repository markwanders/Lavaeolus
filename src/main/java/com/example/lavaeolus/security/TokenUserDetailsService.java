package com.example.lavaeolus.security;

import com.example.lavaeolus.dao.UserRepository;
import com.example.lavaeolus.dao.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class TokenUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

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
}
