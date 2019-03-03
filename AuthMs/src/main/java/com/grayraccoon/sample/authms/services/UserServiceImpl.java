package com.grayraccoon.sample.authms.services;

import com.grayraccoon.sample.authms.data.postgres.domain.Roles;
import com.grayraccoon.sample.authms.data.postgres.domain.Users;
import com.grayraccoon.sample.authms.data.postgres.repository.RolesRepository;
import com.grayraccoon.sample.authms.data.postgres.repository.UsersRepository;
import com.grayraccoon.sample.authms.domain.dto.RolesDto;
import com.grayraccoon.sample.authms.domain.dto.UsersDto;
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
    public List<UsersDto> findAllUsers() {
        List<Users> users = usersRepository.findAll();
        return this.mapperConverterService.createUsersDtoListFromUsersList(users);
    }

    @Transactional(readOnly = true)
    @Override
    public UsersDto findUserById(String userId) {
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
    public UsersDto findUserById(UUID userId) {
        Optional<Users> usersOptional = usersRepository.findById(userId);
        if (usersOptional.isPresent()) {
            Users user = usersOptional.get();
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
    public UsersDto findUserByUsernameOrEmail(String query) {
        Users u = usersRepository.findFirstByEmailOrUsername(query, query);
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
    public UsersDto createUser(UsersDto usersDto) {
        if (usersDto.getUserId() != null) {
            throw new CustomApiException(
                    ApiError.builder()
                            .status(HttpStatus.BAD_REQUEST)
                            .subError(new ApiValidationError("userId", usersDto.getUserId().toString(), "New user must not send userId."))
                            .build()
            );
        }

        // set any default value for new Users

        // User is active by default
        usersDto.setActive(true);

        // New Users have ROLE_USER
        RolesDto userRole = this.mapperConverterService.createRolesDtoFromRole(
                rolesRepository.findFirstByRole(ROLE_QUERY_USER)
        );
        List<RolesDto> roles = new ArrayList<>();
        roles.add(userRole);
        usersDto.setRolesCollection(roles);

        // New Password must be hashed
        if (usersDto.getPassword() != null) {
            usersDto.setPassword(
                    userPasswordEncoder.encode(usersDto.getPassword())
            );
        }

        usersDto = this.saveUser(usersDto);

        // Send Event New User Was Created

        return usersDto;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    @Override
    public UsersDto saveUser(UsersDto usersDto) {

        Users user2save = mapperConverterService.createUserFromUsersDto(usersDto);

        if (user2save.getRolesCollection() == null) {
            throw new CustomApiException(
                    ApiError.builder()
                            .message("User must have at least one role.")
                            .build()
            );
        }

        for (Roles role: user2save.getRolesCollection()) {
            role.addUser(user2save);
        }

        user2save = usersRepository.saveAndFlush(user2save);
        UsersDto savedUser = mapperConverterService.createUsersDtoFromUser(user2save);

        return savedUser;
    }

}
