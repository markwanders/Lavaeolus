package com.example.lavaeolus.dao;

import com.bunq.sdk.context.ApiContext;
import com.bunq.sdk.http.BunqResponse;
import com.bunq.sdk.http.Pagination;
import com.bunq.sdk.model.generated.endpoint.MonetaryAccount;
import com.bunq.sdk.model.generated.endpoint.Payment;
import com.bunq.sdk.model.generated.endpoint.User;
import com.bunq.sdk.model.generated.endpoint.UserPerson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BunqClient {
    private static final Logger LOG = LoggerFactory.getLogger(BunqClient.class);

    /**
     * Size of each page of payments listing.
     */
    private static final int PAGE_SIZE = 3;

    @Autowired
    private ApiContext apiContext;

    public List<MonetaryAccount> fetchAccounts() {
        List<MonetaryAccount> monetaryAccounts = MonetaryAccount.list(
                apiContext,
                getUserID(apiContext)
        ).getValue();

        LOG.info("Received accounts from Bunq: {}", monetaryAccounts);

        return monetaryAccounts;
    }

    public List<Payment> fetchPayments(Integer accountID) {

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

        LOG.info("Received transactions from Bunq: {}", payments);

        return payments;
    }

    private Integer getUserID(final ApiContext apiContext) {
        Integer userID = null;
        //First fetch users in current context
        List<User> users = User.list(apiContext).getValue();
        if (users.size() > 0) {
            //Only interested in the first user
            User user = users.get(0);
            //Only interested in persons, not companies
            UserPerson userPerson = user.getUserPerson();
            if (userPerson != null) {
                userID = userPerson.getId();
            }
        }

        return userID;
    }
}
