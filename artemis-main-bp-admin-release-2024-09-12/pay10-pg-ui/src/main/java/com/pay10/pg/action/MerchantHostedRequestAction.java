package com.pay10.pg.action;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import com.pay10.commons.dto.PassbookPODTO;
import com.pay10.commons.util.*;
import com.pay10.requestrouter.RequestRouter;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.opensymphony.xwork2.Action;
import com.pay10.commons.api.BindbControllerServiceProvider;
import com.pay10.commons.dao.FieldsDao;
import com.pay10.commons.dto.MerchantWalletPODTO;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.mongo.MerchantWalletPODao;
import com.pay10.commons.user.AccountCurrencyRegion;
import com.pay10.commons.user.CardHolderType;
import com.pay10.commons.user.MerchantKeySaltDao;
import com.pay10.commons.user.MultCurrencyCodeDao;
import com.pay10.commons.user.PayoutBankCodeconfigurationDao;
import com.pay10.commons.user.TdrSettingDao;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.pg.action.service.ActionService;
import com.pay10.pg.action.service.RequestUrlValidator;
import com.pay10.pg.core.pageintegrator.GeneralValidator;
import com.pay10.pg.core.util.PayoutUpdateProcessor;
import com.pay10.pg.core.util.RequestCreator;
import com.pay10.pg.core.util.ResponseCreator;
import com.pay10.pg.core.util.TransactionResponser;
import com.pay10.pg.core.util.UpdateProcessor;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MerchantHostedRequestAction extends AbstractSecureAction implements ServletRequestAware {
	private static final long serialVersionUID = -1896215615007334194L;
	private static final Logger logger = LoggerFactory.getLogger(MerchantHostedRequestAction.class.getName());
	private static final String pendingTxnStatus = "Sent to Bank-Enrolled";
	private HttpServletRequest request;
	@Autowired
	private BindbControllerServiceProvider binService;
	@Autowired
	private Fields field;
	@Autowired
	private ResponseCreator responseCreator;
	@Autowired
	private PayoutBankCodeconfigurationDao payoutBankCodeconfigurationDao;
	@Autowired
	private MultCurrencyCodeDao multCurrencyCodeDao;
	@Autowired
	private TransactionResponser transactionResponser;
	@Autowired
	private RequestCreator requestCreator;
	@Autowired
	private GeneralValidator generalValidator;
	@Autowired
	private UserDao userDao;
	@Autowired
	private UpdateProcessor updateProcessor;
	@Autowired
	private MerchantWalletPODao merchantWalletPODao;
	@Autowired
	private PayoutUpdateProcessor payoutUpdateProcessor;
	@Autowired
	private ActionService actionService;
	@Autowired
	private RequestUrlValidator requestUrlValidator;
	@Autowired
	private TdrSettingDao dao;
	@Autowired
	private StaticDataProvider staticDataProvider;
	@Autowired
	private MerchantKeySaltDao merchantKeySaltDao;
	@Autowired
	private FieldsDao fieldsDao;
	@Autowired
	private MacUtil macUtil;
	@JsonIgnore
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String acquirerFlag;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonIgnore
	private String businessName;
	@JsonIgnore
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String pgRefNum;
	@JsonIgnore
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String responseCode;
	@JsonProperty("ENCDATA")
	private String ENCDATA;
	@JsonProperty("PAY_ID")
	private String PAY_ID;
	@Autowired
	private RequestRouter requestRouter;

	public String execute() {
		Fields fields = null;
		try {
			// clean session
			sessionMap.invalidate();

			// create new fields for direct transaction; decrypt fields
			logger.info("New Merchant hosted Payment request [Header values] : {}", request.getParameterMap());
			fields = actionService.prepareFieldsMerchantHosted(request.getParameterMap());
			logger.info("New Merchant hosted Payment request Raw Request Fields, merchant hosted:{}", fields.getFieldsAsString());
			logger.info("before addTransactionExtension PayId:{}, ORDER_ID:{}", fields.get(FieldType.PAY_ID.getName()), fields.get(FieldType.ORDER_ID.getName()));
			if (fields.get(FieldType.UDF13.getName()) != null
					&& fields.get(FieldType.UDF13.getName()).equalsIgnoreCase("PO")) {
				return processPayoutRequest(fields, request);
			}

			fieldsDao.addTransactionExtensionn(fields.get(FieldType.PAY_ID.getName()), fields.get(FieldType.ORDER_ID.getName()));
			logger.info("after addTransactionExtension PayId:{}, ORDER_ID:{}", fields.get(FieldType.PAY_ID.getName()), fields.get(FieldType.ORDER_ID.getName()));
			String accept = request.getHeader("accept");
			String userAgent = request.getHeader("user-agent");
			String javaEnabled = fields.get(FieldType.BROWSER_JAVA_ENABLED.getName());
			String language = fields.get(FieldType.BROWSER_LANG.getName());
			String timezoneOffset = fields.get(FieldType.BROWSER_TZ.getName());
			String colorDepth = fields.get(FieldType.BROWSER_COLOR_DEPTH.getName());
			String screenWidth = fields.get(FieldType.BROWSER_SCREEN_WIDTH.getName());
			String screenHeight = fields.get(FieldType.BROWSER_SCREEN_HEIGHT.getName());
			logger.info("accept:{}, userAgent:{}, javaEnabled:{}, language:{}, timezoneOffset:{}, colorDepth:{}, screenWidth:{}, screenHeight:{}",
					accept, userAgent, javaEnabled, language, timezoneOffset, colorDepth, screenWidth, screenHeight);
			payInRequestParameterValidation(fields);
			if (StringUtils.isBlank(fields.get(FieldType.HASH.getName()))) {
				throw new SystemException(ErrorType.VALIDATION_FAILED, "Invalid " + FieldType.HASH.getName());
			}

			generalValidator.validateReturnUrl(fields);
			generalValidator.validateHash(fields);

			fields.put(FieldType.IS_MERCHANT_HOSTED.getName(), "Y");
			sessionMap.put((FieldType.IS_MERCHANT_HOSTED.getName()), "Y");
			String fieldsAsString = fields.getFieldsAsBlobString();
			sessionMap.put(Constants.FIELDS.getValue(), fields);
			fields.put(FieldType.INTERNAL_REQUEST_FIELDS.getName(), fieldsAsString);

			User user = userDao.getUserClass(fields.get(FieldType.PAY_ID.getName()));
			if (ObjectUtils.isEmpty(user) || user.getUserStatus() != UserStatusType.ACTIVE) {
				throw new SystemException(ErrorType.VALIDATION_FAILED, "User Inactive");
			}

			User user1 = userDao.findPayId(fields.get(FieldType.PAY_ID.getName()));
			/* EKYC code added start here */
			// Check if EKYC Flag is enable and verified then transaction process further
			// other wise transaction is not processed
			if (user1.isEkycFlag()) {
				logger.info("EKYC :{}", user1.isEkycFlag());
				// Get TxnId from UDF12 field which is used to get txnId only.
				String txnId = fields.get(FieldType.UDF12.getName());
				String mobileNumber = fields.get(FieldType.CUST_PHONE.getName());

				// Call new Wrapper API for getting E-KYC detail of users
				String statusCode = callVerificationEkycApi(txnId, mobileNumber, request);
				logger.info("Ekyc Final Status Code is *** :{}", statusCode);
				if (statusCode.equalsIgnoreCase("1000")) {
					logger.info("Ekyc Transaction can processed further success");
				} else if (statusCode.equalsIgnoreCase("1011")) {
					throw new SystemException(ErrorType.EKYC_DETAILS_NOT_FOUND,
							ErrorType.EKYC_DETAILS_NOT_FOUND.getResponseMessage());
				} else if (statusCode.equalsIgnoreCase("0000")) {
					logger.info("Ekyc Verification is Required.");
					throw new SystemException(ErrorType.EKYC_VERIFICATION,
							ErrorType.EKYC_VERIFICATION.getResponseMessage());
				} else {
					logger.info("verification server is down or Exception occurs {}", statusCode);
					throw new SystemException(ErrorType.EKYC_SERVICE_DOWN,
							ErrorType.EKYC_SERVICE_DOWN.getResponseMessage());
				}
			}

			if (StringUtils.isNotBlank(user.getResellerId())) {
				String resellerId = user.getResellerId();
				fields.put((FieldType.RESELLER_ID.getName()), resellerId);
			}

			sessionMap.put((FieldType.INTERNAL_HEADER_USER_AGENT.getName()), request.getHeader("User-Agent"));
			if (StringUtils.isNotBlank((String) sessionMap.get(FieldType.INTERNAL_HEADER_USER_AGENT.getName()))) {
				fields.put((FieldType.INTERNAL_HEADER_USER_AGENT.getName()),
						(String) sessionMap.get(FieldType.INTERNAL_HEADER_USER_AGENT.getName()));
			}

			MerchantPaymentType merchantPaymentType = MerchantPaymentType.getInstanceFromCode(fields.get(FieldType.PAYMENT_TYPE.getName()));

			if (null == merchantPaymentType) {
				throw new SystemException(ErrorType.VALIDATION_FAILED, "Invalid payment type");
			}
			sessionMap.put((FieldType.SURCHARGE_FLAG.getName()), ((user.isSurchargeFlag()) ? "Y" : "N"));
			fields.put((FieldType.SURCHARGE_FLAG.getName()), ((user.isSurchargeFlag()) ? "Y" : "N"));

			switch (merchantPaymentType) {
				case CARD:
					// do bin check, card validation and assign mop
					// Call validateCardDetails instead of validateCardNumber.
					try {
						generalValidator.validateCardDetails(fields);
					} catch (SystemException se){
						if(se.getMessage().contains(FieldType.CARD_NUMBER.getName())){
							throw new SystemException(ErrorType.VALIDATION_FAILED, "Invalid CARD_NUMBER");
						}
						else if(se.getMessage().contains(FieldType.CARD_EXP_DT.getName())){
							throw new SystemException(ErrorType.VALIDATION_FAILED, "Invalid CARD_EXP_DT");
						}
						else if(se.getMessage().contains(FieldType.CVV.getName())){
							throw new SystemException(ErrorType.VALIDATION_FAILED, "Invalid CVV");
						} else {
							throw se;
						}
					}
					Map<String, String> binMap = binService.binfind(fields.get(FieldType.CARD_NUMBER.getName()).substring(0, 9));
					if (null == binMap || binMap.isEmpty()) {
						throw new SystemException(ErrorType.CARD_NUMBER_NOT_SUPPORTED,
								"Bin not present for card, OrderId: " + fields.get(FieldType.ORDER_ID.getName()));
					}
					fields.put(FieldType.PAYMENT_TYPE.getName(), binMap.get(FieldType.PAYMENT_TYPE.getName()));
					fields.put(FieldType.MOP_TYPE.getName(), binMap.get(FieldType.MOP_TYPE.getName()));
					fields.put(FieldType.PAYMENTS_REGION.getName(), binMap.get(FieldType.PAYMENTS_REGION.getName()));
					fields.put(FieldType.CARD_HOLDER_TYPE.getName(), binMap.get(FieldType.CARD_HOLDER_TYPE.getName()));
					fields.put(FieldType.INTERNAL_CARD_ISSUER_BANK.getName(), binMap.get(FieldType.INTERNAL_CARD_ISSUER_BANK.getName()));
					fields.put(FieldType.INTERNAL_CARD_ISSUER_COUNTRY.getName(), binMap.get(FieldType.INTERNAL_CARD_ISSUER_COUNTRY.getName()));
					sessionMap.put((FieldType.CARD_NUMBER.getName()), fields.get(FieldType.CARD_NUMBER.getName()));
					sessionMap.put((FieldType.CARD_EXP_DT.getName()), fields.get(FieldType.CARD_EXP_DT.getName()));
					sessionMap.put((FieldType.CVV.getName()), fields.get(FieldType.CVV.getName()));
					sessionMap.put((FieldType.INTERNAL_CARD_ISSUER_BANK.getName()), fields.get(FieldType.INTERNAL_CARD_ISSUER_BANK.getName()));
					sessionMap.put((FieldType.INTERNAL_CARD_ISSUER_COUNTRY.getName()), fields.get(FieldType.INTERNAL_CARD_ISSUER_COUNTRY.getName()));
					sessionMap.put((FieldType.PAYMENTS_REGION.getName()), binMap.get(FieldType.PAYMENTS_REGION.getName()));
					sessionMap.put((FieldType.CARD_HOLDER_TYPE.getName()), fields.get(FieldType.CARD_HOLDER_TYPE.getName()));
					break;
				case NET_BANKING:
					fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), TransactionType.SALE.getName());
					fields.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
					fields.put(FieldType.PAYMENT_TYPE.getName(), PaymentType.NET_BANKING.getCode());
					fields.put(FieldType.CARD_HOLDER_TYPE.getName(), CardHolderType.CONSUMER.toString());
					fields.put(FieldType.PAYMENTS_REGION.getName(), AccountCurrencyRegion.DOMESTIC.toString());
					sessionMap.put((FieldType.CARD_HOLDER_TYPE.getName()), CardHolderType.CONSUMER.toString());
					sessionMap.put((FieldType.PAYMENTS_REGION.getName()), AccountCurrencyRegion.DOMESTIC.toString());
					break;
				case UPI:
					fields.put(FieldType.PAYMENT_TYPE.getName(), PaymentType.UPI.getCode());
					fields.put(FieldType.MOP_TYPE.getName(), MopType.UPI.getCode());
					fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), TransactionType.SALE.getName());
					fields.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
					fields.put(FieldType.CARD_HOLDER_TYPE.getName(), CardHolderType.CONSUMER.toString());
					fields.put(FieldType.PAYMENTS_REGION.getName(), AccountCurrencyRegion.DOMESTIC.toString());
					sessionMap.put((FieldType.CARD_HOLDER_TYPE.getName()), CardHolderType.CONSUMER.toString());
					sessionMap.put((FieldType.PAYMENTS_REGION.getName()), AccountCurrencyRegion.DOMESTIC.toString());
					sessionMap.put((FieldType.PAYER_ADDRESS.getName()), fields.get(FieldType.PAYER_ADDRESS.getName()));
					fields.put(FieldType.CARD_MASK.getName(), fields.get(FieldType.PAYER_ADDRESS.getName()));
					break;
				case WALLET:
					fields.put(FieldType.PAYMENT_TYPE.getName(), PaymentType.WALLET.getCode());
					fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), TransactionType.SALE.getName());
					fields.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
					fields.put(FieldType.CARD_HOLDER_TYPE.getName(), CardHolderType.CONSUMER.toString());
					fields.put(FieldType.PAYMENTS_REGION.getName(), AccountCurrencyRegion.DOMESTIC.toString());
					sessionMap.put((FieldType.CARD_HOLDER_TYPE.getName()), CardHolderType.CONSUMER.toString());
					sessionMap.put((FieldType.PAYMENTS_REGION.getName()), AccountCurrencyRegion.DOMESTIC.toString());
					break;
			}

			// unsupported/invalid payment type received from merchant

			// Map payment type from merchant to PG payment type

			String origTxnType = ModeType.getDefaultPurchaseTransaction(user.getModeType()).getName();
			sessionMap.put((FieldType.INTERNAL_ORIG_TXN_TYPE.getName()), ModeType.getDefaultPurchaseTransaction(user.getModeType()).getName());
			fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), origTxnType);

			// put original transaction type

			sessionMap.put((FieldType.RETURN_URL.getName()), fields.get(FieldType.RETURN_URL.getName()));

			// --------- added by sonu for IP ------------
			String ipAddress = request.getHeader("X-FORWARDED-FOR");
			if (ipAddress == null) {
				ipAddress = request.getRemoteAddr();
				if (ipAddress == null) {
					logger.info("IP not received from both X-FORWARDED-FOR :{}, Remote Address :{}"
							, request.getHeader("X-FORWARDED-FOR")
							, request.getRemoteAddr());
				}
				fields.put((FieldType.INTERNAL_CUST_IP.getName()), ipAddress);
			} else {
				fields.put((FieldType.INTERNAL_CUST_IP.getName()), ipAddress);
			}
			fields.put(FieldType.INTERNAL_CUST_MAC.getName(), macUtil.getMackByIp(ipAddress));
			// ------------ Completed ----------------------

			String countryCode = request.getHeader("CloudFront-Viewer-Country");
			logger.info("Header value {}", request.getHeader("User-Agent"));
			logger.info("CloudFront-Is-Mobile-Viewer {}", request.getHeader("CloudFront-Is-Mobile-Viewer"));
			logger.info("CloudFront-Is-Tablet-Viewer {}", request.getHeader("CloudFront-Is-Tablet-Viewer"));
			logger.info("CloudFront-Is-SmartTV-Viewer {}", request.getHeader("CloudFront-Is-SmartTV-Viewer"));
			logger.info("CloudFront-Is-Desktop-Viewer {}", request.getHeader("CloudFront-Is-Desktop-Viewer"));
			if (StringUtils.isBlank(countryCode)) {
				countryCode = "NA";
			}

			String domain = "NA";
			if (StringUtils.isNotBlank(request.getHeader("Referer"))) {
				try {
					domain = new URL(request.getHeader("Referer")).getHost();
					logger.info("Internal_Cust_Domain: domain name is {} request referer is {}", domain, request.getHeader("Referer"));
				} catch (Exception e) {
					domain = "NA";
				}
			}

			fields.put((FieldType.INTERNAL_CUST_DOMAIN.getName()), domain);
			fields.put((FieldType.INTERNAL_CUST_COUNTRY_NAME.getName()), countryCode);
			sessionMap.put(FieldType.INTERNAL_CUST_COUNTRY_NAME.getName(), fields.get(FieldType.INTERNAL_CUST_COUNTRY_NAME.getName()));
			requestUrlValidator.validateRequestUrl(fields, request);
			setBusinessName(user.getBusinessName());
			fields.put(FieldType.TXNTYPE.getName(), TransactionType.ENROLL.getName());
			fields.put(FieldType.INTERNAL_REQUEST_FIELDS.getName(), fieldsAsString);
			fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), origTxnType);
			fields.put(FieldType.INTERNAL_VALIDATE_HASH_YN.getName(), "N");
			// Put merchant hosted page flag
			fields.put(FieldType.IS_MERCHANT_HOSTED.getName(), "Y");
			sessionMap.put(FieldType.IS_MERCHANT_HOSTED.getName(), "Y");
			// Put transaction id as original txnId for subsequent transactions
			fields.put(FieldType.INTERNAL_ORIG_TXN_ID.getName(), fields.get(FieldType.ORIG_TXN_ID.getName()));
			fields.remove(FieldType.ORIG_TXN_ID.getName());
			// Put ORIG_TXN_ID in session for updating transaction status in case of timeout
			sessionMap.put(FieldType.INTERNAL_ORIG_TXN_ID.getName(), fields.get(FieldType.INTERNAL_ORIG_TXN_ID.getName()));
			// handle surcharge flg and amount
			handleTransactionCharges(fields);
			sessionMap.put(FieldType.INTERNAL_CUST_IP.getName(), fields.get(FieldType.INTERNAL_CUST_IP.getName()));

			Map<String, String> response;
			// TFP request mandatory fields
			if(StringUtils.isNotEmpty(userAgent)) {
				fields.put(FieldType.BROWSER_USER_AGENT.getName(), userAgent);
			}
			if(StringUtils.isNotEmpty(language)) {
				fields.put(FieldType.BROWSER_LANG.getName(), language);
			}
			if(StringUtils.isNotEmpty(javaEnabled)) {
				fields.put(FieldType.BROWSER_JAVA_ENABLED.getName(), javaEnabled);
			}
			if(StringUtils.isNotEmpty(colorDepth)) {
				fields.put(FieldType.BROWSER_COLOR_DEPTH.getName(), colorDepth);
			}
			if(StringUtils.isNotEmpty(screenWidth)) {
				fields.put(FieldType.BROWSER_SCREEN_WIDTH.getName(), screenWidth);
			}
			if(StringUtils.isNotEmpty(screenHeight)) {
				fields.put(FieldType.BROWSER_SCREEN_HEIGHT.getName(), screenHeight);
			}
			if(StringUtils.isNotEmpty(timezoneOffset)) {
				fields.put(FieldType.BROWSER_TZ.getName(), timezoneOffset);
			}
			if(StringUtils.isNotEmpty(accept)) {
				fields.put(FieldType.BROWSER_ACCEPT_HEADER.getName(), accept);
			}
			fields.logAllFields("MGH fields");

			response = callProcessPayment(fields);

			logger.info("response from Pgws {}", response);
			Fields responseMap = new Fields(response);
			responseMap.logAllFieldsUsingMasking("Response received from pgws :");
			sessionMap.put(Constants.FIELDS.getValue(), responseMap);
			sessionMap.put(FieldType.INTERNAL_ORIG_TXN_ID.getName(), responseMap.get(FieldType.TXN_ID.getName()));
			sessionMap.put(FieldType.PG_REF_NUM.getName(), responseMap.get(FieldType.PG_REF_NUM.getName()));
			sessionMap.put(FieldType.ORDER_ID.getName(), responseMap.get(FieldType.ORDER_ID.getName()));
			responseMap.put(FieldType.IS_MERCHANT_HOSTED.getName(), "Y");
			String status = responseMap.get(FieldType.STATUS.getName());
			String txnType = responseMap.get(FieldType.PAYMENT_TYPE.getName());
			String acquirer = responseMap.get(FieldType.ACQUIRER_TYPE.getName());
			sessionMap.put(FieldType.ACQUIRER_TYPE.getName(), acquirer);

			if (fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(PaymentType.UPI.getCode())
					&& status.equals(StatusType.SENT_TO_BANK.getName())
					&& ((acquirer.equalsIgnoreCase(AcquirerType.BILLDESK.getCode()))
					|| (acquirer.equalsIgnoreCase(AcquirerType.FREECHARGE.getCode()))
					|| (acquirer.equalsIgnoreCase(AcquirerType.YESBANKCB.getCode()))
					|| (acquirer.equalsIgnoreCase(AcquirerType.COSMOS.getCode()))
					|| (acquirer.equalsIgnoreCase(AcquirerType.SBI.getCode())))) {
				setPgRefNum(responseMap.get(FieldType.PG_REF_NUM.getName()));
				setResponseCode(responseMap.get(FieldType.RESPONSE_CODE.getName()));

				sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
				sessionMap.put(FieldType.PASSWORD.getName(), responseMap.get(FieldType.PASSWORD.getName()));
				sessionMap.put(FieldType.MERCHANT_ID.getName(), responseMap.get(FieldType.MERCHANT_ID.getName()));
				fields.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getCode());
				return "upiLoader";
			}

			if ((status.equals(StatusType.ENROLLED.getName())) || (status.equals(StatusType.CAPTURED.getName()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName()) && txnType.equals(PaymentType.NET_BANKING.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName()) && acquirer.equals(AcquirerType.BOB.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName()) && acquirer.equals(AcquirerType.KOTAK.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName()) && acquirer.equals(AcquirerType.IDBIBANK.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName()) && acquirer.equals(AcquirerType.MATCHMOVE.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName()) && acquirer.equals(AcquirerType.ATL.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName()) && acquirer.equals(AcquirerType.DIRECPAY.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName()) && acquirer.equals(AcquirerType.ATOM.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName()) && acquirer.equals(AcquirerType.APBL.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName()) && acquirer.equals(AcquirerType.MOBIKWIK.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName()) && acquirer.equals(AcquirerType.ISGPAY.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName()) && acquirer.equals(AcquirerType.AXISBANK.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName()) && acquirer.equals(AcquirerType.SHIVALIKNBBANK.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName()) && acquirer.equals(AcquirerType.INGENICO.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName()) && acquirer.equals(AcquirerType.PAYTM.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName()) && acquirer.equals(AcquirerType.PAYU.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName()) && acquirer.equals(AcquirerType.PHONEPE.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName()) && acquirer.equals(AcquirerType.ICICIBANK.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName()) && acquirer.equals(AcquirerType.LYRA.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName()) && acquirer.equals(AcquirerType.CASHFREE.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName()) && acquirer.equals(AcquirerType.PAY10.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName()) && acquirer.equals(AcquirerType.DEMO.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName()) && acquirer.equals(AcquirerType.IDFC.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName()) && acquirer.equals(AcquirerType.PAYMENTAGE.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName()) && acquirer.equals(AcquirerType.JAMMU_AND_KASHMIR.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName()) && acquirer.equals(AcquirerType.PINELABS.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName()) && acquirer.equals(AcquirerType.EASEBUZZ.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName()) && acquirer.equals(AcquirerType.AGREEPAY.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName()) && acquirer.equals(AcquirerType.SBI.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName()) && acquirer.equals(AcquirerType.TFP.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName()) && acquirer.equals(AcquirerType.SBICARD.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName()) && acquirer.equals(AcquirerType.KOTAK_CARD.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName()) && acquirer.equals(AcquirerType.SBINB.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName()) && acquirer.equals(AcquirerType.TMBNB.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName()) && acquirer.equals(AcquirerType.NB_FEDERAL.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName()) && acquirer.equals(AcquirerType.YESBANKNB.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName()) && acquirer.equals(AcquirerType.CANARANBBANK.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName()) && acquirer.equals(AcquirerType.HTPAY.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName()) && acquirer.equals(AcquirerType.CITYUNIONBANK.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName()) && acquirer.equals(AcquirerType.CAMSPAY.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName()) && acquirer.equals(AcquirerType.FSS.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName()) && acquirer.equals(AcquirerType.QUOMO.getCode()))
					|| (status.equals(StatusType.PENDING.getName()) && txnType.equals(PaymentType.UPI.getName()))) {

				// To check if acquirer is Citrus if acquirer null return
				// nothing and check capture status

				logger.info("acquirer from Pgws {}", acquirer);

				if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.ICICI_FIRSTDATA.getCode())) {
					sessionMap.put(FieldType.CVV.getName(), fields.get(FieldType.CVV.getName()));
					requestCreator.FirstDataEnrollRequest(responseMap);
				} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.IDFC_FIRSTDATA.getCode())) {
					sessionMap.put(FieldType.CVV.getName(), fields.get(FieldType.CVV.getName()));
					requestCreator.FirstDataEnrollRequest(responseMap);
				} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.YESBANKCB.getCode())) {
					sessionMap.put(FieldType.CVV.getName(), fields.get(FieldType.CVV.getName()));
					sessionMap.put(FieldType.CARD_NUMBER.getName(), fields.get(FieldType.CARD_NUMBER.getName()));
					sessionMap.put(FieldType.CARD_EXP_DT.getName(), fields.get(FieldType.CARD_EXP_DT.getName()));
					requestCreator.cSourceEnrollRequest(responseMap);
				} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.ICICI_MPGS.getCode())) {
					sessionMap.put(FieldType.CVV.getName(), fields.get(FieldType.CVV.getName()));
					sessionMap.put(FieldType.CARD_NUMBER.getName(), fields.get(FieldType.CARD_NUMBER.getName()));
					sessionMap.put(FieldType.CARD_EXP_DT.getName(), fields.get(FieldType.CARD_EXP_DT.getName()));
					requestCreator.iciciMpgsEnrollRequest(responseMap);
				} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.HDFC.getCode())) {
					if (txnType.equals(PaymentType.UPI.getName())) {

					} else {
						requestCreator.EnrollRequest(responseMap);
					}
				} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.ICICIBANK.getCode())) {
					sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
					requestCreator.generateIciciNBRequest(responseMap);
				} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.FEDERAL.getCode())) {
					sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
					requestCreator.generateFederalRequest(responseMap);
				} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.NB_FEDERAL.getCode())) {
					sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
					sessionMap.put(FieldType.ADF1.getName(), responseMap.get(FieldType.ADF1.getName()));
					sessionMap.put(FieldType.ADF2.getName(), responseMap.get(FieldType.ADF2.getName()));
					sessionMap.put(FieldType.ADF3.getName(), responseMap.get(FieldType.ADF3.getName()));
					sessionMap.put(FieldType.PG_REF_NUM.getName(), responseMap.get(FieldType.PG_REF_NUM.getName()));
					requestCreator.generateFederalBankNBRequest(responseMap);
				} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.BOB.getCode())) {
					sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
					requestCreator.generateBobRequest(responseMap);
				} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.ISGPAY.getCode())) {
					sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
					sessionMap.put(FieldType.PASSWORD.getName(), responseMap.get(FieldType.ADF5.getName()));
					sessionMap.put(FieldType.ADF4.getName(), responseMap.get(FieldType.ADF4.getName()));
					requestCreator.generateIsgpayRequest(responseMap);
				} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.PAY10.getCode())) {
					sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
					sessionMap.put(FieldType.PG_REF_NUM.getName(), responseMap.get(FieldType.PG_REF_NUM.getName()));
					sessionMap.put(FieldType.MERCHANT_ID.getName(), responseMap.get(FieldType.MERCHANT_ID.getName()));
					sessionMap.put(FieldType.ADF1.getName(), responseMap.get(FieldType.ADF1.getName()));
					requestCreator.generatePay10Request(responseMap);
				} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.TFP.getCode())) {
					logger.info("Acquirer for TFP {}", responseMap.getFieldsAsString());
					sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
					sessionMap.put(FieldType.PG_REF_NUM.getName(), responseMap.get(FieldType.PG_REF_NUM.getName()));
					sessionMap.put(FieldType.MERCHANT_ID.getName(), responseMap.get(FieldType.MERCHANT_ID.getName()));
					sessionMap.put(FieldType.ADF1.getName(), responseMap.get(FieldType.ADF1.getName()));
					requestCreator.generateNFTRequest(responseMap);
				} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.CANARANBBANK.getCode())) {
					requestCreator.generateCanaraRequest(responseMap);
				} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.PAYMENTAGE.getCode())) {
					sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
					sessionMap.put(FieldType.PG_REF_NUM.getName(), responseMap.get(FieldType.PG_REF_NUM.getName()));
					sessionMap.put(FieldType.MERCHANT_ID.getName(), responseMap.get(FieldType.MERCHANT_ID.getName()));
					sessionMap.put(FieldType.ADF1.getName(), responseMap.get(FieldType.ADF1.getName()));
					requestCreator.generatePaymentEdgeRequest(responseMap);
				} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.CITYUNIONBANK.getCode())) {
					requestCreator.generateCityUnionBankRequest(responseMap);
				} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.KOTAK_CARD.getCode())) {
					requestCreator.generateKotakRequest(responseMap);// added by abhi
				} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.CANARANBBANK.getCode())) {
					requestCreator.generateCanaraRequest(responseMap);
				} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.FSS.getCode())) {
					sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
					requestCreator.generateFssRequest(responseMap);
				} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.KOTAK.getCode())) {
					sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
					sessionMap.put(FieldType.PASSWORD.getName(), responseMap.get(FieldType.ADF2.getName()));
					sessionMap.put(FieldType.ADF2.getName(), responseMap.get(FieldType.ADF2.getName()));
					sessionMap.put(FieldType.ADF3.getName(), responseMap.get(FieldType.ADF3.getName()));
					sessionMap.put(FieldType.ADF4.getName(), responseMap.get(FieldType.ADF4.getName()));
					sessionMap.put(FieldType.ADF5.getName(), responseMap.get(FieldType.ADF5.getName()));
					sessionMap.put(FieldType.ADF10.getName(), responseMap.get(FieldType.ADF10.getName()));
					sessionMap.put(FieldType.MERCHANT_ID.getName(), responseMap.get(FieldType.MERCHANT_ID.getName()));
					requestCreator.generateKotakRequest(responseMap);
				} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.IDFC.getCode())) {
					PaymentType paymentType = PaymentType.getInstanceUsingCode(fields.get(FieldType.PAYMENT_TYPE.getName()));
					switch (paymentType) {
						case NET_BANKING:
							requestCreator.generateidfcBankNBRequest(responseMap);
							break;
						default:
							break;
					}
				} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.JAMMU_AND_KASHMIR.getCode())) {
					sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
					sessionMap.put(FieldType.MERCHANT_ID.getName(), responseMap.get(FieldType.MERCHANT_ID.getName()));
					sessionMap.put(FieldType.ADF1.getName(), responseMap.get(FieldType.ADF1.getName()));
					requestCreator.generateJammuandKashmirRequest(responseMap);
				} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.BILLDESK.getCode())) {
					sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
					sessionMap.put(FieldType.PASSWORD.getName(), responseMap.get(FieldType.PASSWORD.getName()));
					sessionMap.put(FieldType.MERCHANT_ID.getName(), responseMap.get(FieldType.MERCHANT_ID.getName()));
					requestCreator.generateBilldeskRequest(responseMap);
				} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.CASHFREE.getCode())) {
					sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
					sessionMap.put(FieldType.MERCHANT_ID.getName(), responseMap.get(FieldType.MERCHANT_ID.getName()));
					requestCreator.generateCashfreeRequest(responseMap);
				} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.DEMO.getCode())) {
					sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
					sessionMap.put(FieldType.MERCHANT_ID.getName(), responseMap.get(FieldType.MERCHANT_ID.getName()));
					requestCreator.generateDemoRequest(responseMap);
				} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.EASEBUZZ.getCode())) {
					sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.ADF1.getName()));
					sessionMap.put(FieldType.MERCHANT_ID.getName(), responseMap.get(FieldType.MERCHANT_ID.getName()));
					requestCreator.generateEasebuzzRequest(responseMap);
				} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.SBI.getCode())) {
					sessionMap.put(FieldType.MERCHANT_ID.getName(), responseMap.get(FieldType.MERCHANT_ID.getName()));
					if (!fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase("NB")) {
						responseMap.put(FieldType.CARD_NUMBER.getName(),
								RegExUtils.replaceAll(fields.get(FieldType.CARD_NUMBER.getName()), " ", ""));
						responseMap.put(FieldType.PAYMENTS_REGION.getName(), fields.get(FieldType.PAYMENTS_REGION.getName()));
						responseMap.put(FieldType.CARD_HOLDER_TYPE.getName(), fields.get(FieldType.CARD_HOLDER_TYPE.getName()));
						String expDate = fields.get(FieldType.CARD_EXP_DT.getName());
						String expMonth = expDate.substring(0, 2);
						String expYear = expDate.substring(2, 6);
						requestCreator.generateSbiRequest(responseMap, null, expMonth, expYear);
					} else {
						requestCreator.generateSbiRequest(responseMap, null, null, null);
					}
				} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.SBICARD.getCode())) {
					sessionMap.put(FieldType.MERCHANT_ID.getName(), responseMap.get(FieldType.MERCHANT_ID.getName()));
					if (!fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase("NB")) {
						sessionMap.put(FieldType.MERCHANT_ID.getName(), responseMap.get(FieldType.MERCHANT_ID.getName()));
						sessionMap.put(FieldType.CARD_NUMBER.getName(), fields.get(FieldType.CARD_NUMBER.getName()));
						sessionMap.put(FieldType.CVV.getName(), fields.get(FieldType.CVV.getName()));
						sessionMap.put(FieldType.CARD_HOLDER_NAME.getName(), fields.get(FieldType.CARD_HOLDER_NAME.getName()));
						sessionMap.put(FieldType.CARD_EXP_DT.getName(), fields.get(FieldType.CARD_EXP_DT.getName()));
						responseMap.put(FieldType.CARD_NUMBER.getName(),
								RegExUtils.replaceAll(fields.get(FieldType.CARD_NUMBER.getName()), " ", ""));
						responseMap.put(FieldType.PAYMENTS_REGION.getName(), fields.get(FieldType.PAYMENTS_REGION.getName()));
						responseMap.put(FieldType.CARD_HOLDER_TYPE.getName(), fields.get(FieldType.CARD_HOLDER_TYPE.getName()));
						String expDate = fields.get(FieldType.CARD_EXP_DT.getName());
						String expMonth = expDate.substring(0, 2);
						String expYear = expDate.substring(2, 6);
						requestCreator.generateSbiRequest(responseMap, null, expMonth, expYear);
					} else {
						sessionMap.put(FieldType.MERCHANT_ID.getName(), responseMap.get(FieldType.MERCHANT_ID.getName()));
						sessionMap.put(FieldType.CARD_NUMBER.getName(), fields.get(FieldType.CARD_NUMBER.getName()));
						sessionMap.put(FieldType.CVV.getName(), fields.get(FieldType.CVV.getName()));
						sessionMap.put(FieldType.CARD_HOLDER_NAME.getName(), fields.get(FieldType.CARD_HOLDER_NAME.getName()));
						sessionMap.put(FieldType.CARD_EXP_DT.getName(), fields.get(FieldType.CARD_EXP_DT.getName()));
						requestCreator.generateSbiRequest(responseMap, null, null, null);
					}
				} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.SBINB.getCode())) {
					sessionMap.put(FieldType.MERCHANT_ID.getName(), responseMap.get(FieldType.MERCHANT_ID.getName()));
					requestCreator.generateSbiRequest(responseMap, null, null, null);
				} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.AGREEPAY.getCode())) {
					sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
					sessionMap.put(FieldType.MERCHANT_ID.getName(), responseMap.get(FieldType.MERCHANT_ID.getName()));
					// added by Radhe
					sessionMap.put(FieldType.ADF1.getName(), responseMap.get(FieldType.ADF1.getName()));
					requestCreator.generateAgreepayRequest(responseMap);
				} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.PINELABS.getCode())) {
					sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
					sessionMap.put(FieldType.MERCHANT_ID.getName(), responseMap.get(FieldType.MERCHANT_ID.getName()));
					sessionMap.put(FieldType.ADF1.getName(), responseMap.get(FieldType.ADF1.getName()));
					sessionMap.put(FieldType.ADF10.getName(), responseMap.get(FieldType.ADF10.getName()));
					requestCreator.generatePinelabsRequest(responseMap);
				} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.YESBANKNB.getCode())) {
					sessionMap.put(FieldType.PG_REF_NUM.getName(), responseMap.get(FieldType.PG_REF_NUM.getName()));
					sessionMap.put(FieldType.ADF1.getName(), responseMap.get(FieldType.ADF1.getName()));
					sessionMap.put(FieldType.ADF2.getName(), responseMap.get(FieldType.ADF2.getName()));
					sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
					sessionMap.put(FieldType.MERCHANT_ID.getName(), responseMap.get(FieldType.MERCHANT_ID.getName()));
					sessionMap.put(FieldType.ADF10.getName(), responseMap.get(FieldType.ADF10.getName()));
					requestCreator.generateYesBankNBRequest(responseMap);
				} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.DIRECPAY.getCode())) {
					PaymentType paymentType = PaymentType.getInstanceUsingCode(fields.get(FieldType.PAYMENT_TYPE.getName()));
					switch (paymentType) {
						case NET_BANKING:
						case CREDIT_CARD:
						case DEBIT_CARD:
							sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
							sessionMap.put(FieldType.PG_REF_NUM.getName(), responseMap.get(FieldType.PG_REF_NUM.getName()));
							requestCreator.generateDirecpayRequest(responseMap);
							break;
						default:
							break;
					}
				} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.TMBNB.getCode())) {
					PaymentType paymentType = PaymentType.getInstanceUsingCode(fields.get(FieldType.PAYMENT_TYPE.getName()));
					switch (paymentType) {
						case NET_BANKING:
							logger.info("TMB NB BANK responseMap:{}", responseMap.getFieldsAsString());
							requestCreator.generateTMBRequest(responseMap);
							break;
						default:
							break;
					}
				} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.HTPAY.getCode())) {
					PaymentType paymentType = PaymentType.getInstanceUsingCode(fields.get(FieldType.PAYMENT_TYPE.getName()));
					switch (paymentType) {
						case NET_BANKING:
							requestCreator.generateHtpayRequest(responseMap);
							break;
						default:
							break;
					}
				} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.ATOM.getCode())) {
					PaymentType paymentType = PaymentType.getInstanceUsingCode(fields.get(FieldType.PAYMENT_TYPE.getName()));
					switch (paymentType) {
						case NET_BANKING:
                        case UPI:
                        case WALLET:
                            sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.ADF4.getName()));
							sessionMap.put(FieldType.PG_REF_NUM.getName(), responseMap.get(FieldType.PG_REF_NUM.getName()));
							sessionMap.put(FieldType.IV.getName(), responseMap.get(FieldType.ADF3.getName()));
							sessionMap.put(FieldType.RESP_IV.getName(), responseMap.get(FieldType.ADF5.getName()));
							sessionMap.put(FieldType.RESP_TXN_KEY.getName(), responseMap.get(FieldType.ADF8.getName()));
							requestCreator.generateAtomRequest(responseMap);
							break;
                        case CREDIT_CARD:
						case DEBIT_CARD:
							sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
							sessionMap.put(FieldType.PG_REF_NUM.getName(), responseMap.get(FieldType.PG_REF_NUM.getName()));
							sessionMap.put(FieldType.IV.getName(), responseMap.get(FieldType.ADF3.getName()));
							sessionMap.put(FieldType.RESP_IV.getName(), responseMap.get(FieldType.ADF5.getName()));
							sessionMap.put(FieldType.RESP_TXN_KEY.getName(), responseMap.get(FieldType.ADF8.getName()));
							requestCreator.generateAtomRequest(responseMap);
							break;
						default:
							break;
					}
				} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.QUOMO.getCode())) {
					PaymentType paymentType = PaymentType.getInstanceUsingCode(fields.get(FieldType.PAYMENT_TYPE.getName()));
					switch (paymentType) {
						case NET_BANKING:
                        case CREDIT_CARD:
                        case DEBIT_CARD:
                        case WALLET:
                            sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.ADF4.getName()));
							sessionMap.put(FieldType.PG_REF_NUM.getName(), responseMap.get(FieldType.PG_REF_NUM.getName()));
							sessionMap.put(FieldType.IV.getName(), responseMap.get(FieldType.ADF3.getName()));
							sessionMap.put(FieldType.RESP_IV.getName(), responseMap.get(FieldType.ADF5.getName()));
							sessionMap.put(FieldType.RESP_TXN_KEY.getName(), responseMap.get(FieldType.ADF8.getName()));
							requestCreator.generateQuomoRequest(responseMap);
							break;
                        default:
							break;
					}
				} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.INGENICO.getCode())) {
					PaymentType paymentType = PaymentType.getInstanceUsingCode(fields.get(FieldType.PAYMENT_TYPE.getName()));
					switch (paymentType) {
						case CREDIT_CARD:
						case DEBIT_CARD:
							sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
							sessionMap.put(FieldType.PG_REF_NUM.getName(), responseMap.get(FieldType.PG_REF_NUM.getName()));
							sessionMap.put(FieldType.IV.getName(), responseMap.get(FieldType.PASSWORD.getName()));
							requestCreator.generateIngenicoRequest(responseMap);
							break;
						default:
							break;
					}
				} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.APBL.getCode())) {
					PaymentType paymentType = PaymentType.getInstanceUsingCode(fields.get(FieldType.PAYMENT_TYPE.getName()));
					switch (paymentType) {
						case NET_BANKING:
							sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.ADF4.getName()));
							sessionMap.put(FieldType.PG_REF_NUM.getName(), responseMap.get(FieldType.PG_REF_NUM.getName()));
							sessionMap.put(FieldType.IV.getName(), responseMap.get(FieldType.ADF3.getName()));
							sessionMap.put(FieldType.RESP_IV.getName(), responseMap.get(FieldType.ADF5.getName()));
							sessionMap.put(FieldType.RESP_TXN_KEY.getName(), responseMap.get(FieldType.ADF8.getName()));
							requestCreator.generateApblRequest(responseMap);
							break;
						case WALLET:
							sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
							sessionMap.put(FieldType.PG_REF_NUM.getName(), responseMap.get(FieldType.PG_REF_NUM.getName()));
							sessionMap.put(FieldType.IV.getName(), responseMap.get(FieldType.ADF3.getName()));
							sessionMap.put(FieldType.RESP_IV.getName(), responseMap.get(FieldType.ADF5.getName()));
							sessionMap.put(FieldType.RESP_TXN_KEY.getName(), responseMap.get(FieldType.ADF8.getName()));
							requestCreator.generateApblRequest(responseMap);
							break;
						default:
							break;
					}
				} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.PAYTM.getCode())) {
					PaymentType paymentType = PaymentType.getInstanceUsingCode(fields.get(FieldType.PAYMENT_TYPE.getName()));
					switch (paymentType) {
						case WALLET:
							sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
							sessionMap.put(FieldType.PG_REF_NUM.getName(), responseMap.get(FieldType.PG_REF_NUM.getName()));
							sessionMap.put(FieldType.MERCHANT_ID.getName(), responseMap.get(FieldType.MERCHANT_ID.getName()));
							requestCreator.generatePaytmRequest(responseMap);
							break;
						default:
							break;
					}
				} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.PAYU.getCode())) {
					PaymentType paymentType = PaymentType.getInstanceUsingCode(fields.get(FieldType.PAYMENT_TYPE.getName()));
					sessionMap.put(FieldType.CUST_NAME.getName(), responseMap.get(FieldType.CUST_NAME.getName()));
					switch (paymentType) {
						case WALLET:
                        case NET_BANKING:
                            sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
							sessionMap.put(FieldType.ADF3.getName(), responseMap.get(FieldType.ADF3.getName()));
							sessionMap.put(FieldType.TOTAL_AMOUNT.getName(), responseMap.get(FieldType.TOTAL_AMOUNT.getName()));
							sessionMap.put(FieldType.MERCHANT_ID.getName(), responseMap.get(FieldType.MERCHANT_ID.getName()));
							sessionMap.put(FieldType.PASSWORD.getName(), responseMap.get(FieldType.PASSWORD.getName()));
							sessionMap.put(FieldType.PG_REF_NUM.getName(), responseMap.get(FieldType.PG_REF_NUM.getName()));
							sessionMap.put(FieldType.PAYU_FINAL_REQUEST.getName(), responseMap.get(FieldType.PAYU_FINAL_REQUEST.getName()));
							requestCreator.generatePayuRequest(responseMap);
							break;
                        case CREDIT_CARD:
							sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
							sessionMap.put(FieldType.ADF3.getName(), responseMap.get(FieldType.ADF3.getName()));
							sessionMap.put(FieldType.TOTAL_AMOUNT.getName(), responseMap.get(FieldType.TOTAL_AMOUNT.getName()));
							sessionMap.put(FieldType.MERCHANT_ID.getName(), responseMap.get(FieldType.MERCHANT_ID.getName()));
							sessionMap.put(FieldType.PASSWORD.getName(), responseMap.get(FieldType.PASSWORD.getName()));
							sessionMap.put(FieldType.PG_REF_NUM.getName(), responseMap.get(FieldType.PG_REF_NUM.getName()));
							sessionMap.put(FieldType.PAYU_FINAL_REQUEST.getName(), responseMap.get(FieldType.PAYU_FINAL_REQUEST.getName()));
							requestCreator.generatePayuRequest(responseMap);
						case DEBIT_CARD:
							sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
							sessionMap.put(FieldType.ADF3.getName(), responseMap.get(FieldType.ADF3.getName()));
							sessionMap.put(FieldType.TOTAL_AMOUNT.getName(), responseMap.get(FieldType.TOTAL_AMOUNT.getName()));
							sessionMap.put(FieldType.MERCHANT_ID.getName(), responseMap.get(FieldType.MERCHANT_ID.getName()));
							sessionMap.put(FieldType.PASSWORD.getName(), responseMap.get(FieldType.PASSWORD.getName()));
							sessionMap.put(FieldType.PG_REF_NUM.getName(), responseMap.get(FieldType.PG_REF_NUM.getName()));
							sessionMap.put(FieldType.PAYU_FINAL_REQUEST.getName(), responseMap.get(FieldType.PAYU_FINAL_REQUEST.getName()));
							requestCreator.generatePayuRequest(responseMap);
							break;
						case UPI:
							sessionMap.put(FieldType.MERCHANT_ID.getName(), responseMap.get(FieldType.MERCHANT_ID.getName()));
							sessionMap.put(FieldType.PASSWORD.getName(), responseMap.get(FieldType.PASSWORD.getName()));
							sessionMap.put(FieldType.PG_REF_NUM.getName(), responseMap.get(FieldType.PG_REF_NUM.getName()));
							sessionMap.put(FieldType.PAYU_FINAL_REQUEST.getName(), responseMap.get(FieldType.PAYU_FINAL_REQUEST.getName()));
							sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
							sessionMap.put(FieldType.ADF3.getName(), responseMap.get(FieldType.ADF3.getName()));
							sessionMap.put(FieldType.TOTAL_AMOUNT.getName(), responseMap.get(FieldType.TOTAL_AMOUNT.getName()));
							requestCreator.generatePayuRequest(responseMap);
							break;
						default:
							break;
					}
				} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.SHIVALIKNBBANK.getCode())) {
					PaymentType paymentType = PaymentType.getInstanceUsingCode(fields.get(FieldType.PAYMENT_TYPE.getName()));
					switch (paymentType) {
						case NET_BANKING:
							logger.info("SHIVALIK NB BANK responseMap:{}", responseMap.getFieldsAsString());
							requestCreator.generateShivalikRequest(responseMap);
							break;
						default:
							break;
					}
				} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.CAMSPAY.getCode())) {
					PaymentType paymentType = PaymentType.getInstanceUsingCode(fields.get(FieldType.PAYMENT_TYPE.getName()));
					sessionMap.put(FieldType.CUST_NAME.getName(), responseMap.get(FieldType.CUST_NAME.getName()));
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
					switch (paymentType) {
                        case NET_BANKING:
                        case UPI:
                            sessionMap.put(FieldType.MERCHANT_ID.getName(), responseMap.get(FieldType.MERCHANT_ID.getName()));
							sessionMap.put(FieldType.PASSWORD.getName(), responseMap.get(FieldType.PASSWORD.getName()));
							sessionMap.put(FieldType.PG_REF_NUM.getName(), responseMap.get(FieldType.PG_REF_NUM.getName()));
							sessionMap.put(FieldType.CAMSPAY_FINAL_REQUEST.getName(), responseMap.get(FieldType.CAMSPAY_FINAL_REQUEST.getName()));
							sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
							sessionMap.put(FieldType.TOTAL_AMOUNT.getName(), responseMap.get(FieldType.TOTAL_AMOUNT.getName()));
							requestCreator.generateCamsPayRequest(responseMap);
							break;
						case CREDIT_CARD:
							sessionMap.put(FieldType.MERCHANT_ID.getName(), responseMap.get(FieldType.MERCHANT_ID.getName()));
							sessionMap.put(FieldType.PASSWORD.getName(), responseMap.get(FieldType.PASSWORD.getName()));
							sessionMap.put(FieldType.PG_REF_NUM.getName(), responseMap.get(FieldType.PG_REF_NUM.getName()));
							sessionMap.put(FieldType.CAMSPAY_FINAL_REQUEST.getName(), responseMap.get(FieldType.CAMSPAY_FINAL_REQUEST.getName()));
							sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
							sessionMap.put(FieldType.TOTAL_AMOUNT.getName(), responseMap.get(FieldType.TOTAL_AMOUNT.getName()));
							requestCreator.generateCamsPayRequest(responseMap);
						case DEBIT_CARD:
							sessionMap.put(FieldType.MERCHANT_ID.getName(), responseMap.get(FieldType.MERCHANT_ID.getName()));
							sessionMap.put(FieldType.PASSWORD.getName(), responseMap.get(FieldType.PASSWORD.getName()));
							sessionMap.put(FieldType.PG_REF_NUM.getName(), responseMap.get(FieldType.PG_REF_NUM.getName()));
							sessionMap.put(FieldType.CAMSPAY_FINAL_REQUEST.getName(), responseMap.get(FieldType.CAMSPAY_FINAL_REQUEST.getName()));
							sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
							sessionMap.put(FieldType.TOTAL_AMOUNT.getName(), responseMap.get(FieldType.TOTAL_AMOUNT.getName()));
							requestCreator.generateCamsPayRequest(responseMap);
							break;
                        default:
							break;
					}
				} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.PHONEPE.getCode())) {
					PaymentType paymentType = PaymentType.getInstanceUsingCode(fields.get(FieldType.PAYMENT_TYPE.getName()));
					switch (paymentType) {
						case WALLET:
							sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
							sessionMap.put(FieldType.ADF1.getName(), responseMap.get(FieldType.ADF1.getName()));
							sessionMap.put(FieldType.PG_REF_NUM.getName(), responseMap.get(FieldType.PG_REF_NUM.getName()));
							sessionMap.put(FieldType.MERCHANT_ID.getName(), responseMap.get(FieldType.MERCHANT_ID.getName()));
							requestCreator.generatePhonePeRequest(responseMap);
							break;
						default:
							break;
					}
				} else if ((!StringUtils.isEmpty(acquirer)) && (acquirer.equals(AcquirerType.LYRA.getCode()))) {
					requestCreator.lyraEnrollRequest(responseMap);
				} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.MOBIKWIK.getCode())) {
					PaymentType paymentType = PaymentType.getInstanceUsingCode(fields.get(FieldType.PAYMENT_TYPE.getName()));
					switch (paymentType) {
						case WALLET:
							sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
							sessionMap.put(FieldType.PG_REF_NUM.getName(), responseMap.get(FieldType.PG_REF_NUM.getName()));
							requestCreator.generateMobikwikRequest(responseMap);
							break;
						default:
							break;
					}
				} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.AXISBANK.getCode())) {
					PaymentType paymentType = PaymentType.getInstanceUsingCode(fields.get(FieldType.PAYMENT_TYPE.getName()));
					switch (paymentType) {
						case NET_BANKING:
							sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.ADF8.getName()));
							sessionMap.put(FieldType.IV.getName(), responseMap.get(FieldType.ADF5.getName()));
							sessionMap.put(FieldType.PASSWORD.getName(), responseMap.get(FieldType.ADF4.getName()));
							sessionMap.put(FieldType.PG_REF_NUM.getName(), responseMap.get(FieldType.PG_REF_NUM.getName()));
							sessionMap.put(FieldType.ADF10.getName(), responseMap.get(FieldType.ADF10.getName()));
							sessionMap.put(FieldType.ADF9.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
							sessionMap.put(FieldType.ADF11.getName(), responseMap.get(FieldType.ADF11.getName()));
							requestCreator.generateAxisBankNBRequest(responseMap);
							break;
						case CREDIT_CARD:
						case DEBIT_CARD:
							sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
							sessionMap.put(FieldType.PG_REF_NUM.getName(), responseMap.get(FieldType.PG_REF_NUM.getName()));
							requestCreator.generateAxisBankRequest(responseMap);
							break;
						default:
							break;
					}
				} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.IDBIBANK.getCode())) {
					PaymentType paymentType = PaymentType.getInstanceUsingCode(fields.get(FieldType.PAYMENT_TYPE.getName()));
					switch (paymentType) {
						case NET_BANKING:
							sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
							sessionMap.put(FieldType.PG_REF_NUM.getName(), responseMap.get(FieldType.PG_REF_NUM.getName()));
							requestCreator.generateIdbiRequest(responseMap);
							break;
						case CREDIT_CARD:
						case DEBIT_CARD:
							String integrationMode = PropertiesManager.propertiesMap.get("IDBIINTEGRATIONMODE");
							if (StringUtils.isNotBlank(integrationMode) && integrationMode.equals("REDIRECTION")) {
								sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
								sessionMap.put(FieldType.PG_REF_NUM.getName(), responseMap.get(FieldType.PG_REF_NUM.getName()));
								requestCreator.generateIdbiRequest(responseMap);
							} else {
								sessionMap.put(FieldType.CVV.getName(), fields.get(FieldType.CVV.getName()));
								sessionMap.put(FieldType.TOKEN_ID.getName(), responseMap.get(FieldType.TOKEN_ID.getName()));
								sessionMap.put(FieldType.CARD_HOLDER_NAME.getName(), fields.get(FieldType.CARD_HOLDER_NAME.getName()));
								sessionMap.put(FieldType.CARD_NUMBER.getName(), fields.get(FieldType.CARD_NUMBER.getName()));
								sessionMap.put(FieldType.CARD_EXP_DT.getName(), fields.get(FieldType.CARD_EXP_DT.getName()));
								sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
								requestCreator.generateEnrollIdbiRequest(responseMap);
							}
							break;
						default:
							break;
					}
				} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.MATCHMOVE.getCode())) {
					sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
					requestCreator.generateMatchMoveRequest(responseMap);
				} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.ATL.getCode())) {
					sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
					sessionMap.put(FieldType.PASSWORD.getName(), responseMap.get(FieldType.PASSWORD.getName()));
					requestCreator.generateAtlRequest(responseMap);
				} else if ((!StringUtils.isEmpty(acquirer)) &&
						(acquirer.equals(AcquirerType.NB_SBI.getCode())
								|| acquirer.equals(AcquirerType.NB_ALLAHABAD_BANK.getCode())
								|| acquirer.equals(AcquirerType.NB_AXIS_BANK.getCode())
								|| acquirer.equals(AcquirerType.NB_CORPORATION_BANK.getCode())
								|| acquirer.equals(AcquirerType.NB_ICICI_BANK.getCode())
								|| acquirer.equals(AcquirerType.NB_KARUR_VYSYA_BANK.getCode())
								|| acquirer.equals(AcquirerType.NB_SOUTH_INDIAN_BANK.getCode())
								|| acquirer.equals(AcquirerType.NB_VIJAYA_BANK.getCode())
								|| acquirer.equals(AcquirerType.WL_MOBIKWIK.getCode())
								|| acquirer.equals(AcquirerType.WL_OLAMONEY.getCode())
								|| acquirer.equals(AcquirerType.PAYTM.getCode())
								|| acquirer.equals(AcquirerType.PAYU.getCode())
								|| acquirer.equals(AcquirerType.CAMSPAY.getCode())
								|| acquirer.equals(AcquirerType.NB_KARNATAKA_BANK.getCode()))) {
					sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
					sessionMap.put(FieldType.PASSWORD.getName(), responseMap.get(FieldType.PASSWORD.getName()));
					requestCreator.generateIPayRequest(responseMap);
				} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.AXISMIGS.getCode())) {
					requestCreator.sendMigsEnrollTransaction(responseMap);
				} else if (!(pendingTxnStatus.contains(status))) {
					sessionMap.invalidate();
					acquirerFlag = NONE;
				} else {
					acquirerFlag = NONE;
				}
			} else if (status.equals(StatusType.REJECTED.getName())) {
				responseMap.put(FieldType.TXNTYPE.getName(), (String) sessionMap.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()));
				transactionResponser.removeInvalidResponseFields(responseMap);
				transactionResponser.addResponseDateTime(responseMap);
				responseMap.put(FieldType.IS_MERCHANT_HOSTED.getName(), "Y");
				responseCreator.ResponsePost(responseMap);
				return Action.NONE;
			} else {
				responseMap.put(FieldType.INTERNAL_IRCTC_YN.getName(), (String) sessionMap.get(FieldType.INTERNAL_IRCTC_YN.getName()));
				if (StringUtils.isBlank(responseMap.get(FieldType.RESPONSE_CODE.getName()))) {
					responseMap.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.INTERNAL_SYSTEM_ERROR.getResponseMessage());
					responseMap.put(FieldType.RESPONSE_CODE.getName(), ErrorType.INTERNAL_SYSTEM_ERROR.getCode());
				}
				responseMap.put(FieldType.TXNTYPE.getName(), (String) sessionMap.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()));
				transactionResponser.removeInvalidResponseFields(responseMap);
				transactionResponser.addResponseDateTime(responseMap);
				responseMap.put(FieldType.IS_MERCHANT_HOSTED.getName(), "Y");
				responseCreator.ResponsePost(responseMap);
				return Action.NONE;
			}
			responseMap.remove(FieldType.PAREQ.getName());
			responseMap.removeSecureFields();
		} catch (SystemException systemException) {
			logger.info("systemException:{}", systemException.getMessage(), systemException);
			if (null == fields) {
				logger.info("fields not exist");
				return "invalidRequest";
			}

			if (StringUtils.isEmpty(fields.get(FieldType.PAY_ID.getName()))) {
				logger.info("PAY_ID not exist");
				return "invalidRequest";
			}

			String salt = merchantKeySaltDao.find(fields.get(FieldType.PAY_ID.getName())).getSalt();
			if (null == salt) {
				logger.info("salt not exist");
				return "invalidRequest";
			}

			if (!StringUtils.isBlank(systemException.getMessage())) {
				fields.put(FieldType.PG_TXN_MESSAGE.getName(), systemException.getMessage());
			}

			User user = userDao.getUserClass(fields.get(FieldType.PAY_ID.getName()));
			String origTxnType = ModeType.getDefaultPurchaseTransaction(user.getModeType()).getName();

			fields.put(FieldType.TXNTYPE.getName(), origTxnType);
			fields.put(FieldType.RESPONSE_CODE.getName(), systemException.getErrorType().getCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), systemException.getErrorType().getResponseMessage());

			saveInvalidTransaction(fields, origTxnType);

			// If an invalid request of valid merchant save it
			if (fields.get(FieldType.RESPONSE_CODE.getName()).equals(ErrorType.INVALID_RETURN_URL.getCode())) {
				logger.info("RETURN_URL invalid");
				return "invalidRequest";
			}
			fields.put(FieldType.TXNTYPE.getName(), origTxnType);
			responseCreator.create(fields);
			fields.removeInternalFields();
			fields.put(FieldType.IS_MERCHANT_HOSTED.getName(), "Y");
			responseCreator.ResponsePost(fields);
			sessionMap.invalidate();
			return Action.NONE;
		} catch (Exception exception) {
			sessionMap.invalidate();
			logger.error("Unknown error in merchant hosted payment", exception);
			return ERROR;
		}
		return getAcquirerFlag();
	}

	private Map<String, String> callProcessPayment(Fields fields) throws SystemException {
		try {
			fields.logAllFieldsUsingMasking("direct call /process/payment Raw Request:");
			fields.clean();
			Fields internalFields = fields.removeInternalFields();
			fields.removeExtraInternalFields();
			fields.put(FieldType.IS_INTERNAL_REQUEST.getName(), Constants.Y_FLAG.getValue());
			fields.put(internalFields);
			fields.logAllFieldsUsingMasking("direct call /process/payment Refine Request:");
			return requestRouter.route(fields);
		} catch (Exception exception) {
			logger.error("direct call /process/payment Exception", exception);
			throw new SystemException(ErrorType.TRANSACTION_FAILED, ErrorType.TRANSACTION_FAILED.getResponseMessage());
		}
	}

	// customized validation
	private void payInRequestParameterValidation(Fields fields) throws SystemException {
		String custName = fields.get(FieldType.CUST_NAME.getName());
		String custPhone = fields.get(FieldType.CUST_PHONE.getName());
		String custEmail = fields.get(FieldType.CUST_EMAIL.getName());
		String amount = fields.get(FieldType.AMOUNT.getName());
		String currencyCode = fields.get(FieldType.CURRENCY_CODE.getName());
		String modType = fields.get(FieldType.MOP_TYPE.getName());
		String paymentType = fields.get(FieldType.PAYMENT_TYPE.getName());
		String cardHolderName = fields.get(FieldType.CARD_HOLDER_NAME.getName());
		String payerAddress = fields.get(FieldType.PAYER_ADDRESS.getName());
		String custFirstName = fields.get(FieldType.CUST_FIRST_NAME.getName());
		String custLastName = fields.get(FieldType.CUST_LAST_NAME.getName());
		String custStreetAddress1 = fields.get(FieldType.CUST_STREET_ADDRESS1.getName());
		String custCity = fields.get(FieldType.CUST_CITY.getName());
		String custState = fields.get(FieldType.CUST_STATE.getName());
		String custCountry = fields.get(FieldType.CUST_COUNTRY.getName());
		String custZip = fields.get(FieldType.CUST_ZIP.getName());
		String productDesc = fields.get(FieldType.PRODUCT_DESC.getName());
		String payType = fields.get(FieldType.PAY_TYPE.getName());
		String cardNumber = fields.get(FieldType.CARD_NUMBER.getName());
		String cardExpDt = fields.get(FieldType.CARD_EXP_DT.getName());
		String cvv = fields.get(FieldType.CVV.getName());
		String hash = fields.get(FieldType.HASH.getName());
		String returnUrl = fields.get(FieldType.RETURN_URL.getName());
		String orderId = fields.get(FieldType.ORDER_ID.getName());

		char[] permittedSpecialChars = {' ', '{', '}', '&', ';', '@', ',', '-', '_', '+', '/', '=', '*', '.', ':', '\n', '\r', '?', '\'', '(', ')', '|', '#', '$'};
		StringBuilder regexBuilder = new StringBuilder();
		for (char c : permittedSpecialChars) {
			if ("[](){}.*+?^$\\|".indexOf(c) != -1) {
				regexBuilder.append("\\");
			}
			regexBuilder.append(c);
		}
		String regex = "^[a-zA-Z0-9" + regexBuilder + "]*$";
		if(StringUtils.isNotEmpty(orderId) && !orderId.matches(regex)){
			throw new SystemException(ErrorType.VALIDATION_FAILED, "Invalid ORDER_ID, Special char Not Allowed");
		}

		if(StringUtils.isEmpty(returnUrl)){
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.INVALID_RETURN_URL.getCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.INVALID_RETURN_URL.getResponseMessage());
			fields.setValid(false);
			throw new SystemException(ErrorType.INVALID_RETURN_URL, FieldType.RETURN_URL.getName() + " is Required.");
		}
		if (returnUrl.length() < 15 || returnUrl.length() > 1024) {
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.INVALID_RETURN_URL.getCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.INVALID_RETURN_URL.getResponseMessage());
			fields.setValid(false);
			throw new SystemException(ErrorType.INVALID_RETURN_URL, String.format("Minimum and Maximum length of '%s' is %d and %d", FieldType.RETURN_URL.getName(), 15, 1024));
		}
		if(StringUtils.isEmpty(hash)){
			throw new SystemException(ErrorType.VALIDATION_FAILED, FieldType.HASH.getName() + " is Required.");
		}
		if (StringUtils.isEmpty(paymentType)) {
			throw new SystemException(ErrorType.VALIDATION_FAILED, FieldType.PAYMENT_TYPE.getName() + " is a required field.");
		}
		if (paymentType.length() < 2 || paymentType.length() > 5) {
			throw new SystemException(ErrorType.VALIDATION_FAILED, String.format("Minimum and Maximum length of '%s' is %d and %d", FieldType.PAYMENT_TYPE.getName(), 2, 5));
		}

		if (paymentType.equals(MerchantPaymentType.CARD.getCode())) {
			if (StringUtils.isEmpty(cardHolderName)) {
				throw new SystemException(ErrorType.VALIDATION_FAILED, FieldType.CARD_HOLDER_NAME.getName() + " is a required field.");
			}
			if (StringUtils.isEmpty(cardNumber)) {
				throw new SystemException(ErrorType.VALIDATION_FAILED, FieldType.CARD_NUMBER.getName() + " is a required field.");
			}
			if(!StringUtils.isEmpty(cardNumber) && cardNumber.contains(" ")){
				throw new SystemException(ErrorType.VALIDATION_FAILED, "Invalid CARD_NUMBER");
			}
			if (cardNumber.length() != 16) {
				throw new SystemException(ErrorType.VALIDATION_FAILED, String.format("Minimum and Maximum length of '%s' is %d and %d", FieldType.CARD_NUMBER.getName(), 16, 16));
			}
			if (StringUtils.isEmpty(cardExpDt)) {
				throw new SystemException(ErrorType.VALIDATION_FAILED, FieldType.CARD_EXP_DT.getName() + " is a required field.");
			}
			if(!StringUtils.isEmpty(cardExpDt) && cardExpDt.contains(" ")){
				throw new SystemException(ErrorType.VALIDATION_FAILED, "Invalid CARD_EXP_DT");
			}
			if (cardExpDt.length() != 6) {
				throw new SystemException(ErrorType.VALIDATION_FAILED, String.format("Minimum and Maximum length of '%s' is %d and %d", FieldType.CARD_EXP_DT.getName(), 6, 6));
			}
			if (StringUtils.isEmpty(cvv)) {
				throw new SystemException(ErrorType.VALIDATION_FAILED, FieldType.CVV.getName() + " is a required field.");
			}
			if(!StringUtils.isEmpty(cvv) && cvv.contains(" ")){
				throw new SystemException(ErrorType.VALIDATION_FAILED, "Invalid CVV");
			}
			if (cvv.length() != 3) {
				throw new SystemException(ErrorType.VALIDATION_FAILED, String.format("Minimum and Maximum length of '%s' is %d and %d", FieldType.CVV.getName(), 3, 3));
			}
		}
		if (StringUtils.isEmpty(custName)) {
			throw new SystemException(ErrorType.VALIDATION_FAILED, FieldType.CUST_NAME.getName() + " is a required field.");
		}
		if(StringUtils.isEmpty(custName.trim())){
			throw new SystemException(ErrorType.VALIDATION_FAILED, "Invalid Cust Name.");
		}
		custName = custName.trim();
		if (custName.isEmpty() || custName.length() > 150) {
			throw new SystemException(ErrorType.VALIDATION_FAILED, String.format("Minimum and Maximum length of '%s' is %d and %d", FieldType.CUST_NAME.getName(), 1, 150));
		}
		if(!custName.matches("^[a-zA-Z ]+[0-9]{0,149}$")){
			throw new SystemException(ErrorType.VALIDATION_FAILED, "Invalid Cust Name");
		}
		if (StringUtils.isNotEmpty(cardHolderName)) {
			if (cardHolderName.length() < 2 || cardHolderName.length() > 45) {
				throw new SystemException(ErrorType.VALIDATION_FAILED, String.format("Minimum and Maximum length of '%s' is %d and %d", FieldType.CARD_HOLDER_NAME.getName(), 2, 45));
			}
			if(!cardHolderName.matches("^[a-zA-Z ]+[0-9]{0,43}$")){
				throw new SystemException(ErrorType.VALIDATION_FAILED, "Invalid Card Holder Name.");
			}
		}
		if (StringUtils.isEmpty(custPhone)) {
			throw new SystemException(ErrorType.VALIDATION_FAILED, FieldType.CUST_PHONE.getName() + " is a required field.");
		}
		if (custPhone.length() < 8 || custPhone.length() > 15) {
			throw new SystemException(ErrorType.VALIDATION_FAILED, String.format("Minimum and Maximum length of '%s' is %d and %d", FieldType.CUST_PHONE.getName(), 8, 15));
		}
		if (StringUtils.isEmpty(custEmail)) {
			throw new SystemException(ErrorType.VALIDATION_FAILED, FieldType.CUST_EMAIL.getName() + " is a required field.");
		}
		if (custEmail.length() < 6 || custEmail.length() > 120) {
			throw new SystemException(ErrorType.VALIDATION_FAILED, String.format("Minimum and Maximum length of '%s' is %d and %d", FieldType.CUST_EMAIL.getName(), 6, 120));
		}
		if (StringUtils.isEmpty(currencyCode)) {
			throw new SystemException(ErrorType.VALIDATION_FAILED, FieldType.CURRENCY_CODE.getName() + " is a required field.");
		}
		if (currencyCode.length() != 3) {
			throw new SystemException(ErrorType.VALIDATION_FAILED, String.format("Minimum and Maximum length of '%s' is %d and %d", FieldType.CURRENCY_CODE.getName(), 3, 3));
		}
		CurrencyNumber currencyNumberInstance = CurrencyNumber.getInstancefromCode(currencyCode);
		if(currencyNumberInstance == null || !currencyCode.matches("[0-9]{3}")){
			throw new SystemException(ErrorType.VALIDATION_FAILED, "Invalid Currency");
		}
		if (paymentType.equals(PaymentType.UPI.getCode())) {
			if (StringUtils.isEmpty(payerAddress)) {
				throw new SystemException(ErrorType.VALIDATION_FAILED, FieldType.PAYER_ADDRESS.getName() + " is a required field.");
			}
			if (payerAddress.length() < 5 || payerAddress.length() > 255) {
				throw new SystemException(ErrorType.VALIDATION_FAILED, String.format("Minimum and Maximum length of '%s' is %d and %d", FieldType.PAYER_ADDRESS.getName(), 5, 255));
			}
			if(payerAddress.matches("[0-9]+")){
				throw new SystemException(ErrorType.VALIDATION_FAILED, "Invalid Payer Address");
			}
		}
		if (StringUtils.isEmpty(modType)) {
			throw new SystemException(ErrorType.VALIDATION_FAILED, FieldType.MOP_TYPE.getName() + " is a required field.");
		}
		if (modType.length() < 2 || modType.length() > 10) {
			throw new SystemException(ErrorType.VALIDATION_FAILED, String.format("Minimum and Maximum length of '%s' is %d and %d", FieldType.MOP_TYPE.getName(), 2, 10));
		}
		if(!modType.matches("[a-zA-Z0-9]{2,10}")){
			throw new SystemException(ErrorType.VALIDATION_FAILED, "Invalid MOP Type");
		}
		if (StringUtils.isEmpty(amount)) {
			throw new SystemException(ErrorType.VALIDATION_FAILED, FieldType.AMOUNT.getName() + " is a required field.");
		}
		if (amount.length() < 3 || amount.length() > 12) {
			throw new SystemException(ErrorType.VALIDATION_FAILED, String.format("Minimum and Maximum length of '%s' is %d and %d", FieldType.AMOUNT.getName(), 3, 12));
		}
		if(!amount.matches("[0-9]{3,12}")){
			throw new SystemException(ErrorType.VALIDATION_FAILED, "Invalid Amount");
		}
		if(amount.startsWith("0")){
			throw new SystemException(ErrorType.VALIDATION_FAILED, "Invalid Amount");
		}

		if (StringUtils.isNotEmpty(custFirstName)) {
			if (custFirstName.length() < 2 || custFirstName.length() > 150) {
				throw new SystemException(ErrorType.VALIDATION_FAILED, String.format("Minimum and Maximum length of '%s' is %d and %d", FieldType.CUST_FIRST_NAME.getName(), 2, 150));
			}
		}
		if (StringUtils.isNotEmpty(custLastName)) {
			if (custLastName.length() < 2 || custLastName.length() > 150) {
				throw new SystemException(ErrorType.VALIDATION_FAILED, String.format("Minimum and Maximum length of '%s' is %d and %d", FieldType.CUST_LAST_NAME.getName(), 2, 150));
			}
		}
		if (StringUtils.isNotEmpty(custStreetAddress1)) {
			if (custStreetAddress1.length() < 2 || custStreetAddress1.length() > 250) {
				throw new SystemException(ErrorType.VALIDATION_FAILED, String.format("Minimum and Maximum length of '%s' is %d and %d", FieldType.CUST_STREET_ADDRESS1.getName(), 2, 250));
			}
		}
		if (StringUtils.isNotEmpty(custCity)) {
			if (custCity.length() < 2 || custCity.length() > 50) {
				throw new SystemException(ErrorType.VALIDATION_FAILED, String.format("Minimum and Maximum length of '%s' is %d and %d", FieldType.CUST_CITY.getName(), 2, 50));
			}
		}
		if (StringUtils.isNotEmpty(custState)) {
			if (custCity.length() < 2 || custCity.length() > 100) {
				throw new SystemException(ErrorType.VALIDATION_FAILED, String.format("Minimum and Maximum length of '%s' is %d and %d", FieldType.CUST_STATE.getName(), 2, 100));
			}
		}
		if (StringUtils.isNotEmpty(custCountry)) {
			if (custCountry.length() < 2 || custCountry.length() > 100) {
				throw new SystemException(ErrorType.VALIDATION_FAILED, String.format("Minimum and Maximum length of '%s' is %d and %d", FieldType.CUST_COUNTRY.getName(), 2, 100));
			}
		}
		if (StringUtils.isNotEmpty(custZip)) {
			if (custZip.length() < 6 || custZip.length() > 9) {
				throw new SystemException(ErrorType.VALIDATION_FAILED, String.format("Minimum and Maximum length of '%s' is %d and %d", FieldType.CUST_ZIP.getName(), 6, 9));
			}
		}
		if (StringUtils.isNotEmpty(productDesc)) {
			if (productDesc.isEmpty() || productDesc.length() > 1024) {
				throw new SystemException(ErrorType.VALIDATION_FAILED, String.format("Minimum and Maximum length of '%s' is %d and %d", FieldType.PRODUCT_DESC.getName(), 1, 1024));
			}
		}
		if (StringUtils.isNotEmpty(payType)) {
			if (payType.length() < 4 || payType.length() > 6) {
				throw new SystemException(ErrorType.VALIDATION_FAILED, String.format("Minimum and Maximum length of '%s' is %d and %d", FieldType.PAY_TYPE.getName(), 4, 6));
			}
		}
	}

	private String processPayoutRequest(Fields fields, HttpServletRequest request2) {

		try {
			// TODO Auto-generated method stub
			if (StringUtils.isBlank(fields.get(FieldType.HASH.getName()))) {
				throw new SystemException(ErrorType.VALIDATION_FAILED, "Invalid " + FieldType.HASH.getName());
			}

			generalValidator.validateHash(fields);

			fields.put(FieldType.IS_MERCHANT_HOSTED.getName(), "Y");
			sessionMap.put((FieldType.IS_MERCHANT_HOSTED.getName()), "Y");
			String fieldsAsString = fields.getFieldsAsBlobString();
			sessionMap.put(Constants.FIELDS.getValue(), fields);
			fields.put(FieldType.INTERNAL_REQUEST_FIELDS.getName(), fieldsAsString);

			String ip = request.getHeader("X-Forwarded-For");
			if (ip == null) {
				ip = request.getRemoteAddr();
			}
			logger.info("ipAddress in {}", ip);
			fields.put(FieldType.INTERNAL_CUST_IP.getName(), ip);

			//if (!(user.getPayoutIp() == null) && user.getPayoutIp())
			//	if (!(user.getPayoutIpList().contains(ip)))
			//		throw new SystemException(ErrorType.BLACKLISTED_IP, "IP Address is not Match");

			User user = userDao.getUserClass(fields.get(FieldType.PAY_ID.getName()));
			if (ObjectUtils.isEmpty(user) || user.getUserStatus() != UserStatusType.ACTIVE) {
				throw new SystemException(ErrorType.VALIDATION_FAILED, "User Inactive");
			}

			User user1 = userDao.findPayId(fields.get(FieldType.PAY_ID.getName()));
			sessionMap.put((FieldType.INTERNAL_HEADER_USER_AGENT.getName()), request.getHeader("User-Agent"));
			if (StringUtils.isNotBlank((String) sessionMap.get(FieldType.INTERNAL_HEADER_USER_AGENT.getName()))) {
				fields.put((FieldType.INTERNAL_HEADER_USER_AGENT.getName()),
						(String) sessionMap.get(FieldType.INTERNAL_HEADER_USER_AGENT.getName()));
			}
			logger.info("user.isPo() :{}", user1.getPOFlag());

			if (!user1.getPOFlag()) {
				throw new SystemException(ErrorType.PO, "Payout Not Enabled");
			}
			String origTxnType = ModeType.getDefaultPurchaseTransaction(user.getModeType()).getName();
			sessionMap.put((FieldType.INTERNAL_ORIG_TXN_TYPE.getName()), ModeType.getDefaultPurchaseTransaction(user.getModeType()).getName());
			fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), origTxnType);
			fields.put(FieldType.CARD_HOLDER_TYPE.getName(), CardHolderType.CONSUMER.toString());
			fields.put(FieldType.PAYMENTS_REGION.getName(), AccountCurrencyRegion.DOMESTIC.toString());

			String ipAddress = request.getHeader("X-FORWARDED-FOR");
			if (ipAddress == null) {
				ipAddress = request.getRemoteAddr();
				if (ipAddress == null) {
					logger.info("IP not received from both X-FORWARDED-FOR :{}, Remote Address : {}"
							, request.getHeader("X-FORWARDED-FOR")
							, request.getRemoteAddr());
				}
				fields.put((FieldType.INTERNAL_CUST_IP.getName()), ipAddress);
			} else {
				fields.put((FieldType.INTERNAL_CUST_IP.getName()), ipAddress);
			}
			fields.put(FieldType.INTERNAL_CUST_MAC.getName(), macUtil.getMackByIp(ipAddress));

			String countryCode = request.getHeader("CloudFront-Viewer-Country");
			logger.info("Header value {}", request.getHeader("User-Agent"));
			logger.info("CloudFront-Is-Mobile-Viewer {}", request.getHeader("CloudFront-Is-Mobile-Viewer"));
			logger.info("CloudFront-Is-Tablet-Viewer {}", request.getHeader("CloudFront-Is-Tablet-Viewer"));
			logger.info("CloudFront-Is-SmartTV-Viewer {}", request.getHeader("CloudFront-Is-SmartTV-Viewer"));
			logger.info("CloudFront-Is-Desktop-Viewer {}", request.getHeader("CloudFront-Is-Desktop-Viewer"));
			if (StringUtils.isBlank(countryCode)) {
				countryCode = "NA";
			}

			String domain = "NA";
			if (StringUtils.isNotBlank(request.getHeader("Referer"))) {
				try {
					domain = new URL(request.getHeader("Referer")).getHost();
					logger.info("Internal_Cust_Domain: domain name is {} request referer is {}", domain, request.getHeader("Referer"));
				} catch (Exception e) {
					domain = "NA";
				}
			}

			fields.put((FieldType.INTERNAL_CUST_DOMAIN.getName()), domain);
			fields.put((FieldType.INTERNAL_CUST_COUNTRY_NAME.getName()), countryCode);
			sessionMap.put(FieldType.INTERNAL_CUST_COUNTRY_NAME.getName(), fields.get(FieldType.INTERNAL_CUST_COUNTRY_NAME.getName()));
			setBusinessName(user.getBusinessName());
			fields.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
			fields.put(FieldType.INTERNAL_REQUEST_FIELDS.getName(), fieldsAsString);
			fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), origTxnType);
			fields.put(FieldType.INTERNAL_VALIDATE_HASH_YN.getName(), "N");
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.REQUEST_ACCEPTED.getResponseMessage());
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.REQUEST_ACCEPTED.getCode());
			fields.put(FieldType.STATUS.getName(), StatusTypePayout.PENDING.getName());
			fields.put(FieldType.IS_MERCHANT_HOSTED.getName(), "Y");
			sessionMap.put(FieldType.IS_MERCHANT_HOSTED.getName(), "Y");
			fields.put(FieldType.INTERNAL_ORIG_TXN_ID.getName(), fields.get(FieldType.ORIG_TXN_ID.getName()));
			fields.remove(FieldType.ORIG_TXN_ID.getName());
			sessionMap.put(FieldType.INTERNAL_ORIG_TXN_ID.getName(), fields.get(FieldType.INTERNAL_ORIG_TXN_ID.getName()));
			sessionMap.put(FieldType.INTERNAL_CUST_IP.getName(), fields.get(FieldType.INTERNAL_CUST_IP.getName()));

			String newTxnId = TransactionManager.getNewTransactionId();
			fields.put(FieldType.PG_REF_NUM.getName(), newTxnId);
			fields.put(FieldType.TXN_ID.getName(), newTxnId);
			checkCurrencyandBankCode(fields);

			validateDuplicateOrderId(fields);
			amountDeductionFromWallet(fields);
			if(StringUtils.isNotBlank(fields.get(FieldType.STATUS.getName())) && fields.get(FieldType.STATUS.getName()).equalsIgnoreCase(StatusType.INVALID.getName())) {
				fields.put(FieldType.STATUS.getName(),StatusType.FAILED.getName());
			}
			payoutUpdateProcessor.preProcess(fields);
			payoutUpdateProcessor.process(fields);

			responseCreator.ResponsePostPayout(fields);
			setENCDATA((fields.get(FieldType.ENCDATA.getName())));
			setPAY_ID((fields.get(FieldType.PAY_ID.getName())));
			responseCreator.ResponsePost(fields);

			return SUCCESS;

		} catch (SystemException systemException) {
			Fields sessionfields = (Fields) sessionMap.get(Constants.FIELDS.getValue());
			if (null == sessionfields) {
				return "invalidRequest";
			}

			logger.info("SESSION FIELD :{}", sessionfields.getFieldsAsString());
			logger.info("Field :{}",sessionfields.getFieldsAsString());

			String salt=   merchantKeySaltDao.find(fields.get(FieldType.PAY_ID.getName())).getSalt();

			if (null == salt) {
				return "invalidRequest";
			}

			if (!StringUtils.isBlank(systemException.getMessage())) {
				fields.put(FieldType.PG_TXN_MESSAGE.getName(), systemException.getMessage());
			}

			User user = userDao.getUserClass(fields.get(FieldType.PAY_ID.getName()));
			String origTxnType = ModeType.getDefaultPurchaseTransaction(user.getModeType()).getName();

			sessionfields.put(FieldType.TXNTYPE.getName(), origTxnType);
			sessionfields.put(FieldType.RESPONSE_CODE.getName(), systemException.getErrorType().getCode());
			sessionfields.put(FieldType.RESPONSE_MESSAGE.getName(),
					systemException.getErrorType().getResponseMessage());

			saveInvalidTransactionPayout(sessionfields, origTxnType);
			// If an invalid request of valid merchant save it
			if (sessionfields.get(FieldType.RESPONSE_CODE.getName()).equals(ErrorType.INVALID_RETURN_URL.getCode())) {
				return "invalidRequest";
			}
			sessionfields.put(FieldType.TXNTYPE.getName(), origTxnType);
			if(fields.get(FieldType.STATUS.getName()).equalsIgnoreCase(StatusType.INVALID.getName())) {
				fields.put(FieldType.STATUS.getName(),StatusType.FAILED.getName());
			}
			responseCreator.ResponsePostPayout(sessionfields);
			responseCreator.ResponsePost(fields);

			setENCDATA((sessionfields.get(FieldType.ENCDATA.getName())));
			setPAY_ID((sessionfields.get(FieldType.PAY_ID.getName())));
			sessionMap.invalidate();
			return Action.SUCCESS;
		} catch (Exception exception) {
			exception.printStackTrace();
			sessionMap.invalidate();
			logger.error("Unknown error in merchant hosted payment", exception);
			return ERROR;
		}

	}

	private void checkCurrencyandBankCode(Fields fields) throws SystemException {
		if (StringUtils.isBlank(fields.get(FieldType.BANK_CODE.getName()))) {
			throw new SystemException(ErrorType.BANK_CODE_MISSING, "Bank Code Missing");

		}
		if (StringUtils.isBlank(fields.get(FieldType.CURRENCY_CODE.getName()))) {
			throw new SystemException(ErrorType.CURRENCY_CODE_MISSING, "Currency Code Missing");

		}

		if (!payoutBankCodeconfigurationDao.getbankcodeCheck(fields.get(FieldType.BANK_CODE.getName()),
				fields.get(FieldType.CURRENCY_CODE.getName()))) {
			throw new SystemException(ErrorType.INVAILD_BANKCODE_OR_CURRENCY, "bank Code or currencyCode is in Invaild ");

		}

	}

	private void amountDeductionFromWallet(Fields fields) throws SystemException {
		MerchantWalletPODTO merchantWalletPODTO = new MerchantWalletPODTO();
		merchantWalletPODTO.setPayId(fields.get(FieldType.PAY_ID.getName()));

		merchantWalletPODTO.setCurrency(multCurrencyCodeDao.getCurrencyNamebyCode(fields.get(FieldType.CURRENCY_CODE.getName())));

		MerchantWalletPODTO merchantWalletPODTO1 = merchantWalletPODao.findByPayId(merchantWalletPODTO);
		if (merchantWalletPODTO1 == null) {
			throw new SystemException(ErrorType.PO_WALLET_IS_NOT_FOUND,
					"Merchant wallet is not found for  PayId=" + fields.get(FieldType.PAY_ID.getName()));

		}

		double finalAmount = Double.valueOf(merchantWalletPODTO1.getFinalBalance());
		long amount = Double.valueOf(fields.get((FieldType.AMOUNT.getName()))).longValue();
		double totalamount =   Double.valueOf(Amount.toDecimal(String.valueOf(amount),
				fields.get(FieldType.CURRENCY_CODE.getName())));
		if(finalAmount<totalamount) throw new SystemException(ErrorType.INSUFFICIENT_BALANCE,
				"Due to merchant insufficient balance" );
		double finalAmt = finalAmount - totalamount;
		merchantWalletPODTO1.setFinalBalance(String.valueOf(finalAmt));

		double totalBalance = Double.valueOf(merchantWalletPODTO1.getTotalBalance());
		double totalBalanceAmt = totalBalance - totalamount;
		merchantWalletPODTO1.setTotalBalance(String.valueOf(totalBalanceAmt));
		DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalDateTime myDateObj = LocalDateTime.now();
		String formattedDate = myDateObj.format(myFormatObj);
		PassbookPODTO passbookPODTO = new PassbookPODTO();
		passbookPODTO.setPayId(fields.get(FieldType.PAY_ID.getName()));
		passbookPODTO.setType("DEBIT");
		passbookPODTO.setNarration("PAYOUT");
		passbookPODTO.setCreateDate(formattedDate);
		passbookPODTO.setTxnId(TransactionManager.getNewTransactionId());
		passbookPODTO.setDebitAmt(Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()), fields.get(FieldType.CURRENCY_CODE.getName())));
		passbookPODTO.setAmount(Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()), fields.get(FieldType.CURRENCY_CODE.getName())));
		passbookPODTO.setCurrency(multCurrencyCodeDao.getCurrencyNamebyCode(String.valueOf(fields.get(FieldType.CURRENCY_CODE.getName()))));
		passbookPODTO.setRespTxn(fields.get(FieldType.ORDER_ID.getName()));
		logger.info("Save the Transaction History in passbook"+passbookPODTO.toString());
		merchantWalletPODao.savePassbookDetailsByPassbook(passbookPODTO);

		//merchantWalletPODao.SaveAndUpdteWallet(merchantWalletPODTO1);
		merchantWalletPODao.SaveAndUpdteAvailableBalanceWallet(merchantWalletPODTO1);

	}

	public void validateDuplicateOrderId(Fields fields) throws SystemException {
		User user = null;

		if (PropertiesManager.propertiesMap.get(Constants.USE_STATIC_DATA.getValue()) != null
				&& PropertiesManager.propertiesMap.get(Constants.USE_STATIC_DATA.getValue())
				.equalsIgnoreCase(Constants.Y_FLAG.getValue())) {
			user = staticDataProvider.getUserData(fields.get(FieldType.PAY_ID.getName()));
		} else {
			user = userDao.findPayId(fields.get(FieldType.PAY_ID.getName()));
		}

		if((fields.contains(FieldType.IS_MERCHANT_HOSTED.getName())) && !(StringUtils.isEmpty(fields.get(FieldType.IS_MERCHANT_HOSTED.getName()))) && (fields.get(FieldType.IS_MERCHANT_HOSTED.getName()).equals("Y"))) {
			if (fields.get(FieldType.TXNTYPE.getName()).equals(TransactionType.SALE.getName())) {
				if (!user.isAllowSaleDuplicate()) {
					field.validateDupicateSaleOrderIdPayout(fields);
				}

			} }
	}

	private String callVerificationEkycApi(String txnId, String mobileNumber, HttpServletRequest request) {
		String statusCode = "0000";

		RestTemplate restemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();

		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("IPIMEI", request.getRemoteAddr());
		headers.add("AGENT", PropertiesManager.propertiesMap.get("AGENT"));
		logger.info("Agent Is : " + PropertiesManager.propertiesMap.get("AGENT"));

		HashMap<String, Object> tree_map = new LinkedHashMap<>();
		tree_map.put("txnId", txnId);
		tree_map.put("mobileno", mobileNumber);

		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		String jsonString = gson.toJson(tree_map);

		HttpEntity requestEntity = new HttpEntity(jsonString, headers);
		try {
			ResponseEntity<String> responseEntity = restemplate.exchange(
					PropertiesManager.propertiesMap.get("VERIFICATION_WRAPPER_API"), HttpMethod.POST, requestEntity,
					String.class);
			logger.info("Response Getting From API " + responseEntity);

			if (responseEntity.getStatusCodeValue() == 200) {
				logger.info("Response Getting From API " + responseEntity.getBody());
				if (StringUtils.isNoneBlank(responseEntity.getBody())) {
					JSONObject jsonObject = new JSONObject(responseEntity.getBody());

					statusCode = jsonObject.has("statusCode") ? jsonObject.getString("statusCode") : "";

					// String apiTxnId=jsonObject.getString("txnId");

					if (StringUtils.isNotBlank(statusCode) && statusCode.equalsIgnoreCase("1000")) {
						String updatedDate = jsonObject.getString("responseDate");
						// 15 days calculation from today here
						if (StringUtils.isNotBlank(updatedDate)) {
							statusCode = numberOfDayCalculation(updatedDate);
							logger.info("Ekyc days calculation : " + statusCode);
						} else {
							logger.info("Ekyc updatedDate is null or empty");
							statusCode = "0000";
						}
					} else if (StringUtils.isNotBlank(statusCode) && statusCode.equalsIgnoreCase("1011")) {
						logger.info("Ekyc statusCode " + statusCode);
					} else {
						logger.info("Ekyc statusCode is null or empty or statusCode is not 1000 and 1011");
						statusCode = "0000";
					}
				}
			} else {
				statusCode = "1111";

			}
		} catch (Exception e) {
			logger.info("Exception in : " + e);
			statusCode = "1111";
		}
		logger.info("Ekyc final status is : " + statusCode);
		return statusCode;
	}

	private String numberOfDayCalculation(String updatedDate) {
		logger.info("Ekyc inside numberOfDayCalculation : " + updatedDate);
		String statusCode = "0000";
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
			Date dateBefore = sdf.parse(updatedDate);
			Date dateAfter = new Date();

			// Calculate the number of days between dates
			long timeDiff = Math.abs(dateAfter.getTime() - dateBefore.getTime());
			long daysDiff = TimeUnit.DAYS.convert(timeDiff, TimeUnit.MILLISECONDS);
			logger.info("The number of days between dates: " + daysDiff);

			if (daysDiff <= 15) {
				// Transaction further move
				logger.info("Merchant Hosted transaction for EKY is process successfully...");
				statusCode = "1000";
			} else {
				logger.info("Ekyc Re-Verification is needed : " + daysDiff);
			}
		} catch (Exception e) {
			logger.info("Exception in numberOfDayCalculation : " + e);
		}
		return statusCode;
	}

	private void handleTransactionCharges(Fields fields) throws SystemException {
		String paymentsRegion = fields.get(FieldType.PAYMENTS_REGION.getName());
		if (StringUtils.isBlank(paymentsRegion)) {
			paymentsRegion = AccountCurrencyRegion.DOMESTIC.toString();
		}
		if (StringUtils.isEmpty(fields.get(FieldType.AMOUNT.getName()))) {
			throw new SystemException(ErrorType.VALIDATION_FAILED, FieldType.AMOUNT.getName() + " is a mandatory field.");
		}
		if (StringUtils.isEmpty(fields.get(FieldType.CURRENCY_CODE.getName()))) {
			throw new SystemException(ErrorType.VALIDATION_FAILED, FieldType.CURRENCY_CODE.getName() + " is a mandatory field.");
		}
		generalValidator.validateField(FieldType.AMOUNT, FieldType.AMOUNT.getName(), fields);

		String amount = Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()), fields.get(FieldType.CURRENCY_CODE.getName()));
		BigDecimal tempAmount = new BigDecimal(amount);
		String payId = fields.get(FieldType.PAY_ID.getName());
		String surchargeFlag = sessionMap.get(FieldType.SURCHARGE_FLAG.getName()).toString();
		String currencyCode = fields.get(FieldType.CURRENCY_CODE.getName());
		String gst = "";
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Date date = new Date();

		BigDecimal adTransSurcharge = BigDecimal.ZERO;
		BigDecimal surchargeADAmount = BigDecimal.ZERO;
		BigDecimal ccTransSurcharge = BigDecimal.ZERO;
		BigDecimal surchargeCCAmount = BigDecimal.ZERO;
		BigDecimal dcTransSurcharge = BigDecimal.ZERO;
		BigDecimal surchargeDCAmount = BigDecimal.ZERO;
		BigDecimal wlTransSurcharge = BigDecimal.ZERO;
		BigDecimal surchargeWLAmount = BigDecimal.ZERO;
		BigDecimal upTransSurcharge = BigDecimal.ZERO;
		BigDecimal surchargeUPAmount = BigDecimal.ZERO;
		BigDecimal nbTransSurcharge = BigDecimal.ZERO;
		BigDecimal surchargeNBAmount = BigDecimal.ZERO;
		BigDecimal pcTransSurcharge = BigDecimal.ZERO;
		BigDecimal surchargePCAmount = BigDecimal.ZERO;
		BigDecimal qrTransSurcharge = BigDecimal.ZERO;
		BigDecimal surchargeQRAmount = BigDecimal.ZERO;
		BigDecimal bigDecimalAmt;

		fields.put((FieldType.SURCHARGE_FLAG.getName()), surchargeFlag);
		// Add surcharge amount and total amount on the basis of payment type
		String paymentType = fields.get(FieldType.PAYMENT_TYPE.getName());
		PaymentType paymentTypeS = PaymentType.getInstanceUsingCode(paymentType);
		switch (paymentTypeS) {
			case CREDIT_CARD:
				BigDecimal bigDecimal = dao.getCheckSurcharge("CC", Double.valueOf(amount),
						fields.get(FieldType.PAY_ID.getName()), date, paymentsRegion, currencyCode);
				ccTransSurcharge = bigDecimal;
				surchargeCCAmount = tempAmount.add(bigDecimal);
				bigDecimalAmt = surchargeCCAmount;
				logger.info("surcharge {}\tsurchargeCCAmount {}\tbigDecimalAm{}", ccTransSurcharge, surchargeCCAmount, bigDecimalAmt);
				break;
			case DEBIT_CARD:
				BigDecimal bigDecimal1 = dao.getCheckSurcharge("DC", Double.valueOf(amount),
						fields.get(FieldType.PAY_ID.getName()), date, paymentsRegion, currencyCode);
				dcTransSurcharge = bigDecimal1;
				surchargeDCAmount = tempAmount.add(bigDecimal1);
				bigDecimalAmt = surchargeDCAmount;
				logger.info("surcharge {}\tsurchargeCCAmount{}\tbigDecimalAmt{}", dcTransSurcharge, surchargeDCAmount, bigDecimalAmt);
				break;
			case NET_BANKING:
				BigDecimal bigDecimal2 = dao.getCheckSurcharge("NB", Double.valueOf(amount),
						fields.get(FieldType.PAY_ID.getName()), date, paymentsRegion, currencyCode);
				nbTransSurcharge = bigDecimal2;
				surchargeNBAmount = tempAmount.add(bigDecimal2);
				bigDecimalAmt = surchargeNBAmount;
				logger.info("surcharge {}\tsurchargeCCAmount{}\tbigDecimalAmt{}", nbTransSurcharge, surchargeNBAmount, bigDecimalAmt);
				break;
			case UPI:
				BigDecimal bigDecimal3 = dao.getCheckSurcharge("UP", Double.valueOf(amount),
						fields.get(FieldType.PAY_ID.getName()), date, paymentsRegion, currencyCode);
				upTransSurcharge = bigDecimal3;
				surchargeUPAmount = tempAmount.add(bigDecimal3);
				bigDecimalAmt = surchargeUPAmount;
				logger.info("surcharge {}\tsurchargeCCAmount{}\tbigDecimalAmt{}", upTransSurcharge, surchargeUPAmount, bigDecimalAmt);
				break;
			case WALLET:
				BigDecimal bigDecimal4 = dao.getCheckSurcharge("WL", Double.valueOf(amount),
						fields.get(FieldType.PAY_ID.getName()), date, paymentsRegion, currencyCode);
				wlTransSurcharge = bigDecimal4;
				surchargeWLAmount = tempAmount.add(bigDecimal4);
				bigDecimalAmt = surchargeWLAmount;
				logger.info("surcharge {}\tsurchargeCCAmount{}\tbigDecimalAmt{}", wlTransSurcharge, surchargeWLAmount, bigDecimalAmt);
				break;
			case QRCODE:
				BigDecimal bigDecimal5 = dao.getCheckSurcharge("QR", Double.valueOf(amount),
						fields.get(FieldType.PAY_ID.getName()), date, paymentsRegion, currencyCode);

				qrTransSurcharge = bigDecimal5;
				surchargeQRAmount = tempAmount.add(bigDecimal5);
				bigDecimalAmt = surchargeQRAmount;
				logger.info("surcharge {}\tsurchargeCCAmount{}\tbigDecimalAmt{}", qrTransSurcharge, surchargeQRAmount, bigDecimalAmt);
				break;
			default:
				// unsupported payment type
				throw new SystemException(ErrorType.PAYMENT_OPTION_NOT_SUPPORTED, "Unsupported payment type");
		}
		bigDecimalAmt = bigDecimalAmt.multiply(new BigDecimal(100));
		BigInteger bigInteger = bigDecimalAmt.toBigInteger();
		fields.put(FieldType.TOTAL_AMOUNT.getName(), bigInteger.toString());
		if (ccTransSurcharge.doubleValue() != 0
				|| dcTransSurcharge.doubleValue() != 0
				|| nbTransSurcharge.doubleValue() != 0
				|| upTransSurcharge.doubleValue() != 0
				|| wlTransSurcharge.doubleValue() != 0
				|| qrTransSurcharge.doubleValue() != 0) {
			sessionMap.computeIfPresent(FieldType.SURCHARGE_FLAG.getName(), (k, v) -> Constants.Y_FLAG.getValue());
		} else {
			sessionMap.computeIfPresent(FieldType.SURCHARGE_FLAG.getName(), (k, v) -> Constants.N_FLAG.getValue());

		}
	}

	private void saveInvalidTransaction(Fields fields, String origTxnType) {
		// TODO... validate and sanitize fields
		fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), origTxnType);

		sessionMap.put((FieldType.INTERNAL_ORIG_TXN_TYPE.getName()), origTxnType);

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

	private void saveInvalidTransactionPayout(Fields fields, String origTxnType) {
		// TODO... validate and sanitize fields
		fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), origTxnType);

		sessionMap.put((FieldType.INTERNAL_ORIG_TXN_TYPE.getName()), origTxnType);

		try {
			payoutUpdateProcessor.preProcess(fields);
			payoutUpdateProcessor.prepareInvalidTransactionForStorage(fields);
		} catch (SystemException systemException) {
			logger.error("Unable to save invalid transaction", systemException);
		} catch (Exception exception) {
			logger.error("Unhandaled error", exception);
			// Non reachable code for safety
		}
	}

	@Override
	public void validate() {
		logger.info("Validating merchant hosted request");
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getAcquirerFlag() {
		return acquirerFlag;
	}

	public void setAcquirerFlag(String acquirerFlag) {
		this.acquirerFlag = acquirerFlag;
	}

	public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
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

	@JsonProperty("ENCDATA")
	public String getENCDATA() {
		return ENCDATA;
	}

	@JsonProperty("ENCDATA")
	public void setENCDATA(String eNCDATA) {
		ENCDATA = eNCDATA;
	}

	@JsonProperty("PAY_ID")
	public String getPAY_ID() {
		return PAY_ID;
	}

	@JsonProperty("PAY_ID")
	public void setPAY_ID(String pAY_ID) {
		PAY_ID = pAY_ID;
	}

}
