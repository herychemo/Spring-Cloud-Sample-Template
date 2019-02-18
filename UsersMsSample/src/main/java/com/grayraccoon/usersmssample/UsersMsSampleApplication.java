package com.grayraccoon.usersmssample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@EnableDiscoveryClient
@SpringBootApplication
@EnableCircuitBreaker
@ComponentScan(basePackages = {"com.grayraccoon"})	// Load WebUtils
public class UsersMsSampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(UsersMsSampleApplication.class, args);
	}

}

