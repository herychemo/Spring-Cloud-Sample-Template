package com.grayraccoon.sample.accountsms.channels;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface ConsumerUserEventsChannels {

    String USER_CREATED_CHANNEL = "producerUserCreatedMessageChannel";
    String USER_UPDATED_CHANNEL = "producerUserUpdatedMessageChannel";
    String USER_DELETED_CHANNEL = "producerUserDeletedMessageChannel";

    @Input(value = ConsumerUserEventsChannels.USER_CREATED_CHANNEL)
    SubscribableChannel userCreatedChannel();

    @Input(value = ConsumerUserEventsChannels.USER_UPDATED_CHANNEL)
    SubscribableChannel userUpdatedChannel();

    @Input(value = ConsumerUserEventsChannels.USER_DELETED_CHANNEL)
    SubscribableChannel userDeletedChannel();

}
