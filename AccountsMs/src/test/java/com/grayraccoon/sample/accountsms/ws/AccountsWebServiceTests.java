package com.grayraccoon.sample.accountsms.ws;

import com.grayraccoon.sample.accountsdomain.domain.Accounts;
import com.grayraccoon.sample.accountsms.AccountsMsApplication;
import com.grayraccoon.sample.accountsms.services.AccountService;
import com.grayraccoon.webutils.test.auth.Oauth2Utils;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
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

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AccountsMsApplication.class)
public class AccountsWebServiceTests {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountsWebServiceTests.class);

    private MockMvc mockMvc;

    @InjectMocks
    private AccountsWebService usersWebService;

    @Mock
    private AccountService accountService;


    @Rule
    public ExpectedException expectedException = ExpectedException.none();


    @Before
    public void setup() throws ServletException {
        SecurityContextHolderAwareRequestFilter authInjector = new SecurityContextHolderAwareRequestFilter();
        authInjector.afterPropertiesSet();

        this.mockMvc = MockMvcBuilders.standaloneSetup(usersWebService)
                .addFilter(authInjector)
                .build();
    }


    @Test
    public void findAllAccounts_Success_Test() throws Exception {

        final String account0_accountId = "01a0a0a0-1110-1000-a0a0-aa0000aa0000";
        final String account0_fullName = "Some User Admin Admin";

        final String account1_accountId = "01a0a0a0-1110-1000-a0a0-aa0000aa0001";
        final String account1_fullName = "Some User User User";

        final String account2_accountId = "01a0a0a0-1110-1000-a0a0-aa0000aa0002";
        final String account2_fullName = "Some User Other User";

        Mockito.when(accountService.findAllAccounts()).thenReturn(Arrays.asList(
                Accounts.builder()
                        .accountId(UUID.fromString(account0_accountId))
                        .fullName(account0_fullName)
                        .build(),
                Accounts.builder()
                        .accountId(UUID.fromString(account1_accountId))
                        .fullName(account1_fullName)
                        .build(),
                Accounts.builder()
                        .accountId(UUID.fromString(account2_accountId))
                        .fullName(account2_fullName)
                        .build()
        ));

        mockMvc.perform(get("/ws/secured/accounts")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.data", Matchers.notNullValue()))
                .andExpect(jsonPath("$.error").doesNotExist())
                .andExpect(jsonPath("$.data", hasSize(3)))
                .andExpect(jsonPath("$.data[0].accountId", is(account0_accountId)))
                .andExpect(jsonPath("$.data[0].fullName", is(account0_fullName)))
                .andExpect(jsonPath("$.data[1].accountId", is(account1_accountId)))
                .andExpect(jsonPath("$.data[1].fullName", is(account1_fullName)))
                .andExpect(jsonPath("$.data[2].accountId", is(account2_accountId)))
                .andExpect(jsonPath("$.data[2].fullName", is(account2_fullName)))
        ;

    }

    @Test
    public void findAllAccounts_Failed_Test() throws Exception {
        final String exceptionMessage = "Some Internal Error.";

        expectedException.expectCause(isA(RuntimeException.class));
        expectedException.expectMessage(exceptionMessage);

        Mockito.when(accountService.findAllAccounts()).thenThrow(
                new RuntimeException(exceptionMessage)
        );

        mockMvc.perform(get("/ws/secured/accounts")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
        ;
    }


    @Test
    public void findMe_Success_Test() throws Exception {

        final String account_accountId = "01a0a0a0-1110-1000-a0a0-aa0000aa0000";
        final String account_fullName = "Some User Admin Admin";

        Mockito.when(accountService.findAccountById(anyString())).thenReturn(
                Accounts.builder()
                        .accountId(UUID.fromString(account_accountId))
                        .fullName(account_fullName)
                        .build()
        );

        SecurityContextHolder.getContext().setAuthentication(Oauth2Utils.getOauthTestAuthentication());
        mockMvc.perform(get("/ws/authenticated/accounts/me")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.data", Matchers.notNullValue()))
                .andExpect(jsonPath("$.error").doesNotExist())
                .andExpect(jsonPath("$.data.accountId", is(account_accountId)))
                .andExpect(jsonPath("$.data.fullName", is(account_fullName)))
        ;
    }


}
