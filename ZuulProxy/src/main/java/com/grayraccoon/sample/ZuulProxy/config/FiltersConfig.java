package com.grayraccoon.sample.ZuulProxy.config;

import com.grayraccoon.sample.ZuulProxy.filters.PreSsoTokenRelayFilter;
import com.grayraccoon.sample.ZuulProxy.filters.SimpleLogFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2RestOperations;

@Configuration
public class FiltersConfig {

    private OAuth2RestOperations restTemplate;

    @Autowired
    public void setRestTemplate(OAuth2RestOperations restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Bean
    public SimpleLogFilter simpleLogFilter() {
        return new SimpleLogFilter();
    }

    @Bean
    public PreSsoTokenRelayFilter preSsoFilter() {
        PreSsoTokenRelayFilter preSsoTokenRelayFilter = new PreSsoTokenRelayFilter();
        preSsoTokenRelayFilter.setRestTemplate(restTemplate);
        return preSsoTokenRelayFilter;
    }

}
