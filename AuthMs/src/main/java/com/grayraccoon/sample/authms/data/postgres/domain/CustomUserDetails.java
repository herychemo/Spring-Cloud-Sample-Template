package com.grayraccoon.sample.authms.data.postgres.domain;


import com.grayraccoon.sample.authms.domain.dto.RolesDto;
import com.grayraccoon.sample.authms.domain.dto.UsersDto;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CustomUserDetails extends UsersDto implements UserDetails {

    public CustomUserDetails(UsersDto u) {
        super(u);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        for (RolesDto role : getRolesCollection())
            grantedAuthorities.add(new SimpleGrantedAuthority(role.getRole()));
        return grantedAuthorities;
    }

    @Override
    public String getPassword() {
        return super.getPassword();
    }

    @Override
    public String getUsername() {
        return super.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isActive();
    }

}
