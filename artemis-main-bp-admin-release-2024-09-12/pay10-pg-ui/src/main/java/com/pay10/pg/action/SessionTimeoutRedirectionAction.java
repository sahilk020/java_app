package com.pay10.pg.action;

import java.util.Map;

import org.apache.struts2.dispatcher.SessionMap;
import org.apache.struts2.interceptor.SessionAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionTimeoutRedirectionAction extends AbstractAction implements SessionAware {

	private static final long serialVersionUID = -1151811342744023658L;
	protected SessionMap<String, Object> sessionMap;
	private static Logger logger = LoggerFactory.getLogger(SessionTimeoutRedirectionAction.class.getName());

	@Override
	public String execute() {
		try {
			//Commented to test session invalidation as session invalidated automatically by web.xml also
		//	sessionMap.invalidate();
		} catch (Exception exception) {
			logger.error("Exception occured invalidating the session" + exception);
		}
		return SUCCESS;
	}

	@Override
	public void setSession(Map<String, Object> session) {
		this.sessionMap = (SessionMap<String, Object>) session;

	}

}
