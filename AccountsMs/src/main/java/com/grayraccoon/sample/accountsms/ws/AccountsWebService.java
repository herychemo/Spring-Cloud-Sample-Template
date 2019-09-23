package com.grayraccoon.sample.accountsms.ws;

import com.grayraccoon.sample.accountsdomain.domain.Accounts;
import com.grayraccoon.sample.accountsms.services.AccountService;
import com.grayraccoon.webutils.dto.GenericDto;
import com.grayraccoon.webutils.errors.ApiError;
import com.grayraccoon.webutils.exceptions.CustomApiException;
import com.grayraccoon.webutils.ws.BaseWebService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.grayraccoon.webutils.config.components.CustomAccessTokenConverter.getExtraInfo;

@RestController
public class AccountsWebService extends BaseWebService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountsWebService.class);

    @Autowired
    private AccountService accountService;


    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/secured/accounts")
    @HystrixCommand(groupKey = "Accounts", commandKey = "findAllAccounts")
    public GenericDto<?> findAllAccounts() {
        LOGGER.info("findAllAccounts()");
        final List<Accounts> accounts = accountService.findAllAccounts();
        LOGGER.info("{} accounts found.", accounts.size());
        return GenericDto.<List<Accounts>>builder().data(accounts).build();
    }

    @GetMapping("/authenticated/accounts/me")
    @HystrixCommand(fallbackMethod = "findMeFallback", groupKey = "Accounts", commandKey = "findMe")
    public GenericDto<?> findMe(Authentication authentication) {
        Map<String,Object> extraInfo = getExtraInfo(authentication);
        String userId = (String) extraInfo.get("userId");
        LOGGER.info("findMe() {}", userId);
        final Accounts account = accountService.findAccountById(userId);
        LOGGER.info("Account {} Found: {}", userId, account);
        return GenericDto.<Accounts>builder().data(account).build();
    }

    public GenericDto<?> findMeFallback(Authentication authentication, Throwable ex) {
        LOGGER.error("findMeFallback", ex);
        throw new CustomApiException(ApiError.builder().throwable(ex).build());
    }


    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/secured/accounts/{accountId}")
    @HystrixCommand(fallbackMethod = "findAccountFallback", groupKey = "Accounts", commandKey = "findAccount")
    public GenericDto<?> findAccount(@PathVariable String accountId) {
        LOGGER.info("findAccount() {}", accountId);
        final Accounts account = accountService.findAccountById(accountId);
        LOGGER.info("Account {} Found: {}", accountId, account);
        return GenericDto.<Accounts>builder().data(account).build();
    }

    public GenericDto<?> findAccountFallback(String accountId, Throwable ex) {
        LOGGER.error("findAccountFallback", ex);
        throw new CustomApiException(ApiError.builder().throwable(ex).build());
    }


    @PutMapping("/authenticated/accounts")
    @HystrixCommand(fallbackMethod = "updateAccountFallback", groupKey = "Accounts", commandKey = "updateAccount")
    public GenericDto<?> updateAccount(@RequestBody Accounts accounts, Authentication authentication) {
        final Map<String,Object> extraInfo = getExtraInfo(authentication);
        final String userId = (String) extraInfo.get("userId");

        LOGGER.info("updateAccount() {}, {}", userId, accounts);
        final Accounts account = accountService.updateAccount(accounts, userId);
        LOGGER.info("Updated Account: {}", account);
        return GenericDto.<Accounts>builder().data(account).build();
    }

    @HystrixCommand(ignoreExceptions = CustomApiException.class)
    public GenericDto<?> updateAccountFallback(Accounts accounts, Authentication authentication, Throwable ex) {
        LOGGER.error("updateAccountFallback", ex);
        throw new CustomApiException(ApiError.builder().throwable(ex).build());
    }


    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/secured/accounts")
    @HystrixCommand(fallbackMethod = "updateAccountAsAdminFallback",
            groupKey = "Accounts", commandKey = "updateAccountAsAdmin")
    public GenericDto<?> updateAccountAsAdmin(@RequestBody Accounts accounts) {
        LOGGER.info("updateAccountAsAdmin() {}", accounts);
        final Accounts account = accountService.updateAccount(accounts);
        LOGGER.info("Updated Account: {}", account);
        return GenericDto.<Accounts>builder().data(account).build();
    }

    @HystrixCommand(ignoreExceptions = CustomApiException.class)
    public GenericDto<?> updateAccountAsAdminFallback(Accounts accounts, Throwable ex) {
        LOGGER.error("updateAccountFallback", ex);
        throw new CustomApiException(ApiError.builder().throwable(ex).build());
    }
    

}
