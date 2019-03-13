package com.grayraccoon.sample.SpringAdminServer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
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
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /*
    @Bean
    public InstanceExchangeFilterFunction addExtraSessionInfoInterceptor(
            OAuth2AuthorizedClientService clientService) {
        return (instance, request, next) -> {

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            if (auth == null) {
                return next.exchange(request);
            }

            OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) auth;

            OAuth2AuthorizedClient client = clientService.loadAuthorizedClient(
                    token.getAuthorizedClientRegistrationId(),
                    token.getName()
            );

            String accessToken = client.getAccessToken().getTokenValue();
            System.out.println(String.format("Using AccessToken: %s", accessToken));
            request.headers().add(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", accessToken));

            String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
            System.out.println(String.format("Using SessionId: %s", sessionId));
            request.cookies().add("JSESSIONID", sessionId);

            return next.exchange(request);
        };
    }*/


    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(HttpMethod.OPTIONS, "/**");
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .cors().and()
                .authorizeRequests()
                //.antMatchers("/actuator/**").permitAll()
                .antMatchers("/assets/**").permitAll()
                .antMatchers("/actuator", "/actuator/health", "/actuator/info").permitAll()
                .antMatchers(HttpMethod.POST, "/api/applications").permitAll()//
                .antMatchers("/login**").permitAll()
                .anyRequest().authenticated()
                .and().oauth2Login()
                .and().csrf().ignoringAntMatchers("/api/**", "/actuator/**")
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

}
