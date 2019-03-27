package com.grayraccoon.sample.accountsms.config.components;

import com.grayraccoon.webutils.exceptions.CustomApiException;
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

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken oAuth2AccessToken, OAuth2Authentication oAuth2Authentication) {
        Map<String, Object> additionalInfo = new HashMap<>();

        String username = oAuth2Authentication.getName();

        if(username.equals("admin")) {
            OAuth2Request oAuth2Request = oAuth2Authentication.getOAuth2Request();

            additionalInfo.put("userId", "01e3d8d5-1119-4111-b3d0-be6562ca5914");
            additionalInfo.put("username", "admin");
            additionalInfo.put("email", "admin@admin.com");
            additionalInfo.put("clientId", oAuth2Request.getClientId());

            ((DefaultOAuth2AccessToken) oAuth2AccessToken).setAdditionalInformation(additionalInfo);
        }
        return oAuth2AccessToken;
    }

}
