package com.grayraccoon.sample.accountsms;

import com.grayraccoon.sample.accountsms.channels.ConsumerUserEventsChannels;
import com.grayraccoon.webutils.annotations.EnableOauth2Server;
import com.grayraccoon.webutils.annotations.EnableWebUtils;
import com.grayraccoon.webutils.annotations.EnableWebUtilsDataSource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.stream.annotation.EnableBinding;

@EnableDiscoveryClient
@SpringBootApplication
@EnableCircuitBreaker
@EnableFeignClients
@EnableWebUtils
@EnableOauth2Server
@EnableWebUtilsDataSource
@EnableBinding(ConsumerUserEventsChannels.class)
public class AccountsMsApplication {

	public static void main(String[] args) {
		SpringApplication.run(AccountsMsApplication.class, args);
	}

}

