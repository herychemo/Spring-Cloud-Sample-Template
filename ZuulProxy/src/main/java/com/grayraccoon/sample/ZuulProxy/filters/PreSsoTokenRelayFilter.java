package com.grayraccoon.sample.ZuulProxy.filters;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

public class PreSsoTokenRelayFilter extends ZuulFilter {

    private OAuth2RestOperations restTemplate;

    public void setRestTemplate(OAuth2RestOperations restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 6;
    }

    @Override
    public boolean shouldFilter() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof OAuth2Authentication) {
            Object details = auth.getDetails();
            if (details instanceof OAuth2AuthenticationDetails) {
                OAuth2AuthenticationDetails oauth = (OAuth2AuthenticationDetails)details;
                RequestContext ctx = RequestContext.getCurrentContext();

                String authorization =
                        RequestContext.getCurrentContext().getRequest().getHeader("Authorization");

                if (authorization != null && StringUtils.startsWithIgnoreCase(authorization, "basic")) {
                    SecurityContextHolder.getContext().setAuthentication(null);
                    return false;
                }

                ctx.set("ACCESS_TOKEN", oauth.getTokenValue());
                ctx.set("TOKEN_TYPE", oauth.getTokenType() == null ? "Bearer" : oauth.getTokenType());
                return true;
            }
        }

        return false;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext ctx = RequestContext.getCurrentContext();
        String value = (String)ctx.get("ACCESS_TOKEN");
        OAuth2AccessToken accessToken = new DefaultOAuth2AccessToken(value);

        restTemplate.getOAuth2ClientContext().setAccessToken(accessToken);

        return null;
    }
}
