package com.example.lavaeolus;

import com.example.lavaeolus.client.INGClient;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.*;

@Component
public class INGAccessTokenHolder {
    private static final Logger LOG = LoggerFactory.getLogger(INGAccessTokenHolder.class);

    @Autowired
    private INGClient ingClient;

    @Bean
    public AccessTokenResponse ingAccessToken() throws IOException {
        AccessTokenResponse ingAccessToken;
        String fileLocation = "classpath:ing_access_token.json";
        try {
            File file = ResourceUtils.getFile(fileLocation);
            ingAccessToken = readTokenFromFile(file);
            //TODO: check if expired
        } catch(FileNotFoundException fileNotFoundException) {
            ingAccessToken = ingClient.registerApplication();
            writeTokenToFile(new File(fileLocation), ingAccessToken);
        }
        return ingAccessToken;
    }

    private void writeTokenToFile(File file, AccessTokenResponse accessTokenResponse) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
        writer.writeValue(file, accessTokenResponse);
    }

    private AccessTokenResponse readTokenFromFile(File file) throws IOException {
        InputStream in = new FileInputStream(file);

        ObjectMapper objectMapper = new ObjectMapper()
                .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);

        AccessTokenResponse ingAccessToken = objectMapper.readValue(in, AccessTokenResponse.class);
        LOG.info("Read INGAccessToken from file: {}", ingAccessToken);
        return ingAccessToken;
    }
}
