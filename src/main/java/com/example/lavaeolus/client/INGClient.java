package com.example.lavaeolus.client;

import com.example.lavaeolus.AccessTokenResponse;
import com.example.lavaeolus.Greeting;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.text.SimpleDateFormat;
import java.util.*;


@Component
public class INGClient {
    private static final Logger LOG = LoggerFactory.getLogger(INGClient.class);

    private static final String ING_URL = "https://api.ing.com";

    @Autowired
    private RestTemplate mutualTLSRestTemplate;

    @Autowired
    private PrivateKey privateKey;

    @Autowired
    private Signature signature;

    private File file = new File(getClass().getResource(".").getFile() +  "/ing_access_token.json");

    @Value("${ing.client-id}")
    private String clientId;

    private AccessTokenResponse accessToken;

    @PostConstruct
    private void setup() throws IOException {
        try {
            this.accessToken = readTokenFromFile();
            //TODO: check if expired
        } catch(FileNotFoundException fileNotFoundException) {
            this.accessToken = getAccessToken();
            writeTokenToFile(accessToken);
        }
    }

    private AccessTokenResponse getAccessToken() {
        String body = "grant_type=client_credentials&scope=greetings%3Aview";

        String path = "/oauth2/token";
        String requestURL = ING_URL + path;

        LOG.info("Sending request to {}", requestURL);
        try {
            HttpHeaders headers = createHeaders(path, "post", body);

            HttpEntity request = new HttpEntity<>(body, headers);

            ResponseEntity<String> responseEntity = mutualTLSRestTemplate.exchange(
                    requestURL,
                    HttpMethod.POST,
                    request,
                    String.class);

            LOG.info("Received response: {}", responseEntity);

            return new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
                    .readValue(responseEntity.getBody(), AccessTokenResponse.class);
        } catch (HttpClientErrorException e) {
            LOG.error("Did not receive a correct response: {} {}", e.getStatusCode(), e.getResponseBodyAsString());
            return null;
        } catch (IOException e) {
            LOG.error("An error occurred mapping the JSON response to an AccessTokenResponse: ", e);
            return null;
        }
    }

    public Greeting viewGreeting() {
        String path = "/greetings/single";
        String requestURL = ING_URL + path;

        LOG.info("Sending request to {}", requestURL);
        try {
            HttpHeaders headers = createHeaders(path, "get", "");

            HttpEntity request = new HttpEntity<>(headers);

            ResponseEntity<String> responseEntity = mutualTLSRestTemplate.exchange(
                    requestURL,
                    HttpMethod.GET,
                    request,
                    String.class);

            LOG.info("Received response: {}", responseEntity);

            return new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
                    .readValue(responseEntity.getBody(), Greeting.class);
        } catch (HttpClientErrorException e) {
            LOG.error("Did not receive a correct response: {} {}", e.getStatusCode(), e.getResponseBodyAsString());
            if(e.getStatusCode().equals(HttpStatus.UNAUTHORIZED)) {
                deleteToken();
                this.accessToken = getAccessToken();
            }
            return null;
        } catch (IOException e) {
            LOG.error("An error occurred mapping the JSON response to an AccessTokenResponse: ", e);
            return null;
        }
    }

    private HttpHeaders createHeaders(String path, String method, String body) {
        LOG.debug("Creating headers for message: {} {} {}", path, method, body);

        String reqId = UUID.randomUUID().toString();
        String date = getServerTime();
        String digest = "SHA-256=" + new String(Base64.getEncoder().encode(DigestUtils.sha256(body)));
        String stringToSign = "(request-target): " + method + " " + path + "\n" +
                "date: " + date + "\n" +
                "digest: " + digest + "\n" +
                "x-ing-reqid: " + reqId;
        String signature = "keyId=\"" + clientId + "\",algorithm=\"rsa-sha256\",headers=\"(request-target) date digest x-ing-reqid\",signature=\"" + signBase64(stringToSign) + "\"";

        HttpHeaders headers = new HttpHeaders();
        if(accessToken != null) {
            headers.set("Authorization", "Bearer " + accessToken.getAccessToken());
            headers.set("Signature", signature);
        } else {
            headers.set("Authorization", "Signature " + signature);
        }
        headers.set("X-ING-ReqID", reqId);
        headers.set("Date", date);
        headers.set("Digest", digest);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        LOG.debug("Created request headers: {}", headers);

        return headers;
    }

    private String signBase64(String message) {
        LOG.debug("Signing data: {}", message);
        try {
            signature.initSign(privateKey);
            signature.update(message.getBytes());
            byte[] signatureBytes = signature.sign();

            return Base64.getEncoder().encodeToString(signatureBytes);
        } catch (SignatureException | InvalidKeyException e) {
            throw new RuntimeException("Failed to sign string: ", e);
        }
    }

    private String getServerTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(calendar.getTime());
    }

    private void writeTokenToFile(AccessTokenResponse accessTokenResponse) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
        writer.writeValue(file, accessTokenResponse);
    }

    private void deleteToken() {
        this.accessToken = null;
        file.delete();
    }

    private AccessTokenResponse readTokenFromFile() throws IOException {
        InputStream in = new FileInputStream(file);

        ObjectMapper objectMapper = new ObjectMapper()
                .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);

        AccessTokenResponse ingAccessToken = objectMapper.readValue(in, AccessTokenResponse.class);
        LOG.info("Read INGAccessToken from file: {}", ingAccessToken);

        if(ingAccessToken == null) {
            throw new FileNotFoundException();
        }

        return ingAccessToken;
    }
}
