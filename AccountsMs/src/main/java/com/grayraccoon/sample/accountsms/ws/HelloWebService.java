package com.grayraccoon.sample.accountsms.ws;

import com.grayraccoon.webutils.dto.GenericDto;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.apache.commons.lang.math.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.grayraccoon.sample.accountsms.config.ResourceServerConfig.getExtraInfo;

@RestController
@RequestMapping("/hello")
public class HelloWebService {

	private static final Logger LOGGER = LoggerFactory.getLogger(HelloWebService.class.getName());

	@HystrixCommand(fallbackMethod = "getHelloFallback", commandKey = "helloWorld", groupKey = "helloWorld")
	@GetMapping()
	public GenericDto<String> getHello() {
		if (RandomUtils.nextBoolean() && RandomUtils.nextBoolean() && RandomUtils.nextBoolean()) {
			throw new RuntimeException();
		}
		return GenericDto.<String>builder().data("Hello From MicroService").build();
	}

	public GenericDto<String> getHelloFallback() {
		return GenericDto.<String>builder().data("fallback hello").build();
	}


	@GetMapping("/secured")
	public String getSecuredHello(Authentication authentication) {
		Map<String, Object> extraInfo = getExtraInfo(authentication);
		String userId = (String) extraInfo.get("userId");

		return String.format("Hello %s", userId);
	}

}
