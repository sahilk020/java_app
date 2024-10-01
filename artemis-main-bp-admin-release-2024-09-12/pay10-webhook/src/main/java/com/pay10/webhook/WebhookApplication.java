package com.pay10.webhook;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = {MongoAutoConfiguration.class,MongoRepositoriesAutoConfiguration .class,MongoDataAutoConfiguration.class})
@EnableScheduling
@ComponentScan({"com.pay10.commons", "com.pay10.webhook","com.pay10"})
/*@EnableJpaRepositories(basePackages = {"com.pay10.webhook.repository"})*/
//@ServletComponentScan("com.pay10")
public class WebhookApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebhookApplication.class, args);
		
	}
	
	

}
