package com.mindtree.testingpractice.acoe.dtep.authservers.token.auth.server.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration(value = "RestConfig")
public class RestConfig {

	@LoadBalanced
	@Bean(name = { "MicroservicesRestTemplate" })
	public RestTemplate microservicesRestTemplate() {
		return new RestTemplate();
	}
}