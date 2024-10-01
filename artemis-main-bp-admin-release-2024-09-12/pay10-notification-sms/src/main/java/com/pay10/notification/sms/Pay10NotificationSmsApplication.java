package com.pay10.notification.sms;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.StaticDataProvider;



@SpringBootApplication
@ComponentScan({ "com.pay10.commons", "com.pay10.notification.sms" })
public class Pay10NotificationSmsApplication extends SpringBootServletInitializer {
	public static void main(String[] args) {
		
		UserDao userDao = new UserDao();
		userDao.getUserActiveList();
		
		ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
		executorService.scheduleAtFixedRate(StaticDataProvider::updateMapValues, 0, 300, TimeUnit.SECONDS);
		
		SpringApplication.run(Pay10NotificationSmsApplication.class, args);
	}
	
	 @Override
	  protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
	      return builder.sources(Pay10NotificationSmsApplication.class);
	  }
}