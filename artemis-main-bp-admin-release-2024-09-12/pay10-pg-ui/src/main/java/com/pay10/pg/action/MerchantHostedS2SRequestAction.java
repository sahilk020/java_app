package com.pay10.pg.action;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.pay10.commons.user.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.util.TokenHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.opensymphony.xwork2.Action;

import com.opensymphony.xwork2.ActionContext;
import com.pay10.commons.api.BindbControllerServiceProvider;
import com.pay10.commons.api.TransactionControllerServiceProvider;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.AccountCurrencyRegion;
import com.pay10.commons.user.CardHolderType;
import com.pay10.commons.user.TdrSettingDao;
import com.pay10.commons.user.Token;
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
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.SystemConstants;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.action.service.ActionService;
import com.pay10.pg.core.pageintegrator.GeneralValidator;
import com.pay10.pg.core.util.RequestFieldsCreator;
import com.pay10.pg.core.util.ResponseCreator;
import com.pay10.pg.core.util.SurchargeAmountCalculator;
import com.pay10.pg.core.util.TransactionResponser;

public class MerchantHostedS2SRequestAction  extends AbstractSecureAction implements ServletResponseAware, ServletRequestAware{

	private static final long serialVersionUID = -5772807047629774135L;

	private HttpServletRequest request;
	
	private HttpServletResponse response;
	
	@Autowired
    private BindbControllerServiceProvider binService;
	
	@Autowired
	private TransactionControllerServiceProvider transactionControllerServiceProvider;

	@Autowired
	private GeneralValidator generalValidator;
	
	@Autowired
	private RequestFieldsCreator requestCreator;
	
	@Autowired
	private ResponseCreator responseCreator;
	
	@Autowired
	private SurchargeAmountCalculator calculateSurchargeAmount;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	TransactionResponser transactionResponser;
	
	@Autowired
	private PropertiesManager propertiesManager;
	
	@Autowired
	private ActionService actionService;
	
	@Autowired
	private MacUtil macUtil;
	
	@Autowired
	private TdrSettingDao dao;
	
	Fields responseMap = new Fields();
	
	private static final String pendingTxnStatus = "Sent to Bank-Enrolled";
	
	private Map<String, String> responseFields= new HashMap<>();;
	
	Fields fields = new Fields();
	
	private static Logger logger = LoggerFactory.getLogger(MerchantHostedS2SRequestAction.class.getName());

	@Autowired
	private EkycVerDao ekycVerDao;

	@Override
	public String execute() {
		try {
			// clean session
			sessionMap.invalidate();
			
			
			// create new fields for direct transaction; decrypt fields

			Fields fields = actionService.prepareFieldsMerchantHosted(request.getParameterMap());
			logger.info("Raw Request Fields, merchant hosted:  " + fields.getFieldsAsString());
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
			User user1= userDao.findPayId(fields.get(FieldType.PAY_ID.getName()));
			
			
			if(user1.isMerchantS2SFlag()) {
				logger.info("S2S flag enabled....!!");
			}
			else {
				throw new SystemException(ErrorType.MERCHANT_HOSTED_S2S, "S2S flag is not enabled");
			}
			// Added to check if this is a reseller transaction
			if (StringUtils.isNotBlank(user.getResellerId())) {
				String resellerId = user.getResellerId();
				fields.put((FieldType.RESELLER_ID.getName()), resellerId);
			}

			sessionMap.put((FieldType.INTERNAL_HEADER_USER_AGENT.getName()), request.getHeader("User-Agent"));
			if (StringUtils.isNotBlank((String) sessionMap.get(FieldType.INTERNAL_HEADER_USER_AGENT.getName()))) {
				fields.put((FieldType.INTERNAL_HEADER_USER_AGENT.getName()),
						(String) sessionMap.get(FieldType.INTERNAL_HEADER_USER_AGENT.getName()));
			}

			MerchantPaymentType merchantPaymentType = MerchantPaymentType
					.getInstanceFromCode(fields.get(FieldType.PAYMENT_TYPE.getName()));
			// unsupported/invalid payment type received from merchant
			if (null == merchantPaymentType) {
				throw new SystemException(ErrorType.VALIDATION_FAILED, "Invalid payment type");
			}
			sessionMap.put((FieldType.SURCHARGE_FLAG.getName()), ((user.isSurchargeFlag()) ? "Y" : "N"));
			fields.put((FieldType.SURCHARGE_FLAG.getName()), ((user.isSurchargeFlag()) ? "Y" : "N"));
			// Map payment type from merchant to PG payment type
			switch (merchantPaymentType) {
			case CARD:
                // do bin check, card validation and assign mop
                // Call validateCardDetails instead of validateCardNumber.
                generalValidator.validateCardDetails(fields);
                Map<String, String> binMap = binService
                        .binfind(fields.get(FieldType.CARD_NUMBER.getName()).substring(0, 9));
                if (null == binMap || binMap.isEmpty()) {
                    throw new SystemException(ErrorType.CARD_NUMBER_NOT_SUPPORTED,
                            "Bin not present for card, OrderId: " + fields.get(FieldType.ORDER_ID.getName()));
                }
                fields.put(FieldType.PAYMENT_TYPE.getName(), binMap.get(FieldType.PAYMENT_TYPE.getName()));
                fields.put(FieldType.MOP_TYPE.getName(), binMap.get(FieldType.MOP_TYPE.getName()));
                fields.put(FieldType.PAYMENTS_REGION.getName(), binMap.get(FieldType.PAYMENTS_REGION.getName()));
                fields.put(FieldType.CARD_HOLDER_TYPE.getName(), binMap.get(FieldType.CARD_HOLDER_TYPE.getName()));
                fields.put(FieldType.INTERNAL_CARD_ISSUER_BANK.getName(),
                        binMap.get(FieldType.INTERNAL_CARD_ISSUER_BANK.getName()));
                fields.put(FieldType.INTERNAL_CARD_ISSUER_COUNTRY.getName(),
                        binMap.get(FieldType.INTERNAL_CARD_ISSUER_COUNTRY.getName()));
                sessionMap.put((FieldType.CARD_NUMBER.getName()), fields.get(FieldType.CARD_NUMBER.getName()));
                sessionMap.put((FieldType.CARD_EXP_DT.getName()), fields.get(FieldType.CARD_EXP_DT.getName()));

                sessionMap.put((FieldType.CVV.getName()), fields.get(FieldType.CVV.getName()));
                sessionMap.put((FieldType.INTERNAL_CARD_ISSUER_BANK.getName()),
                        fields.get(FieldType.INTERNAL_CARD_ISSUER_BANK.getName()));
                sessionMap.put((FieldType.INTERNAL_CARD_ISSUER_COUNTRY.getName()),
                        fields.get(FieldType.INTERNAL_CARD_ISSUER_COUNTRY.getName()));
                sessionMap.put((FieldType.PAYMENTS_REGION.getName()), binMap.get(FieldType.PAYMENTS_REGION.getName()));
                sessionMap.put((FieldType.CARD_HOLDER_TYPE.getName()),
                        fields.get(FieldType.CARD_HOLDER_TYPE.getName()));
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
			case WALLET:
                fields.put(FieldType.PAYMENT_TYPE.getName(), PaymentType.WALLET.getCode());
                fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), TransactionType.SALE.getName());
                fields.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
                fields.put(FieldType.CARD_HOLDER_TYPE.getName(), CardHolderType.CONSUMER.toString());
                fields.put(FieldType.PAYMENTS_REGION.getName(), AccountCurrencyRegion.DOMESTIC.toString());
                sessionMap.put((FieldType.CARD_HOLDER_TYPE.getName()), CardHolderType.CONSUMER.toString());
                sessionMap.put((FieldType.PAYMENTS_REGION.getName()), AccountCurrencyRegion.DOMESTIC.toString());
                break;	
			case QRCODE:
				fields.put(FieldType.PAYMENT_TYPE.getName(), PaymentType.QRCODE.getCode());
				fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), TransactionType.SALE.getName());
				fields.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
				fields.put(FieldType.CARD_HOLDER_TYPE.getName(), CardHolderType.CONSUMER.toString());
				fields.put(FieldType.PAYMENTS_REGION.getName(), AccountCurrencyRegion.DOMESTIC.toString());
				sessionMap.put((FieldType.CARD_HOLDER_TYPE.getName()), CardHolderType.CONSUMER.toString());
				sessionMap.put((FieldType.PAYMENTS_REGION.getName()), AccountCurrencyRegion.DOMESTIC.toString());
				break;
			case UPI:
				fields.put(FieldType.PAYMENT_TYPE.getName(), PaymentType.UPI.getCode());
				fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), TransactionType.SALE.getName());
				fields.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
				fields.put(FieldType.CARD_HOLDER_TYPE.getName(), CardHolderType.CONSUMER.toString());
				fields.put(FieldType.PAYMENTS_REGION.getName(), AccountCurrencyRegion.DOMESTIC.toString());
				sessionMap.put((FieldType.CARD_HOLDER_TYPE.getName()), CardHolderType.CONSUMER.toString());
				sessionMap.put((FieldType.PAYMENTS_REGION.getName()), AccountCurrencyRegion.DOMESTIC.toString());
				break;

	
			}

			String origTxnType = ModeType.getDefaultPurchaseTransaction(user.getModeType()).getName();
			sessionMap.put((FieldType.INTERNAL_ORIG_TXN_TYPE.getName()),
					ModeType.getDefaultPurchaseTransaction(user.getModeType()).getName());
			fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), origTxnType);

			// put original transaction type
			sessionMap.put((FieldType.RETURN_URL.getName()), fields.get(FieldType.RETURN_URL.getName()));

			// --------- added by sonu for IP ------------
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
			// fields.put((FieldType.INTERNAL_CUST_IP.getName()),
			// request.getHeader("X-Forwarded-For"));
			// ------------ Completed ----------------------

			// fields.put((FieldType.INTERNAL_CUST_IP.getName()),
			// request.getHeader("X-Forwarded-For"));
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
			sessionMap.put(FieldType.INTERNAL_CUST_COUNTRY_NAME.getName(),
					fields.get(FieldType.INTERNAL_CUST_COUNTRY_NAME.getName()));
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
			sessionMap.put(FieldType.INTERNAL_ORIG_TXN_ID.getName(),
					fields.get(FieldType.INTERNAL_ORIG_TXN_ID.getName()));

			// handle surcharge flg and amount
			handleTransactionCharges(fields);
			sessionMap.put(FieldType.INTERNAL_CUST_IP.getName(),
					fields.get(FieldType.INTERNAL_CUST_IP.getName()));

			Map<String, String> response = transactionControllerServiceProvider.transact(fields,
					Constants.TXN_WS_INTERNAL.getValue());
			Fields responseMap = new Fields(response);
			// sessionMap.put(Constants.FIELDS.getValue(), responseMap);
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
			fields.put(FieldType.RESPONSE_CODE.getName(), responseMap.get(FieldType.RESPONSE_CODE.getName()));
			fields.put(FieldType.STATUS.getName(), status);

			fields.put(FieldType.RESPONSE_MESSAGE.getName(),
					responseMap.get(FieldType.RESPONSE_MESSAGE.getName()));
			fields.put(FieldType.PG_TXN_MESSAGE.getName(),
					responseMap.get(FieldType.PG_TXN_MESSAGE.getName()));
			fields.put(FieldType.TXNTYPE.getName(),
					txnType);

			if (((status.equals(StatusType.ENROLLED.getName())) || (status.equals(StatusType.CAPTURED.getName()))
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
					|| (status.equals(StatusType.SENT_TO_BANK.getName())
							&& acquirer.equals(AcquirerType.PAYU.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName())
							&& acquirer.equals(AcquirerType.PHONEPE.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName())
							&& acquirer.equals(AcquirerType.ICICIBANK.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName())
							&& acquirer.equals(AcquirerType.COSMOS.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName())
							&& acquirer.equals(AcquirerType.LYRA.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName())
							&& acquirer.equals(AcquirerType.CASHFREE.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName())
							&& acquirer.equals(AcquirerType.PINELABS.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName())
							&& acquirer.equals(AcquirerType.EASEBUZZ.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName())
							&& acquirer.equals(AcquirerType.AGREEPAY.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName()) && acquirer.equals(AcquirerType.SBI.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName())
							&& acquirer.equals(AcquirerType.SBICARD.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName())
							&& acquirer.equals(AcquirerType.SBINB.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName())
							&& acquirer.equals(AcquirerType.NB_FEDERAL.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName())
							&& acquirer.equals(AcquirerType.YESBANKNB.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName())
							&& acquirer.equals(AcquirerType.CAMSPAY.getCode()))
					|| (status.equals(StatusType.SENT_TO_BANK.getName()) && acquirer.equals(AcquirerType.FSS.getCode())))
				
					) {

				// To check if acquirer is Citrus if acquirer null return
				// nothing and check capture status
				// String acquirer =
				// responseMap.get(FieldType.ACQUIRER_TYPE.getName());
				if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.ICICI_FIRSTDATA.getCode())) {
					sessionMap.put(FieldType.CVV.getName(), fields.get(FieldType.CVV.getName()));
					addParams(requestCreator.FirstDataEnrollRequest(responseMap),fields);
				} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.IDFC_FIRSTDATA.getCode())) {
					sessionMap.put(FieldType.CVV.getName(), fields.get(FieldType.CVV.getName()));
					addParams(requestCreator.FirstDataEnrollRequest(responseMap),fields);
				} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.YESBANKCB.getCode())) {
					sessionMap.put(FieldType.CVV.getName(), fields.get(FieldType.CVV.getName()));
					sessionMap.put(FieldType.CARD_NUMBER.getName(), fields.get(FieldType.CARD_NUMBER.getName()));
					sessionMap.put(FieldType.CARD_EXP_DT.getName(), fields.get(FieldType.CARD_EXP_DT.getName()));
					addParams(requestCreator.cyberSourceEnrollRequest(responseMap),fields);
				} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.ICICI_MPGS.getCode())) {
					sessionMap.put(FieldType.CVV.getName(), fields.get(FieldType.CVV.getName()));
					sessionMap.put(FieldType.CARD_NUMBER.getName(), fields.get(FieldType.CARD_NUMBER.getName()));
					sessionMap.put(FieldType.CARD_EXP_DT.getName(), fields.get(FieldType.CARD_EXP_DT.getName()));
					addParams(requestCreator.iciciMpgsEnrollRequest(responseMap),fields);
				} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.HDFC.getCode())) {
					if (txnType.equals(PaymentType.UPI.getName())) {

					} else {
						addParams(requestCreator.EnrollRequest(responseMap),fields);
					}

				} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.ICICIBANK.getCode())) {
					sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
					addParams(requestCreator.generateIciciNBRequest(responseMap),fields);

				} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.FEDERAL.getCode())) {
					sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
					addParams(requestCreator.generateFederalRequest(responseMap),fields);

				} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.NB_FEDERAL.getCode())) {
					sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
					sessionMap.put(FieldType.ADF1.getName(), responseMap.get(FieldType.ADF1.getName()));
					sessionMap.put(FieldType.ADF2.getName(), responseMap.get(FieldType.ADF2.getName()));
					sessionMap.put(FieldType.ADF3.getName(), responseMap.get(FieldType.ADF3.getName()));
					sessionMap.put(FieldType.PG_REF_NUM.getName(), responseMap.get(FieldType.PG_REF_NUM.getName()));

					addParams(requestCreator.generateFederalBankNBRequest(responseMap),fields);

				} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.BOB.getCode())) {
					sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
					addParams(requestCreator.generateBobRequest(responseMap),fields);

				} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.ISGPAY.getCode())) {

					sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
					sessionMap.put(FieldType.PASSWORD.getName(), responseMap.get(FieldType.ADF5.getName()));
					sessionMap.put(FieldType.ADF4.getName(), responseMap.get(FieldType.ADF4.getName()));
					// sessionMap.put(FieldType.PASSWORD.getName(),
					// responseMap.get(FieldType.PASSWORD.getName()));

					addParams(requestCreator.generateIsgpayRequest(responseMap),fields);

				} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.FSS.getCode())) {
					sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
					addParams(requestCreator.generateFssRequest(responseMap),fields);

				} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.KOTAK.getCode())) {
					sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
					sessionMap.put(FieldType.PASSWORD.getName(), responseMap.get(FieldType.PASSWORD.getName()));
					addParams(requestCreator.generateKotakRequest(responseMap),fields);

				}
			else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.COSMOS.getCode())) {
				sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
				addParams(requestCreator.generatecosmosQRBankNBRequest(responseMap), fields);

			} 
				else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.BILLDESK.getCode())) {
					sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
					sessionMap.put(FieldType.PASSWORD.getName(), responseMap.get(FieldType.PASSWORD.getName()));
					sessionMap.put(FieldType.MERCHANT_ID.getName(), responseMap.get(FieldType.MERCHANT_ID.getName()));
					addParams(requestCreator.generateBilldeskRequest(responseMap),fields);

				}

				/*
				 * else if ((!StringUtils.isEmpty(acquirer)) &&
				 * acquirer.equals(AcquirerType.CASHFREE.getCode())) {
				 * sessionMap.put(FieldType.TXN_KEY.getName(),
				 * responseMap.get(FieldType.TXN_KEY.getName()));
				 * sessionMap.put(FieldType.MERCHANT_ID.getName(),
				 * responseMap.get(FieldType.MERCHANT_ID.getName()));
				 * requestCreator.generateCashfreeRequest(responseMap));
				 * 
				 * }
				 */

				else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.CASHFREE.getCode())) {
					sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
					sessionMap.put(FieldType.MERCHANT_ID.getName(), responseMap.get(FieldType.MERCHANT_ID.getName()));
					addParams(requestCreator.generateCashfreeRequest(responseMap),fields);

				}

				else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.EASEBUZZ.getCode())) {
					sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.ADF1.getName()));
					sessionMap.put(FieldType.MERCHANT_ID.getName(), responseMap.get(FieldType.MERCHANT_ID.getName()));
					addParams(requestCreator.generateEasebuzzRequest(responseMap),fields);

				} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.SBI.getCode())) {
					sessionMap.put(FieldType.MERCHANT_ID.getName(), responseMap.get(FieldType.MERCHANT_ID.getName()));
					addParams(requestCreator.generateSbiRequest(responseMap, null, null, null),fields);

				} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.SBICARD.getCode())) {
					sessionMap.put(FieldType.MERCHANT_ID.getName(), responseMap.get(FieldType.MERCHANT_ID.getName()));
					addParams(requestCreator.generateSbiRequest(responseMap, null, null, null),fields);

				} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.SBINB.getCode())) {
					sessionMap.put(FieldType.MERCHANT_ID.getName(), responseMap.get(FieldType.MERCHANT_ID.getName()));
					addParams(requestCreator.generateSbiRequest(responseMap, null, null, null),fields);

				} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.AGREEPAY.getCode())) {
					sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
					sessionMap.put(FieldType.MERCHANT_ID.getName(), responseMap.get(FieldType.MERCHANT_ID.getName()));
					// added by Radhe
					sessionMap.put(FieldType.ADF1.getName(), responseMap.get(FieldType.ADF1.getName()));
					addParams(requestCreator.generateAgreepayRequest(responseMap),fields);
				} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.PINELABS.getCode())) {
					sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
					sessionMap.put(FieldType.MERCHANT_ID.getName(), responseMap.get(FieldType.MERCHANT_ID.getName()));
					sessionMap.put(FieldType.ADF1.getName(), responseMap.get(FieldType.ADF1.getName()));
					sessionMap.put(FieldType.ADF10.getName(), responseMap.get(FieldType.ADF10.getName()));
					addParams(requestCreator.generatePinelabsRequest(responseMap),fields);

				}

				else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.YESBANKNB.getCode())) {
					sessionMap.put(FieldType.PG_REF_NUM.getName(), responseMap.get(FieldType.PG_REF_NUM.getName()));
					sessionMap.put(FieldType.ADF1.getName(), responseMap.get(FieldType.ADF1.getName()));
					sessionMap.put(FieldType.ADF2.getName(), responseMap.get(FieldType.ADF2.getName()));
					sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
					sessionMap.put(FieldType.MERCHANT_ID.getName(), responseMap.get(FieldType.MERCHANT_ID.getName()));
					sessionMap.put(FieldType.ADF10.getName(), responseMap.get(FieldType.ADF10.getName()));
					addParams(requestCreator.generateYesBankNBRequest(responseMap),fields);
				}

				else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.DIRECPAY.getCode())) {
					PaymentType paymentType = PaymentType
							.getInstanceUsingCode(fields.get(FieldType.PAYMENT_TYPE.getName()));

					switch (paymentType) {
					case NET_BANKING:
						sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
						sessionMap.put(FieldType.PG_REF_NUM.getName(), responseMap.get(FieldType.PG_REF_NUM.getName()));
						addParams(requestCreator.generateDirecpayRequest(responseMap),fields);
						break;

					case CREDIT_CARD:
					case DEBIT_CARD:
						sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
						sessionMap.put(FieldType.PG_REF_NUM.getName(), responseMap.get(FieldType.PG_REF_NUM.getName()));
						addParams(requestCreator.generateDirecpayRequest(responseMap),fields);
						break;
					default:
						break;
					}
				}else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.INGENICO.getCode())) {
                    PaymentType paymentType = PaymentType
                            .getInstanceUsingCode(fields.get(FieldType.PAYMENT_TYPE.getName()));

                    switch (paymentType) {
                        case CREDIT_CARD:
                        case DEBIT_CARD:
                            sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
                            sessionMap.put(FieldType.PG_REF_NUM.getName(), responseMap.get(FieldType.PG_REF_NUM.getName()));
                            sessionMap.put(FieldType.IV.getName(), responseMap.get(FieldType.PASSWORD.getName()));
                            addParams(requestCreator.generateIngenicoRequest(responseMap),fields);
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
						addParams(requestCreator.generateAtomRequest(responseMap),fields);
						break;

					case UPI:
						sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.ADF4.getName()));
						sessionMap.put(FieldType.PG_REF_NUM.getName(), responseMap.get(FieldType.PG_REF_NUM.getName()));
						sessionMap.put(FieldType.IV.getName(), responseMap.get(FieldType.ADF3.getName()));
						sessionMap.put(FieldType.RESP_IV.getName(), responseMap.get(FieldType.ADF5.getName()));
						sessionMap.put(FieldType.RESP_TXN_KEY.getName(), responseMap.get(FieldType.ADF8.getName()));
						addParams(requestCreator.generateAtomRequest(responseMap),fields);
						break;

					case CREDIT_CARD:
					case DEBIT_CARD:
						sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
						sessionMap.put(FieldType.PG_REF_NUM.getName(), responseMap.get(FieldType.PG_REF_NUM.getName()));
						sessionMap.put(FieldType.IV.getName(), responseMap.get(FieldType.ADF3.getName()));
						sessionMap.put(FieldType.RESP_IV.getName(), responseMap.get(FieldType.ADF5.getName()));
						sessionMap.put(FieldType.RESP_TXN_KEY.getName(), responseMap.get(FieldType.ADF8.getName()));
						addParams(requestCreator.generateAtomRequest(responseMap),fields);
						break;
					 case WALLET:
                         sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.ADF4.getName()));
                         sessionMap.put(FieldType.PG_REF_NUM.getName(), responseMap.get(FieldType.PG_REF_NUM.getName()));
                         sessionMap.put(FieldType.IV.getName(), responseMap.get(FieldType.ADF3.getName()));
                         sessionMap.put(FieldType.RESP_IV.getName(), responseMap.get(FieldType.ADF5.getName()));
                         sessionMap.put(FieldType.RESP_TXN_KEY.getName(), responseMap.get(FieldType.ADF8.getName()));
                         addParams(requestCreator.generateAtomRequest(responseMap),fields);
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
						addParams(requestCreator.generateApblRequest(responseMap),fields);
						break;
					case WALLET:
                        sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
                        sessionMap.put(FieldType.PG_REF_NUM.getName(), responseMap.get(FieldType.PG_REF_NUM.getName()));
                        sessionMap.put(FieldType.IV.getName(), responseMap.get(FieldType.ADF3.getName()));
                        sessionMap.put(FieldType.RESP_IV.getName(), responseMap.get(FieldType.ADF5.getName()));
                        sessionMap.put(FieldType.RESP_TXN_KEY.getName(), responseMap.get(FieldType.ADF8.getName()));
                        addParams(requestCreator.generateApblRequest(responseMap),fields);
                        break;	

					default:
						break;
					}
				}else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.PAYTM.getCode())) {
                    PaymentType paymentType = PaymentType
                            .getInstanceUsingCode(fields.get(FieldType.PAYMENT_TYPE.getName()));

                    switch (paymentType) {
                        case WALLET:
                            sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
                            sessionMap.put(FieldType.PG_REF_NUM.getName(), responseMap.get(FieldType.PG_REF_NUM.getName()));
                            sessionMap.put(FieldType.MERCHANT_ID.getName(),
                                    responseMap.get(FieldType.MERCHANT_ID.getName()));
                            addParams(requestCreator.generatePaytmRequest(responseMap),fields);
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
                        sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
                        sessionMap.put(FieldType.ADF3.getName(), responseMap.get(FieldType.ADF3.getName()));
                        sessionMap.put(FieldType.TOTAL_AMOUNT.getName(),
                                responseMap.get(FieldType.TOTAL_AMOUNT.getName()));

                        sessionMap.put(FieldType.MERCHANT_ID.getName(),
                                responseMap.get(FieldType.MERCHANT_ID.getName()));
                        sessionMap.put(FieldType.PASSWORD.getName(), responseMap.get(FieldType.PASSWORD.getName()));
                        sessionMap.put(FieldType.PG_REF_NUM.getName(), responseMap.get(FieldType.PG_REF_NUM.getName()));
                        sessionMap.put(FieldType.PAYU_FINAL_REQUEST.getName(),
                                responseMap.get(FieldType.PAYU_FINAL_REQUEST.getName()));
                        addParams(requestCreator.generatePayuRequest(responseMap),fields);
                        break;
					case NET_BANKING:
						sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
						sessionMap.put(FieldType.ADF3.getName(), responseMap.get(FieldType.ADF3.getName()));
						sessionMap.put(FieldType.TOTAL_AMOUNT.getName(),
								responseMap.get(FieldType.TOTAL_AMOUNT.getName()));

						sessionMap.put(FieldType.MERCHANT_ID.getName(),
								responseMap.get(FieldType.MERCHANT_ID.getName()));
						sessionMap.put(FieldType.PASSWORD.getName(), responseMap.get(FieldType.PASSWORD.getName()));
						sessionMap.put(FieldType.PG_REF_NUM.getName(), responseMap.get(FieldType.PG_REF_NUM.getName()));
						sessionMap.put(FieldType.PAYU_FINAL_REQUEST.getName(),
								responseMap.get(FieldType.PAYU_FINAL_REQUEST.getName()));
						addParams(requestCreator.generatePayuRequest(responseMap),fields);
						break;
					case CREDIT_CARD:
                        if (!StringUtils.isEmpty(fields.get(FieldType.CARD_SAVE_FLAG.getName())) && fields.get(FieldType.CARD_SAVE_FLAG.getName()).equalsIgnoreCase("true")) {
                            logger.info("-----------------Saving Card Details CC---------------------------------");

                            String expMonth = fields.get(FieldType.CARD_EXP_DT.getName()).substring(0, fields.get(FieldType.CARD_EXP_DT.getName()).length() - 4);
                            String expYear = fields.get(FieldType.CARD_EXP_DT.getName()).substring(fields.get(FieldType.CARD_EXP_DT.getName()).length() - 4);
                            fields.put(com.pay10.pg.core.payu.util.Constants.CCEXPMON, expMonth);
                            fields.put(com.pay10.pg.core.payu.util.Constants.CCEXPYR, expYear);
                            fields.put(FieldType.INTERNAL_CARD_ISSUER_BANK.getName(), MopType.getmop(fields.get(FieldType.MOP_TYPE.getName())).getName());
                            logger.info("Card Save Flag " + fields.get(FieldType.CARD_SAVE_FLAG.getName()).equalsIgnoreCase("true"));
                            logger.info("Respo Code " + fields.get(FieldType.RESPONSE_CODE.getName()));
                            logger.info("Txn Type " + fields.get(FieldType.TXNTYPE.getName()));
                            logger.info("Txn Type " + fields.getFieldsAsBlobString());
                            if (StringUtils.isNotBlank(fields.get(FieldType.RESPONSE_CODE.getName()))
                                    && fields.get(FieldType.RESPONSE_CODE.getName()).equalsIgnoreCase(ErrorType.SUCCESS.getCode())
                                    && !fields.get(FieldType.TXNTYPE.getName())
                                    .equalsIgnoreCase(TransactionType.NEWORDER.getName())) {
                                //tokenManager.addToken(fields, userDao.findPayId(fields.get(FieldType.PAY_ID.getName())));
                            }
                        }

                        sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
                        sessionMap.put(FieldType.ADF3.getName(), responseMap.get(FieldType.ADF3.getName()));
                        sessionMap.put(FieldType.TOTAL_AMOUNT.getName(),
                                responseMap.get(FieldType.TOTAL_AMOUNT.getName()));

                        sessionMap.put(FieldType.MERCHANT_ID.getName(),
                                responseMap.get(FieldType.MERCHANT_ID.getName()));
                        sessionMap.put(FieldType.PASSWORD.getName(), responseMap.get(FieldType.PASSWORD.getName()));
                        sessionMap.put(FieldType.PG_REF_NUM.getName(), responseMap.get(FieldType.PG_REF_NUM.getName()));
                        sessionMap.put(FieldType.PAYU_FINAL_REQUEST.getName(),
                                responseMap.get(FieldType.PAYU_FINAL_REQUEST.getName()));
                        addParams(requestCreator.generatePayuRequest(responseMap),fields);
                    case DEBIT_CARD:

                        if (!StringUtils.isEmpty(fields.get(FieldType.CARD_SAVE_FLAG.getName())) && fields.get(FieldType.CARD_SAVE_FLAG.getName()).equalsIgnoreCase("true")) {
                            logger.info("-----------------Saving Card Details DC---------------------------------");
                            String expMonth = fields.get(FieldType.CARD_EXP_DT.getName()).substring(0, fields.get(FieldType.CARD_EXP_DT.getName()).length() - 4);
                            String expYear = fields.get(FieldType.CARD_EXP_DT.getName()).substring(fields.get(FieldType.CARD_EXP_DT.getName()).length() - 4);
                            fields.put(com.pay10.pg.core.payu.util.Constants.CCEXPMON, expMonth);
                            fields.put(com.pay10.pg.core.payu.util.Constants.CCEXPYR, expYear);
                            fields.put(FieldType.INTERNAL_CARD_ISSUER_BANK.getName(), MopType.getmop(fields.get(FieldType.MOP_TYPE.getName())).getName());
                            logger.info("Card Save Flag " + fields.get(FieldType.CARD_SAVE_FLAG.getName()).equalsIgnoreCase("true"));
                            logger.info("Card Save Flag " + fields.get(FieldType.CARD_SAVE_FLAG.getName()).equalsIgnoreCase("true"));
                            logger.info("Respo Code " + fields.get(FieldType.RESPONSE_CODE.getName()));
                            logger.info("Txn Type " + fields.get(FieldType.TXNTYPE.getName()));
                            logger.info("Txn Type " + fields.getFieldsAsBlobString());
                            if (StringUtils.isNotBlank(fields.get(FieldType.RESPONSE_CODE.getName()))
                                    && fields.get(FieldType.RESPONSE_CODE.getName()).equalsIgnoreCase(ErrorType.SUCCESS.getCode())
                                    && !fields.get(FieldType.TXNTYPE.getName())
                                    .equalsIgnoreCase(TransactionType.NEWORDER.getName())) {
                                //tokenManager.addToken(fields, userDao.findPayId(fields.get(FieldType.PAY_ID.getName())));
                            }
                        }

                        sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
                        sessionMap.put(FieldType.ADF3.getName(), responseMap.get(FieldType.ADF3.getName()));
                        sessionMap.put(FieldType.TOTAL_AMOUNT.getName(),
                                responseMap.get(FieldType.TOTAL_AMOUNT.getName()));

                        sessionMap.put(FieldType.MERCHANT_ID.getName(),
                                responseMap.get(FieldType.MERCHANT_ID.getName()));
                        sessionMap.put(FieldType.PASSWORD.getName(), responseMap.get(FieldType.PASSWORD.getName()));
                        sessionMap.put(FieldType.PG_REF_NUM.getName(), responseMap.get(FieldType.PG_REF_NUM.getName()));
                        sessionMap.put(FieldType.PAYU_FINAL_REQUEST.getName(),
                                responseMap.get(FieldType.PAYU_FINAL_REQUEST.getName()));
                        addParams(requestCreator.generatePayuRequest(responseMap),fields);
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
						addParams(requestCreator.generateCamsPayRequest(responseMap),fields);
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
                        addParams( requestCreator.generateCamsPayRequest(responseMap),fields);
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
                        addParams(requestCreator.generateCamsPayRequest(responseMap),fields);
                        break;	

					default:
						break;
					}
				}else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.PHONEPE.getCode())) {
                    PaymentType paymentType = PaymentType
                            .getInstanceUsingCode(fields.get(FieldType.PAYMENT_TYPE.getName()));

                    switch (paymentType) {
                        case WALLET:
                            sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
                            sessionMap.put(FieldType.ADF1.getName(), responseMap.get(FieldType.ADF1.getName()));
                            sessionMap.put(FieldType.PG_REF_NUM.getName(), responseMap.get(FieldType.PG_REF_NUM.getName()));
                            sessionMap.put(FieldType.MERCHANT_ID.getName(),
                                    responseMap.get(FieldType.MERCHANT_ID.getName()));
                           addParams(requestCreator.generatePhonePeRequest(responseMap),fields);
                            break;
                        default:
                            break;
                    }
                } 


				else if ((!StringUtils.isEmpty(acquirer)) && (acquirer.equals(AcquirerType.LYRA.getCode()))) {
					addParams(requestCreator.lyraEnrollRequest(responseMap),fields);
				}
				 else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.MOBIKWIK.getCode())) {
	                    PaymentType paymentType = PaymentType
	                            .getInstanceUsingCode(fields.get(FieldType.PAYMENT_TYPE.getName()));

	                    switch (paymentType) {

	                        case WALLET:
	                            sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
	                            sessionMap.put(FieldType.PG_REF_NUM.getName(), responseMap.get(FieldType.PG_REF_NUM.getName()));
	                           addParams(requestCreator.generateMobikwikRequest(responseMap),fields);
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
						addParams(requestCreator.generateAxisBankNBRequest(responseMap),fields);
						break;

					case CREDIT_CARD:
                    case DEBIT_CARD:
                        sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
                        sessionMap.put(FieldType.PG_REF_NUM.getName(), responseMap.get(FieldType.PG_REF_NUM.getName()));
                        addParams(requestCreator.generateAxisBankRequest(responseMap),fields);
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
						addParams(requestCreator.generateIdbiRequest(responseMap),fields);
						break;
					 case CREDIT_CARD:
                     case DEBIT_CARD:
                         String integrationMode = PropertiesManager.propertiesMap.get("IDBIINTEGRATIONMODE");
                         if (StringUtils.isNotBlank(integrationMode) && integrationMode.equals("REDIRECTION")) {
                             sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
                             sessionMap.put(FieldType.PG_REF_NUM.getName(),
                                     responseMap.get(FieldType.PG_REF_NUM.getName()));
                             addParams(requestCreator.generateIdbiRequest(responseMap),fields);
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
                             addParams(requestCreator.generateEnrollIdbiRequest(responseMap),fields);
                         }
                         break;
                    	

					default:
						break;
					}
				}

				else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.MATCHMOVE.getCode())) {
					sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
					addParams(requestCreator.generateMatchMoveRequest(responseMap),fields);
				} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.ATL.getCode())) {
					sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
					sessionMap.put(FieldType.PASSWORD.getName(), responseMap.get(FieldType.PASSWORD.getName()));
					addParams(requestCreator.generateAtlRequest(responseMap),fields);

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
						|| acquirer.equals(AcquirerType.NB_KARNATAKA_BANK.getCode()))) {
					sessionMap.put(FieldType.TXN_KEY.getName(), responseMap.get(FieldType.TXN_KEY.getName()));
					sessionMap.put(FieldType.PASSWORD.getName(), responseMap.get(FieldType.PASSWORD.getName()));
					addParams(requestCreator.generateIPayRequest(responseMap),fields);
				} else if ((!StringUtils.isEmpty(acquirer)) && acquirer.equals(AcquirerType.AXISMIGS.getCode())) {

					addParams(requestCreator.sendMigsEnrollTransaction(responseMap),fields);

				} else if (!(pendingTxnStatus.contains(status))) {
					sessionMap.invalidate();
					fields.put(FieldType.RESPONSE_MESSAGE.getName(),
							ErrorType.INTERNAL_SYSTEM_ERROR.getResponseMessage());
					fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.INTERNAL_SYSTEM_ERROR.getCode());
					fields.put(FieldType.STATUS.getName(), ErrorType.INTERNAL_SYSTEM_ERROR.getResponseMessage());
					fields.put(FieldType.PG_TXN_MESSAGE.getName(),
							ErrorType.INTERNAL_SYSTEM_ERROR.getResponseMessage());
					fields.put(FieldType.IS_MERCHANT_HOSTED.getName(), "Y");
					fields.put(FieldType.TXNTYPE.getName(),
							(String) sessionMap.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()));
					transactionResponser.removeInvalidResponseFields(responseMap);
					fields.put(FieldType.IS_MERCHANT_HOSTED.getName(), "Y");
				} else {
					fields.put(FieldType.RESPONSE_MESSAGE.getName(),
							ErrorType.INTERNAL_SYSTEM_ERROR.getResponseMessage());
					fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.INTERNAL_SYSTEM_ERROR.getCode());
					fields.put(FieldType.STATUS.getName(), ErrorType.INTERNAL_SYSTEM_ERROR.getResponseMessage());
					fields.put(FieldType.PG_TXN_MESSAGE.getName(),
							ErrorType.INTERNAL_SYSTEM_ERROR.getResponseMessage());
					fields.put(FieldType.IS_MERCHANT_HOSTED.getName(), "Y");
					fields.put(FieldType.TXNTYPE.getName(),
							(String) sessionMap.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()));
					transactionResponser.removeInvalidResponseFields(responseMap);
					fields.put(FieldType.IS_MERCHANT_HOSTED.getName(), "Y");
				}
			} else if (status.equals(StatusType.REJECTED.getName())) {

				fields.put(FieldType.TXNTYPE.getName(),
						(String) sessionMap.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()));
				transactionResponser.removeInvalidResponseFields(responseMap);
				transactionResponser.addResponseDateTime(responseMap);
				fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.REJECTED.getCode());
				fields.put(FieldType.STATUS.getName(), ErrorType.REJECTED.getResponseMessage());
				fields.put(FieldType.PG_TXN_MESSAGE.getName(),
						ErrorType.REJECTED.getResponseMessage());
				fields.put(FieldType.RESPONSE_MESSAGE.getName(),
						ErrorType.REJECTED.getResponseMessage());
				fields.put(FieldType.IS_MERCHANT_HOSTED.getName(), "Y");
			} else {
				if (StringUtils.isBlank(responseMap.get(FieldType.RESPONSE_CODE.getName()))) {
					fields.put(FieldType.RESPONSE_MESSAGE.getName(),
							ErrorType.INTERNAL_SYSTEM_ERROR.getResponseMessage());
					fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.INTERNAL_SYSTEM_ERROR.getCode());
					fields.put(FieldType.STATUS.getName(), ErrorType.INTERNAL_SYSTEM_ERROR.getResponseMessage());
					fields.put(FieldType.PG_TXN_MESSAGE.getName(),
							ErrorType.INTERNAL_SYSTEM_ERROR.getResponseMessage());
					fields.put(FieldType.IS_MERCHANT_HOSTED.getName(), "Y");
				}
				fields.put(FieldType.TXNTYPE.getName(),
						(String) sessionMap.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()));
				transactionResponser.removeInvalidResponseFields(responseMap);
				fields.put(FieldType.IS_MERCHANT_HOSTED.getName(), "Y");
				
				
			}

			fields.remove(FieldType.PAREQ.getName());
			fields.removeSecureFields();
			fields.put(FieldType.IS_MERCHANT_HOSTED.getName(), "Y");
			createResponse(fields);
		} catch (SystemException systemException) {
			Fields fields = (Fields) sessionMap.get(Constants.FIELDS.getValue());

			String salt = propertiesManager.getSalt(fields.get(FieldType.PAY_ID.getName()));

			if (!StringUtils.isBlank(systemException.getMessage())) {
				fields.put(FieldType.PG_TXN_MESSAGE.getName(), systemException.getMessage());
			}

			User user = userDao.getUserClass(fields.get(FieldType.PAY_ID.getName()));
			String origTxnType = ModeType.getDefaultPurchaseTransaction(user.getModeType()).getName();

			fields.put(FieldType.TXNTYPE.getName(), origTxnType);
			fields.put(FieldType.RESPONSE_CODE.getName(), systemException.getErrorType().getCode());
			fields.put(FieldType.STATUS.getName(), systemException.getErrorType().getResponseMessage());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(),
					systemException.getErrorType().getResponseMessage());
			fields.put(FieldType.PG_TXN_MESSAGE.getName(),
					systemException.getErrorType().getResponseMessage());
			
			
			fields.put(FieldType.TXNTYPE.getName(), origTxnType);
			fields.put(FieldType.TXNTYPE.getName(), origTxnType);
			fields.removeInternalFields();
			fields.put(FieldType.IS_MERCHANT_HOSTED.getName(), "Y");
			createResponse(fields);
			sessionMap.invalidate();
			return Action.ERROR;
		} catch (Exception exception) {
			sessionMap.invalidate();
			logger.error("Unknown error in merchant hosted payment", exception);
			fields.put(FieldType.RESPONSE_MESSAGE.getName(),
					ErrorType.INTERNAL_SYSTEM_ERROR.getResponseMessage());
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.INTERNAL_SYSTEM_ERROR.getCode());
			fields.put(FieldType.STATUS.getName(), ErrorType.INTERNAL_SYSTEM_ERROR.getResponseMessage());
			fields.put(FieldType.PG_TXN_MESSAGE.getName(),
					ErrorType.INTERNAL_SYSTEM_ERROR.getResponseMessage());
			fields.removeInternalFields();
			fields.put(FieldType.IS_MERCHANT_HOSTED.getName(), "Y");
			logger.error("Unknown error in merchant hosted payment", exception);
			createResponse(responseMap);
		}
		return Action.SUCCESS;
	}
	
	private void createResponse(Fields fields) {
		
		HttpSession session = ServletActionContext.getRequest().getSession();
		System.out.println("Inserting Session ID: "+session.getId());
		fields.put(FieldType.TOKEN_ID.getName(), session.getId());
		logger.info("Creating Response : "+fields.getFieldsAsString());
		responseCreator.ResponsePostNBS2s(fields);
		setResponseFields(fields.getFields());
		
		// Save to cookie
//		Arrays.stream(request.getCookies()).filter(cookie -> cookie.getName().equalsIgnoreCase("JSESSIONID"))
//		.findFirst().ifPresent(response::addCookie);
		

	}
	
	private void handleTransactionCharges(Fields fields) throws SystemException, NumberFormatException, ParseException {
		String paymentsRegion = fields.get(FieldType.PAYMENTS_REGION.getName());
		if (StringUtils.isBlank(paymentsRegion)) {
			paymentsRegion = AccountCurrencyRegion.DOMESTIC.toString();
		}

		if (StringUtils.isEmpty(fields.get(FieldType.AMOUNT.getName()))) {
			throw new SystemException(ErrorType.VALIDATION_FAILED,
					FieldType.AMOUNT.getName() + " is a mandatory field.");
		}

		generalValidator.validateField(FieldType.AMOUNT, FieldType.AMOUNT.getName(), fields);

		String amount = Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()),
				fields.get(FieldType.CURRENCY_CODE.getName()));
		 BigDecimal tempAmount = new BigDecimal(amount);
		String payId = fields.get(FieldType.PAY_ID.getName());
		String surchargeFlag = sessionMap.get(FieldType.SURCHARGE_FLAG.getName()).toString();
		String currencyCode = fields.get(FieldType.CURRENCY_CODE.getName());
		String gst = "";
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Date date=new Date();



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



        BigDecimal bigDecimalAmt = new BigDecimal(amount);

//		if (surchargeFlag.equals("Y")) {
			fields.put((FieldType.SURCHARGE_FLAG.getName()), surchargeFlag);
			// Add surcharge amount and total amount on the basis of payment type
			String paymentType = fields.get(FieldType.PAYMENT_TYPE.getName());
			PaymentType paymentTypeS = PaymentType.getInstanceUsingCode(paymentType);
			switch (paymentTypeS) {
			case CREDIT_CARD:
				BigDecimal bigDecimal=dao.getCheckSurcharge("CC",Double.valueOf(amount), fields.get(FieldType.PAY_ID.getName()),date,paymentsRegion,currencyCode);
				  ccTransSurcharge = bigDecimal;
                  surchargeCCAmount = tempAmount.add(bigDecimal);
                  bigDecimalAmt=surchargeCCAmount;

				logger.info("surcharge " + ccTransSurcharge + "\tsurchargeCCAmount" + surchargeCCAmount+"\tbigDecimalAmt "+bigDecimalAmt);
				break;
			case DEBIT_CARD:
				BigDecimal bigDecimal1=dao.getCheckSurcharge("DC",Double.valueOf(amount), fields.get(FieldType.PAY_ID.getName()),date,paymentsRegion,currencyCode);
				 dcTransSurcharge = bigDecimal1;
                 surchargeDCAmount = tempAmount.add(bigDecimal1);
                 bigDecimalAmt=surchargeDCAmount;
                 logger.info("surcharge " + dcTransSurcharge + "\tsurchargeCCAmount" + surchargeDCAmount+"\tbigDecimalAmt "+bigDecimalAmt);


				break;
			case NET_BANKING:
				BigDecimal bigDecimal2=dao.getCheckSurcharge("NB",Double.valueOf(amount), fields.get(FieldType.PAY_ID.getName()),date,paymentsRegion,currencyCode);
				 nbTransSurcharge =bigDecimal2;
                 surchargeNBAmount = tempAmount.add(bigDecimal2);
                 bigDecimalAmt=surchargeNBAmount;
                 logger.info("surcharge " + nbTransSurcharge + "\tsurchargeCCAmount" + surchargeNBAmount+"\tbigDecimalAmt "+bigDecimalAmt);

				break;
			case UPI:
				BigDecimal bigDecimal3=dao.getCheckSurcharge("UP",Double.valueOf(amount), fields.get(FieldType.PAY_ID.getName()),date,paymentsRegion,currencyCode);
				 upTransSurcharge = bigDecimal3;
                 surchargeUPAmount = tempAmount.add(bigDecimal3);
                 bigDecimalAmt=surchargeUPAmount;
                 logger.info("surcharge " + upTransSurcharge + "\tsurchargeCCAmount" + surchargeUPAmount+"\tbigDecimalAmt "+bigDecimalAmt);

				break;
			case WALLET:
				BigDecimal bigDecimal4=dao.getCheckSurcharge("WL",Double.valueOf(amount), fields.get(FieldType.PAY_ID.getName()),date,paymentsRegion,currencyCode);
				wlTransSurcharge = bigDecimal4;
                surchargeWLAmount = tempAmount.add(bigDecimal4);
                bigDecimalAmt=surchargeWLAmount;
                logger.info("surcharge " + wlTransSurcharge + "\tsurchargeCCAmount" + surchargeWLAmount+"\tbigDecimalAmt "+bigDecimalAmt);

				break;
			case QRCODE:
				BigDecimal bigDecimal5=dao.getCheckSurcharge("QR",Double.valueOf(amount), fields.get(FieldType.PAY_ID.getName()),date,paymentsRegion,currencyCode);

				qrTransSurcharge = bigDecimal5;
                surchargeQRAmount = tempAmount.add(bigDecimal5);
                bigDecimalAmt=surchargeQRAmount;
                logger.info("surcharge " + qrTransSurcharge + "\tsurchargeCCAmount" + surchargeQRAmount+"\tbigDecimalAmt "+bigDecimalAmt);

				break;
			default:
				// unsupported payment type
				throw new SystemException(ErrorType.PAYMENT_OPTION_NOT_SUPPORTED, "Unsupported payment type");
			}
			bigDecimalAmt=bigDecimalAmt.multiply(new BigDecimal(100));
			//String.valueOf(bigDecimalAmt).split(".")
			BigInteger bigInteger=bigDecimalAmt.toBigInteger();
			fields.put(FieldType.TOTAL_AMOUNT.getName(),bigInteger.toString());
			 if (ccTransSurcharge.doubleValue()!= 0 || dcTransSurcharge.doubleValue()!= 0 || nbTransSurcharge.doubleValue() != 0 || upTransSurcharge.doubleValue() != 0
						|| wlTransSurcharge.doubleValue() != 0
						|| qrTransSurcharge.doubleValue()!=0) {
					sessionMap.computeIfPresent(FieldType.SURCHARGE_FLAG.getName(), (k, v) ->Constants.Y_FLAG.getValue());

				}else {
					sessionMap.computeIfPresent(FieldType.SURCHARGE_FLAG.getName(), (k, v) ->Constants.N_FLAG.getValue());

				}
//		} else {
//			// TDR MODE
//			fields.put(FieldType.TOTAL_AMOUNT.getName(), fields.get(FieldType.AMOUNT.getName()));
//		}
	}

	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response=response;
		
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request=request;
		
	}
	
	
	public Map<String, String> getResponseFields() {
		return responseFields;
	}

	public void setResponseFields(Map<String, String> responseFields) {
		this.responseFields.put(FieldType.PAY_ID.getName(), responseFields.get(FieldType.PAY_ID.getName()));
		this.responseFields.put(FieldType.ENCDATA.getName(), responseFields.get(FieldType.ENCDATA.getName()));
		this.responseFields.put(FieldType.TOKEN_ID.getName(), responseFields.get(FieldType.TOKEN_ID.getName()));

	}
	
	
	private void addParams(Map<String,String> requestMap,Fields fields)
	{
		fields.put("URL", requestMap.get("url"));
		String paramsStr = requestMap.keySet().stream().filter(key->!key.equalsIgnoreCase("url"))
			      .map(key -> key + "=" + requestMap.get(key))
			      .collect(Collectors.joining("&"));
		fields.put("PARAMS",paramsStr);
		
	}

}
