package com.grayraccoon.sample.authms.ws;

import com.grayraccoon.sample.authms.AuthMsApplication;
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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static com.grayraccoon.sample.authms.config.AuthUtils.getUserAccessToken;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AuthMsApplication.class)
public class UsersWebServiceIT {

    private static final Logger LOGGER = LoggerFactory.getLogger(UsersWebServiceIT.class);

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
    public void findAllUsers_Success_Test() throws Exception {
        String access_token = getUserAccessToken(mockMvc, "admin","password");

        mockMvc.perform(get("/ws/secured/users")
                .header("Authorization", "Bearer " + access_token)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.data", Matchers.notNullValue()))
                .andExpect(jsonPath("$.error").doesNotExist())
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].userId", is("01e3d8d5-1119-4111-b3d0-be6562ca5914")))
                .andExpect(jsonPath("$.data[0].username", is("admin")))
                .andExpect(jsonPath("$.data[1].userId", is("01e3d8d5-1119-4111-b3d0-be6562ca5901")))
                .andExpect(jsonPath("$.data[1].username", is("user")))
        ;
    }

    @Test
    public void findAllUsers_Unauthorized_Test() throws Exception {
        mockMvc.perform(get("/ws/secured/users")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isUnauthorized())   //  401
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.error", Matchers.notNullValue()))
                .andExpect(jsonPath("$.data").doesNotExist())
        ;
    }

    @Test
    public void findAllUsers_Forbidden_Test() throws Exception {
        String access_token = getUserAccessToken(mockMvc, "user","password");

        mockMvc.perform(get("/ws/secured/users")
                .header("Authorization", "Bearer " + access_token)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.error", Matchers.notNullValue()))
                .andExpect(jsonPath("$.data").doesNotExist())
        ;
    }

}
