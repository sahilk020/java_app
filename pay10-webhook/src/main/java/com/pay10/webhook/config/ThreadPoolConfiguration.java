package com.pay10.webhook.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class ThreadPoolConfiguration implements AsyncConfigurer{
	
	private static final int MAX_POOL_SIZE = 100;
	private static final int CORE_POOL_SIZE = 75;
	private static final int QUEUE_CAPACITY = 75;
	
	
	@Override
	public Executor getAsyncExecutor() {
		// TODO Auto-generated method stub
		ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
		threadPoolTaskExecutor.setCorePoolSize(CORE_POOL_SIZE);
		threadPoolTaskExecutor.setMaxPoolSize(MAX_POOL_SIZE);
		threadPoolTaskExecutor.setQueueCapacity(QUEUE_CAPACITY);
		//threadPoolTaskExecutor.setAllowCoreThreadTimeOut(true);
		//threadPoolTaskExecutor.setKeepAliveSeconds(120);
		threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		threadPoolTaskExecutor.setThreadNamePrefix("Webhook-Executor");
		threadPoolTaskExecutor.initialize();
		return threadPoolTaskExecutor;
	}
	
	

}
