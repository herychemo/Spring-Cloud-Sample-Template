package com.grayraccoon.sample.authms.services;

import com.grayraccoon.sample.authms.data.postgres.domain.RolesEntity;
import com.grayraccoon.sample.authms.data.postgres.domain.UsersEntity;
import com.grayraccoon.sample.authms.domain.Roles;
import com.grayraccoon.sample.authms.domain.Users;

import java.util.Collection;
import java.util.List;

public interface MapperConverterService {

    Users createUsersDtoFromUser(UsersEntity user);
    List<Users> createUsersDtoListFromUsersList(List<UsersEntity> usersEntityList);

    Roles createRolesDtoFromRole(RolesEntity role);
    Collection<Roles> createRolesDtoCollectionFromRolesCollection(Collection<RolesEntity> roles);

    UsersEntity createUserFromUsersDto(Users user);

    RolesEntity createRoleFromRolesDto(Roles role);
    Collection<RolesEntity> createRolesCollectionFromRolesDtoCollection(Collection<Roles> roles);

}
