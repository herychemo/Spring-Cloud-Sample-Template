package com.grayraccoon.usersmssample.controllers;


import com.grayraccoon.usersmssample.domain.dto.Users;
import com.grayraccoon.usersmssample.services.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UsersService usersService;

    @GetMapping(consumes = "application/json", produces = "application/json")
    public List<Users> getUsers() {
        return usersService.getAllUsers();
    }

    @GetMapping(value = "/{user_id}", consumes = "application/json", produces = "application/json")
    public Users getUser(@PathVariable String user_id) {
        return usersService.getUserById(user_id);
    }

}
