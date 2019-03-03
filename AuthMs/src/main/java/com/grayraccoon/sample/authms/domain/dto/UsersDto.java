package com.grayraccoon.sample.authms.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;

@Data
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UsersDto implements Serializable {

    private UUID userId;
    private boolean active;
    private String email;
    private String username;
    private String name;
    private String lastName;

    @com.fasterxml.jackson.annotation.JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ToString.Exclude
    private String password;

    private LocalDateTime createDateTime;
    private LocalDateTime updateDateTime;
    @Singular("role") private Collection<RolesDto> rolesCollection;

    public UsersDto(final UsersDto user) {
        this.userId = user.userId;
        this.active = user.active;
        this.email = user.email;
        this.username = user.username;
        this.name = user.name;
        this.lastName = user.lastName;
        this.password = user.password;
        this.createDateTime = user.createDateTime;
        this.updateDateTime = user.updateDateTime;
        this.rolesCollection = user.rolesCollection;
    }

}
