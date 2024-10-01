package com.pay10.scheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan({ "com.pay10.commons", "com.pay10.scheduler" })
@EnableScheduling
public class AsiancheckoutSchedulerApplication {

	public static void main(String[] args) {
		SpringApplication.run(AsiancheckoutSchedulerApplication.class, args);
	}
}