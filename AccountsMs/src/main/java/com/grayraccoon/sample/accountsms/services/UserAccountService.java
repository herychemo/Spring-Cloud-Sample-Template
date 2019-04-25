package com.grayraccoon.sample.accountsms.services;

import com.grayraccoon.sample.accountsdomain.domain.UserAccounts;

import java.util.UUID;

public interface UserAccountService {

    UserAccounts findUserAccountById(String userAccountId, boolean isMe);
    UserAccounts findUserAccountById(UUID userAccountId, boolean isMe);

    UserAccounts findUserAccountById(String userAccountId);
    UserAccounts findUserAccountById(UUID userAccountId);
}
