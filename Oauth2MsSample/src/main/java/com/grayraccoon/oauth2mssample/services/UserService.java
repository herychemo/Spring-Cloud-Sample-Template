package com.grayraccoon.oauth2mssample.services;

import com.grayraccoon.oauth2mssample.data.postgres.domain.Roles;
import com.grayraccoon.oauth2mssample.data.postgres.domain.Users;
import com.grayraccoon.oauth2mssample.data.postgres.repository.UsersRepository;
import com.grayraccoon.webutils.errors.ApiError;
import com.grayraccoon.webutils.errors.ApiValidationError;
import com.grayraccoon.webutils.exceptions.CustomApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    UsersRepository usersRepository;


    public Users findUserById(String userId) {
        return this.findUserById(UUID.fromString(userId));
    }
    public Users findUserById(UUID userId) {
        return usersRepository.getOne(userId);
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
