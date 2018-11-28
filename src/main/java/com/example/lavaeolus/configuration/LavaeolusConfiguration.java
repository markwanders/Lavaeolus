package com.example.lavaeolus.configuration;

import com.example.lavaeolus.database.UserRepository;
import com.example.lavaeolus.database.domain.Role;
import com.example.lavaeolus.database.domain.User;
import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class LavaeolusConfiguration {

    @Value("${lavaeolus.database-url}")
    private String databaseURL;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public InitializingBean insertDefaultUsers() {
        return new InitializingBean() {
            private final static String DEFAULT_USER = "admin";
            private final static String DEFAULT_PASSWORD = "admin";

            @Autowired
            private UserRepository userRepository;

            @Override
            public void afterPropertiesSet() {
                //Add default user if not already present
                if(!userRepository.findOneByUsername(DEFAULT_USER).isPresent()) {
                    addUser();
                }
            }

            private void addUser() {
                User user = new User();
                user.setUsername(DEFAULT_USER);
                user.setPassword(new BCryptPasswordEncoder().encode(DEFAULT_PASSWORD));
                user.setRole(Role.ADMIN);
                userRepository.save(user);
            }
        };
    }

    @Bean
    public BasicDataSource dataSource() throws URISyntaxException {
        URI dbUri = new URI(databaseURL);

        String username = dbUri.getUserInfo().split(":")[0];
        String password = dbUri.getUserInfo().split(":")[1];
        String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath() + "?sslmode=require";

        BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setUrl(dbUrl);
        basicDataSource.setUsername(username);
        basicDataSource.setPassword(password);

        return basicDataSource;
    }
}
