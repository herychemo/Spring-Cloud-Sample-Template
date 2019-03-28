package com.grayraccoon.sample.authms.ws;

import com.grayraccoon.sample.authdomain.domain.Users;
import com.grayraccoon.sample.authms.AuthMsApplication;
import com.grayraccoon.sample.authms.services.UserService;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.ServletException;
import java.util.Arrays;
import java.util.UUID;

import static com.grayraccoon.sample.authms.config.AuthUtils.getOauthTestAuthentication;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AuthMsApplication.class)
public class UsersWebServiceTests {

    private static final Logger LOGGER = LoggerFactory.getLogger(UsersWebServiceTests.class);

    private MockMvc mockMvc;

    @InjectMocks
    private UsersWebService usersWebService;

    @Mock
    private UserService userService;

    @Before
    public void setup() throws ServletException {
        SecurityContextHolderAwareRequestFilter authInjector = new SecurityContextHolderAwareRequestFilter();
        authInjector.afterPropertiesSet();

        this.mockMvc = MockMvcBuilders.standaloneSetup(usersWebService)
                .addFilter(authInjector)
                .build();
    }

    @Test
    public void findAllUsers_Success_Test() throws Exception {

        final String user0_userId = "01a0a0a0-1110-1000-a0a0-aa0000aa0000";
        final String user0_username = "zero";

        final String user1_userId = "01a0a0a0-1110-1000-a0a0-aa0000aa0001";
        final String user1_username = "someUser";

        final String user2_userId = "01a0a0a0-1110-1000-a0a0-aa0000aa0002";
        final String user2_username = "someOtherUser";

        Mockito.when(userService.findAllUsers()).thenReturn(
                Arrays.asList(
                        Users.builder()
                                .userId(UUID.fromString(user0_userId))
                                .username(user0_username)
                                .build(),
                        Users.builder()
                                .userId(UUID.fromString(user1_userId))
                                .username(user1_username)
                                .build(),
                        Users.builder()
                                .userId(UUID.fromString(user2_userId))
                                .username(user2_username)
                                .build()
                )
        );

        mockMvc.perform(get("/ws/secured/users")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.data", Matchers.notNullValue()))
                .andExpect(jsonPath("$.error").doesNotExist())
                .andExpect(jsonPath("$.data", hasSize(3)))
                .andExpect(jsonPath("$.data[0].userId", is(user0_userId)))
                .andExpect(jsonPath("$.data[0].username", is(user0_username)))
                .andExpect(jsonPath("$.data[1].userId", is(user1_userId)))
                .andExpect(jsonPath("$.data[1].username", is(user1_username)))
                .andExpect(jsonPath("$.data[2].userId", is(user2_userId)))
                .andExpect(jsonPath("$.data[2].username", is(user2_username)))
        ;
    }

    @Test
    public void findMe_Success_Test() throws Exception {

        final String user_userId = "01a0a0a0-1110-1000-a0a0-aa0000aa0000";
        final String user_username = "admin";
        final String user_name = "Some User";

        Mockito.when(userService.findUserById(anyString())).thenReturn(
                Users.builder()
                        .userId(UUID.fromString(user_userId))
                        .username(user_username)
                        .name(user_name)
                        .build()
        );

        SecurityContextHolder.getContext().setAuthentication(getOauthTestAuthentication());
        mockMvc.perform(get("/ws/authenticated/users/me")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.data", Matchers.notNullValue()))
                .andExpect(jsonPath("$.error").doesNotExist())
                .andExpect(jsonPath("$.data.userId", is(user_userId)))
                .andExpect(jsonPath("$.data.username", is(user_username)))
                .andExpect(jsonPath("$.data.name", is(user_name)))
        ;
    }


}
