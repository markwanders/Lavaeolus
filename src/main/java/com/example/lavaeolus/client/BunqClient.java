package com.example.lavaeolus.client;

import com.bunq.sdk.context.ApiContext;
import com.bunq.sdk.http.BunqResponse;
import com.bunq.sdk.http.Pagination;
import com.bunq.sdk.model.generated.endpoint.MonetaryAccount;
import com.bunq.sdk.model.generated.endpoint.Payment;
import com.bunq.sdk.model.generated.endpoint.User;
import com.bunq.sdk.model.generated.endpoint.UserPerson;
import com.example.lavaeolus.AccessTokenResponse;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class BunqClient {
    private static final Logger LOG = LoggerFactory.getLogger(BunqClient.class);

    /**
     * Size of each page of payments listing.
     */
    private static final int PAGE_SIZE = 10;

    @Resource
    private RestTemplate restTemplate;

    public List<MonetaryAccount> fetchAccounts(ApiContext apiContext) {
        LOG.debug("Fetching accounts: {}", apiContext);
        List<MonetaryAccount> monetaryAccounts = MonetaryAccount.list(
                apiContext,
                getUserID(apiContext)
        ).getValue();

        LOG.debug("Received accounts from Bunq: {}", monetaryAccounts);

        return monetaryAccounts;
    }

    public List<Payment> fetchPayments(ApiContext apiContext, Integer accountID) {
        LOG.debug("Fetching payments: {} {}", apiContext, accountID);

        Pagination paginationCountOnly = new Pagination();
        paginationCountOnly.setCount(PAGE_SIZE);
        BunqResponse<List<Payment>> paymentListResponse = Payment.list(
                apiContext,
                getUserID(apiContext),
                accountID,
                paginationCountOnly.getUrlParamsCountOnly()
        );

        List<Payment> payments = new ArrayList<>(paymentListResponse.getValue());

        Pagination pagination = paymentListResponse.getPagination();

        if (pagination.hasPreviousPage()) {
            List<Payment> previousPayments = Payment.list(
                    apiContext,
                    getUserID(apiContext),
                    accountID,
                    pagination.getUrlParamsPreviousPage()
            ).getValue();

            payments.addAll(previousPayments);
        }

        LOG.debug("Received transactions from Bunq: {}", payments);

        return payments;
    }

    private Integer getUserID(final ApiContext apiContext) {
        Integer userID = null;
        //First fetch users in current context
        List<User> users = User.list(apiContext).getValue();
        LOG.info("Bunq users in context: {}", users);
        if (users.size() > 0) {
            //Only interested in the first user
            User user = users.get(0);
            LOG.info("First Bunq user in context: {}", user);
            //Only interested in persons, not companies
            UserPerson userPerson = user.getUserPerson();
            if (userPerson != null) {
                userID = userPerson.getId();
                LOG.info("First Bunq userID in context: {}", userID);
            }
        }

        return userID;
    }

    public AccessTokenResponse getAccessToken(String authorizationCode, String clientId, String clientSecret, String redirectURI) {
        String requestURL = "https://api.oauth.bunq.com/v1/token";

        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(requestURL)
                    .queryParam("grant_type", "authorization_code")
                    .queryParam("code", authorizationCode)
                    .queryParam("redirect_uri",redirectURI)
                    .queryParam("client_id", clientId)
                    .queryParam("client_secret", clientSecret);

            LOG.info("Sending request to {}", builder.toUriString());

            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    builder.toUriString(),
                    HttpMethod.POST,
                    null,
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

}
