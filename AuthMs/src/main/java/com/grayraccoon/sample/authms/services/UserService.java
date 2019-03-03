package com.grayraccoon.sample.authms.services;

import com.grayraccoon.sample.authms.data.postgres.domain.Users;
import com.grayraccoon.sample.authms.data.postgres.repository.UsersRepository;
import com.grayraccoon.sample.authms.domain.dto.UsersDto;
import com.grayraccoon.webutils.errors.ApiError;
import com.grayraccoon.webutils.errors.ApiValidationError;
import com.grayraccoon.webutils.exceptions.CustomApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private MapperConverterService mapperConverterService;

    @Autowired
    private UsersRepository usersRepository;

    @Transactional(readOnly = true)
    public List<UsersDto> findAllUsers() {
        List<Users> users = usersRepository.findAll();
        return this.mapperConverterService.createUsersDtoListFromUsersList(users);
    }

    @Transactional(readOnly = true)
    public UsersDto findUserById(String userId) {
        try {
            return this.findUserById(UUID.fromString(userId));
        } catch (IllegalArgumentException ex) {
            throw new CustomApiException(
                    ApiError.builder()
                            .ex(ex)
                            .status(HttpStatus.BAD_REQUEST)
                            .subError(new ApiValidationError(userId))
                            .build()
            );
        }
    }

    @Transactional(readOnly = true)
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

}
