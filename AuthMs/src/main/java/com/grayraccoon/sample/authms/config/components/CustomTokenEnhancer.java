package com.grayraccoon.sample.authms.config.components;

import com.grayraccoon.sample.authdomain.domain.Users;
import com.grayraccoon.sample.authms.services.UserServiceImpl;
import com.grayraccoon.webutils.exceptions.CustomApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
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
        try {
            final Users user = userService.findUserByUsernameOrEmail(username);

            OAuth2Request oAuth2Request = oAuth2Authentication.getOAuth2Request();

            additionalInfo.put("userId", user.getUserId().toString());
            additionalInfo.put("username", user.getUsername());
            additionalInfo.put("email", user.getEmail());
            additionalInfo.put("clientId", oAuth2Request.getClientId());

            ((DefaultOAuth2AccessToken) oAuth2AccessToken).setAdditionalInformation(additionalInfo);
        } catch (CustomApiException ex) {
            // User doesn't exist
        }
        return oAuth2AccessToken;
    }

}
