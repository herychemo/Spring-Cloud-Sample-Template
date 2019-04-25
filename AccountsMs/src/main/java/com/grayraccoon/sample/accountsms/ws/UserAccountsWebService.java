package com.grayraccoon.sample.accountsms.ws;

import com.grayraccoon.sample.accountsdomain.domain.UserAccounts;
import com.grayraccoon.sample.accountsms.services.UserAccountService;
import com.grayraccoon.webutils.dto.GenericDto;
import com.grayraccoon.webutils.errors.ApiError;
import com.grayraccoon.webutils.exceptions.CustomApiException;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.grayraccoon.sample.accountsms.config.ResourceServerConfig.getExtraInfo;

@RestController
@RequestMapping("/ws")
public class UserAccountsWebService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserAccountsWebService.class);

    @Autowired
    private UserAccountService userAccountService;

    @HystrixCommand(fallbackMethod = "findMeUserAccountFallback",
            commandKey = "findMeUserAccount",
            groupKey = "UserAccounts",
            ignoreExceptions = CustomApiException.class)
    @GetMapping("/authenticated/userAccounts/me")
    public GenericDto<UserAccounts> findMeUserAccount(Authentication authentication) {
        Map<String,Object> extraInfo = getExtraInfo(authentication);
        String userId = (String) extraInfo.get("userId");
        LOGGER.info("findMeUserAccount() {}", userId);
        final UserAccounts userAccount = userAccountService.findUserAccountById(userId,true);
        LOGGER.info("UserAccounts {} Found: {}", userId, userAccount);
        return GenericDto.<UserAccounts>builder().data(userAccount).build();
    }

    public GenericDto<UserAccounts> findMeUserAccountFallback(Authentication authentication, Throwable ex) {
        LOGGER.error("findMeUserAccountFallback", ex);
        throw new CustomApiException(ApiError.builder().throwable(ex).build());
    }

    @HystrixCommand(fallbackMethod = "findUserAccountFallback",
            commandKey = "findUserAccount",
            groupKey = "UserAccounts",
            ignoreExceptions = CustomApiException.class)
    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/secured/userAccounts/{userId}")
    public GenericDto<UserAccounts> findUserAccount(@PathVariable String userId) {
        LOGGER.info("findUser() {}", userId);
        final UserAccounts userAccount = userAccountService.findUserAccountById(userId);
        LOGGER.info("UserAccounts {} Found: {}", userId, userAccount);
        return GenericDto.<UserAccounts>builder().data(userAccount).build();
    }

    public GenericDto<UserAccounts> findUserAccountFallback(String userId, Throwable ex) {
        LOGGER.error("findUserFallback", ex);
        throw new CustomApiException(ApiError.builder().throwable(ex).build());
    }
    
}
