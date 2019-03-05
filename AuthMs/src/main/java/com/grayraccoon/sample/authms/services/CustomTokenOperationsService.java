package com.grayraccoon.sample.authms.services;

public interface CustomTokenOperationsService {
    void revokeAllAccessTokensByUsernameList(String... usernameList);
}
