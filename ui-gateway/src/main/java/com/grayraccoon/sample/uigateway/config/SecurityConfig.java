package com.grayraccoon.sample.uigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.reactive.function.client.WebClient;

@EnableWebFluxSecurity
public class SecurityConfig {

    //https://www.baeldung.com/spring-oauth-login-webflux

    @Bean
    public SecurityWebFilterChain configure(ServerHttpSecurity http) throws Exception {
        return http.authorizeExchange()

                .pathMatchers("/", "/login**", "/error**", "/oauth2/**").permitAll()
                .pathMatchers("/actuator", "/actuator/health", "/actuator/info", "/actuator/hystrix.stream").permitAll()

                //.pathMatchers("/admin/**").permitAll()

                .anyExchange().authenticated()
                .and().oauth2Login()

                .and().build();
    }

    @Bean
    public WebClient webClient(ReactiveClientRegistrationRepository clientRegistrationRepo,
                               ServerOAuth2AuthorizedClientRepository authorizedClientRepo) {
        ServerOAuth2AuthorizedClientExchangeFilterFunction filter =
                new ServerOAuth2AuthorizedClientExchangeFilterFunction(clientRegistrationRepo, authorizedClientRepo);

        return WebClient.builder().filter(filter).build();
    }

}
