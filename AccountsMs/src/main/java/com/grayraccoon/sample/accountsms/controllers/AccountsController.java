package com.grayraccoon.sample.accountsms.controllers;


import com.grayraccoon.sample.accountsms.domain.dto.Accounts;
import com.grayraccoon.sample.accountsms.services.AccountService;
import com.grayraccoon.webutils.errors.ApiError;
import com.grayraccoon.webutils.exceptions.CustomApiException;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountsController.class.getName());

    @Autowired
    private AccountService accountService;

    @HystrixCommand(fallbackMethod = "getAccountsFallback",
            commandKey = "GetAccounts",
            groupKey = "Accounts",
            ignoreExceptions = CustomApiException.class)
    @GetMapping(consumes = "application/json", produces = "application/json")
    public List<Accounts> getAccounts() {
        LOGGER.info("getAccounts()");
        return accountService.getAllAccounts();
    }

    @HystrixCommand(fallbackMethod = "getAccountFallback",
            commandKey = "GetAccount",
            groupKey = "Accounts",
            ignoreExceptions = CustomApiException.class)
    @GetMapping(value = "/{account_id}", consumes = "application/json", produces = "application/json")
    public Accounts getAccount(@PathVariable String account_id) {
        LOGGER.info("getAccount() {}", account_id);
        Accounts account = accountService.getAccountById(account_id);
        LOGGER.info("Found Account {}", account);
        return account;
    }

    public List<Accounts> getAccountsFallback(Throwable ex) {
        LOGGER.error("getAccountsFallback", ex);
        throw new CustomApiException(ApiError.builder().ex(ex).build());
    }

    public Accounts getAccountFallback(String id, Throwable ex) {
        LOGGER.error("getAccountFallback", ex);
        throw new CustomApiException(ApiError.builder().ex(ex).build());
    }

}
