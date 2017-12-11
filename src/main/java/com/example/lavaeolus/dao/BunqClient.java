package com.example.lavaeolus.dao;

import com.bunq.sdk.context.ApiContext;
import com.bunq.sdk.model.generated.endpoint.MonetaryAccount;
import com.bunq.sdk.model.generated.endpoint.User;
import com.bunq.sdk.model.generated.endpoint.UserPerson;
import com.bunq.sdk.model.generated.object.Pointer;
import com.example.lavaeolus.dao.domain.BunqResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BunqClient {
    private static final Logger LOG = LoggerFactory.getLogger(BunqClient.class);

    @Autowired
    private ApiContext apiContext;

    public BunqResponse fetchAccount() {
        BunqResponse bunqResponse = new BunqResponse();

        //First fetch users in current context
        List<User> users = User.list(apiContext).getValue();
        if(users.size() > 0) {
            //Only interested in the first user
            User user = users.get(0);
            //Only interested in persons, not companies
            UserPerson userPerson = user.getUserPerson();
            if(userPerson != null) {
                List<MonetaryAccount> monetaryAccounts = MonetaryAccount.list(
                        apiContext,
                        userPerson.getId()
                ).getValue();
                LOG.info("Received accounts from Bunq: {}", monetaryAccounts);
                //Only interested in first account
                if(monetaryAccounts.size() > 0) {
                    MonetaryAccount monetaryAccount = monetaryAccounts.get(0);
                    bunqResponse.setBalance(monetaryAccount.getMonetaryAccountBank().getBalance().getValue());
                    bunqResponse.setCurrency(monetaryAccount.getMonetaryAccountBank().getBalance().getCurrency());
                    List<Pointer> pointers = monetaryAccount.getMonetaryAccountBank().getAlias();
                    for (Pointer alias : pointers) {
                        if("IBAN".equals(alias.getType())) {
                            bunqResponse.setIBAN(alias.getValue());
                            bunqResponse.setName(alias.getName());
                        }
                    }
                }
            }
        }

        return bunqResponse;

    }
}
