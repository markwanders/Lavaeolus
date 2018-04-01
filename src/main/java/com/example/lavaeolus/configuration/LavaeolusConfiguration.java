package com.example.lavaeolus.configuration;

import com.bunq.sdk.context.ApiContext;
import com.bunq.sdk.context.ApiEnvironmentType;
import com.bunq.sdk.json.BunqGsonBuilder;
import com.example.lavaeolus.dao.UserRepository;
import com.example.lavaeolus.dao.domain.Role;
import com.example.lavaeolus.dao.domain.User;
import com.google.gson.Gson;
import com.launchdarkly.client.LDClient;
import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class LavaeolusConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(LavaeolusConfiguration.class);

    private static final String DEVICE_DESCRIPTION = "Lavaeolus";

    protected static Gson gson = BunqGsonBuilder.buildDefault().create();

    @Value("${bunq.api-key}")
    private String apiKey;

    @Value("${bunq.environment}")
    private String environment;

    @Value("${bunq.conf-location}")
    private String confLocation;

    @Value("${lavaeolus.database-url}")
    private String databaseURL;

    @Value("${launchdarkly.sdk-key}")
    private String launchDarklySDKKey;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public ApiContext apiContext() {
        ApiContext apiContext;
        ApiEnvironmentType apiEnvironment = ApiEnvironmentType.valueOf(environment);
        File confFile = new File(confLocation);
        if(confFile.exists()) {
            LOG.info("Existing Bunq config file found, restoring context for {}", apiEnvironment);
            apiContext = ApiContext.restore(confLocation);
        } else {
            LOG.info("No existing Bunq config file found, creating new context for {}", apiEnvironment);
            apiContext = ApiContext.create(apiEnvironment, apiKey,
                    DEVICE_DESCRIPTION);
            apiContext.save(confLocation);
        }

        return apiContext;
    }

    @Bean
    public InitializingBean insertDefaultUsers() {
        return new InitializingBean() {
            private final static String DEFAULT_USER = "admin";
            private final static String DEFAULT_PASSWORD = "admin";

            @Autowired
            private UserRepository userRepository;

            @Override
            public void afterPropertiesSet() throws Exception {
                //Add default user if not already present
                if(!userRepository.findOneByUsername(DEFAULT_USER).isPresent()) {
                    addUser(DEFAULT_USER, DEFAULT_PASSWORD);
                }
            }

            private void addUser(String username, String password) {
                User user = new User();
                user.setUsername(username);
                user.setPassword(new BCryptPasswordEncoder().encode(password));
                user.setRole(username.equals(DEFAULT_USER) ? Role.ADMIN : Role.USER);
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

    @Bean
    public LDClient ldClient() {
        return new LDClient(launchDarklySDKKey);
    }
}
