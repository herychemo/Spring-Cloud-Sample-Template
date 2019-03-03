package com.grayraccoon.sample.authms.services;

import com.grayraccoon.sample.authms.data.postgres.domain.CustomUserDetails;
import com.grayraccoon.sample.authms.data.postgres.domain.Users;
import com.grayraccoon.sample.authms.data.postgres.repository.UsersRepository;
import com.grayraccoon.sample.authms.domain.dto.UsersDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private MapperConverterService mapperConverterService;

    @Autowired
    private UsersRepository usersRepository;


    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Users user = usersRepository.findFirstByEmailOrUsername(s, s);
        if (user == null) {
            throw new UsernameNotFoundException("Username or Email Not Found");
        }
        UsersDto usersDto = mapperConverterService.createUsersDtoFromUser(user);
        return new CustomUserDetails(usersDto);
    }

}
