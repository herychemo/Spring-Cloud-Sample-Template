package com.grayraccoon.sample.authms.data.postgres.domain;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Collection;

@Entity
@Table(catalog = "cloud_db", schema = "auth")
@NamedQueries({
        @NamedQuery(name = "Roles.findAll", query = "SELECT r FROM Roles r")})
public class Roles implements Serializable {

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
    private Collection<Users> usersCollection;

    public Roles() {}
    public Roles(@NotNull Integer roleId) {
        this.roleId = roleId;
    }
    public Roles(@NotNull Integer roleId, @NotNull @Size(min = 1, max = 90) String role) {
        this.roleId = roleId;
        this.role = role;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Collection<Users> getUsersCollection() {
        return usersCollection;
    }

    public void setUsersCollection(Collection<Users> usersCollection) {
        this.usersCollection = usersCollection;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Roles roles = (Roles) o;

        return new EqualsBuilder()
                .append(roleId, roles.roleId)
                .append(role, roles.role)
                .append(usersCollection, roles.usersCollection)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(roleId)
                .append(role)
                .append(usersCollection)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("roleId", roleId)
                .append("role", role)
                .append("usersCollection", usersCollection)
                .toString();
    }
}
