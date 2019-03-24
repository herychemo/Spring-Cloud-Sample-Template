package com.grayraccoon.sample.authdomain.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Roles implements Serializable {
    private Integer roleId;
    private String role;
}
