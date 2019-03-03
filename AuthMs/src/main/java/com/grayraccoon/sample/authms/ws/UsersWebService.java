package com.grayraccoon.sample.authms.ws;

import com.grayraccoon.sample.authms.domain.Users;
import com.grayraccoon.sample.authms.services.UserService;
import com.grayraccoon.webutils.dto.GenericDto;
import com.grayraccoon.webutils.errors.ApiError;
import com.grayraccoon.webutils.exceptions.CustomApiException;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ws")
public class UsersWebService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UsersWebService.class);

    @Autowired
    private UserService userService;

    @Autowired
    private TokenStore tokenStore;

    @HystrixCommand(fallbackMethod = "findAllUsersFallback",
            commandKey = "FindAllUsers",
            groupKey = "UsersEntity",
            ignoreExceptions = CustomApiException.class)
    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/secured/users")
    public GenericDto<List<Users>> findAllUsers() {
        LOGGER.info("findAllUsers()");
        final List<Users> users = userService.findAllUsers();
        LOGGER.info("{} users found.", users.size());
        return GenericDto.<List<Users>>builder().data(users).build();
    }

    public GenericDto<List<Users>> findAllUsersFallback(Throwable ex) {
        LOGGER.error("findAllUsersFallback", ex);
        throw new CustomApiException(ApiError.builder().throwable(ex).build());
    }


    @HystrixCommand(fallbackMethod = "findMeFallback",
            commandKey = "findMe",
            groupKey = "UsersEntity",
            ignoreExceptions = CustomApiException.class)
    @GetMapping("/authenticated/users/me")
    public GenericDto<Users> findMe(OAuth2Authentication authentication) {
        Map<String,Object> extraInfo = getExtraInfo(authentication);
        String userId = (String) extraInfo.get("userId");
        LOGGER.info("findMe() {}", userId);
        final Users user = userService.findUserById(userId);
        LOGGER.info("User {} Found: {}", userId, user);
        return GenericDto.<Users>builder().data(user).build();
    }

    public GenericDto<Users> findMeFallback(OAuth2Authentication authentication, Throwable ex) {
        LOGGER.error("findMeFallback", ex);
        throw new CustomApiException(ApiError.builder().throwable(ex).build());
    }


    @HystrixCommand(fallbackMethod = "findUserFallback",
            commandKey = "findUser",
            groupKey = "UsersEntity",
            ignoreExceptions = CustomApiException.class)
    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/secured/users/{userId}")
    public GenericDto<Users> findUser(@PathVariable String userId) {
        LOGGER.info("findUser() {}", userId);
        final Users user = userService.findUserById(userId);
        LOGGER.info("User {} Found: {}", userId, user);
        return GenericDto.<Users>builder().data(user).build();
    }

    public GenericDto<Users> findUserFallback(String userId, Throwable ex) {
        LOGGER.error("findUserFallback", ex);
        throw new CustomApiException(ApiError.builder().throwable(ex).build());
    }

    @HystrixCommand(fallbackMethod = "saveUserFallback",
            commandKey = "saveUser",
            groupKey = "UsersEntity",
            ignoreExceptions = CustomApiException.class)
    @PostMapping("/users")
    public GenericDto<Users> saveUser(@RequestBody Users users) {
        LOGGER.info("saveUser() {}", users);
        final Users user = userService.createUser(users);
        LOGGER.info("Saved User: {}", user);
        return GenericDto.<Users>builder().data(user).build();
    }

    @HystrixCommand(ignoreExceptions = CustomApiException.class)
    public GenericDto<Users> saveUserFallback(Users users, Throwable ex) {
        LOGGER.error("saveUserFallback", ex);
        throw new CustomApiException(ApiError.builder().throwable(ex).build());
    }




    private Map<String, Object> getExtraInfo(OAuth2Authentication auth) {
        OAuth2AuthenticationDetails details
                = (OAuth2AuthenticationDetails) auth.getDetails();
        OAuth2AccessToken accessToken = tokenStore
                .readAccessToken(details.getTokenValue());
        return accessToken.getAdditionalInformation();
    }

}
