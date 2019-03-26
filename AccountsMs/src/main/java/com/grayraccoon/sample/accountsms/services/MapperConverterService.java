package com.grayraccoon.sample.accountsms.services;

import com.grayraccoon.sample.accountsdomain.domain.Accounts;
import com.grayraccoon.sample.accountsdomain.values.Address;
import com.grayraccoon.sample.accountsms.data.postgres.domain.AccountsEntity;
import com.grayraccoon.sample.accountsms.data.postgres.embedded.EmbeddableAddress;

import java.util.List;

interface MapperConverterService {
    
    Accounts createAccountFromAccountsEntity(AccountsEntity account);
    List<Accounts> createAccountsListFromAccountsEntitiesList(List<AccountsEntity> accountsEntityList);

    AccountsEntity createAccountsEntityFromAccount(Accounts account);

    Address createAddressFromEmbeddableAddress(EmbeddableAddress embeddableAddress);
    EmbeddableAddress createEmbeddableAddressFromAddress(Address address);

}
