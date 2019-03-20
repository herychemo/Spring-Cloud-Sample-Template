package com.grayraccoon.sample.ZuulProxy.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.DefaultAccessTokenRequest;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationManager;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationProcessingFilter;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.web.context.request.RequestContextListener;


@Configuration
@EnableOAuth2Sso
@EnableResourceServer
@Order(value = 0)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String CSRF_COOKIE_NAME = "XSRF-TOKEN";
    private static final String CSRF_HEADER_NAME = "X-XSRF-TOKEN";

    @Value("${security.token-services.client-id}")
    private String tokenServicesClientId;

    @Value("${security.token-services.client-secret}")
    private String tokenServicesClientSecret;

    @Value("${security.token-services.check-token-url}")
    private String tokenServicesCheckTokenUrl;

    @Value("${security.jwt.local-resource-id}")
    private String localResourceId;


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.antMatcher("/**")
                .authorizeRequests()

                .antMatchers("/", "/login**", "/error**", "/auth-ms/**").permitAll()
                .antMatchers("/admin/**").authenticated()


                .antMatchers("/actuator", "/actuator/health", "/actuator/info", "/actuator/hystrix.stream").permitAll()
                .antMatchers("/actuator/**").authenticated()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                //.antMatchers("/actuator/**").permitAll()
                .anyRequest().permitAll()

                .and().addFilterAfter(oAuth2AuthenticationProcessingFilter(),
                AbstractPreAuthenticatedProcessingFilter.class)

                .logout().permitAll().logoutSuccessUrl("/")
                .and().cors()
                .and().csrf().disable()
        ;
    }


    private OAuth2AuthenticationProcessingFilter oAuth2AuthenticationProcessingFilter() {
        OAuth2AuthenticationProcessingFilter oAuth2AuthenticationProcessingFilter = new OAuth2AuthenticationProcessingFilter();
        oAuth2AuthenticationProcessingFilter.setAuthenticationManager(oauthAuthenticationManager());
        oAuth2AuthenticationProcessingFilter.setStateless(false);

        return oAuth2AuthenticationProcessingFilter;
    }
    private AuthenticationManager oauthAuthenticationManager() {
        OAuth2AuthenticationManager oAuth2AuthenticationManager = new OAuth2AuthenticationManager();
        oAuth2AuthenticationManager.setResourceId(localResourceId);
        oAuth2AuthenticationManager.setTokenServices(tokenServices());
        oAuth2AuthenticationManager.setClientDetailsService(null);

        return oAuth2AuthenticationManager;
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

    @Bean
    public OAuth2RestOperations restOperations(
            OAuth2ProtectedResourceDetails resource
    ) {
        OAuth2ClientContext context = new DefaultOAuth2ClientContext(new DefaultAccessTokenRequest());
        return new OAuth2RestTemplate(resource, context);
    }

}
