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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.grayraccoon.sample.accountsms.config.ResourceServerConfig.getExtraInfo;

@RestController
@RequestMapping("/ws")
public class AccountsWebService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountsWebService.class);

    @Autowired
    private AccountService accountService;

    @HystrixCommand(fallbackMethod = "findAllAccountsFallback",
            commandKey = "FindAllAccounts",
            groupKey = "Accounts",
            ignoreExceptions = CustomApiException.class)
    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/secured/accounts")
    public GenericDto<List<Accounts>> findAllAccounts() {
        LOGGER.info("findAllAccounts()");
        final List<Accounts> accounts = accountService.findAllAccounts();
        LOGGER.info("{} accounts found.", accounts.size());
        return GenericDto.<List<Accounts>>builder().data(accounts).build();
    }

    public GenericDto<List<Accounts>> findAllAccountsFallback(Throwable ex) {
        LOGGER.error("findAllAccountsFallback", ex);
        throw new CustomApiException(ApiError.builder().throwable(ex).build());
    }

    @HystrixCommand(fallbackMethod = "findMeFallback",
            commandKey = "findMe",
            groupKey = "Accounts",
            ignoreExceptions = CustomApiException.class)
    @GetMapping("/authenticated/accounts/me")
    public GenericDto<Accounts> findMe(Authentication authentication) {
        Map<String,Object> extraInfo = getExtraInfo(authentication);
        String userId = (String) extraInfo.get("userId");
        LOGGER.info("findMe() {}", userId);
        final Accounts account = accountService.findAccountById(userId);
        LOGGER.info("Account {} Found: {}", userId, account);
        return GenericDto.<Accounts>builder().data(account).build();
    }

    public GenericDto<Accounts> findMeFallback(Authentication authentication, Throwable ex) {
        LOGGER.error("findMeFallback", ex);
        throw new CustomApiException(ApiError.builder().throwable(ex).build());
    }


    @HystrixCommand(fallbackMethod = "findAccountFallback",
            commandKey = "findAccount",
            groupKey = "Accounts",
            ignoreExceptions = CustomApiException.class)
    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/secured/accounts/{accountId}")
    public GenericDto<Accounts> findAccount(@PathVariable String accountId) {
        LOGGER.info("findAccount() {}", accountId);
        final Accounts account = accountService.findAccountById(accountId);
        LOGGER.info("Account {} Found: {}", accountId, account);
        return GenericDto.<Accounts>builder().data(account).build();
    }

    public GenericDto<Accounts> findAccountFallback(String accountId, Throwable ex) {
        LOGGER.error("findAccountFallback", ex);
        throw new CustomApiException(ApiError.builder().throwable(ex).build());
    }

    @HystrixCommand(fallbackMethod = "updateAccountFallback",
            commandKey = "updateAccount",
            groupKey = "Accounts",
            ignoreExceptions = CustomApiException.class)
    @PutMapping("/authenticated/accounts")
    public GenericDto<Accounts> updateAccount(@RequestBody Accounts accounts, Authentication authentication) {
        Map<String,Object> extraInfo = getExtraInfo(authentication);
        String userId = (String) extraInfo.get("userId");

        LOGGER.info("updateAccount() {}, {}", userId, accounts);
        final Accounts account = accountService.updateAccount(accounts, userId);
        LOGGER.info("Updated Account: {}", account);
        return GenericDto.<Accounts>builder().data(account).build();
    }

    @HystrixCommand(ignoreExceptions = CustomApiException.class)
    public GenericDto<Accounts> updateAccountFallback(Accounts accounts, Authentication authentication, Throwable ex) {
        LOGGER.error("updateAccountFallback", ex);
        throw new CustomApiException(ApiError.builder().throwable(ex).build());
    }


    @HystrixCommand(fallbackMethod = "updateAccountAsAdminFallback",
            commandKey = "updateAccountAsAdmin",
            groupKey = "Accounts",
            ignoreExceptions = CustomApiException.class)
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/secured/accounts")
    public GenericDto<Accounts> updateAccountAsAdmin(@RequestBody Accounts accounts) {
        LOGGER.info("updateAccountAsAdmin() {}", accounts);
        final Accounts account = accountService.updateAccount(accounts);
        LOGGER.info("Updated Account: {}", account);
        return GenericDto.<Accounts>builder().data(account).build();
    }

    @HystrixCommand(ignoreExceptions = CustomApiException.class)
    public GenericDto<Accounts> updateAccountAsAdminFallback(Accounts accounts, Throwable ex) {
        LOGGER.error("updateAccountFallback", ex);
        throw new CustomApiException(ApiError.builder().throwable(ex).build());
    }
    

}
