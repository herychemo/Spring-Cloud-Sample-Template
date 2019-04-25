package com.grayraccoon.sample.accountsms;

import com.grayraccoon.sample.accountsms.channels.ConsumerUserEventsChannels;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.ComponentScan;

@EnableDiscoveryClient
@SpringBootApplication
@EnableCircuitBreaker
@EnableFeignClients
@ComponentScan(basePackages = {"com.grayraccoon"})	// Load WebUtils
@EnableBinding(ConsumerUserEventsChannels.class)
public class AccountsMsApplication {

	public static void main(String[] args) {
		SpringApplication.run(AccountsMsApplication.class, args);
	}

}

