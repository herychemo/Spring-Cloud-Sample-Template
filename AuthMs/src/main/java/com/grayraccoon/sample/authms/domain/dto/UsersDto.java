package com.grayraccoon.sample.authms.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;

@Data
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UsersDto implements Serializable {

    private UUID userId;
    private boolean active;
    private String email;
    private String username;
    private String name;
    private String lastName;
    private String password;
    private LocalDateTime createDateTime;
    private LocalDateTime updateDateTime;
    @Singular("role") private Collection<RolesDto> rolesCollection;

}
