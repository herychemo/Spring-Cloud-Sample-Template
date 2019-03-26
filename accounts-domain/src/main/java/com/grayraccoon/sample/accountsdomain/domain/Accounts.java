package com.grayraccoon.sample.accountsdomain.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.grayraccoon.sample.accountsdomain.values.Address;
import com.grayraccoon.sample.accountsdomain.values.Genre;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Accounts implements Serializable {

    private UUID accountId;
    private String fullName;
    private String nickName;
    private String description;
    private Genre genre;
    private String phone;
    private String website;
    private Address address;

    private LocalDateTime createDateTime;
    private LocalDateTime updateDateTime;

}
