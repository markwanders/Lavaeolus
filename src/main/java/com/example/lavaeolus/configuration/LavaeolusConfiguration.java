package com.example.lavaeolus.configuration;

import com.example.lavaeolus.database.UserRepository;
import com.example.lavaeolus.database.domain.Role;
import com.example.lavaeolus.database.domain.User;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.Assert;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.*;
import java.security.cert.CertificateException;

@Configuration
public class LavaeolusConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(LavaeolusConfiguration.class);

    @Value("${lavaeolus.database-url}")
    private String databaseURL;

    @Value("${lavaeolus.signing-key-alias}")
    private String signingAlias;

    @Value("${lavaeolus.signing-key-store}")
    private String signingKeyStore;

    @Value("${lavaeolus.signing-key-store-password}")
    private String signingKeyStorePassword;

    @Value("${server.ssl.key-store}")
    private String sslKeyStore;

    @Value("${server.ssl.key-store-password}")
    private String sslKeyStorePassword;

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

    @Bean
    public PrivateKey privateKey() {
        try {
            InputStream keyStoreInputStream = Thread.currentThread().getContextClassLoader().
                    getResourceAsStream(signingKeyStore);
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            char[] keyStorePassword = signingKeyStorePassword.toCharArray();
            keyStore.load(keyStoreInputStream, keyStorePassword);

            //Fetch our private key we use for signing
            Key key = keyStore.getKey(signingAlias,keyStorePassword);
            PrivateKey privateKey = (PrivateKey) key;
            Assert.notNull(privateKey, "Private key not found for alias " + signingAlias);

            LOG.debug("Found private key {} for alias {}", privateKey, signingAlias);

            return privateKey;

        } catch (IOException | CertificateException | NoSuchAlgorithmException | UnrecoverableKeyException | KeyStoreException e) {
            throw new IllegalArgumentException("Failed to load public and private keys: ", e);
        }
    }

    @Bean
    public Signature signature() throws NoSuchAlgorithmException {
        return Signature.getInstance("SHA256withRSA");
    }

    @Bean
    public RestTemplate mutualTLSRestTemplate(RestTemplateBuilder builder) throws Exception {
        char[] password = sslKeyStorePassword.toCharArray();

        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        File key = ResourceUtils.getFile(sslKeyStore);
        try (InputStream in = new FileInputStream(key)) {
            keyStore.load(in, password);
        }

        SSLContext sslContext = SSLContextBuilder.create()
                .loadKeyMaterial(keyStore, password)
                .loadTrustMaterial(null, new TrustSelfSignedStrategy()).build();

        HttpClient client = HttpClients.custom().setSSLContext(sslContext).build();
        return builder
                .requestFactory(new HttpComponentsClientHttpRequestFactory(client))
                .build();
    }

}
