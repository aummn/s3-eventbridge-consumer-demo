package com.aummn.eventbridge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class S3EventbridgeConsumerDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(S3EventbridgeConsumerDemoApplication.class, args);
	}

}
