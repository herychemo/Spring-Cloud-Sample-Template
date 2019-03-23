package com.grayraccoon.sample.accountsdomain.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Accounts implements Serializable {

    private String id;
    private String name;

}
