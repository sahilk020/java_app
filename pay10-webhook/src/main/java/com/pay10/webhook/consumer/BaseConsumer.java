package com.pay10.webhook.consumer;

public abstract class BaseConsumer {
	
	
	public void start()
	{
		throw new RuntimeException("Stop Method not implemented");
	}
	
	public void stop() 
	{
		throw new RuntimeException("Stop Method not implemented");
	}
	
	
	
	
}
