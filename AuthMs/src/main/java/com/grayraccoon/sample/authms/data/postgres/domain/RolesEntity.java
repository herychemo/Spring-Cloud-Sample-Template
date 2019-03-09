package com.grayraccoon.sample.authms.data.postgres.domain;


import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Roles", catalog = "cloud_db", schema = "auth")
@NamedQueries({
        @NamedQuery(name = "RolesEntity.findAll", query = "SELECT r FROM RolesEntity r")})
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Builder
public class RolesEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "role_id", nullable = false)
    private Integer roleId;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 90)
    @Column(nullable = false, length = 90)
    private String role;

    @JoinTable(name = "user_role", schema = "auth", joinColumns = {
            @JoinColumn(name = "role_id", referencedColumnName = "role_id", nullable = false)}, inverseJoinColumns = {
            @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)})
    @ManyToMany
    @com.fasterxml.jackson.annotation.JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<UsersEntity> usersCollection;

    public void addUser(UsersEntity user) {
        if (this.usersCollection == null) {
            this.usersCollection = new HashSet<>();
        }
        this.usersCollection.add(user);
    }

}
