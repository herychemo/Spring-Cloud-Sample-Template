package com.grayraccoon.sample.authdomain.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Users implements Serializable {

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
    @Singular("role") private Set<Roles> rolesCollection;

    public Users(final Users user) {
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
