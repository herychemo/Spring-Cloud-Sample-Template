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
            groupKey = "Users",
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
            groupKey = "Users",
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
            groupKey = "Users",
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

    @HystrixCommand(fallbackMethod = "createUserFallback",
            commandKey = "createUser",
            groupKey = "Users",
            ignoreExceptions = CustomApiException.class)
    @PostMapping("/users")
    public GenericDto<Users> createUser(@RequestBody Users users) {
        LOGGER.info("createUser() {}", users);
        final Users user = userService.createUser(users);
        LOGGER.info("Created User: {}", user);
        return GenericDto.<Users>builder().data(user).build();
    }

    @HystrixCommand(ignoreExceptions = CustomApiException.class)
    public GenericDto<Users> createUserFallback(Users users, Throwable ex) {
        LOGGER.error("createUserFallback", ex);
        throw new CustomApiException(ApiError.builder().throwable(ex).build());
    }


    @HystrixCommand(fallbackMethod = "updateUserFallback",
            commandKey = "updateUser",
            groupKey = "Users",
            ignoreExceptions = CustomApiException.class)
    @PutMapping("/authenticated/users")
    public GenericDto<Users> updateUser(@RequestBody Users users, OAuth2Authentication authentication) {
        Map<String,Object> extraInfo = getExtraInfo(authentication);
        String sessionUserId = (String) extraInfo.get("userId");

        LOGGER.info("updateUser() {}, {}", sessionUserId, users);
        final Users user = userService.updateUser(users, sessionUserId);
        LOGGER.info("Updated User: {}", user);
        return GenericDto.<Users>builder().data(user).build();
    }

    @HystrixCommand(ignoreExceptions = CustomApiException.class)
    public GenericDto<Users> updateUserFallback(Users users, OAuth2Authentication authentication, Throwable ex) {
        LOGGER.error("updateUserFallback", ex);
        throw new CustomApiException(ApiError.builder().throwable(ex).build());
    }


    @HystrixCommand(fallbackMethod = "updateUserAsAdminFallback",
            commandKey = "updateUserAsAdmin",
            groupKey = "Users",
            ignoreExceptions = CustomApiException.class)
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/secured/users")
    public GenericDto<Users> updateUserAsAdmin(@RequestBody Users users) {
        LOGGER.info("updateUserAsAdmin() {}", users);
        final Users user = userService.updateUser(users);
        LOGGER.info("Updated User: {}", user);
        return GenericDto.<Users>builder().data(user).build();
    }

    @HystrixCommand(ignoreExceptions = CustomApiException.class)
    public GenericDto<Users> updateUserAsAdminFallback(Users users, Throwable ex) {
        LOGGER.error("updateUserFallback", ex);
        throw new CustomApiException(ApiError.builder().throwable(ex).build());
    }


    @HystrixCommand(fallbackMethod = "deleteUserFallback",
            commandKey = "deleteUser",
            groupKey = "Users",
            ignoreExceptions = CustomApiException.class)
    @DeleteMapping("/authenticated/users")
    public GenericDto<String> deleteUser(OAuth2Authentication authentication) {
        Map<String,Object> extraInfo = getExtraInfo(authentication);
        String sessionUserId = (String) extraInfo.get("userId");

        LOGGER.info("deleteUser() {}", sessionUserId);
        userService.deleteUser(sessionUserId);
        LOGGER.info("User Deleted.");
        return GenericDto.<String>builder().data("User Deleted.").build();
    }

    @HystrixCommand(ignoreExceptions = CustomApiException.class)
    public GenericDto<String> deleteUserFallback(OAuth2Authentication authentication, Throwable ex) {
        LOGGER.error("deleteUserFallback", ex);
        throw new CustomApiException(ApiError.builder().throwable(ex).build());
    }


    @HystrixCommand(fallbackMethod = "deleteUserAsAdminFallback",
            commandKey = "deleteUserAsAdmin",
            groupKey = "Users",
            ignoreExceptions = CustomApiException.class)
    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/secured/users/{userId}")
    public GenericDto<String> deleteUserAsAdmin(@PathVariable String userId) {
        LOGGER.info("deleteUserAsAdmin() {}", userId);
        userService.deleteUser(userId);
        LOGGER.info("User Deleted.");
        return GenericDto.<String>builder().data("User Deleted.").build();
    }

    @HystrixCommand(ignoreExceptions = CustomApiException.class)
    public GenericDto<String> deleteUserAsAdminFallback(String userId, Throwable ex) {
        LOGGER.error("deleteUserAsAdminFallback", ex);
        throw new CustomApiException(ApiError.builder().throwable(ex).build());
    }


    @HystrixCommand(fallbackMethod = "toggleAdminRoleToAsAdminFallback",
            commandKey = "toggleAdminRoleToAsAdmin",
            groupKey = "Roles",
            ignoreExceptions = CustomApiException.class)
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/secured/users/roles/admin/{userId}")
    public GenericDto<Users> toggleAdminRoleToAsAdmin(@PathVariable String userId) {
        LOGGER.info("toggleAdminRoleToAsAdmin() {}", userId);
        Users user = userService.toggleAdminRoleTo(userId);
        LOGGER.info("User Role toggled. {}", user);
        return GenericDto.<Users>builder().data(user).build();
    }

    @HystrixCommand(ignoreExceptions = CustomApiException.class)
    public GenericDto<Users> toggleAdminRoleToAsAdminFallback(String userId, Throwable ex) {
        LOGGER.error("toggleAdminRoleToAsAdminFallback", ex);
        throw new CustomApiException(ApiError.builder().throwable(ex).build());
    }


    @HystrixCommand(fallbackMethod = "revokeAllMyTokensFallback",
            commandKey = "revokeAllMyTokens",
            groupKey = "Tokens",
            ignoreExceptions = CustomApiException.class)
    @PostMapping("/authenticated/users/revokeAll")
    public GenericDto<String> revokeAllMyTokens(OAuth2Authentication authentication) {
        Map<String,Object> extraInfo = getExtraInfo(authentication);
        String sessionUserId = (String) extraInfo.get("userId");

        LOGGER.info("revokeAllMyTokens() {}", sessionUserId);
        userService.revokeAllAccessTokens(sessionUserId);
        LOGGER.info("All tokens revoked");
        return GenericDto.<String>builder().data("All tokens revoked").build();
    }

    @HystrixCommand(ignoreExceptions = CustomApiException.class)
    public GenericDto<String> revokeAllMyTokensFallback(OAuth2Authentication authentication, Throwable ex) {
        LOGGER.error("revokeAllMyTokensFallback", ex);
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
