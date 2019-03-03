package com.grayraccoon.sample.authms.ws;

import com.grayraccoon.sample.authms.domain.dto.UsersDto;
import com.grayraccoon.sample.authms.services.UserService;
import com.grayraccoon.sample.authms.services.UserServiceImpl;
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
    public GenericDto<List<UsersDto>> findAllUsers() {
        LOGGER.info("findAllUsers()");
        final List<UsersDto> users = userService.findAllUsers();
        LOGGER.info("{} users found.", users.size());
        return GenericDto.<List<UsersDto>>builder().data(users).build();
    }

    public GenericDto<List<UsersDto>> findAllUsersFallback(Throwable ex) {
        LOGGER.error("findAllUsersFallback", ex);
        throw new CustomApiException(ApiError.builder().throwable(ex).build());
    }


    @HystrixCommand(fallbackMethod = "findMeFallback",
            commandKey = "findMe",
            groupKey = "Users",
            ignoreExceptions = CustomApiException.class)
    @GetMapping("/authenticated/users/me")
    public GenericDto<UsersDto> findMe(OAuth2Authentication authentication) {
        Map<String,Object> extraInfo = getExtraInfo(authentication);
        String userId = (String) extraInfo.get("userId");
        LOGGER.info("findMe() {}", userId);
        final UsersDto user = userService.findUserById(userId);
        LOGGER.info("User {} Found: {}", userId, user);
        return GenericDto.<UsersDto>builder().data(user).build();
    }

    public GenericDto<UsersDto> findMeFallback(OAuth2Authentication authentication, Throwable ex) {
        LOGGER.error("findMeFallback", ex);
        throw new CustomApiException(ApiError.builder().throwable(ex).build());
    }


    @HystrixCommand(fallbackMethod = "findUserFallback",
            commandKey = "findUser",
            groupKey = "Users",
            ignoreExceptions = CustomApiException.class)
    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/secured/users/{userId}")
    public GenericDto<UsersDto> findUser(@PathVariable String userId) {
        LOGGER.info("findUser() {}", userId);
        final UsersDto user = userService.findUserById(userId);
        LOGGER.info("User {} Found: {}", userId, user);
        return GenericDto.<UsersDto>builder().data(user).build();
    }

    public GenericDto<UsersDto> findUserFallback(String userId, Throwable ex) {
        LOGGER.error("findUserFallback", ex);
        throw new CustomApiException(ApiError.builder().throwable(ex).build());
    }

    @HystrixCommand(fallbackMethod = "saveUserFallback",
            commandKey = "saveUser",
            groupKey = "Users",
            ignoreExceptions = CustomApiException.class)
    @PostMapping("/users")
    public GenericDto<UsersDto> saveUser(@RequestBody UsersDto usersDto) {
        LOGGER.info("saveUser() {}", usersDto);
        final UsersDto user = userService.createUser(usersDto);
        LOGGER.info("Saved User: {}", user);
        return GenericDto.<UsersDto>builder().data(user).build();
    }

    @HystrixCommand(ignoreExceptions = CustomApiException.class)
    public GenericDto<UsersDto> saveUserFallback(UsersDto usersDto, Throwable ex) {
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
