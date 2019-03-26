package com.grayraccoon.sample.accountsms.data.postgres.embedded;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Builder
public class EmbeddableAddress implements Serializable {

    @Size(min = 0, max = 44)
    @Column(nullable = false, length = 44)
    private String addressLine1;

    @Size(min = 0, max = 28)
    @Column(nullable = false, length = 28)
    private String addressLine2;

    @Size(min = 0, max = 34)
    @Column(nullable = false, length = 34)
    private String city;

    @Size(min = 0, max = 10)
    @Column(nullable = false, length = 10)
    private String zipCode;

    @Size(min = 0, max = 34)
    @Column(nullable = false, length = 34)
    private String state;

    @Size(min = 0, max = 34)
    @Column(nullable = false, length = 34)
    private String country;

}
