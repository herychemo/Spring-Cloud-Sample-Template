package com.grayraccoon.sample.SpringAdminServer.config;

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
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.web.context.request.RequestContextListener;


@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
@Configuration
@Profile("!default")
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${security.token-services.client-id}")
    private String tokenServicesClientId;

    @Value("${security.token-services.client-secret}")
    private String tokenServicesClientSecret;

    @Value("${security.token-services.check-token-url}")
    private String tokenServicesCheckTokenUrl;


    /*
    @Bean
    public HttpHeadersProvider addExtraTokenHeaderProvider(
            OAuth2AuthorizedClientService clientService) {
        return  instance -> {
            HttpHeaders httpHeaders = new HttpHeaders();

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null) {
                return httpHeaders;
            }

            OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) auth;

            OAuth2AuthorizedClient client = clientService.loadAuthorizedClient(
                    token.getAuthorizedClientRegistrationId(),
                    token.getName()
            );

            String accessToken = client.getAccessToken().getTokenValue();
            //System.out.println(String.format("Using AccessToken: %s", accessToken));
            httpHeaders.add(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", accessToken));

            return httpHeaders;
        };
    }
    */
/*
    @Bean
    public HttpHeadersProvider addExtraTokenHeaderProvider(
            @Qualifier("oauth2ClientContext") OAuth2ClientContext context) {
        return  instance -> {
            HttpHeaders httpHeaders = new HttpHeaders();

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null) {
                return httpHeaders;
            }

            String accessToken = context.getAccessToken().getValue();
            //System.out.println(String.format("Using AccessToken: %s", accessToken));

            httpHeaders.set(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", accessToken));

            return httpHeaders;
        };
    }
    */


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
