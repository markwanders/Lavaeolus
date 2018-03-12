package com.example.lavaeolus.security;

import com.example.lavaeolus.dao.UserRepository;
import com.example.lavaeolus.dao.domain.LavaeolusUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class TokenUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    private final AccountStatusUserDetailsChecker accountStatusUserDetailsChecker = new AccountStatusUserDetailsChecker();

    @Override
    public TokenUser loadUserByUsername(String username) throws UsernameNotFoundException {
        final LavaeolusUser user = userRepository.findOneByUsername(username).orElseThrow(() -> new UsernameNotFoundException(""));

        TokenUser tokenUser = new TokenUser(user);
        accountStatusUserDetailsChecker.check(tokenUser);

        return tokenUser;
    }
}
