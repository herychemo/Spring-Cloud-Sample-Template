package com.grayraccoon.sample.authms.services;

import com.grayraccoon.sample.authms.data.postgres.domain.Roles;
import com.grayraccoon.sample.authms.data.postgres.domain.Users;
import com.grayraccoon.sample.authms.domain.dto.RolesDto;
import com.grayraccoon.sample.authms.domain.dto.UsersDto;

import java.util.Collection;
import java.util.List;

public interface MapperConverterService {

    UsersDto createUsersDtoFromUser(Users user);
    List<UsersDto> createUsersDtoListFromUsersList(List<Users> usersList);

    RolesDto createRolesDtoFromRole(Roles role);
    Collection<RolesDto> createRolesDtoCollectionFromRolesCollection(Collection<Roles> roles);

    Users createUserFromUsersDto(UsersDto user);

    Roles createRoleFromRolesDto(RolesDto role);
    Collection<Roles> createRolesCollectionFromRolesDtoCollection(Collection<RolesDto> roles);

}
