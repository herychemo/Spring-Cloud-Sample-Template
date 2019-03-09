package com.grayraccoon.sample.authms.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomTokenOperationsServiceImpl.class);

    private TokenStore tokenStore;

    @Autowired
    private ApprovalStore approvalStore;

    @Autowired
    private DefaultTokenServices tokenService;

    @Autowired
    public void setTokenStore(TokenStore tokenStore) {
        this.tokenStore = tokenStore;
    }


    @Override
    public void revokeAllAccessTokensByUsernameList(String... userNames) {

        if (userNames == null) {
            return;
        }

        LOGGER.info("revokeAllAccessTokensByUsernameList(): {}", (Object[]) userNames);

        if (!(tokenStore instanceof JdbcTokenStore) ) {
            return;
        }

        final List<Approval> allUserApprovals = new ArrayList<>();
        String clientId = null;

        for (String username: userNames) {
            Collection<OAuth2AccessToken> accessTokens = ((JdbcTokenStore)tokenStore).findTokensByUserName(username);

            LOGGER.info("Found {} access tokens for: {}", accessTokens.size(), username);

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
            LOGGER.info("No approvals found.");
            return;
        }

        LOGGER.info("Found {} approvals.", allUserApprovals.size());

        //Revoke all approvals
        approvalStore.revokeApprovals(allUserApprovals);
    }

}
