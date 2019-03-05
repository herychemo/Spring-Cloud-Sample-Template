package com.grayraccoon.sample.authms.services;

import com.grayraccoon.sample.authms.data.postgres.domain.UsersEntity;
import com.grayraccoon.sample.authms.domain.Users;

import java.util.List;
import java.util.UUID;

public interface UserService {

    List<Users> findAllUsers();
    Users findUserById(String userId);
    Users findUserById(UUID userId);
    Users findUserByUsernameOrEmail(String query);

    Users createUser(Users users);

    Users updateUser(Users users, String sessionUserId);
    Users updateUser(Users users);

    void deleteUser(String userId);
    void deleteUser(UUID userId);

    void validateUsersEntity(UsersEntity usersEntity);

    boolean isValidEmailCombination(String email, UUID userId);
    boolean isValidUsernameCombination(String username, UUID userId);

}