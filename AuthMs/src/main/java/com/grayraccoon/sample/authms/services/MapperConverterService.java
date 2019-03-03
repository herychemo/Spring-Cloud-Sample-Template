package com.grayraccoon.sample.authms.services;

import com.grayraccoon.sample.authms.data.postgres.domain.RolesEntity;
import com.grayraccoon.sample.authms.data.postgres.domain.UsersEntity;
import com.grayraccoon.sample.authms.domain.Roles;
import com.grayraccoon.sample.authms.domain.Users;

import java.util.List;
import java.util.Set;

public interface MapperConverterService {

    Users createUserFromUsersEntity(UsersEntity user);
    List<Users> createUsersListFromUsersEntitiesList(List<UsersEntity> usersEntityList);

    Roles createRoleFromRolesEntity(RolesEntity role);
    Set<Roles> createRolesSetFromRolesEntitiesSet(Set<RolesEntity> roles);

    UsersEntity createUsersEntityFromUser(Users user);

    RolesEntity createRolesEntityFromRole(Roles role);
    Set<RolesEntity> createRolesEntitiesSetFromRolesSet(Set<Roles> roles);

}
