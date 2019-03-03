package com.grayraccoon.sample.authms.services;

import com.grayraccoon.sample.authms.data.postgres.domain.RolesEntity;
import com.grayraccoon.sample.authms.data.postgres.domain.UsersEntity;
import com.grayraccoon.sample.authms.data.postgres.repository.RolesRepository;
import com.grayraccoon.sample.authms.data.postgres.repository.UsersRepository;
import com.grayraccoon.sample.authms.domain.Roles;
import com.grayraccoon.sample.authms.domain.Users;
import com.grayraccoon.webutils.errors.ApiError;
import com.grayraccoon.webutils.errors.ApiValidationError;
import com.grayraccoon.webutils.exceptions.CustomApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.grayraccoon.sample.authms.data.postgres.repository.RolesRepository.ROLE_QUERY_USER;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private MapperConverterService mapperConverterService;

    @Autowired
    private PasswordEncoder userPasswordEncoder;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private RolesRepository rolesRepository;

    @Transactional(readOnly = true)
    @Override
    public List<Users> findAllUsers() {
        List<UsersEntity> users = usersRepository.findAll();
        return this.mapperConverterService.createUsersDtoListFromUsersList(users);
    }

    @Transactional(readOnly = true)
    @Override
    public Users findUserById(String userId) {
        try {
            return this.findUserById(UUID.fromString(userId));
        } catch (IllegalArgumentException ex) {
            throw new CustomApiException(
                    ApiError.builder()
                            .throwable(ex)
                            .status(HttpStatus.BAD_REQUEST)
                            .subError(new ApiValidationError(userId))
                            .build()
            );
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Users findUserById(UUID userId) {
        Optional<UsersEntity> usersOptional = usersRepository.findById(userId);
        if (usersOptional.isPresent()) {
            UsersEntity user = usersOptional.get();
            return this.mapperConverterService.createUsersDtoFromUser(user);
        }
        throw new CustomApiException(
                ApiError.builder()
                        .status(HttpStatus.NOT_FOUND)
                        .subError(new ApiValidationError(userId))
                        .build()
        );
    }

    @Transactional(readOnly = true)
    @Override
    public Users findUserByUsernameOrEmail(String query) {
        UsersEntity u = usersRepository.findFirstByEmailOrUsername(query, query);
        if (u == null) {
            throw new CustomApiException(
                    ApiError.builder()
                            .status(HttpStatus.NOT_FOUND)
                            .subError(new ApiValidationError(query))
                            .build()
            );
        }
        return this.mapperConverterService.createUsersDtoFromUser(u);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    @Override
    public Users createUser(Users users) {
        if (users.getUserId() != null) {
            throw new CustomApiException(
                    ApiError.builder()
                            .status(HttpStatus.BAD_REQUEST)
                            .subError(new ApiValidationError("userId", users.getUserId().toString(), "New user must not send userId."))
                            .build()
            );
        }

        // set any default value for new UsersEntity

        // User is active by default
        users.setActive(true);

        // New UsersEntity have ROLE_USER
        Roles userRole = this.mapperConverterService.createRolesDtoFromRole(
                rolesRepository.findFirstByRole(ROLE_QUERY_USER)
        );
        List<Roles> roles = new ArrayList<>();
        roles.add(userRole);
        users.setRolesCollection(roles);

        // New Password must be hashed
        if (users.getPassword() != null) {
            users.setPassword(
                    userPasswordEncoder.encode(users.getPassword())
            );
        }

        users = this.saveUser(users);

        // Send Event New User Was Created

        return users;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    @Override
    public Users saveUser(Users users) {

        UsersEntity user2save = mapperConverterService.createUserFromUsersDto(users);

        if (user2save.getRolesCollection() == null) {
            throw new CustomApiException(
                    ApiError.builder()
                            .message("User must have at least one role.")
                            .build()
            );
        }

        for (RolesEntity role: user2save.getRolesCollection()) {
            role.addUser(user2save);
        }

        user2save = usersRepository.saveAndFlush(user2save);
        Users savedUser = mapperConverterService.createUsersDtoFromUser(user2save);

        return savedUser;
    }

}
