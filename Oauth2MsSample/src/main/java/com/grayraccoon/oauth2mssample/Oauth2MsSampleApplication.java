package com.grayraccoon.oauth2mssample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@EnableDiscoveryClient
@SpringBootApplication
@EnableCircuitBreaker
@ComponentScan(basePackages = {"com.grayraccoon"})	// Load WebUtils
public class Oauth2MsSampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(Oauth2MsSampleApplication.class, args);
	}

}
