package com.grayraccoon.sample.authms.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RolesDto implements Serializable {
    private Integer roleId;
    private String role;
}
