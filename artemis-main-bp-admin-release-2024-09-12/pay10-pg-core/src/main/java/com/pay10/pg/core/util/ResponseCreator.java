package com.pay10.pg.core.util;

import com.pay10.commons.dao.FieldsDao;
import com.pay10.commons.dao.InvoiceDao;
import com.pay10.commons.dao.PaymentLinkDao;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.Invoice;
import com.pay10.commons.user.PaymentLink;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.*;
import com.pay10.pg.core.fraudPrevention.core.FraudRuleImplementor;
import com.pay10.pg.core.security.TransactionConverterIrctc;
import com.pay10.pg.core.util.emitra.EmitraScrambler;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.owasp.esapi.ESAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.util.Map.Entry;

/**
 * @author Sunil
 */

@Service
public class ResponseCreator extends Forwarder {

	private static Logger logger = LoggerFactory.getLogger(ResponseCreator.class.getName());
	private static final long serialVersionUID = 6021494405007391983L;

	@Autowired
	@Qualifier("responseProcessor")
	private Processor responseProcessor;

	@Autowired
	@Qualifier("userDao")
	private UserDao userDao;

	@Autowired
	InvoiceDao invoiceDao;

	@Autowired
	PaymentLinkDao paymentLinkDao;

	@Autowired
	@Qualifier("propertiesManager")
	private PropertiesManager propertiesManager;

	@Autowired
	@Qualifier("irctcTransactionConverter")
	private TransactionConverterIrctc converter;

	@Autowired
	TransactionResponser transactionResponser;

	@Autowired
	private MerchantHostedUtils merchantHostedUtils;

	@Autowired
	private FieldsDao fieldsDao;

	@Autowired
	private EmailService emailService;

	@Autowired
	private FraudRuleImplementor fraudRuleImplementor;

	@Autowired
	private EmitraScrambler emitraScrambler;

	public void create(Fields fields) {
		try {
			if (StringUtils.isNotBlank(fields.get(FieldType.RESPONSE_CODE.getName())) && fields
					.get(FieldType.RESPONSE_CODE.getName()).equals(ErrorType.DUPLICATE_RESPONSE.getCode())) {
				return;
			}
			responseProcessor.preProcess(fields);
			responseProcessor.process(fields);
			responseProcessor.postProcess(fields);

		} catch (SystemException systemException) {
			logger.error("Exception", systemException);
			fields.clear();
		} catch (Exception exception) {
			logger.error("Exception", exception);
			fields.clear();
		}
	}

	public void ResponsePost(Fields fields) {
		resolveStatus(fields);

		if (StringUtils.isNotBlank(fields.get(FieldType.RESPONSE_CODE.getName())) && fields
				.get(FieldType.RESPONSE_CODE.getName()).equals(ErrorType.DUPLICATE_RESPONSE.getCode())) {
			return;
		}
		if(StringUtils.isNotBlank(fields.get(FieldType.STATUS.getName()))
				&& (fields.get(FieldType.STATUS.getName()).equalsIgnoreCase("Sent to Bank") || fields.get(FieldType.STATUS.getName()).equalsIgnoreCase("Pending")  || fields.get(FieldType.STATUS.getName()).equalsIgnoreCase("REQUEST ACCEPTED"))) {
			if(StringUtils.isNotBlank(fields.get(FieldType.RESPONSE_MESSAGE.getName()))
					&& fields.get(FieldType.RESPONSE_MESSAGE.getName()).equalsIgnoreCase("SUCCESS")) {
				fields.put(FieldType.RESPONSE_MESSAGE.getName(), "Request Accepted");
			}
		}
		if(StringUtils.isNotBlank(fields.get(FieldType.STATUS.getName()))
				&& fields.get(FieldType.STATUS.getName()).equalsIgnoreCase("Failed")) {
			if(StringUtils.isNotBlank(fields.get(FieldType.RESPONSE_MESSAGE.getName()))
					&& fields.get(FieldType.RESPONSE_MESSAGE.getName()).equalsIgnoreCase("SUCCESS")) {
				fields.put(FieldType.RESPONSE_MESSAGE.getName(), "Transaction Failed");
			}
		}


		String internalIrctcYN = fields.get(FieldType.INTERNAL_IRCTC_YN.getName());
		System.out.println("IRCTC FLAG :"+fields.get(FieldType.INTERNAL_IRCTC_YN.getName()));
		System.out.println("IRCTC FLAG :"+internalIrctcYN);
		String merchantHostedFlag = fields.get(FieldType.IS_MERCHANT_HOSTED.getName());
		logger.info("ResponsePost method IS_MERCHANT_HOSTED : {}", merchantHostedFlag);

		String returnUrl = fields.get(FieldType.RETURN_URL.getName());
		boolean emitra = StringUtils.containsIgnoreCase(returnUrl, PropertiesManager.propertiesMap.get("EMITRA_URL"));
		try {
			if (StringUtils.isNotBlank(fields.get(FieldType.STATUS.getName()))
					&& StringUtils.isNotBlank(fields.get(FieldType.PAY_ID.getName()))) {
				fieldsDao.sendCallback(fields);
			}
			fields.remove(FieldType.HASH.getName());
			String applicationKeyForRemoval;
			if(fields.contains(FieldType.UDF13.getName()) && fields.get(FieldType.UDF13.getName()).equalsIgnoreCase("PO")) {
				applicationKeyForRemoval = Constants.RESPONSE_CALLBACK_PAY_OUT.getValue();
			}else{
				applicationKeyForRemoval = Constants.RESPONSE_CALLBACK_PAY_IN.getValue();
			}
			
			String postingMethodFlag = fields.get(FieldType.POSTING_METHOD_FLAG.getName());
			logger.info("postingMethodFlag, PG_REF_NUM={},ORDER_ID={},POSTING_METHOD_FLAG={}",fields.get(FieldType.PG_REF_NUM.getName()),fields.get(FieldType.ORDER_ID.getName()),postingMethodFlag);
			boolean useGetMethod = false;
			if (postingMethodFlag != null) {
				useGetMethod = true;
			}
			logger.info("postingMethodFlag, PG_REF_NUM={},ORDER_ID={},POSTING_METHOD_FLAG={},useGetMethod={}",fields.get(FieldType.PG_REF_NUM.getName()),fields.get(FieldType.ORDER_ID.getName()),postingMethodFlag,useGetMethod);
			fieldsDao.removeFieldsByPropertyFile(fields, applicationKeyForRemoval);
			PrintWriter out = ServletActionContext.getResponse().getWriter();

			if (null != internalIrctcYN && internalIrctcYN.equals("Y")) {
				String key = propertiesManager.getSystemProperty(ConstantsIrctc.IRCTC_KEY);
				String iv = propertiesManager.getSystemProperty(ConstantsIrctc.IRCTC_IV);

				try {
					StringBuilder requestfields = converter.mapSaleFields(fields);
					requestfields = converter.mapChecksum(requestfields);
					logger.info("Plain text response to  Irctc eTicketing" + requestfields);
					String encryptedString = MerchantIPayUtil.encryptIRCTC(requestfields.toString(), key, iv);
					String finalResponse = createCrisResponse(fields, encryptedString);
					logger.info("encrypted response to Irctc eTicketing " + finalResponse);
					out.write(finalResponse);
					out.flush();
					out.close();
				} catch (Exception e) {
					logger.error("Exception", e);
				}
			}

			else if (null != merchantHostedFlag && "Y".equalsIgnoreCase(merchantHostedFlag.trim())) {
				try {
//					fields.removeInternalFields();
//					fields.removeSecureFields();
//					fields.remove(FieldType.ORIG_TXN_ID.getName());
					fields.remove(FieldType.HASH.getName());
					String internalStatus = fields.get(FieldType.STATUS.getName());
					if (emitra) {
						StatusType statusType = StatusType.getInstanceFromName(StringUtils.upperCase(internalStatus));
						String status = statusType.getUiName();
						status = StringUtils.equalsAnyIgnoreCase(status, "Invalid", "Cancelled") ? "Failed" : status;
						status = StringUtils.equalsAnyIgnoreCase(status, "Captured") ? "Success" : status;
						fields.put(FieldType.STATUS.getName(), StringUtils.upperCase(status));
						transactionResponser.addHash(fields, true);
					} else {
						transactionResponser.addHash(fields);
					}
//					fields.remove(FieldType.ORIG_TXN_ID.getName());
					fields.logAllFields("Merchant Hosted : Plain text response for  merchant: ");
					String finalResponse = createMerchantHostedPgResponse(fields, emitra, internalStatus, returnUrl);
					logger.info("Merchant Hosted : encrypted response to merchant " + finalResponse);
					out.write(finalResponse);
					out.flush();
					out.close();
				} catch (Exception e) {
					logger.error("Exception", e);
				}
			} else {
//				fields.removeInternalFields();
//				fields.removeSecureFields();
//				fields.remove(FieldType.ORIG_TXN_ID.getName());

				fields.remove(FieldType.HASH.getName());
				String internalStatus = fields.get(FieldType.STATUS.getName());
				if (emitra) {
					StatusType statusType = StatusType.getInstanceFromName(StringUtils.upperCase(internalStatus));
					String status = statusType.getUiName();
					status = StringUtils.equalsAnyIgnoreCase(status, "Invalid", "Cancelled") ? "Failed" : status;
					status = StringUtils.equalsAnyIgnoreCase(status, "Captured") ? "Success" : status;
					fields.put(FieldType.STATUS.getName(), StringUtils.upperCase(status));
					transactionResponser.addHash(fields, true);
				} else {
					transactionResponser.addHash(fields);
				}
//				fields.remove(FieldType.ORIG_TXN_ID.getName());
				String finalResponse = createPgResponse(fields, emitra, internalStatus,useGetMethod);
				logger.info("PG Hosted flow final response sent " + finalResponse);
				out.write(finalResponse);
				out.flush();
				out.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error sending response to merchant" + e);
		}
	}

	// response create for upi S2S
	public void ResponsePostUpiS2s(Fields fields) {
		resolveStatus(fields);

		if (StringUtils.isNotBlank(fields.get(FieldType.RESPONSE_CODE.getName())) && fields
				.get(FieldType.RESPONSE_CODE.getName()).equals(ErrorType.DUPLICATE_RESPONSE.getCode())) {
			return;
		}
		if(StringUtils.isNotBlank(fields.get(FieldType.STATUS.getName()))
				&& fields.get(FieldType.STATUS.getName()).equalsIgnoreCase("Sent to Bank")) {
			if(StringUtils.isNotBlank(fields.get(FieldType.RESPONSE_MESSAGE.getName()))
					&& fields.get(FieldType.RESPONSE_MESSAGE.getName()).equalsIgnoreCase("SUCCESS")) {
				fields.put(FieldType.RESPONSE_MESSAGE.getName(), "Request Accepted");
			}
		}
		if(StringUtils.isNotBlank(fields.get(FieldType.STATUS.getName()))
				&& fields.get(FieldType.STATUS.getName()).equalsIgnoreCase("Failed")) {
			if(StringUtils.isNotBlank(fields.get(FieldType.RESPONSE_MESSAGE.getName()))
					&& fields.get(FieldType.RESPONSE_MESSAGE.getName()).equalsIgnoreCase("SUCCESS")) {
				fields.put(FieldType.RESPONSE_MESSAGE.getName(), "Transaction Failed");
			}
		}
		String merchantHostedFlag = fields.get(FieldType.IS_MERCHANT_HOSTED.getName());
		logger.info("UPI S2S ResponsePost method IS_MERCHANT_HOSTED : {}", merchantHostedFlag);
		if (null != merchantHostedFlag && "Y".equalsIgnoreCase(merchantHostedFlag.trim())) {
			try {
				String payer_name = fields.get(FieldType.PAYER_NAME.getName());
				String payer_address = fields.get(FieldType.PAYER_ADDRESS.getName());
				String rrn = fields.get(FieldType.RRN.getName());
				fields.removeInternalFields();
				fields.removeSecureFields();
				fields.remove(FieldType.ORIG_TXN_ID.getName());
				fields.remove(FieldType.HASH.getName());
				transactionResponser.removeInvalidResponseFields(fields);
				fields.put(FieldType.PAYER_ADDRESS.getName(), StringUtils.defaultString(payer_address, "NA"));
				fields.put(FieldType.PAYER_NAME.getName(), StringUtils.defaultString(payer_name, "NA"));
				if (StringUtils.isNotBlank(rrn)) {
					fields.put(FieldType.RRN.getName(), rrn);
				}
				transactionResponser.addHash(fields);
				fields.logAllFields(" UPI S2s Merchant Hosted : Plain text response for  merchant: ");
				String encData = merchantHostedUtils.encryptMerchantResponse(fields);
				fields.put(FieldType.ENCDATA.getName(), encData);
				logger.info(" UPI S2s  Merchant Hosted : encrypted response to merchant " + encData);

			} catch (Exception e) {
				logger.error("Exception : UPI S2s  Merchant Hosted  ", e);
			}
		}

	}
	
	
	public void ResponsePostPayout(Fields fields) {
		
		logger.info("aSas"+fields.getFieldsAsString());
		resolveStatus(fields);

		if (StringUtils.isNotBlank(fields.get(FieldType.RESPONSE_CODE.getName())) && fields
				.get(FieldType.RESPONSE_CODE.getName()).equals(ErrorType.DUPLICATE_RESPONSE.getCode())) {
			return;
		}
		if(StringUtils.isNotBlank(fields.get(FieldType.STATUS.getName()))
				&& fields.get(FieldType.STATUS.getName()).equalsIgnoreCase(StatusTypePayout.REQUEST_ACCEPTED.getName())) {
			if(StringUtils.isNotBlank(fields.get(FieldType.RESPONSE_MESSAGE.getName()))
					&& fields.get(FieldType.RESPONSE_MESSAGE.getName()).equalsIgnoreCase(ErrorType.PROCESSING.getResponseMessage())) {
				//fields.put(FieldType.RESPONSE_MESSAGE.getName(), "Request Accepted");
				fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.PROCESSING.getResponseCode());
				fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.PROCESSING.getResponseMessage());
			}
		}
		
		
		if(StringUtils.isNotBlank(fields.get(FieldType.STATUS.getName()))
				&& fields.get(FieldType.STATUS.getName()).equalsIgnoreCase(StatusTypePayout.PENDING.getName())) {
			if(StringUtils.isNotBlank(fields.get(FieldType.RESPONSE_MESSAGE.getName()))
					&& fields.get(FieldType.RESPONSE_MESSAGE.getName()).equalsIgnoreCase(StatusTypePayout.PENDING.getName())) {
				//fields.put(FieldType.RESPONSE_MESSAGE.getName(), StatusTypePayout.PENDING.getName());
				
				fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.PENDING.getResponseCode());
				fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.PENDING.getResponseMessage());
			}
		}
		
		
		if(StringUtils.isNotBlank(fields.get(FieldType.STATUS.getName()))
				&& fields.get(FieldType.STATUS.getName()).equalsIgnoreCase(StatusTypePayout.SUCCESS.getName())) {
			if(StringUtils.isNotBlank(fields.get(FieldType.RESPONSE_MESSAGE.getName()))
					&& fields.get(FieldType.RESPONSE_MESSAGE.getName()).equalsIgnoreCase(StatusTypePayout.SUCCESS.getName())) {
				//fields.put(FieldType.RESPONSE_MESSAGE.getName(), StatusTypePayout.SUCCESS.getName());
				fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SUCCESS.getResponseCode());
				fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.SUCCESS.getResponseMessage());
			}
		}
		
		if(StringUtils.isNotBlank(fields.get(FieldType.STATUS.getName()))
				&& fields.get(FieldType.STATUS.getName()).equalsIgnoreCase(StatusTypePayout.FAIL.getName())) {
			if(StringUtils.isNotBlank(fields.get(FieldType.RESPONSE_MESSAGE.getName()))
					&& fields.get(FieldType.RESPONSE_MESSAGE.getName()).equalsIgnoreCase(StatusTypePayout.FAIL.getName())) {
				//fields.put(FieldType.RESPONSE_MESSAGE.getName(), StatusTypePayout.FAIL.getName());
				fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.FAILED.getResponseCode());
				fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.FAILED.getResponseMessage());
			}
		}
		
		if(StringUtils.isNotBlank(fields.get(FieldType.STATUS.getName()))
				&& fields.get(FieldType.STATUS.getName()).equalsIgnoreCase("Sent to Bank")) {
			if(StringUtils.isNotBlank(fields.get(FieldType.RESPONSE_MESSAGE.getName()))
					&& fields.get(FieldType.RESPONSE_MESSAGE.getName()).equalsIgnoreCase("SUCCESS")) {
				fields.put(FieldType.RESPONSE_MESSAGE.getName(), "Request Accepted");
			}
		}
		
		
		if(StringUtils.isNotBlank(fields.get(FieldType.STATUS.getName()))
				&& fields.get(FieldType.STATUS.getName()).equalsIgnoreCase("Invalid")) {
			if(StringUtils.isNotBlank(fields.get(FieldType.RESPONSE_MESSAGE.getName()))
					&& fields.get(FieldType.RESPONSE_MESSAGE.getName()).equalsIgnoreCase("Payout Not Enabled")) {
				fields.put(FieldType.RESPONSE_MESSAGE.getName(), "Payout Not Enabled");
			}
		}
		
		if(StringUtils.isNotBlank(fields.get(FieldType.STATUS.getName()))
				&& fields.get(FieldType.STATUS.getName()).equalsIgnoreCase("Failed")) {
			if(StringUtils.isNotBlank(fields.get(FieldType.RESPONSE_MESSAGE.getName()))
					&& fields.get(FieldType.RESPONSE_MESSAGE.getName()).equalsIgnoreCase("SUCCESS")) {
				fields.put(FieldType.RESPONSE_MESSAGE.getName(), "Transaction Failed");
			}
		}
		
		
		if(fields.get(FieldType.STATUS.getName()).equalsIgnoreCase(StatusTypePayout.FAIL.getName()) && fields.get(FieldType.RESPONSE_MESSAGE.getName()).equals(ErrorType.PO_FRM.getInternalMessage())){
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.PO_FRM.getResponseCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(),ErrorType.PO_FRM.getInternalMessage());
		}
		
		if(fields.get(FieldType.STATUS.getName()).equalsIgnoreCase(StatusTypePayout.FAIL.getName()) &&fields.get(FieldType.RESPONSE_MESSAGE.getName()).equals(ErrorType.PO_FRM_MIN_TICKET_SIZE.getInternalMessage())){
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.PO_FRM_MIN_TICKET_SIZE.getResponseCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(),ErrorType.PO_FRM_MIN_TICKET_SIZE.getInternalMessage());
		}
		
		if(fields.get(FieldType.STATUS.getName()).equalsIgnoreCase(StatusTypePayout.FAIL.getName()) &&fields.get(FieldType.RESPONSE_MESSAGE.getName()).equals(ErrorType.PO_FRM_MAX_TICKET_SIZE.getInternalMessage())){
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.PO_FRM_MAX_TICKET_SIZE.getResponseCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(),ErrorType.PO_FRM_MAX_TICKET_SIZE.getInternalMessage());
		}
		
		if(fields.get(FieldType.STATUS.getName()).equalsIgnoreCase(StatusTypePayout.FAIL.getName()) &&fields.get(FieldType.RESPONSE_MESSAGE.getName()).equals(ErrorType.PO_FRM_DAILY_LIMIT.getInternalMessage())){
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.PO_FRM_DAILY_LIMIT.getResponseCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(),ErrorType.PO_FRM_DAILY_LIMIT.getInternalMessage());
		}
		
		if(fields.get(FieldType.STATUS.getName()).equalsIgnoreCase(StatusTypePayout.FAIL.getName()) &&fields.get(FieldType.RESPONSE_MESSAGE.getName()).equals(ErrorType.PO_FRM_DAILY_VOLUME.getInternalMessage())){
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.PO_FRM_DAILY_VOLUME.getResponseCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(),ErrorType.PO_FRM_DAILY_VOLUME.getInternalMessage());
		}
		
		if(fields.get(FieldType.STATUS.getName()).equalsIgnoreCase(StatusTypePayout.FAIL.getName()) &&fields.get(FieldType.RESPONSE_MESSAGE.getName()).equals(ErrorType.PO_FRM_WEEKLY_LIMIT.getInternalMessage())){
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.PO_FRM_WEEKLY_LIMIT.getResponseCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(),ErrorType.PO_FRM_WEEKLY_LIMIT.getInternalMessage());
		}
		
		if(fields.get(FieldType.STATUS.getName()).equalsIgnoreCase(StatusTypePayout.FAIL.getName()) &&fields.get(FieldType.RESPONSE_MESSAGE.getName()).equals(ErrorType.PO_FRM_WEEKLY_VOLUME.getInternalMessage())){
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.PO_FRM_WEEKLY_VOLUME.getResponseCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(),ErrorType.PO_FRM_WEEKLY_VOLUME.getInternalMessage());
		}
		
		if(fields.get(FieldType.STATUS.getName()).equalsIgnoreCase(StatusTypePayout.FAIL.getName()) &&fields.get(FieldType.RESPONSE_MESSAGE.getName()).equals(ErrorType.PO_FRM_MONTHLY_LIMIT.getInternalMessage())){
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.PO_FRM_MONTHLY_LIMIT.getResponseCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(),ErrorType.PO_FRM_MONTHLY_LIMIT.getInternalMessage());
		}
		
		if(fields.get(FieldType.STATUS.getName()).equalsIgnoreCase(StatusTypePayout.FAIL.getName()) &&fields.get(FieldType.RESPONSE_MESSAGE.getName()).equals(ErrorType.PO_FRM_MONTHLY_VOLUME.getInternalMessage())){
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.PO_FRM_MONTHLY_VOLUME.getResponseCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(),ErrorType.PO_FRM_MONTHLY_VOLUME.getInternalMessage());
		}
		
		
		
		
		
		String merchantHostedFlag = fields.get(FieldType.IS_MERCHANT_HOSTED.getName());
		logger.info("UPI S2S ResponsePost method IS_MERCHANT_HOSTED : {}", merchantHostedFlag);
		if (null != merchantHostedFlag && "Y".equalsIgnoreCase(merchantHostedFlag.trim())) {
			try {
				
				fields.remove(FieldType.HASH.getName());
				transactionResponser.removeInvalidResponseFields(fields);
				fields.remove("SURCHARGE_AMOUNT");
				transactionResponser.addHash(fields);
				fields.logAllFields(" UPI S2s Merchant Hosted : Plain text response for  merchant: ");
				String encData = merchantHostedUtils.encryptMerchantResponse(fields);
				fields.put(FieldType.ENCDATA.getName(), encData);
				logger.info(" UPI S2s  Merchant Hosted : encrypted response to merchant " + encData);

			} catch (Exception e) {
				logger.error("Exception : UPI S2s  Merchant Hosted  ", e);
			}
		}

	}

	//response create for nb S2S
	public void ResponsePostNBS2s(Fields fields) {
		resolveStatus(fields);

		if (StringUtils.isNotBlank(fields.get(FieldType.RESPONSE_CODE.getName())) && fields
				.get(FieldType.RESPONSE_CODE.getName()).equals(ErrorType.DUPLICATE_RESPONSE.getCode())) {
			return;
		}
		if(StringUtils.isNotBlank(fields.get(FieldType.STATUS.getName()))
				&& fields.get(FieldType.STATUS.getName()).equalsIgnoreCase("Sent to Bank")) {
			if(StringUtils.isNotBlank(fields.get(FieldType.RESPONSE_MESSAGE.getName()))
					&& fields.get(FieldType.RESPONSE_MESSAGE.getName()).equalsIgnoreCase("SUCCESS")) {
				fields.put(FieldType.RESPONSE_MESSAGE.getName(), "Request Accepted");
			}
		}
		if(StringUtils.isNotBlank(fields.get(FieldType.STATUS.getName()))
				&& fields.get(FieldType.STATUS.getName()).equalsIgnoreCase("Failed")) {
			if(StringUtils.isNotBlank(fields.get(FieldType.RESPONSE_MESSAGE.getName()))
					&& fields.get(FieldType.RESPONSE_MESSAGE.getName()).equalsIgnoreCase("SUCCESS")) {
				fields.put(FieldType.RESPONSE_MESSAGE.getName(), "Transaction Failed");
			}
		}
		String merchantHostedFlag = fields.get(FieldType.IS_MERCHANT_HOSTED.getName());
		logger.info("UPI S2S ResponsePost method IS_MERCHANT_HOSTED : {}", merchantHostedFlag);
		if (null != merchantHostedFlag && "Y".equalsIgnoreCase(merchantHostedFlag.trim())) {
			try {
				String rrn = fields.get(FieldType.RRN.getName());
				String params = fields.get("PARAMS");
				String token = fields.get(FieldType.TOKEN_ID.getName());
				String url = fields.get("URL");
				fields.removeInternalFields();
				fields.removeSecureFields();
				fields.remove(FieldType.ORIG_TXN_ID.getName());
				fields.remove(FieldType.HASH.getName());
				transactionResponser.removeInvalidResponseFields(fields);
				if (StringUtils.isNotBlank(rrn)) {
					fields.put(FieldType.RRN.getName(), rrn);
				}
				if (StringUtils.isNotBlank(params)) {
					fields.put("PARAMS", params);
				}
				if (StringUtils.isNotBlank(url)) {
					fields.put("URL", url);
				}
				if (StringUtils.isNotBlank(token)) {
					fields.put("TOKEN_ID", token);
				}
				transactionResponser.addHash(fields);
				fields.logAllFields(" NB S2s Merchant Hosted : Plain text response for  merchant: ");
				String encData = merchantHostedUtils.encryptMerchantResponse(fields);
				fields.put(FieldType.ENCDATA.getName(), encData);
				logger.info(" NB S2s  Merchant Hosted : encrypted response to merchant " + encData);

			} catch (Exception e) {
				logger.error("Exception : NB S2s  Merchant Hosted  ", e);
			}
		}

	}


	public String createPgResponse(Fields fields, boolean emitra, String internalStatus, boolean useGetMethod) throws SystemException {
		/*
		 * StringBuilder httpRequest = new StringBuilder();
		 * httpRequest.append("<HTML>");
		 * httpRequest.append("<BODY OnLoad=\"OnLoadEvent();\" >");
		 * httpRequest.append("<form name=\"form1\" action=\"");
		 * httpRequest.append(encodeString(fields.get(FieldType.RETURN_URL.getName())));
		 * httpRequest.append("\" method=\"post\">"); for (String key : fields.keySet())
		 * { httpRequest.append("<input type=\"hidden\" name=\"");
		 * httpRequest.append(encodeString(key)); httpRequest.append("\" value=\"");
		 * httpRequest.append(encodeString(fields.get(key))); httpRequest.append("\">");
		 * } httpRequest.append("</form>");
		 * httpRequest.append("<script language=\"JavaScript\">");
		 * httpRequest.append("function OnLoadEvent()");
		 * httpRequest.append("{document.form1.submit();}");
		 * httpRequest.append("</script>"); httpRequest.append("</BODY>");
		 * httpRequest.append("</HTML>");
		 */
		String encData;
		if (emitra) {
			double amount = fields.get(FieldType.AMOUNT.getName()) == null ? 0
					: Double.valueOf(fields.get(FieldType.AMOUNT.getName())) / 100;
			double totalAmount = fields.get(FieldType.TOTAL_AMOUNT.getName()) == null ? 0
					: Double.valueOf(fields.get(FieldType.TOTAL_AMOUNT.getName())) / 100;
			fields.put(FieldType.AMOUNT.getName(), String.valueOf(amount));
			fields.put(FieldType.TOTAL_AMOUNT.getName(), String.valueOf(totalAmount));
			encData = emitraScrambler.encrypt(fields.get(FieldType.PAY_ID.getName()), getFieldsForResponse(fields));
		} else {
			resolveStatus(fields);

			encData = merchantHostedUtils.encryptMerchantResponse(fields);
		}
		StringBuilder httpRequest = new StringBuilder();
		httpRequest.append("<HTML>");
		httpRequest.append("<BODY OnLoad=\"OnLoadEvent();\" >");
		httpRequest.append("<form name=\"form1\" action=\"");
		httpRequest.append(encodeString(fields.get(FieldType.RETURN_URL.getName())));
		if(useGetMethod) {
			httpRequest.append("\" method=\"get\">");
		}else {
			httpRequest.append("\" method=\"post\">");
		}
		
		httpRequest.append("<input type=\"hidden\" name=\"");
		httpRequest.append(FieldType.ENCDATA.getName());
		httpRequest.append("\" value=\"");
		httpRequest.append(encData);
		httpRequest.append("\">");
		httpRequest.append("<input type=\"hidden\" name=\"");
		httpRequest.append(FieldType.PAY_ID.getName());
		httpRequest.append("\" value=\"");
		httpRequest.append(fields.get(FieldType.PAY_ID.getName()));
		httpRequest.append("\">");
		httpRequest.append("</form>");
		httpRequest.append("<script language=\"JavaScript\">");
		httpRequest.append("function OnLoadEvent()");
		httpRequest.append("{document.form1.submit();}");
		httpRequest.append("</script>");
		httpRequest.append("</BODY>");
		httpRequest.append("</HTML>");

		// TODO here I need to add the logic for sent notification api related thing
		User user = userDao.getUserClass(fields.get(FieldType.PAY_ID.getName()));
		if (emitra) {
			fields.put(FieldType.STATUS.getName(), internalStatus);
		}

		logger.info("ResponseCreator  ,STATUS >>>>=" + fields.get(FieldType.STATUS.getName()));
//manish
		// deepak
		/*
		 * if(user.isNotificationApiEnableFlag()==true) {
		 * logger.info("user.isNotificationApiEnableFlag()==true"+user.
		 * isNotificationApiEnableFlag()); emailService.sendSmsPaymentToUser(fields,
		 * user); }
		 */

		// logger.info("---------- user ------------: "+user.toString());
		if (StatusType.getFailedStatusFromInternalStatus().contains(fields.get(FieldType.STATUS.getName()).toString())
				|| StatusType.USER_INACTIVE.getName()
				.equalsIgnoreCase(fields.get(FieldType.STATUS.getName()).toString())
				|| StatusType.ACQUIRER_TIMEOUT.getName()
				.equalsIgnoreCase(fields.get(FieldType.STATUS.getName()).toString())) {

			if (user.isTransactionFailedAlertFlag()) {
				logger.info("mail send for failed transaction");
				emailService.sendingEmailAndSmsNotificationForFailedTxns(fields, user);
			}


		} else if (StatusType.CAPTURED.getName().equalsIgnoreCase(fields.get(FieldType.STATUS.getName()))) {

			//	fraudRuleImplementor.applyRule(fields);

			if (fields.contains(FieldType.INVOICE_ID.getName())) {
				String invoiceId = fields.get(FieldType.INVOICE_ID.getName());
				Invoice invoice = invoiceDao.findByInvoiceId(invoiceId);
				String prodName = invoice.getProductName();

				if (invoice.getInvoiceType().getName().equals(InvoiceType.SINGLE_PAYMENT.getName())) {
					invoice.setInvoiceStatus(InvoiceStatus.PAID);
					invoiceDao.update(invoice);
				}

				if (!StringUtils.isEmpty(invoiceId)) {
					emailService.sendingEmailAndSmsNotificationForInvoiceSuccessTxns(fields, user, prodName);
				}

			} else if (fields.contains(FieldType.PAYMENT_LINK_ID.getName())) {
				String paymentLinkId = fields.get(FieldType.PAYMENT_LINK_ID.getName());
				PaymentLink paymentLink = paymentLinkDao.findByPaymentLinkId(paymentLinkId);
				if (null != paymentLink) {
					paymentLink.setInvoiceStatus(InvoiceStatus.PAID);
					paymentLinkDao.update(paymentLink);
				}
			}
			//deepak
			//send sms for successful transaction TO customer(PG hosted)
			logger.info("user.isNotificationApiEnableFlag()   pg  >>>> {} & PAYID: {}",
					user.isNotificationApiEnableFlag() ,user.getPayId());
			logger.info("User data for SMS  pg =>> {}", user.toString());
			if (user.isNotificationApiEnableFlag()) {
				User getMerchantDetails= userDao.findPayId(fields.get(FieldType.PAY_ID.getName()));
				emailService.sendSmsPaymentToUser(fields, user);
				emailService.sendSmsPaymentToMerchant(fields,getMerchantDetails);
			}
		}

		if (user.isNotificationApiEnableFlag() && !StringUtils.isEmpty(user.getNotificaionApi())) {
			emailService.sendingNotificationApi(fields, user);
		}

		return httpRequest.toString();
	}

	private String getFieldsForResponse(Fields fields) {
		StringBuilder allFields = new StringBuilder();
		for (Entry<String, String> entry : fields.getFields().entrySet()) {
			allFields.append("&");
			allFields.append(entry.getKey());
			allFields.append(ConfigurationConstants.FIELD_EQUATOR.getValue());
			allFields.append(entry.getValue());
		}
		allFields.deleteCharAt(0);
		return allFields.toString();
	}

	public String createMerchantHostedPgResponse(Fields fields, boolean emitra, String internalStatus, String returnUrl) throws SystemException {
		String encData;
		if (emitra) {
			double amount = fields.get(FieldType.AMOUNT.getName()) == null ? 0
					: Double.valueOf(fields.get(FieldType.AMOUNT.getName())) / 100;
			double totalAmount = fields.get(FieldType.TOTAL_AMOUNT.getName()) == null ? 0
					: Double.valueOf(fields.get(FieldType.TOTAL_AMOUNT.getName())) / 100;
			fields.put(FieldType.AMOUNT.getName(), String.valueOf(amount));
			fields.put(FieldType.TOTAL_AMOUNT.getName(), String.valueOf(totalAmount));
			encData = emitraScrambler.encrypt(fields.get(FieldType.PAY_ID.getName()), getFieldsForResponse(fields));
		} else {
			resolveStatus(fields);

			encData = merchantHostedUtils.encryptMerchantResponse(fields);
		}

		StringBuilder httpRequest = new StringBuilder();
		httpRequest.append("<HTML>");
		httpRequest.append("<BODY OnLoad=\"OnLoadEvent();\" >");
		httpRequest.append("<form name=\"form1\" action=\"");
		httpRequest.append(encodeString(returnUrl));
		httpRequest.append("\" method=\"post\">");
		httpRequest.append("<input type=\"hidden\" name=\"");
		httpRequest.append(FieldType.ENCDATA.getName());
		httpRequest.append("\" value=\"");
		httpRequest.append(encData);
		httpRequest.append("\">");
		httpRequest.append("<input type=\"hidden\" name=\"");
		httpRequest.append(FieldType.PAY_ID.getName());
		httpRequest.append("\" value=\"");
		httpRequest.append(fields.get(FieldType.PAY_ID.getName()));
		httpRequest.append("\">");
		httpRequest.append("</form>");
		httpRequest.append("<script language=\"JavaScript\">");
		httpRequest.append("function OnLoadEvent()");
		httpRequest.append("{document.form1.submit();}");
		httpRequest.append("</script>");
		httpRequest.append("</BODY>");
		httpRequest.append("</HTML>");
		User user = userDao.getUserClass(fields.get(FieldType.PAY_ID.getName()));
		boolean userObjectflag = userDao.getUserObjFlag(fields.get(FieldType.PAY_ID.getName()));
		//deepak
		/*if(user.isNotificationApiEnableFlag()==true) {
		logger.info("user.isNotificationApiEnableFlag()==true merchant"+user.isNotificationApiEnableFlag());
		emailService.sendSmsPaymentToUser(fields, user);
	}*/
		if (emitra) {
			fields.put(FieldType.STATUS.getName(), internalStatus);
		}
		if (null != fields.get(FieldType.STATUS.getName()) && null != fields.get(FieldType.PAY_ID.getName())) {
			if (StatusType.getFailedStatusFromInternalStatus()
					.contains(fields.get(FieldType.STATUS.getName()).toString())
					|| StatusType.USER_INACTIVE.getName()
					.equalsIgnoreCase(fields.get(FieldType.STATUS.getName()).toString())
					|| StatusType.ACQUIRER_TIMEOUT.getName()
					.equalsIgnoreCase(fields.get(FieldType.STATUS.getName()).toString())) {
				if (user.isTransactionFailedAlertFlag()) {
					emailService.sendingEmailAndSmsNotificationForFailedTxns(fields, user);
				}
			}
		}

		if (user.isNotificationApiEnableFlag() && !StringUtils.isEmpty(user.getNotificaionApi())) {
			emailService.sendingNotificationApi(fields, user);
		}
		//deepak
		//send sms for successful transaction TO customer (nerchant hosted)
		if(StatusType.CAPTURED.getName().equalsIgnoreCase(fields.get(FieldType.STATUS.getName()))){
			logger.info("NotificationApiEnableFlag   merchant  >>>>" + userObjectflag +"& PAYID: " +user.getPayId());
			logger.info("User data for SMS Merchant =>>" + user.toString());
			if (userObjectflag) {
				emailService.sendSmsPaymentToUser(fields, user);
			}
		}

		return httpRequest.toString();
	}

	public String createCrisResponse(Fields fields, String encryptedString) {
		String returnUrl = fields.get(FieldType.RETURN_URL.getName());
		String bankCode = "";
		String appType = "";
		String[] tt = returnUrl.split("\\?");
		if (tt.length > 1) {
			String[] ttt = tt[1].split("\\&");
			bankCode = ttt[0].split("\\=")[1];
			appType = ttt[1].split("\\=")[1];
		}
		logger.info("IRCTC FINAL REQUEST TO POST"+returnUrl +"  " +bankCode+"    "+appType+"  ");
		StringBuilder httpRequest = new StringBuilder();
		httpRequest.append("<HTML>");
		httpRequest.append("<BODY OnLoad=\"OnLoadEvent();\" >");
		httpRequest.append("<form name=\"form1\" action=\"");
		httpRequest.append(fields.get(FieldType.RETURN_URL.getName()));
		httpRequest.append("\" method=\"post\">");
		httpRequest.append("<input type=\"hidden\" name=\"");
		httpRequest.append("encdata");
		httpRequest.append("\" value=\"");
		httpRequest.append(encryptedString.toUpperCase()+",bankCode="+bankCode+",appType="+appType);
		httpRequest.append("\">");
		httpRequest.append("</form>");
		httpRequest.append("<script language=\"JavaScript\">");
		httpRequest.append("function OnLoadEvent()");
		httpRequest.append("{document.form1.submit();}");
		httpRequest.append("</script>");
		httpRequest.append("</BODY>");
		httpRequest.append("</HTML>");
		logger.info("IRCTC FINAL FORM POST REQUEST TO IRCTC"+httpRequest.toString());
		return httpRequest.toString();
	}

	public String createCrisUpiResponse(Fields fields) {
		String encryptedString = "";
		String key = propertiesManager.getSystemProperty(ConstantsIrctc.IRCTC_KEY);
		String iv = propertiesManager.getSystemProperty(ConstantsIrctc.IRCTC_IV);

		try {
			StringBuilder requestfields = converter.mapSaleFields(fields);
			requestfields = converter.mapChecksum(requestfields);
			logger.info("Plain text response to  Irctc eTicketing for UPI" + requestfields.toString());
			encryptedString = MerchantIPayUtil.encryptIRCTC(requestfields.toString(), key, iv);
			logger.info("encrypted response to Irctc eTicketing for UPI" + encryptedString.toUpperCase());
		} catch (Exception e) {
			logger.error("Exception", e);
		}

		return encryptedString.toUpperCase();
	}

	public String createInvoiceRequest(Fields fields, String requestUrl) {
		StringBuilder httpRequest = new StringBuilder();
		httpRequest.append("<HTML>");
		httpRequest.append("<BODY OnLoad=\"OnLoadEvent();\" >");
		httpRequest.append("<form name=\"form1\" action=\"");
		httpRequest.append(encodeString(requestUrl));
		httpRequest.append("\" method=\"post\">");
		for (String key : fields.keySet()) {
			httpRequest.append("<input type=\"hidden\" name=\"");
			httpRequest.append(encodeString(key));
			httpRequest.append("\" value=\"");
			httpRequest.append(encodeString(fields.get(key)));
			httpRequest.append("\">");
		}
		httpRequest.append("</form>");
		httpRequest.append("<script language=\"JavaScript\">");
		httpRequest.append("function OnLoadEvent()");
		httpRequest.append("{document.form1.submit();}");
		httpRequest.append("</script>");
		httpRequest.append("</BODY>");
		httpRequest.append("</HTML>");

		return httpRequest.toString();
	}

	public static String encodeString(String data) {
		return ESAPI.encoder().encodeForHTML(data);
	}


//	public void resolveStatus(Fields fields) {
//		String status = fields.get(FieldType.STATUS.getName());
//		logger.info("asdf :"+status);
//		if (StringUtils.isNotBlank(status) && (status.equalsIgnoreCase(StatusType.CAPTURED.getName()) || status.equalsIgnoreCase(StatusType.SENT_TO_BANK.getName())
//				|| status.equalsIgnoreCase(StatusType.INVALID.getName()) || status.equalsIgnoreCase(StatusType.FAILED.getName()) || status.equalsIgnoreCase("REFUND_INITIATED"))) {
//		} else if(StringUtils.isNotBlank(status) && (status.equalsIgnoreCase(StatusType.DENIED.getName()) || status.equalsIgnoreCase(StatusType.PENDING.getName()))){
//			//fields.put(FieldType.STATUS.getName(), StatusType.PENDING.getName());
//			fields.put(FieldType.STATUS.getName(), StatusType.SENT_TO_BANK.getName());
//			fields.put(FieldType.RESPONSE_CODE.getName(), "026");
//		}else if(status.equalsIgnoreCase("REQUEST ACCEPTED") || status.equalsIgnoreCase("Pending")){
//			fields.put(FieldType.STATUS.getName(),"REQUEST ACCEPTED");
//			fields.put(FieldType.RESPONSE_CODE.getName(), "000");
//		}else {
//			fields.put(FieldType.STATUS.getName(), StatusType.FAILED.getName());
//		}
//
//	}
//manish

	public void resolveStatus(Fields fields) {
		String status = fields.get(FieldType.STATUS.getName());
		logger.info("resolveStatus :"+status);
		if (StringUtils.isNotBlank(status) && (status.equalsIgnoreCase(StatusType.CAPTURED.getName()) || status.equalsIgnoreCase(StatusType.FAILED.getName()) || status.equalsIgnoreCase("REFUND_INITIATED"))) {
		} else if(StringUtils.isNotBlank(status) && (status.equalsIgnoreCase(StatusType.DENIED.getName()))){
			//fields.put(FieldType.STATUS.getName(), StatusType.PENDING.getName());
			fields.put(FieldType.STATUS.getName(),"REQUEST ACCEPTED");
			//fields.put(FieldType.STATUS.getName(), StatusType.SENT_TO_BANK.getName());
			fields.put(FieldType.RESPONSE_CODE.getName(), "026");
		}else if(status.equalsIgnoreCase("REQUEST ACCEPTED") || status.equalsIgnoreCase("Pending") || status.equalsIgnoreCase(StatusType.SENT_TO_BANK.getName())){
			fields.put(FieldType.STATUS.getName(),"REQUEST ACCEPTED");
			fields.put(FieldType.RESPONSE_CODE.getName(), "000");
		}else {
			fields.put(FieldType.STATUS.getName(), StatusType.FAILED.getName());
		}
	}

}