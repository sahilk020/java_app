package com.pay10.webhook.handler;


import org.apache.commons.lang3.NotImplementedException;


public abstract class BaseEventHandler<T> {
	
	
	public boolean validateEvent(T event)
	{
		throw new NotImplementedException("Validate Event Method Not Implemented");
	}
	
	public void saveEventData(T event) throws Exception
	{
		throw new NotImplementedException("Save Event Data Method Not Implemented");
	}
	
	

}
