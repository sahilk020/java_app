package com.pay10.pg.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"com.pay10.commons"})
public class Pay10PgCoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(Pay10PgCoreApplication.class, args);
	}
}
