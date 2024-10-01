package com.pay10.pg.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.pay10.TFP.TFPProcessor;
import com.pay10.TFP.TFPStausEnquiryProcessor;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mongodb.BasicDBObject;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.pay10.agreepay.AgreepayStatusEnquiryProcessor;
import com.pay10.apbl.APBLStatusEnquiryProcessor;
import com.pay10.atom.AtomStatusEnquiryProcessor;
import com.pay10.axisbank.card.AxisBankCardStatusEnquiryProcessor;
import com.pay10.axisbank.netbanking.AxisBankNBStatusEnquiryProcessor;
import com.pay10.axisbank.upi.AxisBankUpiStatusEnquiryProcessor;
import com.pay10.billdesk.BilldeskStatusEnquiryProcessor;
import com.pay10.bob.BobStatusEnquiryProcessor;
import com.pay10.camspay.CamsPayStatusEnquiryProcessor;
import com.pay10.canaraNBbank.CanaraNBProcessor;
import com.pay10.cashfree.CashfreeStatusEnquiryProcessor;
import com.pay10.commons.api.SmsSender;
import com.pay10.commons.dao.FieldsDao;
import com.pay10.commons.dao.RouterConfigurationDao;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.user.Account;
import com.pay10.commons.user.AccountCurrency;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.Amount;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.DateCreater;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.SystemProperties;
import com.pay10.commons.util.TransactionManager;
import com.pay10.commons.util.TransactionType;
import com.pay10.cosmos.CosmosProcessor;
import com.pay10.direcpay.DirecpayStatusEnquiryProcessor;
import com.pay10.easebuzz.EasebuzzStatusEnquiryProcessor;
import com.pay10.federalNB.FederalBankNBStatusEnquiryProcessor;
import com.pay10.freecharge.FreeChargeStatusEnquiryProcessor;
import com.pay10.hdfc.upi.HdfcUpiStatusEnquiryProcessor;
import com.pay10.icici.netbanking.IciciNBStatusEnquiryProcessor;
import com.pay10.idfc.IdfcbankNBStatusEnquiryProcessor;
import com.pay10.ingenico.IngenicoStatusEnquiryProcessor;
import com.pay10.isgpay.ISGPayStatusEnquiryProcessor;
import com.pay10.jammuandkashmir.JammuandKishmirBankNBStatusEnquiryProcessor;
import com.pay10.kotak.KotakCardStatusEnquiryProcessor;
import com.pay10.kotak.nb.KotakNbStatusEnquiryProcessor;
import com.pay10.lyra.LyraStatusEnquiryProcessor;
import com.pay10.mobikwik.MobikwikStatusEnquiryProcessor;
import com.pay10.paymentEdge.PaymentEdgeStatusEnquiryProcessor;
import com.pay10.paytm.PaytmStatusEnquiryProcessor;
import com.pay10.payu.PayuStatusEnquiryProcessor;
import com.pay10.pg.core.util.Processor;
import com.pay10.pg.security.SecurityProcessor;
import com.pay10.phonepe.PhonePeStatusEnquiryProcessor;
import com.pay10.pinelabs.PinelabsStatusEnquiryProcessor;
import com.pay10.quomo.QuomoStatusEnquiryProcessor;
import com.pay10.sbi.SbiStatusEnquiryProcessor;
import com.pay10.sbi.card.SbiCardStatusEnquiryProcessor;
import com.pay10.sbi.netbanking.SbiNBStatusEnquiryProcessor;
import com.pay10.sbi.upi.SbiUpiStatusEnquiryProcessor;
import com.pay10.shivalikNB.ShivalikStatusEnquiryProcessor;
import com.pay10.tmbNB.TMBNBProcessor;
import com.pay10.yesbank.netbanking.YesbankNBStatusEnquiryProcessor;
import com.pay10.yesbankcb.YesBankUpiStatusEnquiryProcessor;

@Service
public class StatusEnquiryProcessor {

	private static Logger logger = LoggerFactory.getLogger(StatusEnquiryProcessor.class.getName());
	private static final String prefix = "MONGO_DB_";
	private static final Collection<String> allDBRequestFields = SystemProperties.getAllDBRequestFields();
	private static final Collection<String> aLLDB_Fields = SystemProperties.getDBFields();

	@Autowired
	@Qualifier("updateProcessor")
	private Processor updateProcessor;

	@Autowired
	@Qualifier("auto")
	private AutoRefundServiceab autoRefundService;
	
	
	@Autowired
	private PaymentEdgeStatusEnquiryProcessor paymentEdgeStatusEnquiryProcessor;


	@Autowired
	ShivalikStatusEnquiryProcessor shivalikStatusEnquiryProcessor;

	@Autowired
	private SecurityProcessor securityProcessor;

	@Autowired
	 private KotakCardStatusEnquiryProcessor kotakCardStatusEnquiryProcessor;
	
	
	@Autowired
	private BobStatusEnquiryProcessor bobStatusEnquiryProcessor;

	@Autowired
	private CosmosProcessor cosmosProcessor;

	@Autowired
	private HdfcUpiStatusEnquiryProcessor hdfcUpiStatusEnquiryProcessor;

	@Autowired
	private DirecpayStatusEnquiryProcessor direcpayStatusEnquiryProcessor;

	@Autowired
	private ISGPayStatusEnquiryProcessor iSGPayStatusEnquiryProcessor;

	@Autowired
	private AxisBankNBStatusEnquiryProcessor axisBankNBStatusEnquiryProcessor;

	@Autowired
	private AxisBankUpiStatusEnquiryProcessor axisBankUpiStatusEnquiryProcessor;

	@Autowired
	private AxisBankCardStatusEnquiryProcessor axisBankCardStatusEnquiryProcessor;

	@Autowired
	private KotakNbStatusEnquiryProcessor kotakNbStatusEnquiryProcessor;


	@Autowired
	private AtomStatusEnquiryProcessor atomStatusEnquiryProcessor;

	@Autowired
	private IdfcbankNBStatusEnquiryProcessor idfcbankNBStatusEnquiryProcessor;

	@Autowired
	private APBLStatusEnquiryProcessor apblStatusEnquiryProcessor;

	@Autowired
	private IngenicoStatusEnquiryProcessor ingenicoStatusEnquiryProcessor;

	@Autowired
	private PaytmStatusEnquiryProcessor paytmStatusEnquiryProcessor;

	@Autowired
	private PayuStatusEnquiryProcessor payuStatusEnquiryProcessor;

	@Autowired
	private CanaraNBProcessor canaraNBProcessor;

	@Autowired
	private TFPStausEnquiryProcessor tFPStausEnquiryProcessor;

	@Autowired
	TMBNBProcessor tmbnbProcessor;



	@Autowired
	private MobikwikStatusEnquiryProcessor mobikwikStatusEnquiryProcessor;

	@Autowired
	private PhonePeStatusEnquiryProcessor phonePeStatusEnquiryProcessor;

	@Autowired
	private BilldeskStatusEnquiryProcessor billdeskStatusEnquiryProcessor;

	@Autowired
	private LyraStatusEnquiryProcessor lyraStatusEnquiryProcessor;

	@Autowired
	private SbiStatusEnquiryProcessor sbiStatusEnquiryProcessor;

	@Autowired
	private SbiNBStatusEnquiryProcessor sbiNbStatusEnquiryProcessor;

	@Autowired
	private SbiCardStatusEnquiryProcessor sbiCardStatusEnquiryProcessor;

	@Autowired
	private FreeChargeStatusEnquiryProcessor freeChargeStatusEnquiryProcessor;

	@Autowired
	private IciciNBStatusEnquiryProcessor iciciNBStatusEnquiryProcessor;

	@Autowired
	private CashfreeStatusEnquiryProcessor cashfreeStatusEnquiryProcessor;

	@Autowired
	private EasebuzzStatusEnquiryProcessor easebuzzStatusEnquiryProcessor;

	@Autowired
	private AgreepayStatusEnquiryProcessor agreepayStatusEnquiryProcessor;

	@Autowired
	private YesbankNBStatusEnquiryProcessor yesbankNBStatusEnquiryProcessor;

	@Autowired
	private FederalBankNBStatusEnquiryProcessor federalBankNBStatusEnquiryProcessor;

	@Autowired
	private YesBankUpiStatusEnquiryProcessor yesBankUpiStatusEnquiryProcessor;

	@Autowired
	private PinelabsStatusEnquiryProcessor pinelabsStatusEnquiryProcessor;


	@Autowired
	private CamsPayStatusEnquiryProcessor camsPayStatusEnquiryProcessor;

	@Autowired
	private SbiUpiStatusEnquiryProcessor SbiUpiStatusEnquiryProcessor;

	@Autowired
	private JammuandKishmirBankNBStatusEnquiryProcessor jammuandKishmirBankNBStatusEnquiryProcessor;

	@Autowired
	private MongoInstance mongoInstance;

	@Autowired
	PropertiesManager propertiesManager;

	@Autowired
	private FieldsDao fieldsDao;

	@Autowired
	private SmsSender smsSender;

	@Autowired
	private RouterConfigurationDao routerConfigurationDao;

	@Autowired
	private UserDao userDao;
	
	@Autowired
	private QuomoStatusEnquiryProcessor quomoStatusEnquiryProcessor;

	private static int smsCount = 0;

	@SuppressWarnings("incomplete-switch")
	public Map<String, String> process(Fields fields) throws SystemException {

		logger.info("Request received for statusEnquiryProcessor");
		boolean iSTxnFound = getTransactionFields(fields);

		if (StringUtils.isBlank(fields.get(FieldType.ACQUIRER_TYPE.getName()))) {

			logger.info(
					"Acquirer not found for status enquiry , pgRef == " + fields.get(FieldType.PG_REF_NUM.getName()));
			fields.put(FieldType.STATUS.getName(), StatusType.INVALID.getName());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), "No Such Transaction found");
			fields.put(FieldType.PG_TXN_MESSAGE.getName(), "No Such Transaction found");

			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.NO_SUCH_TRANSACTION.getCode());
			return fields.getFields();
		}

		String stausOFTxn = fields.get(FieldType.STATUS.getName());

        if (stausOFTxn.equalsIgnoreCase(StatusType.CAPTURED.getName())
                || stausOFTxn.equalsIgnoreCase(StatusType.SETTLED_SETTLE.getName())
                || stausOFTxn.equalsIgnoreCase(StatusType.SETTLED_RECONCILLED.getName())) {

			logger.info("Transaction not found for status enquiry , pgRef == "
					+ fields.get(FieldType.PG_REF_NUM.getName()));
			fields.put(FieldType.STATUS.getName(), StatusType.PROCESSED.getName());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), "Transaction already success");

			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.ALREADY_CAPTURED_TRANSACTION.getCode());
			return fields.getFields();
		}

		if (!iSTxnFound) {

			logger.info("Transaction not found for status enquiry , pgRef == "
					+ fields.get(FieldType.PG_REF_NUM.getName()));
			fields.put(FieldType.STATUS.getName(), StatusType.INVALID.getName());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), "No Such Transaction found");
			fields.put(FieldType.PG_TXN_MESSAGE.getName(), "No Such Transaction found");

			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.NO_SUCH_TRANSACTION.getCode());
			return fields.getFields();
		}

		securityProcessor.addAcquirerFields(fields);

		AcquirerType acquirerType = AcquirerType.getInstancefromCode(fields.get(FieldType.ACQUIRER_TYPE.getName()));

		switch (acquirerType) {

			case BOB:
				bobStatusEnquiryProcessor.enquiryProcessor(fields);
				break;
			case HDFC:
				hdfcUpiStatusEnquiryProcessor.enquiryProcessor(fields); // HDFC UPI
				break;
			case IDFC:
				idfcbankNBStatusEnquiryProcessor.enquiryProcessor(fields);
				break;
			case DIRECPAY:
				direcpayStatusEnquiryProcessor.enquiryProcessor(fields); // Direcpay
				break;
			case JAMMU_AND_KASHMIR:
				jammuandKishmirBankNBStatusEnquiryProcessor.enquiryProcessor(fields); // ATOM
				break;
			case ISGPAY:
				iSGPayStatusEnquiryProcessor.enquiryProcessor(fields); // ISGPay
				break;
			case AXISBANK:
				if (fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(PaymentType.NET_BANKING.getCode())) {
					axisBankNBStatusEnquiryProcessor.enquiryProcessor(fields); // Axis Bank Net Banking
				} else if (fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(PaymentType.UPI.getCode())) {
					axisBankUpiStatusEnquiryProcessor.enquiryProcessor(fields); // Axis Bank UPI
				} else {
					axisBankCardStatusEnquiryProcessor.enquiryProcessor(fields); // Axis Bank Cards
				}
				break;

			case CANARANBBANK:
				canaraNBProcessor.enquiryProcessor(fields);//CANARA NB
				//canaraNBProcessor.statusEnquiry(fields,false);//CANARA NB
				break;
			case KOTAK_CARD:
				kotakCardStatusEnquiryProcessor.enquiryProcessor(fields);
				break;
			case TMBNB:
				tmbnbProcessor.enquiryStatus(fields);//tmb NB added by vijay
				break;
			case SHIVALIKNBBANK:
				shivalikStatusEnquiryProcessor.enquiryProcessor(fields);//SHIVALIK NB added by vijay
				break;
			case TFP:
				tFPStausEnquiryProcessor.enquiryProcessor(fields);//TFP added by abhi
				break;
			case ATOM:
				atomStatusEnquiryProcessor.enquiryProcessor(fields); // ATOM
				break;
			case FREECHARGE:
				freeChargeStatusEnquiryProcessor.enquiryProcessor(fields); // FREECHARGE
				break;
			case APBL:
				apblStatusEnquiryProcessor.enquiryProcessor(fields); // APBL
				break;

			case INGENICO:
				ingenicoStatusEnquiryProcessor.enquiryProcessor(fields); // INGENICO
				break;

			case KOTAK:
				kotakNbStatusEnquiryProcessor.enquiryProcessor(fields); // HDFC UPI
				break;

			case PAYTM:
				paytmStatusEnquiryProcessor.enquiryProcessor(fields); // Paytm
				break;

			case PAYU:
				payuStatusEnquiryProcessor.enquiryProcessor(fields); // Payu
				break;

			case COSMOS:
				cosmosProcessor.statusCheckApi(fields);
				break;

			case MOBIKWIK:
				mobikwikStatusEnquiryProcessor.enquiryProcessor(fields); // Mobikwik
				break;

			case PHONEPE:
				phonePeStatusEnquiryProcessor.enquiryProcessor(fields); // PhonePe
				break;

			case BILLDESK:
				billdeskStatusEnquiryProcessor.enquiryProcessor(fields);
				break;

			case LYRA:
				lyraStatusEnquiryProcessor.enquiryProcessor(fields); // LYRA
				break;

			case ICICIBANK:
				iciciNBStatusEnquiryProcessor.enquiryProcessor(fields);
				break;

			case SBI:
				if (fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(PaymentType.UPI.getCode())) {
					SbiUpiStatusEnquiryProcessor.enquiryProcessor(fields);
				} else {
					sbiStatusEnquiryProcessor.enquiryProcessor(fields);
				}
				break;

			case CASHFREE:
				cashfreeStatusEnquiryProcessor.enquiryProcessor(fields);
				break;
				
			case PAYMENTAGE:
				paymentEdgeStatusEnquiryProcessor.enquiryProcessor(fields);
				break;

			case EASEBUZZ:
				easebuzzStatusEnquiryProcessor.enquiryProcessor(fields);
				break;

			case AGREEPAY:
				agreepayStatusEnquiryProcessor.enquiryProcessor(fields);
				break;

			case YESBANKNB:
				yesbankNBStatusEnquiryProcessor.enquiryProcessor(fields);
				break;

			case NB_FEDERAL:
				federalBankNBStatusEnquiryProcessor.enquiryProcessor(fields);
				break;
			case YESBANKCB:
				yesBankUpiStatusEnquiryProcessor.enquiryProcessor(fields);
				break;
			case PINELABS:
				pinelabsStatusEnquiryProcessor.enquiryProcessor(fields);
				break;
			case SBINB:
				sbiNbStatusEnquiryProcessor.enquiryProcessor(fields);
				break;
			case SBICARD:
				sbiCardStatusEnquiryProcessor.enquiryProcessor(fields);
				break;
			case CAMSPAY:
				camsPayStatusEnquiryProcessor.enquiryProcessor(fields);
				break;
			case QUOMO:
				quomoStatusEnquiryProcessor.enquiryProcessor(fields);
				break;
			default:

				Map<String, String> failedTxnField = fields.getFields();
				failedTxnField.put(FieldType.STATUS.getName(), "");
				return fields.getFields();
		}

		fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), fields.get(FieldType.TXNTYPE.getName()));
		fields.removeSecureFields();

		logger.info("StatusEnquiryProcessor, stausOFTxn :" + stausOFTxn);
		logger.info("StatusEnquiryProcessor, STATUS :" + fields.getFields().get(FieldType.STATUS.getName()));
		if (StringUtils.isNotBlank(fields.getFields().get(FieldType.STATUS.getName()))
				&& !fields.getFields().get(FieldType.STATUS.getName()).equalsIgnoreCase(stausOFTxn)) {

			if (!fields.getFields().get(FieldType.STATUS.getName()).equalsIgnoreCase(StatusType.INVALID.getName())) {
				updateTxnData(fields);
			}

		}
		if (fields.getFields().get(FieldType.STATUS.getName()).equalsIgnoreCase("Captured")
				&& StringUtils.isNotBlank(fields.getFields().get(FieldType.STATUS.getName()))) {

			autoRefundService.AutoRefundcommunicator(fields.get(FieldType.PG_REF_NUM.getName()));
		}
		return fields.getFields();
	}

	public void updateTxnData(Fields fields) {

		try {

			Fields newFields = createAllForRefund(fields);
			String txnId = TransactionManager.getNewTransactionId();
			newFields.put(FieldType.TXN_ID.getName(), txnId);
			newFields.put("_id", txnId);

			Map<String, String> fieldsMap = fields.getFields();

			for (String key : fieldsMap.keySet()) {

				if (key.equalsIgnoreCase(FieldType.TXN_ID.getName()) || key.equalsIgnoreCase("_id")
						|| key.equalsIgnoreCase(FieldType.TXNTYPE.getName())) {
					continue;
				}

				newFields.put(key, fieldsMap.get(key));
			}

			newFields.put(FieldType.ORIG_TXNTYPE.getName(), newFields.get(FieldType.TXNTYPE.getName()));

			// This has been done to enter the same date as the enroll / sent to bank date.
			// If Fields Dao is used , it will generate a new date and enter that date
			// If status enquiry is done after 12 AM for transaction before 12 AM , dates on
			// enroll / sent to bank and Capture will be different.

			insertTransaction(newFields);
		} catch (Exception e) {
			logger.info("Exception while udpating new txn data from status enquiry");
		}
	}

	public boolean getTransactionFields(Fields fields) {

		try {
			BasicDBObject finalquery = new BasicDBObject(FieldType.PG_REF_NUM.getName(),
					fields.get(FieldType.PG_REF_NUM.getName()));

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns
					.getCollection(propertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
			BasicDBObject match = new BasicDBObject("$match", finalquery);

			BasicDBObject sort = new BasicDBObject("$sort", new BasicDBObject("INSERTION_DATE", -1));
			BasicDBObject limit = new BasicDBObject("$limit", 1);

			List<BasicDBObject> pipeline = Arrays.asList(match, sort, limit);

			AggregateIterable<Document> output = coll.aggregate(pipeline);
			output.allowDiskUse(true);
			MongoCursor<Document> cursor = output.iterator();

			if (!cursor.hasNext()) {
				return false;
			} else {
				while (cursor.hasNext()) {
					Document dbobj = cursor.next();

					if (null != dbobj.getString(FieldType.PAYMENT_TYPE.toString())) {
						fields.put(FieldType.PAYMENT_TYPE.getName(),
								dbobj.getString(FieldType.PAYMENT_TYPE.toString()));
					}

					if (null != dbobj.getString(FieldType.TXNTYPE.toString())) {
						fields.put(FieldType.ORIG_TXNTYPE.getName(), dbobj.getString(FieldType.TXNTYPE.toString()));
					}

					if (null != dbobj.getString(FieldType.PAY_ID.toString())) {
						fields.put(FieldType.PAY_ID.getName(), dbobj.getString(FieldType.PAY_ID.toString()));
					}

					if (null != dbobj.getString(FieldType.ORDER_ID.toString())) {
						fields.put(FieldType.ORDER_ID.getName(), dbobj.getString(FieldType.ORDER_ID.toString()));
					}

					if (null != dbobj.getString(FieldType.ACQUIRER_TYPE.toString())) {
						fields.put(FieldType.ACQUIRER_TYPE.getName(),
								dbobj.getString(FieldType.ACQUIRER_TYPE.toString()));
					}

					if (null != dbobj.getString(FieldType.CURRENCY_CODE.toString())) {
						fields.put(FieldType.CURRENCY_CODE.getName(),
								dbobj.getString(FieldType.CURRENCY_CODE.toString()));
					}

					if (null != dbobj.getString(FieldType.AMOUNT.toString())) {
						fields.put(FieldType.AMOUNT.getName(),
								Amount.formatAmount(dbobj.getString(FieldType.AMOUNT.toString()),
										dbobj.getString(FieldType.CURRENCY_CODE.toString())));
					}
					if (null != dbobj.getString(FieldType.INTERNAL_REQUEST_FIELDS.toString())) {
						fields.put(FieldType.INTERNAL_REQUEST_FIELDS.getName(),
								dbobj.getString(FieldType.INTERNAL_REQUEST_FIELDS.toString()));
					}
					if (null != dbobj.getString(FieldType.TOTAL_AMOUNT.toString())) {
						fields.put(FieldType.TOTAL_AMOUNT.getName(),
								Amount.formatAmount(dbobj.getString(FieldType.TOTAL_AMOUNT.toString()),
										dbobj.getString(FieldType.CURRENCY_CODE.toString())));
					}

					if (null != dbobj.getString(FieldType.CREATE_DATE.toString())) {
						fields.put(FieldType.CREATE_DATE.getName(), dbobj.getString(FieldType.CREATE_DATE.toString()));
					}

					fields.put(FieldType.TXNTYPE.getName(), TransactionType.STATUS.getName());

					if (null != dbobj.getString(FieldType.STATUS.toString())) {
						fields.put(FieldType.STATUS.getName(), dbobj.getString(FieldType.STATUS.toString()));
					}

					if (null != dbobj.getString(FieldType.MOP_TYPE.toString())) {
						fields.put(FieldType.MOP_TYPE.getName(), dbobj.get(FieldType.MOP_TYPE.toString()).toString());
					}

					if (null != dbobj.getString(FieldType.ROUTER_CONFIGURATION_ID.toString())) {
						fields.put(FieldType.ROUTER_CONFIGURATION_ID.getName(),
								dbobj.get(FieldType.ROUTER_CONFIGURATION_ID.toString()).toString());
					}
					String acquirerType = fields.get(FieldType.ACQUIRER_TYPE.getName());
					if (acquirerType.equals(AcquirerType.YESBANKNB.getCode())) {
						if (null != dbobj.getString(FieldType.UDF1.toString())) {
							fields.put(FieldType.UDF1.getName(), dbobj.get(FieldType.UDF1.toString()).toString());
						}
					}

					acquirerType = fields.get(FieldType.ACQUIRER_TYPE.getName());
					if (acquirerType.equals(AcquirerType.PINELABS.getCode())) {
						if (null != dbobj.getString(FieldType.ADF1.toString())) {
							fields.put(FieldType.ADF1.getName(), dbobj.get(FieldType.ADF1.toString()).toString());
						}
					}

					if (acquirerType.equals(AcquirerType.CAMSPAY.getCode())) {
						User user = userDao.findPayId(fields.get(FieldType.PAY_ID.getName()));
						Set<Account> accounts = user.getAccounts() == null ? new HashSet<>() : user.getAccounts();
						List<Account> accountList = accounts.stream()
								.filter(accountsFromSet -> StringUtils.equalsIgnoreCase(
										accountsFromSet.getAcquirerName(),
										AcquirerType.getInstancefromCode(AcquirerType.CAMSPAY.getCode()).getName()))
								.collect(Collectors.toList());
						Account account = accountList != null && accountList.size() > 0 ? accountList.get(0) : null;
						AccountCurrency accountCurrency = account
								.getAccountCurrency(fields.get(FieldType.CURRENCY_CODE.getName()));
						fields.put(FieldType.MERCHANT_ID.getName(), accountCurrency.getMerchantId());
						fields.put(FieldType.PASSWORD.getName(), accountCurrency.getAdf2());
						fields.put(FieldType.TXN_KEY.getName(), accountCurrency.getTxnKey());
						fields.put(FieldType.ADF1.getName(), accountCurrency.getAdf1());
						fields.put(FieldType.ADF2.getName(), accountCurrency.getAdf2());
						fields.put(FieldType.ADF3.getName(), accountCurrency.getAdf3());
						fields.put(FieldType.ADF4.getName(), accountCurrency.getAdf4());
						fields.put(FieldType.ADF5.getName(), accountCurrency.getAdf5());
						fields.put(FieldType.ADF6.getName(), accountCurrency.getAdf6());
						fields.put(FieldType.ADF7.getName(), accountCurrency.getAdf7());
						fields.put(FieldType.ADF8.getName(), accountCurrency.getAdf8());
						fields.put(FieldType.ADF9.getName(), accountCurrency.getAdf9());
						fields.put(FieldType.ADF10.getName(), accountCurrency.getAdf10());
					}

				}

				return true;
			}

		} catch (Exception e) {
			logger.error("Exception while fetching transaction for status enquiry", e);
		}
		return false;
	}

	private Fields createAllForRefund(Fields field) {

		Fields fields = new Fields();

		try {
			BasicDBObject finalquery = new BasicDBObject(FieldType.PG_REF_NUM.getName(),
					field.get(FieldType.PG_REF_NUM.getName()));

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns
					.getCollection(propertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
			BasicDBObject match = new BasicDBObject("$match", finalquery);

			BasicDBObject sort = new BasicDBObject("$sort", new BasicDBObject("INSERTION_DATE", -1));
			BasicDBObject limit = new BasicDBObject("$limit", 1);

			List<BasicDBObject> pipeline = Arrays.asList(match, sort, limit);

			AggregateIterable<Document> output = coll.aggregate(pipeline);
			output.allowDiskUse(true);
			MongoCursor<Document> cursor = output.iterator();

			if (cursor.hasNext()) {
				Document documentObj = cursor.next();

				if (null != documentObj) {
					for (int j = 0; j < documentObj.size(); j++) {
						for (String columnName : aLLDB_Fields) {
							if (documentObj.get(columnName) != null) {
								fields.put(columnName, documentObj.get(columnName).toString());
							} else {

							}

						}
					}
				}
				fields.logAllFields("Previous fields");
			}
			cursor.close();
			return fields;
		} catch (Exception e) {
			logger.error("Exception while getting previous fields", e);
		}
		return null;

	}

	@SuppressWarnings("static-access")
	public void insertTransaction(Fields fields) throws SystemException {
		try {

			MongoDatabase dbIns = null;
			BasicDBObject newFieldsObj = new BasicDBObject();

			String amountString = fields.get(FieldType.AMOUNT.getName());
			String surchargeAmountString = fields.get(FieldType.SURCHARGE_AMOUNT.getName());
			String currencyString = fields.get(FieldType.CURRENCY_CODE.getName());
			String totalAmountString = fields.get(FieldType.TOTAL_AMOUNT.getName());

			String amount = "0";
			if (!StringUtils.isEmpty(amountString) && !StringUtils.isEmpty(currencyString)) {
				amount = Amount.toDecimal(amountString, currencyString);
				newFieldsObj.put(FieldType.AMOUNT.getName(), amount);
			}

			String totalAmount = "0";
			if (!StringUtils.isEmpty(totalAmountString) && !StringUtils.isEmpty(currencyString)) {
				totalAmount = Amount.toDecimal(totalAmountString, currencyString);
				newFieldsObj.put(FieldType.TOTAL_AMOUNT.getName(), totalAmount);
			}

			String surchargeAmount = "0";
			if (!StringUtils.isEmpty(surchargeAmountString) && !StringUtils.isEmpty(currencyString)) {
				surchargeAmount = Amount.toDecimal(surchargeAmountString, currencyString);
				newFieldsObj.put(FieldType.SURCHARGE_AMOUNT.getName(), surchargeAmount);
			}
			String origTxnId = "0";
			String origTxnStr = fields.get(FieldType.ORIG_TXN_ID.getName());
			if (StringUtils.isEmpty(origTxnStr)) {
				String internalOrigTxnStr = fields.get(FieldType.INTERNAL_ORIG_TXN_ID.getName());
				if (StringUtils.isEmpty(internalOrigTxnStr)) {
					newFieldsObj.put(FieldType.ORIG_TXN_ID.getName(), origTxnId);
				}
				if (!StringUtils.isEmpty(internalOrigTxnStr)) {
					newFieldsObj.put(FieldType.ORIG_TXN_ID.getName(), internalOrigTxnStr);
				}
			}

			if (!StringUtils.isEmpty(origTxnStr)) {
				newFieldsObj.put(FieldType.ORIG_TXN_ID.getName(), origTxnStr);
			}

			String origTxnType = fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName());
			if (!StringUtils.isEmpty(origTxnType)) {
				String txnType = fields.get(FieldType.TXNTYPE.getName());
				if ((txnType.equals(TransactionType.REFUND.getName()))
						|| (txnType.equals(TransactionType.REFUNDRECO.getName()))) {
					newFieldsObj.put(FieldType.ORIG_TXNTYPE.getName(), TransactionType.REFUND.getName());
				} else {
					newFieldsObj.put(FieldType.ORIG_TXNTYPE.getName(), origTxnType);
				}

			}
			String pgRefNo = "0";
			String pgRefNum = fields.get(FieldType.PG_REF_NUM.getName());
			if (StringUtils.isEmpty(pgRefNum)) {
				newFieldsObj.put(FieldType.PG_REF_NUM.getName(), pgRefNo);
			}

			if (!StringUtils.isEmpty(pgRefNum)) {
				newFieldsObj.put(FieldType.PG_REF_NUM.getName(), pgRefNum);
			}
			String acctId = "0";
			String acctIdStr = fields.get(FieldType.ACCT_ID.getName());
			if (acctIdStr != null && acctIdStr.length() > 0) {

				newFieldsObj.put(FieldType.ACCT_ID.getName(), acctIdStr);
			}
			if (acctIdStr == null) {
				newFieldsObj.put(FieldType.ACCT_ID.getName(), acctId);
			}
			String acqId = "0";
			String acqIdStr = fields.get(FieldType.ACQ_ID.getName());
			if (acqIdStr != null && acqIdStr.length() > 0) {
				newFieldsObj.put(FieldType.ACQ_ID.getName(), acqIdStr);
			}
			if (acqIdStr == null) {
				newFieldsObj.put(FieldType.ACQ_ID.getName(), acqId);
			}
			String oid = fields.get(FieldType.OID.getName());
			String longOid = "0";
			if (!StringUtils.isEmpty(oid)) {

				newFieldsObj.put(FieldType.OID.getName(), oid);
			}
			if (StringUtils.isEmpty(oid)) {
				newFieldsObj.put(FieldType.OID.getName(), longOid);
			}
			String udf1 = fields.get(FieldType.UDF1.getName());
			if (!StringUtils.isEmpty(udf1)) {
				newFieldsObj.put(FieldType.UDF1.getName(), udf1);
			}
			String udf2 = fields.get(FieldType.UDF2.getName());
			if (!StringUtils.isEmpty(udf2)) {
				newFieldsObj.put(FieldType.UDF2.getName(), udf2);
			}
			String udf3 = fields.get(FieldType.UDF3.getName());
			if (!StringUtils.isEmpty(udf3)) {
				newFieldsObj.put(FieldType.UDF3.getName(), udf3);
			}
			String udf4 = fields.get(FieldType.UDF4.getName());
			if (!StringUtils.isEmpty(udf4)) {
				newFieldsObj.put(FieldType.UDF4.getName(), udf4);
			}
			String udf5 = fields.get(FieldType.UDF5.getName());
			if (!StringUtils.isEmpty(udf5)) {
				newFieldsObj.put(FieldType.UDF5.getName(), udf5);
			}
			String udf6 = fields.get(FieldType.UDF6.getName());
			if (!StringUtils.isEmpty(udf6)) {
				newFieldsObj.put(FieldType.UDF6.getName(), udf6);
			}

			String paymentsRegion = fields.get(FieldType.PAYMENTS_REGION.getName());
			if (!StringUtils.isEmpty(paymentsRegion)) {
				newFieldsObj.put(FieldType.PAYMENTS_REGION.getName(), paymentsRegion);
			}

			String cardHolderType = fields.get(FieldType.CARD_HOLDER_TYPE.getName());
			if (!StringUtils.isEmpty(cardHolderType)) {
				newFieldsObj.put(FieldType.CARD_HOLDER_TYPE.getName(), cardHolderType);
			}

			String requestDate = fields.get(FieldType.REQUEST_DATE.getName());
			if (!StringUtils.isEmpty(requestDate)) {
				newFieldsObj.put(FieldType.REQUEST_DATE.getName(), requestDate);
			}

			String saleAmoiunt = fields.get(FieldType.SALE_AMOUNT.getName());
			if ((!StringUtils.isEmpty(saleAmoiunt) && !StringUtils.isEmpty(currencyString))) {
				saleAmoiunt = Amount.toDecimal(saleAmoiunt, currencyString);
				newFieldsObj.put(FieldType.SALE_AMOUNT.getName(), saleAmoiunt);
			}

			String totalSaleAmoiunt = fields.get(FieldType.SALE_TOTAL_AMOUNT.getName());
			if ((!StringUtils.isEmpty(totalSaleAmoiunt) && !StringUtils.isEmpty(currencyString))) {
				totalSaleAmoiunt = Amount.toDecimal(totalSaleAmoiunt, currencyString);
				newFieldsObj.put(FieldType.SALE_TOTAL_AMOUNT.getName(), totalSaleAmoiunt);
			}

			// Date createDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
			// .parse(fields.get(FieldType.CREATE_DATE.getName()));
			// Calendar calendar = Calendar.getInstance();
			// calendar.setTime(createDate);

			// calendar.add(Calendar.SECOND, 1);

			// Date later = calendar.getTime();
			// System.out.println(later);

			// DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			// String createDateUpdated = dateFormat.format(later);

			// Date dNow = new Date();

			Date currentDateTime = new Date();
			String dateNow = DateCreater.formatDateForDb(currentDateTime);

			for (int i = 0; i < fields.size(); i++) {

				for (String columnName : aLLDB_Fields) {

					if (columnName.equals(FieldType.CREATE_DATE.getName())) {
						newFieldsObj.put(columnName, dateNow);
					} else if (columnName.equals(FieldType.DATE_INDEX.getName())) {
						newFieldsObj.put(columnName, dateNow.substring(0, 10).replace("-", ""));
					} else if (columnName.equals(FieldType.UPDATE_DATE.getName())) {
						newFieldsObj.put(columnName, dateNow);
					} else if (columnName.equals("_id")) {
						newFieldsObj.put(columnName, fields.get(FieldType.TXN_ID.getName()));
					} else if (columnName.equals(FieldType.INSERTION_DATE.getName())) {
						newFieldsObj.put(columnName, currentDateTime);
					} else if (columnName.equals(FieldType.ACQUIRER_TDR_SC.getName())) {
						newFieldsObj.put(columnName, fields.get(FieldType.ACQUIRER_TDR_SC.getName()));
					} else if (columnName.equals(FieldType.ACQUIRER_GST.getName())) {
						newFieldsObj.put(columnName, fields.get(FieldType.ACQUIRER_GST.getName()));
					} else if (columnName.equals(FieldType.PG_TDR_SC.getName())) {
						newFieldsObj.put(columnName, fields.get(FieldType.PG_TDR_SC.getName()));
					} else if (columnName.equals(FieldType.PG_GST.getName())) {
						newFieldsObj.put(columnName, fields.get(FieldType.PG_GST.getName()));
					} else if (columnName.equals(FieldType.AMOUNT.getName())) {
						continue;
					} else if (columnName.equals(FieldType.TOTAL_AMOUNT.getName())) {
						continue;
					} else if (columnName.equals(FieldType.SURCHARGE_AMOUNT.getName())) {
						continue;
					} else if (columnName.equals(FieldType.ORIG_TXN_ID.getName())) {
						continue;
					} else if (columnName.equals(FieldType.PG_REF_NUM.getName())) {
						continue;
					} else if (columnName.equals(FieldType.ACCT_ID.getName())) {
						continue;
					} else if (columnName.equals(FieldType.ACQ_ID.getName())) {
						continue;
					} else if (columnName.equals(FieldType.OID.getName())) {
						continue;
					} else if (columnName.equals(FieldType.UDF1.getName())) {
						continue;
					} else if (columnName.equals(FieldType.UDF2.getName())) {
						continue;
					} else if (columnName.equals(FieldType.UDF3.getName())) {
						continue;
					} else if (columnName.equals(FieldType.UDF4.getName())) {
						continue;
					} else if (columnName.equals(FieldType.UDF5.getName())) {
						continue;
					} else if (columnName.equals(FieldType.UDF6.getName())) {
						continue;
					} else if (columnName.equals(FieldType.PAYMENTS_REGION.getName())) {
						continue;
					} else if (columnName.equals(FieldType.CARD_HOLDER_TYPE.getName())) {
						continue;
					} else if (columnName.equals(FieldType.SALE_AMOUNT.getName())) {
						continue;
					} else if (columnName.equals(FieldType.SALE_TOTAL_AMOUNT.getName())) {
						continue;
					} else if (columnName.equals(FieldType.ORIG_TXNTYPE.getName())) {
						continue;
					} else {
						newFieldsObj.put(columnName, fields.get(columnName));
					}
				}
			}

			dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns
					.getCollection(propertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
			Document doc = new Document(newFieldsObj);
			if (StringUtils.isBlank(doc.getString(FieldType.INTERNAL_CUST_IP.getName()))) {
				doc.put(FieldType.INTERNAL_CUST_IP.getName(),
						fieldsDao.getIPFromInitiateRequest(doc.getString(FieldType.OID.getName())));
			}
			collection.insertOne(doc);

			// Change by Shaiwal , whole transaction status update block moved inside a
			// separate try/catch
			try {
				logger.info("calling update service for order id " + fields.get(FieldType.ORDER_ID.getName()));
				Runnable runnable = new Runnable() {
					public void run() {
						logger.info("Updating Status Collection for Order Id after Status Enquiry "
								+ fields.get(FieldType.ORDER_ID.getName()));
						updateStatusColl(fields, doc);
					}
				};

				Thread thread = new Thread(runnable);
				thread.start();
			} catch (Exception e) {
				logger.error("Txn Status Collection failed after status enquiry for Order Id "
						+ fields.get(FieldType.ORDER_ID.getName()));
				logger.error("Exception in updating Order status", e);

				if (StringUtils.isNotBlank(PropertiesManager.propertiesMap.get("AllowTxnUpdtFailAlert"))
						&& PropertiesManager.propertiesMap.get("AllowTxnUpdtFailAlert").equalsIgnoreCase("Y")) {

					smsCount = smsCount + 1;
					if (smsCount % 10 == 0) {

						StringBuilder smsBody = new StringBuilder();
						smsBody.append("Pay10 Alert !");
						smsBody.append("Transaction Status not updated for Order Id = "
								+ fields.get(FieldType.ORDER_ID.getName()));

						String smsSenderList = PropertiesManager.propertiesMap.get("smsAlertList");

						for (String mobile : smsSenderList.split(",")) {
							try {
								smsSender.sendSMS(mobile, smsBody.toString());
							} catch (IOException ioEx) {
								logger.error("SMS not sent for transaction status update failure when account is null",
										ioEx);
							}
						}

					}

				}

			}

			logger.info("Just for test " + fields.get(FieldType.ORDER_ID.getName()));
		} catch (Exception exception) {
			String message = "Error while inserting transaction in database";
			logger.error(message, exception);
			throw new SystemException(ErrorType.DATABASE_ERROR, exception, message);
		}
	}

	// New method to only show status enquiry result without updating the database,
	// this will respond even if status was success previously
	// No HASH required for this transaction

	@SuppressWarnings("incomplete-switch")
	public Map<String, String> getStatus(Fields fields) throws SystemException {

		boolean iSTxnFound = getTransactionFields(fields);

		if (StringUtils.isBlank(fields.get(FieldType.ACQUIRER_TYPE.getName()))) {

			logger.info(
					"Acquirer not found for status enquiry , pgRef == " + fields.get(FieldType.PG_REF_NUM.getName()));
			fields.put(FieldType.STATUS.getName(), StatusType.INVALID.getName());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), "No Such Transaction found");
			fields.put(FieldType.PG_TXN_MESSAGE.getName(), "No Such Transaction found");

			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.NO_SUCH_TRANSACTION.getCode());
			return fields.getFields();
		}

		if (!iSTxnFound) {

			logger.info("Transaction not found for status enquiry , pgRef == "
					+ fields.get(FieldType.PG_REF_NUM.getName()));
			fields.put(FieldType.STATUS.getName(), StatusType.INVALID.getName());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), "No Such Transaction found");
			fields.put(FieldType.PG_TXN_MESSAGE.getName(), "No Such Transaction found");

			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.NO_SUCH_TRANSACTION.getCode());
			return fields.getFields();
		}
		securityProcessor.addAcquirerFields(fields);

		AcquirerType acquirerType = AcquirerType.getInstancefromCode(fields.get(FieldType.ACQUIRER_TYPE.getName()));
		switch (acquirerType) {
			case BOB:
				bobStatusEnquiryProcessor.enquiryProcessor(fields);
				break;
			case FSS:
				hdfcUpiStatusEnquiryProcessor.enquiryProcessor(fields); // HDFC UPI
				break;
			case DIRECPAY:
				direcpayStatusEnquiryProcessor.enquiryProcessor(fields); // Direcpay
				break;
			case ISGPAY:
				iSGPayStatusEnquiryProcessor.enquiryProcessor(fields); // ISGPay
				break;
			case AXISBANK:
				if (fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(PaymentType.NET_BANKING.getCode())) {
					axisBankNBStatusEnquiryProcessor.enquiryProcessor(fields); // Axis Bank Net Banking
				} else if (fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(PaymentType.UPI.getCode())) {
					axisBankUpiStatusEnquiryProcessor.enquiryProcessor(fields); // Axis Bank UPI
				} else {
					axisBankCardStatusEnquiryProcessor.enquiryProcessor(fields); // Axis Bank Cards
				}
				break;
			case CANARANBBANK:
				canaraNBProcessor.enquiryProcessor(fields);//CANARA NB
				//canaraNBProcessor.statusEnquiry(fields,false);//CANARA NB
				break;
			case ATOM:
				atomStatusEnquiryProcessor.enquiryProcessor(fields); // ATOM
				break;

			case APBL:
				apblStatusEnquiryProcessor.enquiryProcessor(fields); // APBL
				break;

			case INGENICO:
				ingenicoStatusEnquiryProcessor.enquiryProcessor(fields); // INGENICO
				break;

			case PAYTM:
				paytmStatusEnquiryProcessor.enquiryProcessor(fields); // Paytm
				break;

			case PAYU:
				payuStatusEnquiryProcessor.enquiryProcessor(fields); // Paytm
				break;

			case MOBIKWIK:
				mobikwikStatusEnquiryProcessor.enquiryProcessor(fields); // Mobikwik
				break;

			case PHONEPE:
				phonePeStatusEnquiryProcessor.enquiryProcessor(fields); // PhonePe
				break;
			case CAMSPAY:
				camsPayStatusEnquiryProcessor.enquiryProcessor(fields);
				break;
			case TMBNB:
				tmbnbProcessor.enquiryStatus(fields);//tmb NB added by vijay
				break;
			default:

				Map<String, String> failedTxnField = fields.getFields();
				failedTxnField.put(FieldType.STATUS.getName(), "");
				fields.removeSecureFields();
				return fields.getFields();
		}

		fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), fields.get(FieldType.TXNTYPE.getName()));
		fields.removeSecureFields();

		return fields.getFields();
	}

	public void updateStatusColl(Fields fields, Document document) {

		try {

			logger.info("Request Received for updateStatusColl ");
			logger.info("fields " + fields.getFieldsAsString());
			logger.info("document " + document.toJson());

			logger.info("Transaction Status :: " + document.get(FieldType.STATUS.getName()).toString());
			logger.info("Router Configuration Id :: " + fields.get(FieldType.ROUTER_CONFIGURATION_ID.getName()));

			/*
			 * //added by sonu for smart routing merchant payment release if
			 * (fields.get(FieldType.ROUTER_CONFIGURATION_ID.getName()) != null &&
			 * (document.get(FieldType.STATUS.getName()).toString().equalsIgnoreCase(
			 * "Declined") ||
			 * document.get(FieldType.STATUS.getName()).toString().equalsIgnoreCase(
			 * "FAILED") ||
			 * document.get(FieldType.STATUS.getName()).toString().equalsIgnoreCase(
			 * "Cancelled") || document.get(FieldType.STATUS.getName()).toString().
			 * equalsIgnoreCase("Failed at Acquirer") ||
			 * document.get(FieldType.STATUS.getName()).toString().equalsIgnoreCase(
			 * "Invalid") || document.get(FieldType.STATUS.getName()).toString().
			 * equalsIgnoreCase("User Inactive") ||
			 * document.get(FieldType.STATUS.getName()).toString().equalsIgnoreCase(
			 * "Rejected"))) {
			 *
			 *
			 *
			 * //!(document.get(FieldType.STATUS.getName()).toString().equalsIgnoreCase(
			 * "Captured"))) {
			 *
			 * //&& (document.get(FieldType.STATUS.getName()).toString().equalsIgnoreCase(
			 * "Declined") // ||
			 * document.get(FieldType.STATUS.getName()).toString().equalsIgnoreCase("FAILED"
			 * ))) {
			 *
			 * // TODO Call Method to release merchant freeze amount. logger.
			 * info("Request received to release merchant freeze amount through Scheduler."
			 * );
			 *
			 * routerConfigurationDao.freezeRouterConfigurationMaxAmountById(
			 * Double.valueOf(document.get(FieldType.TOTAL_AMOUNT.getName()).toString()),
			 * Long.valueOf(fields.get(FieldType.ROUTER_CONFIGURATION_ID.getName()))); }
			 */

			// added by sonu for smart routing merchant payment release
			if (fields.get(FieldType.ROUTER_CONFIGURATION_ID.getName()) != null
					&& (document.get(FieldType.STATUS.getName()).toString().equalsIgnoreCase("Captured"))) {

				logger.info("Request received to Freeze merchant amount through Scheduler.");

				routerConfigurationDao.freezeRouterConfigurationMaxAmountById(
						Double.valueOf(document.get(FieldType.TOTAL_AMOUNT.getName()).toString()),
						Long.valueOf(fields.get(FieldType.ROUTER_CONFIGURATION_ID.getName())));
			}

			MongoDatabase dbIns = mongoInstance.getDB();

			String orderId = document.get(FieldType.ORDER_ID.getName()).toString();
			String oid = document.get(FieldType.OID.getName()).toString();
			String origTxnType = fields.get(FieldType.ORIG_TXNTYPE.getName());
			String pgRefNum = fields.get(FieldType.PG_REF_NUM.getName());
			String txnType = fields.get(FieldType.TXNTYPE.getName());

			if (StringUtils.isBlank(txnType)) {
				txnType = TransactionType.SALE.getName();
			}

			if (txnType.equalsIgnoreCase(TransactionType.INVALID.getName())
					|| txnType.equalsIgnoreCase(TransactionType.NEWORDER.getName())
					|| txnType.equalsIgnoreCase(TransactionType.RECO.getName())
					|| txnType.equalsIgnoreCase(TransactionType.ENROLL.getName())
					|| txnType.equalsIgnoreCase(TransactionType.STATUS.getName())) {
				txnType = TransactionType.SALE.getName();
			}

			if (txnType.equalsIgnoreCase(TransactionType.REFUND.getName())
					|| txnType.equalsIgnoreCase(TransactionType.REFUNDRECO.getName())) {
				txnType = TransactionType.REFUND.getName();
			}

			if (StringUtils.isBlank(origTxnType)) {
				origTxnType = txnType;
			}

			if (origTxnType.equalsIgnoreCase(TransactionType.INVALID.getName())) {
				origTxnType = TransactionType.SALE.getName();
			}

			if (origTxnType.equalsIgnoreCase(TransactionType.STATUS.getName())) {
				origTxnType = TransactionType.SALE.getName();
			}

			origTxnType = txnType;

			// SALE
			if (origTxnType.equalsIgnoreCase(TransactionType.SALE.getName())) {
				if (StringUtils.isBlank(orderId) || StringUtils.isBlank(oid) || StringUtils.isBlank(origTxnType)) {

					logger.info("Cannot update transaction status collection for combination " + " Order Id = "
							+ orderId + " OID = " + oid + " orig Txn Type = " + origTxnType);
					logger.info("Txn cannot be added , moving to transactionStatusException ");

					Document doc = new Document();
					doc.put(FieldType.ORDER_ID.getName(), orderId);
					doc.put(FieldType.OID.getName(), oid);
					doc.put(FieldType.ORIG_TXNTYPE.getName(), origTxnType);

					MongoCollection<Document> excepColl = dbIns.getCollection(PropertiesManager.propertiesMap
							.get(prefix + Constants.TRANSACTION_STATUS_EXCEP_COLLECTION.getValue()));

					excepColl.insertOne(doc);
				} else {

					MongoCollection<Document> coll = dbIns.getCollection(propertiesManager.propertiesMap
							.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));

					List<BasicDBObject> dbObjList = new ArrayList<BasicDBObject>();
					dbObjList.add(new BasicDBObject(FieldType.ORDER_ID.getName(), orderId));
					dbObjList.add(new BasicDBObject(FieldType.OID.getName(), oid));

					dbObjList.add(new BasicDBObject(FieldType.ORIG_TXNTYPE.getName(), origTxnType));

					BasicDBObject andQuery = new BasicDBObject("$and", dbObjList);

					FindIterable<Document> cursor = coll.find(andQuery);

					if (cursor.iterator().hasNext()) {

						String transactionStatusFields = PropertiesManager.propertiesMap.get("TransactionStatusFields");
						String transactionStatusFieldsArr[] = transactionStatusFields.split(",");

						MongoCollection<Document> txnStatusColl = dbIns.getCollection(propertiesManager.propertiesMap
								.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));

						BasicDBObject searchQuery = andQuery;
						BasicDBObject updateFields = new BasicDBObject();

						String status = fields.get(FieldType.STATUS.getName());
						String statusALias = resolveStatus(status);

						// Added to avoid exception in report
						if (StringUtils.isBlank(statusALias)) {
							logger.info("Alias Status not resolved for order Id = " + orderId);
							statusALias = "Cancelled";
						}

						logger.info("StatausEnquiryProcessor : logs : " + document.toJson());
						
						for (String key : transactionStatusFieldsArr) {

//							if (status.equalsIgnoreCase(StatusType.FAILED.getName())) {

								if ((key.equalsIgnoreCase(FieldType.DATE_INDEX.getName()))
										|| (key.equalsIgnoreCase(FieldType.CREATE_DATE.getName()))
										|| (key.equalsIgnoreCase(FieldType.UPDATE_DATE.getName()))
										|| (key.equalsIgnoreCase(FieldType.INSERTION_DATE.getName()))
										|| (key.equalsIgnoreCase(FieldType.TXNTYPE.getName()))) {
									continue;
								}

//								if ((key.equalsIgnoreCase(FieldType.SETTLEMENT_DATE.getName()))) {
//									updateFields.put(key, document.get(FieldType.CREATE_DATE.getName()));
//									continue;
//								}
//
//								if ((key.equalsIgnoreCase(FieldType.SETTLEMENT_FLAG.getName()))) {
//									updateFields.put(key, "Y");
//									continue;
//								}
//
//								if ((key.equalsIgnoreCase(FieldType.SETTLEMENT_DATE_INDEX.getName()))) {
//									updateFields.put(key, document.get(FieldType.DATE_INDEX.getName()));
//									continue;
//								}

//							}

							if (document.get(key) != null) {
								updateFields.put(key, document.get(key).toString());
							} else {
								updateFields.put(key, document.get(key));
							}

						}

						updateFields.put(FieldType.ALIAS_STATUS.getName(), statusALias);
						updateFields.put(FieldType.ORIG_TXNTYPE.getName(), origTxnType);
						txnStatusColl.updateOne(searchQuery, new BasicDBObject("$set", updateFields));

					} else {

						MongoCollection<Document> txnStatusColl = dbIns.getCollection(propertiesManager.propertiesMap
								.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));

						String status = fields.get(FieldType.STATUS.getName());
						String statusALias = resolveStatus(status);

						// Added to avoid exception in report
						if (StringUtils.isBlank(statusALias)) {
							logger.info("Alias Status not resolved for order Id = " + orderId);
							statusALias = "Cancelled";
						}

						document.put(FieldType.ALIAS_STATUS.getName(), statusALias);
						document.put(FieldType.ORIG_TXNTYPE.getName(), origTxnType);
						txnStatusColl.insertOne(document);

					}

				}
			}

			// REFUND

			if (origTxnType.equalsIgnoreCase(TransactionType.REFUND.getName())) {
				if (StringUtils.isBlank(pgRefNum) || StringUtils.isBlank(oid) || StringUtils.isBlank(origTxnType)) {

					logger.info("Cannot update transaction status collection for combination " + " PG REF NUM = "
							+ pgRefNum + " OID = " + oid + " orig Txn Type = " + origTxnType);
					logger.info("Txn cannot be added , moving to transactionStatusException ");

					Document doc = new Document();
					doc.put(FieldType.PG_REF_NUM.getName(), pgRefNum);
					doc.put(FieldType.OID.getName(), oid);
					doc.put(FieldType.ORIG_TXNTYPE.getName(), origTxnType);

					MongoCollection<Document> excepColl = dbIns.getCollection(PropertiesManager.propertiesMap
							.get(prefix + Constants.TRANSACTION_STATUS_EXCEP_COLLECTION.getValue()));

					excepColl.insertOne(doc);
				} else {

					MongoCollection<Document> coll = dbIns.getCollection(propertiesManager.propertiesMap
							.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));

					List<BasicDBObject> dbObjList = new ArrayList<BasicDBObject>();
					dbObjList.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pgRefNum));
					dbObjList.add(new BasicDBObject(FieldType.OID.getName(), oid));
					dbObjList.add(new BasicDBObject(FieldType.ORIG_TXNTYPE.getName(), origTxnType));

					BasicDBObject andQuery = new BasicDBObject("$and", dbObjList);

					FindIterable<Document> cursor = coll.find(andQuery);

					if (cursor.iterator().hasNext()) {

						String transactionStatusFields = PropertiesManager.propertiesMap.get("TransactionStatusFields");
						String transactionStatusFieldsArr[] = transactionStatusFields.split(",");

						MongoCollection<Document> txnStatusColl = dbIns.getCollection(propertiesManager.propertiesMap
								.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));

						BasicDBObject searchQuery = andQuery;
						BasicDBObject updateFields = new BasicDBObject();

						for (String key : transactionStatusFieldsArr) {

							if (document.get(key) != null) {
								updateFields.put(key, document.get(key).toString());
							} else {
								updateFields.put(key, document.get(key));
							}

						}

						String status = fields.get(FieldType.STATUS.getName());
						String statusALias = resolveStatus(status);
						updateFields.put(FieldType.ALIAS_STATUS.getName(), statusALias);
						updateFields.put(FieldType.ORIG_TXNTYPE.getName(), origTxnType);
						txnStatusColl.updateOne(searchQuery, new BasicDBObject("$set", updateFields));

					} else {

						MongoCollection<Document> txnStatusColl = dbIns.getCollection(propertiesManager.propertiesMap
								.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));

						String status = fields.get(FieldType.STATUS.getName());
						String statusALias = resolveStatus(status);
						document.put(FieldType.ALIAS_STATUS.getName(), statusALias);
						document.put(FieldType.ORIG_TXNTYPE.getName(), origTxnType);
						txnStatusColl.insertOne(document);

					}

				}
			}

		} catch (Exception e) {
			logger.error("Exception in adding txn to transaction status", e);
		}

	}

	public String resolveStatus(String status) {

		if (StringUtils.isBlank(status)) {
			return status;
		} else {
			if (status.equals(StatusType.CAPTURED.getName())) {
				return "Captured";

			} else if (status.equals(StatusType.SETTLED_SETTLE.getName())) {
				return "Settled";

			} else if (status.equals(StatusType.PENDING.getName()) || status.equals(StatusType.SENT_TO_BANK.getName())
					|| status.equals(StatusType.ENROLLED.getName())) {
				return "Pending";

			} else if (status.equals(StatusType.BROWSER_CLOSED.getName())
					|| status.equals(StatusType.CANCELLED.getName())
					|| status.equals(StatusType.USER_INACTIVE.getName())) {
				return "Cancelled";

			} else if (status.equals(StatusType.INVALID.getName()) || status.equals(StatusType.DUPLICATE.getName())) {
				return "Invalid";

			} else {
				return "Failed";
			}

		}
	}

	@SuppressWarnings("incomplete-switch")
	public Map<String, String> getBilldeskUpiStatus(Fields fields) throws SystemException {

		getTransactionFields(fields);
		securityProcessor.addAcquirerFields(fields);

		billdeskStatusEnquiryProcessor.enquiryProcessor(fields);

		fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), TransactionType.SALE.getName());
		fields.removeSecureFields();

		logger.info("Status in fields after billdesk status enquiry == " + fields.get(FieldType.STATUS.getName())
				+ "  PG REF  ==  " + fields.get(FieldType.PG_REF_NUM.getName()));
		// If Transaction is not "Processing" from billdesk upi , update this in the
		// Database
		if (StringUtils.isNotBlank(fields.get(FieldType.STATUS.getName()))
				&& !fields.get(FieldType.STATUS.getName()).equalsIgnoreCase(StatusType.PROCESSING.getName())) {

			process(fields);

		}
		return fields.getFields();
	}

	@SuppressWarnings("incomplete-switch")
	public Map<String, String> getFreeChargeUpiStatus(Fields fields) throws SystemException {

		getTransactionFields(fields);
		securityProcessor.addAcquirerFields(fields);

		freeChargeStatusEnquiryProcessor.enquiryProcessor(fields);

		fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), TransactionType.SALE.getName());
		fields.removeSecureFields();

		logger.info("Status in fields after Freecharge status enquiry == " + fields.get(FieldType.STATUS.getName())
				+ "  PG REF  ==  " + fields.get(FieldType.PG_REF_NUM.getName()));
		// If Transaction is not "Processing" from Freecharge upi , update this in the
		// Database
		if (StringUtils.isNotBlank(fields.get(FieldType.STATUS.getName()))
				&& !fields.get(FieldType.STATUS.getName()).equalsIgnoreCase(StatusType.PROCESSING.getName())) {

			process(fields);

		}
		return fields.getFields();
	}
}
