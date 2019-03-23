package com.grayraccoon.sample.uigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class UiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(UiGatewayApplication.class, args);
    }

}
