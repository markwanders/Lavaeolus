package com.example.lavaeolus;

import com.bunq.sdk.context.ApiContext;
import com.bunq.sdk.context.ApiEnvironmentType;
import com.example.lavaeolus.database.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.HashMap;

@Component
public class BunqContextHolder {
    private static final Logger LOG = LoggerFactory.getLogger(BunqContextHolder.class);

    private static final String DEVICE_DESCRIPTION = "Lavaeolus";

    private static String environment;

    @Value("${bunq.environment}")
    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    private static String confLocation;

    @Value("${bunq.conf-location}")
    public void setConfLocation(String confLocation) {
        this.confLocation = confLocation;
    }

    private static HashMap<Long, ApiContext> contextMap = new HashMap<>();

    public static ApiContext getContextForUser(User user) {
        Long userId = user.getId();

        LOG.debug("Querying contextMap for user {}", userId);

        if(contextMap.get(userId) != null) {
            LOG.debug("Found APIContext for user {}", userId);

            return contextMap.get(userId);
        } else {
            if(user.getBunqKey() != null && !user.getBunqKey().isEmpty()) {
                LOG.debug("No APIContext found for user {}, creating new APIContext", userId);
                String confFileString = confLocation + userId + ".conf";

                ApiContext apiContext;
                ApiEnvironmentType apiEnvironment = ApiEnvironmentType.valueOf(environment);
                File confFile = new File(confFileString);
                if(confFile.exists()) {
                    LOG.info("Existing Bunq config file found, restoring context for {}", apiEnvironment);
                    apiContext = ApiContext.restore(confFileString);
                } else {
                    LOG.info("No existing Bunq config file found, creating new context for {}", apiEnvironment);
                    apiContext = ApiContext.create(apiEnvironment, user.getBunqKey(),
                            DEVICE_DESCRIPTION);
                    apiContext.save(confFileString);
                }

                contextMap.put(userId, apiContext);

                return apiContext;
            } else {
                LOG.error("User {} has no Bunq key, cannot create APIContext", userId);
                return null;
            }

        }
    }

}
