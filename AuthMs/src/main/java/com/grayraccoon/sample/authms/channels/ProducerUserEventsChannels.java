package com.grayraccoon.sample.authms.channels;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface ProducerUserEventsChannels {

    String USER_CREATED_CHANNEL = "consumerUserCreatedMessageChannel";
    String USER_UPDATED_CHANNEL = "consumerUserUpdatedMessageChannel";
    String USER_DELETED_CHANNEL = "consumerUserDeletedMessageChannel";

    @Output(value = ProducerUserEventsChannels.USER_CREATED_CHANNEL)
    MessageChannel userCreatedChannel();

    @Output(value = ProducerUserEventsChannels.USER_UPDATED_CHANNEL)
    MessageChannel userUpdatedChannel();

    @Output(value = ProducerUserEventsChannels.USER_DELETED_CHANNEL)
    MessageChannel userDeletedChannel();

}
