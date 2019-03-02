package com.grayraccoon.sample.authms.data.postgres.domain;


import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;

@Entity
@Table(catalog = "cloud_db", schema = "auth", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"email"})
        , @UniqueConstraint(columnNames = {"username"})
})
@NamedQueries({
        @NamedQuery(name = "Users.findAll", query = "SELECT u FROM Users u")})
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Users implements Serializable {

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
    @com.fasterxml.jackson.annotation.JsonIgnore
    private String password;


    @Column
    @CreationTimestamp
    private LocalDateTime createDateTime;

    @Column
    @UpdateTimestamp
    private LocalDateTime updateDateTime;

    @ManyToMany(mappedBy = "usersCollection")
    private Collection<Roles> rolesCollection;

    public Users() {}

    public Users(final Users user) {
        this.userId = user.userId;
        this.active = user.active;
        this.email = user.email;
        this.username = user.username;
        this.name = user.name;
        this.lastName = user.lastName;
        this.password = user.password;
        this.createDateTime = user.createDateTime;
        this.updateDateTime = user.updateDateTime;
        this.rolesCollection = user.rolesCollection;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getCreateDateTime() {
        return createDateTime;
    }

    public void setCreateDateTime(LocalDateTime createDateTime) {
        this.createDateTime = createDateTime;
    }

    public LocalDateTime getUpdateDateTime() {
        return updateDateTime;
    }

    public void setUpdateDateTime(LocalDateTime updateDateTime) {
        this.updateDateTime = updateDateTime;
    }

    public Collection<Roles> getRolesCollection() {
        return rolesCollection;
    }

    public void setRolesCollection(Collection<Roles> rolesCollection) {
        this.rolesCollection = rolesCollection;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Users users = (Users) o;

        return new EqualsBuilder()
                .append(active, users.active)
                .append(userId, users.userId)
                .append(email, users.email)
                .append(username, users.username)
                .append(name, users.name)
                .append(lastName, users.lastName)
                .append(password, users.password)
                .append(createDateTime, users.createDateTime)
                .append(updateDateTime, users.updateDateTime)
                .append(rolesCollection, users.rolesCollection)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(userId)
                .append(active)
                .append(email)
                .append(username)
                .append(name)
                .append(lastName)
                .append(password)
                .append(createDateTime)
                .append(updateDateTime)
                .append(rolesCollection)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("userId", userId)
                .append("active", active)
                .append("email", email)
                .append("username", username)
                .append("name", name)
                .append("lastName", lastName)
                .append("password", password)
                .append("createDateTime", createDateTime)
                .append("updateDateTime", updateDateTime)
                .append("rolesCollection", rolesCollection)
                .toString();
    }
}
