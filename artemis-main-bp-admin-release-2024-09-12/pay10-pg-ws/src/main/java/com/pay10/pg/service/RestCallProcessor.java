package com.pay10.pg.service;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.opensymphony.xwork2.Action;
import com.pay10.commons.api.TransactionControllerServiceProvider;
import com.pay10.commons.dao.FieldsDao;
import com.pay10.commons.dao.RouterConfigurationDao;
import com.pay10.commons.dto.MerchantWalletPODTO;
import com.pay10.commons.dto.PassbookPODTO;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.mongo.MerchantWalletPODao;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.user.AccountCurrencyRegion;
import com.pay10.commons.user.CardHolderType;
import com.pay10.commons.user.MerchantKeySaltDao;
import com.pay10.commons.user.MultCurrencyCodeDao;
import com.pay10.commons.user.PayoutBankCodeconfigurationDao;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.Amount;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.MacUtil;
import com.pay10.commons.util.MerchantPaymentType;
import com.pay10.commons.util.ModeType;
import com.pay10.commons.util.MopType;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StaticDataProvider;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.StatusTypePayout;
import com.pay10.commons.util.TransactionManager;
import com.pay10.commons.util.TransactionType;
import com.pay10.commons.util.UserStatusType;
import com.pay10.cosmos.CosmosProcessor;
import com.pay10.pg.core.pageintegrator.GeneralValidator;
import com.pay10.pg.core.util.MerchantHostedUtils;
import com.pay10.pg.core.util.PayoutUpdateProcessor;
import com.pay10.pg.core.util.ProcessManager;
import com.pay10.pg.core.util.Processor;
import com.pay10.pg.core.util.ResponseCreator;
import com.pay10.pg.core.util.SurchargeAmountCalculator;
import com.pay10.pg.core.util.TransactionResponser;
import com.pay10.pg.core.util.UpdateProcessor;
import com.pay10.requestrouter.RequestRouter;
import com.pay10.sbi.SbiProcessor;
import com.pay10.sbi.upi.SbiUpiIntegrator;
import com.pay10.yesbankcb.YesBankCbIntegrator;

@Service
public class RestCallProcessor {
	private static Logger logger = LoggerFactory.getLogger(RestCallProcessor.class.getName());
	public static final String SEPARATOR = "~";
	public static final String EQUATOR = "=";

	@Autowired
	private TransactionControllerServiceProvider transactionControllerServiceProvider;

	@Autowired
	private GeneralValidator generalValidator;

	@Autowired
	private UserDao userDao;

	@Autowired
	private YesBankCbIntegrator yesBankCbIntegrator;

	@Autowired
	private SbiUpiIntegrator sbiUpiIntegrator;

	@Autowired
	private CosmosProcessor cosmosProcessor;

	@Autowired
	private SbiProcessor sbiProcessor;

	@Autowired
	private SurchargeAmountCalculator calculateSurchargeAmount;

	@Autowired
	@Qualifier("updateProcessor")
	private UpdateProcessor updateProcessor;

	@Autowired
	@Qualifier("securityProcessor")
	private Processor securityProcessor;

	@Autowired
	@Qualifier("cSourceProcessor")
	private Processor cSourceProcessor;

	@Autowired
	@Qualifier("fraudProcessor")
	private Processor fraudProcessor;

	@Autowired
	private RequestRouter router;
	@Autowired
	private ResponseCreator responseCreator;

	@Autowired
	RouterConfigurationDao routerConfigurationDao;

	@Autowired
	FieldsDao fieldsDao;

	@Autowired
	private PayoutUpdateProcessor payoutUpdateProcessor;

	@Autowired
	private MerchantWalletPODao merchantWalletPODao;

	@Autowired
	private MacUtil macUtil;

	@Autowired
	private PayoutBankCodeconfigurationDao payoutBankCodeconfigurationDao;

	@Autowired
	private StaticDataProvider staticDataProvider;

	@Autowired
	private MerchantKeySaltDao merchantKeySaltDao;

	@Autowired
	TransactionResponser transactionResponser;

	@Autowired
	private MerchantHostedUtils merchantHostedUtils;
	
	@Autowired
	MultCurrencyCodeDao multCurrencyCodeDao;
	
	@Autowired
	private MongoInstance mongoInstance;

	@SuppressWarnings("incomplete-switch")
	public Map<String, String> process(Fields fields, HttpServletRequest request) throws SystemException {
		try {

			if (StringUtils.isBlank(fields.get(FieldType.HASH.getName()))) {
				throw new SystemException(ErrorType.VALIDATION_FAILED, "Invalid " + FieldType.HASH.getName());
			}

			generalValidator.validateHash(fields);

			User user1 = userDao.findPayId(fields.get(FieldType.PAY_ID.getName()));

			if (user1.isMerchantS2SFlag()) {
				logger.info("Rest transaction payin, for payId={}, orderId={}", fields.get(FieldType.PAY_ID.getName()),
						fields.get(FieldType.ORDER_ID.getName()));
			} else {
				logger.error("Rest transaction payin, Please enabled S2S Flag for payId={}, orderId={}",
						fields.get(FieldType.PAY_ID.getName()), fields.get(FieldType.ORDER_ID.getName()));
				throw new SystemException(ErrorType.MERCHANT_HOSTED_S2S, "S2S flag is not enabled");
			}

			String iprem = request.getRemoteAddr();
			String ip = request.getHeader("X-Forwarded-For");
			if (StringUtils.isNotBlank(fields.get(FieldType.CUST_IP.getName()))) {
				fields.put((FieldType.INTERNAL_CUST_IP.getName()), fields.get(FieldType.CUST_IP.getName()));
				fields.remove(FieldType.CUST_IP.getName());

			} else if (StringUtils.isNotBlank(ip)) {
				fields.put((FieldType.INTERNAL_CUST_IP.getName()), ip);

			} else if (StringUtils.isNotBlank(iprem)) {
				fields.put((FieldType.INTERNAL_CUST_IP.getName()), iprem);

			}

			fields.put(FieldType.IS_MERCHANT_HOSTED.getName(), "Y");
			String fieldsAsString = fields.getFieldsAsBlobString();
			fields.put(FieldType.INTERNAL_REQUEST_FIELDS.getName(), fieldsAsString);

			User user = userDao.getUserClass(fields.get(FieldType.PAY_ID.getName()));

			MerchantPaymentType merchantPaymentType = MerchantPaymentType
					.getInstanceFromCode(fields.get(FieldType.PAYMENT_TYPE.getName()));

			// unsupported/invalid payment type received from merchant
			if (null == merchantPaymentType) {
				throw new SystemException(ErrorType.VALIDATION_FAILED, "Invalid payment type");
			}

			fields.put((FieldType.SURCHARGE_FLAG.getName()), ((user.isSurchargeFlag()) ? "Y" : "N"));

			if (merchantPaymentType.getName().equalsIgnoreCase(PaymentType.UPI.getName())) {
				fields.put(FieldType.PAYMENT_TYPE.getName(), PaymentType.UPI.getCode());
				fields.put(FieldType.MOP_TYPE.getName(), MopType.UPI.getCode());
				fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), TransactionType.SALE.getName());
				fields.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
				fields.put(FieldType.CARD_HOLDER_TYPE.getName(), CardHolderType.CONSUMER.toString());
				fields.put(FieldType.PAYMENTS_REGION.getName(), AccountCurrencyRegion.DOMESTIC.toString());
			}
			String origTxnType = TransactionType.SALE.getName();
			fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), TransactionType.SALE.getName());

			fields.put(FieldType.TXNTYPE.getName(), TransactionType.ENROLL.getName());
			fields.put(FieldType.INTERNAL_REQUEST_FIELDS.getName(), fieldsAsString);
			fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), TransactionType.SALE.getName());
			fields.put(FieldType.INTERNAL_VALIDATE_HASH_YN.getName(), "N");
			// Put transaction id as original txnId for subsequent transactions
			fields.put(FieldType.INTERNAL_ORIG_TXN_ID.getName(), fields.get(FieldType.ORIG_TXN_ID.getName()));
			fields.remove(FieldType.ORIG_TXN_ID.getName());
			fields.logAllFields("check vpa logs ....");

			fields.clean();
			Fields internalFields = fields.removeInternalFields();
			// Refine the fields sent by internal application
			fields.removeExtraInternalFields();
			// put Internal fields back
			fields.put(internalFields);
			fields.logAllFieldsUsingMasking("check vpa  Refine Request:");

			return callRestCollectPay(fields);

		} catch (SystemException systemException) {

			logger.info("Exception fields={}" + fields.getFieldsAsString());
			if (!StringUtils.isBlank(systemException.getMessage())) {
				fields.put(FieldType.PG_TXN_MESSAGE.getName(), systemException.getMessage());
			}

			User user = userDao.getUserClass(fields.get(FieldType.PAY_ID.getName()));
			String origTxnType = ModeType.getDefaultPurchaseTransaction(user.getModeType()).getName();

			fields.put(FieldType.TXNTYPE.getName(), origTxnType);
			fields.put(FieldType.RESPONSE_CODE.getName(), systemException.getErrorType().getCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), systemException.getErrorType().getResponseMessage());
			fields.put(FieldType.STATUS.getName(), systemException.getErrorType().getCode());
			fields.put(FieldType.PG_TXN_MESSAGE.getName(), systemException.getMessage());
			fields.put(FieldType.IS_MERCHANT_HOSTED.getName(), "Y");

			if (fields.get(FieldType.RESPONSE_CODE.getName()).equals(ErrorType.VALIDATION_FAILED.getCode())) {
				fields.put(FieldType.TXNTYPE.getName(), origTxnType);
				// createResponse(fields, processCall);
				return fields.getFields();
			}

			// saveInvalidTransaction(fields, TransactionType.SALE.getName());

			fields.put(FieldType.TXNTYPE.getName(), origTxnType);
			fields.put(FieldType.IS_MERCHANT_HOSTED.getName(), "Y");
			// createResponse(fields, processCall);
			return fields.getFields();
		}
	}

	private Map<String, String> callRestCollectPay(Fields fields) {
		Map<String, String> response = new HashMap<String, String>();
		try {

			logger.info("callRestCollectPay, fields={}" + fields.getFieldsAsString());
			ProcessManager.flow(securityProcessor, fields, false);

			String acquirer = fields.get(FieldType.ACQUIRER_TYPE.getName());
			logger.info("acquirer name : " + acquirer);
			logger.info("callRestCollectPay, after securityProcessor fields={}" + fields.getFieldsAsString());
			// logger.info("callRestCollectPay, fields={}"+fields.getFieldsAsString());
			// fraud Prevention processor
			// ProcessManager.flow(fraudProcessor, fields, false);
			// logger.info("callRestCollectPay, fraudProcessor
			// fields={}"+fields.getFieldsAsString());

			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SUCCESS.getResponseCode());
			fields.put(FieldType.PG_TXN_MESSAGE.getName(), ErrorType.SUCCESS.getResponseMessage());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.SUCCESS.getResponseMessage());

			if (StringUtils.isNotBlank(fields.get(FieldType.RESPONSE_CODE.getName()))
					&& fields.get(FieldType.RESPONSE_CODE.getName()).equals(ErrorType.SUCCESS.getCode())) {

				String redirectUrl = "https://uat.pay10.com/pgui/jsp/merchantPaymentLink?payinOrderId="
						+ String.valueOf(UUID.randomUUID());
				fields.put(FieldType.STATUS.getName(), StatusType.PENDING.getName());
				fields.put(FieldType.ALIAS_STATUS.getName(), StatusType.PENDING.getName());
				fields.put(FieldType.PAYIN_ORDER_ID.getName(), redirectUrl);
				ProcessManager.flow(updateProcessor, fields, true);

				response.put("redirectUrl", redirectUrl);
			} else {
				fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.ACQUIRER_NOT_FOUND.getResponseCode());
				fields.put(FieldType.PG_TXN_MESSAGE.getName(), ErrorType.ACQUIRER_NOT_FOUND.getResponseMessage());
				fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.FAILED.getResponseMessage());
				fields.put(FieldType.STATUS.getName(), StatusType.FAILED.getName());
				fields.put(FieldType.ALIAS_STATUS.getName(), StatusType.FAILED.getName());
				ProcessManager.flow(updateProcessor, fields, true);
			}
		} catch (Exception e) {
			logger.info("Exception == " + e);
		}
		return response;

	}

	public void getCollectPay(Fields fields) throws SystemException {
//		Map<String, String> responseMap = router.route(fields);
//		fields = new Fields(responseMap);
//		fields.get(FieldType.ACQUIRER_TYPE.getName());
//		logger.info("acquirer name routes : " + acquirer);

		// Process security
		ProcessManager.flow(securityProcessor, fields, false);

		String acquirer = fields.get(FieldType.ACQUIRER_TYPE.getName());
		logger.info("acquirer name : " + acquirer);

		// fraud Prevention processor
		ProcessManager.flow(fraudProcessor, fields, false);

		if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.YESBANKCB.getCode())) {
			// Process transaction with YESBANKCB
			ProcessManager.flow(cSourceProcessor, fields, false);

			ProcessManager.flow(updateProcessor, fields, true);

		} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.COSMOS.getCode())) {
			// Process transaction with COSMOS
			ProcessManager.flow(cosmosProcessor, fields, false);

			ProcessManager.flow(updateProcessor, fields, true);

		} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.SBI.getCode())) {
			// Process transaction with COSMOS
			ProcessManager.flow(sbiProcessor, fields, false);

			ProcessManager.flow(updateProcessor, fields, true);

		}

		else {
			fields.put(FieldType.STATUS.getName(), StatusType.INVALID.getName());
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.ACQUIRER_NOT_FOUND.getResponseCode());
			fields.put(FieldType.PG_TXN_MESSAGE.getName(), ErrorType.ACQUIRER_NOT_FOUND.getResponseMessage());
		}
	}

	public Map<String, String> ValidRequest(Fields fields) {
		Map<String, String> resp = new HashMap<String, String>();
		String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" + // part before @
				"(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

		if (StringUtils.isEmpty(fields.get(FieldType.PAY_ID.getName()))) {
			resp.put("respMessage", "Invalid Request, Pay Id is required");
			resp.put("respCode", "100");
		} else if (StringUtils.isEmpty(fields.get(FieldType.ORDER_ID.getName()))) {
			resp.put("respMessage", "Invalid Request, Order Id is required");
			resp.put("respCode", "100");
		} else if (fields.get(FieldType.ORDER_ID.getName()) != null
				&& Pattern.compile("[^a-z_0-9 ]", Pattern.CASE_INSENSITIVE)
						.matcher(fields.get(FieldType.ORDER_ID.getName())).find()) {
			resp.put("respMessage", "Invalid Request, Order Id not accept special character");
			resp.put("respCode", "100");
		} else if (fields.get(FieldType.ORDER_ID.getName()) != null
				&& Pattern.compile(" ").matcher(fields.get(FieldType.ORDER_ID.getName())).find()) {
			resp.put("respMessage", "Invalid Request, Order Id not accept blank space");
			resp.put("respCode", "100");
		} else if (fieldsDao.checkPaymentByOrderId(fields.get(FieldType.ORDER_ID.getName()),
				fields.get(FieldType.PAY_ID.getName()))) {
			resp.put("respMessage", "Invalid Request, Order Id already exists");
			resp.put("respCode", "100");
		} else if (StringUtils.isEmpty(fields.get(FieldType.AMOUNT.getName()))) {
			resp.put("respMessage", "Invalid Request, Amount is required");
			resp.put("respCode", "100");
		} else if (fields.get(FieldType.AMOUNT.getName()) != null) {
			String amount = fields.get(FieldType.AMOUNT.getName());
			if (Integer.valueOf(amount.replace(".", "")) <= 0) {
				resp.put("respMessage", "Invalid Request, Amount should be greater than 0");
				resp.put("respCode", "100");
			}
		} else if (StringUtils.isEmpty(fields.get(FieldType.CUST_NAME.getName()))) {
			resp.put("respMessage", "Invalid Request, Customer Name is required");
			resp.put("respCode", "100");
		} else if (StringUtils.isEmpty(fields.get(FieldType.CUST_EMAIL.getName()))) {
			resp.put("respMessage", "Invalid Request, Customer Email is required");
			resp.put("respCode", "100");
		} else if (fields.get(FieldType.CUST_EMAIL.getName()) != null
				&& !Pattern.compile(emailRegex).matcher(fields.get(FieldType.CUST_EMAIL.getName())).matches()) {
			resp.put("respMessage", "Invalid Request, Valid Customer Email is required");
			resp.put("respCode", "100");
		} else if (StringUtils.isEmpty(fields.get(FieldType.CUST_PHONE.getName()))) {
			resp.put("respMessage", "Invalid Request, Customer Phone is required");
			resp.put("respCode", "100");
		} else if (fields.get(FieldType.CUST_PHONE.getName()) != null
				&& !(fields.get(FieldType.CUST_PHONE.getName()).length() == 10)) {
			resp.put("respMessage", "Invalid Request, Customer Phone must only 10 digit");
			resp.put("respCode", "100");
		} else if (fields.get(FieldType.CUST_PHONE.getName()) != null
				&& !fields.get(FieldType.CUST_PHONE.getName()).matches("[0-9]+")) {
			resp.put("respMessage", "Invalid Request, Customer Phone not accept special character");
			resp.put("respCode", "100");
		}

		logger.info("=========== resp==========" + resp);
		return resp;
	}

	public Fields prepareFieldsMerchantHosted(Map<String, String> reqmap) throws SystemException {
		Map<String, String> requestMap = new HashMap<String, String>();
		String payId = reqmap.get(FieldType.PAY_ID.getName());
		String encData = reqmap.get(FieldType.ENCDATA.getName()).replace(" ", "+");
		if (StringUtils.isBlank(encData) || StringUtils.isBlank(payId)) {
			throw new SystemException(ErrorType.INVALID_INPUT, "Invalid request received");
		}

		logger.info("Encrypted request from merchant PAY_ID: " + payId + " " + encData);
		Map<String, String> responseMap = transactionControllerServiceProvider.decrypt(payId, encData);

		if (responseMap.containsKey(FieldType.RESPONSE_CODE.getName())) {
			logger.error("Response code : " + responseMap.get(FieldType.RESPONSE_CODE.getName()));
			throw new SystemException(ErrorType.getInstanceFromCode(responseMap.get(FieldType.RESPONSE_CODE.getName())),
					responseMap.get(FieldType.RESPONSE_MESSAGE.getName()));
		}

		String decryptedString = responseMap.get(FieldType.ENCDATA.getName());
		String[] fieldArray = decryptedString.split(SEPARATOR);

		Fields fields = null;
		for (String entry : fieldArray) {
			String[] namValuePair = entry.split(EQUATOR, 2);
			if (namValuePair.length == 2) {
				requestMap.put(namValuePair[0], namValuePair[1]);
			} else {
				requestMap.put(namValuePair[0], "");
			}
		}
		fields = new Fields(requestMap);
		fields.removeInternalFields();
		return fields;
		// Valid Response map will not contain any responseCode or responseMessage. If
		// it contains responseCode key then send exception. Ex ResponseCode = 100, User
		// not found in case of wrong payId.

	}

	public String processPayoutRequest(Fields fields, HttpServletRequest request) {
		try {

			logger.info("New Merchant hosted Payment request Raw Request Fields, merchant hosted:  " + fields.getFieldsAsString());
			if (StringUtils.isBlank(fields.get(FieldType.HASH.getName()))) {
				throw new SystemException(ErrorType.VALIDATION_FAILED, "Invalid " + FieldType.HASH.getName());
			}
			
			logger.info("before Requets Fields validation :"+fields.getFieldsAsString());
			validateRequestFields(fields);
			logger.info("after Requets Fields validation :"+fields.getFieldsAsString());
			
			String currency=fields.get(FieldType.CURRENCY_CODE.getName());
			String amount=Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()), currency);
			String payId=fields.get(FieldType.PAY_ID.getName());
			
			String channel="FIAT";
			
			Document documentFilter=new Document();
			documentFilter.append("CHANNEL", channel).append("CURRENCY_CODE", currency).append("PAY_ID", payId).
			append("STATUS", "Active");
			
			logger.info("Final Query before amount validation : " + documentFilter);
			
			MongoCursor<Document> cursor =mongoInstance.getDB().getCollection("PO_FRM").find(documentFilter).iterator();
			String minAmount="";
			String maxAmount="";
			
			if(cursor.hasNext()) {
				Document document=cursor.next();
				logger.info("payout frm : " + document);
				minAmount=document.getString("MIN_TICKET_SIZE");
				maxAmount=document.getString("MAX_TICKET_SIZE");
			}
			if(cursor!=null) {
				cursor.close();
			}
			double requestAmount=Double.parseDouble(amount);
			logger.info("FRM minticket and maxticket and requestAmount : " + minAmount + " - " + maxAmount + " - " + requestAmount);
			if (StringUtils.isNotBlank(minAmount) && StringUtils.isNotBlank(maxAmount)) {

				if (!(Double.parseDouble(minAmount) <= requestAmount)) {
					throw new SystemException(ErrorType.PO_FRM_MIN_TICKET_SIZE, "PAYOUT MIN TICKET SIZE BREACH");
				}

				if (!(Double.parseDouble(maxAmount) >= requestAmount)) {
					throw new SystemException(ErrorType.PO_FRM_MAX_TICKET_SIZE, "PAYOUT MAX TICKET SIZE BREACH");
				}
			} else {
				throw new SystemException(ErrorType.PO_FRM, "FRM IS NOT SET");
			}
			
			generalValidator.validateHash(fields);

			fields.put(FieldType.IS_MERCHANT_HOSTED.getName(), "Y");
//			sessionMap.put((FieldType.IS_MERCHANT_HOSTED.getName()), "Y");
			String fieldsAsString = fields.getFieldsAsBlobString();
//			sessionMap.put(Constants.FIELDS.getValue(), fields);
			fields.put(FieldType.INTERNAL_REQUEST_FIELDS.getName(), fieldsAsString);

			User user = userDao.getUserClass(fields.get(FieldType.PAY_ID.getName()));

			String ip = request.getHeader("X-Forwarded-For");

			if (ip == null)
				ip = request.getRemoteAddr();

			logger.info("ipAddress in" + ip);
			fields.put(FieldType.INTERNAL_CUST_IP.getName(), ip);

			if (!(user.getPayoutIp() == null) && user.getPayoutIp())
				if (!(user.getPayoutIpList().contains(ip)))
					throw new SystemException(ErrorType.BLACKLISTED_IP, "IP Address is not Match");

			if (ObjectUtils.isEmpty(user) || user.getUserStatus() != UserStatusType.ACTIVE) {
				throw new SystemException(ErrorType.VALIDATION_FAILED, "User Inactive");
			}
			
			User user1 = userDao.findPayId(fields.get(FieldType.PAY_ID.getName()));
//			sessionMap.put((FieldType.INTERNAL_HEADER_USER_AGENT.getName()), request.getHeader("User-Agent"));
//			if (StringUtils.isNotBlank((String) sessionMap.get(FieldType.INTERNAL_HEADER_USER_AGENT.getName()))) {
//				fields.put((FieldType.INTERNAL_HEADER_USER_AGENT.getName()),
//						(String) sessionMap.get(FieldType.INTERNAL_HEADER_USER_AGENT.getName()));
//			}

			logger.info("user.isPo() :" + user1.getPOFlag());

			if (!user1.getPOFlag()) {
				throw new SystemException(ErrorType.PO, "Payout Not Enabled");
			}
			String origTxnType = ModeType.getDefaultPurchaseTransaction(user.getModeType()).getName();
			// sessionMap.put((FieldType.INTERNAL_ORIG_TXN_TYPE.getName()),
			// ModeType.getDefaultPurchaseTransaction(user.getModeType()).getName());
			fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), origTxnType);

			fields.put(FieldType.CARD_HOLDER_TYPE.getName(), CardHolderType.CONSUMER.toString());
			fields.put(FieldType.PAYMENTS_REGION.getName(), AccountCurrencyRegion.DOMESTIC.toString());

			String ipAddress = request.getHeader("X-FORWARDED-FOR");
			if (ipAddress == null) {
				ipAddress = request.getRemoteAddr();
				if (ipAddress == null) {
					logger.info("IP not received from both X-FORWARDED-FOR :" + request.getHeader("X-FORWARDED-FOR")
							+ ", " + "Remote Address : " + request.getRemoteAddr());
				}
				fields.put((FieldType.INTERNAL_CUST_IP.getName()), ipAddress);
			} else {
				fields.put((FieldType.INTERNAL_CUST_IP.getName()), ipAddress);
			}
			fields.put(FieldType.INTERNAL_CUST_MAC.getName(), macUtil.getMackByIp(ipAddress));

			String countryCode = request.getHeader("CloudFront-Viewer-Country");
			logger.info("Header value " + request.getHeader("User-Agent"));
			logger.info("CloudFront-Is-Mobile-Viewer    " + request.getHeader("CloudFront-Is-Mobile-Viewer"));
			logger.info("CloudFront-Is-Tablet-Viewer    " + request.getHeader("CloudFront-Is-Tablet-Viewer"));
			logger.info("CloudFront-Is-SmartTV-Viewer    " + request.getHeader("CloudFront-Is-SmartTV-Viewer"));
			logger.info("CloudFront-Is-Desktop-Viewer    " + request.getHeader("CloudFront-Is-Desktop-Viewer"));
			if (StringUtils.isBlank(countryCode)) {
				countryCode = "NA";
			}

			String domain = "NA";
			if (StringUtils.isNotBlank(request.getHeader("Referer"))) {
				try {
					domain = new URL(request.getHeader("Referer")).getHost();
					logger.info("Internal_Cust_Domain: domain name is " + domain + " request referer is "
							+ request.getHeader("Referer"));
				} catch (Exception e) {
					domain = "NA";
				}
			}

			fields.put((FieldType.INTERNAL_CUST_DOMAIN.getName()), domain);

			fields.put((FieldType.INTERNAL_CUST_COUNTRY_NAME.getName()), countryCode);
//			sessionMap.put(FieldType.INTERNAL_CUST_COUNTRY_NAME.getName(), fields.get(FieldType.INTERNAL_CUST_COUNTRY_NAME.getName()));
//			setBusinessName(user.getBusinessName());
			fields.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
			fields.put(FieldType.INTERNAL_REQUEST_FIELDS.getName(), fieldsAsString);
			fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), origTxnType);
			fields.put(FieldType.INTERNAL_VALIDATE_HASH_YN.getName(), "N");

			fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.REQUEST_ACCEPTED.getResponseMessage());
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.REQUEST_ACCEPTED.getCode());
			fields.put(FieldType.STATUS.getName(), StatusTypePayout.PENDING.getName());
			fields.put(FieldType.IS_MERCHANT_HOSTED.getName(), "Y");
//			sessionMap.put(FieldType.IS_MERCHANT_HOSTED.getName(), "Y");
			fields.put(FieldType.INTERNAL_ORIG_TXN_ID.getName(), fields.get(FieldType.ORIG_TXN_ID.getName()));
			fields.remove(FieldType.ORIG_TXN_ID.getName());

//			sessionMap.put(FieldType.INTERNAL_ORIG_TXN_ID.getName(), fields.get(FieldType.INTERNAL_ORIG_TXN_ID.getName()));

//			sessionMap.put(FieldType.INTERNAL_CUST_IP.getName(), fields.get(FieldType.INTERNAL_CUST_IP.getName()));

			String newTxnId = TransactionManager.getNewTransactionId();
			fields.put(FieldType.PG_REF_NUM.getName(), newTxnId);
			fields.put(FieldType.TXN_ID.getName(), newTxnId);
			checkCurrencyandBankCode(fields);
			//validateDuplicateOrderId(fields);
			fieldsDao.validateDuplicateOrderIdForPayout(fields.get(FieldType.PAY_ID.getName()), fields.get(FieldType.ORDER_ID.getName())); 
			amountDeductionFromWallet(fields);
			if (StringUtils.isNotBlank(fields.get(FieldType.STATUS.getName()))
					&& fields.get(FieldType.STATUS.getName()).equalsIgnoreCase(StatusType.INVALID.getName())) {
				fields.put(FieldType.STATUS.getName(), StatusType.FAILED.getName());
			}
			payoutUpdateProcessor.preProcess(fields);
			payoutUpdateProcessor.process(fields);
			return ResponsePostPayout(fields);

		} catch (SystemException systemException) {
			logger.info("---------------------------SystemException----------------------------- ");
			logger.info("systemException "+systemException);
//			Fields sessionfields = (Fields) sessionMap.get(Constants.FIELDS.getValue());
//
//			logger.info("SESSION FIELD : " + sessionfields.getFieldsAsString());
//			logger.info("Field : " + sessionfields.getFieldsAsString());

//			if (null == sessionfields) {
//				return "invalidRequest";
//			}
			String salt = merchantKeySaltDao.find(fields.get(FieldType.PAY_ID.getName())).getSalt();

//			String salt = propertiesManager.getSalt(fields.get(FieldType.PAY_ID.getName()));

			if (null == salt) {
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

			saveInvalidTransactionPayout(fields, origTxnType);
			// If an invalid request of valid merchant save it
//			if (sessionfields.get(FieldType.RESPONSE_CODE.getName()).equals(ErrorType.INVALID_RETURN_URL.getCode())) {
//				return "invalidRequest";
//			}
			fields.put(FieldType.TXNTYPE.getName(), origTxnType);
			if (fields.get(FieldType.STATUS.getName()).equalsIgnoreCase(StatusType.INVALID.getName())) {
				fields.put(FieldType.STATUS.getName(), StatusType.FAILED.getName());
			}
			return ResponsePostPayout(fields);

		} catch (Exception exception) {
			exception.printStackTrace();
			logger.error("Unknown error in merchant hosted payment", exception);
			return null;
		}

	}

	public void validateRequestFields(Fields fields) throws SystemException {
		
		logger.info("--------------- validateRequestFields ---------------"+fields.getFieldsAsString());
		
		String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +  //part before @
                "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"; 
		//PAY_ID
		if (StringUtils.isEmpty(fields.get(FieldType.PAY_ID.getName()))) {
			throw new SystemException(ErrorType.VALIDATION_FAILED, "PayId should not be empty");
		} else if (fields.get(FieldType.PAY_ID.getName()) != null &&  !(fields.get(FieldType.PAY_ID.getName()).length() == 16)) {
			throw new SystemException(ErrorType.VALIDATION_FAILED, "PayId should be 16 digit");
		}else if (fields.get(FieldType.PAY_ID.getName()) != null &&  !fields.get(FieldType.PAY_ID.getName()).matches("[0-9]+")) {
			throw new SystemException(ErrorType.VALIDATION_FAILED, "PayId should be numeric only");
		}
		// ORDER_ID
		if (StringUtils.isEmpty(fields.get(FieldType.ORDER_ID.getName()))) {
			throw new SystemException(ErrorType.VALIDATION_FAILED, "OrderId should not be empty");
		} else if (fields.get(FieldType.ORDER_ID.getName()) !=null && !(fields.get(FieldType.ORDER_ID.getName()).length() >=1 && fields.get(FieldType.ORDER_ID.getName()).length()<=50)) {
			throw new SystemException(ErrorType.VALIDATION_FAILED, "Invalid OrderId");
		} else if (fields.get(FieldType.ORDER_ID.getName()) != null && Pattern.compile("[^a-z_0-9 ]", Pattern.CASE_INSENSITIVE).matcher(fields.get(FieldType.ORDER_ID.getName())).find()) {
			throw new SystemException(ErrorType.VALIDATION_FAILED, "Order Id not accept special character");
		} 
		
		// ACC_NO
		if (StringUtils.isEmpty(fields.get(FieldType.ACC_NO.getName()))) {
			throw new SystemException(ErrorType.VALIDATION_FAILED, "ACC no should not be empty");
		}else if (null != fields.get(FieldType.ACC_NO.getName()) && !(fields.get(FieldType.ACC_NO.getName()).length() >=6 && fields.get(FieldType.ACC_NO.getName()).length()<=24)) {
			throw new SystemException(ErrorType.VALIDATION_FAILED, "ACC no min-max length exceeded");
		} else if (null != fields.get(FieldType.ACC_NO.getName()) && !fields.get(FieldType.ACC_NO.getName()).matches("[0-9]+")) {
			throw new SystemException(ErrorType.VALIDATION_FAILED, "ACC no should be numeric only");
		}
		// PAY_TYPE
		if (StringUtils.isEmpty(fields.get(FieldType.PAY_TYPE.getName()))) {
			throw new SystemException(ErrorType.VALIDATION_FAILED, "Paytype should not be empty");
		} else if (fields.get(FieldType.PAY_TYPE.getName()) !=null && !fields.get(FieldType.PAY_TYPE.getName()).equals("FIAT")) {
			throw new SystemException(ErrorType.VALIDATION_FAILED, "Invalid paytype");
		}
		
		// ACC_CITY_NAME
		if (StringUtils.isEmpty(fields.get(FieldType.ACC_CITY_NAME.getName()))) {
			throw new SystemException(ErrorType.VALIDATION_FAILED, "Acc city name should not be empty");
		} else if (fields.get(FieldType.ACC_CITY_NAME.getName()) !=null && !(fields.get(FieldType.ACC_CITY_NAME.getName()).length() >=2 && fields.get(FieldType.ACC_CITY_NAME.getName()).length()<=50)) {
			throw new SystemException(ErrorType.VALIDATION_FAILED, "Invalid acc city name");
		} else if (fields.get(FieldType.ACC_CITY_NAME.getName()) !=null && !(fields.get(FieldType.ACC_CITY_NAME.getName()).matches("[a-zA-Z]+"))) {
			throw new SystemException(ErrorType.VALIDATION_FAILED, "Invalid acc city name");
		}
		
		
		
		// CUST_PHONE
		if (StringUtils.isEmpty(fields.get(FieldType.CUST_PHONE.getName()))) {
			throw new SystemException(ErrorType.VALIDATION_FAILED, "cust phone name should not be empty");
		} else if (fields.get(FieldType.CUST_PHONE.getName()) !=null && !(fields.get(FieldType.CUST_PHONE.getName()).length() >=8 && fields.get(FieldType.CUST_PHONE.getName()).length()<=15)) {
			throw new SystemException(ErrorType.VALIDATION_FAILED, "cust phone min-max length exceeded");
		} else if (fields.get(FieldType.CUST_PHONE.getName()) !=null && !(fields.get(FieldType.CUST_PHONE.getName()).matches("[0-9]+"))) {
			throw new SystemException(ErrorType.VALIDATION_FAILED, "cust phone should be numeric only");
		}
		
		// ACC_NAME
		if (StringUtils.isEmpty(fields.get(FieldType.ACC_NAME.getName()))) {
			throw new SystemException(ErrorType.VALIDATION_FAILED, "acc name should not be empty");
		} else if (fields.get(FieldType.ACC_NAME.getName()) !=null && !(fields.get(FieldType.ACC_NAME.getName()).length() >=2 && fields.get(FieldType.ACC_NAME.getName()).length()<=50)) {
			throw new SystemException(ErrorType.VALIDATION_FAILED, "acc name min-max length exceeded");
		}else if (fields.get(FieldType.ACC_NAME.getName()) !=null && !(fields.get(FieldType.ACC_NAME.getName()).matches("[a-zA-Z ]+"))) {
			throw new SystemException(ErrorType.VALIDATION_FAILED, "Invalid Acc Name or Acc name accept only alphabets");
		}
		
		// REMARKS
		if (StringUtils.isEmpty(fields.get(FieldType.REMARKS.getName()))) {
			throw new SystemException(ErrorType.VALIDATION_FAILED, "remarks should not be empty");
		} else if (fields.get(FieldType.REMARKS.getName()) !=null && !(fields.get(FieldType.REMARKS.getName()).length() >=1 && fields.get(FieldType.REMARKS.getName()).length()<=1024)) {
			throw new SystemException(ErrorType.VALIDATION_FAILED, "remarks min-max length exceeded");
		}
		
		// CURRENCY_CODE
		if (StringUtils.isEmpty(fields.get(FieldType.CURRENCY_CODE.getName()))) {
			throw new SystemException(ErrorType.VALIDATION_FAILED, "currency_code should not be empty");
		} else if (fields.get(FieldType.CURRENCY_CODE.getName()) !=null && !(fields.get(FieldType.CURRENCY_CODE.getName()).length() >=3 && fields.get(FieldType.CURRENCY_CODE.getName()).length()<=8)) {
			throw new SystemException(ErrorType.VALIDATION_FAILED, "currency_code min-max length exceeded");
		} else if (fields.get(FieldType.CURRENCY_CODE.getName()) !=null && !(fields.get(FieldType.CURRENCY_CODE.getName()).matches("[0-9]+"))) {
			throw new SystemException(ErrorType.VALIDATION_FAILED, "currency_code should be numeric only");
		} 
		
		String allCurrency = "356,704,608,458,764";
		List<String> currencyList = Arrays.asList(allCurrency.split(","));
		if (null != fields.get(FieldType.CURRENCY_CODE.getName()) &&  !currencyList.contains(fields.get(FieldType.CURRENCY_CODE.getName()))) {
			throw new SystemException(ErrorType.VALIDATION_FAILED, "Invalid currency code");
		}
		
		//IFSC
		if (StringUtils.isEmpty(fields.get(FieldType.IFSC.getName()))) {
			throw new SystemException(ErrorType.VALIDATION_FAILED, "ifsc should not be empty");
		} else if (fields.get(FieldType.IFSC.getName()) !=null && !(fields.get(FieldType.IFSC.getName()).length() >=9 && fields.get(FieldType.IFSC.getName()).length()<=18)) {
			throw new SystemException(ErrorType.VALIDATION_FAILED, "ifsc min-max length exceeded");
		} else if(fields.get(FieldType.IFSC.getName()) !=null && fields.get(FieldType.IFSC.getName()).matches(".*[^a-zA-Z0-9].*")){ 
			throw new SystemException(ErrorType.VALIDATION_FAILED, "Ifsc should not accept any special character");
		}else if (fields.get(FieldType.IFSC.getName()) !=null && !(fields.get(FieldType.IFSC.getName()).matches(".*[a-zA-Z]+.*") && fields.get(FieldType.IFSC.getName()).matches(".*\\d+.*"))) {
			throw new SystemException(ErrorType.VALIDATION_FAILED, "Ifsc should accept only alphanumeric");
		}
		
		// ACC_PROVINCE
		if (StringUtils.isEmpty(fields.get(FieldType.ACC_PROVINCE.getName()))) {
			throw new SystemException(ErrorType.VALIDATION_FAILED, "acc_province should not be empty");
		} else if (fields.get(FieldType.ACC_PROVINCE.getName()) !=null && !(fields.get(FieldType.ACC_PROVINCE.getName()).length() >=2 && fields.get(FieldType.ACC_PROVINCE.getName()).length()<=100)) {
			throw new SystemException(ErrorType.VALIDATION_FAILED, "acc_province min-max length exceeded");
		} else if (fields.get(FieldType.ACC_PROVINCE.getName()) !=null && !(fields.get(FieldType.ACC_PROVINCE.getName()).matches("[a-zA-Z]+"))) {
			throw new SystemException(ErrorType.VALIDATION_FAILED, "acc_province accept only characters");
		}
		//UDF13
		if (StringUtils.isEmpty(fields.get(FieldType.UDF13.getName()))) {
			throw new SystemException(ErrorType.VALIDATION_FAILED, "udf13 should not be empty");
		} else if (fields.get(FieldType.UDF13.getName()) !=null && !(fields.get(FieldType.UDF13.getName()).equals("PO"))) {
			throw new SystemException(ErrorType.VALIDATION_FAILED, "invalid udf13");
		}
		
		//AMOUNT
		if (StringUtils.isEmpty(fields.get(FieldType.AMOUNT.getName()))) {
			throw new SystemException(ErrorType.VALIDATION_FAILED, "amount should not be empty");
		} else if (fields.get(FieldType.AMOUNT.getName()) !=null && !(fields.get(FieldType.AMOUNT.getName()).matches("[0-9]+"))) {
			throw new SystemException(ErrorType.VALIDATION_FAILED, "amount should be numeric only");
		} else if (fields.get(FieldType.AMOUNT.getName()) !=null && !(fields.get(FieldType.AMOUNT.getName()).length() >= 3 && fields.get(FieldType.AMOUNT.getName()).length() <= 12)) {
			throw new SystemException(ErrorType.VALIDATION_FAILED, "amount min-max length exceeded");
		} else if (fields.get(FieldType.AMOUNT.getName()) != null) {
			String amount = fields.get(FieldType.AMOUNT.getName());
			if (Double.valueOf(amount.replace(".", "")) <= 0) {
				throw new SystemException(ErrorType.VALIDATION_FAILED, "amount should be greater than 0");
			}
		}
		
		// TOTAL_AMOUNT
		if (StringUtils.isEmpty(fields.get(FieldType.TOTAL_AMOUNT.getName()))) {
			throw new SystemException(ErrorType.VALIDATION_FAILED, "total_amount should not be empty");
		} else if (fields.get(FieldType.TOTAL_AMOUNT.getName()) !=null && !(fields.get(FieldType.TOTAL_AMOUNT.getName()).matches("[0-9]+"))) {
			throw new SystemException(ErrorType.VALIDATION_FAILED, "total_amount should be numeric only");
		} else if (fields.get(FieldType.TOTAL_AMOUNT.getName()) !=null && !(fields.get(FieldType.TOTAL_AMOUNT.getName()).length() >= 3 && fields.get(FieldType.TOTAL_AMOUNT.getName()).length() <= 12)) {
			throw new SystemException(ErrorType.VALIDATION_FAILED, "total_amount min-max length exceeded");
		} else if (fields.get(FieldType.TOTAL_AMOUNT.getName()) != null) {
			String amount = fields.get(FieldType.TOTAL_AMOUNT.getName());
			if (Double.valueOf(amount.replace(".", "")) <= 0) {
				throw new SystemException(ErrorType.VALIDATION_FAILED, "total_amount should be greater than 0");
			}
		} 
		
		if(fields.get(FieldType.TOTAL_AMOUNT.getName()) != null && fields.get(FieldType.AMOUNT.getName()) != null) {
			String totalAmount = fields.get(FieldType.TOTAL_AMOUNT.getName());
			String amount = fields.get(FieldType.AMOUNT.getName());
			if (Double.valueOf(totalAmount) < Double.valueOf(amount)) {
				throw new SystemException(ErrorType.VALIDATION_FAILED, "total_amount should be greater than and equal to amount");
			}
		}
		
		//BANK_BRANCH
		if (StringUtils.isEmpty(fields.get(FieldType.BANK_BRANCH.getName()))) {
			throw new SystemException(ErrorType.VALIDATION_FAILED, "bank_branch should not be empty");
		} else if (fields.get(FieldType.BANK_BRANCH.getName()) !=null && !(fields.get(FieldType.BANK_BRANCH.getName()).length() >=2 && fields.get(FieldType.BANK_BRANCH.getName()).length()<=100)) {
			throw new SystemException(ErrorType.VALIDATION_FAILED, "bank_branch min-max length exceeded");
		} else if (fields.get(FieldType.BANK_BRANCH.getName()) !=null && !(!(fields.get(FieldType.HASH.getName()).matches(".*[a-zA-Z]+.*") && fields.get(FieldType.HASH.getName()).matches(".*\\d+.*")) || !(fields.get(FieldType.HASH.getName()).matches("[a-zA-Z]+")))) {
			throw new SystemException(ErrorType.VALIDATION_FAILED, "invalid bank_branch");
		}
		//CUST_EMAIL
		if (StringUtils.isEmpty(fields.get(FieldType.CUST_EMAIL.getName()))) {
			throw new SystemException(ErrorType.VALIDATION_FAILED, "cust_email should not be empty");
		} else if (fields.get(FieldType.CUST_EMAIL.getName()) !=null && !(fields.get(FieldType.CUST_EMAIL.getName()).length() >=6 && fields.get(FieldType.CUST_EMAIL.getName()).length()<=120)) {
			throw new SystemException(ErrorType.VALIDATION_FAILED, "cust_email min-max length exceeded");
		} else if (fields.get(FieldType.CUST_EMAIL.getName()) !=null && !Pattern.compile(emailRegex).matcher(fields.get(FieldType.CUST_EMAIL.getName())).matches()) {
			throw new SystemException(ErrorType.VALIDATION_FAILED, "invalid cust_email");
		}
		
		//BANK_CODE
		if (StringUtils.isEmpty(fields.get(FieldType.BANK_CODE.getName()))) {
			throw new SystemException(ErrorType.VALIDATION_FAILED, "bank_code should not be empty");
		} else if (fields.get(FieldType.BANK_CODE.getName()) !=null && !(fields.get(FieldType.BANK_CODE.getName()).length() >=3 && fields.get(FieldType.BANK_CODE.getName()).length()<=10)) {
			throw new SystemException(ErrorType.VALIDATION_FAILED, "bank_code min-max length exceeded");
		}
		
		//CUST_NAME
		if (StringUtils.isEmpty(fields.get(FieldType.CUST_NAME.getName()))) {
			throw new SystemException(ErrorType.VALIDATION_FAILED, "cust_name should not be empty");
		} else if (fields.get(FieldType.CUST_NAME.getName()) !=null && !(fields.get(FieldType.CUST_NAME.getName()).length() >=1 && fields.get(FieldType.CUST_NAME.getName()).length()<=150)) {
			throw new SystemException(ErrorType.VALIDATION_FAILED, "cust_name min-max length exceeded");
		} else if (fields.get(FieldType.CUST_NAME.getName()) !=null && !(fields.get(FieldType.CUST_NAME.getName()).matches("[a-zA-Z ]+"))) {
			throw new SystemException(ErrorType.VALIDATION_FAILED, "cust_name accept only alphabets");
		}
		
		
		//TRANSFER_TYPE
		if (fields.get(FieldType.CURRENCY_CODE.getName()) !=null && (fields.get(FieldType.CURRENCY_CODE.getName()).equals("356"))) {
			 if (StringUtils.isEmpty(fields.get(FieldType.TRANSFER_TYPE.getName()))) {
					throw new SystemException(ErrorType.VALIDATION_FAILED, "transfer_type should not be empty");
				} else if (fields.get(FieldType.TRANSFER_TYPE.getName()) !=null && !(fields.get(FieldType.TRANSFER_TYPE.getName()).equals("IMPS") || fields.get(FieldType.TRANSFER_TYPE.getName()).equals("NEFT"))) {
					throw new SystemException(ErrorType.VALIDATION_FAILED, "invalid transfer_type");
				}
		}
		
		if (fields.get(FieldType.ADF1.getName()) !=null && !(fields.get(FieldType.ADF1.getName()).length() >=1 && fields.get(FieldType.ADF1.getName()).length()<=32)) {
			throw new SystemException(ErrorType.VALIDATION_FAILED, "adf1 min-max length exceeded");
		} else if (fields.get(FieldType.ADF2.getName()) !=null && !(fields.get(FieldType.ADF2.getName()).length() >=1 && fields.get(FieldType.ADF2.getName()).length()<=32)) {
			throw new SystemException(ErrorType.VALIDATION_FAILED, "adf2 min-max length exceeded");
		} else if (fields.get(FieldType.ADF3.getName()) !=null && !(fields.get(FieldType.ADF3.getName()).length() >=1 && fields.get(FieldType.ADF3.getName()).length()<=32)) {
			throw new SystemException(ErrorType.VALIDATION_FAILED, "adf3 min-max length exceeded");
		} else if (fields.get(FieldType.ADF4.getName()) !=null && !(fields.get(FieldType.ADF4.getName()).length() >=1 && fields.get(FieldType.ADF4.getName()).length()<=32)) {
			throw new SystemException(ErrorType.VALIDATION_FAILED, "adf4 min-max length exceeded");
		}else if (fields.get(FieldType.ADF5.getName()) !=null && !(fields.get(FieldType.ADF5.getName()).length() >=1 && fields.get(FieldType.ADF5.getName()).length()<=32)) {
			throw new SystemException(ErrorType.VALIDATION_FAILED, "adf5 min-max length exceeded");
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
			throw new SystemException(ErrorType.INVAILD_BANKCODE_OR_CURRENCY,
					"bank Code or currencyCode is in Invaild ");

		}

	}

	private void amountDeductionFromWallet(Fields fields) throws SystemException {
		MerchantWalletPODTO merchantWalletPODTO = new MerchantWalletPODTO();
		merchantWalletPODTO.setPayId(fields.get(FieldType.PAY_ID.getName()));

		merchantWalletPODTO
				.setCurrency(multCurrencyCodeDao.getCurrencyNamebyCode(fields.get(FieldType.CURRENCY_CODE.getName())));

		MerchantWalletPODTO merchantWalletPODTO1 = merchantWalletPODao.findByPayId(merchantWalletPODTO);
		if (merchantWalletPODTO1 == null) {
			throw new SystemException(ErrorType.PO_WALLET_IS_NOT_FOUND,
					"Merchant wallet is not found for  PayId=" + fields.get(FieldType.PAY_ID.getName()));

		}

		double finalAmount = Double.valueOf(merchantWalletPODTO1.getFinalBalance());
		long amount = Double.valueOf(fields.get((FieldType.AMOUNT.getName()))).longValue();
		double totalamount = Double
				.valueOf(Amount.toDecimal(String.valueOf(amount), fields.get(FieldType.CURRENCY_CODE.getName())));
		if (finalAmount < totalamount)
			throw new SystemException(ErrorType.INSUFFICIENT_BALANCE, "Due to merchant insufficient balance");
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
		//Added by deep
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

		if ((fields.contains(FieldType.IS_MERCHANT_HOSTED.getName()))
				&& !(StringUtils.isEmpty(fields.get(FieldType.IS_MERCHANT_HOSTED.getName())))
				&& (fields.get(FieldType.IS_MERCHANT_HOSTED.getName()).equals("Y"))) {
			if (fields.get(FieldType.TXNTYPE.getName()).equals(TransactionType.SALE.getName())) {
				if (!user.isAllowSaleDuplicate()) {
					fields.validateDupicateSaleOrderIdPayout(fields);
				}

			}
		}
	}

	private void saveInvalidTransactionPayout(Fields fields, String origTxnType) {
		fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), origTxnType);

		try {
			payoutUpdateProcessor.preProcess(fields);
			payoutUpdateProcessor.prepareInvalidTransactionForStorage(fields);
		} catch (SystemException systemException) {
			logger.error("Unable to save invalid transaction", systemException);
		} catch (Exception exception) {
			logger.error("Unhandaled error", exception);
		}
	}

	public String ResponsePostPayout(Fields fields) {

		logger.info("aSas" + fields.getFieldsAsString());
		resolveStatus(fields);

		if (StringUtils.isNotBlank(fields.get(FieldType.RESPONSE_CODE.getName()))
				&& fields.get(FieldType.RESPONSE_CODE.getName()).equals(ErrorType.DUPLICATE_RESPONSE.getCode())) {
			return null;
		}
		if (StringUtils.isNotBlank(fields.get(FieldType.STATUS.getName())) && fields.get(FieldType.STATUS.getName())
				.equalsIgnoreCase(StatusTypePayout.REQUEST_ACCEPTED.getName())) {
			if (StringUtils.isNotBlank(fields.get(FieldType.RESPONSE_MESSAGE.getName()))
					&& fields.get(FieldType.RESPONSE_MESSAGE.getName())
							.equalsIgnoreCase(ErrorType.PROCESSING.getResponseMessage())) {
				// fields.put(FieldType.RESPONSE_MESSAGE.getName(), "Request Accepted");
				fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.PROCESSING.getResponseCode());
				fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.PROCESSING.getResponseMessage());
			}
		}

		if (StringUtils.isNotBlank(fields.get(FieldType.STATUS.getName()))
				&& fields.get(FieldType.STATUS.getName()).equalsIgnoreCase(StatusTypePayout.PENDING.getName())) {
			if (StringUtils.isNotBlank(fields.get(FieldType.RESPONSE_MESSAGE.getName())) && fields
					.get(FieldType.RESPONSE_MESSAGE.getName()).equalsIgnoreCase(StatusTypePayout.PENDING.getName())) {
				// fields.put(FieldType.RESPONSE_MESSAGE.getName(),
				// StatusTypePayout.PENDING.getName());

				fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.PENDING.getResponseCode());
				fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.PENDING.getResponseMessage());
			}
		}

		if (StringUtils.isNotBlank(fields.get(FieldType.STATUS.getName()))
				&& fields.get(FieldType.STATUS.getName()).equalsIgnoreCase(StatusTypePayout.SUCCESS.getName())) {
			if (StringUtils.isNotBlank(fields.get(FieldType.RESPONSE_MESSAGE.getName())) && fields
					.get(FieldType.RESPONSE_MESSAGE.getName()).equalsIgnoreCase(StatusTypePayout.SUCCESS.getName())) {
				// fields.put(FieldType.RESPONSE_MESSAGE.getName(),
				// StatusTypePayout.SUCCESS.getName());
				fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SUCCESS.getResponseCode());
				fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.SUCCESS.getResponseMessage());
			}
		}

		if (StringUtils.isNotBlank(fields.get(FieldType.STATUS.getName()))
				&& fields.get(FieldType.STATUS.getName()).equalsIgnoreCase(StatusTypePayout.FAIL.getName())) {
			if (StringUtils.isNotBlank(fields.get(FieldType.RESPONSE_MESSAGE.getName())) && fields
					.get(FieldType.RESPONSE_MESSAGE.getName()).equalsIgnoreCase(StatusTypePayout.FAIL.getName())) {
				// fields.put(FieldType.RESPONSE_MESSAGE.getName(),
				// StatusTypePayout.FAIL.getName());
				fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.FAILED.getResponseCode());
				fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.FAILED.getResponseMessage());
			}
		}

		if (StringUtils.isNotBlank(fields.get(FieldType.STATUS.getName()))
				&& fields.get(FieldType.STATUS.getName()).equalsIgnoreCase("Sent to Bank")) {
			if (StringUtils.isNotBlank(fields.get(FieldType.RESPONSE_MESSAGE.getName()))
					&& fields.get(FieldType.RESPONSE_MESSAGE.getName()).equalsIgnoreCase("SUCCESS")) {
				fields.put(FieldType.RESPONSE_MESSAGE.getName(), "Request Accepted");
			}
		}

		if (StringUtils.isNotBlank(fields.get(FieldType.STATUS.getName()))
				&& fields.get(FieldType.STATUS.getName()).equalsIgnoreCase("Invalid")) {
			if (StringUtils.isNotBlank(fields.get(FieldType.RESPONSE_MESSAGE.getName()))
					&& fields.get(FieldType.RESPONSE_MESSAGE.getName()).equalsIgnoreCase("Payout Not Enabled")) {
				fields.put(FieldType.RESPONSE_MESSAGE.getName(), "Payout Not Enabled");
			}
		}

		if (StringUtils.isNotBlank(fields.get(FieldType.STATUS.getName()))
				&& fields.get(FieldType.STATUS.getName()).equalsIgnoreCase("Failed")) {
			if (StringUtils.isNotBlank(fields.get(FieldType.RESPONSE_MESSAGE.getName()))
					&& fields.get(FieldType.RESPONSE_MESSAGE.getName()).equalsIgnoreCase("SUCCESS")) {
				fields.put(FieldType.RESPONSE_MESSAGE.getName(), "Transaction Failed");
			}
		}

		if (fields.get(FieldType.STATUS.getName()).equalsIgnoreCase(StatusTypePayout.FAIL.getName())
				&& fields.get(FieldType.RESPONSE_MESSAGE.getName()).equals(ErrorType.PO_FRM.getInternalMessage())) {
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.PO_FRM.getResponseCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.PO_FRM.getInternalMessage());
		}

		if (fields.get(FieldType.STATUS.getName()).equalsIgnoreCase(StatusTypePayout.FAIL.getName())
				&& fields.get(FieldType.RESPONSE_MESSAGE.getName())
						.equals(ErrorType.PO_FRM_MIN_TICKET_SIZE.getInternalMessage())) {
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.PO_FRM_MIN_TICKET_SIZE.getResponseCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.PO_FRM_MIN_TICKET_SIZE.getInternalMessage());
		}

		if (fields.get(FieldType.STATUS.getName()).equalsIgnoreCase(StatusTypePayout.FAIL.getName())
				&& fields.get(FieldType.RESPONSE_MESSAGE.getName())
						.equals(ErrorType.PO_FRM_MAX_TICKET_SIZE.getInternalMessage())) {
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.PO_FRM_MAX_TICKET_SIZE.getResponseCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.PO_FRM_MAX_TICKET_SIZE.getInternalMessage());
		}

		if (fields.get(FieldType.STATUS.getName()).equalsIgnoreCase(StatusTypePayout.FAIL.getName()) && fields
				.get(FieldType.RESPONSE_MESSAGE.getName()).equals(ErrorType.PO_FRM_DAILY_LIMIT.getInternalMessage())) {
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.PO_FRM_DAILY_LIMIT.getResponseCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.PO_FRM_DAILY_LIMIT.getInternalMessage());
		}

		if (fields.get(FieldType.STATUS.getName()).equalsIgnoreCase(StatusTypePayout.FAIL.getName()) && fields
				.get(FieldType.RESPONSE_MESSAGE.getName()).equals(ErrorType.PO_FRM_DAILY_VOLUME.getInternalMessage())) {
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.PO_FRM_DAILY_VOLUME.getResponseCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.PO_FRM_DAILY_VOLUME.getInternalMessage());
		}

		if (fields.get(FieldType.STATUS.getName()).equalsIgnoreCase(StatusTypePayout.FAIL.getName()) && fields
				.get(FieldType.RESPONSE_MESSAGE.getName()).equals(ErrorType.PO_FRM_WEEKLY_LIMIT.getInternalMessage())) {
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.PO_FRM_WEEKLY_LIMIT.getResponseCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.PO_FRM_WEEKLY_LIMIT.getInternalMessage());
		}

		if (fields.get(FieldType.STATUS.getName()).equalsIgnoreCase(StatusTypePayout.FAIL.getName())
				&& fields.get(FieldType.RESPONSE_MESSAGE.getName())
						.equals(ErrorType.PO_FRM_WEEKLY_VOLUME.getInternalMessage())) {
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.PO_FRM_WEEKLY_VOLUME.getResponseCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.PO_FRM_WEEKLY_VOLUME.getInternalMessage());
		}

		if (fields.get(FieldType.STATUS.getName()).equalsIgnoreCase(StatusTypePayout.FAIL.getName())
				&& fields.get(FieldType.RESPONSE_MESSAGE.getName())
						.equals(ErrorType.PO_FRM_MONTHLY_LIMIT.getInternalMessage())) {
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.PO_FRM_MONTHLY_LIMIT.getResponseCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.PO_FRM_MONTHLY_LIMIT.getInternalMessage());
		}

		if (fields.get(FieldType.STATUS.getName()).equalsIgnoreCase(StatusTypePayout.FAIL.getName())
				&& fields.get(FieldType.RESPONSE_MESSAGE.getName())
						.equals(ErrorType.PO_FRM_MONTHLY_VOLUME.getInternalMessage())) {
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.PO_FRM_MONTHLY_VOLUME.getResponseCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.PO_FRM_MONTHLY_VOLUME.getInternalMessage());
		}

		String merchantHostedFlag = fields.get(FieldType.IS_MERCHANT_HOSTED.getName());
		logger.info("UPI S2S ResponsePost method IS_MERCHANT_HOSTED ::: {}", merchantHostedFlag);

		try {

			fields.remove(FieldType.HASH.getName());
			transactionResponser.removeInvalidResponseFields(fields);
			fields.remove("SURCHARGE_AMOUNT");
			transactionResponser.addHash(fields);
			fields.logAllFields(" UPI S2s Merchant Hosted : Plain text response for  merchant: ");
			String encData = merchantHostedUtils.encryptMerchantResponse(fields);
			fields.put(FieldType.ENCDATA.getName(), encData);
			logger.info(" UPI S2s  Merchant Hosted : encrypted response to merchant " + encData);
			return encData;

		} catch (Exception e) {
			logger.error("Exception : UPI S2s  Merchant Hosted  ", e);
		}

		return null;

	}

	public void resolveStatus(Fields fields) {
		String status = fields.get(FieldType.STATUS.getName());
		logger.info("resolveStatus :" + status);
		if (StringUtils.isNotBlank(status) && (status.equalsIgnoreCase(StatusType.CAPTURED.getName())
				|| status.equalsIgnoreCase(StatusType.FAILED.getName())
				|| status.equalsIgnoreCase("REFUND_INITIATED"))) {
		} else if (StringUtils.isNotBlank(status) && (status.equalsIgnoreCase(StatusType.DENIED.getName()))) {
			// fields.put(FieldType.STATUS.getName(), StatusType.PENDING.getName());
			fields.put(FieldType.STATUS.getName(), "REQUEST ACCEPTED");
			// fields.put(FieldType.STATUS.getName(), StatusType.SENT_TO_BANK.getName());
			fields.put(FieldType.RESPONSE_CODE.getName(), "026");
		} else if (status.equalsIgnoreCase("REQUEST ACCEPTED") || status.equalsIgnoreCase("Pending")
				|| status.equalsIgnoreCase(StatusType.SENT_TO_BANK.getName())) {
			fields.put(FieldType.STATUS.getName(), "REQUEST ACCEPTED");
			fields.put(FieldType.RESPONSE_CODE.getName(), "000");
		} else {
			fields.put(FieldType.STATUS.getName(), StatusType.FAILED.getName());
		}
	}

	public static void main(String[] args) {
		String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" + // part before @
				"(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
		Fields params = new Fields();
		params.put("CURRENCY_CODE","356");
		params.put("PAY_ID","1066240304084030");
		params.put("PAY_TYPE","FIAT");
		params.put("ACC_NO","62364212896");
		params.put("ACC_CITY_NAME","Delhi");
		params.put("CUST_PHONE","DDDDDDDDDDD");
		params.put("REMARKS","Pls Approve");
		params.put("UDF13","PO");
		params.put("ACC_NAME","State Bank of India");
		params.put("TRANSFER_TYPE","IMPS");
		params.put("IFSC","SBIN0020962");
		params.put("ACC_PROVINCE","Delhi");
		params.put("ORDER_ID","S20240430040814");
		params.put("AMOUNT","15000");
		params.put("BANK_BRANCH","Delhi");
		params.put("CUST_EMAIL","");
		params.put("BANK_CODE","1013");
		params.put("CUST_NAME","");
		params.put("TOTAL_AMOUNT","15000");
		
		params.put("RETURN_URL","http://localhost:9191/jspWebKit/response.jsp");
		
//		try {
//			new RestCallProcessor().validateRequestFields(params);
//		} catch (SystemException e) {
//			System.out.println("--"+e);
//		}

		Fields fields = new Fields();
		fields.put(FieldType.IFSC.getName(), "");
//		if (StringUtils.isEmpty(fields.get(FieldType.IFSC.getName()))) {
//			System.out.println("ifsc should not be empty");
//		} else if (fields.get(FieldType.IFSC.getName()) !=null && !(fields.get(FieldType.IFSC.getName()).length() >=9 && fields.get(FieldType.IFSC.getName()).length()<=18)) {
//			System.out.println("ifsc min-max length exceeded");
//		} else if (fields.get(FieldType.IFSC.getName()) !=null && !(fields.get(FieldType.IFSC.getName()).matches(".*[a-zA-Z]+.*") && fields.get(FieldType.IFSC.getName()).matches(".*\\d+.*"))) {
//			System.out.println("Ifsc should accept only alphanumeric");
//		}
		
		//System.out.println("++"+StringUtils.isAlphanumeric(fields.get(FieldType.IFSC.getName())));
		//System.out.println("--"+(fields.get(FieldType.IFSC.getName()).matches(".*[a-zA-Z]+.*") && fields.get(FieldType.IFSC.getName()).matches(".*\\d+.*")));
		
		
		fields.put(FieldType.CUST_NAME.getName(), "Sonu Chaudhari");
		fields.put(FieldType.TOTAL_AMOUNT.getName(), "100.01");
		fields.put(FieldType.AMOUNT.getName(), "100.01");
		
		
		if (fields.get(FieldType.CUST_NAME.getName()) !=null && !(fields.get(FieldType.CUST_NAME.getName()).matches("[a-zA-Z ]+"))) {
			System.out.println("cust_name accept only alphabets");
		}
		
		
	
	}
	

}
