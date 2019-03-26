package com.grayraccoon.sample.accountsms.services;

import com.grayraccoon.sample.accountsdomain.domain.Accounts;
import com.grayraccoon.sample.accountsms.data.postgres.domain.AccountsEntity;
import com.grayraccoon.sample.accountsms.data.postgres.repository.AccountsRepository;
import com.grayraccoon.webutils.errors.ApiError;
import com.grayraccoon.webutils.errors.ApiValidationError;
import com.grayraccoon.webutils.exceptions.CustomApiException;
import com.grayraccoon.webutils.services.CustomValidatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class AccountServiceImpl implements AccountService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountServiceImpl.class.getName());

    @Autowired
    private MapperConverterService mapperConverterService;

    @Autowired
    private AccountsRepository accountsRepository;

    @Autowired
    private CustomValidatorService customValidatorService;

    @Transactional(readOnly = true)
    @Override
    public List<Accounts> findAllAccounts() {
        LOGGER.info("Finding all accounts...");
        List<AccountsEntity> accounts = accountsRepository.findAll();
        return this.mapperConverterService.createAccountsListFromAccountsEntitiesList(accounts);
    }

    @Transactional(readOnly = true)
    @Override
    public Accounts findAccountById(String accountId) {
        try {
            return this.findAccountById(UUID.fromString(accountId));
        } catch (IllegalArgumentException ex) {
            LOGGER.error("Error in FindAccountById: {}, {}", accountId, ex);
            throw new CustomApiException(
                    ApiError.builder()
                            .throwable(ex)
                            .status(HttpStatus.BAD_REQUEST)
                            .subError(new ApiValidationError(accountId))
                            .build()
            );
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Accounts findAccountById(UUID accountId) {
        LOGGER.info("findAccountById: {}", accountId);
        Optional<AccountsEntity> accountsOptional = accountsRepository.findById(accountId);
        if (accountsOptional.isPresent()) {
            AccountsEntity account = accountsOptional.get();
            return this.mapperConverterService.createAccountFromAccountsEntity(account);
        }
        throw new CustomApiException(
                ApiError.builder()
                        .status(HttpStatus.NOT_FOUND)
                        .subError(new ApiValidationError(accountId))
                        .build()
        );
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    @Override
    public Accounts createAccount(Accounts accounts) {
        LOGGER.info("createAccount: {}", accounts.getFullName());
        if (accounts.getAccountId() != null) {
            throw new CustomApiException(
                    ApiError.builder()
                            .status(HttpStatus.BAD_REQUEST)
                            .subError(new ApiValidationError(
                                    "accountId",
                                    accounts.getAccountId().toString(),
                                    "New account must not send accountId."))
                            .build()
            );
        }

        // set any default value for new AccountsEntity

        accounts.setDescription("");

        accounts = this.saveAccount(accounts);

        LOGGER.info("New account has been created: {}", accounts.getAccountId());

        // Create Account Done.

        return accounts;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    @Override
    public Accounts updateAccount(Accounts accounts, String sessionAccountId) {
        LOGGER.info("updateAccount: {}, {}", sessionAccountId, accounts);
        if (!sessionAccountId.equals( accounts.getAccountId().toString() )) {
            throw new CustomApiException(
                    ApiError.builder()
                            .status(HttpStatus.BAD_REQUEST)
                            .subError(new ApiValidationError(
                                    "accountId",
                                    accounts.getAccountId().toString(),
                                    "AccountId doesn't match with AccountId from authenticated account."))
                            .build()
            );
        }

        return this.updateAccount(accounts);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    @Override
    public Accounts updateAccount(Accounts accounts) {
        LOGGER.info("updateAccount(): {}", accounts);

        Accounts originalAccount = findAccountById(accounts.getAccountId());

        //No updatable information
        accounts.setCreateDateTime(originalAccount.getCreateDateTime());
        accounts.setUpdateDateTime(null);


        accounts = saveAccount(accounts);

        LOGGER.info("Account info has been updated for: {}", accounts.getAccountId());

        // Update Account Done.

        return accounts;
    }


    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    protected Accounts saveAccount(Accounts accounts) {
        LOGGER.info("Saving Account: {}, {}", accounts.getAccountId(), accounts);

        AccountsEntity account2save = mapperConverterService.createAccountsEntityFromAccount(accounts);

        this.validateAccountsEntity(account2save);

        account2save = accountsRepository.saveAndFlush(account2save);
        Accounts savedAccount = mapperConverterService.createAccountFromAccountsEntity(account2save);

        LOGGER.info("An Account was saved successfully: {}", savedAccount.getAccountId());

        return savedAccount;
    }


    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    @Override
    public void deleteAccount(String accountId) {
        try {
            this.deleteAccount(UUID.fromString(accountId));
        } catch (IllegalArgumentException ex) {
            throw new CustomApiException(
                    ApiError.builder()
                            .throwable(ex)
                            .status(HttpStatus.BAD_REQUEST)
                            .subError(new ApiValidationError(accountId))
                            .build()
            );
        }
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    @Override
    public void deleteAccount(UUID accountId) {
        LOGGER.info("Trying to delete account: {}", accountId);

        // if accountId doesn't exist, next call will throw 404
        Accounts account2delete = findAccountById(accountId);

        AccountsEntity accountsEntity2delete = mapperConverterService.createAccountsEntityFromAccount(account2delete);

        accountsRepository.delete(accountsEntity2delete);

        LOGGER.info("Account deleted: {}", accountId);

        // Delete Account Done.
    }

    @Override
    public void validateAccountsEntity(AccountsEntity accountsEntity) {
        LOGGER.info("validating an account entity for: {}", accountsEntity.getAccountId());

        Set<ApiValidationError> errors = new HashSet<>();

        // Custom Manual Validations
        //

        // Validations from annotations
        errors.addAll(
                customValidatorService.validateObject(accountsEntity)
        );

        // Validations that require database
        //

        LOGGER.info("Errors found while validating accounts entity: {}", errors.size());

        if (!errors.isEmpty()) {
            ApiError apiError = customValidatorService.getApiErrorFromApiValidationErrors(errors);
            throw new CustomApiException(apiError);
        }
    }

}
