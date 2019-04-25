package com.grayraccoon.sample.accountsms.services;

import com.grayraccoon.sample.accountsdomain.domain.Accounts;
import com.grayraccoon.sample.accountsdomain.domain.UserAccounts;
import com.grayraccoon.sample.accountsms.clients.UsersClient;
import com.grayraccoon.sample.authdomain.domain.Users;
import com.grayraccoon.webutils.dto.GenericDto;
import com.grayraccoon.webutils.errors.ApiError;
import com.grayraccoon.webutils.errors.ApiValidationError;
import com.grayraccoon.webutils.exceptions.CustomApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserAccountServiceImpl implements UserAccountService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountServiceImpl.class.getName());

    @Autowired
    private AccountService accountService;

    @Autowired
    private UsersClient usersClient;


    @Override
    public UserAccounts findUserAccountById(String userAccountId) {
        return findUserAccountById(userAccountId, false);
    }

    @Override
    public UserAccounts findUserAccountById(UUID userAccountId) {
        return findUserAccountById(userAccountId, false);
    }

    @Override
    public UserAccounts findUserAccountById(String userAccountId, boolean isMe) {
        try {
            return this.findUserAccountById(UUID.fromString(userAccountId), isMe);
        } catch (IllegalArgumentException ex) {
            LOGGER.error("Error in findUserAccountById: {}, {}", userAccountId, ex);
            throw new CustomApiException(
                    ApiError.builder()
                            .throwable(ex)
                            .status(HttpStatus.BAD_REQUEST)
                            .subError(new ApiValidationError(userAccountId))
                            .build()
            );
        }
    }

    @Override
    public UserAccounts findUserAccountById(UUID userAccountId, boolean isMe) {
        LOGGER.info("findUserAccountById: {}", userAccountId);

        final Accounts account = accountService.findAccountById(userAccountId);

        final GenericDto<Users> userDTO = isMe ? usersClient.findMeUser() : usersClient.findMeUser(userAccountId.toString());

        final Users user = userDTO.getData() != null ? userDTO.getData() : new Users();
        if (userDTO.getError() != null) {
            final ApiError error = userDTO.getError();
            LOGGER.error(error.getMessage());
            LOGGER.error(error.getDebugMessage());
        }

        return UserAccounts.builder()
                .userAccountId(userAccountId)

                .active(user.isActive())
                .email(user.getEmail())
                .username(user.getUsername())
                .name(user.getName())
                .lastName(user.getLastName())
                .createDateTime(user.getCreateDateTime())
                .updateDateTime(user.getUpdateDateTime())

                .fullName(account.getFullName())
                .nickName(account.getNickName())
                .description(account.getDescription())
                .genre(account.getGenre())
                .phone(account.getPhone())
                .website(account.getWebsite())
                .address(account.getAddress())

                .build();
    }

}
