package com.pay10.crm.action;




import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MasterReport extends AbstractSecureAction {

	private static final long serialVersionUID = -3306411281051464691L;
	private static Logger logger = LoggerFactory.getLogger(MasterReport.class.getName());
	
	
	@Override
	public String execute() {
		logger.info("MasterReport Page Executed");
		return SUCCESS;
	}	
	
}
