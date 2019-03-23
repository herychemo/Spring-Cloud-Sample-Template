package com.grayraccoon.sample.authms.services;

import com.grayraccoon.sample.authms.data.postgres.domain.RolesEntity;
import com.grayraccoon.sample.authms.data.postgres.domain.UsersEntity;
import com.grayraccoon.sample.authms.data.postgres.repository.RolesRepository;
import com.grayraccoon.sample.authms.data.postgres.repository.UsersRepository;
import com.grayraccoon.sample.authdomain.domain.PasswordUpdaterModel;
import com.grayraccoon.sample.authdomain.domain.Roles;
import com.grayraccoon.sample.authdomain.domain.Users;
import com.grayraccoon.webutils.errors.ApiError;
import com.grayraccoon.webutils.errors.ApiValidationError;
import com.grayraccoon.webutils.exceptions.CustomApiException;
import com.grayraccoon.webutils.services.CustomValidatorService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.grayraccoon.sample.authms.data.postgres.repository.RolesRepository.ROLE_QUERY_ADMIN;
import static com.grayraccoon.sample.authms.data.postgres.repository.RolesRepository.ROLE_QUERY_USER;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private MapperConverterService mapperConverterService;

    @Autowired
    private PasswordEncoder userPasswordEncoder;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private RolesRepository rolesRepository;

    @Autowired
    private CustomValidatorService customValidatorService;

    @Autowired
    private CustomTokenOperationsService customTokenOperationsService;


    @Transactional(readOnly = true)
    @Override
    public List<Users> findAllUsers() {
        LOGGER.info("Finding all users...");
        List<UsersEntity> users = usersRepository.findAll();
        return this.mapperConverterService.createUsersListFromUsersEntitiesList(users);
    }

    @Transactional(readOnly = true)
    @Override
    public Users findUserById(String userId) {
        try {
            return this.findUserById(UUID.fromString(userId));
        } catch (IllegalArgumentException ex) {
            LOGGER.error("Error in FindUserById: {}, {}", userId, ex);
            throw new CustomApiException(
                    ApiError.builder()
                            .throwable(ex)
                            .status(HttpStatus.BAD_REQUEST)
                            .subError(new ApiValidationError(userId))
                            .build()
            );
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Users findUserById(UUID userId) {
        LOGGER.info("findUserById: {}", userId);
        Optional<UsersEntity> usersOptional = usersRepository.findById(userId);
        if (usersOptional.isPresent()) {
            UsersEntity user = usersOptional.get();
            return this.mapperConverterService.createUserFromUsersEntity(user);
        }
        throw new CustomApiException(
                ApiError.builder()
                        .status(HttpStatus.NOT_FOUND)
                        .subError(new ApiValidationError(userId))
                        .build()
        );
    }

    @Transactional(readOnly = true)
    @Override
    public Users findUserByUsernameOrEmail(String query) {
        LOGGER.info("findUserByUsernameOrEmail: {}", query);
        UsersEntity u = usersRepository.findFirstByEmailOrUsername(query, query);
        if (u == null) {
            throw new CustomApiException(
                    ApiError.builder()
                            .status(HttpStatus.NOT_FOUND)
                            .subError(new ApiValidationError(query))
                            .build()
            );
        }
        return this.mapperConverterService.createUserFromUsersEntity(u);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    @Override
    public Users createUser(Users users) {
        LOGGER.info("createUser: {}, {}", users.getEmail(), users.getUsername());
        if (users.getUserId() != null) {
            throw new CustomApiException(
                    ApiError.builder()
                            .status(HttpStatus.BAD_REQUEST)
                            .subError(new ApiValidationError(
                                    "userId",
                                    users.getUserId().toString(),
                                    "New user must not send userId."))
                            .build()
            );
        }

        // set any default value for new UsersEntity

        // User is active by default
        users.setActive(true);

        // New UsersEntity have ROLE_USER
        Roles userRole = this.mapperConverterService.createRoleFromRolesEntity(
                rolesRepository.findFirstByRole(ROLE_QUERY_USER)
        );
        Set<Roles> roles = new HashSet<>();
        roles.add(userRole);
        users.setRolesCollection(roles);

        // New Password must be hashed
        if (users.getPassword() != null) {
            users.setPassword(
                    userPasswordEncoder.encode(users.getPassword())
            );
        }

        users = this.saveUser(users);

        LOGGER.info("New user has been created: {}", users.getUserId());

        // TODO: Send Event New User Was Created

        return users;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    @Override
    public Users updateUser(Users users, String sessionUserId) {
        LOGGER.info("updateUser: {}, {}", sessionUserId, users);
        if (!sessionUserId.equals( users.getUserId().toString() )) {
            throw new CustomApiException(
                    ApiError.builder()
                            .status(HttpStatus.BAD_REQUEST)
                            .subError(new ApiValidationError(
                                    "userId",
                                    users.getUserId().toString(),
                                    "UserId doesn't match with UserId from authenticated user."))
                            .build()
            );
        }

        return this.updateUser(users);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    @Override
    public Users updateUser(Users users) {
        LOGGER.info("updateUser(): {}", users);

        Users originalUser = findUserById(users.getUserId());

        //No updatable information
        users.setCreateDateTime(originalUser.getCreateDateTime());
        users.setUpdateDateTime(null);

        users.setPassword(originalUser.getPassword());
        users.setRolesCollection(originalUser.getRolesCollection());


        users = saveUser(users);

        LOGGER.info("User info has been updated for: {}", users.getUserId());

        // TODO: Send Event User Was Updated

        return users;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    protected Users saveUser(Users users) {
        LOGGER.info("Saving User: {}, {}", users.getUserId(), users);

        UsersEntity user2save = mapperConverterService.createUsersEntityFromUser(users);

        this.validateUsersEntity(user2save);
        fixRoles(user2save);

        user2save = usersRepository.saveAndFlush(user2save);
        Users savedUser = mapperConverterService.createUserFromUsersEntity(user2save);

        LOGGER.info("A User was saved successfully: {}", savedUser.getUserId());

        return savedUser;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    protected void fixRoles(UsersEntity user2save) {
        LOGGER.info("Fixing Roles for userId: {}", user2save.getUserId());

        Set<RolesEntity> userRoles = user2save.getRolesCollection();
        Set<RolesEntity> roles2persist = new HashSet<>();

        final UUID userId = user2save.getUserId();

        handleMissingRoles(userId, userRoles);

        /* Handle new roles */
        for (RolesEntity userRole: userRoles) {
            RolesEntity role2persist = rolesRepository.findFirstByRole(userRole.getRole());

            boolean mustAddUser = true;

            if (userId != null) {
                boolean isUserInRole = role2persist.getUsersCollection().stream()
                        .anyMatch(userInRole -> userInRole.getUserId().equals(userId));
                if (isUserInRole) {
                    mustAddUser = false;
                }
            }

            if (mustAddUser) {
                role2persist.addUser(user2save);
            }

            roles2persist.add(role2persist);
        }
        user2save.setRolesCollection(roles2persist);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    protected void handleMissingRoles(UUID userId, Set<RolesEntity> userRoles) {
        if (userId == null) {
            return;
        }
        LOGGER.info("handling missing roles for: {}", userId);
        final UsersEntity oldUser = usersRepository.findByUserId(userId);
        if (oldUser != null) {
            /* If user already exists, handle missing roles */
            Set<RolesEntity> roles2delete = new HashSet<>();
            final Set<RolesEntity> oldUserRoles = oldUser.getRolesCollection();

            for (RolesEntity oldUserRole: oldUserRoles) {
                boolean isOldRoleInNewUserRoles = userRoles.stream().anyMatch(userRole -> userRole.getRoleId().equals(oldUserRole.getRoleId()));
                if (!isOldRoleInNewUserRoles) {
                    roles2delete.add(oldUserRole);
                }
            }

            LOGGER.info("For user: {} , deleting roles: {}", userId, roles2delete);

            for (RolesEntity role2delete: roles2delete) {
                oldUser.setRolesCollection(
                        oldUser.getRolesCollection().stream()
                                .filter(oldUserRole -> !oldUserRole.getRoleId().equals(role2delete.getRoleId()))
                                .collect(Collectors.toSet())
                );
                role2delete.setUsersCollection(
                        role2delete.getUsersCollection().stream()
                                .filter(userInRole -> !userInRole.getUserId().equals(oldUser.getUserId()))
                                .collect(Collectors.toSet())
                );
            }

            if (!roles2delete.isEmpty()) {
                //Updates deleted roles
                rolesRepository.saveAll(roles2delete);
            }
        }
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    @Override
    public void deleteUser(String userId) {
        try {
            this.deleteUser(UUID.fromString(userId));
        } catch (IllegalArgumentException ex) {
            throw new CustomApiException(
                    ApiError.builder()
                            .throwable(ex)
                            .status(HttpStatus.BAD_REQUEST)
                            .subError(new ApiValidationError(userId))
                            .build()
            );
        }
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    @Override
    public void deleteUser(UUID userId) {
        LOGGER.info("Trying to delete user: {}", userId);

        // if userId doesn't exist, next call will throw 404
        Users user2delete = findUserById(userId);

        //  Revoke all user access tokens due to user deletion
        revokeAllAccessTokens(userId);

        UsersEntity usersEntity2delete = mapperConverterService.createUsersEntityFromUser(user2delete);
        Set<RolesEntity> userRoles = usersEntity2delete.getRolesCollection();
        Set<RolesEntity> rolesUserRemoved = new HashSet<>();
        for (RolesEntity userRole: userRoles) {
            RolesEntity role2persist = rolesRepository.findFirstByRole(userRole.getRole());
            role2persist.getUsersCollection().remove(usersEntity2delete);
            rolesUserRemoved.add(role2persist);
        }
        usersEntity2delete.setRolesCollection(rolesUserRemoved);
        usersRepository.delete(usersEntity2delete);

        LOGGER.info("User deleted: {}", userId);

        // TODO: Send Event User Was deleted
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    @Override
    public Users updateUserPassword(String userId, PasswordUpdaterModel passwordUpdaterModel) {
        LOGGER.info("Updating user password: {}", userId);

        Users user = this.findUserById(userId);

        boolean validOldPassword = userPasswordEncoder
                .matches(passwordUpdaterModel.getOldPassword(), user.getPassword());

        if (!validOldPassword) {
            throw new CustomApiException(
                    ApiError.builder()
                            .status(HttpStatus.BAD_REQUEST)
                            .subError(new ApiValidationError(
                                    "oldPassword",
                                    null,
                                    "Old password doesn't match"))
                            .build()
            );
        }

        LOGGER.info("Old password validated, updating password for: {}", userId);

        return updateUserPasswordAsAdmin(userId, passwordUpdaterModel);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    @Override
    public Users updateUserPasswordAsAdmin(String userId, PasswordUpdaterModel passwordUpdaterModel) {
        LOGGER.info("updateUserPasswordAsAdmin(), {}", userId);

        Users user = this.findUserById(userId);

        String newPassword = userPasswordEncoder
                .encode(passwordUpdaterModel.getNewPassword());

        user.setPassword(newPassword);

        user = this.saveUser(user);

        //  Revoke all user access tokens due to password update
        revokeAllAccessTokens(userId);

        LOGGER.info("User password has been updated for: {}", userId);

        // TODO: Send Event user was updated

        return user;
    }

    @Transactional()
    @Override
    public Users toggleAdminRoleTo(String userId) {
        try {
            return this.toggleAdminRoleTo(UUID.fromString(userId));
        } catch (IllegalArgumentException ex) {
            throw new CustomApiException(
                    ApiError.builder()
                            .throwable(ex)
                            .status(HttpStatus.BAD_REQUEST)
                            .subError(new ApiValidationError(userId))
                            .build()
            );
        }
    }

    @Transactional()
    @Override
    public Users toggleAdminRoleTo(UUID userId) {
        return toggleUserRole(userId, ROLE_QUERY_ADMIN);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    protected Users toggleUserRole(UUID userId, String role) {
        LOGGER.info("Toggling {} role to user: {}", role, userId);

        Users user = this.findUserById(userId);

        boolean hasRole = doesUserHaveRole(user, role);
        RolesEntity rolesEntity = rolesRepository.findFirstByRole(role);
        if (hasRole) {
            user.setRolesCollection(
                    user.getRolesCollection().stream()
                            .filter(roles -> !roles.getRole().equalsIgnoreCase(role))
                    .collect(Collectors.toSet())
            );
        }else {
            final Roles role2add = mapperConverterService.createRoleFromRolesEntity(rolesEntity);
            user = user.toBuilder()
                    .role(role2add)
                    .build();
        }

        user = this.saveUser(user);

        //  Revoke all user access tokens due to updated roles
        revokeAllAccessTokens(userId);

        LOGGER.info("Role {}, has been toggled for: {}", role, userId);

        // TODO: Send Event user was updated

        return user;
    }

    private boolean doesUserHaveRole(Users user, String role) {
        if (user.getRolesCollection() == null) {
            return false;
        }
        return user.getRolesCollection().stream()
                .anyMatch(roles -> roles.getRole().equalsIgnoreCase(role));
    }


    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    @Override
    public void revokeAllAccessTokens(String userId) {
        try {
            this.revokeAllAccessTokens(UUID.fromString(userId));
        } catch (IllegalArgumentException ex) {
            throw new CustomApiException(
                    ApiError.builder()
                            .throwable(ex)
                            .status(HttpStatus.BAD_REQUEST)
                            .subError(new ApiValidationError(userId))
                            .build()
            );
        }
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    @Override
    public void revokeAllAccessTokens(UUID userId) {
        LOGGER.info("Revoking all access tokens to: {}", userId);

        Users user = findUserById(userId);
        customTokenOperationsService.revokeAllAccessTokensByUsernameList(
                user.getUsername(),
                user.getEmail()
        );
    }


    @Override
    public void validateUsersEntity(UsersEntity usersEntity) {
        LOGGER.info("validating a user entity for: {}", usersEntity.getUserId());

        Set<ApiValidationError> errors = new HashSet<>();

        if (usersEntity.getRolesCollection() == null) {
            errors.add(new ApiValidationError(
                    "roles",
                    null,
                    "User must have at least one role."));
        }

        errors.addAll(
                customValidatorService.validateObject(usersEntity)
        );

        if (StringUtils.isNotBlank(usersEntity.getEmail())
                && !isValidEmailCombination(usersEntity.getEmail(), usersEntity.getUserId())) {
            errors.add(new ApiValidationError(
                    "email",
                    usersEntity.getEmail(),
                    "Email not unique."));
        }

        if (StringUtils.isNotBlank(usersEntity.getUsername())
                && !isValidUsernameCombination(usersEntity.getUsername(), usersEntity.getUserId())) {
            errors.add(new ApiValidationError(
                    "username",
                    usersEntity.getUsername(),
                    "Username not unique."));
        }

        LOGGER.info("Errors found while validating user entity: {}", errors.size());

        if (!errors.isEmpty()) {
            ApiError apiError = customValidatorService.getApiErrorFromApiValidationErrors(errors);
            throw new CustomApiException(apiError);
        }
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    @Override
    public boolean isValidEmailCombination(String email, UUID userId) {
        return usersRepository.isValidEmailCombination(email, userId);
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    @Override
    public boolean isValidUsernameCombination(String username, UUID userId) {
        return usersRepository.isValidUsernameCombination(username, userId);
    }

}
