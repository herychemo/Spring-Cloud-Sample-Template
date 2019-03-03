package com.grayraccoon.sample.authms.data.postgres.repository;

import com.grayraccoon.sample.authms.data.postgres.domain.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UsersRepository extends JpaRepository<UsersEntity, UUID> {

    UsersEntity findByUsername(String username);

    UsersEntity findByEmail(String email);

    UsersEntity findByUserId(UUID userId);

    UsersEntity findFirstByEmailOrUsername(String email, String username);

}
