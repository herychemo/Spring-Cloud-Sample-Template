package com.grayraccoon.sample.accountsms.data.postgres.repository;

import com.grayraccoon.sample.accountsms.data.postgres.domain.AccountsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AccountsRepository extends JpaRepository<AccountsEntity, UUID> {

}
