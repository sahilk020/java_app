package com.pay10.crm.mongoReports;



import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.GsonJsonParser;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.pay10.commons.dto.DashboardReportCount;
import com.pay10.commons.dto.ReportingCollection;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.user.TransactionSearchNew;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.MopType;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.PaymentTypeUI;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;






@Component
public class DashboardService {

    private static Logger logger = LoggerFactory.getLogger(DashboardService.class.getName());
    private static final String prefix = "MONGO_DB_";
    private static Map<String, User> userMap = new HashMap<String, User>();
    
    @Autowired
    private UserDao userdao;


    @Autowired
    private MongoInstance mongoInstance;

    public DashboardReportCount getDashboardReportCount() {
    	DashboardReportCount count=null;
    	try {
    		count=new DashboardReportCount();
    		
    		Date date=new java.util.Date();
    		String todayDate=new SimpleDateFormat("yyyyMMdd").format(date);
    		String sDate=new SimpleDateFormat("yyyy-MM-dd").format(date);
    		String eDate=sDate+" 23:59:59";
    		sDate=sDate+" 00:00:00";
    		MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));
			
			BasicDBObject matchRNS=new BasicDBObject();
			matchRNS.put(FieldType.STATUS.getName(), StatusType.SETTLED_RECONCILLED.getName());
			matchRNS.put(FieldType.DATE_INDEX.getName(),todayDate);
			
			List<BasicDBObject> rnsQuery = Arrays.asList(new BasicDBObject("$match", matchRNS));
			
			logger.info("Rns Count query : "+rnsQuery);
			int rnsCount=coll.aggregate(rnsQuery).into(new ArrayList<>()).size();
			
			count.setRnsCount(rnsCount);
			
			
			BasicDBObject matchForceCapture=new BasicDBObject();
			matchForceCapture.put(FieldType.STATUS.getName(), StatusType.FORCE_CAPTURED.getName());
//			matchForceCapture.put(FieldType.DATE_INDEX.getName(),todayDate);
			matchForceCapture.put(FieldType.SETTLEMENT_DATE.getName(),new BasicDBObject("$gte", sDate).append("$lte", eDate));
			
			List<BasicDBObject> forceCaptureQuery = Arrays.asList(new BasicDBObject("$match", matchForceCapture));
			
			logger.info("Force Capture Count query : "+forceCaptureQuery);
			int forceCaptureCount=coll.aggregate(forceCaptureQuery).into(new ArrayList<>()).size();
			
			count.setForceCaptureCount(forceCaptureCount);
    		double exceptionCount=getCountException(sDate, eDate).getExceptionCount();
			count.setExceptionCount(exceptionCount);
			
		} catch (Exception e) {
			logger.error("Exception in getDashboardReportCount :",e);
			e.printStackTrace();
		}
    	return count;
    }
    public DashboardReportCount getCountException(String startDate, String endDate) {
    	DashboardReportCount count=null;
    	try {
    		count=new DashboardReportCount();
    		MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.REPORTING_COLLECTION_NAME.getValue()));
			
			BasicDBObject matchRNS=new BasicDBObject();
			matchRNS.put(FieldType.CREATE_DATE.getName(),new BasicDBObject("$gte", startDate).append("$lte", endDate));
			
			List<BasicDBObject> rnsQuery = Arrays.asList(new BasicDBObject("$match", matchRNS));
			
			logger.info("Exception Count query : "+rnsQuery);
			int rnsCount=coll.aggregate(rnsQuery).into(new ArrayList<>()).size();	
			count.setExceptionCount(rnsCount);
		
		} catch (Exception e) {
			logger.error("Exception in getDashboardReportCount :",e);
			e.printStackTrace();
		}
    	return count;
    }
    public int dashboardReportCount(String type, int start, int length) {
    	Date date=new java.util.Date();
		String todayDate=new SimpleDateFormat("yyyyMMdd").format(date);
		String sDate=new SimpleDateFormat("yyyy-MM-dd").format(date);
		String eDate=sDate+" 23:59:59";
		sDate=sDate+" 00:00:00";
		try {

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));
			 
			
			BasicDBObject match = new BasicDBObject();
			//match.put(FieldType.DATE_INDEX.getName(), todayDate);
			if (type.equalsIgnoreCase("RNS")) {
				match.put(FieldType.SETTLEMENT_DATE_INDEX.getName(), todayDate);
				match.put(FieldType.STATUS.getName(),StatusType.SETTLED_RECONCILLED.getName());
			}else if(type.equalsIgnoreCase("Force Capture")) {
				match.put(FieldType.SETTLEMENT_DATE.getName(),new BasicDBObject("$gte", sDate).append("$lte", eDate));
				match.put(FieldType.STATUS.getName(),StatusType.FORCE_CAPTURED.getName());
			}else {
				match.put(FieldType.SETTLEMENT_DATE.getName(),new BasicDBObject("$gte", sDate).append("$lte", eDate));
				match.put(FieldType.STATUS.getName(),StatusType.FORCE_CAPTURED.getName());
			}
			
			BasicDBObject matchQ = new BasicDBObject("$match", match);
			
			List<BasicDBObject> pipeline = Arrays.asList(matchQ);
			logger.info("Query : "+pipeline);
			return coll.aggregate(pipeline).into(new ArrayList<>()).size();
		} catch (Exception e) {
			logger.error("Exception in search payment for admin", e);
		}
		return 0;
	}
	public List<TransactionSearchNew> dashboardReport(String type, int start, int length) {
		Date date=new java.util.Date();
		String todayDate=new SimpleDateFormat("yyyyMMdd").format(date);
		String sDate=new SimpleDateFormat("yyyy-MM-dd").format(date);
		String eDate=sDate+" 23:59:59";
		sDate=sDate+" 00:00:00";
		List<TransactionSearchNew> transactionList = new ArrayList<TransactionSearchNew>();

		try {

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));
			 
		
			
			BasicDBObject match = new BasicDBObject();
//			match.put(FieldType.DATE_INDEX.getName(), todayDate);
			if (type.equalsIgnoreCase("RNS")) {
				match.put(FieldType.SETTLEMENT_DATE_INDEX.getName(), todayDate);
				match.put(FieldType.STATUS.getName(),StatusType.SETTLED_RECONCILLED.getName());
			}else if(type.equalsIgnoreCase("Force Capture")) {
				match.put(FieldType.SETTLEMENT_DATE.getName(),new BasicDBObject("$gte", sDate).append("$lte", eDate));
				match.put(FieldType.STATUS.getName(),StatusType.FORCE_CAPTURED.getName());
			}else {
				match.put(FieldType.SETTLEMENT_DATE.getName(),new BasicDBObject("$gte", sDate).append("$lte", eDate));
				match.put(FieldType.STATUS.getName(),StatusType.FORCE_CAPTURED.getName());
			}
			
			BasicDBObject matchQ = new BasicDBObject("$match", match);
			BasicDBObject sort = new BasicDBObject("$sort", new BasicDBObject("CREATE_DATE", -1));
			BasicDBObject skip  = new BasicDBObject("$skip", start);
			BasicDBObject limit = new BasicDBObject("$limit", length);
			List<BasicDBObject> pipeline = Arrays.asList(matchQ, sort,skip, limit);
			
			logger.info("Query : "+pipeline);
			
			AggregateIterable<Document> output = coll.aggregate(pipeline);
			output.allowDiskUse(true);
			MongoCursor<Document> cursor = output.iterator();

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

				// end by deep

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

				// end by deep

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
				if (null != dbobj.getString(FieldType.ACCT_ID.toString())) {
					transReport.setTransactRef(dbobj.getString(FieldType.ACCT_ID.toString()));
				} else {
					transReport.setTransactRef(dbobj.getString(CrmFieldConstants.NA.getValue()));
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
				}
				else {
					transReport.setMerchants(user1.getBusinessName());
//					transReport.setMerchants("N/A");
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
				if (String.valueOf(dbobj.get(FieldType.PAYMENT_TYPE.getName()))
						.equalsIgnoreCase(PaymentTypeUI.CREDIT_CARD.getCode())
						|| String.valueOf(dbobj.get(FieldType.PAYMENT_TYPE.getName()))
								.equalsIgnoreCase(PaymentTypeUI.DEBIT_CARD.getCode())
						|| String.valueOf(dbobj.get(FieldType.PAYMENT_TYPE.getName()))
								.equalsIgnoreCase(PaymentTypeUI.NET_BANKING.getCode())) {
					transReport.setCardHolderType(String.valueOf(
							dbobj.get(FieldType.CARD_HOLDER_TYPE.getName()) == null ? CrmFieldConstants.NA.getValue()
									: dbobj.get(FieldType.CARD_HOLDER_TYPE.getName())));
				} else {
					transReport.setCardHolderType(CrmFieldConstants.NA.getValue());
				}
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
							String.format("%.2f", ((Double.valueOf(dbobj.getString(FieldType.PG_TDR_SC.toString()))))));
				} else {
					transReport.setPgSurchargeAmount("0.00");
				}

				if (StringUtils.isNotBlank(dbobj.getString(FieldType.ACQUIRER_TDR_SC.toString()))) {
					transReport.setAcquirerSurchargeAmount(String.format("%.2f",
							(Double.valueOf(dbobj.getString(FieldType.ACQUIRER_TDR_SC.toString())))));
				} else {
					transReport.setAcquirerSurchargeAmount("0.00");
				}

				if (StringUtils.isNotBlank(dbobj.getString(FieldType.PG_GST.toString()))) {
					transReport.setPgGst(
							String.format("%.2f", ((Double.valueOf(dbobj.getString(FieldType.PG_GST.toString()))))));
				} else {
					transReport.setPgGst("0.00");
				}

				if (StringUtils.isNotBlank(dbobj.getString(FieldType.ACQUIRER_GST.toString()))) {
					transReport.setAcquirerGst(String.format("%.2f",
							(Double.valueOf(dbobj.getString(FieldType.ACQUIRER_GST.toString())))));
				} else {
					transReport.setAcquirerGst("0.00");
				}
				transactionList.add(transReport);
			}
			cursor.close();
			logger.info("Inside TxnReports , searchPayment , transactionListSize = " + transactionList.size());
			return transactionList;

		} catch (Exception e) {
			logger.error("Exception in search payment for admin", e);
		}
		return transactionList;
	}


	//add new filename
	  public int dashboardExceptionReportCount(String type, int start, int length,String startDate, String endDate,String fileName) {
	    	
			try {

				MongoDatabase dbIns = mongoInstance.getDB();
				MongoCollection<Document> coll = dbIns.getCollection(
						PropertiesManager.propertiesMap.get(prefix + Constants.REPORTING_COLLECTION_NAME.getValue()));
				 
				//add new fileName in mongodb
				BasicDBObject match = new BasicDBObject();
				logger.info("FileName : " + fileName);
				if(StringUtils.isNoneBlank(fileName)) {
					fileName="/home/umesh/RR/reco_sftp/recoInput/"+fileName;
					match.put(FieldType.FILE_NAME.getName(), new BasicDBObject("$regex",fileName).append("$options","i"));
				}
				
				match.put(FieldType.CREATE_DATE.getName(),new BasicDBObject("$gte", startDate).append("$lte", endDate));
				
				
				BasicDBObject matchQ = new BasicDBObject("$match", match);
				
				List<BasicDBObject> pipeline = Arrays.asList(matchQ);
				logger.info("Query : "+pipeline);
				return coll.aggregate(pipeline).into(new ArrayList<>()).size();
			} catch (Exception e) {
				logger.error("Exception in search payment for admin", e);
			}
			return 0;
		}
		public List<ReportingCollection> dashboardExceptionReport(String type, int start, int length,String startDate, String endDate) {
			
			List<ReportingCollection> transactionList = new ArrayList<ReportingCollection>();

			try {

				MongoDatabase dbIns = mongoInstance.getDB();
				MongoCollection<Document> coll = dbIns.getCollection(
						PropertiesManager.propertiesMap.get(prefix + Constants.REPORTING_COLLECTION_NAME.getValue()));
				 
			
				
				BasicDBObject match = new BasicDBObject();
				
				
				match.put(FieldType.CREATE_DATE.getName(),new BasicDBObject("$gte", startDate).append("$lte", endDate));
				
				
				BasicDBObject matchQ = new BasicDBObject("$match", match);
				BasicDBObject sort = new BasicDBObject("$sort", new BasicDBObject("CREATE_DATE", -1));
				BasicDBObject skip  = new BasicDBObject("$skip", start);
				BasicDBObject limit = new BasicDBObject("$limit", length);
				List<BasicDBObject> pipeline = Arrays.asList(matchQ, sort,skip, limit);
				
				logger.info("Query : "+pipeline);
				
				AggregateIterable<Document> output = coll.aggregate(pipeline);
				output.allowDiskUse(true);
				MongoCursor<Document> cursor = output.iterator();

				while (cursor.hasNext()) {
					Document dbobj = cursor.next();
					ReportingCollection collection =new ReportingCollection();
					collection.set_id(String.valueOf(dbobj.get("_id")!=null?dbobj.get("_id"):"N/A"));
					collection.setcREATE_DATE(String.valueOf(dbobj.get("CREATE_DATE")!=null?dbobj.get("CREATE_DATE"):"N/A"));
					collection.setdB_ACQ_ID(String.valueOf(dbobj.get("DB_ACQ_ID")!=null?dbobj.get("DB_ACQ_ID"):"N/A"));
					collection.setdB_ACQUIRER_TYPE(String.valueOf(dbobj.get("DB_ACQUIRER_TYPE")!=null?dbobj.get("DB_ACQUIRER_TYPE"):"N/A"));
					collection.setdB_AMOUNT(String.valueOf(dbobj.get("DB_AMOUNT")!=null?dbobj.get("DB_AMOUNT"):"N/A"));
					collection.setdB_OID(String.valueOf(dbobj.get("DB_OID")!=null?dbobj.get("DB_OID"):"N/A"));
					collection.setdB_ORDER_ID(String.valueOf(dbobj.get("DB_ORDER_ID")!=null?dbobj.get("DB_ORDER_ID"):"N/A"));
					collection.setdB_ORIG_TXN_ID(String.valueOf(dbobj.get("DB_ORIG_TXN_ID")!=null?dbobj.get("DB_ORIG_TXN_ID"):"N/A"));
					collection.setdB_ORIG_TXNTYPE(String.valueOf(dbobj.get("DB_ORIG_TXNTYPE")!=null?dbobj.get("DB_ORIG_TXNTYPE"):"N/A"));
					collection.setdB_PAY_ID(String.valueOf(dbobj.get("DB_PAY_ID")!=null?dbobj.get("DB_PAY_ID"):"N/A"));
					collection.setdB_PG_REF_NUM(String.valueOf(dbobj.get("DB_PG_REF_NUM")!=null?dbobj.get("DB_PG_REF_NUM"):"N/A"));
					collection.setdB_TXN_ID(String.valueOf(dbobj.get("DB_TXN_ID")!=null?dbobj.get("DB_TXN_ID"):"N/A"));
					collection.setdB_TXNTYPE(String.valueOf(dbobj.get("DB_TXNTYPE")!=null?dbobj.get("DB_TXNTYPE"):"N/A"));
					collection.setdB_USER_TYPE(String.valueOf(dbobj.get("DB_TXNTYPE")!=null?dbobj.get("DB_TXNTYPE"):"N/A"));
					collection.setfILE_LINE_DATA(String.valueOf(dbobj.get("FILE_LINE_DATA")!=null?dbobj.get("FILE_LINE_DATA"):"N/A"));
					collection.setfILE_LINE_NO(String.valueOf(dbobj.get("FILE_LINE_NO")!=null?dbobj.get("FILE_LINE_NO"):"N/A"));
					collection.setfILE_NAME(String.valueOf(dbobj.get("FILE_NAME")!=null?dbobj.get("FILE_NAME"):"N/A"));
					collection.setrECO_EXCEPTION_STATUS(String.valueOf(dbobj.get("RECO_EXCEPTION_STATUS")!=null?dbobj.get("RECO_EXCEPTION_STATUS"):"N/A"));
					collection.setrECO_TXNTYPE(String.valueOf(dbobj.get("RECO_TXNTYPE")!=null?dbobj.get("RECO_TXNTYPE"):"N/A"));
					collection.setrESPONSE_CODE(String.valueOf(dbobj.get("RESPONSE_CODE")!=null?dbobj.get("RESPONSE_CODE"):"N/A"));
					collection.setrESPONSE_MESSAGE(String.valueOf(dbobj.get("RESPONSE_MESSAGE")!=null?dbobj.get("RESPONSE_MESSAGE"):"N/A"));
					collection.settXN_ID(String.valueOf(dbobj.get("TXN_ID")!=null?dbobj.get("TXN_ID"):"N/A"));
					collection.setuPDATE_DATE(String.valueOf(dbobj.get("UPDATE_DATE")!=null?dbobj.get("UPDATE_DATE"):"N/A"));
                    transactionList.add(collection);
				}
				cursor.close();
				logger.info("Inside TxnReports , searchPayment , transactionListSize = " + transactionList.size());
				return transactionList;

			} catch (Exception e) {
				logger.error("Exception in search payment for admin", e);
			}
			return transactionList;
		}

		//new add replica dashboardExceptionReportFile
public List<ReportingCollection> dashboardExceptionReportFile(String type, int start, int length,String startDate, String endDate,String fileName) {
			
			List<ReportingCollection> transactionList = new ArrayList<ReportingCollection>();

			try {

				MongoDatabase dbIns = mongoInstance.getDB();
				MongoCollection<Document> coll = dbIns.getCollection(
						PropertiesManager.propertiesMap.get(prefix + Constants.REPORTING_COLLECTION_NAME.getValue()));
				 
			//add new fileName in mongodb
				
				BasicDBObject match = new BasicDBObject();
				logger.info("FileName : " + fileName);
				if(StringUtils.isNoneBlank(fileName)) {
					fileName="/home/umesh/RR/reco_sftp/recoInput/"+fileName;
					match.put(FieldType.FILE_NAME.getName(), fileName);
				}
				
				
				match.put(FieldType.CREATE_DATE.getName(),new BasicDBObject("$gte", startDate).append("$lte", endDate));
				
				
				//BasicDBObject andCondition=new BasicDBObject("$and",match);
				
				BasicDBObject matchQ = new BasicDBObject("$match", match);
				BasicDBObject sort = new BasicDBObject("$sort", new BasicDBObject("CREATE_DATE", -1));
				BasicDBObject skip  = new BasicDBObject("$skip", start);
				BasicDBObject limit = new BasicDBObject("$limit", length);
				List<BasicDBObject> pipeline = Arrays.asList(matchQ, sort,skip, limit);
				
				logger.info("Query : "+pipeline);
				
				AggregateIterable<Document> output = coll.aggregate(pipeline);
				output.allowDiskUse(true);
				MongoCursor<Document> cursor = output.iterator();

				while (cursor.hasNext()) {
					Document dbobj = cursor.next();
					ReportingCollection collection =new ReportingCollection();
					collection.set_id(String.valueOf(dbobj.get("_id")!=null?dbobj.get("_id"):"N/A"));
					collection.setcREATE_DATE(String.valueOf(dbobj.get("CREATE_DATE")!=null?dbobj.get("CREATE_DATE"):"N/A"));
					collection.setdB_ACQ_ID(String.valueOf(dbobj.get("DB_ACQ_ID")!=null?dbobj.get("DB_ACQ_ID"):"N/A"));
					collection.setdB_ACQUIRER_TYPE(String.valueOf(dbobj.get("DB_ACQUIRER_TYPE")!=null?dbobj.get("DB_ACQUIRER_TYPE"):"N/A"));
					collection.setdB_AMOUNT(String.valueOf(dbobj.get("DB_AMOUNT")!=null?dbobj.get("DB_AMOUNT"):"N/A"));
					collection.setdB_OID(String.valueOf(dbobj.get("DB_OID")!=null?dbobj.get("DB_OID"):"N/A"));
					collection.setdB_ORDER_ID(String.valueOf(dbobj.get("DB_ORDER_ID")!=null?dbobj.get("DB_ORDER_ID"):"N/A"));
					collection.setdB_ORIG_TXN_ID(String.valueOf(dbobj.get("DB_ORIG_TXN_ID")!=null?dbobj.get("DB_ORIG_TXN_ID"):"N/A"));
					collection.setdB_ORIG_TXNTYPE(String.valueOf(dbobj.get("DB_ORIG_TXNTYPE")!=null?dbobj.get("DB_ORIG_TXNTYPE"):"N/A"));
					collection.setdB_PAY_ID(String.valueOf(dbobj.get("DB_PAY_ID")!=null?dbobj.get("DB_PAY_ID"):"N/A"));
					collection.setdB_PG_REF_NUM(String.valueOf(dbobj.get("DB_PG_REF_NUM")!=null?dbobj.get("DB_PG_REF_NUM"):"N/A"));
					collection.setdB_TXN_ID(String.valueOf(dbobj.get("DB_TXN_ID")!=null?dbobj.get("DB_TXN_ID"):"N/A"));
					collection.setdB_TXNTYPE(String.valueOf(dbobj.get("DB_TXNTYPE")!=null?dbobj.get("DB_TXNTYPE"):"N/A"));
					collection.setdB_USER_TYPE(String.valueOf(dbobj.get("DB_TXNTYPE")!=null?dbobj.get("DB_TXNTYPE"):"N/A"));
					collection.setfILE_LINE_DATA(String.valueOf(dbobj.get("FILE_LINE_DATA")!=null?dbobj.get("FILE_LINE_DATA"):"N/A"));
					collection.setfILE_LINE_NO(String.valueOf(dbobj.get("FILE_LINE_NO")!=null?dbobj.get("FILE_LINE_NO"):"N/A"));
					collection.setfILE_NAME(String.valueOf(dbobj.get("FILE_NAME")!=null?dbobj.get("FILE_NAME"):"N/A"));
					collection.setrECO_EXCEPTION_STATUS(String.valueOf(dbobj.get("RECO_EXCEPTION_STATUS")!=null?dbobj.get("RECO_EXCEPTION_STATUS"):"N/A"));
					collection.setrECO_TXNTYPE(String.valueOf(dbobj.get("RECO_TXNTYPE")!=null?dbobj.get("RECO_TXNTYPE"):"N/A"));
					collection.setrESPONSE_CODE(String.valueOf(dbobj.get("RESPONSE_CODE")!=null?dbobj.get("RESPONSE_CODE"):"N/A"));
					collection.setrESPONSE_MESSAGE(String.valueOf(dbobj.get("RESPONSE_MESSAGE")!=null?dbobj.get("RESPONSE_MESSAGE"):"N/A"));
					collection.settXN_ID(String.valueOf(dbobj.get("TXN_ID")!=null?dbobj.get("TXN_ID"):"N/A"));
					collection.setuPDATE_DATE(String.valueOf(dbobj.get("UPDATE_DATE")!=null?dbobj.get("UPDATE_DATE"):"N/A"));
                    transactionList.add(collection);
				}
				cursor.close();
				logger.info("Inside TxnReports , searchPayment , transactionListSize = " + transactionList.size());
				return transactionList;

			} catch (Exception e) {
				logger.error("Exception in search payment for admin", e);
			}
			return transactionList;
		}
		
}



