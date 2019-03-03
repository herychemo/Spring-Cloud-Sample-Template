package com.grayraccoon.sample.authms.services;

import com.grayraccoon.sample.authms.data.postgres.domain.RolesEntity;
import com.grayraccoon.sample.authms.data.postgres.domain.UsersEntity;
import com.grayraccoon.sample.authms.domain.Roles;
import com.grayraccoon.sample.authms.domain.Users;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MapperConverterServiceImpl implements MapperConverterService {

    @Override
    public Users createUsersDtoFromUser(UsersEntity user) {
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
                        createRolesDtoCollectionFromRolesCollection(user.getRolesCollection())
                ).build();
    }

    @Override
    public List<Users> createUsersDtoListFromUsersList(List<UsersEntity> usersEntityList) {
        return usersEntityList.stream().map(this::createUsersDtoFromUser).collect(Collectors.toList());
    }

    @Override
    public Roles createRolesDtoFromRole(RolesEntity role) {
        return Roles.builder()
                .roleId(role.getRoleId())
                .role(role.getRole())
                .build();
    }

    @Override
    public Collection<Roles> createRolesDtoCollectionFromRolesCollection(Collection<RolesEntity> roles) {
        return roles.stream().map(this::createRolesDtoFromRole).collect(Collectors.toList());
    }


    @Override
    public UsersEntity createUserFromUsersDto(Users user) {
        return UsersEntity.builder()
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
    public RolesEntity createRoleFromRolesDto(Roles role) {
        return RolesEntity.builder()
                .roleId(role.getRoleId())
                .role(role.getRole())
                .build();
    }

    @Override
    public Set<RolesEntity> createRolesCollectionFromRolesDtoCollection(Collection<Roles> roles) {
        return roles.stream().map(this::createRoleFromRolesDto).collect(Collectors.toSet());
    }

}
