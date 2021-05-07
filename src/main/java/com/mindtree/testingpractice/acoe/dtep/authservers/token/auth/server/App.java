package com.mindtree.testingpractice.acoe.dtep.authservers.token.auth.server;

import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

@SpringBootApplication
@EntityScan(basePackageClasses = { App.class, Jsr310JpaConverters.class })
public class App {

	@PostConstruct
	void init() {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	public static void main(String[] args) {
		System.setProperty("dtep.homeDirectory", ".dtep");
		System.setProperty("dtep.workDirectory", "work");
		System.setProperty("dtep.logsDirectory", "logs");
		System.setProperty("dtep.home", String.join(System.getProperty("file.separator"),
				System.getProperty("user.home"), System.getProperty("dtep.homeDirectory")));
		System.setProperty("dtep.work.home", String.join(System.getProperty("file.separator"),
				System.getProperty("dtep.home"), System.getProperty("dtep.workDirectory")));
		System.setProperty("dtep.logs.home", String.join(System.getProperty("file.separator"),
				System.getProperty("dtep.home"), System.getProperty("dtep.logsDirectory")));
		SpringApplication.run(App.class, args);
	}
}