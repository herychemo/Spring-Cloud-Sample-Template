package com.grayraccoon.sample.authms.config.components;

import com.grayraccoon.sample.authms.domain.Users;
import com.grayraccoon.sample.authms.services.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CustomTokenEnhancer implements TokenEnhancer {

    private UserServiceImpl userService;

    @Autowired
    public CustomTokenEnhancer(UserServiceImpl userService) {
        this.userService = userService;
    }

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken oAuth2AccessToken, OAuth2Authentication oAuth2Authentication) {
        Map<String, Object> additionalInfo = new HashMap<>();

        String username = oAuth2Authentication.getName();
        Users user = userService.findUserByUsernameOrEmail(username);

        additionalInfo.put("userId", user.getUserId().toString());
        ((DefaultOAuth2AccessToken) oAuth2AccessToken).setAdditionalInformation(additionalInfo);

        return oAuth2AccessToken;
    }

}
