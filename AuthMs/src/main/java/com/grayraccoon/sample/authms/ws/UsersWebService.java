package com.grayraccoon.sample.authms.ws;

import com.grayraccoon.sample.authdomain.domain.PasswordUpdaterModel;
import com.grayraccoon.sample.authdomain.domain.Users;
import com.grayraccoon.sample.authms.services.UserService;
import com.grayraccoon.webutils.dto.GenericDto;
import com.grayraccoon.webutils.errors.ApiError;
import com.grayraccoon.webutils.exceptions.CustomApiException;
import com.grayraccoon.webutils.ws.BaseWebService;
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
public class UsersWebService extends BaseWebService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UsersWebService.class);

    @Autowired
    private UserService userService;

    @Autowired
    private TokenStore tokenStore;


    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/secured/users")
    @HystrixCommand(groupKey = "Users", commandKey = "findAllUsers")
    public GenericDto<?> findAllUsers() {
        LOGGER.info("findAllUsers()");
        final List<Users> users = userService.findAllUsers();
        LOGGER.info("{} users found.", users.size());
        return GenericDto.<List<Users>>builder().data(users).build();
    }

    @GetMapping("/authenticated/user")
    public Users findMeFlat(OAuth2Authentication authentication) {
        Map<String,Object> extraInfo = getExtraInfo(authentication);
        String userId = (String) extraInfo.get("userId");
        LOGGER.info("findMeFlat() {}", userId);
        final Users user = userService.findUserById(userId);
        LOGGER.info("Flat User {} Found: {}", userId, user);
        return user;
    }


    @GetMapping("/authenticated/users/me")
    @HystrixCommand(fallbackMethod = "findMeFallback", groupKey = "Users", commandKey = "findMe")
    public GenericDto<?> findMe(OAuth2Authentication authentication) {
        Map<String,Object> extraInfo = getExtraInfo(authentication);
        String userId = (String) extraInfo.get("userId");
        LOGGER.info("findMe() {}", userId);
        final Users user = userService.findUserById(userId);
        LOGGER.info("User {} Found: {}", userId, user);
        return GenericDto.<Users>builder().data(user).build();
    }

    public GenericDto<?> findMeFallback(OAuth2Authentication authentication, Throwable ex) {
        LOGGER.error("findMeFallback", ex);
        throw new CustomApiException(ApiError.builder().throwable(ex).build());
    }


    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/secured/users/{userId}")
    @HystrixCommand(fallbackMethod = "findUserFallback", groupKey = "Users", commandKey = "findUser")
    public GenericDto<?> findUser(@PathVariable String userId) {
        LOGGER.info("findUser() {}", userId);
        final Users user = userService.findUserById(userId);
        LOGGER.info("User {} Found: {}", userId, user);
        return GenericDto.<Users>builder().data(user).build();
    }

    public GenericDto<?> findUserFallback(String userId, Throwable ex) {
        LOGGER.error("findUserFallback", ex);
        throw new CustomApiException(ApiError.builder().throwable(ex).build());
    }


    @PostMapping("/users")
    @HystrixCommand(fallbackMethod = "createUserFallback", groupKey = "Users", commandKey = "createUser")
    public GenericDto<?> createUser(@RequestBody Users users) {
        LOGGER.info("createUser() {}", users);
        final Users user = userService.createUser(users);
        LOGGER.info("Created User: {}", user);
        return GenericDto.<Users>builder().data(user).build();
    }

    @HystrixCommand(ignoreExceptions = CustomApiException.class)
    public GenericDto<?> createUserFallback(Users users, Throwable ex) {
        LOGGER.error("createUserFallback", ex);
        throw new CustomApiException(ApiError.builder().throwable(ex).build());
    }


    @PutMapping("/authenticated/users")
    @HystrixCommand(fallbackMethod = "updateUserFallback", groupKey = "Users", commandKey = "updateUser")
    public GenericDto<?> updateUser(@RequestBody Users users, OAuth2Authentication authentication) {
        Map<String,Object> extraInfo = getExtraInfo(authentication);
        String sessionUserId = (String) extraInfo.get("userId");

        LOGGER.info("updateUser() {}, {}", sessionUserId, users);
        final Users user = userService.updateUser(users, sessionUserId);
        LOGGER.info("Updated User: {}", user);
        return GenericDto.<Users>builder().data(user).build();
    }

    @HystrixCommand(ignoreExceptions = CustomApiException.class)
    public GenericDto<?> updateUserFallback(Users users, OAuth2Authentication authentication, Throwable ex) {
        LOGGER.error("updateUserFallback", ex);
        throw new CustomApiException(ApiError.builder().throwable(ex).build());
    }


    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/secured/users")
    @HystrixCommand(fallbackMethod = "updateUserAsAdminFallback", groupKey = "Users", commandKey = "updateUserAsAdmin")
    public GenericDto<?> updateUserAsAdmin(@RequestBody Users users) {
        LOGGER.info("updateUserAsAdmin() {}", users);
        final Users user = userService.updateUser(users);
        LOGGER.info("Updated User: {}", user);
        return GenericDto.<Users>builder().data(user).build();
    }

    @HystrixCommand(ignoreExceptions = CustomApiException.class)
    public GenericDto<?> updateUserAsAdminFallback(Users users, Throwable ex) {
        LOGGER.error("updateUserFallback", ex);
        throw new CustomApiException(ApiError.builder().throwable(ex).build());
    }


    @DeleteMapping("/authenticated/users")
    @HystrixCommand(fallbackMethod = "deleteUserFallback", groupKey = "Users", commandKey = "deleteUser")
    public GenericDto<?> deleteUser(OAuth2Authentication authentication) {
        Map<String,Object> extraInfo = getExtraInfo(authentication);
        String sessionUserId = (String) extraInfo.get("userId");

        LOGGER.info("deleteUser() {}", sessionUserId);
        userService.deleteUser(sessionUserId);
        LOGGER.info("User Deleted.");
        return GenericDto.<String>builder().data("User Deleted.").build();
    }

    @HystrixCommand(ignoreExceptions = CustomApiException.class)
    public GenericDto<?> deleteUserFallback(OAuth2Authentication authentication, Throwable ex) {
        LOGGER.error("deleteUserFallback", ex);
        throw new CustomApiException(ApiError.builder().throwable(ex).build());
    }


    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/secured/users/{userId}")
    @HystrixCommand(fallbackMethod = "deleteUserAsAdminFallback", groupKey = "Users", commandKey = "deleteUserAsAdmin")
    public GenericDto<?> deleteUserAsAdmin(@PathVariable String userId) {
        LOGGER.info("deleteUserAsAdmin() {}", userId);
        userService.deleteUser(userId);
        LOGGER.info("User Deleted.");
        return GenericDto.<String>builder().data("User Deleted.").build();
    }

    @HystrixCommand(ignoreExceptions = CustomApiException.class)
    public GenericDto<?> deleteUserAsAdminFallback(String userId, Throwable ex) {
        LOGGER.error("deleteUserAsAdminFallback", ex);
        throw new CustomApiException(ApiError.builder().throwable(ex).build());
    }


    @PutMapping("/authenticated/users/password")
    @HystrixCommand(fallbackMethod = "updateUserPasswordFallback",
            groupKey = "Users", commandKey = "updateUserPassword")
    public GenericDto<?> updateUserPassword(
            @RequestBody PasswordUpdaterModel passwordUpdaterModel,
            OAuth2Authentication authentication) {
        Map<String,Object> extraInfo = getExtraInfo(authentication);
        String sessionUserId = (String) extraInfo.get("userId");

        LOGGER.info("updateUserPassword() {}", sessionUserId);
        final Users user = userService.updateUserPassword(sessionUserId, passwordUpdaterModel);
        LOGGER.info("User Password Updated. {}", user);
        return GenericDto.<Users>builder().data(user).build();
    }

    @HystrixCommand(ignoreExceptions = CustomApiException.class)
    public GenericDto<?> updateUserPasswordFallback(
            PasswordUpdaterModel passwordUpdaterModel,
            OAuth2Authentication authentication,
            Throwable ex) {
        LOGGER.error("updateUserPasswordFallback", ex);
        throw new CustomApiException(ApiError.builder().throwable(ex).build());
    }


    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/secured/users/password/{userId}")
    @HystrixCommand(fallbackMethod = "updateUserPasswordAsAdminFallback",
            groupKey = "Users", commandKey = "updateUserPasswordAsAdmin")
    public GenericDto<?> updateUserPasswordAsAdmin(
            @RequestBody PasswordUpdaterModel passwordUpdaterModel,
            @PathVariable String userId) {
        LOGGER.info("updateUserPasswordAsAdmin() {}", userId);
        final Users user = userService.updateUserPasswordAsAdmin(userId, passwordUpdaterModel);
        LOGGER.info("User Password Updated. {}", user);
        return GenericDto.<Users>builder().data(user).build();
    }

    @HystrixCommand(ignoreExceptions = CustomApiException.class)
    public GenericDto<?> updateUserPasswordAsAdminFallback(
            PasswordUpdaterModel passwordUpdaterModel,
            String userId, Throwable ex) {
        LOGGER.error("updateUserPasswordAsAdminFallback", ex);
        throw new CustomApiException(ApiError.builder().throwable(ex).build());
    }


    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/secured/users/roles/admin/{userId}")
    @HystrixCommand(fallbackMethod = "toggleAdminRoleToAsAdminFallback",
            groupKey = "Roles", commandKey = "toggleAdminRoleToAsAdmin")
    public GenericDto<?> toggleAdminRoleToAsAdmin(@PathVariable String userId) {
        LOGGER.info("toggleAdminRoleToAsAdmin() {}", userId);
        Users user = userService.toggleAdminRoleTo(userId);
        LOGGER.info("User Role toggled. {}", user);
        return GenericDto.<Users>builder().data(user).build();
    }

    @HystrixCommand(ignoreExceptions = CustomApiException.class)
    public GenericDto<?> toggleAdminRoleToAsAdminFallback(String userId, Throwable ex) {
        LOGGER.error("toggleAdminRoleToAsAdminFallback", ex);
        throw new CustomApiException(ApiError.builder().throwable(ex).build());
    }


    @PostMapping("/authenticated/users/revokeAll")
    @HystrixCommand(fallbackMethod = "revokeAllMyTokensFallback",
            groupKey = "Tokens", commandKey = "revokeAllMyTokens")
    public GenericDto<?> revokeAllMyTokens(OAuth2Authentication authentication) {
        Map<String,Object> extraInfo = getExtraInfo(authentication);
        String sessionUserId = (String) extraInfo.get("userId");

        LOGGER.info("revokeAllMyTokens() {}", sessionUserId);
        userService.revokeAllAccessTokens(sessionUserId);
        LOGGER.info("All tokens revoked");
        return GenericDto.<String>builder().data("All tokens revoked").build();
    }

    @HystrixCommand(ignoreExceptions = CustomApiException.class)
    public GenericDto<?> revokeAllMyTokensFallback(OAuth2Authentication authentication, Throwable ex) {
        LOGGER.error("revokeAllMyTokensFallback", ex);
        throw new CustomApiException(ApiError.builder().throwable(ex).build());
    }



    private Map<String, Object> getExtraInfo(OAuth2Authentication auth) {
        OAuth2AuthenticationDetails details
                = (OAuth2AuthenticationDetails) auth.getDetails();
        if (details.getDecodedDetails() != null) {
            //noinspection unchecked
            return (Map<String, Object>) details.getDecodedDetails();
        }
        OAuth2AccessToken accessToken = tokenStore
                .readAccessToken(details.getTokenValue());
        return accessToken.getAdditionalInformation();
    }

}
