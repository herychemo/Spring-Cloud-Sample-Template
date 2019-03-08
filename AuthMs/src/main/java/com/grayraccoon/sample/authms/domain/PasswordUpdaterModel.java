package com.grayraccoon.sample.authms.domain;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PasswordUpdaterModel implements Serializable {
    private String oldPassword;
    private String newPassword;
}
