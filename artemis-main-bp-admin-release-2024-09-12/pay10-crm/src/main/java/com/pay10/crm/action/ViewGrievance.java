package com.pay10.crm.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.dto.GRS;
import com.pay10.commons.dto.GrsIssueHistoryDto;
import com.pay10.commons.repository.GrievanceRedressalSystemRepository;
import com.pay10.crm.mongoReports.TxnReportForGrs;




public class ViewGrievance extends AbstractSecureAction{
	private static final long serialVersionUID = -633674343079079514L;
	private static Logger logger = LoggerFactory.getLogger(ViewGrievance.class.getName());
	HttpServletRequest request = ServletActionContext.getRequest();
	
	@Autowired
	private TxnReportForGrs txnReportForGrs;
	
	private String grsId;
	
	private String payId;
	
	public GRS grs=new GRS();
	
	public GrsIssueHistoryDto issueHistoryDto = new GrsIssueHistoryDto();
	
	@Override
	public String execute() {
		logger.info("Inside ViewGrievance in execute() payId...={}",payId);
		setGrs(txnReportForGrs.getGrsFromGrsID(grsId.trim()));
		setIssueHistoryDto(txnReportForGrs.getGrsIssueHistoryFromGrsID(grsId.trim()));
		return SUCCESS;
	}

	

	public String getGrsId() {
		return grsId;
	}



	public void setGrsId(String grsId) {
		this.grsId = grsId;
	}



	public GRS getGrs() {
		return grs;
	}

	public void setGrs(GRS grs) {
		this.grs = grs;
	}



	public String getPayId() {
		return payId;
	}



	public void setPayId(String payId) {
		this.payId = payId;
	}



	public GrsIssueHistoryDto getIssueHistoryDto() {
		return issueHistoryDto;
	}



	public void setIssueHistoryDto(GrsIssueHistoryDto issueHistoryDto) {
		this.issueHistoryDto = issueHistoryDto;
	}




	
	
}


