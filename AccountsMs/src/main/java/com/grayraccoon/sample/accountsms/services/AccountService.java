package com.grayraccoon.sample.accountsms.services;

import com.grayraccoon.sample.accountsdomain.domain.Accounts;
import com.grayraccoon.sample.accountsms.data.postgres.domain.AccountsEntity;

import java.util.List;
import java.util.UUID;

public interface AccountService {

    List<Accounts> findAllAccounts();

    Accounts findAccountById(String accountId);
    Accounts findAccountById(UUID accountId);

    Accounts createAccount(Accounts accounts);

    Accounts updateAccount(Accounts accounts, String sessionAccountId);
    Accounts updateAccount(Accounts accounts);

    void deleteAccount(String accountId);
    void deleteAccount(UUID accountId);
    void validateAccountsEntity(AccountsEntity accountsEntity);

}
