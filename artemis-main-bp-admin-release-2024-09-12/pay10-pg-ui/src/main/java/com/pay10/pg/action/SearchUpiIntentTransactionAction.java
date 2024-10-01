package com.pay10.pg.action;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;
import com.pay10.commons.api.TransactionControllerServiceProvider;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.Amount;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.action.beans.SearchTransactionActionBean;
import com.pay10.pg.action.beans.TransactionStatusCheckBean;
import com.pay10.pg.core.util.MerchantHostedUtils;
import com.pay10.pg.core.util.ResponseCreator;

public class SearchUpiIntentTransactionAction extends AbstractSecureAction {

	private static final long serialVersionUID = -340013262943006979L;
	private static Logger logger = LoggerFactory.getLogger(SearchUpiIntentTransactionAction.class.getName());

	private String pgRefNum;
	private String transactionStatus;
	private Map<String, String> responseFields;
	Fields fields = new Fields();
	@Autowired
	private ResponseCreator responseCreator;
	@Autowired
	private SearchTransactionActionBean searchTransactionActionBean;

	@Autowired
	private TransactionControllerServiceProvider transactionControllerServiceProvider;

	@Autowired
	private TransactionStatusCheckBean transactionStatusCheckBean;
	
	@Autowired
	private MerchantHostedUtils merchantHostedUtils;
	
	@Override
	public String execute() {

		logger.info("Looking for transctions for the pg ref num " + getPgRefNum());
		try {
			Fields fields = (Fields) sessionMap.get(Constants.FIELDS.getValue());
			if (null != fields) {
			} else {
				logger.info("session fields lost");
				return ERROR;
			}

			Map<String, String> responseMap = new HashMap<String, String>();
			String enquiryFlag = PropertiesManager.propertiesMap.get(Constants.ENQUIRY_FLAG.getValue());
			String acquirerType = fields.get(FieldType.ACQUIRER_TYPE.getName());
			if (acquirerType.equalsIgnoreCase(AcquirerType.YESBANKCB.getCode())) {
				if (enquiryFlag.equalsIgnoreCase(Constants.Y_FLAG.getValue())) {
					Map<String, String> enquiry = statusEnquiry(pgRefNum, fields);
				}
			}

			if (acquirerType.equalsIgnoreCase(AcquirerType.BILLDESK.getCode()) || acquirerType.equalsIgnoreCase(AcquirerType.FREECHARGE.getCode()) ) {

				responseMap = transactionStatusCheckBean.searchPayment(fields);

			} else {
				responseMap = searchTransactionActionBean.searchPayment(getPgRefNum());
			}

			if (responseMap == null) {
				setTransactionStatus(StatusType.SENT_TO_BANK.getName().toString());
				return SUCCESS;
			}

			else {

				fields.putAll(responseMap);
				String currencyCode = fields.get(FieldType.CURRENCY_CODE.getName());

				if (fields.get(FieldType.AMOUNT.getName()) != null) {
					String amount = fields.get(FieldType.AMOUNT.getName());
					fields.put(FieldType.AMOUNT.getName(), Amount.formatAmount(amount, currencyCode));
				}

				if (fields.get(FieldType.TOTAL_AMOUNT.getName()) != null) {
					String upTotalAmount = fields.get(FieldType.TOTAL_AMOUNT.getName());
					fields.put(FieldType.TOTAL_AMOUNT.getName(), Amount.formatAmount(upTotalAmount, currencyCode));
				}
				String crisFlag = (String) sessionMap.get(FieldType.INTERNAL_IRCTC_YN.getName());
				fields.put(FieldType.RETURN_URL.getName(), sessionMap.get(FieldType.RETURN_URL.getName()).toString());
				fields.remove(FieldType.HASH.getName());
				fields.remove(FieldType.ORIG_TXN_ID.getName());
				//responseCreator.create(fields);
				// transactionResponser.addHash(field);
				String encDataNew = merchantHostedUtils.encryptMerchantResponse(fields);
				fields.put(FieldType.ENCDATA.getName(), encDataNew);
				fields.put(FieldType.PAY_ID.getName(),fields.get(FieldType.PAY_ID.getName()));
				logger.info("Fields after response received after STB ...   " + fields.getFieldsAsString());
				setTransactionStatus(fields.get(FieldType.STATUS.getName()));
				setResponseFields(fields.getFields());
				String isMerchantHosted = (String) sessionMap.get(FieldType.IS_MERCHANT_HOSTED.getName());
				// E-Ticketing
				if (StringUtils.isNotBlank(crisFlag) && crisFlag.equals(Constants.Y.getValue())) {
					fields.put(FieldType.INTERNAL_IRCTC_YN.getName(), crisFlag);
					String encData = responseCreator.createCrisUpiResponse(fields);
					responseFields.put(Constants.ENC_DATA.getValue(), encData);
					responseFields.put(FieldType.RETURN_URL.getName(),
							sessionMap.get(FieldType.RETURN_URL.getName()).toString());

					if (StringUtils.isNotBlank(isMerchantHosted) && !isMerchantHosted.equalsIgnoreCase("Y")) {
						sessionMap.invalidate();
					}
				} else {
					responseFields.remove(FieldType.INTERNAL_CUSTOM_MDC.getName());

					if (StringUtils.isNotBlank(isMerchantHosted) && !isMerchantHosted.equalsIgnoreCase("Y")) {
						sessionMap.invalidate();
					}

				}
				return Action.SUCCESS;

			}
		} catch (Exception e) {
			logger.error("Exception in SearchTransactionAction ", e);
		}

		return SUCCESS;
	}

	public Map<String, String> statusEnquiry(String pgRefNum, Fields fields) {

		Object fieldsObj = sessionMap.get("FIELDS");
		if (null != fieldsObj) {
			fields.put((Fields) fieldsObj);
		}
		fields.put(FieldType.TXNTYPE.getName(), TransactionType.ENQUIRY.getName());
		fields.put(FieldType.PG_REF_NUM.getName(), pgRefNum);
		fields.put(FieldType.ACQUIRER_TYPE.getName(), AcquirerType.YESBANKCB.getCode());
		fields.put(FieldType.INTERNAL_VALIDATE_HASH_YN.getName(), "N");

		Map<String, String> res = new HashMap<String, String>();
		try {
			res = transactionControllerServiceProvider.transact(fields, Constants.TXN_WS_INTERNAL.getValue());
			logger.info("Response received from WS FOR YES BANK UPI for status enquiry" + res.values().toString());
			res.remove(FieldType.ORIG_TXN_ID.getName());
			res.remove(FieldType.ACQUIRER_TYPE.getName());
			res.remove(FieldType.TXN_ID.getName());
		} catch (Exception e) {
			logger.error("Error in YEs bank UPI Status enquiry = " + e);
		}

		return res;

	}

	public String getPgRefNum() {
		return pgRefNum;
	}

	public void setPgRefNum(String pgRefNum) {
		this.pgRefNum = pgRefNum;
	}

	public String getTransactionStatus() {
		return transactionStatus;
	}

	public void setTransactionStatus(String transactionStatus) {
		this.transactionStatus = transactionStatus;
	}

	public Map<String, String> getResponseFields() {
		return responseFields;
	}

	public void setResponseFields(Map<String, String> responseFields) {
		this.responseFields = responseFields;
	}
}
