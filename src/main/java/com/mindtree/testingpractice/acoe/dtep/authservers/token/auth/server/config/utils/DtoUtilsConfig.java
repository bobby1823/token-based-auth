package com.mindtree.testingpractice.acoe.dtep.authservers.token.auth.server.config.utils;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(value = "DtoUtilsConfig")
public class DtoUtilsConfig {

	@Bean(name = {"ModelMapper"})
	public ModelMapper modelMapper() {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setAmbiguityIgnored(true);
		return modelMapper;
	}
}