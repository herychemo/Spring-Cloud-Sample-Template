package com.grayraccoon.sample.authms.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Data
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RolesDto {
    private Integer roleId;
    private String role;
}
