package com.pay10.crypto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
@ComponentScan("com.pay10")
@ServletComponentScan("com.pay10")
public class Pay10CryptoApplication extends SpringBootServletInitializer{

	public static void main(String[] args) {
		SpringApplication.run(Pay10CryptoApplication.class, args);
	}
	
	 @Override
	  protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
	      return builder.sources(Pay10CryptoApplication.class);
	  }
}
