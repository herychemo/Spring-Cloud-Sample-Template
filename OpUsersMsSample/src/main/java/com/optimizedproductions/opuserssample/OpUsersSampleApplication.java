package com.optimizedproductions.opuserssample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
@EnableCircuitBreaker
public class OpUsersSampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(OpUsersSampleApplication.class, args);
	}

}

