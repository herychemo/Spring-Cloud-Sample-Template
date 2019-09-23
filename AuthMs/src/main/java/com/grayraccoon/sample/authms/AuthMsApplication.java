package com.grayraccoon.sample.authms;

import com.grayraccoon.sample.authms.channels.ProducerUserEventsChannels;
import com.grayraccoon.webutils.annotations.EnableOauth2Server;
import com.grayraccoon.webutils.annotations.EnableWebUtils;
import com.grayraccoon.webutils.annotations.EnableWebUtilsDataSource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.stream.annotation.EnableBinding;

@EnableDiscoveryClient
@SpringBootApplication
@EnableCircuitBreaker
@EnableWebUtils
@EnableOauth2Server
@EnableWebUtilsDataSource
@EnableBinding(ProducerUserEventsChannels.class)
public class AuthMsApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthMsApplication.class, args);
	}

}
