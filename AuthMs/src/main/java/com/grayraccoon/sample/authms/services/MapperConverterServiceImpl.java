package com.grayraccoon.sample.authms.services;

import com.grayraccoon.sample.authms.data.postgres.domain.Roles;
import com.grayraccoon.sample.authms.data.postgres.domain.Users;
import com.grayraccoon.sample.authms.domain.dto.RolesDto;
import com.grayraccoon.sample.authms.domain.dto.UsersDto;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MapperConverterServiceImpl implements MapperConverterService {

    @Override
    public UsersDto createUsersDtoFromUser(Users user) {
        return UsersDto.builder()
                .userId(user.getUserId())
                .active(user.isActive())
                .email(user.getEmail())
                .username(user.getUsername())
                .name(user.getName())
                .lastName(user.getLastName())
                .password(user.getPassword())
                .createDateTime(user.getCreateDateTime())
                .updateDateTime(user.getUpdateDateTime())
                .rolesCollection(
                        createRolesDtoCollectionFromRolesCollection(user.getRolesCollection())
                ).build();
    }

    @Override
    public List<UsersDto> createUsersDtoListFromUsersList(List<Users> usersList) {
        return usersList.stream().map(this::createUsersDtoFromUser).collect(Collectors.toList());
    }

    @Override
    public RolesDto createRolesDtoFromRole(Roles role) {
        return RolesDto.builder()
                .roleId(role.getRoleId())
                .role(role.getRole())
                .build();
    }

    @Override
    public Collection<RolesDto> createRolesDtoCollectionFromRolesCollection(Collection<Roles> roles) {
        return roles.stream().map(this::createRolesDtoFromRole).collect(Collectors.toList());
    }


    @Override
    public Users createUserFromUsersDto(UsersDto user) {
        return Users.builder()
                .userId(user.getUserId())
                .active(user.isActive())
                .email(user.getEmail())
                .username(user.getUsername())
                .name(user.getName())
                .lastName(user.getLastName())
                .password(user.getPassword())
                .createDateTime(user.getCreateDateTime())
                .updateDateTime(user.getUpdateDateTime())
                .rolesCollection(
                        createRolesCollectionFromRolesDtoCollection(user.getRolesCollection())
                ).build();
    }

    @Override
    public Roles createRoleFromRolesDto(RolesDto role) {
        return Roles.builder()
                .roleId(role.getRoleId())
                .role(role.getRole())
                .build();
    }

    @Override
    public Set<Roles> createRolesCollectionFromRolesDtoCollection(Collection<RolesDto> roles) {
        return roles.stream().map(this::createRoleFromRolesDto).collect(Collectors.toSet());
    }

}
