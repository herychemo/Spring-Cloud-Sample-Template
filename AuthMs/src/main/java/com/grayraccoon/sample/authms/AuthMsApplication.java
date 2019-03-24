package com.grayraccoon.sample.authms;

import com.grayraccoon.sample.authms.channels.ProducerUserEventsChannels;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableDiscoveryClient
@SpringBootApplication
@EnableCircuitBreaker
@ComponentScan(basePackages = {"com.grayraccoon"})	// Load WebUtils
@EnableBinding(ProducerUserEventsChannels.class)
@EnableAsync
public class AuthMsApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthMsApplication.class, args);
	}

}
