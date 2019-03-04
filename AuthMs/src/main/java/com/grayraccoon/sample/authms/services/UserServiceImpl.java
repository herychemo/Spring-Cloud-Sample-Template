package com.grayraccoon.sample.authms.services;

import com.grayraccoon.sample.authms.data.postgres.domain.RolesEntity;
import com.grayraccoon.sample.authms.data.postgres.domain.UsersEntity;
import com.grayraccoon.sample.authms.data.postgres.repository.RolesRepository;
import com.grayraccoon.sample.authms.data.postgres.repository.UsersRepository;
import com.grayraccoon.sample.authms.domain.Roles;
import com.grayraccoon.sample.authms.domain.Users;
import com.grayraccoon.webutils.errors.ApiError;
import com.grayraccoon.webutils.errors.ApiValidationError;
import com.grayraccoon.webutils.exceptions.CustomApiException;
import com.grayraccoon.webutils.services.CustomValidatorService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.grayraccoon.sample.authms.data.postgres.repository.RolesRepository.ROLE_QUERY_USER;

@Service
public class UserServiceImpl implements UserService {

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


    @Transactional(readOnly = true)
    @Override
    public List<Users> findAllUsers() {
        List<UsersEntity> users = usersRepository.findAll();
        return this.mapperConverterService.createUsersListFromUsersEntitiesList(users);
    }

    @Transactional(readOnly = true)
    @Override
    public Users findUserById(String userId) {
        try {
            return this.findUserById(UUID.fromString(userId));
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

    @Transactional(readOnly = true)
    @Override
    public Users findUserById(UUID userId) {
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

        // TODO: Send Event New User Was Created

        return users;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    @Override
    public Users updateUser(Users users, String sessionUserId) {

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
        Users originalUser = findUserById(users.getUserId());

        //No updatable information
        users.setCreateDateTime(originalUser.getCreateDateTime());
        users.setUpdateDateTime(null);

        // TODO: Different method will be created for updating password and roles
        users.setPassword(originalUser.getPassword());
        users.setRolesCollection(originalUser.getRolesCollection());


        users = saveUser(users);

        // TODO: Send Event User Was Updated

        return users;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    protected Users saveUser(Users users) {

        UsersEntity user2save = mapperConverterService.createUsersEntityFromUser(users);

        this.validateUsersEntity(user2save);

        Set<RolesEntity> userRoles = user2save.getRolesCollection();

        Set<RolesEntity> roles2persist = new HashSet<>();

        final UUID userId = user2save.getUserId();
        for (RolesEntity userRole: userRoles) {
            RolesEntity role2persist = rolesRepository.findFirstByRole(userRole.getRole());

            if (userId != null) {
                role2persist.getUsersCollection().removeIf(
                        userInRole -> userInRole.getUserId().equals(userId));
            }
            role2persist.addUser(user2save);
            roles2persist.add(role2persist);
        }
        user2save.setRolesCollection(roles2persist);

        user2save = usersRepository.saveAndFlush(user2save);
        Users savedUser = mapperConverterService.createUserFromUsersEntity(user2save);

        return savedUser;
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
        // if userId doesn't exist, next call will throw 404
        Users user2delete = findUserById(userId);

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

        //TODO: revoke all user access tokens

        // TODO: Send Event User Was deleted
    }


    @Override
    public void validateUsersEntity(UsersEntity usersEntity) {
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
