package com.grayraccoon.sample.accountsms.services;

import com.grayraccoon.sample.authdomain.domain.Users;

public interface UserEventsConsumerService {

    void userCreatedEventConsumer(Users users);
    void userUpdatedEventConsumer(Users users);
    void userDeletedEventConsumer(String usersId);

}
