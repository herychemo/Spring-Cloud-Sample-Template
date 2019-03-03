package com.grayraccoon.sample.authms.services;

import com.grayraccoon.sample.authms.domain.Users;

import java.util.List;
import java.util.UUID;

public interface UserService {

    List<Users> findAllUsers();
    Users findUserById(String userId);
    Users findUserById(UUID userId);
    Users findUserByUsernameOrEmail(String query);
    Users createUser(Users users);
    Users saveUser(Users users);

}
