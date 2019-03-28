package com.grayraccoon.sample.accountsms;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AccountsMsApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AccountsMsOauth2TokenTests {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountsMsOauth2TokenTests.class);

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    private MockMvc mockMvc;


    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac)
                .addFilter(springSecurityFilterChain).build();
    }

    @Test
    public void getAccessTokenTestSuccess() throws Exception {
        String access_token = getAdminAccessToken(mockMvc, "admin","password");

        Assert.assertNotNull(access_token);
        Assert.assertNotEquals("", access_token);
    }

    @Test
    public void checkTokenTestSuccess() throws Exception {
        String access_token = getAdminAccessToken(mockMvc, "admin","password");

        ResultActions result =
                mockMvc.perform(get("/oauth/check_token")
                        .param("token", access_token)
                        .with(httpBasic("test-client-id","test-client-secret"))
                        .accept("application/json;charset=UTF-8"))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType("application/json;charset=UTF-8"))

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

    public static String getAdminAccessToken(MockMvc mockMvc, String username, String password) throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "password");
        params.add("client_id", "test-client-id");
        params.add("username", username);
        params.add("password", password);

        ResultActions result
                = mockMvc.perform(post("/oauth/token")
                .params(params)
                .with(httpBasic("test-client-id","test-client-secret"))
                .accept("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.access_token", Matchers.notNullValue()));

        String resultString = result.andReturn().getResponse().getContentAsString();

        JacksonJsonParser jsonParser = new JacksonJsonParser();

        String access_token = jsonParser.parseMap(resultString).get("access_token").toString();
        LOGGER.info("getAdminAccessToken(): {}", access_token);

        return access_token;
    }

}
