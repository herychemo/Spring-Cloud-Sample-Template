package com.grayraccoon.oauth2mssample.data.postgres.repository;

import com.grayraccoon.oauth2mssample.data.postgres.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UsersRepository extends JpaRepository<Users, UUID> {

    Users findByUsername(String username);

    Users findByEmail(String email);

    Users findByUserId(UUID userId);

}
