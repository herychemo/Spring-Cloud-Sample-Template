package com.grayraccoon.sample.accountsms.services;

import com.grayraccoon.sample.accountsms.domain.Accounts;
import com.grayraccoon.webutils.errors.ApiError;
import com.grayraccoon.webutils.errors.ApiValidationError;
import com.grayraccoon.webutils.exceptions.CustomApiException;
import org.apache.commons.lang.math.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountService.class.getName());

    private List<Accounts> allAccounts;

    public AccountService() {
        this.allAccounts = new ArrayList<>();
        this.allAccounts.add(new Accounts("1", "Simple Account"));
        this.allAccounts.add(new Accounts("2", "Second Account"));
    }

    public List<Accounts> getAllAccounts() {
        return new ArrayList<>(this.allAccounts);
    }

    public Accounts getAccountById(String account_id) {

        if (RandomUtils.nextBoolean() && RandomUtils.nextBoolean() && RandomUtils.nextBoolean()) {
            throw new RuntimeException();
        }

        Optional<Accounts> foundAccount = this.allAccounts.stream().filter(accounts -> accounts.getId().equals(account_id)).findFirst();
        if (foundAccount.isPresent()) {
            return foundAccount.get();
        }

        LOGGER.info("Account not found: {}", account_id);
        throw new CustomApiException(
                ApiError.builder()
                        .status(HttpStatus.NOT_FOUND)
                        .subError(new ApiValidationError(account_id))
                        .build()
        );

    }

}
