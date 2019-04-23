package com.grayraccoon.sample.authms.ws;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grayraccoon.sample.authdomain.domain.Users;
import com.grayraccoon.sample.authms.AuthMsApplication;
import org.assertj.core.api.Assertions;
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

import java.util.Map;

import static com.grayraccoon.sample.authms.config.AuthUtils.getUserAccessToken;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
        final String access_token = getUserAccessToken(mockMvc, "admin","password");

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
        final String access_token = getUserAccessToken(mockMvc, "user","password");

        mockMvc.perform(get("/ws/secured/users")
                .header("Authorization", "Bearer " + access_token)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isForbidden())   //  403
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.error", Matchers.notNullValue()))
                .andExpect(jsonPath("$.data").doesNotExist())
        ;
    }


    @Test
    public void findMe_Success_Test() throws Exception {
        final String access_token = getUserAccessToken(mockMvc, "user", "password");

        final String user_userId = "01e3d8d5-1119-4111-b3d0-be6562ca5901";
        final boolean user_active = true;
        final String user_email = "user@user.com";
        final String user_username = "user";
        final String user_name = "Some User";
        final String user_lastName = "User User";

        mockMvc.perform(get("/ws/authenticated/users/me")
                .header("Authorization", "Bearer " + access_token)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.data", Matchers.notNullValue()))
                .andExpect(jsonPath("$.error").doesNotExist())
                .andExpect(jsonPath("$.data.userId", is(user_userId)))
                .andExpect(jsonPath("$.data.active", is(user_active)))
                .andExpect(jsonPath("$.data.email", is(user_email)))
                .andExpect(jsonPath("$.data.username", is(user_username)))
                .andExpect(jsonPath("$.data.name", is(user_name)))
                .andExpect(jsonPath("$.data.lastName", is(user_lastName)))
        ;
    }

    @Test
    public void findMe_Unauthorized_Test() throws Exception {
        mockMvc.perform(get("/ws/authenticated/users/me")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isUnauthorized())   //  401
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.error", Matchers.notNullValue()))
                .andExpect(jsonPath("$.data").doesNotExist())
        ;
    }


    @Test
    public void findUser_Success_Test() throws Exception {
        final String access_token = getUserAccessToken(mockMvc, "admin","password");

        final String user_userId = "01e3d8d5-1119-4111-b3d0-be6562ca5901";
        final boolean user_active = true;
        final String user_email = "user@user.com";
        final String user_username = "user";
        final String user_name = "Some User";
        final String user_lastName = "User User";

        mockMvc.perform(get(String.format("/ws/secured/users/%s", user_userId))
                .header("Authorization", "Bearer " + access_token)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.data", Matchers.notNullValue()))
                .andExpect(jsonPath("$.error").doesNotExist())
                .andExpect(jsonPath("$.data.userId", is(user_userId)))
                .andExpect(jsonPath("$.data.active", is(user_active)))
                .andExpect(jsonPath("$.data.email", is(user_email)))
                .andExpect(jsonPath("$.data.username", is(user_username)))
                .andExpect(jsonPath("$.data.name", is(user_name)))
                .andExpect(jsonPath("$.data.lastName", is(user_lastName)))
        ;
    }

    @Test
    public void findUser_NotFound_Test() throws Exception {
        final String access_token = getUserAccessToken(mockMvc, "admin","password");

        final String user_userId = "01e3d8d5-0009-4111-b3d0-be6562ca5922";

        mockMvc.perform(get(String.format("/ws/secured/users/%s", user_userId))
                .header("Authorization", "Bearer " + access_token)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound())   //  404
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.error", Matchers.notNullValue()))
                .andExpect(jsonPath("$.error.message", is("Not Found")))
                .andExpect(jsonPath("$.error.subErrors", Matchers.notNullValue()))
                .andExpect(jsonPath("$.error.subErrors", Matchers.hasSize(1)))
                .andExpect(jsonPath("$.error.subErrors[0]", Matchers.notNullValue()))
                .andExpect(jsonPath("$.error.subErrors[0].rejectedValue", Matchers.notNullValue()))
                .andExpect(jsonPath("$.error.subErrors[0].rejectedValue", is(user_userId)))
        ;
    }

    @Test
    public void findUser_Unauthorized_Test() throws Exception {
        final String user_userId = "01e3d8d5-1119-4111-b3d0-be6562ca5901";

        mockMvc.perform(get(String.format("/ws/secured/users/%s", user_userId))
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isUnauthorized())   //  401
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.error", Matchers.notNullValue()))
                .andExpect(jsonPath("$.data").doesNotExist())
        ;
    }

    @Test
    public void findUser_Forbidden_Test() throws Exception {
        final String access_token = getUserAccessToken(mockMvc, "user","password");

        final String user_userId = "01e3d8d5-1119-4111-b3d0-be6562ca5901";

        mockMvc.perform(get(String.format("/ws/secured/users/%s", user_userId))
                .header("Authorization", "Bearer " + access_token)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isForbidden())   //  403
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.error", Matchers.notNullValue()))
                .andExpect(jsonPath("$.data").doesNotExist())
        ;
    }

    @Test
    public void createUser_Success_Test() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();

        final String user_name = "Some User";
        final String user_lastName = "User New";
        final String user_email = "new@user.com";
        final String user_username = "newUser";
        final String user_password = "Some_password";

        final Users userRequestBody = Users.builder()
                .name(user_name)
                .lastName(user_lastName)
                .username(user_username)
                .email(user_email)
                .build();

        /* we first map user into map to enable serializing password field. */
        final Map<String, Object> userRequestBodyMap = mapper.convertValue(userRequestBody, Map.class);
        userRequestBodyMap.put("password", user_password);
        final String user_req_body_str = mapper.writeValueAsString(userRequestBodyMap);

        mockMvc.perform(post("/ws/users")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(user_req_body_str)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.error").doesNotExist())
                .andExpect(jsonPath("$.data", Matchers.notNullValue()))
                .andExpect(jsonPath("$.data.userId", Matchers.notNullValue()))
                .andExpect(jsonPath("$.data.active", is(true)))
                .andExpect(jsonPath("$.data.email", is(user_email)))
                .andExpect(jsonPath("$.data.username", is(user_username)))
                .andExpect(jsonPath("$.data.name", is(user_name)))
                .andExpect(jsonPath("$.data.lastName", is(user_lastName)))
                .andExpect(jsonPath("$.data.password").doesNotExist())
        ;

        final String access_token = getUserAccessToken(mockMvc, user_username,user_password);
        Assertions.assertThat(access_token).isNotNull();
        Assertions.assertThat(access_token).isNotBlank();
    }

    @Test
    public void createUser_FailedByFieldValidation_Test() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();

        final String user_name = "Some User";
        final String user_lastName = "User New";
        final String user_email = "new@user.com";
        final String user_username = "newUser";

        final Users userRequestBody = Users.builder()
                .name(user_name)
                .lastName(user_lastName)
                .username(user_username)
                .email(user_email)
                .build();

        final String user_req_body_str = mapper.writeValueAsString(userRequestBody);

        mockMvc.perform(post("/ws/users")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(user_req_body_str)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.error", Matchers.notNullValue()))
                .andExpect(jsonPath("$.error.message", is("Bad Request")))
                .andExpect(jsonPath("$.error.subErrors", Matchers.notNullValue()))
                .andExpect(jsonPath("$.error.subErrors", Matchers.hasSize(1)))
                .andExpect(jsonPath("$.error.subErrors[0]", Matchers.notNullValue()))
                .andExpect(jsonPath("$.error.subErrors[0].field", Matchers.notNullValue()))
                .andExpect(jsonPath("$.error.subErrors[0].field", is("password")))
                .andExpect(jsonPath("$.error.subErrors[0].message", Matchers.notNullValue()))
                .andExpect(jsonPath("$.error.subErrors[0].message").isNotEmpty())
        ;
    }

    @Test
    public void createUser_FailedByDbValidation_Test() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();

        final String user_name = "Some User";
        final String user_lastName = "User New";
        final String user_email = "user@user.com";
        final String user_username = "user";
        final String user_password = "Some_password";

        final Users userRequestBody = Users.builder()
                .name(user_name)
                .lastName(user_lastName)
                .username(user_username)
                .email(user_email)
                .build();

        /* we first map user into map to enable serializing password field. */
        final Map<String, Object> userRequestBodyMap = mapper.convertValue(userRequestBody, Map.class);
        userRequestBodyMap.put("password", user_password);
        final String user_req_body_str = mapper.writeValueAsString(userRequestBodyMap);

        mockMvc.perform(post("/ws/users")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(user_req_body_str)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.error", Matchers.notNullValue()))
                .andExpect(jsonPath("$.error.message", is("Bad Request")))
                .andExpect(jsonPath("$.error.subErrors", Matchers.notNullValue()))
                .andExpect(jsonPath("$.error.subErrors", Matchers.hasSize(2)))
                .andExpect(jsonPath("$.error.subErrors[0]", Matchers.notNullValue()))
                .andExpect(jsonPath("$.error.subErrors[1]", Matchers.notNullValue()))
                .andExpect(jsonPath("$.error.subErrors[0].message", Matchers.notNullValue()))
                .andExpect(jsonPath("$.error.subErrors[1].message", Matchers.notNullValue()))
                .andExpect(jsonPath("$.error.subErrors[*].field", Matchers.containsInAnyOrder("email", "username")))
                .andExpect(jsonPath("$.error.subErrors[*].rejectedValue", Matchers.containsInAnyOrder("user@user.com", "user")))
        ;
    }


}
