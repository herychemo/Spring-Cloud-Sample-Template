package com.grayraccoon.usersmssample.services;

import com.grayraccoon.usersmssample.domain.dto.Users;
import com.grayraccoon.webutils.errors.ApiError;
import com.grayraccoon.webutils.errors.ApiValidationError;
import com.grayraccoon.webutils.exceptions.CustomApiException;
import org.apache.commons.lang.math.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UsersService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UsersService.class.getName());

    private List<Users> allUsers;

    public UsersService() {
        this.allUsers = new ArrayList<>();
        this.allUsers.add(new Users("1", "Simple User"));
        this.allUsers.add(new Users("2", "Second User"));
    }

    public List<Users> getAllUsers() {
        return new ArrayList<>(this.allUsers);
    }

    public Users getUserById(String user_id) {

        if (RandomUtils.nextBoolean() && RandomUtils.nextBoolean() && RandomUtils.nextBoolean()) {
            throw new RuntimeException();
        }

        Optional<Users> foundUser = this.allUsers.stream().filter(users -> users.getId().equals(user_id)).findFirst();
        if (foundUser.isPresent()) {
            return foundUser.get();
        }

        LOGGER.info("User not found: {}", user_id);
        throw new CustomApiException(
                ApiError.builder()
                        .status(HttpStatus.NOT_FOUND)
                        .subError(new ApiValidationError(user_id))
                        .build()
        );

    }

}
