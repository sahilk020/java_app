package com.pay10.crm.action;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.util.OldBhartipayStatus;
import com.pay10.crm.mongoReports.OldBhartipayReportData;


public class OldBhartipayReport extends AbstractSecureAction {

	private static final long serialVersionUID = -3306411281051464691L;
	private static Logger logger = LoggerFactory.getLogger(OldBhartipayReport.class.getName());

	private List<OldBhartipayStatus>status=new ArrayList<>();
	
	@Autowired
	private OldBhartipayReportData bhartipayReportData;
	
	@Override
	public String execute() {
		logger.info("OldBhartipay Report Executed");
		setStatus(bhartipayReportData.getStatusFromOldBhartpay());
		return SUCCESS;
	}

	public List<OldBhartipayStatus> getStatus() {
		return status;
	}

	public void setStatus(List<OldBhartipayStatus> status) {
		this.status = status;
	}

	
	

}
