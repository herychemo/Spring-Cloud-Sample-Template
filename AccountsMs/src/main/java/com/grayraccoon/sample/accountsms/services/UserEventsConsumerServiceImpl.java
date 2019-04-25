package com.grayraccoon.sample.accountsms.services;

import com.grayraccoon.sample.accountsdomain.domain.Accounts;
import com.grayraccoon.sample.accountsms.channels.ConsumerUserEventsChannels;
import com.grayraccoon.sample.authdomain.domain.Users;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Service;

@Service
public class UserEventsConsumerServiceImpl implements UserEventsConsumerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserEventsConsumerServiceImpl.class);

    @Autowired
    private AccountService accountService;

    @Override
    @StreamListener(ConsumerUserEventsChannels.USER_CREATED_CHANNEL)
    public void userCreatedEventConsumer(final Users users) {
        LOGGER.info("user created event received : {}", users);
        Accounts account = Accounts.builder()
                .accountId(users.getUserId())
                .fullName(String.format("%s %s", users.getName(), users.getLastName()))
                .nickName(users.getUsername())
                .build();
        account = accountService.createAccount(account);
        LOGGER.info("Account created from user event: {}", account);
    }

    @Override
    @StreamListener(ConsumerUserEventsChannels.USER_UPDATED_CHANNEL)
    public void userUpdatedEventConsumer(final Users users) {
        LOGGER.info("user updated event received : {}", users);
        Accounts account = accountService.findAccountById(users.getUserId());
        account.setFullName(String.format("%s %s", users.getName(), users.getLastName()));
        account = accountService.updateAccount(account);
        LOGGER.info("Account updated from user event: {}", account);
    }

    @Override
    @StreamListener(ConsumerUserEventsChannels.USER_DELETED_CHANNEL)
    public void userDeletedEventConsumer(final String usersId) {
        LOGGER.info("user deleted event received : {}", usersId);
        accountService.deleteAccount(usersId);
        LOGGER.info("Account deleted from user event: {}", usersId);
    }

}
