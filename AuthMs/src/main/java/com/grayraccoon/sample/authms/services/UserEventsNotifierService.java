package com.grayraccoon.sample.authms.services;

import com.grayraccoon.sample.authdomain.domain.Users;

public interface UserEventsNotifierService {

    void sendUserCreatedEvent(Users users);

    void sendUserUpdatedEvent(Users users);

    void sendUserDeletedEvent(String usersId);

}
