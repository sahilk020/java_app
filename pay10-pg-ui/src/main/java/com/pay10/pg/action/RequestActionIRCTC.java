package com.pay10.pg.action;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.util.TokenHelper;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.opensymphony.xwork2.Action;
import com.pay10.commons.api.TransactionControllerServiceProvider;
import com.pay10.commons.dao.InvoiceDao;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.AccountCurrencyRegion;
import com.pay10.commons.user.Invoice;
import com.pay10.commons.user.Token;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Amount;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.CustTransactionAuthentication;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.LocateCountryName;
import com.pay10.commons.util.MacUtil;
import com.pay10.commons.util.ModeType;
import com.pay10.commons.util.PaymentTypeProvider;
import com.pay10.commons.util.PaymentTypeTransactionProvider;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.action.beans.SessionCleaner;
import com.pay10.pg.action.service.ActionService;
import com.pay10.pg.action.service.ActionServiceIRCTC;
import com.pay10.pg.action.service.PgActionServiceFactory;
import com.pay10.pg.action.service.PrepareRequestParemeterIRCTCService;
import com.pay10.pg.core.pageintegrator.GeneralValidator;
import com.pay10.pg.core.tokenization.TokenManager;
import com.pay10.pg.core.util.ResponseCreator;
import com.pay10.pg.core.util.SurchargeAmountCalculator;
import com.pay10.pg.core.util.TransactionResponser;
import com.pay10.pg.core.util.UpdateProcessor;

/**
 * @author SHASHI, Rahul,Vijaya
 *
 */
public class RequestActionIRCTC extends AbstractSecureAction implements ServletRequestAware {

	private static final long serialVersionUID = -8710588322012990107L;

	@Autowired
	TransactionControllerServiceProvider transactionControllerServiceProvider;

	@Autowired
	private SurchargeAmountCalculator calculateSurchargeAmount;

	@Autowired
	private ResponseCreator responseCreator;

	@Autowired
	private UpdateProcessor updateProcessor;

	@Autowired
	private UserDao userDao;

	@Autowired
	TransactionResponser transactionResponser;

	@Autowired
	private LocateCountryName httpURLConnectionCountry;

	@Autowired
	private TokenManager tokenManager;

	@Autowired
	private GeneralValidator generalValidator;

	@Autowired
	private PropertiesManager propertiesManager;

	@Autowired
	private ActionServiceIRCTC actionServiceIRCTC;
	
	@Autowired 
	@Qualifier("paymentTypeProvider")
	private PaymentTypeProvider paymentTypeProvider;
	
	@Autowired
	private PrepareRequestParemeterIRCTCService prepareRequestParemeterIRCTCService;
	
	@Autowired
	private MacUtil macUtil;

	private static Logger logger = LoggerFactory.getLogger(RequestAction.class.getName());

	private HttpServletRequest request;
	private String payId;
	private String amount;
	private String merchantName;
	private String businessName;
	private String orderId;
	private String cardNumber;
	private String expiryYear;
	private String expiryMonth;
	private String cvvNumber;
	private String storeCardFlag;
	private String origTxnType;
	private String redirectUrl;
	private String paymentType;
	private String mopType;
	private String bankName;
	private String responseCode;
	private String acquirerFlag;
	private String responseUrl;
	private String cardName;
	private String currencyCode;
	private String hash;
	private String returnUrl;
	private String cardsaveflag;
	private String tokenId;
	private String issuerBankName;
	private String issuerCountry;
	private String defaultLanguage;
	private String appMode;
	private String txnSource;
	private String upiCustName;
	private String vpa;
	private String response;
	private String supportedPaymentTypeString;
	private String cardHolderType;
	private String paymentsRegion;

	private Map<String, Object> supportedPaymentTypeMap = new HashMap<String, Object>();
	private Map<String, Object> cardPaymentTypeMap = new HashMap<String, Object>();
	private Map<String, Token> tokenMap = new HashMap<String, Token>();
	
	//Added by Sweety
	 @Autowired
	private InvoiceDao invoiceDao;

	@Override
	@SuppressWarnings("unchecked")
	public String execute() {
		try {
			// clean session
			sessionMap.invalidate();

			// create fields for transaction
			ActionService service = PgActionServiceFactory.getActionService();
			Fields fields = actionServiceIRCTC.prepareFieldsIRCTC(request.getParameterMap());
			String fieldsAsString = fields.getFieldsAsString();

			sessionMap.put(Constants.FIELDS.getValue(), fields);

			fields.put(FieldType.INTERNAL_REQUEST_FIELDS.getName(), fieldsAsString);
			logger.info("Raw Request Fields:  " + fields.getFieldsAsString());

			if (StringUtils.isBlank(fields.get(FieldType.HASH.getName()))) {

				throw new SystemException(ErrorType.VALIDATION_FAILED, "Invalid " + FieldType.HASH.getName());
			}

			generalValidator.validateHash(fields);
			generalValidator.validateReturnUrl(fields);

			User user = userDao.findPayId(fields.get(FieldType.PAY_ID.getName()));
			// replace the user supplied transaction type with the mode type
			// assigned to the merchant
			setOrigTxnType(ModeType.getDefaultPurchaseTransaction(user.getModeType()).getName());
			fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), getOrigTxnType());
			fields.put(FieldType.INTERNAL_IRCTC_YN.getName(), "Y");

			sessionMap.put((FieldType.INTERNAL_ORIG_TXN_TYPE.getName()), getOrigTxnType());
			sessionMap.put((FieldType.INTERNAL_SHOPIFY_YN.getName()),
					fields.get(FieldType.INTERNAL_SHOPIFY_YN.getName()));
			sessionMap.put((FieldType.CANCEL_URL.getName()), fields.get(FieldType.CANCEL_URL.getName()));
			sessionMap.put(Constants.CUSTOM_TOKEN.getValue(), TokenHelper.generateGUID());
			sessionMap.put((FieldType.RETURN_URL.getName()), fields.get(FieldType.RETURN_URL.getName()));
			sessionMap.put((FieldType.CURRENCY_CODE.getName()), fields.get(FieldType.CURRENCY_CODE.getName()));
			sessionMap.put((FieldType.SURCHARGE_FLAG.getName()), ((user.isSurchargeFlag()) ? "Y" : "N"));
			sessionMap.put(FieldType.MERCHANT_PAYMENT_TYPE.getName(),
					fields.get(FieldType.MERCHANT_PAYMENT_TYPE.getName()));
			sessionMap.put((FieldType.INTERNAL_IRCTC_YN.getName()), "Y");

			String ip = request.getHeader("X-Forwarded-For");
			String mac = StringUtils.isBlank(ip) ? "NA" : macUtil.getMackByIp(ip);
			fields.put((FieldType.INTERNAL_CUST_IP.getName()), ip);
			fields.put((FieldType.INTERNAL_CUST_MAC.getName()), mac);

			/*String countryCode = httpURLConnectionCountry.findCountryCode(request.getHeader("X-Forwarded-For"));
			if (StringUtils.isBlank(countryCode)) {
				countryCode = "NA";
			}*/
			String countryCode = "NA";
			fields.put((FieldType.INTERNAL_CUST_COUNTRY_NAME.getName()), countryCode);
			sessionMap.put(FieldType.INTERNAL_CUST_COUNTRY_NAME.getName(), countryCode);

			if (countryCode.equals(CrmFieldConstants.INDIA_REGION_CODE.getValue())) {
				fields.put((FieldType.INTERNAL_TXN_AUTHENTICATION.getName()),
						CustTransactionAuthentication.SUCCESS.getAuthenticationName());
			} else {
				fields.put((FieldType.INTERNAL_TXN_AUTHENTICATION.getName()),
						CustTransactionAuthentication.PENDING.getAuthenticationName());
			}

			fields = requestCreator(fields);
			responseCode = fields.get(FieldType.RESPONSE_CODE.getName());

			logger.info("response code " + responseCode);
			if (StringUtils.isEmpty(getResponseCode())) {
				return Action.ERROR;
			} else if (getResponseCode().equals(ErrorType.SUCCESS.getCode())) {

				// These fields are being used at PaymentPage
				sessionMap.put((FieldType.PAY_ID.getName()), fields.get(FieldType.PAY_ID.getName()));
				sessionMap.put(FieldType.ORDER_ID.getName(), fields.get(FieldType.ORDER_ID.getName()));
				sessionMap.put(FieldType.CARD_HOLDER_NAME.getName(), fields.get(FieldType.CARD_HOLDER_NAME.getName()));
				sessionMap.put(FieldType.CURRENCY_CODE.getName(), fields.get(FieldType.CURRENCY_CODE.getName()));
				sessionMap.put(FieldType.AMOUNT.getName(), fields.get(FieldType.AMOUNT.getName()));
				sessionMap.put(FieldType.CUST_PHONE.getName(), fields.get(FieldType.CUST_PHONE.getName()));

				// get String of supported payment types
				PaymentTypeTransactionProvider paymentTypeTransactionProvider = paymentTypeProvider.setSupportedPaymentOptions(user.getPayId());
				setSupportedPaymentTypeMap(paymentTypeTransactionProvider.getSupportedPaymentTypeMap());
				setCardPaymentTypeMap(paymentTypeTransactionProvider.getSupportedCardTypeMap());

				sessionMap.put(Constants.PAYMENT_TYPE_MOP.getValue(), paymentTypeTransactionProvider.getSupportedPaymentTypeMap());
				sessionMap.put(Constants.CARD_PAYMENT_TYPE_MOP.getValue(), getCardPaymentTypeMap());
				if (StringUtils.isNotBlank(fields.get(user.getCardSaveParam()))) {
					sessionMap.put(Constants.EXPRESS_PAY_FLAG.getValue(), user.isExpressPayFlag());
				} else {
					sessionMap.put(Constants.EXPRESS_PAY_FLAG.getValue(), false);
				}
				sessionMap.put(Constants.IFRAME_PAYMENT_PAGE.getValue(), user.isIframePaymentFlag());
				sessionMap.put(FieldType.INTERNAL_ORIG_TXN_ID.getName(), fields.get(FieldType.TXN_ID.getName()));
				sessionMap.put(FieldType.OID.getName(), fields.get(FieldType.TXN_ID.getName()));
				sessionMap.put(FieldType.INTERNAL_IRCTC_YN.getName(), "Y");
				// to set default language of payment page
				setDefaultLanguage(user.getDefaultLanguage());
				
				if (StringUtils.isBlank(user.getCardSaveParam())) {
					sessionMap.put(Constants.TOKEN.getValue(), "NA");
				}
				else if (StringUtils.isBlank(fields.get(user.getCardSaveParam()))) {
					sessionMap.put(Constants.TOKEN.getValue(), "NA");
				}
				else {
					if (!user.isExpressPayFlag()) {
						sessionMap.put(Constants.TOKEN.getValue(), "NA");
					} else {
						tokenMap = tokenManager.getAll(fields);
						if (tokenMap.isEmpty()) {
							sessionMap.put(Constants.TOKEN.getValue(), "NA");
						} else {
							sessionPut(Constants.TOKEN.getValue(), tokenMap);
						}
					}
				}

				String surchargeFlag = sessionMap.get(FieldType.SURCHARGE_FLAG.getName()).toString();

				if (surchargeFlag.equals(Constants.Y_FLAG.getValue())) {

					String payId = sessionMap.get(FieldType.PAY_ID.getName()).toString();

					// BigDecimal serviceTax = BigDecimal.ONE;
					String convertedamount = sessionMap.get(FieldType.AMOUNT.getName()).toString();
					String currency = sessionMap.get(FieldType.CURRENCY_CODE.getName()).toString();
					String amount = Amount.toDecimal(convertedamount, currency);
					String gst="";

					// BigDecimal surchargeDetails[] = new BigDecimal[4];
					
					String paymentsRegion = fields.get(FieldType.PAYMENTS_REGION.getName());
					if (paymentsRegion == null) {
						paymentsRegion = AccountCurrencyRegion.DOMESTIC.toString();
					}
					
					BigDecimal[] surCCAmount = calculateSurchargeAmount.fetchCCSurchargeDetails(amount, payId,paymentsRegion, gst);
					BigDecimal[] surDCAmount = calculateSurchargeAmount.fetchDCSurchargeDetails(amount, payId,paymentsRegion, gst);
					BigDecimal[] surNBAmount = calculateSurchargeAmount.fetchNBSurchargeDetails(amount, payId,paymentsRegion, gst);
					BigDecimal[] surUPAmount = calculateSurchargeAmount.fetchUPSurchargeDetails(amount, payId,paymentsRegion, gst);
					BigDecimal[] surADAmount = calculateSurchargeAmount.fetchADSurchargeDetails(amount, payId,paymentsRegion, gst);
					BigDecimal[] surWLAmount = calculateSurchargeAmount.fetchWLSurchargeDetails(amount, payId,paymentsRegion, gst);
					BigDecimal[] surPCAmount = calculateSurchargeAmount.fetchPCSurchargeDetails(amount, payId,paymentsRegion, gst);
					
					BigDecimal pcTransSurcharge = surPCAmount[0];
					BigDecimal surchargePCAmount = surPCAmount[1];
					
					BigDecimal ccTransSurcharge = surCCAmount[0];
					BigDecimal surchargeCCAmount = surCCAmount[1];
					BigDecimal dcTransSurcharge = surDCAmount[0];
					BigDecimal surchargeDCAmount = surDCAmount[1];
					BigDecimal nbTransSurcharge = surNBAmount[0];
					BigDecimal surchargeNBAmount = surNBAmount[1];
					BigDecimal upTransSurcharge = surUPAmount[0];
					BigDecimal surchargeUPAmount = surUPAmount[1];
					BigDecimal adTransSurcharge = surADAmount[0];
					BigDecimal surchargeADAmount = surADAmount[1];
					
					BigDecimal wlTransSurcharge = surWLAmount[0];
					BigDecimal surchargeWLAmount = surWLAmount[1];

					sessionMap.put(FieldType.CC_TOTAL_AMOUNT.getName(), surchargeCCAmount);
					sessionMap.put(FieldType.DC_TOTAL_AMOUNT.getName(), surchargeDCAmount);
					sessionMap.put(FieldType.NB_TOTAL_AMOUNT.getName(), surchargeNBAmount);
					sessionMap.put(FieldType.UP_TOTAL_AMOUNT.getName(), surchargeUPAmount);
					sessionMap.put(FieldType.AD_TOTAL_AMOUNT.getName(), surchargeADAmount);
					sessionMap.put(FieldType.WL_TOTAL_AMOUNT.getName(), surchargeWLAmount);
					sessionMap.put(FieldType.PC_TOTAL_AMOUNT.getName(), surchargePCAmount);
					
					sessionMap.put(FieldType.CC_SURCHARGE.getName(), ccTransSurcharge);
					sessionMap.put(FieldType.DC_SURCHARGE.getName(), dcTransSurcharge);
					sessionMap.put(FieldType.NB_SURCHARGE.getName(), nbTransSurcharge);
					sessionMap.put(FieldType.UP_SURCHARGE.getName(), upTransSurcharge);
					sessionMap.put(FieldType.AD_SURCHARGE.getName(), adTransSurcharge);
					sessionMap.put(FieldType.WL_SURCHARGE.getName(), wlTransSurcharge);
					sessionMap.put(FieldType.PC_SURCHARGE.getName(), pcTransSurcharge);
					
					JSONObject requestParameterJson = prepareRequestParemeterIRCTCService.prepareRequestParameter(fields , user ,sessionMap);
					sessionMap.put(FieldType.SUPPORTED_PAYMENT_TYPE.getName(), requestParameterJson);
					setSupportedPaymentTypeString(requestParameterJson.toString());
					logger.info("Final supported parameter request eTicketing " + requestParameterJson.toString());
					//return "surchargePaymentPage";
					
					logger.info("Header value "+request.getHeader("User-Agent"));
					logger.info("CloudFront-Is-Mobile-Viewer    " + request.getHeader("CloudFront-Is-Mobile-Viewer"));
					//logger.info("CloudFront-Is-Tablet-Viewer    " + request.getHeader("CloudFront-Is-Tablet-Viewer"));
					//logger.info("CloudFront-Is-SmartTV-Viewer    " + request.getHeader("CloudFront-Is-SmartTV-Viewer"));
					logger.info("CloudFront-Is-Desktop-Viewer    " + request.getHeader("CloudFront-Is-Desktop-Viewer"));
					if (request.getHeader("User-Agent").contains("Mobile") || request.getHeader("User-Agent").contains("iphone") || 
							(request.getHeader("CloudFront-Is-Mobile-Viewer") != null && request.getHeader("CloudFront-Is-Mobile-Viewer").equalsIgnoreCase("true"))
							||(request.getHeader("CloudFront-Is-Tablet-Viewer") != null && request.getHeader("CloudFront-Is-Tablet-Viewer").equalsIgnoreCase("true"))
							) {
						logger.info("Opening Mobile Payment page");
						return "surchargeMobilePaymentPage";
					}
					else {
						logger.info("Opening web Payment page");
						return "surchargePaymentPage";
					}
					
				} else {

					JSONObject requestParameterJson = prepareRequestParemeterIRCTCService.prepareRequestParameter(fields , user,sessionMap);
					sessionMap.put(FieldType.SUPPORTED_PAYMENT_TYPE.getName(), requestParameterJson.toString());
					setSupportedPaymentTypeString(requestParameterJson.toString());
					return "paymentPage";
				}
			} else {
				fields.put(FieldType.TXNTYPE.getName(), getOrigTxnType());
				sessionMap.put(FieldType.INTERNAL_IRCTC_YN.getName(), "Y");
				String crisFlag = (String) sessionMap.get(FieldType.INTERNAL_IRCTC_YN.getName());
				if (StringUtils.isNotBlank(crisFlag)) {
					fields.put(FieldType.INTERNAL_IRCTC_YN.getName(), crisFlag);
				}
				responseCreator.ResponsePost(fields);
				return Action.NONE;
			}

		} catch (SystemException systemException) {
			logger.error("Exception", systemException);
			Fields fields = (Fields) sessionMap.get(Constants.FIELDS.getValue());

			if (null == fields) {
				return "invalidRequest";
			}

			String salt = propertiesManager.getSalt(fields.get(FieldType.PAY_ID.getName()));

			if (null == salt) {
				return "invalidRequest";
			}

			if (StringUtils.isBlank(getOrigTxnType())) {
				User user = userDao.getUserClass(fields.get(FieldType.PAY_ID.getName()));
				setOrigTxnType(ModeType.getDefaultPurchaseTransaction(user.getModeType()).getName());
			}

			fields.put(FieldType.TXNTYPE.getName(), getOrigTxnType());
			fields.put(FieldType.RESPONSE_CODE.getName(), systemException.getErrorType().getCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), systemException.getErrorType().getResponseMessage());

			saveInvalidTransaction(fields);
			// If an invalid request of valid merchant save it
			if (fields.get(FieldType.RESPONSE_CODE.getName()).equals(ErrorType.INVALID_RETURN_URL.getCode())) {
				return "invalidRequest";
			}
			fields.put(FieldType.TXNTYPE.getName(), getOrigTxnType());
			responseCreator.create(fields);
			fields.removeInternalFields();
			String crisFlag = (String) sessionMap.get(FieldType.INTERNAL_IRCTC_YN.getName());
			if (StringUtils.isNotBlank(crisFlag)) {
				fields.put(FieldType.INTERNAL_IRCTC_YN.getName(), crisFlag);
			}
			responseCreator.ResponsePost(fields);
			sessionMap.invalidate();
			return Action.NONE;
		} catch (Exception exception) {
			SessionCleaner.cleanSession(sessionMap);
			logger.error("Exception", exception);
			return Action.ERROR;
		}
	}

	private Fields requestCreator(Fields fields) throws Exception {

		fields.put(FieldType.TXNTYPE.getName(), TransactionType.NEWORDER.getName());
		fields.put(FieldType.INTERNAL_VALIDATE_HASH_YN.getName(), "N");
		fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), getOrigTxnType());
		fields.logAllFields("All request fields :");
		logger.info("Refine fields passes to pgws:  " + fields.getFieldsAsString());

		Map<String, String> response = transactionControllerServiceProvider.transact(fields,
				Constants.TXN_WS_INTERNAL.getValue());
		fields = new Fields(response);
		sessionMap.put(Constants.FIELDS.getValue(), fields);
		logger.info("Response fields from pgws:  " + fields.getFieldsAsString());
		return fields;
	}

	private void saveInvalidTransaction(Fields fields) {
		// TODO... validate and sanitize fields
		setOrigTxnType(fields.get(FieldType.TXNTYPE.getName()));
		fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), getOrigTxnType());

		sessionMap.put((FieldType.INTERNAL_ORIG_TXN_TYPE.getName()), getOrigTxnType());

		try {
			updateProcessor.preProcess(fields);
			updateProcessor.prepareInvalidTransactionForStorage(fields);
		} catch (SystemException systemException) {
			logger.error("Unable to save invalid transaction", systemException);
		} catch (Exception exception) {
			logger.error("Unhandaled error", exception);
			// Non reachable code for safety
		}
	}


	public HttpServletRequest getHttpRequest() {
		return request;
	}

	public void setHttpRequest(HttpServletRequest httpRequest) {
		this.request = httpRequest;
	}

	public String getPayId() {
		return payId;
	}

	public void setPayId(String payId) {
		this.payId = payId;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getMerchantName() {
		return merchantName;
	}

	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}

	public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public String getExpiryYear() {
		return expiryYear;
	}

	public void setExpiryYear(String expiryYear) {
		this.expiryYear = expiryYear;
	}

	public String getExpiryMonth() {
		return expiryMonth;
	}

	public void setExpiryMonth(String expiryMonth) {
		this.expiryMonth = expiryMonth;
	}

	public String getCvvNumber() {
		return cvvNumber;
	}

	public void setCvvNumber(String cvvNumber) {
		this.cvvNumber = cvvNumber;
	}

	public String getStoreCardFlag() {
		return storeCardFlag;
	}

	public void setStoreCardFlag(String storeCardFlag) {
		this.storeCardFlag = storeCardFlag;
	}

	public String getOrigTxnType() {
		return origTxnType;
	}

	public void setOrigTxnType(String origTxnType) {
		this.origTxnType = origTxnType;
	}

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
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

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getAcquirerFlag() {
		return acquirerFlag;
	}

	public void setAcquirerFlag(String acquirerFlag) {
		this.acquirerFlag = acquirerFlag;
	}

	public String getResponseUrl() {
		return responseUrl;
	}

	public void setResponseUrl(String responseUrl) {
		this.responseUrl = responseUrl;
	}

	public String getCardName() {
		return cardName;
	}

	public void setCardName(String cardName) {
		this.cardName = cardName;
	}

	public Map<String, Object> getSupportedPaymentTypeMap() {
		return supportedPaymentTypeMap;
	}

	public void setSupportedPaymentTypeMap(Map<String, Object> supportedPaymentTypeMap) {
		this.supportedPaymentTypeMap = supportedPaymentTypeMap;
	}

	public Map<String, Object> getCardPaymentTypeMap() {
		return cardPaymentTypeMap;
	}

	public void setCardPaymentTypeMap(Map<String, Object> cardPaymentTypeMap) {
		this.cardPaymentTypeMap = cardPaymentTypeMap;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getReturnUrl() {
		return returnUrl;
	}

	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
	}

	public Map<String, Token> getTokenMap() {
		return tokenMap;
	}

	public void setTokenMap(Map<String, Token> tokenMap) {
		this.tokenMap = tokenMap;
	}

	public String getTokenId() {
		return tokenId;
	}

	public void setTokenId(String tokenId) {
		this.tokenId = tokenId;
	}

	public String getDefaultLanguage() {
		return defaultLanguage;
	}

	public void setDefaultLanguage(String defaultLanguage) {
		this.defaultLanguage = defaultLanguage;
	}

	public String getAppMode() {
		return appMode;
	}

	public void setAppMode(String appMode) {
		this.appMode = appMode;
	}

	public String getTxnSource() {
		return txnSource;
	}

	public void setTxnSource(String txnSource) {
		this.txnSource = txnSource;
	}

	public String getIssuerBankName() {
		return issuerBankName;
	}

	public void setIssuerBankName(String issuerBankName) {
		this.issuerBankName = issuerBankName;
	}

	public String getIssuerCountry() {
		return issuerCountry;
	}

	public void setIssuerCountry(String issuerCountry) {
		this.issuerCountry = issuerCountry;
	}

	@Override
	public void setServletRequest(HttpServletRequest hReq) {
		this.request = hReq;
	}

	public String getSupportedPaymentTypeString() {
		return supportedPaymentTypeString;
	}

	public void setSupportedPaymentTypeString(String supportedPaymentTypeString) {
		this.supportedPaymentTypeString = supportedPaymentTypeString;
	}

	public String getUpiCustName() {
		return upiCustName;
	}

	public void setUpiCustName(String upiCustName) {
		this.upiCustName = upiCustName;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public String getPaymentsRegion() {
		return paymentsRegion;
	}

	public void setPaymentsRegion(String paymentsRegion) {
		this.paymentsRegion = paymentsRegion;
	}

	public String getCardHolderType() {
		return cardHolderType;
	}

	public void setCardHolderType(String cardHolderType) {
		this.cardHolderType = cardHolderType;
	}

	public String getVpa() {
		return vpa;
	}

	public void setVpa(String vpa) {
		this.vpa = vpa;
	}

}
