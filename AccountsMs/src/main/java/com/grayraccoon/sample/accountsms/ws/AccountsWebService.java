package com.grayraccoon.sample.accountsms.ws;

import com.grayraccoon.sample.accountsdomain.domain.Accounts;
import com.grayraccoon.sample.accountsms.services.AccountService;
import com.grayraccoon.webutils.dto.GenericDto;
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
public class AccountsWebService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountsWebService.class);

    @Autowired
    private AccountService accountService;

    @HystrixCommand(fallbackMethod = "getAccountsFallback",
            commandKey = "GetAccounts",
            groupKey = "Accounts",
            ignoreExceptions = CustomApiException.class)
    @GetMapping(consumes = "application/json", produces = "application/json")
    public GenericDto<List<Accounts>> getAccounts() {
        LOGGER.info("getAccounts()");
        List<Accounts> accounts = accountService.getAllAccounts();
        LOGGER.info("{} accounts found.", accounts.size());
        return GenericDto.<List<Accounts>>builder().data(accounts).build();
    }

    public GenericDto<List<Accounts>> getAccountsFallback(Throwable ex) {
        LOGGER.error("getAccountsFallback", ex);
        throw new CustomApiException(ApiError.builder().throwable(ex).build());
    }


    @HystrixCommand(fallbackMethod = "getAccountFallback",
            commandKey = "GetAccount",
            groupKey = "Accounts",
            ignoreExceptions = CustomApiException.class)
    @GetMapping(value = "/{account_id}", consumes = "application/json", produces = "application/json")
    public GenericDto<Accounts> getAccount(@PathVariable String account_id) {
        LOGGER.info("getAccount() {}", account_id);
        Accounts account = accountService.getAccountById(account_id);
        LOGGER.info("Account {} Found: {}", account_id, account);
        return GenericDto.<Accounts>builder().data(account).build();
    }

    public GenericDto<Accounts> getAccountFallback(String id, Throwable ex) {
        LOGGER.error("getAccountFallback", ex);
        throw new CustomApiException(ApiError.builder().throwable(ex).build());
    }

}
