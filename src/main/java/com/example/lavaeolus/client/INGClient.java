package com.example.lavaeolus.client;

import com.example.lavaeolus.AccessTokenResponse;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

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

    @Value("${ing.client-id}")
    private String clientId;

    public AccessTokenResponse registerApplication() {
        String body = "grant_type=client_credentials&scope=greetings%3Aview";

        String path = "/oauth2/token";
        String requestURL = ING_URL + path;

        LOG.info("Sending request to {}", requestURL);
        try {
            HttpHeaders headers = createHeaders(body, path, "post");

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
        } catch (
                HttpClientErrorException e) {
            LOG.error("Did not receive a correct response: {} {}", e.getStatusCode(), e.getResponseBodyAsString());
            return null;
        } catch (
                IOException e) {
            LOG.error("An error occurred mapping the JSON response to an AccessTokenResponse: ", e);
            return null;
        }
    }

    public String getAuthorizationServerUrl() {
        return "";
    }

    private HttpHeaders createHeaders(String body, String path, String method) {
        LOG.debug("Creating headers for message: {}", body);
        String reqId = "someid";
        String date = getServerTime();
        String digest = "SHA-256=" + new String(Base64.getEncoder().encode(DigestUtils.sha256(body)));
        String signature = "(request-target): " + method + " " + path + "\n" +
                "date: " + date + "\n" +
                "digest: " + digest + "\n" +
                "x-ing-reqid: " + reqId;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Signature keyId=\"" + clientId + "\",algorithm=\"rsa-sha256\",headers=\"(request-target) date digest x-ing-reqid\",signature=\"" + signBase64(signature) + "\"");
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
}
