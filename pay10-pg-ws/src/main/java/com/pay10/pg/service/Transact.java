package com.pay10.pg.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.pay10.TFP.TFPResponseHandler;
import com.pay10.agreepay.AgreepaySaleResponseHandler;
import com.pay10.apbl.APBLSaleResponseHandler;
import com.pay10.atom.AtomSaleResponseHandler;
import com.pay10.axisbank.netbanking.AxisBankNBSaleResponseHandler;
import com.pay10.axisbank.upi.AxisBankUpiSaleResponseHandler;
import com.pay10.billdesk.BilldeskSaleResponseHandler;
import com.pay10.bob.BobSaleResponseHandler;
import com.pay10.camspay.CamsPaySaleResponseHandler;
import com.pay10.canaraNBbank.CanaraNBResponseHandler;
import com.pay10.cashfree.CashfreeSaleResponseHandler;
import com.pay10.citiunionbank.CityUnionBankNBSaleResponseHandler;
import com.pay10.commons.api.Hasher;
import com.pay10.commons.api.TransactionControllerServiceProvider;
import com.pay10.commons.dao.FieldsDao;
import com.pay10.commons.dto.ApiResponse;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.mongo.WalletHistoryRepository;
import com.pay10.commons.user.AccountCurrencyPayout;
import com.pay10.commons.user.MerchantKeySaltDao;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.ConfigurationConstants;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.SaltFactory;
import com.pay10.commons.util.StaticDataProvider;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionManager;
import com.pay10.commons.util.TransactionType;
import com.pay10.commons.util.TxnType;
import com.pay10.cosmos.CosmosUpiResponseHandler;
import com.pay10.cosmos.CosmosUpiintentResponseHandler;
import com.pay10.demo.DemoSaleResponseHandler;
import com.pay10.direcpay.DirecpaySaleResponseHandler;
import com.pay10.easebuzz.EasebuzzSaleResponseHandler;
import com.pay10.federal.FederalInvalidSaleResponseHandler;
import com.pay10.federalNB.FederalBankNBSaleResponseHandler;
import com.pay10.freecharge.FreeChargeSaleResponseHandler;
import com.pay10.fss.FssSaleResponseHandler;
import com.pay10.hdfc.RupaySaleResponseHandler;
import com.pay10.htpay.HtpaySaleResponseHandler;
import com.pay10.icici.netbanking.IciciNBSaleResponseHandler;
import com.pay10.idbi.IdbiSaleResponseHandler;
import com.pay10.idfc.IdfcNBSaleResponseHandler;
import com.pay10.ingenico.IngenicoSaleResponseHandler;
import com.pay10.ipay.IPaySaleResponseProcessor;
import com.pay10.isgpay.ISGPaySaleResponseHandler;
import com.pay10.jammuandkashmir.jammuandKishmirBankNBSaleResponseHandler;
import com.pay10.kotak.KotakCardSaleResponseHandler;
import com.pay10.kotak.KotakOtp;
import com.pay10.kotak.KotakSaleResponseHandler;
import com.pay10.kotak.upi.KotakUpiSaleResponseHandler;
import com.pay10.matchmove.MatchMoveResponseHandler;
import com.pay10.migs.MigsResponseProcessor;
import com.pay10.mobikwik.MobikwikSaleResponseHandler;
import com.pay10.paymentEdge.PaymentEdgeSaleResponseHandler;
import com.pay10.payten.PaytenSaleResponseHandler;
import com.pay10.paytm.PaytmSaleResponseHandler;
import com.pay10.payu.PayuSaleResponseHandler;
import com.pay10.pg.autodebit.AtlSaleResponseHandler;
import com.pay10.pg.cris.DoubleVerificationService;
import com.pay10.phonepe.PhonePeSaleResponseHandler;
import com.pay10.pinelabs.PinelabsSaleResponseHandler;
import com.pay10.quomo.QuomoSaleResponseHandler;
import com.pay10.requestrouter.PayoutRouter;
import com.pay10.requestrouter.RefundRequestRouter;
import com.pay10.requestrouter.RequestRouter;
import com.pay10.sbi.SbiSaleResponseHandler;
import com.pay10.sbi.card.SbiCardIntegrator;
import com.pay10.sbi.card.SbiCardSaleResponseHandler;
import com.pay10.sbi.card.SbiOtpPageAction;
import com.pay10.sbi.card.SbiRupayCardSaleResponseHandler;
import com.pay10.sbi.netbanking.SbiNBSaleResponseHandler;
import com.pay10.shivalikNB.ShivaliknbResponseHandler;
import com.pay10.tmbNB.TMBNBResponseHandler;
import com.pay10.yesbank.netbanking.YesbankNBSaleResponseHandler;

@RestController
public class Transact {
	private static Logger logger = LoggerFactory.getLogger(Transact.class.getName());

	private Gson gson = new Gson();

	@Autowired
	private PostSettlementAutoRefundService postSettlementAutoRefundService;

	@Autowired
	private KotakOtp kotakOtp;

	@Autowired
	private KotakCardSaleResponseHandler kotakCardSaleResponseHandler;

	@Autowired
	private PaymentEdgeSaleResponseHandler paymentEdgeSaleResponseHandler;

	@Autowired
	private RequestRouter router;

	@Autowired
	private PayoutRouter payoutRouter;

	@Autowired
	RefundValidationService refundValidationService;

	@Autowired
	private DemoSaleResponseHandler demoSaleResponseHandler;

	@Autowired
	private IPaySaleResponseProcessor ipayProcessor;

	@Autowired
	private CosmosUpiResponseHandler cosmosUpiResponseHandler;

	@Autowired
	private CosmosUpiintentResponseHandler cosmosUpiintentResponseHandler;

	@Autowired
	private FederalInvalidSaleResponseHandler federalInvalidSaleResponseHandler;

	@Autowired
	private UpiSaleResponseHandler upiSaleResponseHandler;

	@Autowired
	private CityUnionBankNBSaleResponseHandler cityUnionBankNBSaleResponseHandler;

	@Autowired
	private MigsResponseProcessor migsResponseProcessor;

	@Autowired
	private KotakUpiSaleResponseHandler googlePaySaleResponseHandler;

	@Autowired
	private BobSaleResponseHandler bobSaleResponseHandler;

	@Autowired
	private KotakSaleResponseHandler kotakSaleResponseHandler;

	@Autowired
	private AtlSaleResponseHandler atlSaleResponseHandler;

	@Autowired
	private RupaySaleResponseHandler rupaySaleResponseHandler;

	@Autowired
	private StatusEnquiryProcessor statusEnquiryProcessor;

	@Autowired
	private StatusEnquiryProcessorPayout statusEnquiryProcessorPayout;

	@Autowired
	private IdbiSaleResponseHandler idbiSaleResponseHandler;

	@Autowired
	private MatchMoveResponseHandler matchMoveResponseHandler;

	@Autowired
	private DirecpaySaleResponseHandler direcpaySaleResponseHandler;

	@Autowired
	private ISGPaySaleResponseHandler iSGPaySaleResponseHandler;

	@Autowired
	private FssSaleResponseHandler fssSaleResponseHandler;

	@Autowired
	private AxisBankNBSaleResponseHandler axisBankNBSaleResponseHandler;

	@Autowired
	private CanaraNBResponseHandler canaraNBResponseHandler;
	@Autowired
	private TFPResponseHandler tfpResponseHandler;

	@Autowired
	private ShivaliknbResponseHandler shivaliknbResponseHandler;

	@Autowired
	private FederalBankNBSaleResponseHandler federalNBSaleResponseHandler;

	@Autowired
	private AtomSaleResponseHandler atomSaleResponseHandler;

	@Autowired
	private AxisBankUpiSaleResponseHandler axisBankUpiSaleResponseHandler;

	@Autowired
	private APBLSaleResponseHandler apblSaleResponseHandler;

	@Autowired
	private MobikwikSaleResponseHandler mobikwikSaleResponseHandler;

	@Autowired
	private IngenicoSaleResponseHandler ingenicoSaleResponseHandler;

	@Autowired
	private PaytmSaleResponseHandler paytmSaleResponseHandler;

	@Autowired
	private PayuSaleResponseHandler payuSaleResponseHandler;

	@Autowired
	private HtpaySaleResponseHandler htpaySaleResponseHandler;

	@Autowired
	private jammuandKishmirBankNBSaleResponseHandler jammuandkishmirBankNBSaleResponseHandler;

	@Autowired
	private PhonePeSaleResponseHandler phonePeSaleResponseHandler;
	@Autowired
	private SbiOtpPageAction sbiOtpPageAction;

	@Autowired
	private SbiRupayCardSaleResponseHandler sbiRupayCardSaleResponseHandler;
	@Autowired
	private BilldeskSaleResponseHandler billdeskSaleResponseHandler;

	@Autowired
	private FreeChargeSaleResponseHandler freeChargeSaleResponseHandler;

	@Autowired
	private SbiSaleResponseHandler sbiSaleResponseHandler;

	@Autowired
	private SbiCardSaleResponseHandler sbiCardSaleResponseHandler;

	@Autowired
	private SbiNBSaleResponseHandler sbiNBSaleResponseHandler;

	@Autowired
	private IciciNBSaleResponseHandler iciciNBSaleResponseHandler;

	@Autowired
	private CashfreeSaleResponseHandler cashfreeSaleResponseHandler;

	@Autowired
	private EasebuzzSaleResponseHandler easebuzzSaleResponseHandler;

	@Autowired
	private AgreepaySaleResponseHandler agreepaySaleResponseHandler;

	@Autowired
	private YesbankNBSaleResponseHandler yesBankNBSaleResponseHandler;

	@Autowired
	private TransactionControllerServiceProvider transactionControllerServiceProvider;

	@Autowired
	private PinelabsSaleResponseHandler pinelabsSaleResponseHandler;

	@Autowired
	private UPIRestCallProcessor validateVirtualAddressProcessor;

	@Autowired
	private PaymentVpaValidate paymentVpaValidate;

	@Autowired
	private IdfcNBSaleResponseHandler idfcNBSaleResponseHandler;

	@Autowired
	private PayuRefundStatusEnquiry payuRefundStatusEnquiry;

	@Autowired
	private Resellersechudlarcall resellerSchedularCall;

	@Autowired
	private Resellerpayoutschadular resellerpayoutschadular;

	@Autowired
	private CamsPaySaleResponseHandler camsPaySaleResponseHandler;

	@Autowired
	private PaytenSaleResponseHandler paytenSaleResponseHandler;

	@Autowired
	@Qualifier("crisDoubleVerificationService")
	private DoubleVerificationService crisDoubleVerificationService;

	@Autowired
	private MongoInstance mongoInstance;
	@Autowired
	private TMBNBResponseHandler tmbnbResponseHandler;
	@Autowired
	private RefundService refundService;

	@Autowired
	private RefundRequestRouter refundRouter;

	@Autowired
	private WalletHistoryRepository walletHistoryRepository;

	// TODO PAY10-563 : ADD & Autowired ShivaliknbResponseHandler obj & call double
	// varification method for shivalik acq.

	@Autowired
	private FieldsDao fieldsDao;

	@Autowired
	private SbiCardIntegrator sbiCardIntegrator;

	@Autowired
	private MerchantKeySaltDao merchantKeySaltDao;

	@Autowired
	private QuomoSaleResponseHandler quomoSaleResponseHandler;

	@Autowired
	RestCallProcessor restCallProcessor;

	@Autowired
	UserDao userDao;
	
	@Autowired
	private StaticDataProvider staticDataProvider;

	private static final String prefix = "MONGO_DB_";

	public void addHash(Fields fields) throws SystemException {
		String internalFlag = fields.get(FieldType.IS_INTERNAL_REQUEST.getName());
		if (!(StringUtils.isNotEmpty(internalFlag) && internalFlag.equals(Constants.Y_FLAG.getValue()))) {
			fields.put(FieldType.HASH.getName(), Hasher.getHash(fields));
		}

	}

	@RequestMapping(method = RequestMethod.POST, value = "/transact", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> transact(@RequestBody Map<String, String> reqmap) {

		try {
			logger.info("Initial Status Enquiry request received From Merchant : {}", reqmap);
			Fields fields = new Fields(reqmap);
			fields.logAllFields("Initial Raw Request:");
			Map<String, String> responseMap = null;
			String merchantHash = fields.get(FieldType.HASH.getName());
			String respMessage = statusEnquiryRequestValidation(fields);

			if (StringUtils.isNotBlank(respMessage)) {
				fields.put(FieldType.RESPONSE_MESSAGE.getName(), respMessage);
				fields.put(FieldType.STATUS.getName(), StatusType.INVALID.getName());
				fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.VALIDATION_FAILED.getCode());
				fields.put(FieldType.RESPONSE_DATE_TIME.getName(),
						new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
				logger.info("------- transact ----------" + fields.getFieldsAsString());
				fields.remove(FieldType.HASH.getName());
				List<String> respMsg = new ArrayList<String>();
				respMsg.add("PayId should not be empty");
				respMsg.add("PayId should be numeric only");
				respMsg.add("PayId should be 16 digit");
				respMsg.add("Invalid PayId");
				// if(fields.get(FieldType.RESPONSE_MESSAGE.getName()).equalsIgnoreCase("PayId
				// should not be empty")) {
				if (fields.get(FieldType.RESPONSE_MESSAGE.getName()) != null
						&& respMsg.contains(fields.get(FieldType.RESPONSE_MESSAGE.getName()))) {
					return fields.getFields();
				}
				fields.put(FieldType.HASH.getName(), Hasher.getHash(fields));
				return fields.getFields();
			}

			if (fields.get(FieldType.TXNTYPE.getName()).equalsIgnoreCase("PO_STATUS")) {
				Fields responseFields = statusEnquiryProcessorPayout.getMerchantStatus(fields);
				logger.info("-------- responseFields ----------" + responseFields.getFieldsAsString());
				if (responseFields.get(FieldType.RESPONSE_CODE.getName())
						.equalsIgnoreCase(ErrorType.NO_SUCH_TRANSACTION.getCode())) {
					responseFields.put(FieldType.PAY_ID.getName(), fields.get(FieldType.PAY_ID.getName()));
					responseFields.put(FieldType.ORDER_ID.getName(), fields.get(FieldType.ORDER_ID.getName()));
					responseFields.put(FieldType.TXNTYPE.getName(), fields.get(FieldType.TXNTYPE.getName()));
				}
				responseFields.remove(FieldType.HASH.getName());
				logger.info("-------- responseFields1 ----------" + responseFields.getFieldsAsString());
				responseFields.put(FieldType.RESPONSE_DATE_TIME.getName(),
						new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
				logger.info("-------- responseFields2 ----------" + responseFields.getFieldsAsString());
				fieldsDao.removeFieldsByPropertyFile(responseFields,
						Constants.RESPONSE_STATUS_QUERY_PAY_OUT.getValue());
				responseMap = new HashMap<String, String>(responseFields.getFields());
				logger.info("-------- responseFields2 ----------" + responseMap);
				updateStatus(responseMap);
				responseMap.put(FieldType.HASH.getName(), Hasher.getHash(new Fields(responseMap)));
				logger.info("Final Status Enquiry Response Sent To Merchant :: {}", responseMap);
				return responseMap;

			}

			fields.put(FieldType.HASH.getName(), merchantHash);
			fields.removeInternalFields();
			fields.clean();
			fields.removeExtraFields();
			// To put request blob
			String fieldsAsString = fields.getFieldsAsBlobString();
			fields.put(FieldType.INTERNAL_REQUEST_FIELDS.getName(), fieldsAsString);

			// Map<String, String> responseMap = null;

			responseMap = router.route(fields);
			logger.info("Final Status Enquiry Response : {}", responseMap);

			updateStatus(responseMap);

			responseMap.remove(FieldType.HASH.getName());
			Fields fields1 = new Fields(responseMap);
			fieldsDao.removeFieldsByPropertyFile(fields1, Constants.RESPONSE_STATUS_QUERY_PAY_IN.getValue());
			// responseMap.put(FieldType.HASH.getName(), Hasher.getHash(fields1));
			fields1.put(FieldType.HASH.getName(), Hasher.getHash(fields1));
			logger.info("Final Status Enquiry Response Sent To Merchant :: {}", fields1);
			return fields1.getFields();
		} catch (Exception exception) {
			// Ideally this should be a non-reachable code
			logger.error("/transact API call Exception", exception);
			return null;
		}

	}

//	public static void main(String[] args) {
//		Fields fields = new Fields();
//		fields.put("PAY_ID", "1274540414112651");
//		fields.put("ORDER_ID", "Cash1354540502205848");
//		fields.put("TXNTYPE", "STATUS");
//		fields.put("AMOUNT", "100");
//		fields.put("CURRENCY_CODE", "356");
//		fields.put("HASH", "DD12BC3D2EC8659443DEAE10E60CCAF744A619E1292CD44D9F7765F9E2FF51DB");
//		System.out.println("--" + new Transact().statusEnquiryRequestValidation(fields));
//	}

	public String statusEnquiryRequestValidation(Fields fields) {

		if (StringUtils.isEmpty(fields.get(FieldType.PAY_ID.getName()))
				|| fields.get(FieldType.PAY_ID.getName()) == null) {
			return "PayId should not be empty";
		} else if (fields.get(FieldType.PAY_ID.getName()) != null
				&& !(fields.get(FieldType.PAY_ID.getName()).matches("[0-9]+"))) {
			return "PayId should be numeric only";
		} else if (fields.get(FieldType.PAY_ID.getName()) != null
				&& !(fields.get(FieldType.PAY_ID.getName()).length() == 16)) {
			return "PayId should be 16 digit";
		} else if (userDao.checkPayIdExistOrNot(fields.get(FieldType.PAY_ID.getName()))) {
			return "Invalid PayId";
		}

		if (StringUtils.isEmpty(fields.get(FieldType.HASH.getName()))) {
			return "Hash should not be empty";
		} else if (fields.get(FieldType.HASH.getName()) != null
				&& !(fields.get(FieldType.HASH.getName()).length() == 64)) {
			return "Hash should be 64 digit only";
		} else if (fields.get(FieldType.HASH.getName()) != null
				&& !(fields.get(FieldType.HASH.getName()).matches(".*[a-zA-Z]+.*")
						&& fields.get(FieldType.HASH.getName()).matches(".*\\d+.*"))) {
			return "Hash should accept only alphanumeric";
		}

		if (StringUtils.isEmpty(fields.get(FieldType.ORDER_ID.getName()))) {
			return "OrderId should not be empty";
		} else if (fields.get(FieldType.ORDER_ID.getName()) != null
				&& !(fields.get(FieldType.ORDER_ID.getName()).length() >= 1
						&& fields.get(FieldType.ORDER_ID.getName()).length() <= 57)) {
			return "Invalid OrderId";
		}

		if (StringUtils.isEmpty(fields.get(FieldType.TXNTYPE.getName()))) {
			return "Txntype should not be empty";
		} else if (fields.get(FieldType.TXNTYPE.getName()) != null
				&& !(fields.get(FieldType.TXNTYPE.getName()).equals("PO_STATUS")
						|| fields.get(FieldType.TXNTYPE.getName()).equals("STATUS"))) {
			return "Invalid Txntype, Txntype should be PO_STATUS or STATUS";
		}

		if (fields.get(FieldType.CURRENCY_CODE.getName()) != null
				&& StringUtils.isNotEmpty(fields.get(FieldType.CURRENCY_CODE.getName()))) {
			String allCurrency = "356,704,608,458,764";
			List<String> currencyList = Arrays.asList(allCurrency.split(","));
			if (fields.get(FieldType.CURRENCY_CODE.getName()) != null
					&& !currencyList.contains(fields.get(FieldType.CURRENCY_CODE.getName()))) {
				return "Invalid currency_code";
			}
		}

		if (fields.get(FieldType.AMOUNT.getName()) != null
				&& StringUtils.isNotEmpty(fields.get(FieldType.AMOUNT.getName()))) {
			if (!(fields.get(FieldType.AMOUNT.getName()).matches("[0-9]+"))) {
				return "Amount should be numeric only";
			} else if (!(fields.get(FieldType.AMOUNT.getName()).length() >= 3
					&& fields.get(FieldType.AMOUNT.getName()).length() <= 12)) {
				return "Invalid amount";
			}
		}

		else if (validateHash(fields)) {
			return "Invalid hash";
		}
		return null;
	}

	public boolean validateHash(Fields fields) {
		logger.info("HASH CHECK Field " + fields.getFieldsAsString());

		// Hash sent by merchant in request
		String merchantHash = fields.remove(FieldType.HASH.getName());
		logger.info("HASH remove " + merchantHash);

		String calculatedHash = null;
		try {
			calculatedHash = Hasher.getHash(fields);
		} catch (SystemException e) {
			return false;
		}
		logger.info("merchantHash hash:" + merchantHash + ",,,, calculatedHash:" + calculatedHash);
		if (!calculatedHash.equals(merchantHash)) {
			StringBuilder hashMessage = new StringBuilder("Merchant hash =");
			hashMessage.append(merchantHash);
			hashMessage.append(", Calculated Hash=");
			hashMessage.append(calculatedHash);
			logger.info(hashMessage.toString());
			return true;
		}
		return false;
	}

	private void updateStatus(Map<String, String> responseMap) {

		String status = responseMap.get(FieldType.STATUS.getName());
		if (StringUtils.isNotBlank(status) && (status.equalsIgnoreCase(StatusType.CAPTURED.getName())
				|| status.equalsIgnoreCase(StatusType.FAILED.getName()) || status.equalsIgnoreCase("REFUND_INITIATED"))
				|| status.equalsIgnoreCase(StatusType.SETTLED_SETTLE.getName())
				|| status.equalsIgnoreCase(StatusType.SETTLED_RECONCILLED.getName())
				|| status.equalsIgnoreCase(StatusType.FORCE_CAPTURED.getName())) {
		}
//		else if (StringUtils.isNotBlank(status) && (status.equalsIgnoreCase(StatusType.SETTLED_SETTLE.getName())
//				|| status.equalsIgnoreCase(StatusType.SETTLED_RECONCILLED.getName())
//				|| status.equalsIgnoreCase(StatusType.FORCE_CAPTURED.getName()))) {
//			responseMap.put(FieldType.STATUS.getName(), StatusType.CAPTURED.getName());
//			responseMap.put(FieldType.RESPONSE_MESSAGE.getName(), "SUCCESS");
//		} 
		else if (StringUtils.isNotBlank(status)
				&& (status.equalsIgnoreCase(StatusType.DENIED.getName())
						|| status.equalsIgnoreCase(StatusType.PENDING.getName()))
				|| status.equalsIgnoreCase(StatusType.SENT_TO_BANK.getName())
				|| status.equalsIgnoreCase("REQUEST ACCEPTED")) {
			responseMap.put(FieldType.STATUS.getName(), "Request Accepted");
			responseMap.put(FieldType.RESPONSE_MESSAGE.getName(), "Request Accepted");
			responseMap.put(FieldType.RESPONSE_CODE.getName(), "026");
		} else {
			responseMap.put(FieldType.STATUS.getName(), StatusType.FAILED.getName());
		}

		if (null != responseMap && responseMap.containsKey("STATUS")) {
			if (responseMap.get(FieldType.STATUS.getName()).equalsIgnoreCase("Failed")) {
				if (responseMap.containsKey(FieldType.RESPONSE_MESSAGE.getName())
						&& responseMap.get(FieldType.RESPONSE_MESSAGE.getName()).equalsIgnoreCase("SUCCESS")) {
					responseMap.put(FieldType.RESPONSE_MESSAGE.getName(), "Transaction Failed");
				}
			}
		}
	}

	@RequestMapping(method = RequestMethod.POST, value = "/paymentEdgeProcess/response", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> paymentEdgeResponse(@RequestBody Map<String, String> reqmap) {

		try {
			Fields fields = new Fields(reqmap);
			fields.logAllFields("Payten Raw Request:");
			fields.clean();
			Fields internalFields = fields.removeInternalFields();
			// Refine the fields sent by internal application
			fields.removeExtraInternalFields();
			fields.put(FieldType.IS_INTERNAL_REQUEST.getName(), Constants.Y_FLAG.getValue());
			// put Internal fields back
			fields.put(internalFields);
			Map<String, String> responseMap = paymentEdgeSaleResponseHandler.process(fields);
			return responseMap;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return null;
		}

	}

	@RequestMapping(method = RequestMethod.POST, value = "/pay10Process/response", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> PaytenResponse(@RequestBody Map<String, String> reqmap) {

		try {
			Fields fields = new Fields(reqmap);
			fields.logAllFields("Payten Raw Request:");
			fields.clean();
			Fields internalFields = fields.removeInternalFields();
			// Refine the fields sent by internal application
			fields.removeExtraInternalFields();
			fields.put(FieldType.IS_INTERNAL_REQUEST.getName(), Constants.Y_FLAG.getValue());
			// put Internal fields back
			fields.put(internalFields);
			Map<String, String> responseMap = paytenSaleResponseHandler.process(fields);
			return responseMap;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return null;
		}

	}

	@RequestMapping(method = RequestMethod.POST, value = "/payout/transact", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> payoutTransact(@RequestBody Map<String, String> reqmap) {

		try {
			logger.info("Initial Status Enquiry request received From Merchant : {}", reqmap);
			Fields fields = new Fields(reqmap);
			fields.logAllFields("Initial Raw Request:");

			logger.info("1 :" + fields.getFieldsAsString());

			fields.removeInternalFields();
			logger.info("2 :" + fields.getFieldsAsString());
//			fields.clean();
//			logger.info("3 :"+fields.getFieldsAsString());
//			fields.removeExtraFields();
//			logger.info("4 :"+fields.getFieldsAsString());
			// To put request blob
			String fieldsAsString = fields.getFieldsAsBlobString();
			fields.put(FieldType.INTERNAL_REQUEST_FIELDS.getName(), fieldsAsString);

			fields.logAllFields("Initial Refine Request:");
			fields.put(FieldType.INTERNAL_VALIDATE_HASH_YN.getName(), "N");
			fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), "SALE");

			Map<String, String> responseMap = payoutRouter.route(fields);
			responseMap.remove(FieldType.INTERNAL_CUSTOM_MDC.getName());
			logger.info("Final Status Enquiry Response Sent To Merchant : {}", responseMap);
			return responseMap;
		} catch (Exception exception) {
			// Ideally this should be a non-reachable code
			logger.error("/transact API call Exception", exception);
			return null;
		}

	}

	@RequestMapping(method = RequestMethod.POST, value = "/SBIoptResend/request", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> sbiOtpResend(@RequestBody Map<String, String> reqmap) {

		try {
			logger.info("data for otp resend" + new Gson().toJson(reqmap));

			Map<String, String> responseMap = sbiOtpPageAction.otpResend(reqmap); // added by abhi
			logger.info("data for otp resend" + new Gson().toJson(responseMap));

			return responseMap;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return null;
		}

	}

	@RequestMapping(method = RequestMethod.POST, value = "/CityUnionBankProcessor/response", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> cityUnionBankNBResponse(@RequestBody Map<String, String> reqmap) {

		try {
			Fields fields = new Fields(reqmap);
			fields.logAllFields("Raw Request cityUnion Bank Net Banking :");
			fields.clean();
			Fields internalFields = fields.removeInternalFields();
			// Refine the fields sent by internal application
			fields.removeExtraInternalFields();
			fields.put(FieldType.IS_INTERNAL_REQUEST.getName(), Constants.Y_FLAG.getValue());
			// put Internal fields back
			fields.put(internalFields);
			Map<String, String> responseMap = cityUnionBankNBSaleResponseHandler.process(fields);
			return responseMap;
		} catch (Exception exception) {
			logger.error("Exception in city union bank net banking response ", exception);
			return null;
		}

	}

	@RequestMapping(method = RequestMethod.POST, value = "/SBIoptSumbit/request", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> sbiOptSubmit(@RequestBody Map<String, String> reqmap) {

		try {
			logger.info("data for otp submit" + new Gson().toJson(reqmap));

			Map<String, String> responseMap = sbiOtpPageAction.otpSubmit(reqmap); // added by abhi
			logger.info("data for otp submit" + new Gson().toJson(responseMap));

			return responseMap;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return null;
		}

	}

	@RequestMapping(method = RequestMethod.POST, value = "/sbi/rupaypaRqfinal/response", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> SbiCardResponse(@RequestBody Map<String, String> reqmap) {

		try {
			Fields fields = new Fields(reqmap);
			fields.logAllFields("Raw Request SBI RUPAY CARD:");
			fields.clean();
			Fields internalFields = fields.removeInternalFields();
			// Refine the fields sent by internal application
			fields.removeExtraInternalFields();
			fields.put(FieldType.IS_INTERNAL_REQUEST.getName(), Constants.Y_FLAG.getValue());
			// put Internal fields back
			fields.put(internalFields);
			Map<String, String> responseMap = sbiRupayCardSaleResponseHandler.process(fields);
			logger.info("Raw Response SBI RUPAY CARD : {}", responseMap);
			return responseMap;
		} catch (Exception exception) {
			logger.error("/ SBI RUPAY CARD/response Exception", exception);
			return null;
		}

	}

	@RequestMapping(method = RequestMethod.POST, value = "/JammuAndkashmirProcessor/response", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> jammuandKishmirbankNBResponse(@RequestBody Map<String, String> reqmap) {

		try {
			Fields fields = new Fields(reqmap);
			fields.logAllFields("Raw Request jammuandkashmir Bank Net Banking :");
			fields.clean();
			Fields internalFields = fields.removeInternalFields();
			// Refine the fields sent by internal application
			fields.removeExtraInternalFields();
			fields.put(FieldType.IS_INTERNAL_REQUEST.getName(), Constants.Y_FLAG.getValue());
			// put Internal fields back
			fields.put(internalFields);
			Map<String, String> responseMap = jammuandkishmirBankNBSaleResponseHandler.process(fields);
			return responseMap;
		} catch (Exception exception) {
			logger.error("Exception in jammuandkashmir bank net banking response ", exception);
			return null;
		}

	}

	@RequestMapping(method = RequestMethod.POST, value = "/optResend/request", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> kotakOtpResend(@RequestBody Map<String, String> reqmap) {

		try {
			logger.info("data for otp resend" + new Gson().toJson(reqmap));

			Map<String, String> responseMap = kotakOtp.otpResend(reqmap); // added by abhi
			logger.info("data for otp resend" + new Gson().toJson(responseMap));

			return responseMap;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return null;
		}

	}

	@RequestMapping(method = RequestMethod.POST, value = "/process/payment", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> processPayment(@RequestBody Map<String, String> reqmap) {

		try {
			Fields fields = new Fields(reqmap);
			fields.logAllFieldsUsingMasking("/process/payment Raw Request:");
			fields.clean();
			Fields internalFields = fields.removeInternalFields();
			// Refine the fields sent by internal application
			fields.removeExtraInternalFields();
			fields.put(FieldType.IS_INTERNAL_REQUEST.getName(), Constants.Y_FLAG.getValue());
			// put Internal fields back
			fields.put(internalFields);
			fields.logAllFieldsUsingMasking("/process/payment Refine Request:");
			Map<String, String> responseMap = router.route(fields);
			return responseMap;
		} catch (Exception exception) {
			// Ideally this should be a non-reachable code
			logger.error("/process/payment Exception", exception);
			return null;
		}

	}

	@RequestMapping(method = RequestMethod.POST, value = "/canaraNBProcess/response", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> canaraNBResponse(@RequestBody Map<String, String> reqmap) {

		try {
			Fields fields = new Fields(reqmap);
			fields.logAllFields("Raw Request CANARA-NB:");
			fields.clean();
			Fields internalFields = fields.removeInternalFields();
			// Refine the fields sent by internal application
			fields.removeExtraInternalFields();
			fields.put(FieldType.IS_INTERNAL_REQUEST.getName(), Constants.Y_FLAG.getValue());
			// put Internal fields back
			fields.put(internalFields);
			Map<String, String> responseMap = canaraNBResponseHandler.process(fields);
			logger.info("Raw Response CANARA-NB : {}", responseMap);
			return responseMap;
		} catch (Exception exception) {
			logger.error("/canaraNBProcess/response Exception", exception);
			return null;
		}

	}

	@RequestMapping(method = RequestMethod.POST, value = "/TFPProcess/response", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> tfpResponse(@RequestBody Map<String, String> reqmap) {

		try {
			Fields fields = new Fields(reqmap);
			fields.logAllFields("Raw Request TFP:");
			fields.clean();
			Fields internalFields = fields.removeInternalFields();
			// Refine the fields sent by internal application
			fields.removeExtraInternalFields();
			fields.put(FieldType.IS_INTERNAL_REQUEST.getName(), Constants.Y_FLAG.getValue());
			// put Internal fields back
			fields.put(internalFields);
			Map<String, String> responseMap = tfpResponseHandler.process(fields);
			logger.info("Raw Response TFP : {}", responseMap);
			return responseMap;
		} catch (Exception exception) {
			logger.error("/TFPProcess/response Exception", exception);
			return null;
		}

	}

	@RequestMapping(method = RequestMethod.POST, value = "/shivalikNBProcess/response", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> shivalikNBResponse(@RequestBody Map<String, String> reqmap) {

		try {
			Fields fields = new Fields(reqmap);
			fields.logAllFields("Raw Request SHIVALIK-NB:");
			fields.clean();
			Fields internalFields = fields.removeInternalFields();
			// Refine the fields sent by internal application
			fields.removeExtraInternalFields();
			fields.put(FieldType.IS_INTERNAL_REQUEST.getName(), Constants.Y_FLAG.getValue());
			// put Internal fields back
			fields.put(internalFields);
			Map<String, String> responseMap = shivaliknbResponseHandler.process(fields);
			logger.info("Raw Response SHIVALIK-NB : {}", responseMap);
			return responseMap;
		} catch (Exception exception) {
			logger.error("/shivalikNBProcess/response Exception", exception);
			return null;
		}

	}

	@RequestMapping(method = RequestMethod.POST, value = "/htpayProcess/response", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> htpayResponse(@RequestBody Map<String, String> reqmap) {

		try {
			Fields fields = new Fields(reqmap);
			fields.logAllFields("Raw Request Htpay:");
			fields.clean();
			Fields internalFields = fields.removeInternalFields();
			// Refine the fields sent by internal application
			fields.removeExtraInternalFields();
			fields.put(FieldType.IS_INTERNAL_REQUEST.getName(), Constants.Y_FLAG.getValue());
			// put Internal fields back
			fields.put(internalFields);
			Map<String, String> responseMap = htpaySaleResponseHandler.process(fields);
			logger.info("Raw Response Htpay : {}", responseMap);
			return responseMap;
		} catch (Exception exception) {
			logger.error("/shivalikNBProcess/response Exception", exception);
			return null;
		}

	}

	@RequestMapping(method = RequestMethod.POST, value = "/optSumbit/request", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> kotakOptSubmit(@RequestBody Map<String, String> reqmap) {

		try {
			logger.info("data for otp submit" + new Gson().toJson(reqmap));

			Map<String, String> responseMap = kotakOtp.otpSubmit(reqmap); // added by abhi
			logger.info("data for otp submit" + new Gson().toJson(responseMap));

			return responseMap;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return null;
		}

	}

	@RequestMapping(method = RequestMethod.POST, value = "kotakProcess/responseCard", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> kotakResponseCard(@RequestBody Map<String, String> reqmap) {

		try {
			Fields fields = new Fields(reqmap);
			fields.logAllFields("/kotakProcesscards/response Raw Request:");
			fields.clean();
			Fields internalFields = fields.removeInternalFields();
			// Refine the fields sent by internal application
			fields.removeExtraInternalFields();
			fields.put(FieldType.IS_INTERNAL_REQUEST.getName(), Constants.Y_FLAG.getValue());
			// put Internal fields back
			fields.put(internalFields);
			logger.info("Start kotakSaleResponseHandler");
			Map<String, String> responseMap = kotakCardSaleResponseHandler.process(fields);
			logger.info("/kotakProcess cards/response responseMap " + responseMap.toString());
			return responseMap;
		} catch (Exception exception) {
			logger.error("/kotakProcess/response Exception", exception);
			return null;
		}

	}

	@RequestMapping(method = RequestMethod.POST, value = "/tmbNBResponse/response", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> tmbNBResponse(@RequestBody Map<String, String> reqmap) {

		try {
			Fields fields = new Fields(reqmap);
			fields.logAllFields("Raw Request TMBNB:");
			fields.clean();
			Fields internalFields = fields.removeInternalFields();
			// Refine the fields sent by internal application
			fields.removeExtraInternalFields();
			fields.put(FieldType.IS_INTERNAL_REQUEST.getName(), Constants.Y_FLAG.getValue());
			// put Internal fields back
			fields.put(internalFields);
			Map<String, String> responseMap = tmbnbResponseHandler.process(fields);
			return responseMap;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return null;
		}

	}

	@RequestMapping(method = RequestMethod.POST, value = "/idfc/response", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> idfcBankResponse(@RequestBody Map<String, String> reqmap) {

		try {
			Fields fields = new Fields(reqmap);
			fields.logAllFields("Raw Request Phone Pe  : ");
			fields.clean();
			Fields internalFields = fields.removeInternalFields();
			// Refine the fields sent by internal application
			fields.removeExtraInternalFields();
			fields.put(FieldType.IS_INTERNAL_REQUEST.getName(), Constants.Y_FLAG.getValue());
			// put Internal fields back
			fields.put(internalFields);
			Map<String, String> responseMap = idfcNBSaleResponseHandler.process(fields);
			return responseMap;
		} catch (Exception exception) {
			logger.error("Exception in Phone Pe response ", exception);
			return null;
		}

	}

	@RequestMapping(method = RequestMethod.POST, value = "/iPayProcess/response", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> iPayProcessResponse(@RequestBody Map<String, String> reqmap) {

		try {
			Fields fields = new Fields(reqmap);
			fields.logAllFields("Raw Request:");
			fields.clean();
			Fields internalFields = fields.removeInternalFields();
			// Refine the fields sent by internal application
			fields.removeExtraInternalFields();
			fields.put(FieldType.IS_INTERNAL_REQUEST.getName(), Constants.Y_FLAG.getValue());
			// put Internal fields back
			fields.put(internalFields);
			Map<String, String> responseMap = ipayProcessor.process(fields);
			return responseMap;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return null;
		}

	}

	@RequestMapping(method = RequestMethod.POST, value = "/migsProcessor", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> migsProcessor(@RequestBody Map<String, String> reqmap) {

		try {
			Fields fields = new Fields(reqmap);
			fields.logAllFields("Raw Request:");
			Map<String, String> responseMap = migsResponseProcessor.processMigsResponse(fields);
			return responseMap;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return null;
		}
	}

	@RequestMapping(method = RequestMethod.POST, value = "/federalProcess/response", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> federalMPIResponse(@RequestBody Map<String, String> reqmap) {

		try {
			Fields fields = new Fields(reqmap);
			fields.logAllFields("Raw Request:");
			fields.clean();
			Fields internalFields = fields.removeInternalFields();
			// Refine the fields sent by internal application
			fields.removeExtraInternalFields();
			fields.put(FieldType.IS_INTERNAL_REQUEST.getName(), Constants.Y_FLAG.getValue());
			// put Internal fields back
			fields.put(internalFields);
			Map<String, String> responseMap = federalInvalidSaleResponseHandler.process(fields);
			return responseMap;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return null;
		}

	}

	@RequestMapping(method = RequestMethod.POST, value = "/upiProcess/response", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> federalUPIResponse(@RequestBody Map<String, String> reqmap) {

		try {
			Fields fields = new Fields(reqmap);
			fields.logAllFields("Raw Request:");
			fields.clean();
			Fields internalFields = fields.removeInternalFields();
			// Refine the fields sent by internal application
			fields.removeExtraInternalFields();
			fields.put(FieldType.IS_INTERNAL_REQUEST.getName(), Constants.Y_FLAG.getValue());
			// put Internal fields back
			fields.put(internalFields);

			// Add dummy hash
			fields.put(FieldType.HASH.getName(), "2E94A6B4D8C3AFFAA1C2FDC417DCBAA78C63E6D056BD1B4E58426BD20AE5C044");
			Map<String, String> responseMap = upiSaleResponseHandler.process(fields);
			return responseMap;
		} catch (Exception exception) {
			// Ideally this should be a non-reachable code
			logger.error("Exception", exception);
			return null;
		}

	}

	@RequestMapping(method = RequestMethod.POST, value = "/bobProcess/response", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> bobResponse(@RequestBody Map<String, String> reqmap) {

		try {
			Fields fields = new Fields(reqmap);
			fields.logAllFields("Raw Request BOB:");
			fields.clean();
			Fields internalFields = fields.removeInternalFields();
			// Refine the fields sent by internal application
			fields.removeExtraInternalFields();
			fields.put(FieldType.IS_INTERNAL_REQUEST.getName(), Constants.Y_FLAG.getValue());
			// put Internal fields back
			fields.put(internalFields);
			Map<String, String> responseMap = bobSaleResponseHandler.process(fields);
			return responseMap;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return null;
		}

	}

	@RequestMapping(method = RequestMethod.POST, value = "/isgPayProcess/response", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> ispPayResponse(@RequestBody Map<String, String> reqmap) {

		try {
			Fields fields = new Fields(reqmap);
			fields.logAllFields("Raw Request ISG: ");
			fields.clean();
			Fields internalFields = fields.removeInternalFields();
			// Refine the fields sent by internal application
			fields.removeExtraInternalFields();
			fields.put(FieldType.IS_INTERNAL_REQUEST.getName(), Constants.Y_FLAG.getValue());
			// put Internal fields back
			fields.put(internalFields);
			Map<String, String> responseMap = iSGPaySaleResponseHandler.process(fields);
			return responseMap;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return null;
		}

	}

	@RequestMapping(method = RequestMethod.POST, value = "/direcpayProcess/response", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> direcpayResponse(@RequestBody Map<String, String> reqmap) {

		try {
			Fields fields = new Fields(reqmap);
			fields.logAllFields("Raw Request Direcpay:");
			fields.clean();
			Fields internalFields = fields.removeInternalFields();
			// Refine the fields sent by internal application
			fields.removeExtraInternalFields();
			fields.put(FieldType.IS_INTERNAL_REQUEST.getName(), Constants.Y_FLAG.getValue());
			// put Internal fields back
			fields.put(internalFields);
			Map<String, String> responseMap = direcpaySaleResponseHandler.process(fields);
			return responseMap;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return null;
		}

	}

	@RequestMapping(method = RequestMethod.POST, value = "/axisbankNBProcess/response", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> axisbankNBResponse(@RequestBody Map<String, String> reqmap) {

		try {
			Fields fields = new Fields(reqmap);
			String payidee = fields.get(FieldType.ADF10.getName());
			String txnkey = fields.get(FieldType.TXN_KEY.getName());
			String adf11 = fields.get(FieldType.ADF11.getName());
			String adf9 = fields.get(FieldType.ADF9.getName());

			fields.logAllFields("Raw Request Axis Bank Net Banking :");
			fields.clean();
			Fields internalFields = fields.removeInternalFields();
			// Refine the fields sent by internal application
			fields.removeExtraInternalFields();
			fields.put(FieldType.IS_INTERNAL_REQUEST.getName(), Constants.Y_FLAG.getValue());
			// put Internal fields back
			fields.put(internalFields);
			fields.put(FieldType.TXN_KEY.getName(), txnkey);
			fields.put(FieldType.ADF10.getName(), payidee);
			fields.put(FieldType.ADF9.getName(), adf9);
			fields.put(FieldType.ADF11.getName(), adf11);

			Map<String, String> responseMap = axisBankNBSaleResponseHandler.process(fields);
			return responseMap;
		} catch (Exception exception) {
			logger.error("Exception in axis bank net banking response ", exception);
			return null;
		}

	}

	@RequestMapping(method = RequestMethod.POST, value = "/cosmos/UPIresponse", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> cosmosUPIResponse(@RequestBody Map<String, String> reqmap) {

		try {
			Fields fields = new Fields(reqmap);
			fields.logAllFields("Raw Request COSMOS UPI Response:");
			fields.clean();
			Fields internalFields = fields.removeInternalFields();
			// Refine the fields sent by internal application
			fields.removeExtraInternalFields();
			fields.put(FieldType.IS_INTERNAL_REQUEST.getName(), Constants.Y_FLAG.getValue());
			// put Internal fields back
			fields.put(internalFields);
			Map<String, String> responseMap = cosmosUpiResponseHandler.process(fields);
			return responseMap;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return null;
		}

	}

	@RequestMapping(method = RequestMethod.POST, value = "/cosmos/UPIsalehandler", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> cosmosUPIintentResponse(@RequestBody Map<String, String> reqmap) {

		try {
			Fields fields = new Fields(reqmap);
			fields.logAllFields("Raw Request COSMOS UPI Response:");
			fields.clean();
			Fields internalFields = fields.removeInternalFields();
			// Refine the fields sent by internal application
			fields.removeExtraInternalFields();
			fields.put(FieldType.IS_INTERNAL_REQUEST.getName(), Constants.Y_FLAG.getValue());
			// put Internal fields back
			fields.put(internalFields);
			Map<String, String> responseMap = cosmosUpiintentResponseHandler.process(fields);
			return responseMap;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return null;
		}

	}

	@RequestMapping(method = RequestMethod.POST, value = "/atomProcess/response", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> atomResponse(@RequestBody Map<String, String> reqmap) {

		try {
			Fields fields = new Fields(reqmap);
			fields.logAllFields("Raw Request Atom : ");
			fields.clean();
			Fields internalFields = fields.removeInternalFields();
			// Refine the fields sent by internal application
			fields.removeExtraInternalFields();
			fields.put(FieldType.IS_INTERNAL_REQUEST.getName(), Constants.Y_FLAG.getValue());
			// put Internal fields back
			fields.put(internalFields);
			Map<String, String> responseMap = atomSaleResponseHandler.process(fields);
			return responseMap;
		} catch (Exception exception) {
			logger.error("Exception in Atom response ", exception);
			return null;
		}

	}

	@RequestMapping(method = RequestMethod.POST, value = "/axisUpi/response", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> axisUpiResponse(@RequestBody Map<String, String> reqmap) {

		try {
			Fields fields = new Fields(reqmap);
			fields.logAllFields("Raw Request Axis bank UPI  : ");
			fields.clean();
			Fields internalFields = fields.removeInternalFields();
			// Refine the fields sent by internal application
			fields.removeExtraInternalFields();
			fields.put(FieldType.IS_INTERNAL_REQUEST.getName(), Constants.Y_FLAG.getValue());
			// put Internal fields back
			fields.put(internalFields);
			// Add dummy hash
			fields.put(FieldType.HASH.getName(), "2E94A6B4D8C3AFFAA1C2FDC417DCBAA78C63E6D056BD1B4E58426BD20AE5C044");
			Map<String, String> responseMap = axisBankUpiSaleResponseHandler.process(fields);
			return responseMap;
		} catch (Exception exception) {
			logger.error("Exception in Axis bank UPI response ", exception);
			return null;
		}

	}

	@RequestMapping(method = RequestMethod.POST, value = "/apbl/response", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> apblResponse(@RequestBody Map<String, String> reqmap) {

		try {
			Fields fields = new Fields(reqmap);
			fields.logAllFields("Raw Request APBL  : ");
			fields.clean();
			Fields internalFields = fields.removeInternalFields();
			// Refine the fields sent by internal application
			fields.removeExtraInternalFields();
			fields.put(FieldType.IS_INTERNAL_REQUEST.getName(), Constants.Y_FLAG.getValue());
			// put Internal fields back
			fields.put(internalFields);
			Map<String, String> responseMap = apblSaleResponseHandler.process(fields);
			return responseMap;
		} catch (Exception exception) {
			logger.error("Exception in APBL response ", exception);
			return null;
		}

	}

	@RequestMapping(method = RequestMethod.POST, value = "/mobikwik/response", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> mobikwikResponse(@RequestBody Map<String, String> reqmap) {

		try {
			Fields fields = new Fields(reqmap);
			fields.logAllFields("Raw Request Mobikwik  : ");
			fields.clean();
			Fields internalFields = fields.removeInternalFields();
			// Refine the fields sent by internal application
			fields.removeExtraInternalFields();
			fields.put(FieldType.IS_INTERNAL_REQUEST.getName(), Constants.Y_FLAG.getValue());
			// put Internal fields back
			fields.put(internalFields);
			Map<String, String> responseMap = mobikwikSaleResponseHandler.process(fields);
			return responseMap;
		} catch (Exception exception) {
			logger.error("Exception in Mobikwik response ", exception);
			return null;
		}

	}

	@RequestMapping(method = RequestMethod.POST, value = "/ingenico/response", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> ingenicoResponse(@RequestBody Map<String, String> reqmap) {

		try {
			Fields fields = new Fields(reqmap);
			fields.logAllFields("Raw Request Ingenico  : ");
			fields.clean();
			Fields internalFields = fields.removeInternalFields();
			// Refine the fields sent by internal application
			fields.removeExtraInternalFields();
			fields.put(FieldType.IS_INTERNAL_REQUEST.getName(), Constants.Y_FLAG.getValue());
			// put Internal fields back
			fields.put(internalFields);
			Map<String, String> responseMap = ingenicoSaleResponseHandler.process(fields);
			return responseMap;
		} catch (Exception exception) {
			logger.error("Exception in Ingenico response ", exception);
			return null;
		}

	}

	@RequestMapping(method = RequestMethod.POST, value = "/paytm/response", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> paytmResponse(@RequestBody Map<String, String> reqmap) {

		try {
			Fields fields = new Fields(reqmap);
			fields.logAllFields("Raw Request paytm  : ");
			fields.clean();
			Fields internalFields = fields.removeInternalFields();
			// Refine the fields sent by internal application
			fields.removeExtraInternalFields();
			fields.put(FieldType.IS_INTERNAL_REQUEST.getName(), Constants.Y_FLAG.getValue());
			// put Internal fields back
			fields.put(internalFields);
			Map<String, String> responseMap = paytmSaleResponseHandler.process(fields);
			return responseMap;
		} catch (Exception exception) {
			logger.error("Exception in Paytm response ", exception);
			return null;
		}

	}

	@RequestMapping(method = RequestMethod.POST, value = "/payu/response", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> payuResponse(@RequestBody Map<String, String> reqmap) {

		try {
			Fields fields = new Fields(reqmap);
			String salt = reqmap.get(FieldType.ADF3.toString());

			fields.logAllFields("Raw Response payu  : ");
			fields.clean();
			Fields internalFields = fields.removeInternalFields();
			// Refine the fields sent by internal application
			fields.removeExtraInternalFields();
			fields.put(FieldType.IS_INTERNAL_REQUEST.getName(), Constants.Y_FLAG.getValue());
			// put Internal fields back
			fields.put(internalFields);
			fields.put(FieldType.MERCHANT_ID.toString(), reqmap.get(FieldType.MERCHANT_ID.toString()));
			fields.put(FieldType.ADF3.toString(), salt);

			Map<String, String> responseMap = payuSaleResponseHandler.process(fields);
			return responseMap;
		} catch (Exception exception) {
			logger.error("Exception in Payu response ", exception);
			return null;
		}

	}

	@RequestMapping(method = RequestMethod.POST, value = "/phonepe/response", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> phonePeResponse(@RequestBody Map<String, String> reqmap) {

		try {
			Fields fields = new Fields(reqmap);
			fields.logAllFields("Raw Request Phone Pe  : ");
			fields.clean();
			Fields internalFields = fields.removeInternalFields();
			// Refine the fields sent by internal application
			fields.removeExtraInternalFields();
			fields.put(FieldType.IS_INTERNAL_REQUEST.getName(), Constants.Y_FLAG.getValue());
			// put Internal fields back
			fields.put(internalFields);
			Map<String, String> responseMap = phonePeSaleResponseHandler.process(fields);
			return responseMap;
		} catch (Exception exception) {
			logger.error("Exception in Phone Pe response ", exception);
			return null;
		}

	}

	@RequestMapping(method = RequestMethod.POST, value = "/idbiProcess/response", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> idbiProcess(@RequestBody Map<String, String> reqmap) {

		try {
			Fields fields = new Fields(reqmap);
			fields.logAllFields("Raw Request Idbi:");
			fields.clean();
			Fields internalFields = fields.removeInternalFields();
			// Refine the fields sent by internal application
			fields.removeExtraInternalFields();
			fields.put(FieldType.IS_INTERNAL_REQUEST.getName(), Constants.Y_FLAG.getValue());
			// put Internal fields back
			fields.put(internalFields);
			Map<String, String> responseMap = idbiSaleResponseHandler.process(fields);
			return responseMap;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return null;
		}

	}

	@RequestMapping(method = RequestMethod.POST, value = "/matchMoveProcess/response", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> matchMoveProcess(@RequestBody Map<String, String> reqmap) {

		try {
			Fields fields = new Fields(reqmap);
			fields.logAllFields("Raw Request:");
			fields.clean();
			Fields internalFields = fields.removeInternalFields();
			// Refine the fields sent by internal application
			fields.removeExtraInternalFields();
			fields.put(FieldType.IS_INTERNAL_REQUEST.getName(), Constants.Y_FLAG.getValue());
			// put Internal fields back
			fields.put(internalFields);
			Map<String, String> responseMap = matchMoveResponseHandler.process(fields);
			return responseMap;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return null;
		}

	}

	@RequestMapping(method = RequestMethod.POST, value = "/googlePayProcess/response", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> googlePayResponse(@RequestBody Map<String, String> reqmap) {

		try {
			Fields fields = new Fields(reqmap);
			fields.logAllFields("Raw Request:");
			fields.clean();
			Fields internalFields = fields.removeInternalFields();
			// Refine the fields sent by internal application
			fields.removeExtraInternalFields();
			fields.put(FieldType.IS_INTERNAL_REQUEST.getName(), Constants.Y_FLAG.getValue());
			// put Internal fields back
			fields.put(internalFields);
			Map<String, String> responseMap = googlePaySaleResponseHandler.process(fields);
			return responseMap;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return null;
		}

	}

	@RequestMapping(method = RequestMethod.POST, value = "/atlProcess/response", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> atlResponse(@RequestBody Map<String, String> reqmap) {

		try {
			Fields fields = new Fields(reqmap);
			fields.logAllFields("Raw Request:");
			fields.clean();
			Fields internalFields = fields.removeInternalFields();
			// Refine the fields sent by internal application
			fields.removeExtraInternalFields();
			fields.put(FieldType.IS_INTERNAL_REQUEST.getName(), Constants.Y_FLAG.getValue());
			// put Internal fields back
			fields.put(internalFields);
			Map<String, String> responseMap = atlSaleResponseHandler.process(fields);
			return responseMap;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return null;
		}

	}

	@RequestMapping(method = RequestMethod.POST, value = "/sbiProcess/response", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> sbiResponse(@RequestBody Map<String, String> reqmap) {

		try {
			Fields fields = new Fields(reqmap);
			fields.logAllFieldsUsingMasking("Raw Request SBI:");
			fields.clean();
			Fields internalFields = fields.removeInternalFields();
			// Refine the fields sent by internal application
			fields.removeExtraInternalFields();
			fields.put(FieldType.IS_INTERNAL_REQUEST.getName(), Constants.Y_FLAG.getValue());
			// put Internal fields back
			fields.put(internalFields);
			Map<String, String> responseMap;
			if ("CC".equalsIgnoreCase(fields.get(FieldType.PAYMENT_TYPE.getName()))
					|| "DC".equalsIgnoreCase(fields.get(FieldType.PAYMENT_TYPE.getName()))) {
				responseMap = sbiCardSaleResponseHandler.process(fields);
			} else if (fields.get(FieldType.ACQUIRER_TYPE.getName()).equalsIgnoreCase("SBINB")) {
				logger.info("SBINB Sale Handler Request Received : " + fields.get(FieldType.PG_REF_NUM.getName()));
				responseMap = sbiNBSaleResponseHandler.process(fields);
			} else {
				logger.info("SBI Sale Handler Request Received : " + fields.get(FieldType.PG_REF_NUM.getName()));
				responseMap = sbiSaleResponseHandler.process(fields);
			}
			logger.info("Raw Response SBI : {}", responseMap);
			return responseMap;
		} catch (Exception exception) {
			logger.error("/sbiProcess/response Exception", exception);
			return null;
		}

	}

	@RequestMapping(method = RequestMethod.POST, value = "/sbiNBProcess/response", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> sbiNBResponse(@RequestBody Map<String, String> reqmap) {

		try {
			Fields fields = new Fields(reqmap);
			fields.logAllFields("Raw Request SBI-NB:");
			fields.clean();
			Fields internalFields = fields.removeInternalFields();
			// Refine the fields sent by internal application
			fields.removeExtraInternalFields();
			fields.put(FieldType.IS_INTERNAL_REQUEST.getName(), Constants.Y_FLAG.getValue());
			// put Internal fields back
			fields.put(internalFields);
			Map<String, String> responseMap = sbiNBSaleResponseHandler.process(fields);
			logger.info("Raw Response SBI-NB : {}", responseMap);
			return responseMap;
		} catch (Exception exception) {
			logger.error("/sbiNBResponse/response Exception", exception);
			return null;
		}

	}

	@RequestMapping(method = RequestMethod.POST, value = "/internalRefund", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> internalRefund(@RequestBody Map<String, String> reqmap) {

		try {
			Fields fields = new Fields(reqmap);
			fields.logAllFields("internalRefund Raw Request:");
			fields.removeInternalFields();
			fields.clean();
			fields.removeExtraFields();
			refundValidationService.validateunselltedamound(fields);
			// To put request blob
			String fieldsAsString = fields.getFieldsAsBlobString();
			fields.put(FieldType.INTERNAL_REQUEST_FIELDS.getName(), fieldsAsString);
			fields.put(FieldType.INTERNAL_VALIDATE_HASH_YN.getName(), "N");
			if (fields.contains(FieldType.UDF6.getName())) {
				if (!fields.get(FieldType.UDF6.getName()).isEmpty()) {
					fields.put(FieldType.UDF6.getName(), Constants.Y_FLAG.getValue());
				}
			}
			fields.logAllFields("internalRefund Refine Request:");
			Map<String, String> responseMap = router.route(fields);
			responseMap.remove(FieldType.INTERNAL_CUSTOM_MDC.getName());
			return responseMap;
		} catch (Exception exception) {
			// Ideally this should be a non-reachable code
			logger.error("internalRefund Exception", exception);
			return null;
		}

	}

	// added by sonu for Merchant refund api's
	@RequestMapping(method = RequestMethod.POST, value = "/externalRefund", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> externalRefund(@RequestBody Map<String, String> reqmap) {

		Map<String, String> finalResponseMap = new HashMap<String, String>();
		try {
			logger.info("Request Received For External Refund For PayId : " + reqmap.toString());
			Map<String, String> plainResponseMap = transactionControllerServiceProvider.decrypt(reqmap.get("PAY_ID"),
					reqmap.get("ENCDATA"));
			logger.info("Plain Request received, ResponseMap : " + plainResponseMap.toString());

			String decryptedString = plainResponseMap.get(FieldType.ENCDATA.getName());
			String[] fieldArray = decryptedString.split("~");
			Map<String, String> requestMap = new HashMap<String, String>();

			Fields fields = null;
			for (String entry : fieldArray) {
				String[] namValuePair = entry.split("=", 2);
				if (namValuePair.length == 2) {
					requestMap.put(namValuePair[0], namValuePair[1]);
				} else {
					requestMap.put(namValuePair[0], "");
				}
			}
			fields = new Fields(requestMap);
			logger.info("Request fields : " + fields.getFieldsAsBlobString());

			// Fields fields = new Fields(reqmap);
			fields.logAllFields("Raw Request:");
			fields.removeInternalFields();
			fields.clean();
			fields.removeExtraFields();
			// To put request blob
			String fieldsAsString = fields.getFieldsAsBlobString();
			fields.put(FieldType.INTERNAL_REQUEST_FIELDS.getName(), fieldsAsString);
			fields.put(FieldType.INTERNAL_VALIDATE_HASH_YN.getName(), "N");
			if (fields.contains(FieldType.UDF6.getName())) {
				if (!fields.get(FieldType.UDF6.getName()).isEmpty()) {
					fields.put(FieldType.UDF6.getName(), Constants.Y_FLAG.getValue());
				}
			}
			logger.info("Inserting PG_REF For Order Id from DB : " + fields.get(FieldType.ORDER_ID.getName()));
			Map<String, String> pgRefMap = fieldsDao.getCaptureTxn(fields.get(FieldType.ORDER_ID.getName()));
			if (pgRefMap.containsKey(FieldType.PG_REF_NUM.getName())) {
				fields.put(FieldType.PG_REF_NUM.getName(), pgRefMap.get(FieldType.PG_REF_NUM.getName()));
			}
			fields.logAllFields("Refine Request:");
			Map<String, String> responseMap = router.route(fields);
			responseMap.remove(FieldType.INTERNAL_CUSTOM_MDC.getName());
			logger.info("Final Response received fron acquirer, responseMap : " + responseMap.toString());

			responseMap.remove("CUST_PHONE");
			responseMap.remove("HASH");
			responseMap.remove("ORIG_TXN_ID");
			responseMap.remove("CARD_MASK");
			responseMap.remove("CURRENCY_CODE");
			responseMap.remove("CUST_EMAIL");
			responseMap.remove("AUTH_CODE");
			responseMap.remove("TXN_ID");
			responseMap.remove("RRN");
			responseMap.remove("CARD_HOLDER_NAME");

			logger.info("Final Response post to merchant, responseMap : " + responseMap.toString());

			StringBuilder allFields = new StringBuilder();
			for (Entry<String, String> entry : responseMap.entrySet()) {
				allFields.append(ConfigurationConstants.FIELD_SEPARATOR.getValue());
				allFields.append(entry.getKey());
				allFields.append(ConfigurationConstants.FIELD_EQUATOR.getValue());
				allFields.append(entry.getValue());
			}
			allFields.deleteCharAt(0);
			logger.info("Merchant refund response in plain text : " + allFields.toString());
			String encryptedString = transactionControllerServiceProvider
					.encrypt(responseMap.get("PAY_ID"), allFields.toString()).get(FieldType.ENCDATA.getName());

			finalResponseMap.put("PAY_ID", responseMap.get("PAY_ID"));
			finalResponseMap.put("ENCDATA", encryptedString);
			return finalResponseMap;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			finalResponseMap.put("PAY_ID", reqmap.get("PAY_ID"));
			String encryptedString = null;
			try {
				encryptedString = transactionControllerServiceProvider
						.encrypt(reqmap.get("PAY_ID"), "Something goes wrong!").get(FieldType.ENCDATA.getName());
			} catch (SystemException e) {
				e.printStackTrace();
			}
			finalResponseMap.put("ENCDATA", encryptedString);
			return finalResponseMap;
		}
	}

	@RequestMapping(method = RequestMethod.POST, value = "/fssProcess/response", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> fssResponse(@RequestBody Map<String, String> reqmap) {

		try {
			Fields fields = new Fields(reqmap);
			fields.logAllFields("Raw Request FSS:");
			fields.clean();
			Fields internalFields = fields.removeInternalFields();
			// Refine the fields sent by internal application
			fields.removeExtraInternalFields();
			fields.put(FieldType.IS_INTERNAL_REQUEST.getName(), Constants.Y_FLAG.getValue());
			// put Internal fields back
			fields.put(internalFields);
			Map<String, String> responseMap = fssSaleResponseHandler.process(fields);
			return responseMap;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return null;
		}

	}

	@RequestMapping(method = RequestMethod.POST, value = "/kotakProcess/response", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> kotakResponse(@RequestBody Map<String, String> reqmap) {

		try {
			Fields fields = new Fields(reqmap);
			fields.logAllFields("/kotakProcess/response Raw Request:");
			fields.clean();
			Fields internalFields = fields.removeInternalFields();
			// Refine the fields sent by internal application
			fields.removeExtraInternalFields();
			fields.put(FieldType.IS_INTERNAL_REQUEST.getName(), Constants.Y_FLAG.getValue());
			// put Internal fields back
			fields.put(internalFields);
			logger.info("Start kotakSaleResponseHandler");
			Map<String, String> responseMap = kotakSaleResponseHandler.process(fields);
			logger.info("/kotakProcess/response responseMap " + responseMap.toString());
			return responseMap;
		} catch (Exception exception) {
			logger.error("/kotakProcess/response Exception", exception);
			return null;
		}

	}

	@RequestMapping(method = RequestMethod.POST, value = "/rupayProcess/response", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> rupayResponse(@RequestBody Map<String, String> reqmap) {

		try {
			Fields fields = new Fields(reqmap);
			fields.logAllFields("Raw Request:");
			fields.clean();
			Fields internalFields = fields.removeInternalFields();
			// Refine the fields sent by internal application
			fields.removeExtraInternalFields();
			fields.put(FieldType.IS_INTERNAL_REQUEST.getName(), Constants.Y_FLAG.getValue());
			// put Internal fields back
			fields.put(internalFields);
			Map<String, String> responseMap = rupaySaleResponseHandler.process(fields);
			return responseMap;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return null;
		}

	}

	@RequestMapping(method = RequestMethod.POST, value = "/enquiry/process", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> enquiryResponse(@RequestBody Map<String, String> reqmap) {

		try {
			logger.info("/enquiry/process request : {}", reqmap);
			Fields fields = new Fields(reqmap);
			fields.logAllFields("Txn status enquiry Raw Request:");
			fields.clean();
			Fields internalFields = fields.removeInternalFields();
			// Refine the fields sent by internal application
			fields.removeExtraInternalFields();
			fields.put(FieldType.IS_INTERNAL_REQUEST.getName(), Constants.Y_FLAG.getValue());
			// put Internal fields back
			fields.put(internalFields);
			Map<String, String> responseMap = statusEnquiryProcessor.process(fields);
			logger.info("/enquiry/process response : {}", responseMap);
			return responseMap;
		} catch (Exception exception) {
			logger.error("/enquiry/process Exception", exception);
			return null;
		}

	}

	@RequestMapping(method = RequestMethod.POST, value = "/payuRefundStatusenquiry/process", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> payuRefundStatusenquiry(@RequestBody Map<String, String> reqmap) {

		try {
			logger.info("/enquiry/process request : {}", reqmap);
			Fields fields = new Fields(reqmap);
			fields.logAllFields("Txn status enquiry Raw Request:");
			fields.clean();
			Fields internalFields = fields.removeInternalFields();
			// Refine the fields sent by internal application
			fields.removeExtraInternalFields();
			fields.put(FieldType.IS_INTERNAL_REQUEST.getName(), Constants.Y_FLAG.getValue());
			// put Internal fields back
			fields.put(internalFields);
			Map<String, String> responseMap = payuRefundStatusEnquiry.process(fields);
			logger.info("/enquiry/process response : {}", responseMap);
			return responseMap;
		} catch (Exception exception) {
			logger.error("/enquiry/process Exception", exception);
			return null;
		}

	}

	@RequestMapping(method = RequestMethod.POST, value = "schuldar/process", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody String Resellerupdate(@RequestBody String reqmap) {
		logger.info("reqMap...={}", reqmap);

		try {

			String responseMap = resellerSchedularCall.process();
			return responseMap;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return null;
		}

	}

	@RequestMapping(method = RequestMethod.POST, value = "schuldar/payout", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody String Resellerpayout(@RequestBody String reqmap) {

		try {

			String responseMap = resellerpayoutschadular.process();
			return responseMap;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return null;
		}

	}

	@RequestMapping(method = RequestMethod.POST, value = "schuldar/payoutmonth", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody String Resellerpayoutmonth(@RequestBody String reqmap) {

		try {

			String responseMap = resellerpayoutschadular.payoutQuaterly();
			return responseMap;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return null;
		}

	}

	@RequestMapping(method = RequestMethod.POST, value = "/getStatus", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> getStatus(@RequestBody Map<String, String> reqmap) {

		try {

			Fields fields = new Fields(reqmap);
			fields.logAllFields("Raw Request for getStatus: ");
			fields.clean();
			Fields internalFields = fields.removeInternalFields();
			fields.removeExtraInternalFields();
			fields.put(FieldType.IS_INTERNAL_REQUEST.getName(), Constants.Y_FLAG.getValue());
			fields.put(internalFields);
			Map<String, String> responseMap = statusEnquiryProcessor.getStatus(fields);
			return responseMap;

		} catch (Exception exception) {
			logger.error("Exception in status enquiry , getStatus process for status enquiry ", exception);
			return null;
		}

	}

	@RequestMapping(method = RequestMethod.POST, value = "/enquiry/payoutProcess", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> enquiryPayoutResponse(@RequestBody Map<String, String> reqmap) {

		try {
			logger.info("/enquiry/payoutProcess request : {}", reqmap);
			Fields fields = new Fields(reqmap);
			fields.logAllFields("Txn status enquiry Raw Request:");
			fields.clean();
			Fields internalFields = fields.removeInternalFields();
			// Refine the fields sent by internal application
			fields.removeExtraInternalFields();
			fields.put(FieldType.IS_INTERNAL_REQUEST.getName(), Constants.Y_FLAG.getValue());
			// put Internal fields back
			fields.put(internalFields);
			Map<String, String> responseMap = statusEnquiryProcessorPayout.process(fields);
			logger.info("/enquiry/process response : {}", responseMap);
			return responseMap;
		} catch (Exception exception) {
			logger.error("/enquiry/process Exception", exception);
			return null;
		}

	}

	@PostMapping(value = "/irctc/doubleVerification")
	public String crisDoubleVerification(@RequestParam String encdata) {
		try {
			return crisDoubleVerificationService.verifyTransaction(encdata);
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return null;
		}

	}

	@PostMapping(value = "/postSettlementAutoRefund")
	public void postSettlementAutoRefund(@RequestBody Map<String, String> reqmap) {
		try {
			logger.info("/postSettlementAutoRefund : {}", reqmap);
			if (reqmap.containsKey(FieldType.PG_REF_NUM.getName())
					&& !StringUtils.isEmpty(reqmap.get(FieldType.PG_REF_NUM.getName()))) {
				postSettlementAutoRefundService.initiateAutoRefund(reqmap.get(FieldType.PG_REF_NUM.getName()));
			} else {
				logger.error("Invalid PG_REF_NUM : " + FieldType.PG_REF_NUM.getName());
			}
		} catch (Exception e) {
			logger.error("Failed to inititate post settlement auto refund.");
			logger.error(e.getMessage());
		}
	}

	@RequestMapping(method = RequestMethod.POST, value = "/freeChargeProcess/response", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> freeChargeResponse(@RequestBody Map<String, String> reqmap) {

		try {
			Fields fields = new Fields(reqmap);
			fields.logAllFields("Raw Request:");
			fields.clean();
			Fields internalFields = fields.removeInternalFields();
			// Refine the fields sent by internal application
			fields.removeExtraInternalFields();
			fields.put(FieldType.IS_INTERNAL_REQUEST.getName(), Constants.Y_FLAG.getValue());
			// put Internal fields back
			fields.put(internalFields);
			Map<String, String> responseMap = freeChargeSaleResponseHandler.process(fields);
			return responseMap;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return null;
		}

	}

	@RequestMapping(method = RequestMethod.POST, value = "/billdeskProcess/response", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> BilldeskResponse(@RequestBody Map<String, String> reqmap) {

		try {
			Fields fields = new Fields(reqmap);
			fields.logAllFields("Billdesk Raw Request:");
			fields.clean();
			Fields internalFields = fields.removeInternalFields();
			// Refine the fields sent by internal application
			fields.removeExtraInternalFields();
			fields.put(FieldType.IS_INTERNAL_REQUEST.getName(), Constants.Y_FLAG.getValue());
			// put Internal fields back
			fields.put(internalFields);
			Map<String, String> responseMap = billdeskSaleResponseHandler.process(fields);
			return responseMap;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return null;
		}

	}

	@RequestMapping(method = RequestMethod.POST, value = "/iciciNBProcess/response", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> IciciNBResponse(@RequestBody Map<String, String> reqmap) {

		try {
			Fields fields = new Fields(reqmap);
			fields.logAllFields("Billdesk Raw Request:");
			fields.clean();
			Fields internalFields = fields.removeInternalFields();
			// Refine the fields sent by internal application
			fields.removeExtraInternalFields();
			fields.put(FieldType.IS_INTERNAL_REQUEST.getName(), Constants.Y_FLAG.getValue());
			// put Internal fields back
			fields.put(internalFields);
			Map<String, String> responseMap = iciciNBSaleResponseHandler.process(fields);
			return responseMap;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return null;
		}

	}

	@RequestMapping(method = RequestMethod.POST, value = "/transact/getBilldeskUpiStatus", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> getBilldeskUpiStatus(@RequestBody Map<String, String> reqmap) {

		try {

			Fields fields = new Fields(reqmap);
			fields.logAllFields("Raw Request for getStatus: ");
			fields.clean();
			Fields internalFields = fields.removeInternalFields();
			fields.removeExtraInternalFields();
			fields.put(FieldType.IS_INTERNAL_REQUEST.getName(), Constants.Y_FLAG.getValue());
			fields.put(internalFields);
			Map<String, String> responseMap = statusEnquiryProcessor.getBilldeskUpiStatus(fields);
			return responseMap;

		} catch (Exception exception) {
			logger.error("Exception in status enquiry , getStatus process for status enquiry ", exception);
			return null;
		}

	}

	@RequestMapping(method = RequestMethod.POST, value = "/transact/getFreechargeUpiStatus", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> getFreeChargeUpiStatus(@RequestBody Map<String, String> reqmap) {

		try {

			Fields fields = new Fields(reqmap);
			fields.logAllFields("Raw Request for Freecharge getStatus: ");
			fields.clean();
			Fields internalFields = fields.removeInternalFields();
			fields.removeExtraInternalFields();
			fields.put(FieldType.IS_INTERNAL_REQUEST.getName(), Constants.Y_FLAG.getValue());
			fields.put(internalFields);
			Map<String, String> responseMap = statusEnquiryProcessor.getFreeChargeUpiStatus(fields);
			return responseMap;

		} catch (Exception exception) {
			logger.error("Exception in status enquiry , Freecharge getStatus process for status enquiry ", exception);
			return null;
		}

	}

	@RequestMapping(method = RequestMethod.POST, value = "/cashfreeProcess/response", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> CashfreeResponse(@RequestBody Map<String, String> reqmap) {

		try {
			logger.info("/cashfreeProcess/response request: {}", reqmap);
			Fields fields = new Fields(reqmap);
			// fields.logAllFields("Cashfree Raw Request:");
			fields.logAllFieldsUsingMasking("Cashfree Raw Request:");
			fields.clean();
			Fields internalFields = fields.removeInternalFields();
			// Refine the fields sent by internal application
			fields.removeExtraInternalFields();
			fields.put(FieldType.IS_INTERNAL_REQUEST.getName(), Constants.Y_FLAG.getValue());
			// put Internal fields back
			fields.put(internalFields);
			Map<String, String> responseMap = cashfreeSaleResponseHandler.process(fields);
			logger.info("/cashfreeProcess/response response: {} ", responseMap);
			return responseMap;
		} catch (Exception exception) {
			logger.error("/easebuzzProcess/response Exception", exception);
			return null;
		}
	}

	@RequestMapping(method = RequestMethod.POST, value = "/easebuzzProcess/response", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> EasebuzzResponse(@RequestBody Map<String, String> reqmap) {

		try {
			Fields fields = new Fields(reqmap);
			fields.logAllFields("Easebuzz Raw Request:");
			fields.clean();
			Fields internalFields = fields.removeInternalFields();
			// Refine the fields sent by internal application
			fields.removeExtraInternalFields();
			fields.put(FieldType.IS_INTERNAL_REQUEST.getName(), Constants.Y_FLAG.getValue());
			// put Internal fields back
			fields.put(internalFields);
			Map<String, String> responseMap = easebuzzSaleResponseHandler.process(fields);
			logger.info("/easebuzzProcess/response Response : {}", responseMap);
			return responseMap;
		} catch (Exception exception) {
			logger.error("/easebuzzProcess/response Exception", exception);
			return null;
		}
	}

	@RequestMapping(method = RequestMethod.POST, value = "/agreepayProcess/response", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> AgreepayResponse(@RequestBody Map<String, String> reqmap) {

		try {
			Fields fields = new Fields(reqmap);
			fields.logAllFields("agreepay Raw Request:");
			fields.clean();
			Fields internalFields = fields.removeInternalFields();
			// Refine the fields sent by internal application
			fields.removeExtraInternalFields();
			fields.put(FieldType.IS_INTERNAL_REQUEST.getName(), Constants.Y_FLAG.getValue());
			// put Internal fields back
			fields.put(internalFields);
			Map<String, String> responseMap = agreepaySaleResponseHandler.process(fields);
			return responseMap;
		} catch (Exception exception) {
			logger.error(" /agreepayProcess/response Exception", exception);
			return null;
		}
	}

	@RequestMapping(method = RequestMethod.POST, value = "/pinelabsProcess/response", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> PinelabsResponse(@RequestBody Map<String, String> reqmap) {

		try {
			Fields fields = new Fields(reqmap);
			String adf1 = fields.get(FieldType.ADF1.getName());
			fields.logAllFields("Pinelabs Raw Request:");
			fields.clean();
			Fields internalFields = fields.removeInternalFields();
			// Refine the fields sent by internal application
			fields.removeExtraInternalFields();
			fields.put(FieldType.IS_INTERNAL_REQUEST.getName(), Constants.Y_FLAG.getValue());
			// put Internal fields back
			fields.put(internalFields);
			fields.put(FieldType.ADF1.getName(), adf1);
			Map<String, String> responseMap = pinelabsSaleResponseHandler.process(fields);
			return responseMap;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return null;
		}
	}

	@RequestMapping(method = RequestMethod.POST, value = "/federalNBProcess/response", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> federalNBResponse(@RequestBody Map<String, String> reqmap) {

		try {
			Fields fields = new Fields(reqmap);
			fields.logAllFields("Raw Request Federal Bank Net Banking :");
			fields.clean();
			Fields internalFields = fields.removeInternalFields();
			// Refine the fields sent by internal application
			fields.removeExtraInternalFields();
			fields.put(FieldType.IS_INTERNAL_REQUEST.getName(), Constants.Y_FLAG.getValue());
			// put Internal fields back
			fields.put(internalFields);
			Map<String, String> responseMap = federalNBSaleResponseHandler.process(fields);
			return responseMap;
		} catch (Exception exception) {
			logger.error("/federalNBProcess/response Exception in Federal bank net banking response ", exception);
			return null;
		}

	}

	@RequestMapping(method = RequestMethod.POST, value = "/yesbankNBProcess/response", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> yesbankNBResponse(@RequestBody Map<String, String> reqmap) {

		try {
			Fields fields = new Fields(reqmap);
			String txnDt = fields.get(FieldType.ADF10.getName());
			fields.logAllFields("Raw Request YES Bank Net Banking :");
			fields.clean();
			Fields internalFields = fields.removeInternalFields();
			// Refine the fields sent by internal application
			fields.removeExtraInternalFields();
			fields.put(FieldType.IS_INTERNAL_REQUEST.getName(), Constants.Y_FLAG.getValue());
			// put Internal fields back
			fields.put(internalFields);
			fields.put(FieldType.ADF10.getName(), txnDt);
			Map<String, String> responseMap = yesBankNBSaleResponseHandler.process(fields);
			return responseMap;
		} catch (Exception exception) {
			logger.error("/yesbankNBProcess/response Exception in yes bank net banking response ", exception);
			return null;
		}

	}

	@RequestMapping(method = RequestMethod.POST, value = "/camspay/response", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> camsPayResponse(@RequestBody Map<String, String> reqmap) {

		try {
			Fields fields = new Fields(reqmap);
			fields.logAllFields("camsPayResponse:: Raw response=");
			fields.clean();
			Fields internalFields = fields.removeInternalFields();
			// Refine the fields sent by internal application
			fields.removeExtraInternalFields();
			fields.put(FieldType.IS_INTERNAL_REQUEST.getName(), Constants.Y_FLAG.getValue());
			// put Internal fields back
			fields.put(internalFields);
			fields.put(FieldType.MERCHANT_ID.toString(), reqmap.get(FieldType.MERCHANT_ID.toString()));
			Map<String, String> responseMap = camsPaySaleResponseHandler.process(fields);
			return responseMap;
		} catch (Exception exception) {
			logger.error("camsPayResponse:: failed.", exception);
			return null;
		}

	}

	// validate virtual Address for pay10 API S2S
	@RequestMapping(method = RequestMethod.POST, value = "/upi/validatevpa", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> ValidateVpaResponse(@RequestBody Map<String, String> reqmap,
			HttpServletRequest request) {

		try {
			logger.info("/upi/validateVpa request : {}", reqmap);
			Fields fields = new Fields(reqmap);
			// Fields fields = new Fields(reqmap);
			fields.logAllFields("Transct Action validateVpa :Raw Request:");
			Map<String, String> resp = validateVirtualAddressProcessor.process(fields,
					Constants.UPI_S2S_VPA_VALID.getValue(), request);

			Map<String, String> responseMap = new HashMap<String, String>();
			responseMap.put(FieldType.PAY_ID.getName(), resp.get(FieldType.PAY_ID.getName()));
			responseMap.put(FieldType.ENCDATA.getName(), resp.get(FieldType.ENCDATA.getName()));
			logger.info("/upi/validateVpa response : {}", responseMap);
			return responseMap;
		} catch (Exception exception) {
			logger.error("/upi/validateVpa Exception", exception);
			return null;
		}

	}

	@RequestMapping(method = RequestMethod.POST, value = "/process/paymentVpaValidate", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> paymentVpaValidate(@RequestBody Map<String, String> reqmap,
			HttpServletRequest request) {

		try {
			logger.info("/upi/validateVpa request : {}", reqmap);
			Fields fields = new Fields(reqmap);
			// Fields fields = new Fields(reqmap);
			fields.logAllFields("Transct Action paymentVpaValidate :Raw Request:");
			Map<String, String> resp = paymentVpaValidate.process(fields);
			fields.logAllFields("Transct Action paymentVpaValidate :Raw response:");

			return resp;
		} catch (Exception exception) {
			logger.error("/upi/validateVpa Exception", exception);
			return null;
		}

	}

	// Collect pay for pay10 API S2S
	@RequestMapping(method = RequestMethod.POST, value = "/upi/collectpay", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> CollectPayResponse(@RequestBody Map<String, String> reqmap,
			HttpServletRequest request1) {

		try {
			logger.info("/upi/CollectPay request : {}", reqmap);
			Fields fields = new Fields(reqmap);
			// Fields fields = new Fields(reqmap);
			fields.logAllFields("Transct Action CollectPay :Raw Request:");
			Map<String, String> resp = validateVirtualAddressProcessor.process(fields,
					Constants.UPI_S2S_COLLECT_PAY.getValue(), request1);

			Map<String, String> responseMap = new HashMap<String, String>();
			responseMap.put(FieldType.PAY_ID.getName(), resp.get(FieldType.PAY_ID.getName()));
			responseMap.put(FieldType.ENCDATA.getName(), resp.get(FieldType.ENCDATA.getName()));
			logger.info("/upi/CollectPay response : {}", responseMap);
			return responseMap;
		} catch (Exception exception) {
			logger.error("/upi/validateVpa Exception", exception);
			return null;
		}

	}

	// added by sonu for Merchant refund api's
	@RequestMapping(method = RequestMethod.POST, value = "/merchantRefund", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> merchantRefund(@RequestBody Map<String, String> reqmap) {

		Map<String, String> finalResponseMap = new HashMap<String, String>();
		try {
			logger.info("Request Received For Merchant Refund For PayId : " + reqmap.toString());
			Map<String, String> plainResponseMap = transactionControllerServiceProvider.decrypt(reqmap.get("PAY_ID"),
					reqmap.get("ENCDATA"));
			logger.info("Plain Request received from merchant, ResponseMap : " + plainResponseMap.toString());

			String decryptedString = plainResponseMap.get(FieldType.ENCDATA.getName());
			String[] fieldArray = decryptedString.split("~");
			Map<String, String> requestMap = new HashMap<String, String>();

			Fields fields = null;
			for (String entry : fieldArray) {
				String[] namValuePair = entry.split("=", 2);
				if (namValuePair.length == 2) {
					requestMap.put(namValuePair[0], namValuePair[1]);
				} else {
					requestMap.put(namValuePair[0], "");
				}
			}
			fields = new Fields(requestMap);
			logger.info("Request fields : " + fields.getFieldsAsBlobString());

			// Fields fields = new Fields(reqmap);
			fields.logAllFields("Raw Request:");
			fields.removeInternalFields();
			fields.clean();
			fields.removeExtraFields();
			// To put request blob
			String fieldsAsString = fields.getFieldsAsBlobString();
			fields.put(FieldType.INTERNAL_REQUEST_FIELDS.getName(), fieldsAsString);
			fields.put(FieldType.INTERNAL_VALIDATE_HASH_YN.getName(), "N");
			if (fields.contains(FieldType.UDF6.getName())) {
				if (!fields.get(FieldType.UDF6.getName()).isEmpty()) {
					fields.put(FieldType.UDF6.getName(), Constants.Y_FLAG.getValue());
				}
			}
			fields.logAllFields("Refine Request:");
			Map<String, String> responseMap = router.route(fields);
			responseMap.remove(FieldType.INTERNAL_CUSTOM_MDC.getName());
			logger.info("Final Response received fron acquirer, responseMap : " + responseMap.toString());

			responseMap.remove("CUST_PHONE");
			responseMap.remove("HASH");
			responseMap.remove("ORIG_TXN_ID");
			responseMap.remove("CARD_MASK");
			responseMap.remove("CURRENCY_CODE");
			responseMap.remove("CUST_EMAIL");
			responseMap.remove("AUTH_CODE");
			responseMap.remove("TXN_ID");
			responseMap.remove("RRN");
			responseMap.remove("CARD_HOLDER_NAME");

			logger.info("Final Response post to merchant, responseMap : " + responseMap.toString());

			StringBuilder allFields = new StringBuilder();
			for (Entry<String, String> entry : responseMap.entrySet()) {
				allFields.append(ConfigurationConstants.FIELD_SEPARATOR.getValue());
				allFields.append(entry.getKey());
				allFields.append(ConfigurationConstants.FIELD_EQUATOR.getValue());
				allFields.append(entry.getValue());
			}
			allFields.deleteCharAt(0);
			logger.info("Merchant refund response in plain text : " + allFields.toString());
			String encryptedString = transactionControllerServiceProvider
					.encrypt(responseMap.get("PAY_ID"), allFields.toString()).get(FieldType.ENCDATA.getName());

			finalResponseMap.put("PAY_ID", responseMap.get("PAY_ID"));
			finalResponseMap.put("ENCDATA", encryptedString);
			return finalResponseMap;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			finalResponseMap.put("PAY_ID", reqmap.get("PAY_ID"));
			String encryptedString = null;
			try {
				encryptedString = transactionControllerServiceProvider
						.encrypt(reqmap.get("PAY_ID"), "Something goes wrong!").get(FieldType.ENCDATA.getName());
			} catch (SystemException e) {
				e.printStackTrace();
			}
			finalResponseMap.put("ENCDATA", encryptedString);
			return finalResponseMap;
		}
	}

	@RequestMapping(method = RequestMethod.POST, value = "/api/v1/transaction/getStatus", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> cashfreeTransactionStatus(@RequestBody Map<String, String> reqmap) {

		try {
			logger.info("Cashfree Txn Status Request :" + reqmap);
			Map<String, String> responseMap = cashfreeSaleResponseHandler.txnStatus(reqmap);
			return responseMap;
		} catch (Exception exception) {
			logger.error("/api/v1/transaction/getStatus Exception in cashfreeTransactionStatus response ", exception);
			return null;
		}

	}

	@GetMapping("/api/v1/cashfreeRefund")
	public String CashfreeRefundScheduler() {
		try {
			logger.info("------------ Start CashfreeRefundScheduler -----------------------" + new Date());
			String responseMap = cashfreeSaleResponseHandler.refundScheduler();
			logger.info("------------ Completed CashfreeRefundScheduler -----------------------" + new Date());
			return responseMap;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return null;
		}
	}

	// For AxisbankNB Status testing purpose
	@RequestMapping(method = RequestMethod.POST, value = "/axisbank/nb/status", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody String testAxisBankNBStatusEnquiry(@RequestBody Map<String, String> reqmap) {

		try {
			logger.info("AxisBankNB Status Enquiry Txn Status Request :" + reqmap);
			String xmlResponse = null;
			String hostUrl = reqmap.get("hostUrl");
			String encdata = reqmap.get("encdata");
			String payeeid = reqmap.get("payeeid");
			String enccat = reqmap.get("enccat");
			String mercat = reqmap.get("mercat");

			logger.info("hostUrl : " + hostUrl + ", payeeid : " + payeeid + ", enccat : " + enccat + ", mercat : "
					+ mercat);
			logger.info("encdata : " + encdata);

			URL url = new URL(hostUrl);
			Map<String, Object> params = new LinkedHashMap<>();
			params.put("encdata", encdata);
			params.put("payeeid", payeeid);
			params.put("enccat", enccat);
			params.put("mercat", mercat);

			StringBuilder postData = new StringBuilder();
			for (Map.Entry<String, Object> param : params.entrySet()) {
				if (postData.length() != 0)
					postData.append('&');
				postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
				postData.append('=');
				postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
			}
			logger.info("postData : " + postData.toString());
			byte[] postDataBytes = postData.toString().getBytes("UTF-8");

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
			conn.setRequestProperty("payloadtype", "json");
			conn.setDoOutput(true);
			conn.getOutputStream().write(postDataBytes);

			Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

			StringBuilder sb = new StringBuilder();
			for (int c; (c = in.read()) >= 0;)
				sb.append((char) c);
			xmlResponse = sb.toString();
			logger.info("AxisBankNB Status Enquiry Txn Status xmlResponse :" + xmlResponse);
			return xmlResponse;
		} catch (Exception exception) {
			logger.error("/axisbank/nb/status Exception in testAxisBankNBStatusEnquiry response ", exception);
			return null;
		}

	}

	@RequestMapping(method = RequestMethod.POST, value = "/onlinerefund")
	public @ResponseBody String onlineRefund(@RequestParam(name = "encdata") String irctc) {
		logger.info("Inside Transact class in onlineRefund() Request : " + irctc);

		Map<String, String> reqmap = new LinkedHashMap<>();

		AES256Utility utility = new AES256Utility();
		try {

			Map<String, String> response = new LinkedHashMap<>();
			String decrypt = utility.decrypt(irctc);
			logger.info("Inside Transact class in onlineRefund() decrypt Request : " + decrypt);
			decrypt = decrypt.replaceAll("\\|",
					Character.toString((char) 34) + Character.toString((char) 44) + Character.toString((char) 34));
			decrypt = decrypt.replaceAll("\\=",
					Character.toString((char) 34) + Character.toString((char) 58) + Character.toString((char) 34));
			decrypt = "\"" + decrypt + "\"";
			decrypt = "{" + decrypt + "}";

			OnlineRefunfIRCTC onlineRefunfIRCTC = new Gson().fromJson(decrypt, OnlineRefunfIRCTC.class);

			String postForEncrypt = FieldType.ORDER_ID.getName() + "=" + onlineRefunfIRCTC.getReservationId() + "~"
					+ FieldType.REFUND_FLAG.getName() + "=" + onlineRefunfIRCTC.getRefundType() + "~"
					+ FieldType.AMOUNT.getName() + "="
					+ String.valueOf((int) (onlineRefunfIRCTC.getRefundAmount() * 100)) + "~"
					+ FieldType.PG_REF_NUM.getName() + "=" + onlineRefunfIRCTC.getBankTxnId() + "~"
					+ FieldType.REFUND_ORDER_ID.getName() + "=" + onlineRefunfIRCTC.getRefundId() + "~"
					+ FieldType.CURRENCY_CODE.getName() + "=" + "356" + "~" + FieldType.TXNTYPE.getName() + "="
					+ TxnType.REFUND.getCode() + "~" + FieldType.PAY_ID.getName() + "="
					+ onlineRefunfIRCTC.getTerminalID();

			logger.info("Inside Transact class in onlineRefund() data for encrypt in our type : " + postForEncrypt);

			Map<String, String> enc = transactionControllerServiceProvider.encrypt(onlineRefunfIRCTC.getTerminalID(),
					postForEncrypt);

			// String encrypt = utility.encrypt(postForEncrypt);
			// logger.info("Inside Transact class in onlineRefund() after data encrypt in
			// our type : " + encrypt);
			reqmap.put(FieldType.PAY_ID.getName(), onlineRefunfIRCTC.getTerminalID());
			reqmap.put(FieldType.ENCDATA.getName(), enc.get("ENCDATA"));

			logger.info("Request Received For Merchant Refund For PayId : " + reqmap.toString());
			Map<String, String> plainResponseMap = transactionControllerServiceProvider.decrypt(reqmap.get("PAY_ID"),
					reqmap.get("ENCDATA"));
			logger.info("Plain Request received from merchant, ResponseMap : " + plainResponseMap.toString());

			String decryptedString = plainResponseMap.get(FieldType.ENCDATA.getName());
			String[] fieldArray = decryptedString.split("~");
			Map<String, String> requestMap = new HashMap<String, String>();

			Fields fields = null;
			for (String entry : fieldArray) {
				String[] namValuePair = entry.split("=", 2);
				if (namValuePair.length == 2) {
					requestMap.put(namValuePair[0], namValuePair[1]);
				} else {
					requestMap.put(namValuePair[0], "");
				}
			}
			fields = new Fields(requestMap);
			logger.info("Request fields : " + fields.getFieldsAsBlobString());

			// Fields fields = new Fields(reqmap);
			fields.logAllFields("Raw Request:");
			fields.removeInternalFields();
			fields.clean();
			fields.removeExtraFields();
			// To put request blob
			String fieldsAsString = fields.getFieldsAsBlobString();
			fields.put(FieldType.INTERNAL_REQUEST_FIELDS.getName(), fieldsAsString);
			fields.put(FieldType.INTERNAL_VALIDATE_HASH_YN.getName(), "N");
			if (fields.contains(FieldType.UDF6.getName())) {
				if (!fields.get(FieldType.UDF6.getName()).isEmpty()) {
					fields.put(FieldType.UDF6.getName(), Constants.Y_FLAG.getValue());
				}
			}
			fields.logAllFields("Refine Request:");
			Map<String, String> responseMap = router.route(fields);
			responseMap.remove(FieldType.INTERNAL_CUSTOM_MDC.getName());
			logger.info("Final Response received fron acquirer, responseMap : " + responseMap.toString());

			responseMap.remove("CUST_PHONE");
			responseMap.remove("HASH");
			responseMap.remove("ORIG_TXN_ID");
			responseMap.remove("CARD_MASK");
			responseMap.remove("CURRENCY_CODE");
			responseMap.remove("CUST_EMAIL");
			responseMap.remove("AUTH_CODE");
			responseMap.remove("TXN_ID");
			responseMap.remove("RRN");
			responseMap.remove("CARD_HOLDER_NAME");

			logger.info("Final Response post to merchant, responseMap : " + responseMap.toString());

			response.put("terminalID", responseMap.get("PAY_ID"));
			response.put("terminalpwd", onlineRefunfIRCTC.getTerminalpwd());
			response.put("reservationId", responseMap.get("ORDER_ID"));
			response.put("bankTxnId", responseMap.get("PG_REF_NUM"));
			response.put("refundAmount",
					String.format("%.2f", Double.parseDouble(responseMap.get("TOTAL_AMOUNT")) / 100));
			response.put("txnAmount", "" + onlineRefunfIRCTC.getTxnAmount());
			response.put("refundDate", responseMap.get("RESPONSE_DATE_TIME"));

			response.put("status", responseMap.get("RESPONSE_CODE").equalsIgnoreCase("026") ? "Pending"
					: responseMap.get("RESPONSE_CODE").equalsIgnoreCase("000") ? "Success" : "Failed");

			if (responseMap.get("PG_TXN_MESSAGE") != null || !responseMap.get("PG_TXN_MESSAGE").isEmpty()) {
				if (responseMap.get("PG_TXN_MESSAGE").equalsIgnoreCase("Invalid PAY_ID")) {
					response.put("statusDesc", "Invalid terminalID");
				} else {
					response.put("statusDesc", responseMap.get("PG_TXN_MESSAGE"));
				}
			} else {
				response.put("statusDesc", responseMap.get("RESPONSE_MESSAGE"));
			}
			String res = response.toString();
			res = res.replaceAll("\\{", "");
			res = res.replaceAll("\\}", "");
			res = res.replaceAll("\\, ", "|");
			String checksum = Utils.getSHA(res);
			res = res + "|" + "checkSum=" + checksum;
			logger.info(
					"Inside Transact class in onlineRefund() Actual Response to Merchant without encryption: " + res);
			res = utility.encrypt(res);
			logger.info("Inside Transact class in onlineRefund() Actual Response to Merchant: " + res);
			return res;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			Map<String, String> response = new LinkedHashMap<>();
			response.put("terminalID", reqmap.get("PAY_ID"));
			response.put("status", "Something Went Wrong");
			String res = response.toString();
			res = res.replaceAll("\\{", "");
			res = res.replaceAll("\\}", "");
			res = res.replaceAll("\\, ", "|");
			String checksum = Utils.getSHA(res);
			res = res + "|" + "checkSum=" + checksum;
			logger.info(
					"Inside Transact class in onlineRefund() Actual Response to Merchant without encryption: " + res);
			res = utility.encrypt(res);
			logger.info("Inside Transact class in onlineRefund() Actual Response to Merchant: " + res);
			return res;
		}
	}

	public Map<String, String> doubleVerficationAndStatusEnuiryForIRCTC(Map<String, String> reqmap) {
		Map<String, String> responseMap = new HashMap<>();
		try {
			logger.info("Inside Transact class in commonDoubleVerficationAndStatusEnuiry() Map Parameters : " + reqmap);
			Fields fields = new Fields(reqmap);
			fields.logAllFields("Initial Raw Request:");
			fields.removeInternalFields();
			fields.clean();
			fields.removeExtraFields();
			// To put request blob
			String fieldsAsString = fields.getFieldsAsBlobString();
			fields.put(FieldType.INTERNAL_REQUEST_FIELDS.getName(), fieldsAsString);
			/*
			 * if (fields.get(FieldType.TXNTYPE.getName()).equals(TransactionType.REFUND.
			 * getName())) { String refundFlag =
			 * fields.get(FieldType.REFUND_FLAG.getName()); if ((refundFlag == null)) {
			 * fields.put(FieldType.REFUND_FLAG.getName(), "C"); } } else
			 */ if (fields.get(FieldType.TXNTYPE.getName()).equals(TransactionType.STATUS.getName())) {
				// JSONObject req = new JSONObject(reqmap);
				Gson gson = new Gson();
				String request = gson.toJson(reqmap);
				logger.info("commonDoubleVerficationAndStatusEnuiry Request:" + request);
			}
			fields.logAllFields("Initial Refine Request:");
			responseMap = router.route(fields);
			if (fields.get(FieldType.TXNTYPE.getName()).equals(TransactionType.STATUS.getName())) {
				// JSONObject res = new JSONObject(responseMap);
				Gson gson = new Gson();
				String request = gson.toJson(responseMap);
				logger.info("Double verification Response:" + request);
			}
			responseMap.remove(FieldType.INTERNAL_CUSTOM_MDC.getName());

			logger.info("Inside Transact class in commonDoubleVerficationAndStatusEnuiry(), Response We are getting : "
					+ responseMap);

		} catch (Exception e) {
			logger.error("Inside Transact class in commonDoubleVerficationAndStatusEnuiry() Exception Occur : ", e);
		}
		return responseMap;
	}

	@RequestMapping(method = RequestMethod.POST, value = "/refunddoubleverification")
	public @ResponseBody String refundDoubleVarification(@RequestParam(name = "encdata") String irctc) {
		try {
			logger.info("Inside Transact class in refundDoubleVarification() Request : " + irctc);
			Map<String, String> reqmap = new LinkedHashMap<>();
			AES256Utility utility = new AES256Utility();
			String dec = utility.decrypt(irctc);
			logger.info("Inside Transact class in refundDoubleVarification() Decrypt Request : " + dec);
			dec = dec.replaceAll("\\|",
					Character.toString((char) 34) + Character.toString((char) 44) + Character.toString((char) 34));
			dec = dec.replaceAll("\\=",
					Character.toString((char) 34) + Character.toString((char) 58) + Character.toString((char) 34));
			dec = "\"" + dec + "\"";
			dec = "{" + dec + "}";

			OnlineRefunfIRCTC onlineRefunfIRCTC = new Gson().fromJson(dec, OnlineRefunfIRCTC.class);

			reqmap.put("AMOUNT", "" + (int) (onlineRefunfIRCTC.getTxnAmount() * 100));
			reqmap.put("CURRENCY_CODE", "356");
			reqmap.put("ORDER_ID", onlineRefunfIRCTC.getReservationId());

			MongoDatabase dbIns = mongoInstance.getDB();
			System.out.println("DB NAME" + dbIns.getName());
			MongoCollection<Document> coll = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));
			BasicDBObject whereQuery = new BasicDBObject();
			whereQuery.put(FieldType.ORDER_ID.getName(), onlineRefunfIRCTC.getReservationId());
			System.out.println("ORDER_ID : " + onlineRefunfIRCTC.getReservationId());
			MongoCursor<Document> cursor = coll.find(whereQuery).iterator();
//					MongoCursor<Document>cursor=coll.aggregate(Arrays.asList(new Document("$match", new Document().put(FieldType.ORDER_ID.getName(), onlineRefunfIRCTC.getReservationId())))).iterator();
			if (cursor.hasNext()) {
				Document document = (Document) cursor.next();
				System.out.println(document);
				System.out.println(document.toJson());
				onlineRefunfIRCTC.setMerchantCode(document.getString(FieldType.PAY_ID.toString()));
			}
			reqmap.put("PAY_ID", onlineRefunfIRCTC.getMerchantCode());
			reqmap.put("TXNTYPE", "STATUS");

			String hash = reqmap.toString();
			hash = hash.replaceAll(" ", "");
			hash = hash.replaceAll("\\,", "~");
			hash = hash.replaceAll("\\{", "");
			hash = hash.replaceAll("\\}", "");
			User user = new User();
			user.setPayId(onlineRefunfIRCTC.getMerchantCode());
			String salt = SaltFactory.getSaltProperty(user);
			logger.info("Inside Transact class in refundDoubleVarification() Salt : " + salt);

			hash = hash + salt;
			logger.info("Inside Transact class in refundDoubleVarification() parameter of Generating Hash : " + hash);
			String hashGenrated = Utils.getSHA(hash);
			logger.info("Inside Transact class in refundDoubleVarification() hash generated : "
					+ hashGenrated.toUpperCase());
			reqmap.put("HASH", hashGenrated.toUpperCase());

			Map<String, String> responseMap = doubleVerficationAndStatusEnuiryForIRCTC(reqmap);

			Map<String, String> response = new LinkedHashMap<>();
			response.put("refundId", onlineRefunfIRCTC.getRefundId());
			response.put("reservationId", responseMap.get("ORDER_ID"));
			response.put("txnAmount", String.format("%.2f", Double.parseDouble(responseMap.get("AMOUNT")) / 100));
			response.put("bankTxnId", responseMap.get("PG_REF_NUM"));
			response.put("bankRefundRefId", responseMap.get("TXN_ID"));
			response.put("status", responseMap.get("RESPONSE_CODE").equalsIgnoreCase("026") ? "Pending"
					: responseMap.get("RESPONSE_CODE").equalsIgnoreCase("000") ? "Success" : "Failed");

			if (responseMap.get("PG_TXN_MESSAGE") != null || !responseMap.get("PG_TXN_MESSAGE").isEmpty()) {
				if (responseMap.get("PG_TXN_MESSAGE").equalsIgnoreCase("Invalid PAY_ID")) {
					response.put("statusDesc", "Invalid MerchantCode");
				} else {
					response.put("statusDesc", responseMap.get("PG_TXN_MESSAGE"));
				}
			} else {
				response.put("statusDesc", responseMap.get("RESPONSE_MESSAGE"));
			}
			String res = response.toString();
			res = res.replaceAll("\\{", "");
			res = res.replaceAll("\\}", "");
			res = res.replaceAll("\\, ", "|");
			String checksum = Utils.getSHA(res);
			res = res + "|" + "checkSum=" + checksum;
			logger.info(
					"Inside Transact class in refundDoubleVarification() Actual Response to Merchant without encryption: "
							+ res);
			res = utility.encrypt(res);
			logger.info("Inside Transact class in refundDoubleVarification() Actual Response to Merchant: " + res);
			return res;
		} catch (Exception exception) {
			// Ideally this should be a non-reachable code
			logger.error("/refundDoubleVarification() API call Exception", exception);
			return null;
		}
	}

	@RequestMapping(method = RequestMethod.POST, value = "/refundstatusenuiry")
	public @ResponseBody String refundStatusEnuiry(@RequestParam(name = "encdata") String irctc) {
		try {
			logger.info("Inside Transact class in refundStatusEnuiry() Request : " + irctc);
			Map<String, String> reqmap = new LinkedHashMap<>();
			AES256Utility utility = new AES256Utility();
			String dec = utility.decrypt(irctc);
			logger.info("Inside Transact class in refundStatusEnuiry() Decrypt Request : " + dec);
			dec = dec.replaceAll("\\|",
					Character.toString((char) 34) + Character.toString((char) 44) + Character.toString((char) 34));
			dec = dec.replaceAll("\\=",
					Character.toString((char) 34) + Character.toString((char) 58) + Character.toString((char) 34));
			dec = "\"" + dec + "\"";
			dec = "{" + dec + "}";

			OnlineRefunfIRCTC onlineRefunfIRCTC = new Gson().fromJson(dec, OnlineRefunfIRCTC.class);

			reqmap.put("AMOUNT", "" + (int) (onlineRefunfIRCTC.getTxnAmount() * 100));
			reqmap.put("CURRENCY_CODE", "356");
			reqmap.put("ORDER_ID", onlineRefunfIRCTC.getReservationId());
			reqmap.put("PAY_ID", onlineRefunfIRCTC.getTerminalID());
			reqmap.put("TXNTYPE", "STATUS");

			String hash = reqmap.toString();
			hash = hash.replaceAll(" ", "");
			hash = hash.replaceAll("\\,", "~");
			hash = hash.replaceAll("\\{", "");
			hash = hash.replaceAll("\\}", "");
			User user = new User();
			user.setPayId(onlineRefunfIRCTC.getTerminalID());
			String salt = SaltFactory.getSaltProperty(user);
			logger.info("Inside Transact class in refundStatusEnuiry() Salt : " + salt);

			hash = hash + salt;
			logger.info("Inside Transact class in refundStatusEnuiry() parameter of Generating Hash : " + hash);
			String hashGenrated = Utils.getSHA(hash);
			logger.info("Inside Transact class in refundStatusEnuiry() hash generated : " + hashGenrated.toUpperCase());
			reqmap.put("HASH", hashGenrated.toUpperCase());

			Map<String, String> responseMap = doubleVerficationAndStatusEnuiryForIRCTC(reqmap);

			Map<String, String> response = new LinkedHashMap<>();
			response.put("refundId", onlineRefunfIRCTC.getRefundId());
			response.put("reservationId", responseMap.get("ORDER_ID"));
			response.put("txnAmount", String.format("%.2f", Double.parseDouble(responseMap.get("AMOUNT")) / 100));
			response.put("bankTxnId", responseMap.get("PG_REF_NUM"));
			response.put("bankRefundRefId", responseMap.get("TXN_ID"));
			response.put("RefundDate", "");
			response.put("CustRefundDate", "");

			response.put("status", responseMap.get("RESPONSE_CODE").equalsIgnoreCase("026") ? "Pending"
					: responseMap.get("RESPONSE_CODE").equalsIgnoreCase("000") ? "Success" : "Failed");

			if (responseMap.get("PG_TXN_MESSAGE") != null || !responseMap.get("PG_TXN_MESSAGE").isEmpty()) {
				if (responseMap.get("PG_TXN_MESSAGE").equalsIgnoreCase("Invalid PAY_ID")) {
					response.put("statusDesc", "Invalid MerchantCode");
				} else {
					response.put("statusDesc", responseMap.get("PG_TXN_MESSAGE"));
				}
			} else {
				response.put("statusDesc", responseMap.get("RESPONSE_MESSAGE"));
			}
			String res = response.toString();
			res = res.replaceAll("\\{", "");
			res = res.replaceAll("\\}", "");
			res = res.replaceAll("\\, ", "|");
			String checksum = Utils.getSHA(res);
			res = res + "|" + "checkSum=" + checksum;
			logger.info("Inside Transact class in refundStatusEnuiry() Actual Response to Merchant without encryption: "
					+ res);
			res = utility.encrypt(res);
			logger.info("Inside Transact class in refundStatusEnuiry() Actual Response to Merchant: " + res);
			return res;
		} catch (Exception exception) {
			// Ideally this should be a non-reachable code
			logger.error("/refundStatusEnuiry() API call Exception", exception);
			return null;
		}
	}

	@RequestMapping(method = RequestMethod.POST, value = "/doubleVerification")
	public @ResponseBody String doubleVarification(@RequestParam(value = "encdata") String encdata,
			HttpServletRequest request) {
		try {
			logger.info("Inside Transact class in doubleVarification() Request : " + encdata);
			// org.json.JSONObject jsonObject = new org.json.JSONObject(encdata);

			String irctc = encdata;
			logger.info("Inside Transact class in doubleVarification() Request : " + irctc);
			Map<String, String> reqmap = new LinkedHashMap<>();
			AES256Utility utility = new AES256Utility();
			String dec = utility.decrypt(irctc);
			logger.info("Inside Transact class in doubleVarification() Decrypt Request : " + dec);
			dec = dec.replaceAll("\\|",
					Character.toString((char) 34) + Character.toString((char) 44) + Character.toString((char) 34));
			dec = dec.replaceAll("\\=",
					Character.toString((char) 34) + Character.toString((char) 58) + Character.toString((char) 34));
			dec = "\"" + dec + "\"";
			dec = "{" + dec + "}";

			OnlineRefunfIRCTC onlineRefunfIRCTC = new Gson().fromJson(dec, OnlineRefunfIRCTC.class);

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));

			BasicDBObject match = new BasicDBObject();
			match.put(FieldType.PG_REF_NUM.getName(), onlineRefunfIRCTC.getBankTxnId());

			List<BasicDBObject> queryExecute = Arrays.asList(new BasicDBObject("$match", match));
			logger.info("Find Transaction From PG_REF_NUM for IRCTC : " + queryExecute);
			MongoCursor<Document> cursor = coll.aggregate(queryExecute).iterator();

			String txnAmount = "";
			String reservationId = "";
			String bankTxnId = "";
			String merchantCode = "";

			String status = "";
			String statusDesc = "";

			if (cursor.hasNext()) {
				Document document = (Document) cursor.next();
				txnAmount = document.getString(FieldType.AMOUNT.getName());
				reservationId = document.getString(FieldType.ORDER_ID.getName());
				bankTxnId = document.getString(FieldType.PG_REF_NUM.getName());
				merchantCode = document.getString(FieldType.PAY_ID.getName());
			}

			if (!txnAmount.equalsIgnoreCase(String.valueOf(onlineRefunfIRCTC.getTxnAmount()))) {
				status = "Failure";
				statusDesc = "Invalid_Booking_Amount";
			}

			if (!reservationId.equalsIgnoreCase(onlineRefunfIRCTC.getReservationId())) {
				status = "Failure";
				statusDesc = "Invalid_Reservation_Id";
			}
			if (!bankTxnId.equalsIgnoreCase(onlineRefunfIRCTC.getBankTxnId())) {
				status = "Failure";
				statusDesc = "Invalid_Bank_Txn_Id";
			}
			if (!merchantCode.equalsIgnoreCase(onlineRefunfIRCTC.getMerchantCode())) {
				status = "Failure";
				statusDesc = "Invalid_merchantCode";
			}

			if (!status.equalsIgnoreCase("1")) {
				status = "Success";
				statusDesc = "Completed successfully";
			}

			Map<String, String> response = new LinkedHashMap<>();
			response.put("merchantCode", onlineRefunfIRCTC.getMerchantCode());
			response.put("reservationId", onlineRefunfIRCTC.getReservationId());
			response.put("txnAmount", String.format("%.2f", onlineRefunfIRCTC.getTxnAmount()));
			response.put("bankTxnId", onlineRefunfIRCTC.getBankTxnId());
			response.put("status", status);
			response.put("statusDesc", statusDesc);

			String res = response.toString();
			res = res.replaceAll("\\{", "");
			res = res.replaceAll("\\}", "");
			res = res.replaceAll("\\, ", "|");
			String checksum = Utils.getSHA(res);
			res = res + "|" + "checkSum=" + checksum.toUpperCase();
			logger.info("Inside Transact class in doubleVarification() Actual Response to Merchant without encryption: "
					+ res);
			res = utility.encrypt(res);
			logger.info("Inside Transact class in doubleVarification() Actual Response to Merchant: " + res);
			return res;
		} catch (Exception exception) {
			// Ideally this should be a non-reachable code
			logger.error("/doubleVarification API call Exception", exception);
			return null;
		}
	}

	@RequestMapping(method = RequestMethod.POST, value = "/internal/refund", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> InternalMerchantRefund(@RequestBody Map<String, String> reqmap) {

		try {
			Map<String, String> responseMap = null;
			Fields fields = new Fields(reqmap);
			fields.logAllFields("internalRefund Raw Request:::");
			fields.removeInternalFields();
			fields.clean();
			fields.removeExtraFields();
			// refundValidationService.validateunselltedamound(fields);
			// To put request blob
			String fieldsAsString = fields.getFieldsAsBlobString();
			fields.put(FieldType.INTERNAL_REQUEST_FIELDS.getName(), fieldsAsString);
			fields.put(FieldType.INTERNAL_VALIDATE_HASH_YN.getName(), "N");
			if (fields.contains(FieldType.UDF6.getName())) {
				if (!fields.get(FieldType.UDF6.getName()).isEmpty()) {
					fields.put(FieldType.UDF6.getName(), Constants.Y_FLAG.getValue());
				}
			}

			logger.info("Fields Values Before check refund state = {}" + fields.getFieldsAsString());
			fields = refundService.checkRefundState(fields);
			logger.info("Fields Values After check refund state = {}" + fields.getFieldsAsString());

			if (null != fields.get(FieldType.RESPONSE_CODE.getName())
					&& fields.get(FieldType.RESPONSE_CODE.getName()).equalsIgnoreCase("026")) {
				logger.info(
						"+++++++++++++++++++++++++++++ Inside if condition +++++++++++++++++++++++++++++++++++++++");
				responseMap = fields.getFields();
			} else {
				logger.info(
						"+++++++++++++++++++++++++++++ Inside else condition +++++++++++++++++++++++++++++++++++++++");
				if (reqmap.containsKey("IRCTC_REFUND") && reqmap.get("IRCTC_REFUND") != null
						&& reqmap.get("IRCTC_REFUND").equalsIgnoreCase("Y")) {
					logger.info("inside InternalMerchantRefund for irctc " + fields.getFieldsAsString());
				}
				responseMap = refundRouter.route(fields);
			}

			responseMap = removeRefundRespFieldsValues(responseMap);
			if (responseMap.containsKey("STATUS") && null != responseMap.get("STATUS")
					&& responseMap.get("STATUS").equalsIgnoreCase("Sent to Bank")) {
				responseMap.put("STATUS", "REFUND_INITIATED");
			}

			logger.info("Final Response received fron acquirer for InternalMerchantRefund , responseMap : "
					+ responseMap.toString());

			return responseMap;
		} catch (Exception exception) {
			logger.error("internalRefund Exception", exception);
			return null;
		}
	}

	@RequestMapping(method = RequestMethod.POST, value = "/merchant/refund", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> merchantRefundNew(@RequestBody Map<String, String> reqmap) {

		Map<String, String> finalResponseMap = new HashMap<String, String>();
		try {
			logger.info("Request Received For Merchant Refund For PayId :: " + reqmap.toString());
			Map<String, String> plainResponseMap = transactionControllerServiceProvider.decrypt(reqmap.get("PAY_ID"),
					reqmap.get("ENCDATA"));
			logger.info("Plain Request received from merchant, ResponseMap : " + plainResponseMap.toString());

			String decryptedString = plainResponseMap.get(FieldType.ENCDATA.getName());
			String[] fieldArray = decryptedString.split("~");
			Map<String, String> requestMap = new HashMap<String, String>();

			Fields fields = null;
			for (String entry : fieldArray) {
				String[] namValuePair = entry.split("=", 2);
				if (namValuePair.length == 2) {
					requestMap.put(namValuePair[0], namValuePair[1]);
				} else {
					requestMap.put(namValuePair[0], "");
				}
			}

			logger.info("requestMap ----------------:: " + requestMap);
			fields = new Fields(requestMap);
			logger.info("Request fields : " + fields.getFieldsAsBlobString());

			// Fields fields = new Fields(reqmap);
			fields.logAllFields("Raw Request:");
			fields.removeInternalFields();
			fields.clean();
			fields.removeExtraFields();
			// To put request blob
			String fieldsAsString = fields.getFieldsAsBlobString();
			fields.put(FieldType.INTERNAL_REQUEST_FIELDS.getName(), fieldsAsString);
			fields.put(FieldType.INTERNAL_VALIDATE_HASH_YN.getName(), "N");
			if (fields.contains(FieldType.UDF6.getName())) {
				if (!fields.get(FieldType.UDF6.getName()).isEmpty()) {
					fields.put(FieldType.UDF6.getName(), Constants.Y_FLAG.getValue());
				}
			}

			logger.info("Fields Values Before check refund state = {}" + fields.getFieldsAsString());
			fields = refundService.checkRefundState(fields);
			logger.info("Fields Values After check refund state = {}" + fields.getFieldsAsString());
			Map<String, String> responseMap = null;
			if (null != fields.get(FieldType.RESPONSE_CODE.getName())
					&& fields.get(FieldType.RESPONSE_CODE.getName()).equalsIgnoreCase("026")) {
				logger.info(
						"+++++++++++++++++++++++++++++ Inside if condition +++++++++++++++++++++++++++++++++++++++");
				responseMap = fields.getFields();
			} else {
				logger.info(
						"+++++++++++++++++++++++++++++ Inside else condition +++++++++++++++++++++++++++++++++++++++");
				responseMap = refundRouter.route(fields);
			}
			logger.info("Final Response received fron acquirer, responseMap : " + responseMap.toString());

			responseMap = removeRefundRespFieldsValues(responseMap);

			if (responseMap.containsKey("STATUS") && null != responseMap.get("STATUS")
					&& responseMap.get("STATUS").equalsIgnoreCase("Sent to Bank")) {
				responseMap.put("STATUS", "REFUND_INITIATED");
			}
			logger.info("Final Response post to merchant, responseMap : " + responseMap.toString());

			StringBuilder allFields = new StringBuilder();
			for (Entry<String, String> entry : responseMap.entrySet()) {
				allFields.append(ConfigurationConstants.FIELD_SEPARATOR.getValue());
				allFields.append(entry.getKey());
				allFields.append(ConfigurationConstants.FIELD_EQUATOR.getValue());
				allFields.append(entry.getValue());
			}
			allFields.deleteCharAt(0);
			logger.info("Merchant refund response in plain text : " + allFields.toString());
			String encryptedString = transactionControllerServiceProvider
					.encrypt(responseMap.get("PAY_ID"), allFields.toString()).get(FieldType.ENCDATA.getName());

			finalResponseMap.put("PAY_ID", responseMap.get("PAY_ID"));
			finalResponseMap.put("ENCDATA", encryptedString);
			return finalResponseMap;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			finalResponseMap.put("PAY_ID", reqmap.get("PAY_ID"));
			String encryptedString = null;
			try {
				encryptedString = transactionControllerServiceProvider
						.encrypt(reqmap.get("PAY_ID"), "Something goes wrong!").get(FieldType.ENCDATA.getName());
			} catch (SystemException e) {
				e.printStackTrace();
			}
			finalResponseMap.put("ENCDATA", encryptedString);
			return finalResponseMap;
		}
	}

	@RequestMapping(method = RequestMethod.POST, value = "/merchant/externalrefund", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> merchantRefundExternal(@RequestBody Map<String, String> reqmap) {

		Map<String, String> finalResponseMap = new HashMap<String, String>();
		try {
			logger.info("Request Received For Merchant External Refund For PayId :: " + reqmap.toString());
			Map<String, String> plainResponseMap = transactionControllerServiceProvider.decrypt(reqmap.get("PAY_ID"),
					reqmap.get("ENCDATA"));
			logger.info("Plain Request received from merchant, ResponseMap : " + plainResponseMap.toString());

			String decryptedString = plainResponseMap.get(FieldType.ENCDATA.getName());
			String[] fieldArray = decryptedString.split("~");
			Map<String, String> requestMap = new HashMap<String, String>();

			Fields fields = null;
			for (String entry : fieldArray) {
				String[] namValuePair = entry.split("=", 2);
				if (namValuePair.length == 2) {
					requestMap.put(namValuePair[0], namValuePair[1]);
				} else {
					requestMap.put(namValuePair[0], "");
				}
			}

			logger.info("requestMap ----------------:: " + requestMap);
			fields = new Fields(requestMap);
			logger.info("Request fields : " + fields.getFieldsAsBlobString());

			// Fields fields = new Fields(reqmap);
			fields.logAllFields("Raw Request:");
			fields.removeInternalFields();
			fields.clean();
			fields.removeExtraFields();
			// To put request blob
			String fieldsAsString = fields.getFieldsAsBlobString();
			fields.put(FieldType.INTERNAL_REQUEST_FIELDS.getName(), fieldsAsString);
			fields.put(FieldType.INTERNAL_VALIDATE_HASH_YN.getName(), "N");
			if (fields.contains(FieldType.UDF6.getName())) {
				if (!fields.get(FieldType.UDF6.getName()).isEmpty()) {
					fields.put(FieldType.UDF6.getName(), Constants.Y_FLAG.getValue());
				}
			}

			logger.info("Inserting PG_REF For Order Id And payId from DB : " + fields.get(FieldType.ORDER_ID.getName())
					+ " " + reqmap.get("PAY_ID"));
			Map<String, String> pgRefMap = fieldsDao.getCaptureTxn(fields.get(FieldType.ORDER_ID.getName()),
					reqmap.get("PAY_ID"));
			if (pgRefMap.containsKey(FieldType.PG_REF_NUM.getName())) {
				fields.put(FieldType.PG_REF_NUM.getName(), pgRefMap.get(FieldType.PG_REF_NUM.getName()));
			}

			logger.info("Fields Values Before check refund state = {}" + fields.getFieldsAsString());
			fields = refundService.checkRefundState(fields);
			logger.info("Fields Values After check refund state = {}" + fields.getFieldsAsString());
			Map<String, String> responseMap = null;
			if (null != fields.get(FieldType.RESPONSE_CODE.getName())
					&& fields.get(FieldType.RESPONSE_CODE.getName()).equalsIgnoreCase("026")) {
				logger.info(
						"+++++++++++++++++++++++++++++ Inside if condition +++++++++++++++++++++++++++++++++++++++");
				responseMap = fields.getFields();
			} else {
				logger.info(
						"+++++++++++++++++++++++++++++ Inside else condition +++++++++++++++++++++++++++++++++++++++");
				responseMap = refundRouter.route(fields);
			}
			logger.info("Final Response received fron acquirer, responseMap : " + responseMap.toString());

			responseMap = removeRefundRespFieldsValues(responseMap);

			if (responseMap.containsKey("STATUS") && null != responseMap.get("STATUS")
					&& responseMap.get("STATUS").equalsIgnoreCase("Sent to Bank")) {
				responseMap.put("STATUS", "REFUND_INITIATED");
			}
			logger.info("Final Response post to merchant, responseMap : " + responseMap.toString());

			StringBuilder allFields = new StringBuilder();
			for (Entry<String, String> entry : responseMap.entrySet()) {
				allFields.append(ConfigurationConstants.FIELD_SEPARATOR.getValue());
				allFields.append(entry.getKey());
				allFields.append(ConfigurationConstants.FIELD_EQUATOR.getValue());
				allFields.append(entry.getValue());
			}
			allFields.deleteCharAt(0);
			logger.info("Merchant refund response in plain text : " + allFields.toString());
			String encryptedString = transactionControllerServiceProvider
					.encrypt(responseMap.get("PAY_ID"), allFields.toString()).get(FieldType.ENCDATA.getName());

			finalResponseMap.put("PAY_ID", responseMap.get("PAY_ID"));
			finalResponseMap.put("ENCDATA", encryptedString);
			return finalResponseMap;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			finalResponseMap.put("PAY_ID", reqmap.get("PAY_ID"));
			String encryptedString = null;
			try {
				encryptedString = transactionControllerServiceProvider
						.encrypt(reqmap.get("PAY_ID"), "Something goes wrong!").get(FieldType.ENCDATA.getName());
			} catch (SystemException e) {
				e.printStackTrace();
			}
			finalResponseMap.put("ENCDATA", encryptedString);
			return finalResponseMap;
		}
	}

	@PostMapping(value = "/refund/requery", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> requeryRefund(@RequestBody Map<String, String> reqmap) {

		try {
			logger.info("requeryRefund:: initialize. request={}", reqmap);
			Fields fields = new Fields(reqmap);
			String generatedHash = getHash(fields);
			logger.info("generatedHash : " + generatedHash);
			if (generatedHash != null && !generatedHash.equalsIgnoreCase(reqmap.get("HASH"))) {
				Map<String, String> responseMap = new HashMap<String, String>();
				responseMap.put(FieldType.ORDER_ID.getName(), reqmap.get("ORDER_ID"));
				responseMap.put(FieldType.REFUND_ORDER_ID.getName(), reqmap.get("REFUND_ORDER_ID"));
				responseMap.put(FieldType.PAY_ID.getName(), reqmap.get("PAY_ID"));
				responseMap.put(FieldType.RESPONSE_MESSAGE.getName(), "Invalid Hash");
				responseMap.put(FieldType.HASH.getName(), reqmap.get("HASH"));
				responseMap.put(FieldType.RESPONSE_CODE.getName(), "300");

				return responseMap;
			} else {
				Map<String, String> responseMap = refundService.refundStatusInquiry(reqmap.get("ORDER_ID"),
						reqmap.get("PAY_ID"), reqmap.get("REFUND_ORDER_ID"));

				if (responseMap.containsKey("STATUS") && null != responseMap.get("STATUS")
						&& responseMap.get("STATUS").equalsIgnoreCase("Sent to Bank")) {
					responseMap.put("STATUS", "REFUND_INITIATED");
				}
				return responseMap;
			}

		} catch (Exception exception) {
			// Ideally this should be a non-reachable code
			logger.error("requeryRefund:: failed. request={}", reqmap, exception);
			return null;
		}

	}

	private Map<String, String> removeRefundRespFieldsValues(Map<String, String> responseMap) {
		if (responseMap.containsKey(FieldType.INTERNAL_CUSTOM_MDC.getName())) {
			responseMap.remove(FieldType.INTERNAL_CUSTOM_MDC.getName());
		}
		if (responseMap.containsKey(FieldType.CUST_PHONE.getName())) {
			responseMap.remove(FieldType.CUST_PHONE.getName());
		}
		if (responseMap.containsKey(FieldType.HASH.getName())) {
			responseMap.remove(FieldType.HASH.getName());
		}
		if (responseMap.containsKey(FieldType.ORIG_TXN_ID.getName())) {
			responseMap.remove(FieldType.ORIG_TXN_ID.getName());
		}
		if (responseMap.containsKey(FieldType.CARD_MASK.getName())) {
			responseMap.remove(FieldType.CARD_MASK.getName());
		}
		if (responseMap.containsKey(FieldType.CURRENCY_CODE.getName())) {
			responseMap.remove(FieldType.CURRENCY_CODE.getName());
		}
		if (responseMap.containsKey(FieldType.CUST_EMAIL.getName())) {
			responseMap.remove(FieldType.CUST_EMAIL.getName());
		}
		if (responseMap.containsKey(FieldType.AUTH_CODE.getName())) {
			responseMap.remove(FieldType.AUTH_CODE.getName());
		}
		if (responseMap.containsKey(FieldType.TXN_ID.getName())) {
			responseMap.remove(FieldType.TXN_ID.getName());
		}
		if (responseMap.containsKey(FieldType.RRN.getName())) {
			responseMap.remove(FieldType.RRN.getName());
		}
		if (responseMap.containsKey(FieldType.CARD_HOLDER_NAME.getName())) {
			responseMap.remove(FieldType.CARD_HOLDER_NAME.getName());
		}
		return responseMap;

	}

	public static String getHash(Fields fields) throws SystemException {

		String salt = null;
		salt = PropertiesManager.saltStore.get(fields.get(FieldType.PAY_ID.getName()));
		if (StringUtils.isBlank(salt)) {
			salt = (new PropertiesManager()).getSalt(fields.get(FieldType.PAY_ID.getName()));
			if (salt != null) {
				logger.info("Salt found from propertiesManager for payId = " + fields.get(FieldType.PAY_ID.getName()));
			}

		} else {
			logger.info("Salt found from static map in propertiesManager for payId = "
					+ fields.get(FieldType.PAY_ID.getName()));
		}

		if (null == salt) {
			logger.info("Inside Hasher , salt = null , fields = " + fields.getFieldsAsBlobString());
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.INVALID_PAYID_ATTEMPT.getCode());
			throw new SystemException(ErrorType.INVALID_PAYID_ATTEMPT, "Invalid " + FieldType.PAY_ID.getName());
		}

		logger.info("hash check..={}", fields.getFieldsAsString());
		// Calculate the hash string
		StringBuilder allFields = new StringBuilder();
		allFields.append(FieldType.ORDER_ID.getName());
		allFields.append(ConfigurationConstants.FIELD_EQUATOR.getValue());
		allFields.append(fields.get(FieldType.ORDER_ID.getName()));
		allFields.append(ConfigurationConstants.FIELD_SEPARATOR.getValue());
		allFields.append(FieldType.PAY_ID.getName());
		allFields.append(ConfigurationConstants.FIELD_EQUATOR.getValue());
		allFields.append(fields.get(FieldType.PAY_ID.getName()));
		allFields.append(ConfigurationConstants.FIELD_SEPARATOR.getValue());
		allFields.append(FieldType.REFUND_ORDER_ID.getName());
		allFields.append(ConfigurationConstants.FIELD_EQUATOR.getValue());
		allFields.append(fields.get(FieldType.REFUND_ORDER_ID.getName()));
		allFields.append(salt);
		logger.info(" ############# allFields ############# " + allFields.toString());

		// Calculate hash at server side
		return Hasher.getHash(allFields.toString());
	}

	@RequestMapping(method = RequestMethod.POST, value = "/demoProcess/response", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> demoResponse(@RequestBody Map<String, String> reqmap) {

		try {
			logger.info("/demoProcess/response request: {}", reqmap);
			Fields fields = new Fields(reqmap);
			// fields.logAllFields("Cashfree Raw Request:");
			fields.logAllFieldsUsingMasking("demoProcess Raw Request:");
			fields.clean();
			Fields internalFields = fields.removeInternalFields();
			// Refine the fields sent by internal application
			fields.removeExtraInternalFields();
			fields.put(FieldType.IS_INTERNAL_REQUEST.getName(), Constants.Y_FLAG.getValue());
			// put Internal fields back
			fields.put(internalFields);
			Map<String, String> responseMap = demoSaleResponseHandler.process(fields);
			logger.info("/demoProcess/response response: {} ", responseMap);
			return responseMap;
		} catch (Exception exception) {
			logger.error("/demoProcess/response Exception", exception);
			return null;
		}
	}

	@RequestMapping(method = RequestMethod.POST, value = "/quomoProcess/response", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> quomoResponse(@RequestBody Map<String, String> reqmap) {

		try {
			Fields fields = new Fields(reqmap);
			fields.logAllFields("Raw Request QUOMO : ");
			fields.clean();
			Fields internalFields = fields.removeInternalFields();
			// Refine the fields sent by internal application
			fields.removeExtraInternalFields();
			fields.put(FieldType.IS_INTERNAL_REQUEST.getName(), Constants.Y_FLAG.getValue());
			// put Internal fields back
			fields.put(internalFields);
			Map<String, String> responseMap = quomoSaleResponseHandler.process(fields);
			return responseMap;
		} catch (Exception exception) {
			logger.error("Exception in QUOMO response ", exception);
			return null;
		}

	}

	@RequestMapping(method = RequestMethod.POST, value = "/api/v1/rest/transaction/payin", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ApiResponse> restPayinTransaction(@RequestBody Map<String, String> reqmap,
			HttpServletRequest request1) {
		ApiResponse response = new ApiResponse();
		Map<String, String> respMap = null;
		try {
			Fields fields = new Fields(reqmap);
			logger.info("REST FLOW, Request received for payin transaction, request={}" + fields.getFieldsAsString());

			// Map<String, String> responseMap = null; //restCallProcessor.process(fields,
			// request1);

			respMap = restCallProcessor.ValidRequest(fields);
			logger.info("REST FLOW, Final response to maerchant for payin transaction, response={}" + respMap);
			if (respMap != null && !respMap.isEmpty()) {

				response.setMessage("Invalid Request");
				response.setStatus(false);
				response.setData(respMap);
				return ResponseEntity.ok(response);
			}
			respMap = restCallProcessor.process(fields, request1);
			if (respMap != null) {
				respMap.put("respMessage", "Request Accepted");
				respMap.put("respCode", "000");
				respMap.put("ORDER_ID", fields.get(FieldType.ORDER_ID.getName()));
				response.setMessage("Request Accepted");
				response.setStatus(true);
				response.setData(respMap);
				return ResponseEntity.ok(response);
			} else {
				response.setMessage("Something went wrong, Please try again!");
				response.setStatus(false);
				response.setData("");
				return ResponseEntity.ok(response);
			}

		} catch (Exception exception) {
			logger.error(String.format("Error creating rest payment redirect for order %s", reqmap.get("ORDER_ID")));
			response.setMessage("Something went wrong, Please try again!");
			response.setStatus(false);
			response.setData("");
			return ResponseEntity.ok(response);
		}

	}

	@RequestMapping(method = RequestMethod.POST, value = "/api/v1/rest/transaction/payout", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Map<String, String> restPayOutTransaction(@RequestBody Map<String, String> reqmap,
			HttpServletRequest request) {
		Map<String, String> respMap = new HashMap<String, String>();
		try {
			// Fields fields = new Fields(reqmap);
			logger.info("REST FLOW, Request received for payin transaction, request={}" + reqmap);

			Fields fields = restCallProcessor.prepareFieldsMerchantHosted(reqmap);
			logger.info("REST FLOW, Final response to maerchant for payin transaction, response={}"
					+ fields.getFieldsAsString());

			String encData = restCallProcessor.processPayoutRequest(fields, request);

			respMap.put(FieldType.PAY_ID.getName(), reqmap.get(FieldType.PAY_ID.getName()));
			respMap.put(FieldType.ENCDATA.getName(), encData);
			return respMap;
		} catch (Exception exception) {
			logger.error(String.format("Error creating rest payment redirect for order %s", reqmap.get("ORDER_ID")));
			respMap.put(FieldType.PAY_ID.getName(), reqmap.get(FieldType.PAY_ID.getName()));
			respMap.put(FieldType.ENCDATA.getName(), null);
			return respMap;
		}

	}

	@RequestMapping(method = RequestMethod.POST, value = "/balanceEnquiry", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody String payoutBalanceEnquiry(@RequestBody String jsonRequest) {
		logger.info("Request Data for balance Enquiry : " + jsonRequest);
		Map<String, String> responsemap = new LinkedHashMap<String, String>();


		org.json.JSONObject jo = new org.json.JSONObject(jsonRequest);
		String merchantId = jo.optString("merchantId");
		String reuqestHash = jo.optString("hash");

		if (StringUtils.isBlank(merchantId)) {
			responsemap.put("code", ErrorType.MERCHANT.getResponseCode());
			responsemap.put("message",  ErrorType.MERCHANT.getResponseMessage());
			responsemap.put("requestId", TransactionManager.getNewTransactionId());
			responsemap.put("response", "ERROR");
			return gson.toJson(responsemap);
		}
		
		User user = null;
		if (PropertiesManager.propertiesMap.get(Constants.USE_STATIC_DATA.getValue()) != null
				&& PropertiesManager.propertiesMap.get(Constants.USE_STATIC_DATA.getValue())
						.equalsIgnoreCase(Constants.Y_FLAG.getValue())) {
			user = staticDataProvider.getUserData(merchantId);
		} else {
			user = userDao.findPayId(merchantId);
		}
		
		if (ObjectUtils.isEmpty(user)) {
			responsemap.put("code",  ErrorType.MERCHANT.getResponseCode());
			responsemap.put("message",  ErrorType.MERCHANT.getResponseMessage());
			responsemap.put("requestId", TransactionManager.getNewTransactionId());
			responsemap.put("response", "ERROR");
			return gson.toJson(responsemap);
		}
		
		String secretKey = merchantKeySaltDao.find(merchantId).getSalt();

		HashMap<String, String> tree_map = new LinkedHashMap<>();
		tree_map.put("merchantId", jo.getString("merchantId"));
		tree_map.put("secretKey", secretKey);

		gson = new GsonBuilder().disableHtmlEscaping().create();
		String jsonString = gson.toJson(tree_map);
		String hash = generatetHashString(jsonString);
		tree_map.put("hash", hash);
		tree_map.remove("secretKey");

		logger.info("Data : " + jsonString);

		if (!hash.equals(reuqestHash)) {
			responsemap.put("code",  ErrorType.INVALID_HASH.getResponseCode());
			responsemap.put("message", ErrorType.INVALID_HASH.getResponseMessage());
			responsemap.put("response", "ERROR");
			responsemap.put("secretKey", secretKey);
			gson = new GsonBuilder().disableHtmlEscaping().create();
			String genHash = gson.toJson(responsemap);
			String returnHash = generatetHashString(genHash);
			responsemap.put("hash", returnHash);
			responsemap.remove("secretKey");
			String jsonReturnString = gson.toJson(responsemap);
			return jsonReturnString;
		}

		if (StringUtils.isBlank(merchantId)) {
			responsemap.put("code", ErrorType.MERCHANT.getResponseCode());
			responsemap.put("message", ErrorType.MERCHANT.getResponseMessage());
			responsemap.put("requestId", TransactionManager.getNewTransactionId());
			responsemap.put("response", "ERROR");
			responsemap.put("secretKey", secretKey);

			gson = new GsonBuilder().disableHtmlEscaping().create();
			String genHash = gson.toJson(responsemap);
			String returnHash = generatetHashString(genHash);

			responsemap.put("hash", returnHash);
			responsemap.remove("secretKey");
			return gson.toJson(responsemap);
		}

		List<Map<String, String>> balanceMap = new ArrayList<>();
		walletHistoryRepository.findMerchantFundByPayId(jo.getString("merchantId")).stream().forEach(entry -> {
			Map<String, String> map = new HashMap<>();
			map.put("Amount", entry.get("finalBalance").toString());
			map.put("Currency", entry.get("currency").toString());
			balanceMap.add(map);
		});

		responsemap.put("merchantId", jo.getString("merchantId"));
		responsemap.put("balance", new Gson().toJson(balanceMap));
		responsemap.put("status", "SUCCESS");
		responsemap.put("responseCode", ErrorType.SUCCESS.getResponseCode());
		responsemap.put("responseMessage", ErrorType.SUCCESS.getResponseMessage());
		responsemap.put("secretKey", secretKey);

		gson = new GsonBuilder().disableHtmlEscaping().create();
		String genHash = gson.toJson(responsemap);
		String returnHash = generatetHashString(genHash);

		responsemap.put("hash", returnHash);
		responsemap.remove("secretKey");
		
		JSONArray balanceArray = new JSONArray(new Gson().toJson(balanceMap));
        JSONObject return_jo = new JSONObject();
		return_jo.put("balance", balanceArray);
		return_jo.put("hash", returnHash);
		return_jo.put("merchantId", jo.getString("merchantId"));
		return_jo.put("responseCode", ErrorType.SUCCESS.getResponseCode());
		return_jo.put("responseMessage", ErrorType.SUCCESS.getResponseMessage());
		return_jo.put("status", "SUCCESS");
		return return_jo.toString(4);
	
	}

	private static String generatetHashString(String input) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] messageDigest = md.digest(input.getBytes());
			BigInteger no = new BigInteger(1, messageDigest);
			StringBuilder hashtext = new StringBuilder(no.toString(16));
			while (hashtext.length() < 64) {
				hashtext.insert(0, '0');
			}
			return hashtext.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}
}