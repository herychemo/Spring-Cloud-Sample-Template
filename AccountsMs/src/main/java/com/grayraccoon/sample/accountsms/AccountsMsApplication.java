package com.grayraccoon.sample.accountsms;

import com.grayraccoon.sample.accountsms.channels.ConsumerUserEventsChannels;
import com.grayraccoon.sample.authdomain.domain.Users;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
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


	// TODO: Move this consumers to a service.
	@StreamListener(ConsumerUserEventsChannels.USER_CREATED_CHANNEL)
	public void userCreatedEventConsumer(Users users) {
		final Logger LOGGER = LoggerFactory.getLogger("userCreatedEventConsumer");
		LOGGER.info("user created event received : {}", users);
	}

	@StreamListener(ConsumerUserEventsChannels.USER_UPDATED_CHANNEL)
	public void userUpdatedEventConsumer(Users users) {
		final Logger LOGGER = LoggerFactory.getLogger("userUpdatedEventConsumer");
		LOGGER.info("user updated event received : {}", users);
	}

	@StreamListener(ConsumerUserEventsChannels.USER_DELETED_CHANNEL)
	public void userDeletedEventConsumer(String usersId) {
		final Logger LOGGER = LoggerFactory.getLogger("userDeletedEventConsumer");
		LOGGER.info("user deleted event received : {}", usersId);
	}

}

