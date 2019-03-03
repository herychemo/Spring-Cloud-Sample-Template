package com.grayraccoon.sample.authms.data.postgres.repository;

import com.grayraccoon.sample.authms.data.postgres.domain.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UsersRepository extends JpaRepository<UsersEntity, UUID> {

    UsersEntity findByUsername(String username);

    UsersEntity findByEmail(String email);

    UsersEntity findByUserId(UUID userId);

    UsersEntity findFirstByEmailOrUsername(String email, String username);

    @Query(value = "SELECT CASE WHEN COUNT(user) = 0 THEN true ELSE false END " +
            "FROM UsersEntity user WHERE user.email = :email AND (:userId IS NULL OR user.userId <> :userId)")
    boolean isValidEmailCombination(String email, UUID userId);

    @Query(value = "SELECT CASE WHEN COUNT(user) = 0 THEN true ELSE false END " +
            "FROM UsersEntity user WHERE user.username = :username AND (:userId IS NULL OR user.userId <> :userId)")
    boolean isValidUsernameCombination(String username, UUID userId);

}
