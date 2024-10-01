package com.pay10.crm.action;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.api.Hasher;
import com.pay10.commons.api.TransactionControllerServiceProvider;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.Merchants;
import com.pay10.commons.user.RefundRejection;
import com.pay10.commons.user.User;
import com.pay10.commons.util.Amount;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.DateCreater;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.TransactionManager;
import com.pay10.commons.util.TransactionType;
import com.pay10.crm.mongoReports.RefundRejectioReportService;

public class RefundRejectionReportAction extends AbstractSecureAction {

	private static final long serialVersionUID = 149102749485831816L;

	private static Logger logger = LoggerFactory.getLogger(RefundRejectionReportAction.class.getName());

	private List<Merchants> merchantList = new LinkedList<Merchants>();
	private String orderId;
	private String merchant;
	private String paymentType;
	private String dateFrom;
	private String dateTo;
	private String acquirer;
	private String response;
	
	private String refundFlag;
	private String refundAmount;
	private String pgRefNum;
	private String refundDate;
	private String totalAmount;
	private String refundOrderId;
	private String currencyCode;
	
	private int count = 1;
	private User sessionUser = new User();
	List<RefundRejection> aaData = new ArrayList<RefundRejection>();

	@Autowired
	private CrmValidator validator;
	
	@Autowired
	private TransactionControllerServiceProvider transactionControllerServiceProvider;
	
	@Autowired
	private RefundRejectioReportService refundRejectioReportService;

	@Override
	public String execute() {
		try {
			sessionUser = (User) sessionMap.get(Constants.USER.getValue());
			String segment = sessionMap.get(Constants.SEGMENT.getValue()).toString();
			setDateFrom(DateCreater.toDateTimeformatCreater(dateFrom));
			setDateTo(DateCreater.formDateTimeformatCreater(dateTo));
			setAaData(refundRejectioReportService.searchRejectedRefund(getMerchant(), getOrderId(), getRefundOrderId(), getPaymentType(), getAcquirer(), dateFrom, dateTo, segment, sessionUser.getRole().getId()));
			return SUCCESS;

		} catch (Exception exception) {
			logger.error("Exception in RefundRejectionReportAction Class, execute method ", exception);
		}

		return SUCCESS;
	}

	public String refund() {
		try {
			logger.error("RefundRejectionReportAction Class, refund method !!");
			//String hostUrl = PropertiesManager.propertiesMap.get("RefundURL");
			logger.info("Order Id : " + getOrderId());
			logger.info("Refund Flag : " + getRefundFlag());
			logger.info("Refund Amount : " + getRefundAmount());
			logger.info("Currency Code : " + getCurrencyCode());
			logger.info("PG_REF_NUM : " + getPgRefNum());
			logger.info("Refund Order Id : " + getRefundOrderId());
			logger.info("Merchant : " + getMerchant());
			
			Fields fields = new Fields();
			fields.put(FieldType.ORDER_ID.getName(),getOrderId());
			fields.put(FieldType.REFUND_FLAG.getName(),getRefundFlag());
			fields.put(FieldType.AMOUNT.getName(),(Amount.formatAmount(getRefundAmount(), getCurrencyCode())));
			fields.put(FieldType.PG_REF_NUM.getName(),getPgRefNum());
			fields.put(FieldType.REFUND_ORDER_ID.getName(),getRefundOrderId());
			fields.put(FieldType.CURRENCY_CODE.getName(),getCurrencyCode());
			fields.put(FieldType.TXNTYPE.getName(),TransactionType.REFUND.getName());
			fields.put(FieldType.PAY_ID.getName(),getMerchant());
			fields.put(FieldType.REQUEST_DATE.getName(),getRefundDate());
			fields.put(FieldType.HASH.getName(),Hasher.getHash(TransactionManager.getNewTransactionId()));
			logger.info("refund request : " + fields.getFieldsAsString());
			
			Map<String, String> response = transactionControllerServiceProvider.transact(fields, "RefundURL");
			logger.info("refund API  response received from pg ws " + response);
			if(response.isEmpty()) {
				setResponse("Refund not initiated !!");
			} else {
				setResponse("Refund Initiated Successfully !!, Response Status is :" + response.get(FieldType.STATUS.getName()));
			}
			
		} catch (JSONException exception) {			
			logger.error("Exception in RefundRejectionReportAction Class, refund function ", exception);
		} catch (SystemException exception) {
			logger.error("Exception in RefundRejectionReportAction Class, refund method ", exception);
		}
		return SUCCESS;
	}
	
	/*public void validate() {
		if (validator.validateBlankField(getOrderId())) {
		} else if (!validator.validateField(CrmFieldType.ORDER_ID, getOrderId())) {
			addFieldError(CrmFieldType.ORDER_ID.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
		}

		if (validator.validateBlankField(getDateFrom())) {
		} else if (!validator.validateField(CrmFieldType.DATE_FROM, getDateFrom())) {
			addFieldError(CrmFieldType.DATE_FROM.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
		}

		if (validator.validateBlankField(getDateTo())) {
		} else if (!validator.validateField(CrmFieldType.DATE_TO, getDateTo())) {
			addFieldError(CrmFieldType.DATE_TO.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
		}

		if (!validator.validateBlankField(getDateTo())) {
			if (DateCreater.formatStringToDate(DateCreater.formatFromDate(getDateFrom()))
					.compareTo(DateCreater.formatStringToDate(DateCreater.formatFromDate(getDateTo()))) > 0) {
				addFieldError(CrmFieldType.DATE_FROM.getName(), CrmFieldConstants.FROMTO_DATE_VALIDATION.getValue());
			} else if (DateCreater.diffDate(getDateFrom(), getDateTo()) > 7) {
				addFieldError(CrmFieldType.DATE_FROM.getName(), CrmFieldConstants.DATE_RANGE.getValue());
			}
		}
	}*/
	
	public String getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(String dateFrom) {
		this.dateFrom = dateFrom;
	}

	public String getDateTo() {
		return dateTo;
	}

	public void setDateTo(String dateTo) {
		this.dateTo = dateTo;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public String getAcquirer() {
		return acquirer;
	}

	public void setAcquirer(String acquirer) {
		this.acquirer = acquirer;
	}

	public String getMerchant() {
		return merchant;
	}

	public void setMerchant(String merchant) {
		this.merchant = merchant;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public String getRefundFlag() {
		return refundFlag;
	}

	public void setRefundFlag(String refundFlag) {
		this.refundFlag = refundFlag;
	}

	public String getRefundAmount() {
		return refundAmount;
	}

	public void setRefundAmount(String refundAmount) {
		this.refundAmount = refundAmount;
	}

	public String getPgRefNum() {
		return pgRefNum;
	}

	public void setPgRefNum(String pgRefNum) {
		this.pgRefNum = pgRefNum;
	}

	public String getRefundDate() {
		return refundDate;
	}

	public void setRefundDate(String refundDate) {
		this.refundDate = refundDate;
	}

	public String getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getRefundOrderId() {
		return refundOrderId;
	}

	public void setRefundOrderId(String refundOrderId) {
		this.refundOrderId = refundOrderId;
	}
	
	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public List<RefundRejection> getAaData() {
		return aaData;
	}

	public void setAaData(List<RefundRejection> aaData) {
		this.aaData = aaData;
	}

}
