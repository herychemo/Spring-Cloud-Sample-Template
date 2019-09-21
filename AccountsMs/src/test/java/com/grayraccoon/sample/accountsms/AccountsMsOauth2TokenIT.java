package com.grayraccoon.sample.accountsms;

import com.grayraccoon.sample.accountsms.config.LocalTestsAppContext;
import com.grayraccoon.webutils.test.auth.Oauth2Utils;
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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {AccountsMsApplication.class})
public class AccountsMsOauth2TokenIT {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountsMsOauth2TokenIT.class);

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private FilterChainProxy springSecurityFilterChain;

    private MockMvc mockMvc;


    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac)
                .addFilter(springSecurityFilterChain).build();
    }

    @Test
    public void contextLoads() { }

    @Test
    public void getAccessTokenTestSuccess() throws Exception {
        String access_token = Oauth2Utils.getUserAccessToken(mockMvc, "admin","password");

        Assert.assertNotNull(access_token);
        Assert.assertNotEquals("", access_token);
    }

    @Test
    public void checkTokenTestSuccess() throws Exception {
        String access_token = Oauth2Utils.getUserAccessToken(mockMvc, "admin","password");

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
                ;

        String resultString = result.andReturn().getResponse().getContentAsString();
        LOGGER.info("checkTokenTestSuccess(): {}", resultString);
    }


}
