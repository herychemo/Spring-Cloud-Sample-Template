package com.grayraccoon.sample.accountsms.data.postgres.domain;

import com.grayraccoon.sample.accountsdomain.values.Genre;
import com.grayraccoon.sample.accountsms.data.postgres.embedded.EmbeddableAddress;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "Accounts")
@NamedQueries({
        @NamedQuery(name = "AccountsEntity.findAll", query = "SELECT u FROM AccountsEntity u")})
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Builder
public class AccountsEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @org.hibernate.annotations.Type(type = "org.hibernate.type.UUIDCharType")
    @Column(name = "account_id", nullable = false, updatable = false, length = 42)
    private UUID accountId;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 90)
    @Column(name = "full_name", nullable = false, length = 90)
    private String fullName;

    @Size(min = 0, max = 90)
    @Column(name = "nick_name", nullable = true, length = 90)
    private String nickName;

    @Size(min = 0, max = 512)
    @Column(nullable = true, length = 512)
    private String description;

    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    private Genre genre;

    @Size(min = 0, max = 16)
    @Column(nullable = true, length = 16)
    private String phone;

    @Size(min = 0, max = 256)
    @Column(nullable = true, length = 256)
    private String website;

    @Embedded
    @AttributeOverrides(value = {
            @AttributeOverride(name = "addressLine1", column = @Column(name = "address_line_1")),
            @AttributeOverride(name = "addressLine2", column = @Column(name = "address_line_2")),
            @AttributeOverride(name = "city", column = @Column(name = "address_city")),
            @AttributeOverride(name = "zipCode", column = @Column(name = "address_zip_code")),
            @AttributeOverride(name = "state", column = @Column(name = "address_state")),
            @AttributeOverride(name = "country", column = @Column(name = "address_country"))
    })
    private EmbeddableAddress address;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createDateTime;

    @Column
    @UpdateTimestamp
    private LocalDateTime updateDateTime;

}
