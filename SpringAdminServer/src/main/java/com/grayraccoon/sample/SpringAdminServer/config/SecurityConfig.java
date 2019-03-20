package com.grayraccoon.sample.SpringAdminServer.config;

import de.codecentric.boot.admin.server.web.client.HttpHeadersProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
@EnableOAuth2Sso
public class SecurityConfig extends WebSecurityConfigurerAdapter {

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

    @Bean
    public HttpHeadersProvider addExtraTokenHeaderProvider(
            @Qualifier("oauth2ClientContext") OAuth2ClientContext context,
            OAuth2RestOperations restTemplate) {
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


    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(HttpMethod.OPTIONS, "/**");
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .cors().and()

                .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers("/assets/**").permitAll()
                .antMatchers("/actuator", "/actuator/health", "/actuator/info", "/actuator/hystrix.stream").permitAll()
                .antMatchers(HttpMethod.POST, "/api/applications").permitAll()//
                .antMatchers("/login**", "/oauth2**").permitAll()
                .anyRequest().authenticated()

                //.and().oauth2Login()
                .and().csrf().ignoringAntMatchers(
                        "/api/**", "/oauth2/**",
                "/actuator/**", "/instances/**")
                .csrfTokenRepository(csrfTokenRepository()).and()
                .addFilterAfter(csrfHeaderFilter(), CsrfFilter.class);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
        return source;
    }

    private Filter csrfHeaderFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request,
                                            HttpServletResponse response, FilterChain filterChain)
                    throws ServletException, IOException {
                CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
                if (csrf != null) {
                    Cookie cookie = WebUtils.getCookie(request, "XSRF-TOKEN");
                    String token = csrf.getToken();
                    if (cookie == null || token != null && !token.equals(cookie.getValue())) {
                        cookie = new Cookie("XSRF-TOKEN", token);
                        cookie.setPath("/");
                        response.addCookie(cookie);
                    }
                }
                filterChain.doFilter(request, response);
            }
        };
    }

    private CsrfTokenRepository csrfTokenRepository() {
        HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
        repository.setHeaderName("X-XSRF-TOKEN");
        return repository;
    }



    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration().applyPermitDefaultValues();
        config.addAllowedMethod(HttpMethod.GET);
        config.addAllowedMethod(HttpMethod.POST);
        config.addAllowedMethod(HttpMethod.PUT);
        config.addAllowedMethod(HttpMethod.DELETE);
        config.addAllowedMethod(HttpMethod.OPTIONS);
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    @Bean
    public RequestContextListener requestContextListener() {
        return new RequestContextListener();
    }

    @Bean
    public OAuth2RestOperations restOperations(
            OAuth2ProtectedResourceDetails resource,
            @Qualifier("oauth2ClientContext") OAuth2ClientContext context
    ) {
        return new OAuth2RestTemplate(resource, context);
    }

}
