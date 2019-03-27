package com.grayraccoon.sample.accountsms.config;

import com.grayraccoon.sample.accountsms.config.components.CustomAccessTokenConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
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

    @Value("${security.signing-key:dummy-value}")
    private String signingKey;


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
    @Profile("!default")
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
    @Profile("default")
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setSigningKey(signingKey);
        converter.setAccessTokenConverter(customAccessTokenConverter());
        return converter;
    }
    @Bean
    @Profile("default")
    public TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }
    @Bean
    @Primary
    @Profile("default")
    public DefaultTokenServices defaultTokenServices() {
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore());
        defaultTokenServices.setSupportRefreshToken(true);
        return defaultTokenServices;
    }



    @Bean
    public RequestContextListener requestContextListener() {
        return new RequestContextListener();
    }

}
