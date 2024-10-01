package com.pay10.bindb;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StaticDataProvider;

@SpringBootApplication
@ComponentScan("com.pay10")
@ServletComponentScan("com.pay10")
public class Pay10BindbApplication extends SpringBootServletInitializer implements CommandLineRunner{

	
	private static Logger logger = LoggerFactory.getLogger(Pay10BindbApplication.class.getName());

	{
		logger.info("static block called upon invocation");
		UserDao userDao = new UserDao();
		userDao.getUserActiveList();
		
		ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
		executorService.scheduleAtFixedRate(StaticDataProvider::updateMapValues, 0, 300, TimeUnit.SECONDS);
	}
	
	public static void main(String[] args) {
		SpringApplication.run(Pay10BindbApplication.class, args);
	}
	
	 @Override
	  protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
	      return builder.sources(Pay10BindbApplication.class);
	  }

	@Override
	public void run(String... args) throws Exception {
		PropertiesManager propertiesManager = new PropertiesManager();
		
	}
}
