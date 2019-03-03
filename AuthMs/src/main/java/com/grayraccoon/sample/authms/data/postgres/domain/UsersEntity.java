package com.grayraccoon.sample.authms.data.postgres.domain;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "Users", catalog = "cloud_db", schema = "auth", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"email"})
        , @UniqueConstraint(columnNames = {"username"})
})
@NamedQueries({
        @NamedQuery(name = "UsersEntity.findAll", query = "SELECT u FROM UsersEntity u")})
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Builder
public class UsersEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @org.hibernate.annotations.Type(type = "org.hibernate.type.UUIDCharType")
    @Column(name = "user_id", nullable = false, updatable = false, length = 42)
    private UUID userId;


    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private boolean active;

    @javax.validation.constraints.Pattern(regexp = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message = "Invalid email")
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 90)
    @Column(nullable = false, length = 90)
    private String email;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(nullable = false, length = 50)
    private String username;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 90)
    @Column(nullable = false, length = 90)
    private String name;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 90)
    @Column(name = "last_name", nullable = false, length = 90)
    private String lastName;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 60)
    @Column(nullable = false, length = 60)
    @ToString.Exclude
    private String password;


    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createDateTime;

    @Column
    @UpdateTimestamp
    private LocalDateTime updateDateTime;

    @ManyToMany(mappedBy = "usersCollection")
    private Set<RolesEntity> rolesCollection;

}
