package com.pay10.pg.action;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.pay10.commons.api.Hasher;
import com.pay10.commons.api.TransactionControllerServiceProvider;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.AccountCurrencyRegion;
import com.pay10.commons.user.CardHolderType;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.Amount;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.MacUtil;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.util.ResponseCreator;

public class UpiRequestAction extends AbstractSecureAction {

	private static final long serialVersionUID = -862716535985091306L;
	private static Logger logger = LoggerFactory.getLogger(UpiRequestAction.class.getName());

	@Autowired
	private TransactionControllerServiceProvider transactionControllerServiceProvider;

	@Autowired
	private ResponseCreator responseCreator;

	@Autowired
	private MacUtil macUtil;

	private String pgRefNum;
	private String paymentType;
	private String mopType;
	// private String upiCustName;
	private String QRSTRING;

	private String vpa;
	private String vpaPhone;
	private String responseCode;
	private String responseMessage;
	private String transactionStatus;
	private String txnType;
	private String cardHolderType;
	private String paymentsRegion;
	private Map<String, String> responseFields;
	private String redirectURL;
	private String msg;
	private String BROWSER_DEVICE;

	@Override
	public String execute() {

		responseFields = new HashMap<String, String>();

		try {
			Fields fields = (Fields) sessionMap.get(Constants.FIELDS.getValue());
			if (null != fields) {
			} else {
				logger.info("session fields lost");
				return ERROR;
			}

			// If User has already entered VPA but validation failed , allow user to enter
			// VPA Again
			String pgtxnMsg = fields.get(FieldType.PG_TXN_MESSAGE.getName());
			if(StringUtils.isNotBlank(BROWSER_DEVICE)){
				fields.put(FieldType.BROWSER_DEVICE.getName(),BROWSER_DEVICE.replaceAll("(?:-)+", "").replace(",", "").trim());
			}

			if (StringUtils.isNoneBlank(pgtxnMsg) && pgtxnMsg.equalsIgnoreCase(Constants.INVALID_VPA_MSG.getValue())) {
				sessionMap.remove(FieldType.IS_ENROLLED.getName());
			}

			if ((sessionMap == null) || sessionMap.get(FieldType.IS_ENROLLED.getName()) != null && sessionMap
					.get(FieldType.IS_ENROLLED.getName()).toString().equalsIgnoreCase(Constants.Y_FLAG.getValue())) {

				Fields responseMap = new Fields();
				logger.info(
						"Response Code === " + ErrorType.DUPLICATE.getInternalMessage() + " Redirecting to merchant ");
				sessionMap.put(Constants.FIELDS.getValue(), responseMap);
				sessionMap.put(FieldType.INTERNAL_ORIG_TXN_ID.getName(), responseMap.get(FieldType.TXN_ID.getName()));
				sessionMap.put(FieldType.ACQUIRER_TYPE.getName(), responseMap.get(FieldType.ACQUIRER_TYPE.getName()));
				responseMap.put(FieldType.RESPONSE_DATE_TIME.getName(),
						fields.get(FieldType.RESPONSE_DATE_TIME.getName()));
				responseMap.put(FieldType.RETURN_URL.getName(),
						sessionMap.get(FieldType.RETURN_URL.getName()).toString());
				responseMap.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.DUPLICATE.getInternalMessage());
				responseMap.put(FieldType.RESPONSE_CODE.getName(), ErrorType.DUPLICATE.getCode());
				responseMap.put(FieldType.TXNTYPE.getName(),
						(String) sessionMap.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()));
				responseMap.put(FieldType.ORIG_TXN_ID.getName(),
						fields.get(FieldType.ORIG_TXN_ID.getName()).toString());
				responseMap.put(FieldType.ORDER_ID.getName(), fields.get(FieldType.ORDER_ID.getName()).toString());
				responseMap.put(FieldType.PAY_ID.getName(), fields.get(FieldType.PAY_ID.getName()).toString());
				responseMap.put(FieldType.PAYMENT_TYPE.getName(),
						fields.get(FieldType.PAYMENT_TYPE.getName()).toString());
				responseMap.put(FieldType.TXN_ID.getName(), fields.get(FieldType.TXN_ID.getName()).toString());
				responseMap.put(FieldType.AMOUNT.getName(), fields.get(FieldType.AMOUNT.getName()).toString());
				responseMap.put(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.PG_REF_NUM.getName()).toString());
				responseMap.put(FieldType.STATUS.getName(), StatusType.REJECTED.getName());
				responseMap.put(FieldType.CURRENCY_CODE.getName(),
						fields.get(FieldType.CURRENCY_CODE.getName()).toString());
				responseMap.put(FieldType.MOP_TYPE.getName(), fields.get(FieldType.MOP_TYPE.getName()).toString());

				if (StringUtils.isNotBlank(fields.get(FieldType.CUST_ID.getName()))) {
					responseMap.put(FieldType.CUST_ID.getName(), fields.get(FieldType.CUST_ID.getName()).toString());
				}

				if (StringUtils.isNotBlank(fields.get(FieldType.CUST_NAME.getName()))) {
					responseMap.put(FieldType.CUST_NAME.getName(),
							fields.get(FieldType.CUST_NAME.getName()).toString());
				}

				if (StringUtils.isNotBlank(fields.get(FieldType.TOTAL_AMOUNT.getName()))) {
					responseMap.put(FieldType.TOTAL_AMOUNT.getName(),
							fields.get(FieldType.TOTAL_AMOUNT.getName()).toString());
				}

				if (StringUtils.isNotBlank(fields.get(FieldType.RESELLER_ID.getName()))) {
					responseMap.put(FieldType.RESELLER_ID.getName(),
							fields.get(FieldType.RESELLER_ID.getName()).toString());
				}

				if (StringUtils.isNotBlank(fields.get(FieldType.CUST_EMAIL.getName()))) {
					responseMap.put(FieldType.CUST_EMAIL.getName(),
							fields.get(FieldType.CUST_EMAIL.getName()).toString());
				}

				if (StringUtils.isNotBlank(fields.get(FieldType.PRODUCT_DESC.getName()))) {
					responseMap.put(FieldType.PRODUCT_DESC.getName(),
							fields.get(FieldType.PRODUCT_DESC.getName()).toString());
				}
				if (StringUtils.isNotBlank(fields.get(FieldType.CARD_MASK.getName()))) {
					responseMap.put(FieldType.CARD_MASK.getName(),
							fields.get(FieldType.CARD_MASK.getName()).toString());
				}

				if (StringUtils.isNotBlank(fields.get(FieldType.CUST_PHONE.getName()))) {
					responseMap.put(FieldType.CUST_PHONE.getName(),
							fields.get(FieldType.CUST_PHONE.getName()).toString());
				}

				setResponseMessage(ErrorType.DUPLICATE.getInternalMessage());
				setPgRefNum(fields.get(FieldType.PG_REF_NUM.getName()));
				setResponseCode(ErrorType.DUPLICATE.getCode());

				setTransactionStatus(StatusType.REJECTED.getName());
				setTxnType(responseMap.get(FieldType.PAYMENT_TYPE.getName()));
				responseMap.removeInternalFields();
				responseMap.removeSecureFields();
				responseMap.remove(FieldType.HASH.getName());
				responseMap.put(FieldType.HASH.getName(), Hasher.getHash(responseMap));
				setResponseFields(responseMap.getFields());
				return SUCCESS;

				// return Action.NONE;
			} else {
				sessionMap.put(FieldType.IS_ENROLLED.getName(), Constants.Y_FLAG.getValue());
			}

			String ipaddress = (String) sessionMap.get(FieldType.INTERNAL_CUST_IP.getName());
			String useragent = (String) sessionMap.get(FieldType.INTERNAL_HEADER_USER_AGENT.getName());
			fields.put(FieldType.INTERNAL_CUST_IP.getName(), ipaddress);
			String mac = StringUtils.isBlank(ipaddress) ? "NA" : macUtil.getMackByIp(ipaddress);
			fields.put(FieldType.INTERNAL_CUST_MAC.getName(), mac);
			fields.put(FieldType.INTERNAL_HEADER_USER_AGENT.getName(), useragent);

			fields.put(FieldType.PAYMENT_TYPE.getName(), getPaymentType());
			fields.put(FieldType.MOP_TYPE.getName(), getMopType());

			sessionMap.put((FieldType.INTERNAL_ORIG_TXN_TYPE.getName()), TransactionType.SALE.getName());

			fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), TransactionType.SALE.getName());
			fields.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
			fields.put(FieldType.PAYER_ADDRESS.getName(), getVpa());
			fields.put(FieldType.PAYER_PHONE.getName(), getVpaPhone());
			fields.put(FieldType.INTERNAL_VALIDATE_HASH_YN.getName(), "N");
			String sessionOID = (String) sessionMap.get(FieldType.OID.getName());
			fields.put(FieldType.INTERNAL_ORIG_TXN_ID.getName(), sessionOID);

			fields.put(FieldType.CARD_HOLDER_TYPE.getName(), CardHolderType.CONSUMER.toString());
			fields.put(FieldType.PAYMENTS_REGION.getName(), AccountCurrencyRegion.DOMESTIC.toString());

			// Clear symbols from PG response
			if (fields.get(FieldType.PG_TXN_MESSAGE.getName()) != null) {
				String PG_TXN_MESSAGE = fields.get(FieldType.PG_TXN_MESSAGE.getName());
				if (PG_TXN_MESSAGE.contains(":")) {

					PG_TXN_MESSAGE.replace(":", " ");
					fields.put(FieldType.PG_TXN_MESSAGE.getName(), PG_TXN_MESSAGE);
				}
			}

			String surchargeFlag = sessionMap.get("SURCHARGE_FLAG").toString();

			if (surchargeFlag.equals("Y")) {
				fields.put((FieldType.SURCHARGE_FLAG.getName()), surchargeFlag);
				String currencyCode = fields.get(FieldType.CURRENCY_CODE.getName());
				String paymentType = fields.get(FieldType.PAYMENT_TYPE.getName());
				if (paymentType.equals(PaymentType.UPI.getCode())) {
					String upTotalAmount = sessionMap.get(FieldType.UP_TOTAL_AMOUNT.getName()).toString();
					upTotalAmount = Amount.formatAmount(upTotalAmount, currencyCode);

					fields.put(FieldType.TOTAL_AMOUNT.getName(), upTotalAmount);
				}else if (paymentType.equals(PaymentType.QRCODE.getCode())) {
					
					String qrTotalAmount = sessionMap.get(FieldType.QR_TOTAL_AMOUNT.getName()).toString();
					qrTotalAmount = Amount.formatAmount(qrTotalAmount, currencyCode);

					fields.put(FieldType.TOTAL_AMOUNT.getName(), qrTotalAmount);
					logger.info(">>>>>>>>>>>>>>>>>>>>>>>>"+fields.getFieldsAsString());

				} 
				else {
					throw new SystemException(ErrorType.PAYMENT_OPTION_NOT_SUPPORTED, "Unsupported payment type");
				}
			} else {
				fields.put(FieldType.TOTAL_AMOUNT.getName(), fields.get(FieldType.AMOUNT.getName()));
			}

			if (StringUtils.isNotBlank(fields.get(FieldType.ACQUIRER_TYPE.getName())))

			// If Double response is posted from payment page ,then send txn rejected
			// response
			{
				String responseCode = ErrorType.DUPLICATE.getCode();
				Map<String, String> fieldsMap = new HashMap<String, String>();

				for (String key : fields.keySet()) {
					fieldsMap.put(key, fields.get(key));
				}
				fieldsMap.put(FieldType.RESPONSE_CODE.getName(), ErrorType.DUPLICATE.getResponseCode());
				fieldsMap.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.DUPLICATE.getResponseMessage());
				fieldsMap.put(FieldType.PG_TXN_MESSAGE.getName(), ErrorType.DUPLICATE.getResponseMessage());
				fieldsMap.put(FieldType.STATUS.getName(), StatusType.DUPLICATE.getName());
				fieldsMap.remove(FieldType.ACS_URL.getName());
				fieldsMap.remove(FieldType.CARD_MASK.getName());
				fieldsMap.remove(FieldType.PRODUCT_DESC.getName());
				fieldsMap.remove(FieldType.MD.getName());
				fieldsMap.remove(FieldType.CUST_EMAIL.getName());
				fieldsMap.remove(FieldType.CARD_HOLDER_TYPE.getName());
				fieldsMap.remove(FieldType.PAYER_NAME.getName());
				fieldsMap.remove(FieldType.PAYMENTS_REGION.getName());
				fieldsMap.remove(FieldType.PAYER_ADDRESS.getName());
				fieldsMap.remove(FieldType.PAYER_PHONE.getName());
				fieldsMap.remove(FieldType.MERCHANT_ID.getName());
				fieldsMap.remove(FieldType.ACQUIRER_TYPE.getName());
				fieldsMap.remove(FieldType.ACQ_ID.getName());
				fieldsMap.remove(FieldType.ACQUIRER_TYPE.getName());
				Fields responseMap = new Fields(fieldsMap);

				logger.info("Response Code === " + responseCode + " Redirecting to merchant ");
				logger.info("Response fields from pgws:  " + responseMap.getFieldsAsString());
				sessionMap.put(Constants.FIELDS.getValue(), responseMap);
				sessionMap.put(FieldType.INTERNAL_ORIG_TXN_ID.getName(), responseMap.get(FieldType.TXN_ID.getName()));
				responseMap.put(FieldType.RETURN_URL.getName(),
						sessionMap.get(FieldType.RETURN_URL.getName()).toString());
				if (responseMap.get(FieldType.PG_TXN_MESSAGE.getName()) != null) {
					setResponseMessage(responseMap.get(FieldType.PG_TXN_MESSAGE.getName()));
				} else {
					setResponseMessage(responseMap.get(FieldType.RESPONSE_MESSAGE.getName()));
				}
				setPgRefNum(responseMap.get(FieldType.PG_REF_NUM.getName()));
				setResponseCode(responseMap.get(FieldType.RESPONSE_CODE.getName()));

				setTransactionStatus(responseMap.get(FieldType.STATUS.getName()));
				setTxnType(responseMap.get(FieldType.PAYMENT_TYPE.getName()));
				responseMap.removeInternalFields();
				responseMap.removeSecureFields();
				responseMap.remove(FieldType.HASH.getName());
				// E-ticketing
				String crisFlag = (String) sessionMap.get(FieldType.INTERNAL_IRCTC_YN.getName());
				if (StringUtils.isNotBlank(crisFlag) && crisFlag.equals(Constants.Y.getValue())) {
					responseMap.put(FieldType.INTERNAL_IRCTC_YN.getName(), crisFlag);
					String encData = responseCreator.createCrisUpiResponse(responseMap);
					logger.info("encrypted response to Irctc eTicketing for UPI in UPIRequestAction " + encData);
					responseFields.put(Constants.ENC_DATA.getValue(), encData);
					responseFields.put(FieldType.RETURN_URL.getName(),
							sessionMap.get(FieldType.RETURN_URL.getName()).toString());
				} else {
					responseMap.put(FieldType.HASH.getName(), Hasher.getHash(responseMap));
					setResponseFields(responseMap.getFields());
				}

				return SUCCESS;
			}

			if (StringUtils.isBlank(fields.get(FieldType.AMOUNT.getName()))) {
				throw new SystemException(ErrorType.BANK_SURCHARGE_REJECTED, "Bank Surcharge request rejected");
			}

			if (surchargeFlag.equals("Y")) {
				logger.info("Upi Request action orderId = " + fields.get(FieldType.ORDER_ID.getName())
						+ " total amount = " + fields.get(FieldType.TOTAL_AMOUNT.getName()) + "  amount  = "
						+ fields.get(FieldType.AMOUNT.getName()));
				if (Double.valueOf(fields.get(FieldType.TOTAL_AMOUNT.getName()))
						.compareTo(Double.valueOf(fields.get(FieldType.AMOUNT.getName()))) < 0) {
					throw new SystemException(ErrorType.BANK_SURCHARGE_REJECTED, "Bank Surcharge request rejected");
				}
			}

			if (StringUtils.isNotBlank(getVpa())) {
				fields.put(FieldType.CARD_MASK.getName(), getVpa());
				fields.put(FieldType.PAYMENT_TYPE.getName(), getPaymentType());
			}
			Map<String, String> response = transactionControllerServiceProvider.transact(fields,
					Constants.TXN_WS_INTERNAL.getValue());

			String crisFlag = (String) sessionMap.get(FieldType.INTERNAL_IRCTC_YN.getName());

			// If Invalid VPA, remove acquirer type from response to enable payment again
			// via CC DC or NB
			if (response.get(FieldType.RESPONSE_CODE.getName())
					.equalsIgnoreCase(ErrorType.INVALID_VPA.getResponseCode())) {
				response.remove(FieldType.ACQUIRER_TYPE.getName());
			}

			Fields responseMap = new Fields(response);
			logger.info("Response received from Transact in UPI Request Action "
					+ responseMap.get(FieldType.RESPONSE_CODE.getName()));
			if (responseMap.get(FieldType.RESPONSE_CODE.getName()).equalsIgnoreCase(ErrorType.UNKNOWN.getCode())) {

				logger.info("Response fields from pgws:  " + responseMap.getFieldsAsString());
				sessionMap.put(Constants.FIELDS.getValue(), responseMap);
				sessionMap.put(FieldType.INTERNAL_ORIG_TXN_ID.getName(), responseMap.get(FieldType.TXN_ID.getName()));
				sessionMap.put(FieldType.ACQUIRER_TYPE.getName(), responseMap.get(FieldType.ACQUIRER_TYPE.getName()));
				responseMap.put(FieldType.RETURN_URL.getName(),
						sessionMap.get(FieldType.RETURN_URL.getName()).toString());
				responseMap.put(FieldType.RESPONSE_MESSAGE.getName(),
						ErrorType.INTERNAL_SYSTEM_ERROR.getResponseMessage());
				responseMap.put(FieldType.RESPONSE_CODE.getName(), ErrorType.INTERNAL_SYSTEM_ERROR.getCode());
				responseMap.put(FieldType.TXNTYPE.getName(),
						(String) sessionMap.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()));
				if (responseMap.get(FieldType.PG_TXN_MESSAGE.getName()) != null) {
					setResponseMessage(responseMap.get(FieldType.PG_TXN_MESSAGE.getName()));
				} else {
					setResponseMessage(responseMap.get(FieldType.RESPONSE_MESSAGE.getName()));
				}
				setPgRefNum(responseMap.get(FieldType.PG_REF_NUM.getName()));
				setResponseCode(responseMap.get(FieldType.RESPONSE_CODE.getName()));

				setTransactionStatus(responseMap.get(FieldType.STATUS.getName()));
				setTxnType(responseMap.get(FieldType.PAYMENT_TYPE.getName()));
				responseMap.removeInternalFields();
				responseMap.removeSecureFields();
				responseMap.remove(FieldType.HASH.getName());
				// E-ticketing
				if (StringUtils.isNotBlank(crisFlag) && crisFlag.equals(Constants.Y.getValue())) {
					responseMap.put(FieldType.INTERNAL_IRCTC_YN.getName(), crisFlag);
					String encData = responseCreator.createCrisUpiResponse(responseMap);
					responseFields.put(Constants.ENC_DATA.getValue(), encData);
					responseFields.put(FieldType.RETURN_URL.getName(),
							sessionMap.get(FieldType.RETURN_URL.getName()).toString());
				} else {
					responseMap.put(FieldType.HASH.getName(), Hasher.getHash(responseMap));
					setResponseFields(responseMap.getFields());
				}
				return SUCCESS;
			}

			if (responseMap.get(FieldType.RESPONSE_CODE.getName())
					.equalsIgnoreCase(ErrorType.ACQUIRER_NOT_FOUND.getCode())) {

				logger.info("Response Code === " + responseMap.get(FieldType.RESPONSE_CODE.getName())
						+ " Redirecting to merchant ");
				logger.info("Response fields from pgws:  " + responseMap.getFieldsAsString());
				sessionMap.put(Constants.FIELDS.getValue(), responseMap);
				sessionMap.put(FieldType.INTERNAL_ORIG_TXN_ID.getName(), responseMap.get(FieldType.TXN_ID.getName()));
				sessionMap.put(FieldType.ACQUIRER_TYPE.getName(), responseMap.get(FieldType.ACQUIRER_TYPE.getName()));
				responseMap.put(FieldType.RETURN_URL.getName(),
						sessionMap.get(FieldType.RETURN_URL.getName()).toString());
				responseMap.put(FieldType.RESPONSE_MESSAGE.getName(),
						ErrorType.ACQUIRER_NOT_FOUND.getResponseMessage());
				responseMap.put(FieldType.RESPONSE_CODE.getName(), ErrorType.ACQUIRER_NOT_FOUND.getCode());
				responseMap.put(FieldType.TXNTYPE.getName(),
						(String) sessionMap.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()));
				if (responseMap.get(FieldType.PG_TXN_MESSAGE.getName()) != null) {
					setResponseMessage(responseMap.get(FieldType.PG_TXN_MESSAGE.getName()));
				} else {
					setResponseMessage(responseMap.get(FieldType.RESPONSE_MESSAGE.getName()));
				}
				setPgRefNum(responseMap.get(FieldType.PG_REF_NUM.getName()));
				setResponseCode(responseMap.get(FieldType.RESPONSE_CODE.getName()));

				setTransactionStatus(responseMap.get(FieldType.STATUS.getName()));
				setTxnType(responseMap.get(FieldType.PAYMENT_TYPE.getName()));
				responseMap.removeInternalFields();
				responseMap.removeSecureFields();
				responseMap.remove(FieldType.HASH.getName());
				// E-ticketing
				if (StringUtils.isNotBlank(crisFlag) && crisFlag.equals(Constants.Y.getValue())) {
					responseMap.put(FieldType.INTERNAL_IRCTC_YN.getName(), crisFlag);
					String encData = responseCreator.createCrisUpiResponse(responseMap);
					responseFields.put(Constants.ENC_DATA.getValue(), encData);
					responseFields.put(FieldType.RETURN_URL.getName(),
							sessionMap.get(FieldType.RETURN_URL.getName()).toString());
				} else {
				 	responseMap.put(FieldType.HASH.getName(), Hasher.getHash(responseMap));
					setResponseFields(responseMap.getFields());
					setRedirectURL(PropertiesManager.propertiesMap.get("UPIRedirectURL"));
					sessionMap.put(Constants.FIELDS.getValue(), responseMap);
					setPgRefNum(responseMap.get(FieldType.PG_REF_NUM.getName()));
					setResponseCode(ErrorType.REDIRECT_UPI.getCode());
				}
				return SUCCESS;
			}
			
			if (responseMap.get(FieldType.RESPONSE_CODE.getName())
					.equalsIgnoreCase(ErrorType.DENIED_BY_FRAUD.getCode())) {
				logger.info("******* DENIED_BY_FRAUD ******");
				logger.info("Response Code === " + responseMap.get(FieldType.RESPONSE_CODE.getName()) + " Redirecting to merchant ");
				logger.info("Response fields from pgws:  " + responseMap.getFieldsAsString());
				setRedirectURL(PropertiesManager.propertiesMap.get("UPIRedirectURL"));
				sessionMap.put(Constants.FIELDS.getValue(), responseMap);
				sessionMap.put(FieldType.INTERNAL_ORIG_TXN_ID.getName(), responseMap.get(FieldType.TXN_ID.getName()));
				sessionMap.put(FieldType.ACQUIRER_TYPE.getName(), responseMap.get(FieldType.ACQUIRER_TYPE.getName()));
				sessionMap.put(FieldType.ADF7.getName(), responseMap.get(FieldType.ADF7.getName()));
				responseMap.put(FieldType.RETURN_URL.getName(), sessionMap.get(FieldType.RETURN_URL.getName()).toString());
				setPgRefNum(responseMap.get(FieldType.PG_REF_NUM.getName()));
				setResponseCode(ErrorType.REDIRECT_UPI.getCode());
				return SUCCESS;
			}
			if (responseMap.get(FieldType.RESPONSE_CODE.getName())
					.equalsIgnoreCase(ErrorType.TDR_NOT_SET_FOR_THIS_AMOUNT.getResponseCode())) {
				logger.info("******* TDR NOT SET FOR THIS AMOUNT ******");
				logger.info("Response Code === " + responseMap.get(FieldType.RESPONSE_CODE.getName()) + " Redirecting to merchant ");
				logger.info("Response fields from pgws:  " + responseMap.getFieldsAsString());
				setRedirectURL(PropertiesManager.propertiesMap.get("UPIRedirectURL"));
				sessionMap.put(Constants.FIELDS.getValue(), responseMap);
				sessionMap.put(FieldType.INTERNAL_ORIG_TXN_ID.getName(), responseMap.get(FieldType.TXN_ID.getName()));
				sessionMap.put(FieldType.ACQUIRER_TYPE.getName(), responseMap.get(FieldType.ACQUIRER_TYPE.getName()));
				sessionMap.put(FieldType.ADF7.getName(), responseMap.get(FieldType.ADF7.getName()));
				responseMap.put(FieldType.RETURN_URL.getName(), sessionMap.get(FieldType.RETURN_URL.getName()).toString());
				setPgRefNum(responseMap.get(FieldType.PG_REF_NUM.getName()));
				setResponseCode(ErrorType.REDIRECT_UPI.getResponseCode());
				return SUCCESS;
			}
			logger.info("UPIRequestAction " + responseMap.get(FieldType.RESPONSE_CODE.getName())+"\t"+ErrorType.EKYC_VERIFICATION.getCode());
			if (responseMap.get(FieldType.RESPONSE_CODE.getName())
					.equalsIgnoreCase(ErrorType.EKYC_VERIFICATION.getCode())) {

				logger.info("Response Code === " + responseMap.get(FieldType.RESPONSE_CODE.getName())
						+ " Redirecting to merchant ");
				logger.info("Response fields from pgws:  " + responseMap.getFieldsAsString());
				sessionMap.put(Constants.FIELDS.getValue(), responseMap);
				sessionMap.put(FieldType.INTERNAL_ORIG_TXN_ID.getName(), responseMap.get(FieldType.TXN_ID.getName()));
				sessionMap.put(FieldType.ACQUIRER_TYPE.getName(), responseMap.get(FieldType.ACQUIRER_TYPE.getName()));
				responseMap.put(FieldType.RETURN_URL.getName(),
						sessionMap.get(FieldType.RETURN_URL.getName()).toString());
				responseMap.put(FieldType.RESPONSE_MESSAGE.getName(),
						ErrorType.EKYC_VERIFICATION.getResponseMessage());
				responseMap.put(FieldType.RESPONSE_CODE.getName(), ErrorType.FAILED.getCode());
				responseMap.put(FieldType.TXNTYPE.getName(),
						(String) sessionMap.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()));
				if (responseMap.get(FieldType.PG_TXN_MESSAGE.getName()) != null) {
					setResponseMessage(responseMap.get(FieldType.PG_TXN_MESSAGE.getName()));
				} else {
					setResponseMessage(responseMap.get(FieldType.RESPONSE_MESSAGE.getName()));
				}
				setPgRefNum(responseMap.get(FieldType.PG_REF_NUM.getName()));
				setResponseCode(responseMap.get(FieldType.RESPONSE_CODE.getName()));

				setTransactionStatus(responseMap.get(FieldType.STATUS.getName()));
				setTxnType(responseMap.get(FieldType.PAYMENT_TYPE.getName()));
				responseMap.removeInternalFields();
				responseMap.removeSecureFields();
				responseMap.remove(FieldType.HASH.getName());
				// E-ticketing
				if (StringUtils.isNotBlank(crisFlag) && crisFlag.equals(Constants.Y.getValue())) {
					responseMap.put(FieldType.INTERNAL_IRCTC_YN.getName(), crisFlag);
					String encData = responseCreator.createCrisUpiResponse(responseMap);
					responseFields.put(Constants.ENC_DATA.getValue(), encData);
					responseFields.put(FieldType.RETURN_URL.getName(),
							sessionMap.get(FieldType.RETURN_URL.getName()).toString());
				} else {
					responseMap.put(FieldType.HASH.getName(), Hasher.getHash(responseMap));
					setResponseFields(responseMap.getFields());
				}
				return SUCCESS;
			}
			
			if (responseMap.get(FieldType.RESPONSE_CODE.getName())
					.equalsIgnoreCase(ErrorType.MERCHANT_HOSTED_S2S.getCode())) {

				logger.info("Response Code === " + responseMap.get(FieldType.RESPONSE_CODE.getName())
						+ " Redirecting to merchant ");
				logger.info("Response fields from pgws:  " + responseMap.getFieldsAsString());
				sessionMap.put(Constants.FIELDS.getValue(), responseMap);
				sessionMap.put(FieldType.INTERNAL_ORIG_TXN_ID.getName(), responseMap.get(FieldType.TXN_ID.getName()));
				sessionMap.put(FieldType.ACQUIRER_TYPE.getName(), responseMap.get(FieldType.ACQUIRER_TYPE.getName()));
				responseMap.put(FieldType.RETURN_URL.getName(),
						sessionMap.get(FieldType.RETURN_URL.getName()).toString());
				responseMap.put(FieldType.RESPONSE_MESSAGE.getName(),
						ErrorType.MERCHANT_HOSTED_S2S.getResponseMessage());
				responseMap.put(FieldType.RESPONSE_CODE.getName(), ErrorType.MERCHANT_HOSTED_S2S.getCode());
				responseMap.put(FieldType.TXNTYPE.getName(),
						(String) sessionMap.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()));
				if (responseMap.get(FieldType.PG_TXN_MESSAGE.getName()) != null) {
					setResponseMessage(responseMap.get(FieldType.PG_TXN_MESSAGE.getName()));
				} else {
					setResponseMessage(responseMap.get(FieldType.RESPONSE_MESSAGE.getName()));
				}
				setPgRefNum(responseMap.get(FieldType.PG_REF_NUM.getName()));
				setResponseCode(responseMap.get(FieldType.RESPONSE_CODE.getName()));

				setTransactionStatus(responseMap.get(FieldType.STATUS.getName()));
				setTxnType(responseMap.get(FieldType.PAYMENT_TYPE.getName()));
				responseMap.removeInternalFields();
				responseMap.removeSecureFields();
				responseMap.remove(FieldType.HASH.getName());
				// E-ticketing
				if (StringUtils.isNotBlank(crisFlag) && crisFlag.equals(Constants.Y.getValue())) {
					responseMap.put(FieldType.INTERNAL_IRCTC_YN.getName(), crisFlag);
					String encData = responseCreator.createCrisUpiResponse(responseMap);
					responseFields.put(Constants.ENC_DATA.getValue(), encData);
					responseFields.put(FieldType.RETURN_URL.getName(),
							sessionMap.get(FieldType.RETURN_URL.getName()).toString());
				} else {
					responseMap.put(FieldType.HASH.getName(), Hasher.getHash(responseMap));
					setResponseFields(responseMap.getFields());
				}
				return SUCCESS;
			}
			
			logger.info("Response fields from pgws:  " + responseMap.getFieldsAsString());
			sessionMap.put(Constants.FIELDS.getValue(), responseMap);
			sessionMap.put(FieldType.INTERNAL_ORIG_TXN_ID.getName(), responseMap.get(FieldType.TXN_ID.getName()));
			sessionMap.put(FieldType.ACQUIRER_TYPE.getName(), responseMap.get(FieldType.ACQUIRER_TYPE.getName()));
			sessionMap.put(FieldType.ADF7.getName(), responseMap.get(FieldType.ADF7.getName()));
			responseMap.put(FieldType.RETURN_URL.getName(), sessionMap.get(FieldType.RETURN_URL.getName()).toString());
			if (responseMap.get(FieldType.PG_TXN_MESSAGE.getName()) != null) {
				setResponseMessage(responseMap.get(FieldType.PG_TXN_MESSAGE.getName()));
			} else {
				setResponseMessage(responseMap.get(FieldType.RESPONSE_MESSAGE.getName()));
			}
			setPgRefNum(responseMap.get(FieldType.PG_REF_NUM.getName()));
			setResponseCode(responseMap.get(FieldType.RESPONSE_CODE.getName()));

			// Incase of UPI via BOB, redirect user to bob url
			if (responseMap.get(FieldType.ACQUIRER_TYPE.getName()).equalsIgnoreCase(AcquirerType.BOB.getCode())) {
				setResponseCode(ErrorType.REDIRECT_UPI.getCode());
				setRedirectURL(PropertiesManager.propertiesMap.get("UPIRedirectURL"));
				sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
				sessionMap.put(FieldType.BOB_FINAL_REQUEST.getName(),
						responseMap.get(FieldType.BOB_FINAL_REQUEST.getName()));
			}

			// Incase of UPI via PAYU, redirect user to PAYU URL
			if (responseMap.get(FieldType.ACQUIRER_TYPE.getName()).equalsIgnoreCase(AcquirerType.PAYU.getCode())) {
				setResponseCode(ErrorType.REDIRECT_UPI.getCode());
				setRedirectURL(PropertiesManager.propertiesMap.get("UPIRedirectURL"));
				sessionMap.put(FieldType.MERCHANT_ID.getName(), responseMap.get(FieldType.MERCHANT_ID.getName()));
				sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
				sessionMap.put(FieldType.ADF3.getName(), responseMap.get(FieldType.ADF3.getName()));
				sessionMap.put(FieldType.TOTAL_AMOUNT.getName(), responseMap.get(FieldType.TOTAL_AMOUNT.getName()));
				sessionMap.put(FieldType.PG_REF_NUM.getName(), responseMap.get(FieldType.PG_REF_NUM.getName()));
			}

			
			if (responseMap.get(FieldType.ACQUIRER_TYPE.getName()).equalsIgnoreCase(AcquirerType.PAY10.getCode())) {

				setResponseCode(ErrorType.REDIRECT_UPI.getCode());
				setRedirectURL(PropertiesManager.propertiesMap.get("UPIRedirectURL"));
				sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
				sessionMap.put(FieldType.MERCHANT_ID.getName(), responseMap.get(FieldType.MERCHANT_ID.getName()));
				sessionMap.put(FieldType.ADF1.getName(), responseMap.get(FieldType.ADF1.getName()));
			}
			
			if (responseMap.get(FieldType.ACQUIRER_TYPE.getName()).equalsIgnoreCase(AcquirerType.HTPAY.getCode())) {

				setResponseCode(ErrorType.REDIRECT_UPI.getCode());
				setRedirectURL(PropertiesManager.propertiesMap.get("UPIRedirectURL"));
				sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
				sessionMap.put(FieldType.MERCHANT_ID.getName(), responseMap.get(FieldType.MERCHANT_ID.getName()));
				sessionMap.put(FieldType.ADF1.getName(), responseMap.get(FieldType.ADF1.getName()));
			}
			if (responseMap.get(FieldType.ACQUIRER_TYPE.getName()).equalsIgnoreCase(AcquirerType.DEMO.getCode())) {
				setResponseCode(ErrorType.REDIRECT_UPI.getCode());
				setRedirectURL(PropertiesManager.propertiesMap.get("UPIRedirectURL"));
				sessionMap.put(FieldType.MERCHANT_ID.getName(), responseMap.get(FieldType.MERCHANT_ID.getName()));
				sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
				sessionMap.put(FieldType.PG_REF_NUM.getName(), responseMap.get(FieldType.PG_REF_NUM.getName()));
			}
			
			

			// Incase of UPI via CAMSPAY, redirect user to CAMSPAY URL
			if (responseMap.get(FieldType.ACQUIRER_TYPE.getName()).equalsIgnoreCase(AcquirerType.CAMSPAY.getCode())) {
				setResponseCode(ErrorType.REDIRECT_UPI.getCode());
				setRedirectURL(PropertiesManager.propertiesMap.get("UPIRedirectURL"));
				sessionMap.put(FieldType.MERCHANT_ID.getName(), responseMap.get(FieldType.MERCHANT_ID.getName()));
				sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
				sessionMap.put(FieldType.ADF1.getName(), responseMap.get(FieldType.ADF1.getName()));
				sessionMap.put(FieldType.ADF2.getName(), responseMap.get(FieldType.ADF2.getName()));
				sessionMap.put(FieldType.ADF3.getName(), responseMap.get(FieldType.ADF3.getName()));
				sessionMap.put(FieldType.ADF4.getName(), responseMap.get(FieldType.ADF4.getName()));
				sessionMap.put(FieldType.ADF5.getName(), responseMap.get(FieldType.ADF5.getName()));
				sessionMap.put(FieldType.ADF6.getName(), responseMap.get(FieldType.ADF6.getName()));
				sessionMap.put(FieldType.ADF7.getName(), responseMap.get(FieldType.ADF7.getName()));
				sessionMap.put(FieldType.ADF8.getName(), responseMap.get(FieldType.ADF8.getName()));
				sessionMap.put(FieldType.ADF9.getName(), responseMap.get(FieldType.ADF9.getName()));
				sessionMap.put(FieldType.ADF10.getName(), responseMap.get(FieldType.ADF10.getName()));
				sessionMap.put(FieldType.TOTAL_AMOUNT.getName(), responseMap.get(FieldType.TOTAL_AMOUNT.getName()));
				sessionMap.put(FieldType.PG_REF_NUM.getName(), responseMap.get(FieldType.PG_REF_NUM.getName()));
			}

			// Incase of UPI via ATOM, redirect user to ATOM URL
			if (responseMap.get(FieldType.ACQUIRER_TYPE.getName()).equalsIgnoreCase(AcquirerType.ATOM.getCode())) {
				setResponseCode(ErrorType.REDIRECT_UPI.getCode());
				setRedirectURL(PropertiesManager.propertiesMap.get("UPIRedirectURL"));
				sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.ADF4.getName()));
				sessionMap.put(FieldType.PG_REF_NUM.getName(), responseMap.get(FieldType.PG_REF_NUM.getName()));
				sessionMap.put(FieldType.IV.getName(), responseMap.get(FieldType.ADF3.getName()));
				sessionMap.put(FieldType.RESP_IV.getName(), responseMap.get(FieldType.ADF5.getName()));
				sessionMap.put(FieldType.RESP_TXN_KEY.getName(), responseMap.get(FieldType.ADF8.getName()));
			}
			
			if (responseMap.get(FieldType.ACQUIRER_TYPE.getName()).equalsIgnoreCase(AcquirerType.PAYMENTAGE.getCode())) {
				setResponseCode(ErrorType.REDIRECT_UPI.getCode());
				setRedirectURL(PropertiesManager.propertiesMap.get("UPIRedirectURL"));
				sessionMap.put(FieldType.MERCHANT_ID.getName(), responseMap.get(FieldType.MERCHANT_ID.getName()));
				sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
				sessionMap.put(FieldType.ADF3.getName(), responseMap.get(FieldType.ADF3.getName()));
				sessionMap.put(FieldType.TOTAL_AMOUNT.getName(), responseMap.get(FieldType.TOTAL_AMOUNT.getName()));
				sessionMap.put(FieldType.PG_REF_NUM.getName(), responseMap.get(FieldType.PG_REF_NUM.getName()));
			}

			// In case of UPI via CASHFREE, redirect user to CASHFREE URL
			if (responseMap.get(FieldType.ACQUIRER_TYPE.getName()).equalsIgnoreCase(AcquirerType.CASHFREE.getCode())) {
				setResponseCode(ErrorType.REDIRECT_UPI.getCode());
				setRedirectURL(PropertiesManager.propertiesMap.get("UPIRedirectURL"));
				sessionMap.put(FieldType.MERCHANT_ID.getName(), responseMap.get(FieldType.MERCHANT_ID.getName()));
				sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
				sessionMap.put(FieldType.PG_REF_NUM.getName(), responseMap.get(FieldType.PG_REF_NUM.getName()));
			}
			if (responseMap.get(FieldType.ACQUIRER_TYPE.getName()).equalsIgnoreCase(AcquirerType.DEMO.getCode())) {
				setResponseCode(ErrorType.REDIRECT_UPI.getCode());
				setRedirectURL(PropertiesManager.propertiesMap.get("UPIRedirectURL"));
				sessionMap.put(FieldType.MERCHANT_ID.getName(), responseMap.get(FieldType.MERCHANT_ID.getName()));
				sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
				sessionMap.put(FieldType.PG_REF_NUM.getName(), responseMap.get(FieldType.PG_REF_NUM.getName()));
			}
			// In case of UPI via AGREEPAY, redirect user to AGREEPAY URL
			if (responseMap.get(FieldType.ACQUIRER_TYPE.getName()).equalsIgnoreCase(AcquirerType.AGREEPAY.getCode())) {
				setResponseCode(ErrorType.REDIRECT_UPI.getCode());
				setRedirectURL(PropertiesManager.propertiesMap.get("AGREEPAYReturnUrl"));
				sessionMap.put(FieldType.MERCHANT_ID.getName(), responseMap.get(FieldType.MERCHANT_ID.getName()));
				sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
				sessionMap.put(FieldType.PG_REF_NUM.getName(), responseMap.get(FieldType.PG_REF_NUM.getName()));
				sessionMap.put(FieldType.ADF1.getName(), responseMap.get(FieldType.ADF1.getName()));
			}
			// In case of UPI via PINELABS, redirect user to PINELABS URL
			if (responseMap.get(FieldType.ACQUIRER_TYPE.getName()).equalsIgnoreCase(AcquirerType.PINELABS.getCode())) {
				setResponseCode(ErrorType.REDIRECT_UPI.getCode());
				setRedirectURL(PropertiesManager.propertiesMap.get("UPIRedirectURL"));
				sessionMap.put(FieldType.MERCHANT_ID.getName(), responseMap.get(FieldType.MERCHANT_ID.getName()));
				sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
				sessionMap.put(FieldType.PG_REF_NUM.getName(), responseMap.get(FieldType.PG_REF_NUM.getName()));
				sessionMap.put(FieldType.ADF1.getName(), responseMap.get(FieldType.ADF1.getName()));
				sessionMap.put(FieldType.ADF10.getName(), responseMap.get(FieldType.ADF10.getName()));
			}

			// In case of UPI via EASEBUZZ, redirect user to EASEBUZZ URL
			if (responseMap.get(FieldType.ACQUIRER_TYPE.getName()).equalsIgnoreCase(AcquirerType.EASEBUZZ.getCode())) {
				setResponseCode(ErrorType.REDIRECT_UPI.getCode());
				setRedirectURL(PropertiesManager.propertiesMap.get("EASEBUZZReturnUrl"));
				sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.ADF1.getName()));
				sessionMap.put(FieldType.MERCHANT_ID.getName(), responseMap.get(FieldType.MERCHANT_ID.getName()));
				// sessionMap.put(FieldType.MERCHANT_ID.getName(),
				// responseMap.get(FieldType.MERCHANT_ID.getName()));
				// sessionMap.put(FieldType.TXN_KEY.getName(),
				// responseMap.get(FieldType.TXN_KEY.getName()));
				sessionMap.put(FieldType.PG_REF_NUM.getName(), responseMap.get(FieldType.PG_REF_NUM.getName()));
			}
			if (responseMap.get(FieldType.ACQUIRER_TYPE.getName()).equalsIgnoreCase(AcquirerType.COSMOS.getCode())&&responseMap.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(PaymentType.QRCODE.getCode())) {

				setQRSTRING(generateQRCodeImage(responseMap.get(FieldType.COSMOS_UPI_FINAL_REQUEST.getName())));
			}
			// In case of UPI via ISGPAY, redirect user to ISGPAY URL
			if (responseMap.get(FieldType.ACQUIRER_TYPE.getName()).equalsIgnoreCase(AcquirerType.ISGPAY.getCode())) {
				setResponseCode(ErrorType.REDIRECT_UPI.getCode());
				setRedirectURL(PropertiesManager.propertiesMap.get("ISGPAYReturnUrl"));
				sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
				sessionMap.put(FieldType.PASSWORD.getName(), responseMap.get(FieldType.ADF5.getName()));
				sessionMap.put(FieldType.ADF4.getName(), responseMap.get(FieldType.ADF4.getName()));
				sessionMap.put(FieldType.PG_REF_NUM.getName(), responseMap.get(FieldType.PG_REF_NUM.getName()));
			}

			setTransactionStatus(responseMap.get(FieldType.STATUS.getName()));
			setTxnType(responseMap.get(FieldType.PAYMENT_TYPE.getName()));
			responseMap.removeInternalFields();
			responseMap.removeSecureFields();
			responseMap.remove(FieldType.HASH.getName());
			// E-ticketing
			if (StringUtils.isNotBlank(crisFlag) && crisFlag.equals(Constants.Y.getValue())) {
				responseMap.put(FieldType.INTERNAL_IRCTC_YN.getName(), crisFlag);
				String encData = responseCreator.createCrisUpiResponse(responseMap);
				responseFields.put(Constants.ENC_DATA.getValue(), encData);
				responseFields.put(FieldType.RETURN_URL.getName(),
						sessionMap.get(FieldType.RETURN_URL.getName()).toString());
			} else {
				responseMap.put(FieldType.HASH.getName(), Hasher.getHash(responseMap));
				setResponseFields(responseMap.getFields());
			}

			return SUCCESS;

		} catch (SystemException systemException) {
			logger.error("systemException", systemException);

		} catch (Exception exception) {
			logger.error("Error handling of transaction", exception);
			return ERROR;
		}
		return SUCCESS;
	}
	public static String generateQRCodeImage(String barcodeText) throws Exception {
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
	    QRCodeWriter barcodeWriter = new QRCodeWriter();
	    BitMatrix bitMatrix = 
	      barcodeWriter.encode(barcodeText, BarcodeFormat.QR_CODE, 200, 200);
	    BufferedImage img = MatrixToImageWriter.toBufferedImage(bitMatrix);
	    		try (final java.io.OutputStream b64os = (java.io.OutputStream)Base64.getEncoder().wrap(os)) {
	    	        ImageIO.write(img, "png", b64os);
	    	    } catch (final IOException ioe) {
	    	        throw new UncheckedIOException(ioe);
	    	    }
	    	    return os.toString();
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public String getMopType() {
		return mopType;
	}

	public void setMopType(String mopType) {
		this.mopType = mopType;
	}

	public String getVpaPhone() {
		return vpaPhone;
	}

	public void setVpaPhone(String vpaPhone) {
		this.vpaPhone = vpaPhone;
	}

	public String getVpa() {
		return vpa;
	}

	public void setVpa(String vpa) {
		this.vpa = vpa;
	}

	public String getPgRefNum() {
		return pgRefNum;
	}

	public void setPgRefNum(String pgRefNum) {
		this.pgRefNum = pgRefNum;
	}

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	public String getTransactionStatus() {
		return transactionStatus;
	}

	public void setTransactionStatus(String transactionStatus) {
		this.transactionStatus = transactionStatus;
	}

	public String getTxnType() {
		return txnType;
	}

	public void setTxnType(String txnType) {
		this.txnType = txnType;
	}

	public String getCardHolderType() {
		return cardHolderType;
	}

	public void setCardHolderType(String cardHolderType) {
		this.cardHolderType = cardHolderType;
	}

	public String getPaymentsRegion() {
		return paymentsRegion;
	}

	public void setPaymentsRegion(String paymentsRegion) {
		this.paymentsRegion = paymentsRegion;
	}

	public Map<String, String> getResponseFields() {
		return responseFields;
	}

	public void setResponseFields(Map<String, String> responseFields) {
		this.responseFields = responseFields;
	}

	public String getRedirectURL() {
		return redirectURL;
	}

	public void setRedirectURL(String redirectURL) {
		this.redirectURL = redirectURL;
	}

	public String getQRSTRING() {
		return QRSTRING;
	}


	public void setQRSTRING(String qRSTRING) {
		QRSTRING = qRSTRING;
	}
	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getBROWSER_DEVICE() {
		return BROWSER_DEVICE;
	}

	public void setBROWSER_DEVICE(String bROWSER_DEVICE) {
		BROWSER_DEVICE = bROWSER_DEVICE;
	}

}
