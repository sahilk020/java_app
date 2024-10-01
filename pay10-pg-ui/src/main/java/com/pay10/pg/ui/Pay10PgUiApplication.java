package com.pay10.pg.ui;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.StaticDataProvider;

@SpringBootApplication
@ComponentScan({"com.pay10.commons","com.pay10.pg.core","com.pay10.pg.ws","com.pay10.pg.ui","com.pay10.notification.email"})
public class Pay10PgUiApplication{
	
	{
		UserDao userDao = new UserDao();
		userDao.getUserActiveList();
		
		ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
		executorService.scheduleAtFixedRate(StaticDataProvider::updateMapValues, 0, 300, TimeUnit.SECONDS);
	}
	
	public static void main(String[] args) {
		SpringApplication.run(Pay10PgUiApplication.class, args);
	}
}
