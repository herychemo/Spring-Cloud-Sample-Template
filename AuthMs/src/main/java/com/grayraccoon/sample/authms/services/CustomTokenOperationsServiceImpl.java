package com.grayraccoon.sample.authms.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.approval.Approval;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
public class CustomTokenOperationsServiceImpl implements CustomTokenOperationsService {

    TokenStore tokenStore;

    @Autowired
    ApprovalStore approvalStore;

    @Autowired
    DefaultTokenServices tokenService;

    @Autowired
    public void setTokenStore(TokenStore tokenStore) {
        this.tokenStore = tokenStore;
    }


    @Override
    public void revokeAllAccessTokensByUsernameList(String... userNames) {

        if (userNames == null) {
            return;
        }

        if (!(tokenStore instanceof JdbcTokenStore) ) {
            return;
        }

        final List<Approval> allUserApprovals = new ArrayList<>();
        String clientId = null;

        for (String username: userNames) {
            Collection<OAuth2AccessToken> accessTokens = ((JdbcTokenStore)tokenStore).findTokensByUserName(username);

            for (OAuth2AccessToken oAuth2AccessToken: accessTokens) {

                if (clientId == null) {
                    Map<String, Object> extraInfo = oAuth2AccessToken.getAdditionalInformation();
                    clientId = (String) extraInfo.get("clientId");
                }

                //Revoke and remove access token and refresh token
                tokenService.revokeToken(oAuth2AccessToken.getValue());
            }

            if (clientId == null) {
                continue;
            }

            allUserApprovals.addAll(
                    approvalStore.getApprovals(username, clientId)
            );
        }

        if (allUserApprovals.isEmpty()) {
            return;
        }

        //Revoke all approvals
        approvalStore.revokeApprovals(allUserApprovals);
    }

}
