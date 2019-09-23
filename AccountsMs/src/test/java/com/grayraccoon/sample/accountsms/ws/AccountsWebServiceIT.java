package com.grayraccoon.sample.accountsms.ws;

import com.grayraccoon.sample.accountsms.AccountsMsApplication;
import com.grayraccoon.webutils.test.auth.Oauth2Utils;
import org.flywaydb.core.Flyway;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * @author Heriberto Reyes Esparza
 */
@RunWith(SpringRunner.class)
@DirtiesContext
@SpringBootTest(classes = AccountsMsApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(properties = {
        "server.port=26142",
})
public class AccountsWebServiceIT {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountsWebServiceIT.class);

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
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
    public void findAllAccounts_Success_Test() throws Exception {
        String access_token = Oauth2Utils.getUserAccessToken(mockMvc, "admin", "password");

        mockMvc.perform(get("/ws/secured/accounts")
                .header("Authorization", "Bearer " + access_token)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.data", Matchers.notNullValue()))
                .andExpect(jsonPath("$.error").doesNotExist())
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].accountId", is("01e3d8d5-1119-4111-b3d0-be6562ca5914")))
                .andExpect(jsonPath("$.data[0].fullName", is("Some User Admin Admin")))
                .andExpect(jsonPath("$.data[1].accountId", is("01e3d8d5-1119-4111-b3d0-be6562ca5901")))
                .andExpect(jsonPath("$.data[1].fullName", is("Some User User User")))
        ;
    }


}
