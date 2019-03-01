package com.grayraccoon.oauth2mssample.services;

import com.grayraccoon.oauth2mssample.data.postgres.domain.CustomUserDetails;
import com.grayraccoon.oauth2mssample.data.postgres.domain.Users;
import com.grayraccoon.webutils.exceptions.CustomApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Users user;
        try {
            user = userService.findByUsernameOrEmail(s);
        } catch (CustomApiException ex) {
            if (ex.getApiError().getStatus().equals(HttpStatus.NOT_FOUND)) {
                throw new UsernameNotFoundException("Username or Email Not Found");
            }else {
                throw ex;
            }
        }

        CustomUserDetails userDetails = new CustomUserDetails(user);
        userDetails.setRolesCollection(
                userService.getRolesFor(userDetails.getUserId())
        );
        return userDetails;
    }

}
