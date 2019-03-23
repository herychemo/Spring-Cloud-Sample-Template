package com.grayraccoon.sample.accountsms.config;

import com.grayraccoon.sample.accountsms.config.components.CustomAccessTokenConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.web.context.request.RequestContextListener;

@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${security.token-services.client-id}")
    private String tokenServicesClientId;

    @Value("${security.token-services.client-secret}")
    private String tokenServicesClientSecret;

    @Value("${security.token-services.check-token-url}")
    private String tokenServicesCheckTokenUrl;


    @Bean
    protected CustomAccessTokenConverter customAccessTokenConverter() {
        return new CustomAccessTokenConverter();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(HttpMethod.OPTIONS, "/**");
    }


    @Bean
    @Primary
    public RemoteTokenServices tokenServices() {
        RemoteTokenServices tokenService = new RemoteTokenServices();

        tokenService.setCheckTokenEndpointUrl( // OAuth2 Server Url
                tokenServicesCheckTokenUrl);

        tokenService.setAccessTokenConverter(customAccessTokenConverter());

        // Client Id n Secret from oauth_client_details table
        tokenService.setClientId(tokenServicesClientId);
        tokenService.setClientSecret(tokenServicesClientSecret);

        return tokenService;
    }

    @Bean
    public RequestContextListener requestContextListener() {
        return new RequestContextListener();
    }


}
