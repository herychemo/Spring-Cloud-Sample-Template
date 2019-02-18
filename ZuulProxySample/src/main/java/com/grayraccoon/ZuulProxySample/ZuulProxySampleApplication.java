package com.grayraccoon.ZuulProxySample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@EnableZuulProxy
@EnableDiscoveryClient
@SpringBootApplication
public class ZuulProxySampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZuulProxySampleApplication.class, args);
	}

}

