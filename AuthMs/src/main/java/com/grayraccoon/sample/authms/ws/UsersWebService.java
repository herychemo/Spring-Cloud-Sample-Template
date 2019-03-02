package com.grayraccoon.sample.authms.ws;

import com.grayraccoon.sample.authms.data.postgres.domain.Users;
import com.grayraccoon.sample.authms.services.UserService;
import com.grayraccoon.webutils.dto.GenericDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ws")
public class UsersWebService {

    @Autowired
    private UserService userService;

    @Autowired
    private TokenStore tokenStore;

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/secured/users")
    public GenericDto<List<Users>> findAllUsers() {
        final List<Users> users = userService.findAllUsers();
        return GenericDto.<List<Users>>builder().data(users).build();
    }

    @GetMapping("/authenticated/users/me")
    public GenericDto<Users> findMe(OAuth2Authentication authentication) {
        Map<String,Object> extraInfo = getExtraInfo(authentication);
        String userId = (String) extraInfo.get("userId");

        final Users user = userService.findUserById(userId);
        return GenericDto.<Users>builder().data(user).build();
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/secured/users/{userId}")
    public GenericDto<Users> findUser(@PathVariable String userId) {
        final Users user = userService.findUserById(userId);
        return GenericDto.<Users>builder().data(user).build();
    }



    public Map<String, Object> getExtraInfo(OAuth2Authentication auth) {
        OAuth2AuthenticationDetails details
                = (OAuth2AuthenticationDetails) auth.getDetails();
        OAuth2AccessToken accessToken = tokenStore
                .readAccessToken(details.getTokenValue());
        return accessToken.getAdditionalInformation();
    }

}
