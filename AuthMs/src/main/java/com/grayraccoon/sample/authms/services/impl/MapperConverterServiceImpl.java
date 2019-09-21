package com.grayraccoon.sample.authms.services.impl;

import com.grayraccoon.sample.authms.data.postgres.domain.RolesEntity;
import com.grayraccoon.sample.authms.data.postgres.domain.UsersEntity;
import com.grayraccoon.sample.authdomain.domain.Roles;
import com.grayraccoon.sample.authdomain.domain.Users;
import com.grayraccoon.sample.authms.services.MapperConverterService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MapperConverterServiceImpl implements MapperConverterService {

    @Override
    public Users createUserFromUsersEntity(UsersEntity user) {
        if (user == null) {
            return null;
        }
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
                        createRolesSetFromRolesEntitiesSet(user.getRolesCollection())
                )
                .build();
    }

    @Override
    public List<Users> createUsersListFromUsersEntitiesList(List<UsersEntity> usersEntityList) {
        return usersEntityList.stream().map(this::createUserFromUsersEntity).collect(Collectors.toList());
    }

    @Override
    public Roles createRoleFromRolesEntity(RolesEntity role) {
        return Roles.builder()
                .roleId(role.getRoleId())
                .role(role.getRole())
                .build();
    }

    @Override
    public Set<Roles> createRolesSetFromRolesEntitiesSet(Set<RolesEntity> roles) {
        return roles.stream().map(this::createRoleFromRolesEntity).collect(Collectors.toSet());
    }


    @Override
    public UsersEntity createUsersEntityFromUser(Users user) {
        if (user == null) {
            return null;
        }
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
                        createRolesEntitiesSetFromRolesSet(user.getRolesCollection())
                )
                .build();
    }

    @Override
    public RolesEntity createRolesEntityFromRole(Roles role) {
        return RolesEntity.builder()
                .roleId(role.getRoleId())
                .role(role.getRole())
                .build();
    }

    @Override
    public Set<RolesEntity> createRolesEntitiesSetFromRolesSet(Set<Roles> roles) {
        return roles.stream().map(this::createRolesEntityFromRole).collect(Collectors.toSet());
    }

}
