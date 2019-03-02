package com.grayraccoon.sample.authms.services;

import com.grayraccoon.sample.authms.data.postgres.domain.Roles;
import com.grayraccoon.sample.authms.data.postgres.domain.Users;
import com.grayraccoon.sample.authms.data.postgres.repository.UsersRepository;
import com.grayraccoon.webutils.errors.ApiError;
import com.grayraccoon.webutils.errors.ApiValidationError;
import com.grayraccoon.webutils.exceptions.CustomApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UsersRepository usersRepository;

    public List<Users> findAllUsers() {
        return usersRepository.findAll();
    }

    public Users findUserById(String userId) {
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

    public Users findUserById(UUID userId) {
        Optional<Users> usersOptional = usersRepository.findById(userId);
        if (usersOptional.isPresent()) {
            return usersOptional.get();
        }
        throw new CustomApiException(
                ApiError.builder()
                        .status(HttpStatus.NOT_FOUND)
                        .subError(new ApiValidationError(userId))
                        .build()
        );
    }

    public Users findByUsernameOrEmail(String query) {
        Users u;
        u = usersRepository.findByUsername(query);
        if (u == null) {
            u = usersRepository.findByEmail(query);
        }

        if (u == null) {
            throw new CustomApiException(
                    ApiError.builder()
                            .status(HttpStatus.NOT_FOUND)
                            .subError(new ApiValidationError(query))
                            .build()
            );
        }

        return u;
    }

    @Transactional(readOnly = true)
    public List<Roles> getRolesFor(UUID user_id) {
        Users u = usersRepository.findByUserId(user_id);
        return (u == null) ?
                new ArrayList<>() :
                new ArrayList<>(u.getRolesCollection());
    }

}
