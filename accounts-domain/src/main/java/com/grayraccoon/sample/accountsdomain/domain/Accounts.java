package com.grayraccoon.sample.accountsdomain.domain;

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
public class Accounts implements Serializable {

    private String id;
    private String name;

}
