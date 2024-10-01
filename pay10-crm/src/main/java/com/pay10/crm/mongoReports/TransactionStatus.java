package com.pay10.crm.mongoReports;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.joda.time.Days;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.pay10.commons.dao.AcquirerNodalMappingDao;
import com.pay10.commons.dao.HibernateSessionProvider;
import com.pay10.commons.dao.ServiceTaxDao;
import com.pay10.commons.entity.AcqurerNodalMapping;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.user.Account;
import com.pay10.commons.user.AccountCurrencyRegion;
import com.pay10.commons.user.BankSettle;
import com.pay10.commons.user.BankSettleDao;
import com.pay10.commons.user.ChargingDetails;
import com.pay10.commons.user.ChargingDetailsDao;
import com.pay10.commons.user.MISReportObject;
import com.pay10.commons.user.TDRBifurcationReportDetails;
import com.pay10.commons.user.TdrSetting;
import com.pay10.commons.user.TdrSettingDao;
import com.pay10.commons.user.TransactionSearchNew;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.MopType;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;
import com.pay10.crm.actionBeans.TransactionStatusBean;

@Component
public class TransactionStatus {
	@Autowired
	private MongoInstance mongoInstance;

	@Autowired
	private UserDao userdao;
	@Autowired
	private ServiceTaxDao serviceTaxDao;

	@Autowired
	private TdrSettingDao tdrSettingDao;

	@Autowired
	private UserDao userDao;
	@Autowired
	private AcquirerNodalMappingDao acquirerNodalMappingDao;
	private static Map<String, User> userMap = new HashMap<String, User>();
	@Autowired
	private TdrSettingDao dao;

	@Autowired
	private ChargingDetailsDao chargingDetailsDao;
	private static final String prefix = "MONGO_DB_";
	private static Logger logger = LoggerFactory.getLogger(TransactionStatus.class.getName());
	@Autowired
	private BankSettleDao bankSettleDao;

	public String[] getDateBetween(String date_range) {
		// 07/06/2022 - 06/07/2022
		String[] date = new String[2];
		String splitdate[] = date_range.split(" ");
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
			SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
			date[0] = dateFormat1.format(dateFormat.parse(splitdate[0]));
			date[1] = dateFormat1.format(dateFormat.parse(splitdate[2]));

		} catch (Exception e) {
			logger.info("Exception Occur in class TransactionStatus in getDateBetween(): " + e);
			e.printStackTrace();
		}
		return date;
	}
	public int getTDRBurificationReportCount(String payId, String status, String acquirer,
			String dateFrom, String dateTo, String settlementDateFrom, String settlementDateTo) {
		List<TDRBifurcationReportDetails> listToReturn = new ArrayList<TDRBifurcationReportDetails>();
		Session session = HibernateSessionProvider.getSession();

		try {

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));
			MongoCursor<Document> cursor = null;
			BasicDBObject match = new BasicDBObject();

			if (!StringUtils.isBlank(dateFrom) && !StringUtils.isBlank(dateTo)) {
				match.put(FieldType.DATE_INDEX.getName(),
						new Document("$gte", dateFrom.replaceAll("-", "")).append("$lte", dateTo.replaceAll("-", "")));
				if (!status.equalsIgnoreCase("All")) {
					if (status.equalsIgnoreCase("1") || status.equalsIgnoreCase("0")) {
						match.put(FieldType.HOLD_RELEASE.getName(), Integer.parseInt(status));
					} else {
						match.put(FieldType.STATUS.getName(), status);
					}
				} else {
					match.put(FieldType.STATUS.getName(),
							new Document("$in",
									Arrays.asList(StatusType.CAPTURED.getName(),
											StatusType.SETTLED_RECONCILLED.getName(),
											StatusType.SETTLED_SETTLE.getName(), StatusType.FORCE_CAPTURED.getName())));
				}
			}
			if (!StringUtils.isBlank(settlementDateFrom) && !StringUtils.isBlank(settlementDateTo)) {
				match.put(FieldType.SETTLEMENT_DATE_INDEX.getName(),
						new Document("$gte", settlementDateFrom.replaceAll("-", "")).append("$lte",
								settlementDateTo.replaceAll("-", "")));

				match.put(FieldType.STATUS.getName(), StatusType.SETTLED_SETTLE.getName());

			}

			if (!acquirer.equalsIgnoreCase("All")) {
				match.put(FieldType.ACQUIRER_TYPE.getName(), acquirer);
			}
			if (!payId.equalsIgnoreCase("All")) {
				match.put(FieldType.PAY_ID.getName(), payId);
			}

			List<BasicDBObject> queryExecute = Arrays.asList(new BasicDBObject("$match", match));
			logger.info(" TransactionStatus getTDRBurificationReport() Query: \t" + queryExecute);
			return coll.aggregate(queryExecute).into(new ArrayList<>()).size();
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(" Exception Occur in class TransactionStatus in getTDRBurificationReport(): ");

			return 0;
		} finally {
			session.close();
		}

	}
	public List<TDRBifurcationReportDetails> getTDRBurificationReport(String payId, String status, String acquirer,
			String dateFrom, String dateTo, String settlementDateFrom, String settlementDateTo,int start, int length) {
		List<TDRBifurcationReportDetails> listToReturn = new ArrayList<TDRBifurcationReportDetails>();
		Session session = HibernateSessionProvider.getSession();

		try {

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));
			MongoCursor<Document> cursor = null;
			BasicDBObject match = new BasicDBObject();

			if (!StringUtils.isBlank(dateFrom) && !StringUtils.isBlank(dateTo)) {
				match.put(FieldType.DATE_INDEX.getName(),
						new Document("$gte", dateFrom.replaceAll("-", "")).append("$lte", dateTo.replaceAll("-", "")));
				if (!status.equalsIgnoreCase("All")) {
					if (status.equalsIgnoreCase("1") || status.equalsIgnoreCase("0")) {
						match.put(FieldType.HOLD_RELEASE.getName(), Integer.parseInt(status));
					} else {
						match.put(FieldType.STATUS.getName(), status);
					}
				} else {
					match.put(FieldType.STATUS.getName(),
							new Document("$in",
									Arrays.asList(StatusType.CAPTURED.getName(),
											StatusType.SETTLED_RECONCILLED.getName(),
											StatusType.SETTLED_SETTLE.getName(), StatusType.FORCE_CAPTURED.getName())));
				}
			}
			if (!StringUtils.isBlank(settlementDateFrom) && !StringUtils.isBlank(settlementDateTo)) {
				match.put(FieldType.SETTLEMENT_DATE_INDEX.getName(),
						new Document("$gte", settlementDateFrom.replaceAll("-", "")).append("$lte",
								settlementDateTo.replaceAll("-", "")));

				match.put(FieldType.STATUS.getName(), StatusType.SETTLED_SETTLE.getName());

			}

			if (!acquirer.equalsIgnoreCase("All")) {
				match.put(FieldType.ACQUIRER_TYPE.getName(), acquirer);
			}
			if (!payId.equalsIgnoreCase("All")) {
				match.put(FieldType.PAY_ID.getName(), payId);
			}

			BasicDBObject matchQ = new BasicDBObject("$match", match);
			BasicDBObject sort = new BasicDBObject("$sort", new BasicDBObject("CREATE_DATE", -1));
			BasicDBObject skip = new BasicDBObject("$skip", start);
			BasicDBObject limit = new BasicDBObject("$limit", length);
			List<BasicDBObject> queryExecute = Arrays.asList(matchQ, sort, skip, limit);

			logger.info(" TransactionStatus getTDRBurificationReport() Query: \t" + queryExecute);
			cursor = coll.aggregate(queryExecute).iterator();

			while (cursor.hasNext()) {
				double igst = 0.0;
				double csgst = 0.0;
				Document doc = (Document) cursor.next();

				TDRBifurcationReportDetails bifurcationReportDetails = new TDRBifurcationReportDetails();

				User user = new User();

				String acqType = doc.getString(FieldType.ACQUIRER_TYPE.toString());

				user = userdao.findPayId(doc.getString(FieldType.PAY_ID.toString()));

				Account acc = user.getAccountUsingAcquirerCode(acqType);
				String mid = null;
				String currency = doc.getString(FieldType.CURRENCY_CODE.toString());
				if (acc != null)
					mid = acc.getAccountCurrency(currency).getMerchantId();
				bifurcationReportDetails.setTxnId(doc.getString(FieldType.TXN_ID.toString()));
				bifurcationReportDetails.setPgRefNo(doc.getString(FieldType.PG_REF_NUM.toString()));
				bifurcationReportDetails.setMerchantName(user.getBusinessName());
				bifurcationReportDetails.setAcquirer(doc.getString(FieldType.ACQUIRER_TYPE.toString()));
				bifurcationReportDetails.setDate(doc.getString(FieldType.CREATE_DATE.toString()));
				bifurcationReportDetails.setOrderId(doc.getString(FieldType.ORDER_ID.toString()));
				bifurcationReportDetails.setPaymentMethod(doc.getString(FieldType.PAYMENT_TYPE.toString()));
				bifurcationReportDetails.setTxnType(doc.getString(FieldType.TXNTYPE.toString()));
				bifurcationReportDetails.setStatus(doc.getString(FieldType.STATUS.toString()));
				bifurcationReportDetails.setTransactionRegion(doc.getString(FieldType.PAYMENTS_REGION.toString()));

				bifurcationReportDetails.setTotalAmount((doc.getString(FieldType.TOTAL_AMOUNT.toString()) != null)
						? doc.getString(FieldType.TOTAL_AMOUNT.toString())
						: "0.0");
				// bifurcationReportDetails.setTotalAmount(doc.getString(FieldType.TOTAL_AMOUNT.toString()));
				bifurcationReportDetails.setDeltaRefundFlag(doc.getString(FieldType.DELTA_REFUND_FLAG.toString()));
				bifurcationReportDetails.setAcqId(mid);
				// bifurcationReportDetails.setAcqId(doc.getString(FieldType.ACQ_ID.toString()));
				bifurcationReportDetails.setRRN(doc.getString(FieldType.RRN.toString()));
				bifurcationReportDetails.setPostSettledFlag(doc.getString(FieldType.POST_SETTLED_FLAG.toString()));
				bifurcationReportDetails.setRefundOrderId(doc.getString(FieldType.REFUND_ORDER_ID.toString()));
				bifurcationReportDetails.setRefundFlag(doc.getString(FieldType.REFUND_FLAG.toString()));

				bifurcationReportDetails
						.setMopType(MopType.getmop(doc.getString(FieldType.MOP_TYPE.toString())).toString());

				bifurcationReportDetails.setBaseAmount(doc.getString(FieldType.AMOUNT.getName()));

				bifurcationReportDetails.setSurchargeFlag(doc.getString(FieldType.SURCHARGE_FLAG.getName()) != null
						? doc.getString(FieldType.SURCHARGE_FLAG.getName())
						: "N");

				String payID = doc.getString(FieldType.PAY_ID.getName());
				String paymentType = doc.getString(FieldType.PAYMENT_TYPE.getName());
				String acquirerType = doc.getString(FieldType.ACQUIRER_TYPE.getName());
				String mopType = doc.getString(FieldType.MOP_TYPE.getName());
				String transactionType = doc.getString(FieldType.TXNTYPE.getName());
				String amount = doc.getString(FieldType.AMOUNT.getName());
				String date = doc.getString(FieldType.CREATE_DATE.getName());
				String paymentRegion = doc.getString(FieldType.PAYMENTS_REGION.getName());
				String cardHolderType = doc.getString(FieldType.CARD_HOLDER_TYPE.getName());

				if (StringUtils.isBlank(paymentRegion)) {
					paymentRegion = "DOMESTIC";
				}
				if (StringUtils.isBlank(cardHolderType)) {
					cardHolderType = "CONSUMER";
				}

				TdrSetting setting = dao.getTdrAndSurchargeAfterTransaction(payID,
						PaymentType.getInstanceUsingCode(paymentType).toString(), acquirerType,
						MopType.getInstanceUsingCode(mopType).toString(), currency, amount, date,
						paymentRegion, cardHolderType);

				if (setting != null) {
					bifurcationReportDetails.setMerchantPreference(setting.getMerchantPreference());
					bifurcationReportDetails.setMerchantTDR(String.valueOf(new BigDecimal(setting.getMerchantTdr())));
					bifurcationReportDetails.setMerchantMinTdramount(String.valueOf(new BigDecimal(setting.getMerchantMinTdrAmt())));
					bifurcationReportDetails.setMerchantMaxTdramount(String.valueOf(new BigDecimal(setting.getMerchantMaxTdrAmt())));

					bifurcationReportDetails.setBankPreference(setting.getBankPreference());
					bifurcationReportDetails.setBankTDR(String.valueOf(new BigDecimal(setting.getBankTdr())));
					bifurcationReportDetails.setBankMinTdrAmount(String.valueOf(new BigDecimal(setting.getBankMinTdrAmt())));
					bifurcationReportDetails.setBankMaxTdrAmount(String.valueOf(new BigDecimal(setting.getBankMaxTdrAmt())));

					double acuquireTdrSc = Double
							.valueOf(doc.getString(FieldType.ACQUIRER_TDR_SC.getName()) == null ? "0"
									: doc.getString(FieldType.ACQUIRER_TDR_SC.getName()));
					double acquirerGst = Double.valueOf(doc.getString(FieldType.ACQUIRER_GST.getName()) == null ? "0"
							: doc.getString(FieldType.ACQUIRER_GST.getName()));
					double pgTdrSc = Double.valueOf(doc.getString(FieldType.PG_TDR_SC.getName()) == null ? "0"
							: doc.getString(FieldType.PG_TDR_SC.getName()));
					double pgGst = Double.valueOf(doc.getString(FieldType.PG_GST.getName()) == null ? "0"
							: doc.getString(FieldType.PG_GST.getName()));

					double totalAmount = Double.valueOf(doc.getString(FieldType.TOTAL_AMOUNT.getName()) == null ? "0"
							: doc.getString(FieldType.TOTAL_AMOUNT.getName()));


					bifurcationReportDetails.setBankTdrInAMOUNT(new BigDecimal(String.valueOf( acuquireTdrSc)).toString());
					bifurcationReportDetails.setBankGstInAmount(new BigDecimal(String.valueOf( acquirerGst)).toString());

					bifurcationReportDetails.setPgTdrInAmount(new BigDecimal(String.valueOf( pgTdrSc)).toString());
					bifurcationReportDetails.setPgGstInAmount(new BigDecimal(String.valueOf( pgGst)).toString());

					bifurcationReportDetails.setIgst18(new BigDecimal(String.valueOf( setting.getIgst())).toString());

					double amountPayableToMerchant = (totalAmount - acuquireTdrSc - acquirerGst - pgTdrSc - pgGst);
					double amountReceiveInNodal = (totalAmount - acuquireTdrSc - acquirerGst);

					if (bifurcationReportDetails.getRefundFlag() != null
							&& bifurcationReportDetails.getRefundFlag().trim().length() > 0) {

						if (user.getIndustryCategory().equalsIgnoreCase("EDUCATION")) {
							bifurcationReportDetails
									.setAmountPaybleToMerchant("-" + String.valueOf( amountPayableToMerchant));
						} else {
							bifurcationReportDetails
									.setAmountPaybleToMerchant("-" + String.valueOf( amountPayableToMerchant));
						}

					} else {
						bifurcationReportDetails
								.setAmountPaybleToMerchant(String.valueOf( amountPayableToMerchant));
					}

					bifurcationReportDetails.setAmountreceivedInNodal(String.valueOf(amountReceiveInNodal));
				}

//				if (doc.getString(FieldType.SURCHARGE_FLAG.getName()) != null && doc.getString(FieldType.SURCHARGE_FLAG.getName()).equalsIgnoreCase("Y")) {
//					
//				} else {
//					
//				}

				bifurcationReportDetails.setSettlementDate(doc.getString(FieldType.SETTLEMENT_DATE_INDEX.getName()));

				bifurcationReportDetails.setAmountReceivedNodalBank("ICICI Bank");

				if (!doc.getString(FieldType.STATUS.getName()).equalsIgnoreCase(StatusType.SETTLED_SETTLE.getName())) {
					bifurcationReportDetails.setSettlementTat(null);
				} else {
					bifurcationReportDetails.setSettlementTat("T+" + String.valueOf(user.getSettlementDays()));
				}

				bifurcationReportDetails.setAccountHolderName(user.getAccHolderName());
				bifurcationReportDetails.setAccountNumber(user.getAccountNo());
				bifurcationReportDetails.setIfscCode(user.getIfscCode());
				bifurcationReportDetails.setTransactionIdentifer(
						StringUtils.isBlank(doc.getString(FieldType.UDF6.getName())) ? "NORMAL" : "Split");
				bifurcationReportDetails
						.setLiabilityHoldRemakrs(doc.getString(FieldType.LIABILITYHOLDREMARKS.getName()));
				bifurcationReportDetails
						.setLiabilityReleaseRemakrs(doc.getString(FieldType.LIABILITYRELEASEREMARKS.getName()));
				bifurcationReportDetails.setUtrNumber(String.valueOf(doc.get(FieldType.UTR_NO.getName())));

				if (!doc.getString(FieldType.STATUS.getName()).equalsIgnoreCase(StatusType.SETTLED_SETTLE.getName())) {
					bifurcationReportDetails.setSettlementPeriod("0");
				} else {
					if(!StringUtils.isBlank(doc.getString(FieldType.SETTLEMENT_DATE_INDEX.getName()))){
						bifurcationReportDetails.setSettlementPeriod(settlementPeriod(
								doc.getString(FieldType.DATE_INDEX.getName()),
								doc.getString(FieldType.SETTLEMENT_DATE_INDEX.getName()), user.getSettlementDays()));
					}
					bifurcationReportDetails.setSettlementPeriod("0");
				}

				listToReturn.add(bifurcationReportDetails);
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.info(" Exception Occur in class TransactionStatus in getTDRBurificationReport(): ");

			return listToReturn;
		} finally {
			session.close();
		}
		logger.info("TransactionStatus in getTDRBurificationReport() List Size : " + listToReturn.size());
		return listToReturn;

	}

	private String settlementPeriod(String transactionDate, String settlementDate, int settlementTat)
			throws ParseException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		LocalDate settlementDateIndex = dateFormat.parse(settlementDate).toInstant().atZone(ZoneId.systemDefault())
				.toLocalDate();

		LocalDate result = dateFormat.parse(transactionDate).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate assumedSettlementDate = dateFormat.parse(transactionDate).toInstant().atZone(ZoneId.systemDefault())
				.toLocalDate();
		int subtractedDays = 0;
		while (subtractedDays < settlementTat) {
			result = result.plusDays(1);
			String tt = ("" + result.getDayOfMonth()) + ("" + result.getMonthValue());
			if (result.getDayOfWeek() == DayOfWeek.SUNDAY || tt.toString().equalsIgnoreCase("210")
					|| tt.toString().equalsIgnoreCase("261") || tt.toString().equalsIgnoreCase("158")) {
				assumedSettlementDate = assumedSettlementDate.plusDays(1);
			} else {
				assumedSettlementDate = assumedSettlementDate.plusDays(1);
				++subtractedDays;
			}
		}
		int output = Integer.valueOf(String.valueOf(
				Duration.between(assumedSettlementDate.atStartOfDay(), settlementDateIndex.atStartOfDay()).toDays()));
		if (output < 0) {
			output = 0;
		}
		return String.valueOf(output);
	}

	public List<MISReportObject> getMerchantWiseTransactionReport(String payId, String date) {
		logger.info(
				"TransactionStatus getMerchantWiseTransactionReport()\t PayID : " + payId + "\t" + "date : " + date);
		List<MISReportObject> transactionList = new ArrayList<MISReportObject>();
		try {
			String date_to = date.replaceAll("-", "");

			logger.info(
					"TransactionStatus getMerchantWiseTransactionReport()\t Date After Format: \t date :" + date_to);

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));
			MongoCursor<Document> cursor = null;
			Document match = new Document();
			match.put(FieldType.DATE_INDEX.getName(), date_to);
			match.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
			match.put(FieldType.STATUS.getName(), StatusType.SETTLED_SETTLE.getName());
			if (!payId.equalsIgnoreCase("All")) {
				match.put(FieldType.PAY_ID.getName(), payId);
			}
			List<Document> queryExecute = Arrays.asList(new Document("$match", match));
			logger.info("TransactionStatus getMerchantWiseTransactionReport() : \t" + queryExecute);
			cursor = coll.aggregate(queryExecute).iterator();

			while (cursor.hasNext()) {
				Document doc = (Document) cursor.next();
				MISReportObject transReport = new MISReportObject();
				Session session = HibernateSessionProvider.getSession();
				CriteriaBuilder builder = session.getCriteriaBuilder();
				CriteriaQuery<String> query = builder.createQuery(String.class);
				Root<User> root = query.from(User.class);
				query.where(builder.equal(root.get("payId"), doc.getString(FieldType.PAY_ID.toString())));
				query.select(root.get("businessName"));
				Query<String> q = session.createQuery(query);
				List<String> list = q.getResultList();
				if (list.size() > 0) {
					transReport.setMerchants(list.get(0) != null ? list.get(0) : "");
				} else {
					transReport.setMerchants("NA");
				}
				transReport.setTransactionId(doc.getString(FieldType.PG_REF_NUM.toString()));
				transReport.setDateFrom(doc.getString(FieldType.SETTLEMENT_DATE.getName()));
				transReport.setPayId(doc.getString(FieldType.PAY_ID.toString()));
				transReport.setPgRefNum(doc.getString(FieldType.PG_REF_NUM.toString()));

				if (StringUtils.isNotBlank(doc.getString(FieldType.MOP_TYPE.toString()))) {
					transReport.setMopType(MopType.getmop(doc.getString(FieldType.MOP_TYPE.toString())).getName());
				} else {
					transReport.setMopType("NA");
				}

				transReport.setAcquirerType(doc.getString(FieldType.ACQUIRER_TYPE.toString()));

				// if
				// (doc.getString(FieldType.TXNTYPE.toString()).contains(TransactionType.REFUND.getName()))
				// {
				// transReport.setTxnType(TransactionType.REFUND.getName());
				// } else {
				transReport.setTxnType(doc.getString(FieldType.TXNTYPE.toString()));
				// }

				transReport.setPaymentMethods(doc.getString(FieldType.PAYMENT_TYPE.toString()));
				transReport.setTotalAmount(doc.getString(FieldType.TOTAL_AMOUNT.toString()));
				transReport.setAmount(doc.getString(FieldType.AMOUNT.toString()));

				Double amtPayableToMerch = 0.00;

				amtPayableToMerch = Double.valueOf(doc.getString(FieldType.TOTAL_AMOUNT.toString()));
				// amtPayableToMerch = amtPayableToMerch -
				// Double.valueOf(doc.getString(FieldType.PG_TDR_SC.toString()));
				// amtPayableToMerch = amtPayableToMerch -
				// Double.valueOf(doc.getString(FieldType.ACQUIRER_TDR_SC.toString()));

				Double totalPayoutNodalAccount = 0.00;

				totalPayoutNodalAccount = Double.valueOf(doc.getString(FieldType.TOTAL_AMOUNT.toString()));
				// totalPayoutNodalAccount = totalPayoutNodalAccount -
				// Double.valueOf(doc.getString(FieldType.ACQUIRER_TDR_SC.toString()));

				// transReport.setGrossTransactionAmt(String.valueOf(
				// Double.valueOf(doc.getString(FieldType.TOTAL_AMOUNT.toString()) == null ? "0"
				// : doc.getString(FieldType.TOTAL_AMOUNT.toString()))));

				transReport.setGrossTransactionAmt(
						String.valueOf(Double.valueOf(doc.getString(FieldType.TOTAL_AMOUNT.toString()))));

				transReport.setAggregatorCommissionAMT(
						String.valueOf( Double.valueOf(doc.getString(FieldType.PG_TDR_SC.toString()))
								+ Double.valueOf(doc.getString(FieldType.PG_GST.toString()))));
				transReport.setAcquirerCommissionAMT(
						String.valueOf( Double.valueOf(doc.getString(FieldType.ACQUIRER_TDR_SC.toString()))
								+ Double.valueOf(doc.getString(FieldType.ACQUIRER_GST.toString()))));

				amtPayableToMerch -= Double.valueOf(transReport.getAggregatorCommissionAMT().toString());
				amtPayableToMerch -= Double.valueOf(transReport.getAcquirerCommissionAMT().toString());

				totalPayoutNodalAccount -= Double.valueOf(transReport.getAcquirerCommissionAMT().toString());

				transReport.setTotalAmtPayable(String.valueOf(
						(Double.parseDouble(transReport.getGrossTransactionAmt())
								- Double.parseDouble(transReport.getAggregatorCommissionAMT())
								- Double.parseDouble(transReport.getAcquirerCommissionAMT()))));
				transReport.setTotalPayoutNodalAccount(
						String.valueOf( (Double.parseDouble(transReport.getGrossTransactionAmt())
								- Double.parseDouble(transReport.getAcquirerCommissionAMT()))));

				// transReport.setTotalAmtPayable(String.valueOf( amtPayableToMerch));
				// transReport.setTotalPayoutNodalAccount(String.valueOf(
				// totalPayoutNodalAccount));
				if (StringUtils.isBlank(doc.getString(FieldType.SURCHARGE_FLAG.toString()))
						|| doc.getString(FieldType.SURCHARGE_FLAG.toString()).equalsIgnoreCase("N")) {
					transReport.setSurchargeFlag("N");
				} else {
					transReport.setSurchargeFlag("Y");
				}

				if (StringUtils.isBlank(doc.getString(FieldType.CREATE_DATE.toString()))) {
					transReport.setTransactionDate("NA");
				} else {
					transReport.setTransactionDate(doc.getString(FieldType.CREATE_DATE.toString()));
				}

				transReport.setOrderId(doc.getString(FieldType.ORDER_ID.toString()));
				transReport.setRefundFlag(doc.getString(FieldType.REFUND_FLAG.toString()));

				if (StringUtils.isNotBlank(doc.getString(FieldType.PAYMENTS_REGION.toString()))) {
					transReport.setTransactionRegion(doc.getString(FieldType.PAYMENTS_REGION.toString()));
				} else {
					transReport.setTransactionRegion(AccountCurrencyRegion.DOMESTIC.toString());
				}

				transactionList.add(transReport);

			}

		} catch (Exception e) {
			logger.info(
					"TransactionStatus getMerchantWiseTransactionReport() \t Exception Occur in : " + e.getMessage());
			e.printStackTrace();
		}
		return transactionList;
	}

	public List<TransactionStatusBean> getMerchantWiseSummaryReport(String payId, String dateFrom) {
		logger.info(
				"TransactionStatus getMerchantWiseSummaryReport() \t PayID : " + payId + "\t" + "date : " + dateFrom);
		List<TransactionStatusBean> returntransactionBeans = null;
		try {
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns
					.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
			MongoCursor<Document> cursor = null;
			String date_to = dateFrom.replaceAll("-", "");

			logger.info("TransactionStatus getMerchantWiseSummaryReport() \t Date After Format: \t date :" + date_to);
			Document match = new Document();
			match.put(FieldType.DATE_INDEX.getName(), date_to);
			match.put(FieldType.TXNTYPE.getName(), TransactionType.RECO.getName());
			match.put(FieldType.STATUS.getName(), StatusType.SETTLED_SETTLE.getName());
			// match.put(FieldType.STATUS.getName(), new Document("$or",
			// Arrays.asList(StatusType.SETTLED_SETTLE.getName(),
			// StatusType.SETTLED_RECONCILLED.getName())));
			if (!payId.equalsIgnoreCase("All")) {
				match.put("PAY_ID", payId);
			}
			List<Document> queryExecute = Arrays.asList(new Document("$match", match),
					new Document("$group", new Document("_id", new Document("PAY_ID", "$PAY_ID"))
							.append("PG_TDR_SC", new Document("$sum", new Document("$toDouble", "$PG_TDR_SC")))
							.append("PG_GST", new Document("$sum", new Document("$toDouble", "$PG_GST")))
							.append("ACQUIRER_TDR_SC",
									new Document("$sum", new Document("$toDouble", "$ACQUIRER_TDR_SC")))
							.append("ACQUIRER_GST", new Document("$sum", new Document("$toDouble", "$ACQUIRER_GST")))
							.append("TOTAL_AMOUNT", new Document("$sum", new Document("$toDouble", "$TOTAL_AMOUNT")))),
					new Document("$project", new Document("PAY_ID", "$_id.PAY_ID").append("PG_TDR_SC", "$PG_TDR_SC")
							.append("PG_GST", "$PG_GST").append("TOTAL_AMOUNT", "$TOTAL_AMOUNT")
							.append("ACQUIRER_TDR_SC", "$ACQUIRER_TDR_SC").append("ACQUIRER_GST", "$ACQUIRER_GST")));
			logger.info("TransactionStatus getMerchantWiseSummaryReport() : \t" + queryExecute);
			cursor = coll.aggregate(queryExecute).iterator();
			returntransactionBeans = new ArrayList<>();
			while (cursor.hasNext()) {
				Document transaction = (Document) cursor.next();
				if (transaction != null) {
					String payid = transaction.getString("PAY_ID");
					TransactionStatusBean transactionStatusBean = new TransactionStatusBean();
					transactionStatusBean.setPAY_ID(payid);

					Session session = HibernateSessionProvider.getSession();
					CriteriaBuilder builder = session.getCriteriaBuilder();
					CriteriaQuery<String> query = builder.createQuery(String.class);
					Root<User> root = query.from(User.class);
					query.where(builder.equal(root.get("payId"), payid));
					query.select(root.get("businessName"));
					Query<String> q = session.createQuery(query);
					List<String> list = q.getResultList();
					if (list.size() > 0) {
						transactionStatusBean.setBusinessName(list.get(0) != null ? list.get(0) : "");
					} else {
						transactionStatusBean.setBusinessName("NA");
					}
					// Total Aggregator Commision Amt Payable(Including GST) --PG_TDR_SC+PG_GST
					transactionStatusBean.setPG_TDR_SC(Double.valueOf(String.valueOf(
							(transaction.getDouble("PG_TDR_SC") + transaction.getDouble("PG_GST")))));

					// Total Acquirer Commision Amt Payable(Including GST)
					transactionStatusBean.setPG_GST(Double.valueOf(String.valueOf(
							(transaction.getDouble("ACQUIRER_TDR_SC") + transaction.getDouble("ACQUIRER_GST")))));

					// Gross Transaction Amt

					transactionStatusBean.setAMOUNT(transaction.getDouble("TOTAL_AMOUNT"));

					// Total Amt Payable to Merchant A/c
					transactionStatusBean.setNetSettleAmount(String.valueOf( (transactionStatusBean.getAMOUNT()
							- transactionStatusBean.getPG_TDR_SC() - transactionStatusBean.getPG_GST())));

					transactionStatusBean.setACQUIRER_GST(Double.valueOf(String.valueOf(
							(transactionStatusBean.getAMOUNT() - transactionStatusBean.getPG_GST()))));
					returntransactionBeans.add(transactionStatusBean);
				}

			}

		} catch (Exception e) {
			logger.info("TransactionStatus getMerchantWiseSummaryReport() \t Exception Occur in : " + e.getMessage());
			e.printStackTrace();
		}
		logger.info("TransactionStatus getMerchantWiseSummaryReport() \t Return Merchant Data and Size oF List:"
				+ returntransactionBeans.size());
		return returntransactionBeans;

	}

	public List<MISReportObject> getPreMisSettlementReport(String payId, String date, String acquirer) {
		logger.info("TransactionStatus getPreMisSettlementReport() \t PayID : " + payId + "\t" + "date : " + date
				+ "\tAcquirer :" + acquirer);
		List<MISReportObject> transactionList = new ArrayList<MISReportObject>();
		try {
			Map<String, User> userMap = new HashMap<String, User>();
			String date_to = date.replaceAll("-", "");

			logger.info("TransactionStatus getPreMisSettlementReport() \t Date After Format: \t date :" + date_to
					+ "\tAcquirer :" + acquirer);

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));
			MongoCursor<Document> cursor = null;

			Document match = new Document();
			match.put(FieldType.DATE_INDEX.getName(), date_to);
			match.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
			match.put(FieldType.STATUS.getName(), new Document("$in",
					Arrays.asList(StatusType.CAPTURED.getName(), StatusType.SETTLED_RECONCILLED.getName())));
			if (!payId.equalsIgnoreCase("All")) {
				match.put(FieldType.PAY_ID.getName(), payId);
			}
			if (!acquirer.equalsIgnoreCase("All")) {
				match.put(FieldType.ACQUIRER_TYPE.getName(), acquirer);
			}
			List<Document> queryExecute = Arrays.asList(new Document("$match", match));
			logger.info("TransactionStatus getPreMisSettlementReport() : \t" + queryExecute);
			cursor = coll.aggregate(queryExecute).iterator();

			while (cursor.hasNext()) {
				Document doc = (Document) cursor.next();

				MISReportObject transReport = new MISReportObject();
				Session session = HibernateSessionProvider.getSession();

				if (userMap.get(doc.getString(FieldType.PAY_ID.toString())) != null) {

					transReport.setMerchants(userMap.get(doc.getString(FieldType.PAY_ID.toString())).getBusinessName());
					transReport.setSettlementCycle(String
							.valueOf(userMap.get(doc.getString(FieldType.PAY_ID.toString())).getSettlementDays()));
					transReport.setAccountNo(
							userMap.get(doc.getString(FieldType.PAY_ID.toString())).getAccountNo() == null ? "N/A"
									: userMap.get(doc.getString(FieldType.PAY_ID.toString())).getAccountNo());

				} else {
					logger.info("Looking for PayId : {}", doc.getString(FieldType.PAY_ID.toString()));
					try {
						// Kept try-Catch to avoid indiviual payId failure
						User user = userDao.findPayId(doc.getString(FieldType.PAY_ID.toString()));
						if (null != user) {
							transReport.setMerchants(user.getBusinessName());
							userMap.put(user.getPayId(), user);
							transReport.setSettlementCycle(String.valueOf(user.getSettlementDays()));
							transReport.setAccountNo(user.getAccountNo() == null ? "N/A" : user.getAccountNo());
						} else {
							transReport.setMerchants("ALL");
							transReport.setSettlementCycle("N/A");
							transReport.setAccountNo("N/A");

						}
					} catch (Exception ee) {
						logger.info("Error occurred !!! ", ee);
					}

				}

				transReport.setTransactionId(doc.getString(FieldType.PG_REF_NUM.toString()));
				transReport.setDateFrom(doc.getString(FieldType.CREATE_DATE.getName()));
				transReport.setPayId(doc.getString(FieldType.PAY_ID.toString()));
				transReport.setPgRefNum(doc.getString(FieldType.PG_REF_NUM.toString()));

				if (StringUtils.isNotBlank(doc.getString(FieldType.MOP_TYPE.toString()))) {
					transReport.setMopType(MopType.getmop(doc.getString(FieldType.MOP_TYPE.toString())).getName());
				} else {
					transReport.setMopType("NA");
				}

				transReport.setAcquirerType(doc.getString(FieldType.ACQUIRER_TYPE.toString()));

//              if (doc.getString(FieldType.TXNTYPE.toString()).contains(TransactionType.REFUND.getName())) {
//                  transReport.setTxnType(TransactionType.REFUND.getName());
//              } else {
				transReport.setTxnType(doc.getString(FieldType.TXNTYPE.toString()));
//              }

				transReport.setPaymentMethods(doc.getString(FieldType.PAYMENT_TYPE.toString()));
				transReport.setTotalAmount(doc.getString(FieldType.TOTAL_AMOUNT.toString()));
				transReport.setAmount(doc.getString(FieldType.AMOUNT.toString()));

				Double amtPayableToMerch = 0.00;

				amtPayableToMerch = Double.valueOf(doc.getString(FieldType.TOTAL_AMOUNT.toString()));
				// amtPayableToMerch = amtPayableToMerch -
				// Double.valueOf(doc.getString(FieldType.PG_TDR_SC.toString()));
				// amtPayableToMerch = amtPayableToMerch -
				// Double.valueOf(doc.getString(FieldType.ACQUIRER_TDR_SC.toString()));

				Double totalPayoutNodalAccount = 0.00;

				totalPayoutNodalAccount = Double.valueOf(doc.getString(FieldType.TOTAL_AMOUNT.toString()));
				// totalPayoutNodalAccount = totalPayoutNodalAccount -
				// Double.valueOf(doc.getString(FieldType.ACQUIRER_TDR_SC.toString()));

//                if (doc.getString("SURCHARGE_FLAG") == null || doc.getString("SURCHARGE_FLAG").equalsIgnoreCase("N")) {
//                	
//                	String []tdr=tdrSettingDao.calculateTdrAndSurcharge(doc.getString(FieldType.PAY_ID.toString()), doc.getString(FieldType.PAYMENT_TYPE.toString()), doc.getString(FieldType.ACQUIRER_TYPE.toString()), doc.getString(FieldType.MOP_TYPE.toString()), doc.getString(FieldType.TXNTYPE.toString()), doc.getString(FieldType.CURRENCY_CODE.toString()), doc.getString(FieldType.AMOUNT.toString()), doc.getString(FieldType.CREATE_DATE.toString()),"","");
//                	
				transReport.setGrossTransactionAmt(String.valueOf(
						Double.valueOf(doc.getString(FieldType.TOTAL_AMOUNT.toString()) == null ? "0"
								: doc.getString(FieldType.TOTAL_AMOUNT.toString()))));

				transReport.setAggregatorCommissionAMT(
						String.valueOf( Double.valueOf(doc.getString(FieldType.PG_GST.toString()))
								+ Double.valueOf(doc.getString(FieldType.PG_TDR_SC.toString()))));
				transReport.setAcquirerCommissionAMT(
						String.valueOf( Double.valueOf(doc.getString(FieldType.ACQUIRER_GST.toString()))
								+ Double.valueOf(doc.getString(FieldType.ACQUIRER_TDR_SC.toString()))));

//                } 
//                else {
//                	Query chargingdetailquery = session.createSQLQuery(
//							"SELECT bankTDR, bankTDRAFC, bankServiceTax, pgTDR, pgTDRAFC, pgServiceTax,allowFixCharge FROM ChargingDetails WHERE payId=:payId and paymentType=:paymentType and mopType=:mopType and acquirerName=:acquirerName and createdDate<=:createdDate  order by createdDate desc");
//					chargingdetailquery.setParameter("payId", doc.getString(FieldType.PAY_ID.toString()));
//					chargingdetailquery.setParameter("paymentType", PaymentType
//							.getInstanceUsingCode(doc.getString(FieldType.PAYMENT_TYPE.toString())).toString());
//					chargingdetailquery.setParameter("mopType",
//							MopType.getmop(doc.getString(FieldType.MOP_TYPE.toString())).toString());
//					chargingdetailquery.setParameter("acquirerName",
//							AcquirerType.getAcquirerName(doc.getString(FieldType.ACQUIRER_TYPE.toString())));
//					chargingdetailquery.setParameter("createdDate", doc.getString(FieldType.CREATE_DATE.toString()));
//					List<Object[]> chargingdetail = chargingdetailquery.list();
//					logger.info("Query : " + chargingdetailquery.getQueryString());
//
//					Object[] object = chargingdetail.get(0);
//					double bankTDR = Double.parseDouble("" + object[0]);
//					double bankTDRAFC = Double.parseDouble("" + object[1]);
//					double bankServiceTax = Double.parseDouble("" + object[2]);
//
//					double pgTDR = Double.parseDouble("" + object[3]);
//
//					double pgTDRAFC = Double.parseDouble("" + object[4]);
//
//					double pgServiceTax = Double.parseDouble("" + object[5]);
//
//					boolean allowFixCharge = Boolean.parseBoolean("" + object[6]);
//					logger.info(" charging details lise size " + chargingdetail.size());
//					logger.info(" charging details are:");
//					logger.info("allowFixCharge : " + allowFixCharge);
//
//					double aggregatortdramount = 0;
//
//					double acquirertdramount = 0;
//					logger.info("Total Amount : "
//							+ String.valueOf( Double.valueOf(doc.getString(FieldType.TOTAL_AMOUNT.toString())))
//							+ "\t Amount : " + transReport.getAmount());
//					if (allowFixCharge == true) {
//						logger.info("bankTDRAFC : " + bankTDRAFC);
//						logger.info("pgTDRAFC : " + pgTDRAFC);
//						aggregatortdramount = Double.valueOf(transReport.getAmount()) * (pgTDRAFC / 100);
//						logger.info("Aggreator commission without gst " + aggregatortdramount);
//						aggregatortdramount = (aggregatortdramount + ((pgServiceTax / 100) * aggregatortdramount));
//						logger.info("Aggreator commission with gst " + aggregatortdramount);
//						acquirertdramount = Double.valueOf(transReport.getAmount()) * (bankTDRAFC / 100);
//						logger.info("Acquirer commission without gst " + acquirertdramount);
//						acquirertdramount = (acquirertdramount + ((bankServiceTax / 100) * acquirertdramount));
//						logger.info("Acquirer commission with gst " + acquirertdramount);
//					} else {
//						logger.info("bankTDR : " + bankTDR);
//						logger.info("pgTDR : " + pgTDR);
//						aggregatortdramount = Double.valueOf(transReport.getAmount()) * (pgTDR / 100);
//						logger.info("Aggreator commission without gst " + aggregatortdramount);
//						aggregatortdramount = (aggregatortdramount + ((pgServiceTax / 100) * aggregatortdramount));
//						logger.info("Aggreator commission with gst " + aggregatortdramount);
//						acquirertdramount = Double.valueOf(transReport.getAmount()) * (bankTDR / 100);
//						logger.info("Acquirer commission without gst " + acquirertdramount);
//						acquirertdramount = (acquirertdramount + ((bankServiceTax / 100) * acquirertdramount));
//						logger.info("Acquirer commission with gst " + acquirertdramount);
//					}
//					transReport.setAggregatorCommissionAMT(String.valueOf( aggregatortdramount));
//					transReport.setAcquirerCommissionAMT(String.valueOf( acquirertdramount));
//					transReport.setGrossTransactionAmt("" + (Double.parseDouble(transReport.getAmount())
//							+ Double.parseDouble(transReport.getAggregatorCommissionAMT())
//							+ Double.parseDouble(transReport.getAcquirerCommissionAMT())));
//                }

				amtPayableToMerch -= Double.valueOf(transReport.getAggregatorCommissionAMT().toString());
				amtPayableToMerch -= Double.valueOf(transReport.getAcquirerCommissionAMT().toString());

				totalPayoutNodalAccount -= Double.valueOf(transReport.getAcquirerCommissionAMT().toString());

				transReport.setTotalAmtPayable(String.valueOf( amtPayableToMerch));
				transReport.setTotalPayoutNodalAccount(String.valueOf( totalPayoutNodalAccount));

				if (StringUtils.isBlank(doc.getString(FieldType.SURCHARGE_FLAG.toString()))
						|| doc.getString(FieldType.SURCHARGE_FLAG.toString()).equalsIgnoreCase("N")) {
					transReport.setSurchargeFlag("N");
				} else {
					transReport.setSurchargeFlag("Y");
				}

				if (StringUtils.isBlank(doc.getString(FieldType.CREATE_DATE.toString()))) {
					transReport.setTransactionDate("NA");
				} else {
					transReport.setTransactionDate(doc.getString(FieldType.CREATE_DATE.toString()));
				}

				transReport.setOrderId(doc.getString(FieldType.ORDER_ID.toString()));
				transReport.setRefundFlag(doc.getString(FieldType.REFUND_FLAG.toString()));

				if (StringUtils.isNotBlank(doc.getString(FieldType.PAYMENTS_REGION.toString()))) {
					transReport.setTransactionRegion(doc.getString(FieldType.PAYMENTS_REGION.toString()));
				} else {
					transReport.setTransactionRegion(AccountCurrencyRegion.DOMESTIC.toString());
				}

				transactionList.add(transReport);

			}

		} catch (Exception e) {
			logger.info("TransactionStatus getPreMisSettlementReport() \t Exception Occur : " + e.getMessage());
			e.printStackTrace();
		}
//      logger.info("TransactionStatus getPreMisSettlementReport() \t Return Merchant Data and Size oF List:"+returnTransactionStatusBeans.size());
		return transactionList;
	}

	public List<MISReportObject> getPreMisSettlementReportSplit(String payId, String date, String acquirer) {
		logger.info("TransactionStatus getSplitSettlementReport() \t PayID : " + payId + "\t" + "date : " + date
				+ "\tAcquirer :" + acquirer);
		List<MISReportObject> transactionList = new ArrayList<MISReportObject>();
		try {
			Map<String, User> userMap = new HashMap<String, User>();
			String date_to = date.replaceAll("-", "");

			logger.info("TransactionStatus getSplitSettlementReport() \t Date After Format: \t date :" + date_to
					+ "\tAcquirer :" + acquirer);

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));
			MongoCursor<Document> cursor = null;

			Document match = new Document();
			match.put(FieldType.DATE_INDEX.getName(), date_to);
			match.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
			match.put(FieldType.STATUS.getName(), new Document("$in",
					Arrays.asList(StatusType.CAPTURED.getName(), StatusType.SETTLED_RECONCILLED.getName())));
			if (!payId.equalsIgnoreCase("All")) {
				match.put(FieldType.PAY_ID.getName(), payId);
			}
			if (!acquirer.equalsIgnoreCase("All")) {
				match.put(FieldType.ACQUIRER_TYPE.getName(), acquirer);
			}
			List<Document> queryExecute = Arrays.asList(new Document("$match", match));
			logger.info("TransactionStatus getSplitSettlementReport() : \t" + queryExecute);
			cursor = coll.aggregate(queryExecute).iterator();

			while (cursor.hasNext()) {
				Document doc = (Document) cursor.next();
				System.out.println(doc);
				MISReportObject transReport = new MISReportObject();
				Session session = HibernateSessionProvider.getSession();

				if (userMap.get(doc.getString(FieldType.PAY_ID.toString())) != null) {

					transReport.setMerchants(userMap.get(doc.getString(FieldType.PAY_ID.toString())).getBusinessName());
					transReport.setSettlementCycle(String
							.valueOf(userMap.get(doc.getString(FieldType.PAY_ID.toString())).getSettlementDays()));

				} else {
					logger.info("Looking for PayId : {}", doc.getString(FieldType.PAY_ID.toString()));
					try {
						// Kept try-Catch to avoid indiviual payId failure
						User user = userDao.findPayId(doc.getString(FieldType.PAY_ID.toString()));
						if (null != user) {
							transReport.setMerchants(user.getBusinessName());
							userMap.put(user.getPayId(), user);
							transReport.setSettlementCycle(String.valueOf(user.getSettlementDays()));

						} else {
							transReport.setMerchants("ALL");
							transReport.setSettlementCycle("N/A");

						}
					} catch (Exception ee) {
						logger.info("Error occurred !!! ", ee);
					}

				}

				transReport.setTransactionId(doc.getString(FieldType.PG_REF_NUM.toString()));
				transReport.setDateFrom(doc.getString(FieldType.CREATE_DATE.getName()));
				transReport.setPayId(doc.getString(FieldType.PAY_ID.toString()));
				transReport.setPgRefNum(doc.getString(FieldType.PG_REF_NUM.toString()));

				if (StringUtils.isNotBlank(doc.getString(FieldType.MOP_TYPE.toString()))) {
					transReport.setMopType(MopType.getmop(doc.getString(FieldType.MOP_TYPE.toString())).getName());
				} else {
					transReport.setMopType("NA");
				}

				transReport.setAcquirerType(doc.getString(FieldType.ACQUIRER_TYPE.toString()));

//              if (doc.getString(FieldType.TXNTYPE.toString()).contains(TransactionType.REFUND.getName())) {
//                  transReport.setTxnType(TransactionType.REFUND.getName());
//              } else {
				transReport.setTxnType(doc.getString(FieldType.TXNTYPE.toString()));
//              }

				transReport.setPaymentMethods(doc.getString(FieldType.PAYMENT_TYPE.toString()));
				transReport.setTotalAmount(doc.getString(FieldType.TOTAL_AMOUNT.toString()));
				transReport.setAmount(doc.getString(FieldType.AMOUNT.toString()));

				Double amtPayableToMerch = 0.00;

				amtPayableToMerch = Double.valueOf(doc.getString(FieldType.TOTAL_AMOUNT.toString()));
				// amtPayableToMerch = amtPayableToMerch -
				// Double.valueOf(doc.getString(FieldType.PG_TDR_SC.toString()));
				// amtPayableToMerch = amtPayableToMerch -
				// Double.valueOf(doc.getString(FieldType.ACQUIRER_TDR_SC.toString()));

				Double totalPayoutNodalAccount = 0.00;

				totalPayoutNodalAccount = Double.valueOf(doc.getString(FieldType.TOTAL_AMOUNT.toString()));
				// totalPayoutNodalAccount = totalPayoutNodalAccount -
				// Double.valueOf(doc.getString(FieldType.ACQUIRER_TDR_SC.toString()));

//                transReport.setGrossTransactionAmt(String.valueOf(
//                        Double.valueOf(doc.getString(FieldType.TOTAL_AMOUNT.toString()) == null ? "0"
//                                : doc.getString(FieldType.TOTAL_AMOUNT.toString()))));
//                transReport.setAggregatorCommissionAMT(String.valueOf(
//                        Double.valueOf(doc.getString(FieldType.PG_TDR_SC.toString()) == null ? "0"
//                                : doc.getString(FieldType.PG_TDR_SC.toString()))
//                                + Double.valueOf(doc.getString(FieldType.PG_GST.toString()) == null ? "0"
//                                        : doc.getString(FieldType.PG_GST.toString()))));
//                transReport.setAcquirerCommissionAMT(String.valueOf(
//                        Double.valueOf(doc.getString(FieldType.ACQUIRER_TDR_SC.toString()) == null ? "0"
//                                : doc.getString(FieldType.ACQUIRER_TDR_SC.toString()))
//                                + Double.valueOf(doc.getString(FieldType.ACQUIRER_GST.toString()) == null ? "0"
//                                        : doc.getString(FieldType.ACQUIRER_GST.toString()))));

				transReport.setGrossTransactionAmt(String.valueOf(
						Double.valueOf(doc.getString(FieldType.TOTAL_AMOUNT.toString()) == null ? "0"
								: doc.getString(FieldType.TOTAL_AMOUNT.toString()))));

				transReport.setAggregatorCommissionAMT(
						String.valueOf( Double.valueOf(doc.getString(FieldType.PG_GST.toString()))
								+ Double.valueOf(doc.getString(FieldType.PG_TDR_SC.toString()))));
				transReport.setAcquirerCommissionAMT(
						String.valueOf( Double.valueOf(doc.getString(FieldType.ACQUIRER_GST.toString()))
								+ Double.valueOf(doc.getString(FieldType.ACQUIRER_TDR_SC.toString()))));

				amtPayableToMerch -= Double.valueOf(transReport.getAggregatorCommissionAMT().toString());
				amtPayableToMerch -= Double.valueOf(transReport.getAcquirerCommissionAMT().toString());

				totalPayoutNodalAccount -= Double.valueOf(transReport.getAcquirerCommissionAMT().toString());

				transReport.setTotalAmtPayable(String.valueOf( amtPayableToMerch));
				transReport.setTotalPayoutNodalAccount(String.valueOf( totalPayoutNodalAccount));

				if (StringUtils.isBlank(doc.getString(FieldType.SURCHARGE_FLAG.toString()))) {
					transReport.setSurchargeFlag("N");
				} else {
					transReport.setSurchargeFlag("Y");
				}

				if (StringUtils.isBlank(doc.getString(FieldType.CREATE_DATE.toString()))) {
					transReport.setTransactionDate("NA");
				} else {
					transReport.setTransactionDate(doc.getString(FieldType.CREATE_DATE.toString()));
				}

				transReport.setOrderId(doc.getString(FieldType.ORDER_ID.toString()));
				transReport.setRefundFlag(doc.getString(FieldType.REFUND_FLAG.toString()));

				if (StringUtils.isNotBlank(doc.getString(FieldType.PAYMENTS_REGION.toString()))) {
					transReport.setTransactionRegion(doc.getString(FieldType.PAYMENTS_REGION.toString()));
				} else {
					transReport.setTransactionRegion(AccountCurrencyRegion.DOMESTIC.toString());
				}

				// added by deep
				if (StringUtils.isNotBlank(doc.getString(FieldType.UDF6.getName()))) {
					if (doc.getString(FieldType.UDF6.getName()).contains("|")) {

						String data = doc.getString(FieldType.UDF6.getName());
						String[] accountDetails = data.split("\\|");
						logger.info("Split Report UDF 6 : " + doc.getString(FieldType.UDF6.getName()));
						int count = 0;
						for (String account : accountDetails) {
							MISReportObject transReportDuplicateAcNoEntry = (MISReportObject) transReport.clone();
							// fetch account detail from banksettle based on productid
							logger.info("Split Report UDF 6 : " + account);
							String[] pro_acco = account.split("=");
							logger.info("Split Report UDF 6 : " + pro_acco[0]);
							List<BankSettle> bankSettleAccount = bankSettleDao.getSplitAccountDetail(pro_acco[0]);
							logger.info("Split Report UDF 6 : " + bankSettleAccount);
							if (count != 0) {
								transReportDuplicateAcNoEntry.setGrossTransactionAmt("0");
								transReportDuplicateAcNoEntry.setTotalAmount("0");
								transReportDuplicateAcNoEntry.setAggregatorCommissionAMT("0");
								transReportDuplicateAcNoEntry.setTotalAmtPayable("0");
								transReportDuplicateAcNoEntry.setAcquirerCommissionAMT("0");
								transReportDuplicateAcNoEntry.setTotalPayoutNodalAccount("0");
							}
							count++;
							if (bankSettleAccount.size() > 0) {
								transReportDuplicateAcNoEntry
										.setAccountHolderName(bankSettleAccount.get(0).getAccountHolderName());
								transReportDuplicateAcNoEntry
										.setAccountNumber(bankSettleAccount.get(0).getAccountNumber());
								transReportDuplicateAcNoEntry.setIfsc(bankSettleAccount.get(0).getIfscCode());
								transReportDuplicateAcNoEntry
										.setMerchantAmount(StringUtils.isNotBlank(pro_acco[1]) ? pro_acco[1] : "0");
								transactionList.add(transReportDuplicateAcNoEntry);
							}

						}

					}

				}
				// ended by deep

			}

		} catch (Exception e) {
			logger.info("TransactionStatus getSplitSettlementReport() \t Exception Occur : " + e.getMessage());
		}
//      logger.info("TransactionStatus getPreMisSettlementReport() \t Return Merchant Data and Size oF List:"+returnTransactionStatusBeans.size());
		return transactionList;
	}

	public int getForceCaptureReportCount(String payId, String status, String acquirer, String dateFrom,
			String dateTo) {
		List<TDRBifurcationReportDetails> listToReturn = new ArrayList<TDRBifurcationReportDetails>();
		Session session = HibernateSessionProvider.getSession();

		try {

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns
					.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
			MongoCursor<Document> cursor = null;
			BasicDBObject match = new BasicDBObject();

			if (!payId.equalsIgnoreCase("All")) {
				match.put(FieldType.PAY_ID.getName(), payId);
			}
			if (!acquirer.equalsIgnoreCase("All")) {
				match.put(FieldType.ACQUIRER_TYPE.getName(), acquirer);
			}
			if (!StringUtils.isBlank(dateFrom) && !StringUtils.isBlank(dateTo)) {
				match.put(FieldType.DATE_INDEX.getName(),
						new Document("$gte", dateFrom.replaceAll("-", "")).append("$lte", dateTo.replaceAll("-", "")));
				match.put(FieldType.STATUS.getName(),
						new Document("$in", Arrays.asList(StatusType.FORCE_CAPTURED.getName())));
			}
			List<BasicDBObject> queryExecute = Arrays.asList(new BasicDBObject("$match", match));
			logger.info(" TransactionStatus getForceCaptureReportCount Query: \t" + queryExecute);
			return coll.aggregate(queryExecute).into(new ArrayList<>()).size();
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(" Exception Occur in class TransactionStatus in getForceCaptureReportCount(): ");

			return 0;
		} finally {
			session.close();
		}

	}

	public List<TransactionSearchNew> getForceCapturedReport(String payId, String status, String acquirer,
			String dateFrom, String dateTo, int start, int length) {
		List<TransactionSearchNew> transactionList = new ArrayList<TransactionSearchNew>();
		Session session = HibernateSessionProvider.getSession();

		try {
			
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns
					.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
			MongoCursor<Document> cursor = null;
			BasicDBObject match = new BasicDBObject();

			if (!payId.equalsIgnoreCase("All")) {
				match.put(FieldType.PAY_ID.getName(), payId);
			}
			if (!acquirer.equalsIgnoreCase("All")) {
				match.put(FieldType.ACQUIRER_TYPE.getName(), acquirer);
			}

			if (!StringUtils.isBlank(dateFrom) && !StringUtils.isBlank(dateTo)) {
				match.put(FieldType.DATE_INDEX.getName(),
						new Document("$gte", dateFrom.replaceAll("-", "")).append("$lte", dateTo.replaceAll("-", "")));

				match.put(FieldType.STATUS.getName(),
						new Document("$in", Arrays.asList(StatusType.FORCE_CAPTURED.getName())));

			}

			BasicDBObject matchQ = new BasicDBObject("$match", match);
			BasicDBObject sort = new BasicDBObject("$sort", new BasicDBObject("CREATE_DATE", -1));
			BasicDBObject skip = new BasicDBObject("$skip", start);
			BasicDBObject limit = new BasicDBObject("$limit", length);
			List<BasicDBObject> queryExecute = Arrays.asList(matchQ, sort, skip, limit);

			logger.info(" TransactionStatus getForceCapturedReport() Query: \t" + queryExecute);
			cursor = coll.aggregate(queryExecute).iterator();

			while (cursor.hasNext()) {
				Document dbobj = cursor.next();
				TransactionSearchNew transReport = new TransactionSearchNew();
				transReport.setTransactionIdString(dbobj.getString(FieldType.TXN_ID.toString()));

				transReport.setPgRefNum(dbobj.getString(FieldType.PG_REF_NUM.toString()));

				transReport.setPayId(dbobj.getString(FieldType.PAY_ID.toString()));

				if (StringUtils.isNotBlank(dbobj.getString(FieldType.UDF6.getName()))) {
					transReport.setUdf6(dbobj.getString(FieldType.UDF6.getName()));
					transReport.setSplitPayment("True");
				} else {
					transReport.setUdf6(dbobj.getString(CrmFieldConstants.NA.getValue()));
					transReport.setSplitPayment("False");
				}

				if (null != dbobj.getString(FieldType.UDF4.toString())) {
					transReport.setUdf4(dbobj.getString(FieldType.UDF4.toString()));
				} else {
					transReport.setUdf4(dbobj.getString(CrmFieldConstants.NA.getValue()));
				}

				if (null != dbobj.getString(FieldType.UDF5.toString())) {
					transReport.setUdf5(dbobj.getString(FieldType.UDF5.toString()));
				} else {
					transReport.setUdf5(dbobj.getString(CrmFieldConstants.NA.getValue()));
				}

				if (null != dbobj.getString(FieldType.UDF6.toString())) {
					transReport.setUdf6(dbobj.getString(FieldType.UDF6.toString()));
				} else {
					transReport.setUdf6(dbobj.getString(CrmFieldConstants.NA.getValue()));
				}

				if (StringUtils.isNotBlank(dbobj.getString(FieldType.UDF6.getName()))) {
					transReport.setUdf6(dbobj.getString(FieldType.UDF6.getName()));
					transReport.setSplitPayment("True");
				} else {
					transReport.setUdf6(dbobj.getString(CrmFieldConstants.NA.getValue()));
					transReport.setSplitPayment("False");
				}

				if (null != dbobj.getString(FieldType.UDF4.toString())) {
					transReport.setUdf4(dbobj.getString(FieldType.UDF4.toString()));
				} else {
					transReport.setUdf4(dbobj.getString(CrmFieldConstants.NA.getValue()));
				}

				if (null != dbobj.getString(FieldType.UDF5.toString())) {
					transReport.setUdf5(dbobj.getString(FieldType.UDF5.toString()));
				} else {
					transReport.setUdf5(dbobj.getString(CrmFieldConstants.NA.getValue()));
				}

				if (null != dbobj.getString(FieldType.UDF6.toString())) {
					transReport.setUdf6(dbobj.getString(FieldType.UDF6.toString()));
				} else {
					transReport.setUdf6(dbobj.getString(CrmFieldConstants.NA.getValue()));
				}

				if (null != dbobj.getString(FieldType.CARD_HOLDER_NAME.toString())) {
					transReport.setCardHolderName(dbobj.getString(FieldType.CARD_HOLDER_NAME.toString()));
				} else if (null != dbobj.getString(FieldType.CUST_NAME.toString())) {
					transReport.setCustomerName(dbobj.getString(FieldType.CUST_NAME.toString()));
				} else {
					transReport.setCardHolderName(CrmFieldConstants.NA.getValue());
					transReport.setCustomerName(CrmFieldConstants.NA.getValue());
				}

				if (null != dbobj.getString(FieldType.CUST_EMAIL.toString())) {
					transReport.setCustomerEmail(dbobj.getString(FieldType.CUST_EMAIL.toString()));
				} else {
					transReport.setCustomerEmail(CrmFieldConstants.NA.getValue());
				}
				if (null != dbobj.getString(FieldType.CUST_PHONE.toString())) {
					transReport.setCustomerPhone(dbobj.getString(FieldType.CUST_PHONE.toString()));
				} else {
					transReport.setCustomerPhone(CrmFieldConstants.NA.getValue());
				}
				transReport.setMerchants(dbobj.getString(CrmFieldType.BUSINESS_NAME.getName()));
				String payid = (String) dbobj.get(FieldType.PAY_ID.getName());

				User user1 = new User();
				if (userMap.get(payid) != null && !userMap.get(payid).getPayId().isEmpty()) {
					user1 = userMap.get(payid);
				} else {
					user1 = userdao.findPayId(payid);
					userMap.put(payid, user1);
				}

				if (user1 == null) {
					transReport.setMerchants(CrmFieldConstants.NA.getValue());
				} else {
					transReport.setMerchants(user1.getBusinessName());
				}

				if (null != dbobj.getString(FieldType.ORIG_TXNTYPE.toString())) {
					transReport.setTxnType(dbobj.getString(FieldType.ORIG_TXNTYPE.toString()));
				}

				if (null != dbobj.getString(FieldType.MOP_TYPE.toString())) {
					transReport.setMopType(MopType.getmopName(dbobj.getString(FieldType.MOP_TYPE.toString())));
				} else {
					transReport.setMopType(CrmFieldConstants.NOT_AVAILABLE.getValue());
				}
				if (null != dbobj.getString(FieldType.PAYMENT_TYPE.toString())) {
					transReport.setPaymentMethods(
							PaymentType.getpaymentName(dbobj.getString(FieldType.PAYMENT_TYPE.toString())));
				} else {
					transReport.setPaymentMethods(CrmFieldConstants.NA.getValue());
				}

				if (null != dbobj.getString(FieldType.PAYMENT_TYPE.toString())) {
					if (null != dbobj.getString(FieldType.CARD_MASK.toString())) {
						transReport.setCardNumber(dbobj.getString(FieldType.CARD_MASK.toString()));
					} else if ((dbobj.getString(FieldType.PAYMENT_TYPE.getName()))
							.equals(PaymentType.NET_BANKING.getCode())) {
						transReport.setCardNumber(CrmFieldConstants.NET_BANKING.getValue());
					} else if ((dbobj.getString(FieldType.PAYMENT_TYPE.getName()))
							.equals(PaymentType.WALLET.getCode())) {
						transReport.setCardNumber(CrmFieldConstants.WALLET.getValue());
					}
				} else {
					transReport.setCardNumber(CrmFieldConstants.NA.getValue());
				}
				transReport.setRrn(dbobj.getString(FieldType.RRN.toString()));

				transReport.setIpaddress(dbobj.getString(FieldType.INTERNAL_CUST_IP.toString()));

				transReport.setCardMask(dbobj.getString(FieldType.CARD_MASK.toString()));

				transReport.setRrn(dbobj.getString(FieldType.RRN.toString()));
				transReport.setStatus(dbobj.getString(FieldType.STATUS.toString()));
				transReport.setDateFrom(dbobj.getString(FieldType.CREATE_DATE.getName()));
				transReport.setAmount(dbobj.getString(FieldType.AMOUNT.toString()));
				transReport.setTotalAmount(dbobj.getString(FieldType.TOTAL_AMOUNT.toString()));
				transReport.setOrderId(dbobj.getString(FieldType.ORDER_ID.toString()));
				transReport.setIpaddress(dbobj.getString(FieldType.INTERNAL_CUST_IP.toString()));
				if (StringUtils.isNotBlank(dbobj.getString(FieldType.REFUND_ORDER_ID.toString()))) {
					transReport.setRefundOrderId(dbobj.getString(FieldType.REFUND_ORDER_ID.toString()));
				} else {
					transReport.setRefundOrderId(CrmFieldConstants.NA.getValue());
				}
				transReport.setoId(dbobj.getString(FieldType.OID.toString()));

				if (null != dbobj.getString(FieldType.CURRENCY_CODE.toString())) {
					transReport.setCurrency(dbobj.getString(FieldType.CURRENCY_CODE.toString()));
				} else {
					transReport.setCurrency(CrmFieldConstants.NA.getValue());
				}

				if (null != dbobj.getString(FieldType.ACQUIRER_TYPE.toString())) {
					transReport.setAcquirerType(dbobj.getString(FieldType.ACQUIRER_TYPE.toString()));
				}

				if (StringUtils.isNotBlank(dbobj.getString(FieldType.PG_TDR_SC.toString()))) {
					transReport.setPgSurchargeAmount(
							String.valueOf( ((Double.valueOf(dbobj.getString(FieldType.PG_TDR_SC.toString()))))));
				} else {
					transReport.setPgSurchargeAmount("0.00");
				}

				if (StringUtils.isNotBlank(dbobj.getString(FieldType.ACQUIRER_TDR_SC.toString()))) {
					transReport.setAcquirerSurchargeAmount(String.valueOf(
							(Double.valueOf(dbobj.getString(FieldType.ACQUIRER_TDR_SC.toString())))));
				} else {
					transReport.setAcquirerSurchargeAmount("0.00");
				}

				if (StringUtils.isNotBlank(dbobj.getString(FieldType.PG_GST.toString()))) {
					transReport.setPgGst(
							String.valueOf( ((Double.valueOf(dbobj.getString(FieldType.PG_GST.toString()))))));
				} else {
					transReport.setPgGst("0.00");
				}

				if (StringUtils.isNotBlank(dbobj.getString(FieldType.ACQUIRER_GST.toString()))) {
					transReport.setAcquirerGst(String.valueOf(
							(Double.valueOf(dbobj.getString(FieldType.ACQUIRER_GST.toString())))));
				} else {
					transReport.setAcquirerGst("0.00");
				}
				transactionList.add(transReport);
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.info(" Exception Occur in class TransactionStatus in getForceCapturedReport(): ");

			return transactionList;
		} finally {
			session.close();
		}
		logger.info("TransactionStatus in getForceCapturedReport() List Size : " + transactionList.size());
		return transactionList;

	}

	public List<TDRBifurcationReportDetails> downloadForceCapturedReport(String payId, String status, String acquirer,
			String dateFrom, String dateTo) {
		List<TDRBifurcationReportDetails> listToReturn = new ArrayList<TDRBifurcationReportDetails>();
		Session session = HibernateSessionProvider.getSession();

		try {

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));
			MongoCursor<Document> cursor = null;
			BasicDBObject match = new BasicDBObject();

			if (!StringUtils.isBlank(dateFrom) && !StringUtils.isBlank(dateTo)) {
				match.put(FieldType.DATE_INDEX.getName(),
						new Document("$gte", dateFrom.replaceAll("-", "")).append("$lte", dateTo.replaceAll("-", "")));
				if (!status.equalsIgnoreCase("All")) {
					if (status.equalsIgnoreCase("1") || status.equalsIgnoreCase("0")) {
						match.put(FieldType.HOLD_RELEASE.getName(), Integer.parseInt(status));
					} else {
						match.put(FieldType.STATUS.getName(), status);
					}
				} else {
					match.put(FieldType.STATUS.getName(),
							new Document("$in",
									Arrays.asList(StatusType.CAPTURED.getName(),
											StatusType.SETTLED_RECONCILLED.getName(),
											StatusType.SETTLED_SETTLE.getName(), StatusType.FORCE_CAPTURED.getName())));
				}
			}

			if (!acquirer.equalsIgnoreCase("All")) {
				match.put(FieldType.ACQUIRER_TYPE.getName(), acquirer);
			}
			if (!payId.equalsIgnoreCase("All")) {
				match.put(FieldType.PAY_ID.getName(), payId);
			}

			BasicDBObject matchQ = new BasicDBObject("$match", match);
//BasicDBObject sort = new BasicDBObject("$sort", new BasicDBObject("CREATE_DATE", -1));
//BasicDBObject skip = new BasicDBObject("$skip", start);
//BasicDBObject limit = new BasicDBObject("$limit", length);
			List<BasicDBObject> queryExecute = Arrays.asList(matchQ);

			logger.info(" TransactionStatus getTDRBurificationReport() Query: \t" + queryExecute);
			cursor = coll.aggregate(queryExecute).iterator();
// logger.info(" Mongo result size" + queryExecute.size());
			while (cursor.hasNext()) {
				double igst = 0.0;
				double csgst = 0.0;
				Document doc = (Document) cursor.next();
// logger.info(" mongo documet to string " + doc.toJson());
				TDRBifurcationReportDetails forceCaptureReportDetails = new TDRBifurcationReportDetails();

				/*
				 * Query query = session .createQuery("From User where payId='" +
				 * doc.getString(FieldType.PAY_ID.toString()) + "'"); List<User> list =
				 * query.list(); logger.info(" User list: " + list.size());
				 */
//System.out.println(doc);
// get acq id
				User user = new User();
// logger.info(" acquire type " +
// doc.getString(FieldType.ACQUIRER_TYPE.toString()));
				String acqType = doc.getString(FieldType.ACQUIRER_TYPE.toString());
// logger.info(FieldType.PAY_ID.toString() + " : " +
// doc.getString(FieldType.PAY_ID.toString()));
				user = userdao.findPayId(doc.getString(FieldType.PAY_ID.toString()));

// logger.info(user.toString());
// logger.info(acqType);
// Account acc = user.getAccountUsingAcquirerCode(acqType);
				String mid = null;
				String currency = doc.getString(FieldType.CURRENCY_CODE.toString());
// if (acc != null)
// mid = acc.getAccountCurrency(currency).getMerchantId();
				forceCaptureReportDetails.setTxnId(doc.getString(FieldType.TXN_ID.toString()));
				forceCaptureReportDetails.setPgRefNo(doc.getString(FieldType.PG_REF_NUM.toString()));
				forceCaptureReportDetails.setMerchantName(user.getBusinessName());
				forceCaptureReportDetails.setAcquirer(doc.getString(FieldType.ACQUIRER_TYPE.toString()));
				forceCaptureReportDetails.setDate(doc.getString(FieldType.CREATE_DATE.toString()));
				forceCaptureReportDetails.setOrderId(doc.getString(FieldType.ORDER_ID.toString()));
				forceCaptureReportDetails.setPaymentMethod(doc.getString(FieldType.PAYMENT_TYPE.toString()));
				forceCaptureReportDetails.setTxnType(doc.getString(FieldType.TXNTYPE.toString()));
				forceCaptureReportDetails.setStatus(doc.getString(FieldType.STATUS.toString()));
				forceCaptureReportDetails.setTransactionRegion(doc.getString(FieldType.PAYMENTS_REGION.toString()));
				forceCaptureReportDetails.setBaseAmount((doc.getString(FieldType.AMOUNT.toString()) != null)
						? doc.getString(FieldType.AMOUNT.toString())
						: "0.0");
				forceCaptureReportDetails.setTotalAmount((doc.getString(FieldType.TOTAL_AMOUNT.toString()) != null)
						? doc.getString(FieldType.TOTAL_AMOUNT.toString())
						: "0.0");
//forceCaptureReportDetails.setTotalAmount(doc.getString(FieldType.TOTAL_AMOUNT.toString()));
				forceCaptureReportDetails.setDeltaRefundFlag(doc.getString(FieldType.DELTA_REFUND_FLAG.toString()));
				forceCaptureReportDetails.setAcqId(mid);
// forceCaptureReportDetails.setAcqId(doc.getString(FieldType.ACQ_ID.toString()));
				forceCaptureReportDetails.setRRN(doc.getString(FieldType.RRN.toString()));
				forceCaptureReportDetails.setPostSettledFlag(doc.getString(FieldType.POST_SETTLED_FLAG.toString()));
				forceCaptureReportDetails.setRefundOrderId(doc.getString(FieldType.REFUND_ORDER_ID.toString()));
				forceCaptureReportDetails.setRefundFlag(doc.getString(FieldType.REFUND_FLAG.toString()));

				String tdr = "0.0";
				if (doc.getString(FieldType.MOP_TYPE.toString()) != null)
					forceCaptureReportDetails
							.setMopType(MopType.getmop(doc.getString(FieldType.MOP_TYPE.toString())).toString());
				forceCaptureReportDetails.setAcquirerTDRFixed(doc.getString(FieldType.ACQUIRER_TDR_SC.toString()));

//double serviceTaxCharges = serviceTaxDao
//.findServiceTaxByPayId(doc.getString(FieldType.PAY_ID.toString())).doubleValue();
//logger.info("serviceTaxCharges : " + serviceTaxCharges);

				logger.info("Pararmeter For get charging Detail : payId: :" + doc.getString(FieldType.PAY_ID.toString())
						+ "\tpaymentType: "
						+ PaymentType.getInstanceUsingCode(doc.getString(FieldType.PAYMENT_TYPE.toString())).toString()
						+ "\tmopType : " + MopType.getmop(doc.getString(FieldType.MOP_TYPE.toString())).toString()
						+ "\tacquirerName : "
						+ AcquirerType.getAcquirerName(doc.getString(FieldType.ACQUIRER_TYPE.toString()))
						+ "\tcreatedDate : " + doc.getString(FieldType.CREATE_DATE.toString()));

// ChargingDetails chargingDetailsObj=
// chargingDetailsDao.findActiveChargingDetail(MopType.getmopName(doc.getString(FieldType.MOP_TYPE.toString())),
// doc.getString(FieldType.PAYMENT_TYPE.toString()),
// doc.getString(FieldType.TXNTYPE.toString()),
// doc.getString(FieldType.ACQUIRER_TYPE.toString()),
// doc.getString(FieldType.CURRENCY_CODE.toString()),
// doc.getString(FieldType.PAY_ID.toString()))
// Query for charging details
//List<ChargingDetails> chargingDetails = chargingDetailsDao.findDetail(
//doc.getString(FieldType.CREATE_DATE.toString()), doc.getString(FieldType.PAY_ID.toString()),
//doc.getString(FieldType.ACQUIRER_TYPE.toString()),
//PaymentType.getInstanceUsingCode(doc.getString(FieldType.PAYMENT_TYPE.toString())).toString(),
//MopType.getmop(doc.getString(FieldType.MOP_TYPE.toString())).toString(),
//doc.getString(FieldType.CURRENCY_CODE.toString()), doc.getString(FieldType.TXNTYPE.toString()));
//List<ChargingDetails> chargingDetails = new ArrayList<ChargingDetails>();
////	Query<ChargingDetails> chargingdetailquery = session.createNativeQuery(
//"select * from ChargingDetails WHERE payId=:payId and paymentType=:paymentType and mopType=:mopType and acquirerName=:acquirerName and createdDate<=:createdDate  order by createdDate desc",
//ChargingDetails.class);
//chargingdetailquery.setParameter("payId", doc.getString(FieldType.PAY_ID.toString()));
//chargingdetailquery.setParameter("paymentType",
//PaymentType.getInstanceUsingCode(doc.getString(FieldType.PAYMENT_TYPE.toString())).toString());
//chargingdetailquery.setParameter("mopType",
//MopType.getmop(doc.getString(FieldType.MOP_TYPE.toString())).toString());
//chargingdetailquery.setParameter("acquirerName",
//AcquirerType.getAcquirerName(doc.getString(FieldType.ACQUIRER_TYPE.toString())));
//chargingdetailquery.setParameter("createdDate", doc.getString(FieldType.CREATE_DATE.toString()));
//chargingDetails = chargingdetailquery.getResultList();
//logger.info(" charging details Query " + chargingdetailquery.getQueryString());
//logger.info(" charging details lise size " + chargingDetails.size());
//ChargingDetails details = chargingDetails.get(0);

//if (chargingDetails.size() == 0) {
//chargingDetails = chargingDetailsDao.findDetailForTDR(
//doc.getString(FieldType.CREATE_DATE.toString()), doc.getString(FieldType.PAY_ID.toString()),
//doc.getString(FieldType.ACQUIRER_TYPE.toString()),
//PaymentType.getInstanceUsingCode(doc.getString(FieldType.PAYMENT_TYPE.toString()))
//.toString(),
//MopType.getmop(doc.getString(FieldType.MOP_TYPE.toString())).toString(),
//doc.getString(FieldType.CURRENCY_CODE.toString()),
//doc.getString(FieldType.TXNTYPE.toString()));
//
//logger.info(" charging details lise size " + chargingDetails.size());
//}

//logger.info(" Charging details set :" + new Gson().toJson(details));
// logger.info(" User details " + user.toString());
// Acquirer
				forceCaptureReportDetails.setSurchargeFlag(doc.getString(FieldType.SURCHARGE_FLAG.getName()) != null
						? doc.getString(FieldType.SURCHARGE_FLAG.getName())
						: "N");
				if (doc.getString(FieldType.SURCHARGE_FLAG.getName()) != null
						&& doc.getString(FieldType.SURCHARGE_FLAG.getName()).equalsIgnoreCase("Y")) {
//forceCaptureReportDetails.setAcquirerTDRPercentage(String.valueOf( details.getBankTDR()));
//forceCaptureReportDetails.setAcquirerTDRFixed(String.valueOf( details.getBankFixCharge()));
//forceCaptureReportDetails.setGstOnAcquirerTDR(String.valueOf( details.getBankServiceTax()));
//if (forceCaptureReportDetails.getAcquirerTDRPercentage() != null
//&& Double.valueOf(forceCaptureReportDetails.getAcquirerTDRPercentage()) > 0) {

//forceCaptureReportDetails.setAcquirerTDRAmontTotal(
//String.valueOf( (Double.parseDouble(forceCaptureReportDetails.getBaseAmount())
//* (details.getBankTDR() / 100))));
//} else {
//forceCaptureReportDetails.setAcquirerTDRAmontTotal(
//String.valueOf( chargingDetails.get(0).getBankFixCharge()));
//
//}

//forceCaptureReportDetails.setGstOnAcquirerTDR(String.valueOf(
//(Double.parseDouble(forceCaptureReportDetails.getAcquirerTDRAmontTotal())
//* (details.getBankServiceTax() / 100))));

// PG
//forceCaptureReportDetails.setPgTDRFixed(String.valueOf( details.getPgFixCharge()));
//forceCaptureReportDetails.setPgTDRPercentage(String.valueOf( details.getPgTDR()));
//forceCaptureReportDetails.setGstOnPgTDR(String.valueOf( details.getPgServiceTax()));

//if (forceCaptureReportDetails.getPgTDRPercentage() != null
//&& Double.valueOf(forceCaptureReportDetails.getPgTDRPercentage()) > 0) {

//forceCaptureReportDetails.setPgTDRAmontTotal(
//String.valueOf( (Double.parseDouble(forceCaptureReportDetails.getBaseAmount())
//* (details.getPgTDR() / 100))));

//} else {
//forceCaptureReportDetails.setPgTDRAmontTotal(forceCaptureReportDetails.getPgTDRFixed());
//
//}
//forceCaptureReportDetails.setGstOnPgTDR(
//String.valueOf( (Double.parseDouble(forceCaptureReportDetails.getPgTDRAmontTotal())
//* (details.getPgServiceTax() / 100))));

// Merchant
//forceCaptureReportDetails.setMerchantTDRPercentage(String.valueOf( details.getMerchantTDR()));
//forceCaptureReportDetails.setMerchantTDRFixed(String.valueOf( details.getMerchantFixCharge()));
//if (forceCaptureReportDetails.getMerchantTDRPercentage() != null
//&& Double.valueOf(forceCaptureReportDetails.getMerchantTDRPercentage()) > 0) {

					forceCaptureReportDetails.setTdr(
							String.valueOf( (Double.valueOf(forceCaptureReportDetails.getAcquirerTDRAmontTotal())
									+ Double.valueOf(forceCaptureReportDetails.getPgTDRAmontTotal()))));

//} else {
//forceCaptureReportDetails.setTdr(String.valueOf(chargingDetails.get(0).getMerchantFixCharge()));
//}

//System.out.println("Merchant Gst no " + user.getMerchantGstNo());
/// GST
// tdr = forceCaptureReportDetails.getTdr() != null ?
/// forceCaptureReportDetails.getTdr() : "0.0";
//if (user.getMerchantGstNo() != null && !user.getMerchantGstNo().startsWith("07")) {
//igst = serviceTaxCharges * Double.valueOf(tdr) / 100;
// System.out.println("GST before FORMATTING " + igst);
					forceCaptureReportDetails.setIgst18(
							String.valueOf( (Double.valueOf(forceCaptureReportDetails.getGstOnAcquirerTDR())
									+ Double.valueOf(forceCaptureReportDetails.getGstOnPgTDR()))));
// System.out.println("GST AFTER FORMATTING " +
// forceCaptureReportDetails.getIgst18());

//} else {
//csgst = (serviceTaxCharges / 2) * Double.valueOf(tdr) / 100;
//forceCaptureReportDetails.setCgst9(String.valueOf( csgst));
//forceCaptureReportDetails.setSgst9(String.valueOf( csgst));
//}
// net amount

// System.out.println(forceCaptureReportDetails.toString());
					double payableToMerchant = (Double.parseDouble(forceCaptureReportDetails.getAcquirerTDRAmontTotal())
							+ Double.parseDouble(forceCaptureReportDetails.getGstOnAcquirerTDR())
							+ Double.parseDouble(forceCaptureReportDetails.getPgTDRAmontTotal())
							+ Double.parseDouble(forceCaptureReportDetails.getGstOnPgTDR()));
					double fibalPayableToMerchant = Double.parseDouble(forceCaptureReportDetails.getTotalAmount())
							- payableToMerchant;

					double receiveInNodal = (Double.parseDouble(forceCaptureReportDetails.getAcquirerTDRAmontTotal())
							+ Double.parseDouble(forceCaptureReportDetails.getGstOnAcquirerTDR()));
					double finalReceiveInNodal = Double.parseDouble(forceCaptureReportDetails.getTotalAmount())
							- receiveInNodal;
					if (forceCaptureReportDetails.getRefundFlag() != null
							&& forceCaptureReportDetails.getRefundFlag().trim().length() > 0) {

						if (user.getIndustryCategory() != null
								&& user.getIndustryCategory().equalsIgnoreCase("EDUCATION")) {
							forceCaptureReportDetails
									.setAmountPaybleToMerchant("-" + String.valueOf(fibalPayableToMerchant));
						} else {
							forceCaptureReportDetails
									.setAmountPaybleToMerchant("-" + String.valueOf(fibalPayableToMerchant));
						}

					} else {
						forceCaptureReportDetails
								.setAmountPaybleToMerchant(String.valueOf( fibalPayableToMerchant));
					}

					forceCaptureReportDetails.setAmountreceivedInNodal(String.valueOf(finalReceiveInNodal));
				} else {
//forceCaptureReportDetails.setAcquirerTDRPercentage(String.valueOf( details.getBankTDR()));
//forceCaptureReportDetails.setAcquirerTDRFixed(String.valueOf( details.getBankFixCharge()));
//forceCaptureReportDetails.setGstOnAcquirerTDR(String.valueOf( details.getBankServiceTax()));
//if (forceCaptureReportDetails.getAcquirerTDRPercentage() != null
//&& Double.valueOf(forceCaptureReportDetails.getAcquirerTDRPercentage()) > 0) {
					forceCaptureReportDetails.setAcquirerTDRAmontTotal(String.valueOf(
							Double.valueOf(doc.getString(FieldType.ACQUIRER_TDR_SC.getName()) != null
									? doc.getString(FieldType.ACQUIRER_TDR_SC.getName())
									: "0")));
//} else {
//forceCaptureReportDetails.setAcquirerTDRAmontTotal(
//String.valueOf( chargingDetails.get(0).getBankFixCharge()));
//
//}
					forceCaptureReportDetails.setGstOnAcquirerTDR(String.valueOf(
							Double.valueOf(doc.getString(FieldType.ACQUIRER_GST.getName()) != null
									? doc.getString(FieldType.ACQUIRER_GST.getName())
									: "0")));

// PG
//forceCaptureReportDetails.setPgTDRFixed(String.valueOf( details.getPgFixCharge()));
//forceCaptureReportDetails.setPgTDRPercentage(String.valueOf( details.getPgTDR()));
//forceCaptureReportDetails.setGstOnPgTDR(String.valueOf( details.getPgServiceTax()));

//if (forceCaptureReportDetails.getPgTDRPercentage() != null
//&& Double.valueOf(forceCaptureReportDetails.getPgTDRPercentage()) > 0) {

					forceCaptureReportDetails.setPgTDRAmontTotal(String.valueOf(
							Double.valueOf(doc.getString(FieldType.PG_TDR_SC.getName()) != null
									? doc.getString(FieldType.PG_TDR_SC.getName())
									: "0")));

//} else {
//forceCaptureReportDetails.setPgTDRAmontTotal(forceCaptureReportDetails.getPgTDRFixed());
//
//}
					forceCaptureReportDetails.setGstOnPgTDR(String.valueOf(
							Double.valueOf(doc.getString(FieldType.PG_GST.getName()) != null
									? doc.getString(FieldType.PG_GST.getName())
									: "0")));

// Merchant
//forceCaptureReportDetails.setMerchantTDRPercentage(String.valueOf( details.getMerchantTDR()));
//forceCaptureReportDetails.setMerchantTDRFixed(String.valueOf( details.getMerchantFixCharge()));
//if (forceCaptureReportDetails.getMerchantTDRPercentage() != null
//&& Double.valueOf(forceCaptureReportDetails.getMerchantTDRPercentage()) > 0) {

					forceCaptureReportDetails.setTdr(
							String.valueOf( (Double.valueOf(forceCaptureReportDetails.getPgTDRAmontTotal())
									+ Double.valueOf(forceCaptureReportDetails.getAcquirerTDRAmontTotal()))));

//} else {
//forceCaptureReportDetails.setTdr(String.valueOf(chargingDetails.get(0).getMerchantFixCharge()));
//}

// System.out.println("Merchant Gst no " + user.getMerchantGstNo());
/// GST
// tdr = forceCaptureReportDetails.getTdr() != null ?
// forceCaptureReportDetails.getTdr() : "0.0";
//if (user.getMerchantGstNo() != null && !user.getMerchantGstNo().startsWith("07")) {
// igst = serviceTaxCharges * Double.valueOf(tdr) / 100;
// System.out.println("GST before FORMATTING " + igst);
					forceCaptureReportDetails
							.setIgst18(String.valueOf( (Double.valueOf(forceCaptureReportDetails.getGstOnPgTDR())
									+ Double.valueOf(forceCaptureReportDetails.getGstOnAcquirerTDR()))));
// System.out.println("GST AFTER FORMATTING " +
// forceCaptureReportDetails.getIgst18());

//} else {
//csgst = (serviceTaxCharges / 2) * Double.valueOf(tdr) / 100;
//forceCaptureReportDetails.setCgst9(String.valueOf( csgst));
//forceCaptureReportDetails.setSgst9(String.valueOf( csgst));
//}
//// net amount

// System.out.println(forceCaptureReportDetails.toString());

					double payableToMerchant = (Double.parseDouble(forceCaptureReportDetails.getAcquirerTDRAmontTotal())
							+ Double.parseDouble(forceCaptureReportDetails.getGstOnAcquirerTDR())
							+ Double.parseDouble(forceCaptureReportDetails.getPgTDRAmontTotal())
							+ Double.parseDouble(forceCaptureReportDetails.getGstOnPgTDR()));
					double fibalPayableToMerchant = Double.parseDouble(forceCaptureReportDetails.getTotalAmount())
							- payableToMerchant;

					double receiveInNodal = (Double.parseDouble(forceCaptureReportDetails.getAcquirerTDRAmontTotal())
							+ Double.parseDouble(forceCaptureReportDetails.getGstOnAcquirerTDR()));
					double finalReceiveInNodal = Double.parseDouble(forceCaptureReportDetails.getTotalAmount())
							- receiveInNodal;
					if (forceCaptureReportDetails.getRefundFlag() != null
							&& forceCaptureReportDetails.getRefundFlag().trim().length() > 0) {

						if (user.getIndustryCategory().equalsIgnoreCase("EDUCATION")) {
							forceCaptureReportDetails.setAmountPaybleToMerchant("-" + fibalPayableToMerchant);
						} else {
							forceCaptureReportDetails.setAmountPaybleToMerchant("-" + fibalPayableToMerchant);
						}

					} else {
						forceCaptureReportDetails
								.setAmountPaybleToMerchant(String.valueOf( fibalPayableToMerchant));
					}
//forceCaptureReportDetails.setAmountreceivedInNodal(String.valueOf(Double
//.valueOf(forceCaptureReportDetails.getAmountPaybleToMerchant() != null
//? forceCaptureReportDetails.getAmountPaybleToMerchant()
//: "0")
//+ Double.valueOf(forceCaptureReportDetails.getPgTDRAmontTotal() != null
//? forceCaptureReportDetails.getPgTDRAmontTotal()
//: "0")));

					forceCaptureReportDetails.setAmountreceivedInNodal(String.valueOf( finalReceiveInNodal));
				}

// System.out.println(forceCaptureReportDetails.toString());
				forceCaptureReportDetails.setSettlementDate(doc.getString(FieldType.SETTLEMENT_DATE_INDEX.getName()));

				forceCaptureReportDetails.setAmountReceivedNodalBank("ICICI Bank");

				if (!doc.getString(FieldType.STATUS.getName()).equalsIgnoreCase(StatusType.SETTLED_SETTLE.getName())) {
					forceCaptureReportDetails.setSettlementTat(null);
				} else {
					forceCaptureReportDetails.setSettlementTat("T+" + String.valueOf(user.getSettlementDays()));
				}

				forceCaptureReportDetails.setAccountHolderName(user.getAccHolderName());
				forceCaptureReportDetails.setAccountNumber(user.getAccountNo());
				forceCaptureReportDetails.setIfscCode(user.getIfscCode());
				forceCaptureReportDetails.setTransactionIdentifer(
						StringUtils.isBlank(doc.getString(FieldType.UDF6.getName())) ? "NORMAL" : "Split");
				forceCaptureReportDetails
						.setLiabilityHoldRemakrs(doc.getString(FieldType.LIABILITYHOLDREMARKS.getName()));
				forceCaptureReportDetails
						.setLiabilityReleaseRemakrs(doc.getString(FieldType.LIABILITYRELEASEREMARKS.getName()));
				forceCaptureReportDetails.setUtrNumber(String.valueOf(doc.get(FieldType.UTR_NO.getName())));

				if (!doc.getString(FieldType.STATUS.getName()).equalsIgnoreCase(StatusType.SETTLED_SETTLE.getName())) {
					forceCaptureReportDetails.setSettlementPeriod("0");
				} else {
					forceCaptureReportDetails.setSettlementPeriod(settlementPeriod(
							doc.getString(FieldType.DATE_INDEX.getName()),
							doc.getString(FieldType.SETTLEMENT_DATE_INDEX.getName()), user.getSettlementDays()));
				}

				listToReturn.add(forceCaptureReportDetails);
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.info(" Exception Occur in class TransactionStatus in getTDRBurificationReport(): ");

			return listToReturn;
		} finally {
			session.close();
		}
		logger.info("TransactionStatus in getTDRBurificationReport() List Size : " + listToReturn.size());
		return listToReturn;

	}
}
