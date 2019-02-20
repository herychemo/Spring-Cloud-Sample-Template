package com.grayraccoon.usersmssample.controllers;


import com.grayraccoon.usersmssample.domain.dto.Users;
import com.grayraccoon.usersmssample.services.UsersService;
import com.grayraccoon.webutils.errors.ApiError;
import com.grayraccoon.webutils.exceptions.CustomApiException;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UsersService usersService;

    @HystrixCommand(fallbackMethod = "getUsersFallback",
            commandKey = "GetUsers",
            groupKey = "Users",
            ignoreExceptions = CustomApiException.class)
    @GetMapping(consumes = "application/json", produces = "application/json")
    public List<Users> getUsers() {
        return usersService.getAllUsers();
    }

    @HystrixCommand(fallbackMethod = "getUserFallback",
            commandKey = "GetUser",
            groupKey = "Users",
            ignoreExceptions = CustomApiException.class)
    @GetMapping(value = "/{user_id}", consumes = "application/json", produces = "application/json")
    public Users getUser(@PathVariable String user_id) {
        return usersService.getUserById(user_id);
    }

    public List<Users> getUsersFallback(Throwable ex) {
        ex.printStackTrace();
        throw new CustomApiException(ApiError.builder().ex(ex).build());
    }

    public Users getUserFallback(String id, Throwable ex) {
        ex.printStackTrace();
        throw new CustomApiException(ApiError.builder().ex(ex).build());
    }

}
