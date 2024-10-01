package com.pay10.batch.commons;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolProvider {

	// int1= core pool size, int2 = max pool size, 0L(long) keeplAliveTime,time to keepAlive units, BlockingQueue, RejectedExecutionHandler
	//TODO  determine whether to use LinkedBlockingQueue or ArrayBlockingQueue
	private static int corePoolSize = 5;
	private static int maxPoolSize = 100 ;
	private static long maxThreadAliveTime = 18000 ;
	

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static ExecutorService getExecutorService(){
		ExecutorService executorService =new ThreadPoolExecutor(corePoolSize, maxPoolSize, maxThreadAliveTime, TimeUnit.MILLISECONDS, new LinkedBlockingQueue(), new ThreadPoolExecutor.CallerRunsPolicy());
		return executorService;
	}
}
