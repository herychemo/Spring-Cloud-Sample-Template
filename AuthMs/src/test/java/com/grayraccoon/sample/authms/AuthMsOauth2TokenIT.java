package com.grayraccoon.sample.authms;

import org.flywaydb.core.Flyway;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static com.grayraccoon.sample.authms.config.AuthUtils.getUserAccessToken;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AuthMsApplication.class)
public class AuthMsOauth2TokenIT {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthMsOauth2TokenIT.class);

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @Autowired
    private Flyway flyway;

    private MockMvc mockMvc;


    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac)
                .addFilter(springSecurityFilterChain).build();

        // We need to reapply migrations for tests to work
        flyway.clean();
        flyway.migrate();
    }

    @Test
    public void getAccessTokenTestSuccess() throws Exception {
        String access_token = getUserAccessToken(mockMvc, "admin","password");

        Assert.assertNotNull(access_token);
        Assert.assertNotEquals("", access_token);
    }

    @Test
    public void checkTokenTestSuccess() throws Exception {
        String access_token = getUserAccessToken(mockMvc, "admin","password");

        ResultActions result =
                mockMvc.perform(get("/oauth/check_token")
                        .param("token", access_token)
                        .with(httpBasic("test-client-id","test-client-secret"))
                        .accept(MediaType.APPLICATION_JSON_UTF8))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))

                        .andExpect(jsonPath("$.userId", Matchers.notNullValue()))
                        .andExpect(jsonPath("$.userId", Matchers.is("01e3d8d5-1119-4111-b3d0-be6562ca5914")))

                        .andExpect(jsonPath("$.username", Matchers.notNullValue()))
                        .andExpect(jsonPath("$.username", Matchers.is("admin")))
                        .andExpect(jsonPath("$.scope[*]",
                                Matchers.containsInAnyOrder("read","write","user_info")))
                        .andExpect(jsonPath("$.authorities[*]",
                                Matchers.containsInAnyOrder("ROLE_ADMIN","ROLE_USER")))
                        .andExpect(jsonPath("$.active", Matchers.is(true)))
                ;

        String resultString = result.andReturn().getResponse().getContentAsString();
        LOGGER.info("checkTokenTestSuccess(): {}", resultString);
    }

}
