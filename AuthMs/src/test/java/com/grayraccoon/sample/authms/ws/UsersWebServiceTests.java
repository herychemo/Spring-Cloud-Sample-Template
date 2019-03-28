package com.grayraccoon.sample.authms.ws;

import com.grayraccoon.sample.authdomain.domain.Users;
import com.grayraccoon.sample.authms.AuthMsApplication;
import com.grayraccoon.sample.authms.services.UserService;
import com.grayraccoon.webutils.errors.ApiError;
import com.grayraccoon.webutils.exceptions.CustomApiException;
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
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
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
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(usersWebService).build();
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
                .accept("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
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


}
