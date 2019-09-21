package com.grayraccoon.sample.authms.services.impl;

import com.grayraccoon.sample.authdomain.domain.Users;
import com.grayraccoon.sample.authms.channels.ProducerUserEventsChannels;
import com.grayraccoon.sample.authms.services.UserEventsNotifierService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class UserEventsNotifierServiceImpl implements UserEventsNotifierService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserEventsNotifierServiceImpl.class);

    private ProducerUserEventsChannels userEventsChannels;

    @Autowired
    public UserEventsNotifierServiceImpl(ProducerUserEventsChannels userEventsChannels) {
        this.userEventsChannels = userEventsChannels;
    }

    @Async
    @Override
    public void sendUserCreatedEvent(Users users) {
        LOGGER.info("Sending user created event...");
        final boolean userCreatedEventSent = userEventsChannels.userCreatedChannel().send(
                MessageBuilder.withPayload(users).build()
        );
        LOGGER.info("User created event sent: {}", userCreatedEventSent);
    }

    @Async
    @Override
    public void sendUserUpdatedEvent(Users users) {
        LOGGER.info("Sending user updated event...");
        final boolean userUpdatedEventSent = userEventsChannels.userUpdatedChannel().send(
                MessageBuilder.withPayload(users).build()
        );
        LOGGER.info("User updated event sent: {}", userUpdatedEventSent);
    }

    @Async
    @Override
    public void sendUserDeletedEvent(String userId) {
        LOGGER.info("Sending user deleted event...");
        final boolean userDeletedEventSent = userEventsChannels.userDeletedChannel().send(
                MessageBuilder.withPayload(userId).build()
        );
        LOGGER.info("User deleted event sent: {}", userDeletedEventSent);
    }

}
