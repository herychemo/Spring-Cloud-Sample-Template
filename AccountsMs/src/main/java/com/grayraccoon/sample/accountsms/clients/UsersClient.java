package com.grayraccoon.sample.accountsms.clients;

import com.grayraccoon.sample.authdomain.domain.Users;
import com.grayraccoon.webutils.dto.GenericDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("auth-ms")
public interface UsersClient {

    String AUTH_MS_API_PREFIX = "/auth-ms/ws";

    @GetMapping(AUTH_MS_API_PREFIX + "/authenticated/users/me")
    GenericDto<Users> findMeUser();

    @GetMapping(AUTH_MS_API_PREFIX + "/secured/users/{userId}")
    GenericDto<Users> findMeUser(@PathVariable("userId") String userId);

}
