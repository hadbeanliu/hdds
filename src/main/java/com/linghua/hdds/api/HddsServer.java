package com.linghua.hdds.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HddsServer {

	public static void main(String[] args) {
		
		SpringApplication.run(HddsServer.class, args);

	}

}
