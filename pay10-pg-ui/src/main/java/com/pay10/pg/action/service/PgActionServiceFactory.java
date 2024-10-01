package com.pay10.pg.action.service;

public class PgActionServiceFactory {

	public static ActionService getActionService(){
		return new ActionServiceImpl();
	}
}
