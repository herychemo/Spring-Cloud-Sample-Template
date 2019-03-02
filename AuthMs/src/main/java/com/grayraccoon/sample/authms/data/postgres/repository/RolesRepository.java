package com.grayraccoon.sample.authms.data.postgres.repository;


import com.grayraccoon.sample.authms.data.postgres.domain.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolesRepository extends JpaRepository<Roles, String> {

    String ROLE_QUERY_USER = "ROLE_USER";
    String ROLE_QUERY_ADMIN = "ROLE_ADMIN";

    Roles findFirstByRole(String roleQuery);

}
