package com.pay10.crm.action;

import java.util.List;

import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.action.AbstractSecureAction;
import com.pay10.commons.user.RefundSummary;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.DateCreater;
import com.pay10.crm.mongoReports.RefundSummaryReportService;

/**
 * @author Vijaya
 *
 */

public class RefunSummaryReportAction extends AbstractSecureAction {

	private static final long serialVersionUID = -4269892349842953338L;
	
	private static Logger logger = LoggerFactory.getLogger(RefunSummaryReportAction.class.getName());
	
	private String refundRequestDate;
	private String merchant;
	private String mode;
	private String acquirer;
	private String paymentType;
	private List<RefundSummary> aaData;
	
	@Autowired
	RefundSummaryReportService refundSummaryReportService;
	
	@Autowired
	UserDao userDao;
	
	@Override
	public String execute() {
		logger.info("Inside RefunSummaryReportAction Class, In execute method !!");
		String merchantPayId = "";
		if(!getMerchant().equals("ALL")) {
			merchantPayId = userDao.getPayIdByEmailId(getMerchant());
		} else {
			merchantPayId = getMerchant();
		}
		setRefundRequestDate(DateCreater.toDateTimeformatCreater(refundRequestDate));
		setAaData(refundSummaryReportService.getData(merchantPayId, refundRequestDate, acquirer, paymentType, mode));
		return SUCCESS;
	}

	public String getRefundRequestDate() {
		return refundRequestDate;
	}

	public void setRefundRequestDate(String refundRequestDate) {
		this.refundRequestDate = refundRequestDate;
	}

	public String getMerchant() {
		return merchant;
	}

	public void setMerchant(String merchant) {
		this.merchant = merchant;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getAcquirer() {
		return acquirer;
	}

	public void setAcquirer(String acquirer) {
		this.acquirer = acquirer;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public List<RefundSummary> getAaData() {
		return aaData;
	}

	public void setAaData(List<RefundSummary> aaData) {
		this.aaData = aaData;
	}
	

}
