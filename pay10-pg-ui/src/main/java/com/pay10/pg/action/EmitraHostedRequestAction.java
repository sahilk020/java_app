package com.pay10.pg.action;

import java.math.BigDecimal;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.RegExUtils;
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
import com.pay10.commons.dao.RouterConfigurationDao;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.AccountCurrencyRegion;
import com.pay10.commons.user.CardHolderType;
import com.pay10.commons.user.Invoice;
import com.pay10.commons.user.RouterConfiguration;
import com.pay10.commons.user.Token;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.Amount;
import com.pay10.commons.util.ConfigurationConstants;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.CustTransactionAuthentication;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.LocateCountryName;
import com.pay10.commons.util.MacUtil;
import com.pay10.commons.util.ModeType;
import com.pay10.commons.util.MopType;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.PaymentTypeProvider;
import com.pay10.commons.util.PaymentTypeTransactionProvider;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.SystemConstants;
import com.pay10.commons.util.TransactionManager;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.action.beans.SessionCleaner;
import com.pay10.pg.action.service.ActionService;
import com.pay10.pg.action.service.PgActionServiceFactory;
import com.pay10.pg.action.service.PrepareRequestParemeterService;
import com.pay10.pg.action.service.RequestUrlValidator;
import com.pay10.pg.core.fraudPrevention.util.FraudPreventionUtil;
import com.pay10.pg.core.pageintegrator.GeneralValidator;
import com.pay10.pg.core.tokenization.TokenManager;
import com.pay10.pg.core.util.RequestCreator;
import com.pay10.pg.core.util.ResponseCreator;
import com.pay10.pg.core.util.SurchargeAmountCalculator;
import com.pay10.pg.core.util.TransactionResponser;
import com.pay10.pg.core.util.UpdateProcessor;

/**
 * @author Sunil, Rahul, Shaiwal
 *
 */

public class EmitraHostedRequestAction extends AbstractSecureAction implements ServletRequestAware {

	@Autowired
	TransactionControllerServiceProvider transactionControllerServiceProvider;

	@Autowired
	private ResponseCreator responseCreator;

	@Autowired
	private RouterConfigurationDao routerConfigurationDao;

	@Autowired
	private UpdateProcessor updateProcessor;

	@Autowired
	private UserDao userDao;

	@Autowired
	TransactionResponser transactionResponser;

	@Autowired
	private RequestCreator requestCreator;

	@Autowired
	private TokenManager tokenManager;

	@Autowired
	private GeneralValidator generalValidator;

	@Autowired
	private PropertiesManager propertiesManager;

	@Autowired
	private Fields field;

	@Autowired
	private SurchargeAmountCalculator calculateSurchargeAmount;

	@Autowired
	@Qualifier("paymentTypeProvider")
	private PaymentTypeProvider paymentTypeProvider;

	@Autowired
	private PrepareRequestParemeterService prepareRequestParemeterService;

	@Autowired
	private RequestUrlValidator requestUrlValidator;

	@Autowired
	private MacUtil macUtil;

	private static Logger logger = LoggerFactory.getLogger(RequestAction.class.getName());
	private static final long serialVersionUID = 6526733830656374186L;
	private static final String pendingTxnStatus = "Sent to Bank-Enrolled";

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
	private String resellerId;
	private JSONObject supportedPaymentTypeJson;
	private Map<String, Object> supportedPaymentTypeMap = new HashMap<String, Object>();
	private Map<String, Object> cardPaymentTypeMap = new HashMap<String, Object>();
	private Map<String, Token> tokenMap = new HashMap<String, Token>();

	// Added By sweeety
	@Autowired
	private InvoiceDao invoiceDao;

	@Override
	public String execute() {
		try {
			// clean session
			sessionMap.invalidate();
			logger.info("Card Save Flag---------------->" + getCardsaveflag());

			// create fields for transaction
			ActionService service = PgActionServiceFactory.getActionService();
			Fields fields = service.prepareFields(request.getParameterMap());
			String fieldsAsString = fields.getFieldsAsString();

			logger.info("Merchant Row Request Fields : " + fieldsAsString);
			fields.put(FieldType.INTERNAL_REQUEST_FIELDS.getName(), fieldsAsString);
			logger.info("Raw Request Fields:  " + fields.getFieldsAsString());

			if (StringUtils.isBlank(fields.get(FieldType.HASH.getName()))) {

				throw new SystemException(ErrorType.VALIDATION_FAILED, "Invalid " + FieldType.HASH.getName());
			}
			generalValidator.validateReturnUrl(fields);
			generalValidator.validateHash(fields, true);

			logger.info("agains Merchant Row Request Fields : " + fields.getFieldsAsString());
			User user = userDao.findPayId(fields.get(FieldType.PAY_ID.getName()));
			if (StringUtils.isNotBlank(user.getResellerId())) {
				resellerId = user.getResellerId();
				fields.put((FieldType.RESELLER_ID.getName()), resellerId);
			}

			// changing amount from rupees into paise
			String amountInPaisa = String
					.valueOf(Math.round(Double.valueOf(fields.get(FieldType.AMOUNT.getName())) * 100));
			fields.put(FieldType.AMOUNT.getName(), amountInPaisa);
			sessionMap.put(Constants.FIELDS.getValue(), fields);

			logger.info("Order Id =   " + fields.get(FieldType.ORDER_ID.getName()) + "   payId = " + user.getPayId()
					+ " TXNTYPE =   " + fields.get(FieldType.TXNTYPE.getName()));
			// replace the user supplied transaction type with the mode type
			// assigned to the merchant
			setOrigTxnType(ModeType.getDefaultPurchaseTransaction(user.getModeType()).getName());
			fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), getOrigTxnType());

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
			sessionMap.put((FieldType.INTERNAL_HEADER_USER_AGENT.getName()), request.getHeader("User-Agent"));

			logger.info("Internal_Cust_IP for X-Forwarded-For: " + request.getHeader("X-Forwarded-For"));
			logger.info("Internal_Cust_IP for Remote Addr: " + request.getRemoteAddr());

			String ip = request.getHeader("X-Forwarded-For");
			String mac = StringUtils.isBlank(ip) ? "NA" : macUtil.getMackByIp(ip);
			fields.put((FieldType.INTERNAL_CUST_IP.getName()), ip);
			fields.put((FieldType.INTERNAL_CUST_MAC.getName()), mac);
			sessionMap.put(FieldType.INTERNAL_CUST_IP.getName(), ip);
			if (StringUtils.isNotBlank((String) sessionMap.get(FieldType.INTERNAL_HEADER_USER_AGENT.getName()))) {
				fields.put((FieldType.INTERNAL_HEADER_USER_AGENT.getName()),
						(String) sessionMap.get(FieldType.INTERNAL_HEADER_USER_AGENT.getName()));
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

			logger.info("Internal_Cust_country for Country Name: " + request.getHeader("CloudFront-Viewer-Country"));
			LocateCountryName lcName = new LocateCountryName();
			String countryCode = lcName.findCountryCode(request.getHeader("CloudFront-Viewer-Country"));
			if (StringUtils.isBlank(countryCode)) {
				countryCode = "NA";
			}

			logger.info("Internal_Cust_country for Country code: " + countryCode);

			// String countryCode = "NA";
			fields.put((FieldType.INTERNAL_CUST_COUNTRY_NAME.getName()),
					request.getHeader("CloudFront-Viewer-Country"));
			sessionMap.put(FieldType.INTERNAL_CUST_COUNTRY_NAME.getName(),
					request.getHeader("CloudFront-Viewer-Country"));

			if (countryCode.equals(CrmFieldConstants.INDIA_REGION_CODE.getValue())) {
				fields.put((FieldType.INTERNAL_TXN_AUTHENTICATION.getName()),
						CustTransactionAuthentication.SUCCESS.getAuthenticationName());
			} else {
				fields.put((FieldType.INTERNAL_TXN_AUTHENTICATION.getName()),
						CustTransactionAuthentication.PENDING.getAuthenticationName());
			}

			String upiIntent = fields.get(FieldType.UPI_INTENT.getName());
			logger.info("upi intent values :: " + upiIntent);
			requestUrlValidator.validateRequestUrl(fields, request);
			fields = requestCreator(fields);
			responseCode = fields.get(FieldType.RESPONSE_CODE.getName());

			if (upiIntent != null && upiIntent.equalsIgnoreCase("1")) {
				fields.put((FieldType.UPI_INTENT.getName()), upiIntent);
			}
			logger.info("fields " + fields.getFieldsAsString());
			logger.info("response code " + responseCode);
			if (StringUtils.isEmpty(getResponseCode())) {
				return Action.ERROR;
			} else if (getResponseCode().equals(ErrorType.SUCCESS.getCode())) {

				// These fields are being used at PaymentPage
				sessionMap.put((FieldType.PAY_ID.getName()), fields.get(FieldType.PAY_ID.getName()));
				sessionMap.put(FieldType.ORDER_ID.getName(), fields.get(FieldType.ORDER_ID.getName()));
				sessionMap.put(FieldType.CARD_HOLDER_NAME.getName(), fields.get(FieldType.CARD_HOLDER_NAME.getName()));
				sessionMap.put(FieldType.CUST_NAME.getName(), fields.get(FieldType.CUST_NAME.getName()));
				sessionMap.put(FieldType.CURRENCY_CODE.getName(), fields.get(FieldType.CURRENCY_CODE.getName()));
				sessionMap.put(FieldType.AMOUNT.getName(), fields.get(FieldType.AMOUNT.getName()));
				sessionMap.put(FieldType.CUST_PHONE.getName(), fields.get(FieldType.CUST_PHONE.getName()));

				// get String of supported payment types
				PaymentTypeTransactionProvider paymentTypeTransactionProvider = paymentTypeProvider
						.setSupportedPaymentOptions((user.getPayId()));
				setSupportedPaymentTypeMap(paymentTypeTransactionProvider.getSupportedPaymentTypeMap());
				setCardPaymentTypeMap(paymentTypeTransactionProvider.getSupportedCardTypeMap());

				sessionMap.put(Constants.PAYMENT_TYPE_MOP.getValue(),
						paymentTypeTransactionProvider.getSupportedPaymentTypeMap());
				sessionMap.put(Constants.CARD_PAYMENT_TYPE_MOP.getValue(), getCardPaymentTypeMap());
				List<RouterConfiguration> acquirerCount = routerConfigurationDao
						.findAcquirerByPayId(fields.get(FieldType.PAY_ID.getName()));

				if (acquirerCount.size() <= 0) {
					sessionMap.put(Constants.EXPRESS_PAY_FLAG.getValue(), false);
				} else if (StringUtils.isNotBlank(fields.get(user.getCardSaveParam()))) {

					sessionMap.put(Constants.EXPRESS_PAY_FLAG.getValue(), user.isExpressPayFlag());
				} else {
					sessionMap.put(Constants.EXPRESS_PAY_FLAG.getValue(), false);
				}

				sessionMap.put(Constants.IFRAME_PAYMENT_PAGE.getValue(), user.isIframePaymentFlag());
				sessionMap.put(FieldType.INTERNAL_ORIG_TXN_ID.getName(), fields.get(FieldType.TXN_ID.getName()));
				sessionMap.put(FieldType.OID.getName(), fields.get(FieldType.TXN_ID.getName()));
				// to set default language of payment page
				setDefaultLanguage(user.getDefaultLanguage());

				if (StringUtils.isBlank(user.getCardSaveParam())) {
					sessionMap.put(Constants.TOKEN.getValue(), "NA");
				} else if (StringUtils.isBlank(fields.get(user.getCardSaveParam()))) {
					sessionMap.put(Constants.TOKEN.getValue(), "NA");
				} else {
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

					String convertedamount = sessionMap.get(FieldType.AMOUNT.getName()).toString();
					String currency = sessionMap.get(FieldType.CURRENCY_CODE.getName()).toString();
					amount = Amount.toDecimal(convertedamount, currency);

					String paymentsRegion = fields.get(FieldType.PAYMENTS_REGION.getName());
					if (paymentsRegion == null || paymentsRegion.equals("")) {
						paymentsRegion = AccountCurrencyRegion.DOMESTIC.toString();
					}

					BigDecimal[] surADAmount = null;
					BigDecimal[] surCCAmount = null;
					BigDecimal[] surDCAmount = null;
					BigDecimal[] surWLAmount = null;
					BigDecimal[] surUPAmount = null;
					BigDecimal[] surNBAmount = null;
					BigDecimal[] surPCAmount = null;
                    BigDecimal[] surQRAmount = null;

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
					BigDecimal surchargeQRAmount = BigDecimal.ZERO;
	                BigDecimal qrTransSurcharge = BigDecimal.ZERO;

					String gst = "";

					if (fields.get(FieldType.INVOICE_ID.getName()) != null) {
						String invoiceId = fields.get(FieldType.INVOICE_ID.getName()).toString();
						logger.info("invoiceId={}", invoiceId);
						Invoice invoice = invoiceDao.findByInvoiceId(invoiceId);
						gst = invoice.getGst();
						logger.info("gst..={}", gst);
					}

					BigDecimal finalAmt = new BigDecimal(amount);
					String paymentTypeMops = sessionMap.get(Constants.PAYMENT_TYPE_MOP.getValue()).toString();
					if (paymentTypeMops.contains(Constants.PAYMENT_TYPE_AD.getValue())) {
						surADAmount = calculateSurchargeAmount.fetchADSurchargeDetails(amount, payId, paymentsRegion,
								gst);
						adTransSurcharge = surADAmount[0];
						surchargeADAmount = surADAmount[1];
						finalAmt = surchargeADAmount;
					}
					if (paymentTypeMops.contains(Constants.PAYMENT_TYPE_CC.getValue())) {
						Fields field = (Fields) sessionMap.get(Constants.FIELDS.getValue());
						logger.info("invoice id get in seesion map" + field.getFieldsAsString());
						surCCAmount = calculateSurchargeAmount.fetchCCSurchargeDetails(amount, payId, paymentsRegion,
								gst);
						ccTransSurcharge = surCCAmount[0];
						surchargeCCAmount = surCCAmount[1];
						finalAmt = surchargeCCAmount;
						logger.info("Calculating CCSurchargeDetails for Order Id = "
								+ sessionMap.get(FieldType.ORDER_ID.getName()) + " transAmount = " + surCCAmount[0]
								+ " actualTransactionAmount = " + surCCAmount[1]);
					}
					if (paymentTypeMops.contains(Constants.PAYMENT_TYPE_DC.getValue())) {
						Fields field = (Fields) sessionMap.get(Constants.FIELDS.getValue());
						logger.info("invoice id get in seesion map" + field.getFieldsAsString());

						surDCAmount = calculateSurchargeAmount.fetchDCSurchargeDetails(amount, payId, paymentsRegion,
								gst);
						dcTransSurcharge = surDCAmount[0];
						// Added by Sweety
						logger.info("dcTransSurcharge...{}", dcTransSurcharge);
						surchargeDCAmount = surDCAmount[1];
						finalAmt = surchargeDCAmount;
						logger.info("Calculating DCSurchargeDetails for Order Id = "
								+ sessionMap.get(FieldType.ORDER_ID.getName()) + " transAmount = " + surDCAmount[0]
								+ " actualTransactionAmount = " + surDCAmount[1]);
						logger.info("surchargeDCAmount...(ActualTransactionAmount)" + surchargeDCAmount);
					}
					if (paymentTypeMops.contains(Constants.PAYMENT_TYPE_WL.getValue())) {
						surWLAmount = calculateSurchargeAmount.fetchWLSurchargeDetails(amount, payId, paymentsRegion,
								gst);
						wlTransSurcharge = surWLAmount[0];
						surchargeWLAmount = surWLAmount[1];
						finalAmt = surchargeWLAmount;
						logger.info("Calculating WLSurchargeDetails for Order Id = "
								+ sessionMap.get(FieldType.ORDER_ID.getName()) + " transAmount = " + surWLAmount[0]
								+ " actualTransactionAmount = " + surWLAmount[1]);
					}
					if (paymentTypeMops.contains(Constants.PAYMENT_TYPE_UP.getValue())) {
						surUPAmount = calculateSurchargeAmount.fetchUPSurchargeDetails(amount, payId, paymentsRegion,
								gst);
						upTransSurcharge = surUPAmount[0];
						surchargeUPAmount = surUPAmount[1];
						finalAmt = surchargeUPAmount;
						logger.info("Calculating UPISurchargeDetails for Order Id = "
								+ sessionMap.get(FieldType.ORDER_ID.getName()) + " transAmount = " + surUPAmount[0]
								+ " actualTransactionAmount = " + surUPAmount[1]);
					}
					if (paymentTypeMops.contains(Constants.PAYMENT_TYPE_NB.getValue())) {
						surNBAmount = calculateSurchargeAmount.fetchNBSurchargeDetails(amount, payId, paymentsRegion,
								gst);
						nbTransSurcharge = surNBAmount[0];
						surchargeNBAmount = surNBAmount[1];
						finalAmt = surchargeNBAmount;
						logger.info("Calculating NBSurchargeDetails for Order Id = "
								+ sessionMap.get(FieldType.ORDER_ID.getName()) + " transAmount = " + surNBAmount[0]
								+ " actualTransactionAmount = " + surNBAmount[1]);
					}
					if (paymentTypeMops.contains(Constants.PAYMENT_TYPE_PC.getValue())) {
						surPCAmount = calculateSurchargeAmount.fetchPCSurchargeDetails(amount, payId, paymentsRegion,
								gst);
						pcTransSurcharge = surPCAmount[0];
						surchargePCAmount = surPCAmount[1];
						finalAmt = surchargePCAmount;
						logger.info("Calculating PCSurchargeDetails for Order Id = "
								+ sessionMap.get(FieldType.ORDER_ID.getName()) + " transAmount = " + surPCAmount[0]
								+ " actualTransactionAmount = " + surPCAmount[1]);
					}
					 if (paymentTypeMops.contains(Constants.PAYMENT_TYPE_QR.getValue())) {
	                        Fields field = (Fields) sessionMap.get(Constants.FIELDS.getValue());
	                        logger.info("invoice id get in seesion map" + field.getFieldsAsString());

	                        surQRAmount = calculateSurchargeAmount.fetchQRSurchargeDetails(amount, payId, paymentsRegion,
	                                gst);
	                        qrTransSurcharge = surQRAmount[0];
	                        // Added by abhi
	                        logger.info("QRTransSurcharge...{}", qrTransSurcharge);
	                        surchargeQRAmount = surQRAmount[1];
	                        finalAmt = surchargeQRAmount;
	                        logger.info("Calculating QRSurchargeDetails for Order Id = "
	                                + sessionMap.get(FieldType.ORDER_ID.getName()) + " transAmount = " + surQRAmount[0]
	                                + " actualTransactionAmount = " + surQRAmount[1]);
	                        logger.info("surchargeQRAmount...(ActualTransactionAmount)" + surchargeQRAmount);
	                    }
					String currencyCode = fields.get(FieldType.CURRENCY_CODE.getName());

					sessionMap.put(FieldType.TOTAL_AMOUNT.getName(),
							Amount.formatAmount(finalAmt.toString(), currencyCode));
					sessionMap.put(FieldType.CC_TOTAL_AMOUNT.getName(), surchargeCCAmount);
					sessionMap.put(FieldType.DC_TOTAL_AMOUNT.getName(), surchargeDCAmount);
					sessionMap.put(FieldType.NB_TOTAL_AMOUNT.getName(), surchargeNBAmount);
					sessionMap.put(FieldType.UP_TOTAL_AMOUNT.getName(), surchargeUPAmount);
					sessionMap.put(FieldType.AD_TOTAL_AMOUNT.getName(), surchargeADAmount);
					sessionMap.put(FieldType.WL_TOTAL_AMOUNT.getName(), surchargeWLAmount);
					sessionMap.put(FieldType.PC_TOTAL_AMOUNT.getName(), surchargePCAmount);
                    sessionMap.put(FieldType.QR_TOTAL_AMOUNT.getName(), surchargeQRAmount);

					sessionMap.put(FieldType.CC_SURCHARGE.getName(), ccTransSurcharge);
					sessionMap.put(FieldType.DC_SURCHARGE.getName(), dcTransSurcharge);
					sessionMap.put(FieldType.NB_SURCHARGE.getName(), nbTransSurcharge);
					sessionMap.put(FieldType.UP_SURCHARGE.getName(), upTransSurcharge);
					sessionMap.put(FieldType.AD_SURCHARGE.getName(), adTransSurcharge);
					sessionMap.put(FieldType.WL_SURCHARGE.getName(), wlTransSurcharge);
					sessionMap.put(FieldType.PC_SURCHARGE.getName(), pcTransSurcharge);
                    sessionMap.put(FieldType.QR_SURCHARGE.getName(), qrTransSurcharge);

					JSONObject requestParameterJson = prepareRequestParemeterService.prepareRequestParameter(fields,
							user, sessionMap);
					sessionMap.put(FieldType.SUPPORTED_PAYMENT_TYPE.getName(), requestParameterJson.toString());
					setSupportedPaymentTypeString(requestParameterJson.toString());
					// return "surchargePaymentPage";
					logger.info("Header value " + request.getHeader("User-Agent"));
					logger.info("CloudFront-Is-Mobile-Viewer    " + request.getHeader("CloudFront-Is-Mobile-Viewer"));
					logger.info("CloudFront-Is-Tablet-Viewer    " + request.getHeader("CloudFront-Is-Tablet-Viewer"));
					logger.info("CloudFront-Is-SmartTV-Viewer    " + request.getHeader("CloudFront-Is-SmartTV-Viewer"));
					logger.info("CloudFront-Is-Desktop-Viewer    " + request.getHeader("CloudFront-Is-Desktop-Viewer"));
					logger.info("CloudFront-Viewer-Country    " + request.getHeader("CloudFront-Viewer-Country"));
					if (request.getHeader("User-Agent").contains("Mobile")
							|| request.getHeader("User-Agent").contains("iphone")
							|| (request.getHeader("CloudFront-Is-Mobile-Viewer") != null
									&& request.getHeader("CloudFront-Is-Mobile-Viewer").equalsIgnoreCase("true"))
							|| (request.getHeader("CloudFront-Is-Tablet-Viewer") != null
									&& request.getHeader("CloudFront-Is-Tablet-Viewer").equalsIgnoreCase("true"))) {
						logger.info("Opening Mobile Payment page");

						logger.info("intent upi {}, 01 ", fields.get(FieldType.UPI_INTENT.getName()));
						if (fields.get(FieldType.UPI_INTENT.getName()) != null
								&& fields.get(FieldType.UPI_INTENT.getName()).equalsIgnoreCase("1")) {
							logger.info("Opening Intent page");
							return "intentPage";
						} else {
							return "surchargePaymentPage";
						}
					} else {
						logger.info("Opening web Payment page");
						logger.info("intent upi {}, 02 ", fields.get(FieldType.UPI_INTENT.getName()));
						if (fields.get(FieldType.UPI_INTENT.getName()) != null
								&& fields.get(FieldType.UPI_INTENT.getName()).equalsIgnoreCase("1")) {
							logger.info("Opening Intent page");
							return "intentPage";
						} else {
							return "surchargePaymentPage";
						}
					}

				} else {

					String convertedamount = sessionMap.get(FieldType.AMOUNT.getName()).toString();
					String currency = sessionMap.get(FieldType.CURRENCY_CODE.getName()).toString();
					String amount = Amount.toDecimal(convertedamount, currency);
					BigDecimal bigDecimalAmt = new BigDecimal(amount);
					String paymentsRegion = fields.get(FieldType.PAYMENTS_REGION.getName());
					if (paymentsRegion == null || paymentsRegion.equals("")) {
						paymentsRegion = AccountCurrencyRegion.DOMESTIC.toString();
					}

					// Added default values as null values stop payment page from loading
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

					String paymentTypeMops = sessionMap.get(Constants.PAYMENT_TYPE_MOP.getValue()).toString();
					if (paymentTypeMops.contains(Constants.PAYMENT_TYPE_AD.getValue())) {
						adTransSurcharge = BigDecimal.ZERO;
						surchargeADAmount = bigDecimalAmt;
					}
					if (paymentTypeMops.contains(Constants.PAYMENT_TYPE_CC.getValue())) {
						ccTransSurcharge = BigDecimal.ZERO;
						surchargeCCAmount = bigDecimalAmt;
					}
					if (paymentTypeMops.contains(Constants.PAYMENT_TYPE_DC.getValue())) {
						dcTransSurcharge = BigDecimal.ZERO;
						surchargeDCAmount = bigDecimalAmt;
					}
					if (paymentTypeMops.contains(Constants.PAYMENT_TYPE_WL.getValue())) {
						wlTransSurcharge = BigDecimal.ZERO;
						surchargeWLAmount = bigDecimalAmt;
					}
					if (paymentTypeMops.contains(Constants.PAYMENT_TYPE_UP.getValue())) {
						upTransSurcharge = BigDecimal.ZERO;
						surchargeUPAmount = bigDecimalAmt;
					}
					if (paymentTypeMops.contains(Constants.PAYMENT_TYPE_NB.getValue())) {
						nbTransSurcharge = BigDecimal.ZERO;
						surchargeNBAmount = bigDecimalAmt;
					}
					if (paymentTypeMops.contains(Constants.PAYMENT_TYPE_PC.getValue())) {
						pcTransSurcharge = BigDecimal.ZERO;
						surchargePCAmount = bigDecimalAmt;
					}
					if (paymentTypeMops.contains(Constants.PAYMENT_TYPE_QR.getValue())) {
                        qrTransSurcharge = BigDecimal.ZERO;
                        surchargeQRAmount = bigDecimalAmt;
                    }
					sessionMap.put(FieldType.CC_TOTAL_AMOUNT.getName(), surchargeCCAmount);
					sessionMap.put(FieldType.DC_TOTAL_AMOUNT.getName(), surchargeDCAmount);
					sessionMap.put(FieldType.NB_TOTAL_AMOUNT.getName(), surchargeNBAmount);
					sessionMap.put(FieldType.UP_TOTAL_AMOUNT.getName(), surchargeUPAmount);
					sessionMap.put(FieldType.AD_TOTAL_AMOUNT.getName(), surchargeADAmount);
					sessionMap.put(FieldType.WL_TOTAL_AMOUNT.getName(), surchargeWLAmount);
					sessionMap.put(FieldType.PC_TOTAL_AMOUNT.getName(), surchargePCAmount);
                    sessionMap.put(FieldType.QR_TOTAL_AMOUNT.getName(), surchargeQRAmount);

					sessionMap.put(FieldType.CC_SURCHARGE.getName(), ccTransSurcharge);
					sessionMap.put(FieldType.DC_SURCHARGE.getName(), dcTransSurcharge);
					sessionMap.put(FieldType.NB_SURCHARGE.getName(), nbTransSurcharge);
					sessionMap.put(FieldType.UP_SURCHARGE.getName(), upTransSurcharge);
					sessionMap.put(FieldType.AD_SURCHARGE.getName(), adTransSurcharge);
					sessionMap.put(FieldType.WL_SURCHARGE.getName(), wlTransSurcharge);
					sessionMap.put(FieldType.PC_SURCHARGE.getName(), pcTransSurcharge);
                    sessionMap.put(FieldType.QR_SURCHARGE.getName(), qrTransSurcharge);

					JSONObject requestParameterJson = prepareRequestParemeterService.prepareRequestParameter(fields,
							user, sessionMap);
					sessionMap.put(FieldType.SUPPORTED_PAYMENT_TYPE.getName(), requestParameterJson.toString());
					setSupportedPaymentTypeString(requestParameterJson.toString());
					// return "surchargePaymentPage";
					logger.info("Header value " + request.getHeader("User-Agent"));
					logger.info("CloudFront-Is-Mobile-Viewer    " + request.getHeader("CloudFront-Is-Mobile-Viewer"));
					logger.info("CloudFront-Is-Tablet-Viewer    " + request.getHeader("CloudFront-Is-Tablet-Viewer"));
					logger.info("CloudFront-Is-SmartTV-Viewer    " + request.getHeader("CloudFront-Is-SmartTV-Viewer"));
					logger.info("CloudFront-Is-Desktop-Viewer    " + request.getHeader("CloudFront-Is-Desktop-Viewer"));
					if (request.getHeader("User-Agent").contains("Mobile")
							|| request.getHeader("User-Agent").contains("iphone")
							|| (request.getHeader("CloudFront-Is-Mobile-Viewer") != null
									&& request.getHeader("CloudFront-Is-Mobile-Viewer").equalsIgnoreCase("true"))
							|| (request.getHeader("CloudFront-Is-Tablet-Viewer") != null
									&& request.getHeader("CloudFront-Is-Tablet-Viewer").equalsIgnoreCase("true"))) {
						logger.info("Opening Mobile Payment page");
						logger.info("intent upi {}, 03 ", fields.get(FieldType.UPI_INTENT.getName()));
						if (fields.get(FieldType.UPI_INTENT.getName()) != null
								&& fields.get(FieldType.UPI_INTENT.getName()).equalsIgnoreCase("1")) {
							logger.info("Opening Intent page");
							return "intentPage";
						} else {
							return "surchargePaymentPage";
						}
					} else {
						logger.info("Opening web Payment page");
						logger.info("intent upi {}, 04 ", fields.get(FieldType.UPI_INTENT.getName()));
						if (fields.get(FieldType.UPI_INTENT.getName()) != null
								&& fields.get(FieldType.UPI_INTENT.getName()).equalsIgnoreCase("1")) {
							logger.info("Opening Intent page");
							return "intentPage";
						} else {
							return "surchargePaymentPage";
						}
					}

					/*
					 * JSONObject requestParameterJson =
					 * prepareRequestParemeterService.prepareRequestParameter(fields ,
					 * user,sessionMap); sessionMap.put(FieldType.SUPPORTED_PAYMENT_TYPE.getName(),
					 * requestParameterJson.toString());
					 * setSupportedPaymentTypeString(requestParameterJson.toString()); return
					 * "surchargePaymentPage";
					 */
				}
			} else {
				fields.put(FieldType.TXNTYPE.getName(), getOrigTxnType());
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

	public String cancelByUser() {
		logger.info("Cancelled By User Called");
		try {
			Fields fields = (Fields) sessionMap.get(Constants.FIELDS.getValue());

			if (null == fields) {
				fields = new Fields();
				fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.TIMEOUT.getResponseMessage());
				fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.TIMEOUT.getCode());
				fields.put(FieldType.STATUS.getName(), StatusType.USER_INACTIVE.getName());
				fields.put(FieldType.RETURN_URL.getName(), ConfigurationConstants.DEFAULT_RETURN_URL.getValue());

				String crisFlag = (String) sessionMap.get(FieldType.INTERNAL_IRCTC_YN.getName());
				if (StringUtils.isNotBlank(crisFlag)) {
					fields.put(FieldType.INTERNAL_IRCTC_YN.getName(), crisFlag);
				}
				sessionMap.clear();
				responseCreator.ResponsePost(fields);
			} else {

				// Remove hash generated earlier
				logger.info("Fields inside Cancel By User : " + fields.getFieldsAsString());
				fields.remove(FieldType.HASH.getName());
				fields.remove(FieldType.PG_TXN_MESSAGE.getName());
				fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.CANCELLED.getResponseMessage());
				fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.CANCELLED.getCode());
				fields.put(FieldType.STATUS.getName(), StatusType.CANCELLED.getName());
				fields.put(FieldType.INTERNAL_SHOPIFY_YN.getName(),
						(String) sessionMap.get(FieldType.INTERNAL_SHOPIFY_YN.getName()));
				String shopifyFlag = (String) sessionMap.get(FieldType.INTERNAL_SHOPIFY_YN.getName());

				fields.put((FieldType.CANCEL_URL.getName()), (String) sessionMap.get(FieldType.CANCEL_URL.getName()));
				Object txnTypeObject = sessionMap.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName());
				if (null != shopifyFlag && shopifyFlag.equals("Y")) {
					fields.put((FieldType.RETURN_URL.getName()),
							(String) sessionMap.get(FieldType.CANCEL_URL.getName()));
				}

				if (null != txnTypeObject) {
					String txnType = (String) txnTypeObject;
					fields.put(FieldType.TXNTYPE.getName(), txnType);
					fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), txnType);
				}
				fields.put(FieldType.INTERNAL_ORIG_TXN_ID.getName(), fields.get(FieldType.TXN_ID.getName()));
				fields.put(FieldType.OID.getName(), fields.get(FieldType.TXN_ID.getName()));
				String newTxnId = TransactionManager.getNewTransactionId();
				fields.put(FieldType.TXN_ID.getName(), newTxnId);
				fields.put(FieldType.PG_REF_NUM.getName(), newTxnId);
				String surchargeFlag = sessionMap.get(FieldType.SURCHARGE_FLAG.getName()).toString();
				fields.put(FieldType.SURCHARGE_FLAG.getName(), surchargeFlag);
				try {
					logger.info("Fields Before Insert : " + fields.getFieldsAsString());
					field.insert(fields);
				} catch (SystemException systemException) {
					SessionCleaner.cleanSession(sessionMap);
					logger.error("Exception In Cancel By Transaction", systemException);
					fields.put(FieldType.RESPONSE_CODE.getName(), systemException.getErrorType().getCode());
					fields.put(FieldType.RESPONSE_MESSAGE.getName(),
							systemException.getErrorType().getResponseMessage());
					logger.error("Unable to update cancelled transaction", systemException);
				}
				String crisFlag = (String) sessionMap.get(FieldType.INTERNAL_IRCTC_YN.getName());
				if (StringUtils.isNotBlank(crisFlag)) {
					fields.put(FieldType.INTERNAL_IRCTC_YN.getName(), crisFlag);
				}
				responseCreator.create(fields);
				responseCreator.ResponsePost(fields);
				sessionMap.invalidate();
			}
			return NONE;
		} catch (Exception exception) {
			Fields fields = new Fields();
			String crisFlag = (String) sessionMap.get(FieldType.INTERNAL_IRCTC_YN.getName());
			if (StringUtils.isNotBlank(crisFlag)) {
				fields.put(FieldType.INTERNAL_IRCTC_YN.getName(), crisFlag);
			}
			SessionCleaner.cleanSession(sessionMap);
			logger.error("Exception In Cancel By Transaction", exception);

			fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.CANCELLED.getResponseMessage());
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.CANCELLED.getCode());
			fields.put(FieldType.STATUS.getName(), StatusType.CANCELLED.getName());
			fields.put(FieldType.RETURN_URL.getName(), ConfigurationConstants.DEFAULT_RETURN_URL.getValue());
			responseCreator.ResponsePost(fields);
			return Action.ERROR;

		}
	}

	@SuppressWarnings("unchecked")
	public String acquirerHandler() {
		try {
			Fields fields = (Fields) sessionMap.get(Constants.FIELDS.getValue());
			if (null != fields) {
			} else {
				logger.info("session fields lost");
				return ERROR;
			}

			logger.info("<<<<<<<<<<<<<<< acquirerHandler - fields : " + fields.getFieldsAsString());

			if (sessionMap.get(FieldType.IS_ENROLLED.getName()) != null && sessionMap
					.get(FieldType.IS_ENROLLED.getName()).toString().equalsIgnoreCase(Constants.Y_FLAG.getValue())) {
				return Action.NONE;
			} else {
				sessionMap.put(FieldType.IS_ENROLLED.getName(), Constants.Y_FLAG.getValue());
			}

			if (StringUtils.isNotBlank(getCardNumber())) {
				String cardNo = StringUtils.replace(getCardNumber(), " ", "");
				fields.put(FieldType.CARD_NUMBER.getName(), cardNo);
				cardNo = FraudPreventionUtil.makeCardMask1(fields);
				fields.remove(FieldType.CARD_NUMBER.getName());
				fields.put(FieldType.CARD_MASK.getName(), cardNo);
			}

			if (StringUtils.isNotBlank(fields.get(FieldType.ACQUIRER_TYPE.getName()))) {
				String origTxnType = (String) sessionMap.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName());
				fields.put(FieldType.TXNTYPE.getName(), origTxnType);
				fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), origTxnType);
				Map<String, String> responseFields = transactionControllerServiceProvider.transact(fields,
						Constants.TXN_WS_INTERNAL.getValue());
				Fields field = new Fields(responseFields);

				field.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.DUPLICATE.getResponseMessage());
				field.put(FieldType.RESPONSE_CODE.getName(), ErrorType.DUPLICATE.getCode());
				field.put(FieldType.TXNTYPE.getName(), fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()));

				transactionResponser.removeInvalidResponseFields(field);
				transactionResponser.addResponseDateTime(field);
				String crisFlag = (String) sessionMap.get(FieldType.INTERNAL_IRCTC_YN.getName());
				if (StringUtils.isNotBlank(crisFlag)) {
					fields.put(FieldType.INTERNAL_IRCTC_YN.getName(), crisFlag);
				}
				responseCreator.ResponsePost(field);
				return Action.NONE;

			}

			String cardPaymentType = getPaymentType();
			String cardMopType = getMopType();

			if (cardPaymentType.equals(PaymentType.NET_BANKING.getCode())) {

				fields.put(FieldType.PAYMENT_TYPE.getName(), getPaymentType());
				// fields.put(FieldType.MOP_TYPE.getName(), cardMopType);
				fields.put(FieldType.MOP_TYPE.getName(), getMopType());
				fields.put(FieldType.INTERNAL_CARD_ISSUER_COUNTRY.getName(), "IN");
				fields.put(FieldType.INTERNAL_CARD_ISSUER_BANK.getName(), MopType.getmop(getMopType()).getName());
				// setMopType(MopType.getmopName(getMopType()));
			}

			else if (cardPaymentType.equals(PaymentType.WALLET.getCode())) {

				fields.put(FieldType.PAYMENT_TYPE.getName(), getPaymentType());
				fields.put(FieldType.MOP_TYPE.getName(), getMopType());
				fields.put(FieldType.INTERNAL_CARD_ISSUER_COUNTRY.getName(), "IN");
				fields.put(FieldType.INTERNAL_CARD_ISSUER_BANK.getName(), MopType.getmopName(getMopType()));
			}

			else if (cardPaymentType.equals(PaymentType.EXPRESS_PAY.getCode())) {
				logger.info("Inside Express Pay Card Payment Type ---->" + getPaymentType());
				fields.put(FieldType.PAYMENT_TYPE.getName(), getPaymentType());
				fields.put(FieldType.MOP_TYPE.getName(), getMopType());
				fields.put(FieldType.INTERNAL_CARD_ISSUER_COUNTRY.getName(), "IN");
				fields.put(FieldType.INTERNAL_CARD_ISSUER_BANK.getName(), MopType.getmopName(getMopType()));
			}

			else {
//			
				if (!cardPaymentType.equalsIgnoreCase(PaymentType.WALLET.getCode())
						&& !cardPaymentType.equalsIgnoreCase(PaymentType.EXPRESS_PAY.getCode())) {
					Map<String, String> binRangeResponseMap = (Map<String, String>) sessionMap
							.get(Constants.BIN.getValue());
					String binMopType = binRangeResponseMap.get(FieldType.MOP_TYPE.getName());
					String binPaymentType = binRangeResponseMap.get(FieldType.PAYMENT_TYPE.getName());
					String binIssuerBankName = binRangeResponseMap.get(FieldType.INTERNAL_CARD_ISSUER_BANK.getName());
					String binIssuerCountry = binRangeResponseMap.get(FieldType.INTERNAL_CARD_ISSUER_COUNTRY.getName());
					String paymentsRegion = binRangeResponseMap.get(FieldType.PAYMENTS_REGION.getName());
					String cardHolderType = binRangeResponseMap.get(FieldType.CARD_HOLDER_TYPE.getName());

					logger.info("BinRangeResponseMap values for orderId = " + fields.get(FieldType.ORDER_ID.getName())
							+ " binMopType = " + binMopType + " binPaymentType = " + binPaymentType
							+ " binpPaymentsRegion = " + paymentsRegion + " binCardHolderType = " + cardHolderType);

					if (!(binMopType.equals(cardMopType)) && (binPaymentType.equals(cardPaymentType))) {
						String origTxnType = (String) sessionMap.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName());
						fields.put(FieldType.TXNTYPE.getName(), origTxnType);
						fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), origTxnType);

						Map<String, String> responseFields = transactionControllerServiceProvider.transact(fields,
								Constants.TXN_WS_INTERNAL.getValue());
						Fields field = new Fields(responseFields);

						field.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.DENIED_BY_FRAUD.getResponseMessage());
						field.put(FieldType.RESPONSE_CODE.getName(), ErrorType.DENIED_BY_FRAUD.getCode());
						field.put(FieldType.TXNTYPE.getName(), fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()));

						transactionResponser.removeInvalidResponseFields(field);
						transactionResponser.addResponseDateTime(field);
						String crisFlag = (String) sessionMap.get(FieldType.INTERNAL_IRCTC_YN.getName());
						if (StringUtils.isNotBlank(crisFlag)) {
							fields.put(FieldType.INTERNAL_IRCTC_YN.getName(), crisFlag);
						}
						responseCreator.ResponsePost(field);
						return Action.NONE;
					}

					if (cardPaymentType.isEmpty() || cardMopType.isEmpty()) {
						String origTxnType = (String) sessionMap.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName());
						fields.put(FieldType.TXNTYPE.getName(), origTxnType);
						fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), origTxnType);

						Map<String, String> responseFields = transactionControllerServiceProvider.transact(fields,
								Constants.TXN_WS_INTERNAL.getValue());
						Fields field = new Fields(responseFields);

						field.put(FieldType.RESPONSE_MESSAGE.getName(),
								ErrorType.CARD_NUMBER_NOT_SUPPORTED.getResponseMessage());
						field.put(FieldType.RESPONSE_CODE.getName(), ErrorType.CARD_NUMBER_NOT_SUPPORTED.getCode());
						field.put(FieldType.TXNTYPE.getName(), fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()));

						transactionResponser.removeInvalidResponseFields(field);
						transactionResponser.addResponseDateTime(field);
						String crisFlag = (String) sessionMap.get(FieldType.INTERNAL_IRCTC_YN.getName());
						if (StringUtils.isNotBlank(crisFlag)) {
							fields.put(FieldType.INTERNAL_IRCTC_YN.getName(), crisFlag);
						}
						responseCreator.ResponsePost(field);
						return Action.NONE;

					}

					fields.put(FieldType.PAYMENT_TYPE.getName(), getPaymentType());
					fields.put(FieldType.MOP_TYPE.getName(), getMopType());
					fields.put(FieldType.INTERNAL_CARD_ISSUER_COUNTRY.getName(), binIssuerCountry);
					fields.put(FieldType.INTERNAL_CARD_ISSUER_BANK.getName(), binIssuerBankName);

					if (!StringUtils.isBlank(cardHolderType)) {
						fields.put(FieldType.CARD_HOLDER_TYPE.getName(), cardHolderType);
					} else {
						fields.put(FieldType.CARD_HOLDER_TYPE.getName(), CardHolderType.CONSUMER.toString());
					}

					// If PAYMENTS_REGION not found from bin , set defaults to DOMESTIC
					if (!StringUtils.isBlank(paymentsRegion)) {
						fields.put(FieldType.PAYMENTS_REGION.getName(), paymentsRegion);
					} else {
						fields.put(FieldType.PAYMENTS_REGION.getName(), AccountCurrencyRegion.DOMESTIC.toString());
					}

					sessionMap.put((FieldType.CARD_HOLDER_TYPE.getName()),
							fields.get(FieldType.CARD_HOLDER_TYPE.getName()));
					sessionMap.put((FieldType.PAYMENTS_REGION.getName()),
							fields.get(FieldType.PAYMENTS_REGION.getName()));
				}

			}

			String origTxnType = (String) sessionMap.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName());
			fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), origTxnType);

			// Put transaction id as original txnId for subsequent transactions
			String sessionOId = (String) sessionMap.get(FieldType.OID.getName());
			fields.put(FieldType.INTERNAL_ORIG_TXN_ID.getName(), sessionOId);
			switch (PaymentType.getInstanceUsingCode(fields.get(FieldType.PAYMENT_TYPE.getName()))) {
			case CREDIT_CARD:
			case DEBIT_CARD:
			case PREPAID_CARD:
				fields.put(FieldType.TXNTYPE.getName(), TransactionType.ENROLL.getName());
				if (!getMopType().endsWith(MopType.EZEECLICK.getCode())) {
					fields.put(FieldType.CARD_NUMBER.getName(), getCardNumber().replace(" ", "").replace(",", ""));
					fields.put(FieldType.CARD_EXP_DT.getName(), getExpiryMonth() + getExpiryYear());
					fields.put(FieldType.CVV.getName(), getCvvNumber().replace(" ", "").replace(",", ""));
					fields.put(FieldType.CARD_HOLDER_NAME.getName(), getCardName());
				}
				// Save Card detail for Express Payment

				if (StringUtils.isNotBlank(fields.get(FieldType.RESPONSE_CODE.getName()))
						&& fields.get(FieldType.RESPONSE_CODE.getName()).equalsIgnoreCase(ErrorType.SUCCESS.getCode())
						&& !fields.get(FieldType.TXNTYPE.getName())
								.equalsIgnoreCase(TransactionType.NEWORDER.getName())) {
					if (!StringUtils.isEmpty(getCardsaveflag()) && getCardsaveflag().equalsIgnoreCase("true")) {
						tokenManager.addToken(fields, userDao.findPayId(fields.get(FieldType.PAY_ID.getName())));
					}
				}

				sessionMap.put(Constants.CUSTOM_TOKEN.getValue(), TokenHelper.generateGUID());
				sessionMap.put((FieldType.CARD_NUMBER.getName()), fields.get(FieldType.CARD_NUMBER.getName()));
				sessionMap.put((FieldType.CARD_EXP_DT.getName()), fields.get(FieldType.CARD_EXP_DT.getName()));
				sessionMap.put((FieldType.CVV.getName()), fields.get(FieldType.CVV.getName()));
				sessionMap.put((FieldType.INTERNAL_CARD_ISSUER_BANK.getName()),
						fields.get(FieldType.INTERNAL_CARD_ISSUER_BANK.getName()));
				sessionMap.put((FieldType.INTERNAL_CARD_ISSUER_COUNTRY.getName()),
						fields.get(FieldType.INTERNAL_CARD_ISSUER_COUNTRY.getName()));
				fields.put(FieldType.INTERNAL_VALIDATE_HASH_YN.getName(), "N");
				break;

			case EXPRESS_PAY:
				if (null != getTokenId()) {
					logger.info("Inside Express Pay --------------------------");
					fields.put(FieldType.TOKEN_ID.getName(), getTokenId());
					fields.put(FieldType.KEY_ID.getName(), SystemConstants.DEFAULT_KEY_ID);
					Map<String, Token> tokenMap = (Map<String, Token>) sessionMap.get(Constants.TOKEN.getValue());
					Token token = tokenMap.get(getTokenId());
					// decryoted token
//					Map<String, String> cardObj = tokenManager.decryptToken(token);
					fields.put(FieldType.TXNTYPE.getName(), TransactionType.ENROLL.getName());
//					fields.put(FieldType.CARD_NUMBER.getName(),
//							cardObj.get(FieldType.CARD_NUMBER.getName()).replace(" ", "").replace(",", ""));
//					fields.put(FieldType.CARD_EXP_DT.getName(), cardObj.get(FieldType.CARD_EXP_DT.getName()));
					fields.put(FieldType.MOP_TYPE.getName(), token.getMopType());
					fields.put(FieldType.PAYMENT_TYPE.getName(), token.getPaymentType());
					fields.put(FieldType.CARD_HOLDER_NAME.getName(), token.getCustomerName());
					fields.put(FieldType.CVV.getName(), getCvvNumber().replace(" ", "").replace(",", ""));
					// commented for new token implementation
//					fields.put(FieldType.INTERNAL_CARD_ISSUER_BANK.getName(), token.getCardIssuerBank());
//					fields.put(FieldType.INTERNAL_CARD_ISSUER_COUNTRY.getName(), token.getCardIssuerCountry());
					fields.put(FieldType.INTERNAL_VALIDATE_HASH_YN.getName(), "N");
					fields.put(FieldType.CARD_HOLDER_TYPE.getName(), token.getCardHolderType());
					fields.put(FieldType.PAYMENTS_REGION.getName(), token.getPaymentsRegion());

					sessionMap.put(Constants.CUSTOM_TOKEN.getValue(), TokenHelper.generateGUID());
//					sessionMap.put((FieldType.CARD_NUMBER.getName()), fields.get(FieldType.CARD_NUMBER.getName()));
//					sessionMap.put((FieldType.CARD_EXP_DT.getName()), fields.get(FieldType.CARD_EXP_DT.getName()));
					sessionMap.put((FieldType.CVV.getName()), fields.get(FieldType.CVV.getName()));
					sessionMap.put((FieldType.INTERNAL_CARD_ISSUER_BANK.getName()),
							fields.get(FieldType.INTERNAL_CARD_ISSUER_BANK.getName()));
					sessionMap.put((FieldType.INTERNAL_CARD_ISSUER_COUNTRY.getName()),
							fields.get(FieldType.INTERNAL_CARD_ISSUER_COUNTRY.getName()));
					sessionMap.put((FieldType.CARD_HOLDER_TYPE.getName()),
							fields.get(FieldType.CARD_HOLDER_TYPE.getName()));
					sessionMap.put((FieldType.PAYMENTS_REGION.getName()),
							fields.get(FieldType.PAYMENTS_REGION.getName()));
				}
				break;
			case NET_BANKING:
				fields.put(FieldType.APP_CODE.getName(), getAppMode());
				fields.put(FieldType.TXN_SOURCE.getName(), getTxnSource());
				sessionMap.put((FieldType.INTERNAL_ORIG_TXN_TYPE.getName()), TransactionType.SALE.getName());
				fields.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
				fields.put(FieldType.INTERNAL_VALIDATE_HASH_YN.getName(), "N");
				fields.put(FieldType.CARD_HOLDER_TYPE.getName(), CardHolderType.CONSUMER.toString());
				fields.put(FieldType.PAYMENTS_REGION.getName(), AccountCurrencyRegion.DOMESTIC.toString());
				sessionMap.put((FieldType.CARD_HOLDER_TYPE.getName()), CardHolderType.CONSUMER.toString());
				sessionMap.put((FieldType.PAYMENTS_REGION.getName()), AccountCurrencyRegion.DOMESTIC.toString());
				break;
			case WALLET:
				sessionMap.put((FieldType.INTERNAL_ORIG_TXN_TYPE.getName()), TransactionType.SALE.getName());
				fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), TransactionType.SALE.getName());
				fields.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
				fields.put(FieldType.INTERNAL_VALIDATE_HASH_YN.getName(), "N");
				fields.put(FieldType.CARD_HOLDER_TYPE.getName(), CardHolderType.CONSUMER.toString());
				fields.put(FieldType.PAYMENTS_REGION.getName(), AccountCurrencyRegion.DOMESTIC.toString());
				sessionMap.put((FieldType.CARD_HOLDER_TYPE.getName()), CardHolderType.CONSUMER.toString());
				sessionMap.put((FieldType.PAYMENTS_REGION.getName()), AccountCurrencyRegion.DOMESTIC.toString());
				break;
			/*case EMI:
				break;
			case RECURRING_PAYMENT:
				break;*/
			case UPI:
				break;
			/*case AD:
				fields.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
				fields.put(FieldType.INTERNAL_VALIDATE_HASH_YN.getName(), "N");
				break;*/
			default:
				break;

			}

			String surchargeFlag = sessionMap.get(FieldType.SURCHARGE_FLAG.getName()).toString();
			if (surchargeFlag.equals("Y")) {

				fields.put((FieldType.SURCHARGE_FLAG.getName()), surchargeFlag);
				String currencyCode = fields.get(FieldType.CURRENCY_CODE.getName());
				// Add surcharge amount and total amount on the basis of payment
				// type
				String paymentType = fields.get(FieldType.PAYMENT_TYPE.getName());
				if (paymentType.equals(PaymentType.CREDIT_CARD.getCode())) {

					String ccTotalAmount = "";

					if (StringUtils.isNotBlank(paymentsRegion)
							&& paymentsRegion.equalsIgnoreCase(AccountCurrencyRegion.DOMESTIC.toString())) {
						ccTotalAmount = sessionMap.get(FieldType.CC_TOTAL_AMOUNT.getName()).toString();
						ccTotalAmount = Amount.formatAmount(ccTotalAmount, currencyCode);
					} else if (StringUtils.isNotBlank(paymentsRegion)
							&& paymentsRegion.equalsIgnoreCase(AccountCurrencyRegion.INTERNATIONAL.toString())) {
						ccTotalAmount = sessionMap.get(FieldType.CC_TOTAL_AMOUNT_INTERNATIONAL.getName()).toString();
						ccTotalAmount = Amount.formatAmount(ccTotalAmount, currencyCode);
					} else {
						ccTotalAmount = sessionMap.get(FieldType.CC_TOTAL_AMOUNT.getName()).toString();
						ccTotalAmount = Amount.formatAmount(ccTotalAmount, currencyCode);
					}

					fields.put(FieldType.TOTAL_AMOUNT.getName(), ccTotalAmount);
				} else if (paymentType.equals(PaymentType.DEBIT_CARD.getCode())) {

					String dcTotalAmount = "";

					if (StringUtils.isNotBlank(paymentsRegion)
							&& paymentsRegion.equalsIgnoreCase(AccountCurrencyRegion.DOMESTIC.toString())) {
						dcTotalAmount = sessionMap.get(FieldType.DC_TOTAL_AMOUNT.getName()).toString();
						dcTotalAmount = Amount.formatAmount(dcTotalAmount, currencyCode);
					}

					else if (StringUtils.isNotBlank(paymentsRegion)
							&& paymentsRegion.equalsIgnoreCase(AccountCurrencyRegion.INTERNATIONAL.toString())) {
						dcTotalAmount = sessionMap.get(FieldType.DC_TOTAL_AMOUNT_INTERNATIONAL.getName()).toString();
						dcTotalAmount = Amount.formatAmount(dcTotalAmount, currencyCode);
					} else {
						dcTotalAmount = sessionMap.get(FieldType.DC_TOTAL_AMOUNT.getName()).toString();
						dcTotalAmount = Amount.formatAmount(dcTotalAmount, currencyCode);
					}
					fields.put(FieldType.TOTAL_AMOUNT.getName(), dcTotalAmount);

				} else if (paymentType.equals(PaymentType.PREPAID_CARD.getCode())) {

					String pcTotalAmount = "";

					if (StringUtils.isNotBlank(paymentsRegion)
							&& paymentsRegion.equalsIgnoreCase(AccountCurrencyRegion.DOMESTIC.toString())) {
						pcTotalAmount = sessionMap.get(FieldType.PC_TOTAL_AMOUNT.getName()).toString();
						pcTotalAmount = Amount.formatAmount(pcTotalAmount, currencyCode);
					}

					else if (StringUtils.isNotBlank(paymentsRegion)
							&& paymentsRegion.equalsIgnoreCase(AccountCurrencyRegion.INTERNATIONAL.toString())) {
						pcTotalAmount = sessionMap.get(FieldType.PC_TOTAL_AMOUNT_INTERNATIONAL.getName()).toString();
						pcTotalAmount = Amount.formatAmount(pcTotalAmount, currencyCode);
					} else {
						pcTotalAmount = sessionMap.get(FieldType.PC_TOTAL_AMOUNT.getName()).toString();
						pcTotalAmount = Amount.formatAmount(pcTotalAmount, currencyCode);
					}
					fields.put(FieldType.TOTAL_AMOUNT.getName(), pcTotalAmount);

				} else if (paymentType.equals(PaymentType.NET_BANKING.getCode())) {
					String nbTotalAmount = sessionMap.get(FieldType.NB_TOTAL_AMOUNT.getName()).toString();
					nbTotalAmount = Amount.formatAmount(nbTotalAmount, currencyCode);
					fields.put(FieldType.TOTAL_AMOUNT.getName(), nbTotalAmount);
				} else if (paymentType.equals(PaymentType.UPI.getCode())) {
					String upiTotalAmount = sessionMap.get(FieldType.UP_TOTAL_AMOUNT.getName()).toString();
					upiTotalAmount = Amount.formatAmount(upiTotalAmount, currencyCode);
					fields.put(FieldType.TOTAL_AMOUNT.getName(), upiTotalAmount);
				}

				else if (paymentType.equals(PaymentType.WALLET.getCode())) {
					String wlTotalAmount = sessionMap.get(FieldType.WL_TOTAL_AMOUNT.getName()).toString();
					wlTotalAmount = Amount.formatAmount(wlTotalAmount, currencyCode);
					fields.put(FieldType.TOTAL_AMOUNT.getName(), wlTotalAmount);
				}

				/*else if (paymentType.equals(PaymentType.AD.getCode())) {
					String adTotalAmount = sessionMap.get(FieldType.AD_TOTAL_AMOUNT.getName()).toString();
					adTotalAmount = Amount.formatAmount(adTotalAmount, currencyCode);
					fields.put(FieldType.TOTAL_AMOUNT.getName(), adTotalAmount);
				}*/ else {
					throw new SystemException(ErrorType.PAYMENT_OPTION_NOT_SUPPORTED, "Unsupported payment type");
				}
			} else {

				// TDR MODE
				fields.put((FieldType.SURCHARGE_FLAG.getName()), "");
				String currencyCode = fields.get(FieldType.CURRENCY_CODE.getName());
				String paymentType = fields.get(FieldType.PAYMENT_TYPE.getName());
				if (paymentType.equals(PaymentType.CREDIT_CARD.getCode())) {

					String ccTotalAmount = Amount.formatAmount(fields.get(FieldType.AMOUNT.getName()), currencyCode);
					fields.put(FieldType.TOTAL_AMOUNT.getName(), ccTotalAmount);

				} else if (paymentType.equals(PaymentType.DEBIT_CARD.getCode())) {

					String dcTotalAmount = Amount.formatAmount(fields.get(FieldType.AMOUNT.getName()), currencyCode);
					fields.put(FieldType.TOTAL_AMOUNT.getName(), dcTotalAmount);

				} else if (paymentType.equals(PaymentType.PREPAID_CARD.getCode())) {

					String pcTotalAmount = Amount.formatAmount(fields.get(FieldType.AMOUNT.getName()), currencyCode);
					fields.put(FieldType.TOTAL_AMOUNT.getName(), pcTotalAmount);

				} else if (paymentType.equals(PaymentType.NET_BANKING.getCode())) {
					String nbTotalAmount = sessionMap.get(FieldType.NB_TOTAL_AMOUNT.getName()).toString();
					nbTotalAmount = Amount.formatAmount(nbTotalAmount, currencyCode);
					fields.put(FieldType.TOTAL_AMOUNT.getName(), nbTotalAmount);
				} else if (paymentType.equals(PaymentType.UPI.getCode())) {
					String upiTotalAmount = sessionMap.get(FieldType.UP_TOTAL_AMOUNT.getName()).toString();
					upiTotalAmount = Amount.formatAmount(upiTotalAmount, currencyCode);
					fields.put(FieldType.TOTAL_AMOUNT.getName(), upiTotalAmount);
				}

				// changes by shivanand
				else if (paymentType.equals(PaymentType.WALLET.getCode())) {
					String wlTotalAmount = sessionMap.get(FieldType.WL_TOTAL_AMOUNT.getName()).toString();
					wlTotalAmount = Amount.formatAmount(wlTotalAmount, currencyCode);
					fields.put(FieldType.TOTAL_AMOUNT.getName(), wlTotalAmount);
				}

				/*else if (paymentType.equals(PaymentType.AD.getCode())) {
					String adTotalAmount = sessionMap.get(FieldType.AD_TOTAL_AMOUNT.getName()).toString();
					adTotalAmount = Amount.formatAmount(adTotalAmount, currencyCode);
					fields.put(FieldType.TOTAL_AMOUNT.getName(), adTotalAmount);
				}*/ else {
					throw new SystemException(ErrorType.PAYMENT_OPTION_NOT_SUPPORTED, "Unsupported payment type");
				}

				fields.put(FieldType.TOTAL_AMOUNT.getName(), fields.get(FieldType.AMOUNT.getName()));
			}

			if (Double.valueOf(fields.get(FieldType.TOTAL_AMOUNT.getName()))
					.compareTo(Double.valueOf(fields.get(FieldType.AMOUNT.getName()))) < 0) {
				fields.put(FieldType.TXNTYPE.getName(),
						(String) sessionMap.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()));
				fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(),
						(String) sessionMap.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()));

				Map<String, String> responseFields = transactionControllerServiceProvider.transact(fields,
						Constants.TXN_WS_INTERNAL.getValue());
				Fields field = new Fields(responseFields);

				field.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.BANK_SURCHARGE_REJECTED.getResponseMessage());
				field.put(FieldType.RESPONSE_CODE.getName(), ErrorType.BANK_SURCHARGE_REJECTED.getCode());
				field.put(FieldType.TXNTYPE.getName(), fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()));

				transactionResponser.removeInvalidResponseFields(field);
				transactionResponser.addResponseDateTime(field);
				String crisFlag = (String) sessionMap.get(FieldType.INTERNAL_IRCTC_YN.getName());
				if (StringUtils.isNotBlank(crisFlag)) {
					fields.put(FieldType.INTERNAL_IRCTC_YN.getName(), crisFlag);
				}
				responseCreator.ResponsePost(field);
				return Action.NONE;

			}
			logger.info("--------------------status ----" + fields.get(FieldType.STATUS.getName()));
			Map<String, String> response = transactionControllerServiceProvider.transact(fields,
					Constants.TXN_WS_INTERNAL.getValue());
			String crisFlag = (String) sessionMap.get(FieldType.INTERNAL_IRCTC_YN.getName());
			Fields responseMap = new Fields(response);
			// sessionMap.put(Constants.FIELDS.getValue(), responseMap);
			// responseMap.logAllFields("Response received from pgws :");
			responseMap.logAllFieldsUsingMasking("Response received from pgws :");
			sessionMap.put(Constants.FIELDS.getValue(), responseMap);
			sessionMap.put(FieldType.INTERNAL_ORIG_TXN_ID.getName(), responseMap.get(FieldType.TXN_ID.getName()));
			String status = responseMap.get(FieldType.STATUS.getName());
			String txnType = responseMap.get(FieldType.TXNTYPE.getName());
			String acquirer = responseMap.get(FieldType.ACQUIRER_TYPE.getName());
			sessionMap.put(FieldType.ACQUIRER_TYPE.getName(), acquirer);

			// logger.info(acquirer+"------------"+status+">>>>>>>>>>>>>>>>. responseMap
			// >>>>>>>>>>>. " + responseMap.getFieldsAsString());
			// logger.info((AcquirerType.NB_FEDERAL.getCode())+"----------------"+StatusType.SENT_TO_BANK.getName());
			if ((status.equals(StatusType.ENROLLED.getName())) || (status.equals(StatusType.CAPTURED.getName()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName())
							&& txnType.equals(PaymentType.NET_BANKING.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName()) && acquirer.equals(AcquirerType.BOB.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName())
							&& acquirer.equals(AcquirerType.KOTAK.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName())
							&& acquirer.equals(AcquirerType.IDBIBANK.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName())
							&& acquirer.equals(AcquirerType.MATCHMOVE.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName()) && acquirer.equals(AcquirerType.ATL.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName())
							&& acquirer.equals(AcquirerType.DIRECPAY.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName())
							&& acquirer.equals(AcquirerType.ATOM.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName())
							&& acquirer.equals(AcquirerType.APBL.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName())
							&& acquirer.equals(AcquirerType.MOBIKWIK.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName())
							&& acquirer.equals(AcquirerType.ISGPAY.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName())
							&& acquirer.equals(AcquirerType.AXISBANK.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName())
							&& acquirer.equals(AcquirerType.INGENICO.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName())
							&& acquirer.equals(AcquirerType.PAYTM.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName()) && acquirer.equals(AcquirerType.SBI.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName())
							&& acquirer.equals(AcquirerType.SBICARD.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName())
							&& acquirer.equals(AcquirerType.SBINB.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName())
							&& acquirer.equals(AcquirerType.PAYU.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName())
							&& acquirer.equals(AcquirerType.LYRA.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName())
							&& acquirer.equals(AcquirerType.FREECHARGE.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName())
							&& acquirer.equals(AcquirerType.PHONEPE.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName())
							&& acquirer.equals(AcquirerType.BILLDESK.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName())
							&& acquirer.equals(AcquirerType.CASHFREE.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName())
							&& acquirer.equals(AcquirerType.EASEBUZZ.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName())
							&& acquirer.equals(AcquirerType.AGREEPAY.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName())
							&& acquirer.equals(AcquirerType.PINELABS.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName())
							&& acquirer.equals(AcquirerType.YESBANKNB.getCode()))

					|| (status.equals(StatusType.SENT_TO_BANK.getName())
							&& acquirer.equals(AcquirerType.ICICIBANK.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName())
							&& acquirer.equals(AcquirerType.NB_FEDERAL.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName())
							&& acquirer.equals(AcquirerType.CAMSPAY.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName()) && acquirer.equals(AcquirerType.FSS.getCode()))
					|| (status.equals(StatusType.PENDING.getName()) && txnType.equals(PaymentType.UPI.getName()))) {

				logger.info("");
				// To check if acquirer is Citrus if acquirer null return
				// nothing and check capture status
				// String acquirer =
				// responseMap.get(FieldType.ACQUIRER_TYPE.getName());
				if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.ICICI_FIRSTDATA.getCode())) {
					sessionMap.put(FieldType.CVV.getName(), fields.get(FieldType.CVV.getName()));
					requestCreator.FirstDataEnrollRequest(responseMap);
				} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.IDFC_FIRSTDATA.getCode())) {
					sessionMap.put(FieldType.CVV.getName(), fields.get(FieldType.CVV.getName()));
					requestCreator.FirstDataEnrollRequest(responseMap);
				} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.FREECHARGE.getCode())) {
					sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
					sessionMap.put(FieldType.MERCHANT_ID.getName(), responseMap.get(FieldType.MERCHANT_ID.getName()));
					requestCreator.generateFreeChargeRequest(responseMap);

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

				} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.SBI.getCode())) {
					sessionMap.put(FieldType.MERCHANT_ID.getName(), responseMap.get(FieldType.MERCHANT_ID.getName()));
					responseMap.put(FieldType.CARD_NUMBER.getName(), RegExUtils.replaceAll(getCardNumber(), " ", ""));
					responseMap.put(FieldType.PAYMENTS_REGION.getName(),
							fields.get(FieldType.PAYMENTS_REGION.getName()));
					responseMap.put(FieldType.CARD_HOLDER_TYPE.getName(),
							fields.get(FieldType.CARD_HOLDER_TYPE.getName()));
					requestCreator.generateSbiRequest(responseMap, getCardsaveflag(), getExpiryMonth(),
							getExpiryYear());

				} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.SBICARD.getCode())) {
					sessionMap.put(FieldType.MERCHANT_ID.getName(), responseMap.get(FieldType.MERCHANT_ID.getName()));
					responseMap.put(FieldType.CARD_NUMBER.getName(), RegExUtils.replaceAll(getCardNumber(), " ", ""));
					responseMap.put(FieldType.PAYMENTS_REGION.getName(),
							fields.get(FieldType.PAYMENTS_REGION.getName()));
					responseMap.put(FieldType.CARD_HOLDER_TYPE.getName(),
							fields.get(FieldType.CARD_HOLDER_TYPE.getName()));
					requestCreator.generateSbiRequest(responseMap, getCardsaveflag(), getExpiryMonth(),
							getExpiryYear());

				} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.SBINB.getCode())) {
					sessionMap.put(FieldType.MERCHANT_ID.getName(), responseMap.get(FieldType.MERCHANT_ID.getName()));
					requestCreator.generateSbiRequest(responseMap, getCardsaveflag(), getExpiryMonth(),
							getExpiryYear());

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

				} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.ICICIBANK.getCode())) {
					sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
					requestCreator.generateIciciNBRequest(responseMap);

				} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.BOB.getCode())) {
					sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
					requestCreator.generateBobRequest(responseMap);

				} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.ISGPAY.getCode())) {

					sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
					// sessionMap.put(FieldType.PASSWORD.getName(),
					// responseMap.get(FieldType.PASSWORD.getName()));
					sessionMap.put(FieldType.PASSWORD.getName(), responseMap.get(FieldType.ADF5.getName()));
					sessionMap.put(FieldType.ADF4.getName(), responseMap.get(FieldType.ADF4.getName()));

					requestCreator.generateIsgpayRequest(responseMap);

				} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.FSS.getCode())) {
					sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
					requestCreator.generateFssRequest(responseMap);

				} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.KOTAK.getCode())) {
					sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
					sessionMap.put(FieldType.PASSWORD.getName(), responseMap.get(FieldType.ADF2.getName()));
					requestCreator.generateKotakRequest(responseMap);

				} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.BILLDESK.getCode())) {
					sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
					sessionMap.put(FieldType.PASSWORD.getName(), responseMap.get(FieldType.PASSWORD.getName()));
					sessionMap.put(FieldType.MERCHANT_ID.getName(), responseMap.get(FieldType.MERCHANT_ID.getName()));
					requestCreator.generateBilldeskRequest(responseMap);

				}

				else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.CASHFREE.getCode())) {
					sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
					sessionMap.put(FieldType.MERCHANT_ID.getName(), responseMap.get(FieldType.MERCHANT_ID.getName()));
					requestCreator.generateCashfreeRequest(responseMap);

				} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.AGREEPAY.getCode())) {
					sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
					sessionMap.put(FieldType.MERCHANT_ID.getName(), responseMap.get(FieldType.MERCHANT_ID.getName()));
					sessionMap.put(FieldType.ADF1.getName(), responseMap.get(FieldType.ADF1.getName()));
					requestCreator.generateAgreepayRequest(responseMap);

				} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.PINELABS.getCode())) {
					sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
					sessionMap.put(FieldType.MERCHANT_ID.getName(), responseMap.get(FieldType.MERCHANT_ID.getName()));
					sessionMap.put(FieldType.ADF1.getName(), responseMap.get(FieldType.ADF1.getName()));
					sessionMap.put(FieldType.ADF10.getName(), responseMap.get(FieldType.ADF10.getName()));
					logger.info("request -- xverify-" + responseMap.get(FieldType.ADF10.getName()));
					requestCreator.generatePinelabsRequest(responseMap);

				} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.YESBANKNB.getCode())) {
					sessionMap.put(FieldType.ADF1.getName(), responseMap.get(FieldType.ADF1.getName()));
					sessionMap.put(FieldType.ADF2.getName(), responseMap.get(FieldType.ADF2.getName()));
					sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
					sessionMap.put(FieldType.MERCHANT_ID.getName(), responseMap.get(FieldType.MERCHANT_ID.getName()));
					sessionMap.put(FieldType.ADF10.getName(), responseMap.get(FieldType.ADF10.getName()));
					requestCreator.generateYesBankNBRequest(responseMap);
				}

				else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.EASEBUZZ.getCode())) {
					sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.ADF1.getName()));
					sessionMap.put(FieldType.MERCHANT_ID.getName(), responseMap.get(FieldType.MERCHANT_ID.getName()));
					requestCreator.generateEasebuzzRequest(responseMap);

				}

				else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.DIRECPAY.getCode())) {
					PaymentType paymentType = PaymentType
							.getInstanceUsingCode(fields.get(FieldType.PAYMENT_TYPE.getName()));

					switch (paymentType) {
					case NET_BANKING:
						sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
						sessionMap.put(FieldType.PG_REF_NUM.getName(), responseMap.get(FieldType.PG_REF_NUM.getName()));
						requestCreator.generateDirecpayRequest(responseMap);
						break;

					case CREDIT_CARD:
					case DEBIT_CARD:
						sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
						sessionMap.put(FieldType.PG_REF_NUM.getName(), responseMap.get(FieldType.PG_REF_NUM.getName()));
						requestCreator.generateDirecpayRequest(responseMap);
						break;
					default:
						break;
					}
				}

				else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.ATOM.getCode())) {
					PaymentType paymentType = PaymentType
							.getInstanceUsingCode(fields.get(FieldType.PAYMENT_TYPE.getName()));

					switch (paymentType) {
					case NET_BANKING:
						sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.ADF4.getName()));
						sessionMap.put(FieldType.PG_REF_NUM.getName(), responseMap.get(FieldType.PG_REF_NUM.getName()));
						sessionMap.put(FieldType.IV.getName(), responseMap.get(FieldType.ADF3.getName()));
						sessionMap.put(FieldType.RESP_IV.getName(), responseMap.get(FieldType.ADF5.getName()));
						sessionMap.put(FieldType.RESP_TXN_KEY.getName(), responseMap.get(FieldType.ADF8.getName()));
						requestCreator.generateAtomRequest(responseMap);
						break;

					case CREDIT_CARD:
					case DEBIT_CARD:
						sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.ADF4.getName()));
						sessionMap.put(FieldType.PG_REF_NUM.getName(), responseMap.get(FieldType.PG_REF_NUM.getName()));
						sessionMap.put(FieldType.IV.getName(), responseMap.get(FieldType.ADF3.getName()));
						sessionMap.put(FieldType.RESP_IV.getName(), responseMap.get(FieldType.ADF5.getName()));
						sessionMap.put(FieldType.RESP_TXN_KEY.getName(), responseMap.get(FieldType.ADF8.getName()));
						requestCreator.generateAtomRequest(responseMap);
						break;
					case WALLET:
						sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.ADF4.getName()));
						sessionMap.put(FieldType.PG_REF_NUM.getName(), responseMap.get(FieldType.PG_REF_NUM.getName()));
						sessionMap.put(FieldType.IV.getName(), responseMap.get(FieldType.ADF3.getName()));
						sessionMap.put(FieldType.RESP_IV.getName(), responseMap.get(FieldType.ADF5.getName()));
						sessionMap.put(FieldType.RESP_TXN_KEY.getName(), responseMap.get(FieldType.ADF8.getName()));
						requestCreator.generateAtomRequest(responseMap);
						break;
					default:
						break;
					}
				}

				else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.INGENICO.getCode())) {
					PaymentType paymentType = PaymentType
							.getInstanceUsingCode(fields.get(FieldType.PAYMENT_TYPE.getName()));

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
					PaymentType paymentType = PaymentType
							.getInstanceUsingCode(fields.get(FieldType.PAYMENT_TYPE.getName()));

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
				}

				else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.PAYTM.getCode())) {
					PaymentType paymentType = PaymentType
							.getInstanceUsingCode(fields.get(FieldType.PAYMENT_TYPE.getName()));

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
						sessionMap.put(FieldType.MERCHANT_ID.getName(),
								responseMap.get(FieldType.MERCHANT_ID.getName()));
						requestCreator.generatePaytmRequest(responseMap);
						break;
					default:
						break;
					}
				}

				else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.PAYU.getCode())) {
					PaymentType paymentType = PaymentType
							.getInstanceUsingCode(fields.get(FieldType.PAYMENT_TYPE.getName()));
					sessionMap.put(FieldType.CUST_NAME.getName(), responseMap.get(FieldType.CUST_NAME.getName()));
					switch (paymentType) {
					case WALLET:
						sessionMap.put(FieldType.MERCHANT_ID.getName(),
								responseMap.get(FieldType.MERCHANT_ID.getName()));
						sessionMap.put(FieldType.PASSWORD.getName(), responseMap.get(FieldType.PASSWORD.getName()));
						sessionMap.put(FieldType.PG_REF_NUM.getName(), responseMap.get(FieldType.PG_REF_NUM.getName()));
						sessionMap.put(FieldType.PAYU_FINAL_REQUEST.getName(),
								responseMap.get(FieldType.PAYU_FINAL_REQUEST.getName()));
						sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
						sessionMap.put(FieldType.ADF3.getName(), responseMap.get(FieldType.ADF3.getName()));
						sessionMap.put(FieldType.TOTAL_AMOUNT.getName(),
								responseMap.get(FieldType.TOTAL_AMOUNT.getName()));

						requestCreator.generatePayuRequest(responseMap);
						break;
					case NET_BANKING:
						sessionMap.put(FieldType.MERCHANT_ID.getName(),
								responseMap.get(FieldType.MERCHANT_ID.getName()));
						sessionMap.put(FieldType.PASSWORD.getName(), responseMap.get(FieldType.PASSWORD.getName()));
						sessionMap.put(FieldType.PG_REF_NUM.getName(), responseMap.get(FieldType.PG_REF_NUM.getName()));
						sessionMap.put(FieldType.PAYU_FINAL_REQUEST.getName(),
								responseMap.get(FieldType.PAYU_FINAL_REQUEST.getName()));
						sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
						sessionMap.put(FieldType.ADF3.getName(), responseMap.get(FieldType.ADF3.getName()));
						sessionMap.put(FieldType.TOTAL_AMOUNT.getName(),
								responseMap.get(FieldType.TOTAL_AMOUNT.getName()));

						requestCreator.generatePayuRequest(responseMap);
						break;

					case CREDIT_CARD:
						// Save Card detail for Express Payment
						if (!StringUtils.isEmpty(getCardsaveflag()) && getCardsaveflag().equalsIgnoreCase("true")) {
							logger.info("-----------------Saving Card Details CC---------------------------------");
							fields.put(com.pay10.pg.core.payu.util.Constants.CCEXPMON, getExpiryMonth());
							fields.put(com.pay10.pg.core.payu.util.Constants.CCEXPYR, getExpiryYear());
							fields.put(FieldType.INTERNAL_CARD_ISSUER_BANK.getName(),
									MopType.getmop(getMopType()).getName());
							logger.info("Card Save Flag " + getCardsaveflag().equalsIgnoreCase("true"));
							logger.info("Respo Code " + fields.get(FieldType.RESPONSE_CODE.getName()));
							logger.info("Txn Type " + fields.get(FieldType.TXNTYPE.getName()));
							logger.info("Txn Type " + fields.getFieldsAsBlobString());
							if (StringUtils.isNotBlank(fields.get(FieldType.RESPONSE_CODE.getName()))
									&& fields.get(FieldType.RESPONSE_CODE.getName())
											.equalsIgnoreCase(ErrorType.SUCCESS.getCode())
									&& !fields.get(FieldType.TXNTYPE.getName())
											.equalsIgnoreCase(TransactionType.NEWORDER.getName())) {
								tokenManager.addToken(fields,
										userDao.findPayId(fields.get(FieldType.PAY_ID.getName())));
							}
						}

						sessionMap.put(FieldType.MERCHANT_ID.getName(),
								responseMap.get(FieldType.MERCHANT_ID.getName()));
						logger.info("password -------- : " + responseMap.get(FieldType.PASSWORD.getName()));
						sessionMap.put(FieldType.PASSWORD.getName(), responseMap.get(FieldType.PASSWORD.getName()));
						sessionMap.put(FieldType.PG_REF_NUM.getName(), responseMap.get(FieldType.PG_REF_NUM.getName()));
						sessionMap.put(FieldType.PAYU_FINAL_REQUEST.getName(),
								responseMap.get(FieldType.PAYU_FINAL_REQUEST.getName()));
						sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
						sessionMap.put(FieldType.ADF3.getName(), responseMap.get(FieldType.ADF3.getName()));
						sessionMap.put(FieldType.TOTAL_AMOUNT.getName(),
								responseMap.get(FieldType.TOTAL_AMOUNT.getName()));

						requestCreator.generatePayuRequest(responseMap);
					case DEBIT_CARD:
						if (!StringUtils.isEmpty(getCardsaveflag()) && getCardsaveflag().equalsIgnoreCase("true")) {
							logger.info("-----------------Saving Card Details DC---------------------------------");
							fields.put(com.pay10.pg.core.payu.util.Constants.CCEXPMON, getExpiryMonth());
							fields.put(com.pay10.pg.core.payu.util.Constants.CCEXPYR, getExpiryYear());
							fields.put(FieldType.INTERNAL_CARD_ISSUER_BANK.getName(),
									MopType.getmop(getMopType()).getName());
							logger.info("Card Save Flag " + getCardsaveflag().equalsIgnoreCase("true"));
							logger.info("Card Save Flag " + getCardsaveflag().equalsIgnoreCase("true"));
							logger.info("Respo Code " + fields.get(FieldType.RESPONSE_CODE.getName()));
							logger.info("Txn Type " + fields.get(FieldType.TXNTYPE.getName()));
							logger.info("Txn Type " + fields.getFieldsAsBlobString());
							if (StringUtils.isNotBlank(fields.get(FieldType.RESPONSE_CODE.getName()))
									&& fields.get(FieldType.RESPONSE_CODE.getName())
											.equalsIgnoreCase(ErrorType.SUCCESS.getCode())
									&& !fields.get(FieldType.TXNTYPE.getName())
											.equalsIgnoreCase(TransactionType.NEWORDER.getName())) {
								tokenManager.addToken(fields,
										userDao.findPayId(fields.get(FieldType.PAY_ID.getName())));
							}
						}

						sessionMap.put(FieldType.MERCHANT_ID.getName(),
								responseMap.get(FieldType.MERCHANT_ID.getName()));
						sessionMap.put(FieldType.PASSWORD.getName(), responseMap.get(FieldType.PASSWORD.getName()));
						sessionMap.put(FieldType.PG_REF_NUM.getName(), responseMap.get(FieldType.PG_REF_NUM.getName()));
						sessionMap.put(FieldType.PAYU_FINAL_REQUEST.getName(),
								responseMap.get(FieldType.PAYU_FINAL_REQUEST.getName()));
						sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
						sessionMap.put(FieldType.ADF3.getName(), responseMap.get(FieldType.ADF3.getName()));
						sessionMap.put(FieldType.TOTAL_AMOUNT.getName(),
								responseMap.get(FieldType.TOTAL_AMOUNT.getName()));

						requestCreator.generatePayuRequest(responseMap);
						break;
					case UPI:
						sessionMap.put(FieldType.MERCHANT_ID.getName(),
								responseMap.get(FieldType.MERCHANT_ID.getName()));
						sessionMap.put(FieldType.PASSWORD.getName(), responseMap.get(FieldType.PASSWORD.getName()));
						sessionMap.put(FieldType.PG_REF_NUM.getName(), responseMap.get(FieldType.PG_REF_NUM.getName()));
						sessionMap.put(FieldType.PAYU_FINAL_REQUEST.getName(),
								responseMap.get(FieldType.PAYU_FINAL_REQUEST.getName()));
						sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
						sessionMap.put(FieldType.ADF3.getName(), responseMap.get(FieldType.ADF3.getName()));
						sessionMap.put(FieldType.TOTAL_AMOUNT.getName(),
								responseMap.get(FieldType.TOTAL_AMOUNT.getName()));
						requestCreator.generatePayuRequest(responseMap);
						break;
					default:
						break;
					}
				}

				else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.CAMSPAY.getCode())) {
					PaymentType paymentType = PaymentType
							.getInstanceUsingCode(fields.get(FieldType.PAYMENT_TYPE.getName()));
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
					case WALLET:
						break;
					case NET_BANKING:
						sessionMap.put(FieldType.MERCHANT_ID.getName(),
								responseMap.get(FieldType.MERCHANT_ID.getName()));
						sessionMap.put(FieldType.PASSWORD.getName(), responseMap.get(FieldType.PASSWORD.getName()));
						sessionMap.put(FieldType.PG_REF_NUM.getName(), responseMap.get(FieldType.PG_REF_NUM.getName()));
						sessionMap.put(FieldType.CAMSPAY_FINAL_REQUEST.getName(),
								responseMap.get(FieldType.CAMSPAY_FINAL_REQUEST.getName()));
						sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
						sessionMap.put(FieldType.TOTAL_AMOUNT.getName(),
								responseMap.get(FieldType.TOTAL_AMOUNT.getName()));
						requestCreator.generateCamsPayRequest(responseMap);
						break;

					case CREDIT_CARD:
						sessionMap.put(FieldType.MERCHANT_ID.getName(),
								responseMap.get(FieldType.MERCHANT_ID.getName()));
						sessionMap.put(FieldType.PASSWORD.getName(), responseMap.get(FieldType.PASSWORD.getName()));
						sessionMap.put(FieldType.PG_REF_NUM.getName(), responseMap.get(FieldType.PG_REF_NUM.getName()));
						sessionMap.put(FieldType.CAMSPAY_FINAL_REQUEST.getName(),
								responseMap.get(FieldType.CAMSPAY_FINAL_REQUEST.getName()));
						sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
						sessionMap.put(FieldType.TOTAL_AMOUNT.getName(),
								responseMap.get(FieldType.TOTAL_AMOUNT.getName()));
						requestCreator.generateCamsPayRequest(responseMap);
					case DEBIT_CARD:
						sessionMap.put(FieldType.MERCHANT_ID.getName(),
								responseMap.get(FieldType.MERCHANT_ID.getName()));
						sessionMap.put(FieldType.PASSWORD.getName(), responseMap.get(FieldType.PASSWORD.getName()));
						sessionMap.put(FieldType.PG_REF_NUM.getName(), responseMap.get(FieldType.PG_REF_NUM.getName()));
						sessionMap.put(FieldType.CAMSPAY_FINAL_REQUEST.getName(),
								responseMap.get(FieldType.CAMSPAY_FINAL_REQUEST.getName()));
						sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
						sessionMap.put(FieldType.TOTAL_AMOUNT.getName(),
								responseMap.get(FieldType.TOTAL_AMOUNT.getName()));
						requestCreator.generateCamsPayRequest(responseMap);
						break;
					case UPI:
						sessionMap.put(FieldType.MERCHANT_ID.getName(),
								responseMap.get(FieldType.MERCHANT_ID.getName()));
						sessionMap.put(FieldType.PASSWORD.getName(), responseMap.get(FieldType.PASSWORD.getName()));
						sessionMap.put(FieldType.PG_REF_NUM.getName(), responseMap.get(FieldType.PG_REF_NUM.getName()));
						sessionMap.put(FieldType.CAMSPAY_FINAL_REQUEST.getName(),
								responseMap.get(FieldType.CAMSPAY_FINAL_REQUEST.getName()));
						sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
						sessionMap.put(FieldType.TOTAL_AMOUNT.getName(),
								responseMap.get(FieldType.TOTAL_AMOUNT.getName()));
						requestCreator.generateCamsPayRequest(responseMap);
						break;
					default:
						break;
					}
				}

				else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.PHONEPE.getCode())) {
					PaymentType paymentType = PaymentType
							.getInstanceUsingCode(fields.get(FieldType.PAYMENT_TYPE.getName()));

					switch (paymentType) {
					case WALLET:
						sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
						sessionMap.put(FieldType.ADF1.getName(), responseMap.get(FieldType.ADF1.getName()));
						sessionMap.put(FieldType.PG_REF_NUM.getName(), responseMap.get(FieldType.PG_REF_NUM.getName()));
						sessionMap.put(FieldType.MERCHANT_ID.getName(),
								responseMap.get(FieldType.MERCHANT_ID.getName()));
						requestCreator.generatePhonePeRequest(responseMap);
						break;
					default:
						break;
					}
				}

				else if ((!StringUtils.isEmpty(acquirer)) && (acquirer.equals(AcquirerType.LYRA.getCode()))) {
					requestCreator.lyraEnrollRequest(responseMap);
				}

				else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.MOBIKWIK.getCode())) {
					PaymentType paymentType = PaymentType
							.getInstanceUsingCode(fields.get(FieldType.PAYMENT_TYPE.getName()));

					switch (paymentType) {

					case WALLET:
						sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
						sessionMap.put(FieldType.PG_REF_NUM.getName(), responseMap.get(FieldType.PG_REF_NUM.getName()));
						requestCreator.generateMobikwikRequest(responseMap);
						break;
					default:
						break;
					}
				}

				else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.AXISBANK.getCode())) {
					PaymentType paymentType = PaymentType
							.getInstanceUsingCode(fields.get(FieldType.PAYMENT_TYPE.getName()));

					switch (paymentType) {
					case NET_BANKING:
						sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.ADF8.getName()));
						sessionMap.put(FieldType.IV.getName(), responseMap.get(FieldType.ADF5.getName()));
						sessionMap.put(FieldType.PASSWORD.getName(), responseMap.get(FieldType.ADF4.getName()));
						sessionMap.put(FieldType.PG_REF_NUM.getName(), responseMap.get(FieldType.PG_REF_NUM.getName()));
						sessionMap.put(FieldType.ADF10.getName(), responseMap.get(FieldType.ADF10.getName()));
						sessionMap.put(FieldType.ADF9.getName(), responseMap.get(FieldType.ADF9.getName()));

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
				}

				else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.IDBIBANK.getCode())) {

					PaymentType paymentType = PaymentType
							.getInstanceUsingCode(fields.get(FieldType.PAYMENT_TYPE.getName()));

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
							sessionMap.put(FieldType.PG_REF_NUM.getName(),
									responseMap.get(FieldType.PG_REF_NUM.getName()));
							requestCreator.generateIdbiRequest(responseMap);
						} else {
							sessionMap.put(FieldType.CVV.getName(), fields.get(FieldType.CVV.getName()));
							sessionMap.put(FieldType.TOKEN_ID.getName(), responseMap.get(FieldType.TOKEN_ID.getName()));
							sessionMap.put(FieldType.CARD_HOLDER_NAME.getName(),
									fields.get(FieldType.CARD_HOLDER_NAME.getName()));
							sessionMap.put(FieldType.CARD_NUMBER.getName(),
									fields.get(FieldType.CARD_NUMBER.getName()));
							sessionMap.put(FieldType.CARD_EXP_DT.getName(),
									fields.get(FieldType.CARD_EXP_DT.getName()));
							sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
							requestCreator.generateEnrollIdbiRequest(responseMap);
						}
						break;
					default:
						break;
					}

				}

				else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.MATCHMOVE.getCode())) {
					sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
					requestCreator.generateMatchMoveRequest(responseMap);
				}

				else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.ATL.getCode())) {
					sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
					sessionMap.put(FieldType.PASSWORD.getName(), responseMap.get(FieldType.PASSWORD.getName()));
					requestCreator.generateAtlRequest(responseMap);

				} else if ((!StringUtils.isEmpty(acquirer)) && (acquirer.equals(AcquirerType.NB_SBI.getCode())
						|| acquirer.equals(AcquirerType.NB_ALLAHABAD_BANK.getCode())
						|| acquirer.equals(AcquirerType.NB_AXIS_BANK.getCode())
						|| acquirer.equals(AcquirerType.NB_CORPORATION_BANK.getCode())
						|| acquirer.equals(AcquirerType.NB_ICICI_BANK.getCode())
						|| acquirer.equals(AcquirerType.NB_KARUR_VYSYA_BANK.getCode())
						|| acquirer.equals(AcquirerType.NB_SOUTH_INDIAN_BANK.getCode())
						|| acquirer.equals(AcquirerType.NB_VIJAYA_BANK.getCode())
						|| acquirer.equals(AcquirerType.WL_MOBIKWIK.getCode())
						|| acquirer.equals(AcquirerType.WL_OLAMONEY.getCode())
						|| acquirer.equals(AcquirerType.PAYTM.getCode()) || acquirer.equals(AcquirerType.PAYU.getCode())
						|| acquirer.equals(AcquirerType.CAMSPAY.getCode())
						|| acquirer.equals(AcquirerType.NB_KARNATAKA_BANK.getCode())
						|| acquirer.equals(AcquirerType.YESBANKNB.getCode()))) {
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

				responseMap.put(FieldType.TXNTYPE.getName(),
						(String) sessionMap.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()));
				transactionResponser.removeInvalidResponseFields(responseMap);
				transactionResponser.addResponseDateTime(responseMap);

				if (StringUtils.isNotBlank(crisFlag)) {
					responseMap.put(FieldType.INTERNAL_IRCTC_YN.getName(), crisFlag);
				}

				responseCreator.ResponsePost(responseMap);
				return Action.NONE;
			} else {
				responseMap.put(FieldType.INTERNAL_IRCTC_YN.getName(),
						(String) sessionMap.get(FieldType.INTERNAL_IRCTC_YN.getName()));
				if (StringUtils.isBlank(fields.get(FieldType.RESPONSE_CODE.getName()))) {
					responseMap.put(FieldType.RESPONSE_MESSAGE.getName(),
							ErrorType.INTERNAL_SYSTEM_ERROR.getResponseMessage());
					responseMap.put(FieldType.RESPONSE_CODE.getName(), ErrorType.INTERNAL_SYSTEM_ERROR.getCode());
				}
				responseMap.put(FieldType.TXNTYPE.getName(),
						(String) sessionMap.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()));
				transactionResponser.removeInvalidResponseFields(responseMap);
				transactionResponser.addResponseDateTime(responseMap);
				if (StringUtils.isNotBlank(crisFlag)) {
					responseMap.put(FieldType.INTERNAL_IRCTC_YN.getName(), crisFlag);
				}
				responseCreator.ResponsePost(responseMap);
				return Action.NONE;
			}

			responseMap.remove(FieldType.PAREQ.getName());
			responseMap.removeSecureFields();
		} catch (SystemException systemException) {
			logger.error("systemException", systemException);

		} catch (Exception exception) {
			logger.error("Error handling of transaction", exception);
			return ERROR;
		}
		return getAcquirerFlag();
	}

	// Remove Saved Card
	public String deleteSavedCard() {
		Fields fields = (Fields) sessionMap.get(Constants.FIELDS.getValue());
		if (null != tokenId) {
			fields.put(FieldType.TOKEN_ID.getName(), getTokenId());
			// tokenManager.removeSavedCard(fields,
			// userDao.findPayId(fields.get(FieldType.PAY_ID.getName())));
			tokenManager.removeSavedCard(fields);
			// tokenMap = tokenManager.getAll(fields,
			// userDao.findPayId(fields.get(FieldType.PAY_ID.getName())));
			tokenMap = tokenManager.getAll(fields);

			if (tokenMap.isEmpty()) {
				sessionMap.put(Constants.TOKEN.getValue(), "NA");
			} else {
				sessionPut(Constants.TOKEN.getValue(), tokenMap);
			}
		}
		return SUCCESS;
	}

	// for payment modes that are not supported
	public void prepareUnsupportedPaymnetResponse(Fields fields) throws SystemException {
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.PAYMENT_OPTION_NOT_SUPPORTED.getResponseMessage());
		fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.PAYMENT_OPTION_NOT_SUPPORTED.getCode());

		transactionResponser.removeInvalidResponseFields(fields);
		transactionResponser.addResponseDateTime(fields);
		fields.removeSecureFields();
		transactionResponser.addHash(fields);
		String crisFlag = (String) sessionMap.get(FieldType.INTERNAL_IRCTC_YN.getName());
		if (StringUtils.isNotBlank(crisFlag)) {
			fields.put(FieldType.INTERNAL_IRCTC_YN.getName(), crisFlag);
		}
		responseCreator.ResponsePost(fields);
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

	@Override
	public void setServletRequest(HttpServletRequest hReq) {
		this.request = hReq;
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

	public String getCardsaveflag() {
		return cardsaveflag;
	}

	public void setCardsaveflag(String cardsaveflag) {
		this.cardsaveflag = cardsaveflag;
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

	public String getUpiCustName() {
		return upiCustName;
	}

	public void setUpiCustName(String upiCustName) {
		this.upiCustName = upiCustName;
	}

	public String getVpa() {
		return vpa;
	}

	public void setVpa(String vpa) {
		this.vpa = vpa;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public String getSupportedPaymentTypeString() {
		return supportedPaymentTypeString;
	}

	public void setSupportedPaymentTypeString(String supportedPaymentTypeString) {
		this.supportedPaymentTypeString = supportedPaymentTypeString;
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

	public JSONObject getSupportedPaymentTypeJson() {
		return supportedPaymentTypeJson;
	}

	public void setSupportedPaymentTypeJson(JSONObject supportedPaymentTypeJson) {
		this.supportedPaymentTypeJson = supportedPaymentTypeJson;
	}

	public String getResellerId() {
		return resellerId;
	}

	public void setResellerId(String resellerId) {
		this.resellerId = resellerId;
	}

}