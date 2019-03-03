package com.grayraccoon.sample.authms.services;

import com.grayraccoon.sample.authms.data.postgres.domain.Roles;
import com.grayraccoon.sample.authms.data.postgres.domain.Users;
import com.grayraccoon.sample.authms.domain.dto.RolesDto;
import com.grayraccoon.sample.authms.domain.dto.UsersDto;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MapperConverterService {

    public UsersDto createUsersDtoFromUser(Users user) {
        return UsersDto.builder()
                .userId(user.getUserId())
                .active(user.isActive())
                .email(user.getEmail())
                .username(user.getUsername())
                .name(user.getName())
                .lastName(user.getLastName())
                .password(null)
                .createDateTime(user.getCreateDateTime())
                .updateDateTime(user.getUpdateDateTime())
                .rolesCollection(
                        createRolesDtoCollectionFromRolesCollection(user.getRolesCollection())
                ).build();
    }

    public List<UsersDto> createUsersDtoListFromUsersList(List<Users> usersList) {
        return usersList.stream().map(this::createUsersDtoFromUser).collect(Collectors.toList());
    }

    public RolesDto createRolesDtoFromRoles(Roles role) {
        return RolesDto.builder()
                .roleId(role.getRoleId())
                .role(role.getRole())
                .build();
    }

    public Collection<RolesDto> createRolesDtoCollectionFromRolesCollection(Collection<Roles> roles) {
        return roles.stream().map(this::createRolesDtoFromRoles).collect(Collectors.toList());
    }


}
