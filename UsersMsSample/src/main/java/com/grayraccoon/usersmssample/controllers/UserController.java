package com.grayraccoon.usersmssample.controllers;


import com.grayraccoon.usersmssample.domain.dto.Users;
import com.grayraccoon.usersmssample.services.UsersService;
import com.grayraccoon.webutils.errors.ApiError;
import com.grayraccoon.webutils.exceptions.CustomApiException;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class.getName());

    @Autowired
    private UsersService usersService;

    @HystrixCommand(fallbackMethod = "getUsersFallback",
            commandKey = "GetUsers",
            groupKey = "Users",
            ignoreExceptions = CustomApiException.class)
    @GetMapping(consumes = "application/json", produces = "application/json")
    public List<Users> getUsers() {
        LOGGER.info("getUsers()");
        return usersService.getAllUsers();
    }

    @HystrixCommand(fallbackMethod = "getUserFallback",
            commandKey = "GetUser",
            groupKey = "Users",
            ignoreExceptions = CustomApiException.class)
    @GetMapping(value = "/{user_id}", consumes = "application/json", produces = "application/json")
    public Users getUser(@PathVariable String user_id) {
        LOGGER.info("getUser() {}", user_id);
        Users user = usersService.getUserById(user_id);
        LOGGER.info("Found User {}", user);
        return user;
    }

    public List<Users> getUsersFallback(Throwable ex) {
        LOGGER.error("getUsersFallback", ex);
        throw new CustomApiException(ApiError.builder().ex(ex).build());
    }

    public Users getUserFallback(String id, Throwable ex) {
        LOGGER.error("getUserFallback", ex);
        throw new CustomApiException(ApiError.builder().ex(ex).build());
    }

}
