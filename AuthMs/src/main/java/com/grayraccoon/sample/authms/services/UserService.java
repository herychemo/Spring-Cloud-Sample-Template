package com.grayraccoon.sample.authms.services;

import com.grayraccoon.sample.authms.domain.dto.UsersDto;

import java.util.List;
import java.util.UUID;

public interface UserService {

    List<UsersDto> findAllUsers();
    UsersDto findUserById(String userId);
    UsersDto findUserById(UUID userId);
    UsersDto findUserByUsernameOrEmail(String query);
    UsersDto createUser(UsersDto usersDto);
    UsersDto saveUser(UsersDto usersDto);

}
