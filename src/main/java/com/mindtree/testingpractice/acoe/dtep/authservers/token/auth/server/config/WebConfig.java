/**
 * 
 */
package com.mindtree.testingpractice.acoe.dtep.authservers.token.auth.server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author M1030469
 *
 */
@Configuration(value = "WebConfig")
public class WebConfig extends WebMvcConfigurerAdapter {

	@Value("${dtep.api.settings.cross-origin.urls}")
	private String[] consumerUiOrigins;

	// @Bean
	// public MethodValidationPostProcessor methodValidationPostProcessor() {
	// return new MethodValidationPostProcessor();
	// }

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		 registry.addMapping("/**").allowedOrigins("*").allowCredentials(true)
//		registry.addMapping("/**")
		// GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE;
		.allowedMethods(HttpMethod.GET.name(), HttpMethod.HEAD.name(), HttpMethod.POST.name(),
				HttpMethod.PUT.name(), HttpMethod.PATCH.name(), HttpMethod.DELETE.name(),
				HttpMethod.OPTIONS.name(), HttpMethod.TRACE.name());
	}
}