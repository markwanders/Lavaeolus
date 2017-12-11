package com.example.lavaeolus.configuration;

import com.bunq.sdk.context.ApiContext;
import com.bunq.sdk.context.ApiEnvironmentType;
import com.bunq.sdk.json.BunqGsonBuilder;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;

import java.io.File;

@Configuration
public class LavaeolusConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(LavaeolusConfiguration.class);

    private static final String API_KEY = "bunq.api-key";
    private static final String DEVICE_DESCRIPTION = "Lavaeolus.";
    private static final String API_CONTEXT_FILE_PATH = "bunq.conf-location";
    private static final String API_ENVIRONMENT = "bunq.environment";

    protected static Gson gson = BunqGsonBuilder.buildDefault().create();

    @Autowired
    private Environment environment;
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public ApiContext apiContext() {
        ApiContext apiContext;
        ApiEnvironmentType apiEnvironment = ApiEnvironmentType.valueOf(environment.getProperty(API_ENVIRONMENT));
        String confLocation = environment.getProperty(API_CONTEXT_FILE_PATH);
        File confFile = new File(confLocation);
        if(confFile.exists()) {
            LOG.info("Existing Bunq config file found, restoring context for {}", apiEnvironment);
            apiContext = ApiContext.restore(confLocation);
        } else {
            LOG.info("No existing Bunq config file found, creating new context for {}", apiEnvironment);
            String apiKey = environment.getProperty(API_KEY);
            apiContext = ApiContext.create(apiEnvironment, apiKey,
                    DEVICE_DESCRIPTION);
            apiContext.save(confLocation);
        }

        return apiContext;
    }
}
