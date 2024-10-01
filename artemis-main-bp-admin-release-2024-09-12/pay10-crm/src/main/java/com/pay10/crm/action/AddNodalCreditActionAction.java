package com.pay10.crm.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.dao.NodalCreditDebitHistoryDao;
import com.pay10.commons.user.NodalCreditDebitHistory;
import com.pay10.commons.user.NodalReportObject;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.AddNodalCreditService;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.StatusType;

@SuppressWarnings("serial")
public class AddNodalCreditActionAction extends AbstractSecureAction {

	private static Logger logger = LoggerFactory.getLogger(AddNodalCreditActionAction.class.getName());

	private String merchantEmailId;
	private String acquirer;
	private String paymentType;
	private String captureDateRange;
	private String settlementDate;
	private String ignorePgRefList;

	private String actualNodalCredit;
	private String actualNodalDebit;
	private String nodalCreditDate;
	private String nodalDebitDate;
	private String checkboxValue;
	private String saleCount;
	private String refundCount;
	private String nodalDate;
	private String selectedDate;

	private User sessionUser = new User();
	private List<NodalReportObject> aaData;

	@Autowired
	private UserDao userDao;

	@Autowired
	private AddNodalCreditService addNodalCreditService;

	@Autowired
	private NodalCreditDebitHistoryDao nodalCreditDebitHistoryDao;

	@Override
	public String execute() {

		try {

			sessionUser = (User) sessionMap.get(Constants.USER.getValue());
			User merchant = new User();
			merchant = userDao.findPayIdByEmail(merchantEmailId);

			boolean response = addNodalCreditService.updateNodalCreditDate(merchant.getPayId(), acquirer, paymentType,
					captureDateRange, settlementDate, ignorePgRefList, nodalCreditDate, nodalDebitDate);

			NodalCreditDebitHistory nodalCreditHistory = new NodalCreditDebitHistory();
			NodalCreditDebitHistory nodalDebitHistory = new NodalCreditDebitHistory();
			SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH);

			nodalCreditHistory.setAcquirer(acquirer);
			nodalCreditHistory.setCaptureDate(captureDateRange.replace("/", "-"));
			nodalCreditHistory.setCreatedDate(new Date());

			Date nodalCreditDateFormatted = format.parse(nodalCreditDate.replace("/", "-") + " 00:00:00");
			nodalCreditHistory.setNodalDate(nodalCreditDateFormatted);
			nodalCreditHistory.setPayId(merchant.getPayId());
			nodalCreditHistory.setMerchantName(merchant.getBusinessName());
			nodalCreditHistory.setPaymentMethod(paymentType);
			nodalCreditHistory.setCreditAmount(actualNodalCredit);
			nodalCreditHistory.setSaleCount(saleCount);
			nodalCreditHistory.setStatus(StatusType.APPROVED.getName());
			nodalCreditHistory.setRequestedBy(sessionUser.getEmailId());
			nodalCreditHistory.setType("CR");

			nodalDebitHistory.setAcquirer(acquirer);
			nodalDebitHistory.setCaptureDate(captureDateRange.replace("/", "-"));
			nodalDebitHistory.setCreatedDate(new Date());
			Date nodalDebitDateFormatted = format.parse(nodalCreditDate.replace("/", "-") + " 00:00:00");
			nodalDebitHistory.setNodalDate(nodalDebitDateFormatted);
			nodalDebitHistory.setPayId(merchant.getPayId());
			nodalDebitHistory.setMerchantName(merchant.getBusinessName());
			nodalDebitHistory.setPaymentMethod(paymentType);
			nodalDebitHistory.setDebitAmount(actualNodalDebit);
			nodalDebitHistory.setRefundCount(refundCount);
			nodalDebitHistory.setStatus(StatusType.APPROVED.getName());
			nodalDebitHistory.setRequestedBy(sessionUser.getEmailId());
			nodalDebitHistory.setType("DR");

			if (response) {
				nodalCreditDebitHistoryDao.create(nodalCreditHistory);
				nodalCreditDebitHistoryDao.create(nodalDebitHistory);
				return SUCCESS;
			} else {
				return ERROR;
			}
		} catch (Exception e) {
			logger.error("Exception in updating nodal credit date");
			return ERROR;
		}

	}

	public String viewNodalAmount() {

		User user = new User();
		user = userDao.findPayIdByEmail(merchantEmailId);

		setAaData(addNodalCreditService.findNodalAmount(user.getPayId(), acquirer, paymentType, captureDateRange,
				settlementDate, ignorePgRefList));

		return SUCCESS;
	}

	public String viewNodalReport() {

		try {
			
			SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH);
			Date nodalDateFormatted = format.parse(selectedDate.replace("/", "-") + " 00:00:00");
			Date captureDateFormatted = format.parse(selectedDate.replace("/", "-") + " 00:00:00");
			
			User user = new User();
			user = userDao.findPayIdByEmail(merchantEmailId);

			/*if (checkboxValue.equalsIgnoreCase("captureDate") || checkboxValue.equalsIgnoreCase("Capture Date")) {
				
				captureDateFormatted = format.parse(selectedDate.replace("/", "-") + " 00:00:00");
			}
			else {*/
				nodalDateFormatted = format.parse(selectedDate.replace("/", "-") + " 00:00:00");
			//}
			
			List<NodalCreditDebitHistory> nodalCreditDebitHistoryList = nodalCreditDebitHistoryDao
					.findHistory(user.getPayId(), acquirer, paymentType, nodalDateFormatted);

			List<NodalReportObject> nodalReportObjectList = new ArrayList<NodalReportObject>();
			
			for (NodalCreditDebitHistory nodalCreditDebitHistory : nodalCreditDebitHistoryList ) {
				
				NodalReportObject nodalReportObject = new NodalReportObject();
				nodalReportObject.setPayId(nodalCreditDebitHistory.getMerchantName());
				nodalReportObject.setCaptureDate(nodalCreditDebitHistory.getCaptureDate());
				nodalReportObject.setAcquirer(nodalCreditDebitHistory.getAcquirer());
				nodalReportObject.setPaymentType(nodalCreditDebitHistory.getPaymentMethod());
				nodalReportObject.setType(nodalCreditDebitHistory.getType());
				nodalReportObject.setNodalDate(String.valueOf(nodalCreditDebitHistory.getNodalDate()));
				
				if (nodalReportObject.getType().equalsIgnoreCase("CR")) {
					nodalReportObject.setSaleCount(nodalCreditDebitHistory.getSaleCount());
					nodalReportObject.setSaleAmount(nodalCreditDebitHistory.getCreditAmount());
					
					nodalReportObject.setRefundCount("0");
					nodalReportObject.setRefundAmount("0");
				}
				else {
					nodalReportObject.setRefundCount(nodalCreditDebitHistory.getRefundCount());
					nodalReportObject.setRefundAmount(nodalCreditDebitHistory.getDebitAmount());
					
					nodalReportObject.setSaleCount("0");
					nodalReportObject.setSaleAmount("0");
				}
				
				nodalReportObjectList.add(nodalReportObject);
			}
			setAaData(nodalReportObjectList);

			return SUCCESS;
		}

		catch (Exception e) {
			logger.error("Error while fetching nodal transactions report");
			return ERROR;
		}

	}

	public String getActualNodalCredit() {
		return actualNodalCredit;
	}

	public void setActualNodalCredit(String actualNodalCredit) {
		this.actualNodalCredit = actualNodalCredit;
	}

	public String getActualNodalDebit() {
		return actualNodalDebit;
	}

	public void setActualNodalDebit(String actualNodalDebit) {
		this.actualNodalDebit = actualNodalDebit;
	}

	public String getNodalCreditDate() {
		return nodalCreditDate;
	}

	public void setNodalCreditDate(String nodalCreditDate) {
		this.nodalCreditDate = nodalCreditDate;
	}

	public String getNodalDebitDate() {
		return nodalDebitDate;
	}

	public void setNodalDebitDate(String nodalDebitDate) {
		this.nodalDebitDate = nodalDebitDate;
	}

	public String getSaleCount() {
		return saleCount;
	}

	public void setSaleCount(String saleCount) {
		this.saleCount = saleCount;
	}

	public String getRefundCount() {
		return refundCount;
	}

	public void setRefundCount(String refundCount) {
		this.refundCount = refundCount;
	}

	public String getMerchantEmailId() {
		return merchantEmailId;
	}

	public void setMerchantEmailId(String merchantEmailId) {
		this.merchantEmailId = merchantEmailId;
	}

	public String getCaptureDateRange() {
		return captureDateRange;
	}

	public void setCaptureDateRange(String captureDateRange) {
		this.captureDateRange = captureDateRange;
	}

	public String getSettlementDate() {
		return settlementDate;
	}

	public void setSettlementDate(String settlementDate) {
		this.settlementDate = settlementDate;
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

	public String getIgnorePgRefList() {
		return ignorePgRefList;
	}

	public void setIgnorePgRefList(String ignorePgRefList) {
		this.ignorePgRefList = ignorePgRefList;
	}

	public List<NodalReportObject> getAaData() {
		return aaData;
	}

	public void setAaData(List<NodalReportObject> aaData) {
		this.aaData = aaData;
	}

	public String getNodalDate() {
		return nodalDate;
	}

	public void setNodalDate(String nodalDate) {
		this.nodalDate = nodalDate;
	}

	public String getCheckboxValue() {
		return checkboxValue;
	}

	public void setCheckboxValue(String checkboxValue) {
		this.checkboxValue = checkboxValue;
	}

	public String getSelectedDate() {
		return selectedDate;
	}

	public void setSelectedDate(String selectedDate) {
		this.selectedDate = selectedDate;
	}

}
