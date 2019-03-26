package com.grayraccoon.sample.accountsms.services;

import com.grayraccoon.sample.accountsdomain.domain.Accounts;
import com.grayraccoon.sample.accountsdomain.values.Address;
import com.grayraccoon.sample.accountsms.data.postgres.domain.AccountsEntity;
import com.grayraccoon.sample.accountsms.data.postgres.embedded.EmbeddableAddress;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
class MapperConverterServiceImpl implements MapperConverterService {

    @Override
    public Accounts createAccountFromAccountsEntity(AccountsEntity accountsEntity) {
        if (accountsEntity == null) {
            return null;
        }
        return Accounts.builder()
                .accountId(accountsEntity.getAccountId())
                .fullName(accountsEntity.getFullName())
                .nickName(accountsEntity.getNickName())
                .description(accountsEntity.getDescription())
                .genre(accountsEntity.getGenre())
                .phone(accountsEntity.getPhone())
                .website(accountsEntity.getWebsite())
                .address(
                        createAddressFromEmbeddableAddress(accountsEntity.getAddress())
                )
                .createDateTime(accountsEntity.getCreateDateTime())
                .updateDateTime(accountsEntity.getUpdateDateTime())
                .build();
    }

    @Override
    public List<Accounts> createAccountsListFromAccountsEntitiesList(List<AccountsEntity> accountsEntityList) {
        return accountsEntityList.stream().map(this::createAccountFromAccountsEntity).collect(Collectors.toList());
    }

    @Override
    public AccountsEntity createAccountsEntityFromAccount(Accounts account) {
        if (account == null) {
            return null;
        }
        return AccountsEntity.builder()
                .accountId(account.getAccountId())
                .fullName(account.getFullName())
                .nickName(account.getNickName())
                .description(account.getDescription())
                .genre(account.getGenre())
                .phone(account.getPhone())
                .website(account.getWebsite())
                .address(
                        createEmbeddableAddressFromAddress(account.getAddress())
                )
                .createDateTime(account.getCreateDateTime())
                .updateDateTime(account.getUpdateDateTime())
                .build();
    }

    @Override
    public Address createAddressFromEmbeddableAddress(EmbeddableAddress embeddableAddress) {
        if (embeddableAddress == null) {
            return Address.builder().build();
        }
        return Address.builder()
                .addressLine1(embeddableAddress.getAddressLine1())
                .addressLine2(embeddableAddress.getAddressLine2())
                .city(embeddableAddress.getCity())
                .state(embeddableAddress.getState())
                .country(embeddableAddress.getCountry())
                .zipCode(embeddableAddress.getZipCode())
                .build();
    }

    @Override
    public EmbeddableAddress createEmbeddableAddressFromAddress(Address address) {
        if (address == null) {
            return EmbeddableAddress.builder().build();
        }
        return EmbeddableAddress.builder()
                .addressLine1(address.getAddressLine1())
                .addressLine2(address.getAddressLine2())
                .city(address.getCity())
                .state(address.getState())
                .country(address.getCountry())
                .zipCode(address.getZipCode())
                .build();
    }

}
