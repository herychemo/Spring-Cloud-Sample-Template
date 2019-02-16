package com.optimizedproductions.opuserssample.controllers;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.apache.commons.lang.math.RandomUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello")
public class HelloController {


	@HystrixCommand(fallbackMethod = "getHelloFallback", commandKey = "helloWorld", groupKey = "helloWorld")
	@GetMapping()
	public String getHello() {
		if (RandomUtils.nextBoolean() && RandomUtils.nextBoolean() && RandomUtils.nextBoolean()) {
			throw new RuntimeException();
		}
		return "Hello From MicroService";
	}


	public String getHelloFallback() {
		return "fallback hello";
	}

}
