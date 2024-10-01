package com.pay10.crm.mongoReports;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.BasicDBObject;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.user.Merchants;
import com.pay10.commons.user.RefundRejection;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.DateCreater;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;

/**
 * @author Chandan
 *
 */

@Service
public class RefundRejectioReportService {
	
	private static Logger logger = LoggerFactory.getLogger(RefundRejectioReportService.class.getName());
	
	@Autowired
	PropertiesManager propertiesManager;
	
	@Autowired
	private MongoInstance mongoInstance;
	
	@Autowired
	private UserDao userdao;
	
	
	private static final String prefix = "MONGO_DB_";
	
	private static final String alphabaticFileName= "alphabatic-currencycode.properties";
	
	@SuppressWarnings("unchecked")
	public List<RefundRejection> searchRejectedRefund(String merchant, String orderId, String refundOrderId, String paymentType, String acquirer,
			String fromDate, String toDate, String segment, long roleId) {

		List<RefundRejection> refundRejectionList = new ArrayList<RefundRejection>();
		List<Merchants> merchantsList = new ArrayList<Merchants>();
		Map<String,String> mapBusinessName = new HashMap<String, String>();
		//merchantsList = userdao.getActiveMerchantList();
		merchantsList = userdao.getActiveMerchantList(segment, roleId);
		for(Merchants merchants : merchantsList ) {
			mapBusinessName.put(merchants.getPayId(), merchants.getBusinessName());
		}
		try {
			List<BasicDBObject> dateQueryLst = new ArrayList<BasicDBObject>();
			BasicDBObject dateQuery = new BasicDBObject();
			List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject acquirerQuery = new BasicDBObject();

			BasicDBObject allParamQuery = new BasicDBObject();
			/*List<BasicDBObject> acquirerConditionLst = new ArrayList<BasicDBObject>();
			List<BasicDBObject> saleOrAuthList = new ArrayList<BasicDBObject>();*/
			
			if(StringUtils.isBlank(orderId) && StringUtils.isBlank(refundOrderId)) {
				if (!fromDate.isEmpty()) {	
					/*String currentDate = null;
					if (!toDate.isEmpty()) {
						currentDate = toDate;
					} else {
						DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						Calendar cal = Calendar.getInstance();
						currentDate = dateFormat.format(cal.getTime());
					}
	
					dateQuery.put(FieldType.REQUEST_DATE.getName(),
							BasicDBObjectBuilder.start("$gte", new SimpleDateFormat(fromDate).toLocalizedPattern())
									.add("$lte", new SimpleDateFormat(currentDate).toLocalizedPattern()).get());*/
					
					LocalDate startDate = DateCreater.formatStringToLocalDate(fromDate);
					LocalDate endDate = DateCreater.formatStringToLocalDate(toDate).plusDays(1);                    
					
					ArrayList<String> lstRequestDate=new ArrayList<>();
					for (LocalDate date = startDate; date.isBefore(endDate); date = date.plusDays(1)) {
						//lstRequestDate.add(date.toString() + " 00:00:00");
						dateQueryLst.add(new BasicDBObject(FieldType.REQUEST_DATE.getName(),date.toString() + " 00:00:00"));
					}
					//dateQueryLst.add(new BasicDBObject(FieldType.REQUEST_DATE.getName(), new BasicDBObject("$in",lstRequestDate)));
					dateQuery.put("$or", dateQueryLst);
				}
			}

			if (!merchant.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.PAY_ID.getName(), merchant));
			}
			
			if (StringUtils.isNotBlank(orderId)) {
				paramConditionLst.add(new BasicDBObject(FieldType.ORDER_ID.getName(), orderId));
			}
			
			if (StringUtils.isNotBlank(refundOrderId)) {
				paramConditionLst.add(new BasicDBObject(FieldType.REFUND_ORDER_ID.getName(), refundOrderId));
			}
			
			if (!paymentType.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.PAYMENT_TYPE.getName(), paymentType));
			}
			
			if (!acquirer.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.ACQUIRER_TYPE.getName(), acquirer));
			}
			
			
			paramConditionLst.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.REFUND.getName()));
			ArrayList<String> list=new ArrayList<>();
			list.add(StatusType.CAPTURED.getName());
			list.add(StatusType.DECLINED.getName());
			list.add(StatusType.REJECTED.getName());
			list.add(StatusType.ERROR.getName());
			list.add(StatusType.FAILED.getName());
			list.add(StatusType.INVALID.getName());
			list.add(StatusType.CANCELLED.getName());
			list.add(StatusType.PENDING.getName());
			list.add(StatusType.FAILED_AT_ACQUIRER.getName());
			paramConditionLst.add(new BasicDBObject(FieldType.STATUS.getName(), new BasicDBObject("$in",list)));
			/*if(refundType.equals("DELTAREFUND")) {
				paramConditionLst.add(new BasicDBObject(FieldType.UDF6.getName(), Constants.Y.name()));
			}*/
			    
			
			BasicDBObject refundConditionQuery = new BasicDBObject("$and", paramConditionLst);

			if (!paramConditionLst.isEmpty()) {
				allParamQuery = new BasicDBObject("$and", paramConditionLst);
			}

			List<BasicDBObject> allConditionQueryList = new ArrayList<BasicDBObject>();
			
			if (!acquirerQuery.isEmpty()) {
				allConditionQueryList.add(acquirerQuery);
			}
			if (!dateQuery.isEmpty()) {
				allConditionQueryList.add(dateQuery);
			}

			BasicDBObject allConditionQueryObj = new BasicDBObject();
			if (!allConditionQueryList.isEmpty()) {
				allConditionQueryObj = new BasicDBObject("$and", allConditionQueryList);
			} 
			List<BasicDBObject> fianlList = new ArrayList<BasicDBObject>();

			if (!allParamQuery.isEmpty()) {
				fianlList.add(allParamQuery);
			}
			if (!allConditionQueryObj.isEmpty()) {
				fianlList.add(allConditionQueryObj);
			}

			BasicDBObject finalquery = new BasicDBObject("$and", fianlList);

			logger.info("Inside search summary report query , finalquery = " + finalquery);

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns
					.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
		
			BasicDBObject match = new BasicDBObject("$match", finalquery);
			// Now the aggregate operation
			Document firstGroup = new Document("_id",
					new Document("REFUND_ORDER_ID", "$REFUND_ORDER_ID"));
			
			BasicDBObject firstGroupObject = new BasicDBObject(firstGroup);
			BasicDBObject secondGroup = new BasicDBObject("$push", "$$ROOT");
			BasicDBObject group = new BasicDBObject("$group", firstGroupObject.append("entries", secondGroup));
			BasicDBObject sort = new BasicDBObject("$sort", new BasicDBObject("CREATE_DATE", -1));
			List<BasicDBObject> pipeline = Arrays.asList(match, sort, group);
			AggregateIterable<Document> output = coll.aggregate(pipeline);
			output.allowDiskUse(true);
	
			MongoCursor<Document> cursor = output.iterator();
			Boolean capturedFlag = false;
			while (cursor.hasNext()) {
				capturedFlag = false;	
				Document dbobj = cursor.next();
				List<Document> lstDoc = (List<Document>) dbobj.get("entries");
				for(int i=0; i<lstDoc.size();i++) {
					if(lstDoc.get(i).getString(FieldType.TXNTYPE.getName()).equals(TransactionType.REFUND.getName())
							&& (lstDoc.get(i).getString(FieldType.STATUS.getName()).equals(StatusType.CAPTURED.getName()))) {
						capturedFlag = true;
						break;
					}
				}
				
				if(!capturedFlag) {
					Document doc = lstDoc.get(lstDoc.size() - 1);
					RefundRejection refundRejection = new RefundRejection();

					/*List<Fields> fieldsList = new ArrayList<Fields>();
					fieldsList = fieldsDao.getPreviousSaleCapturedForOrderId(doc.getString(FieldType.ORDER_ID.toString()));*/
					
					refundRejection.setMerchant(mapBusinessName.get(doc.getString(FieldType.PAY_ID.toString())));
					refundRejection.setAcquirer(doc.getString(FieldType.ACQUIRER_TYPE.toString()));
					refundRejection.setMop(doc.getString(FieldType.MOP_TYPE.toString()));
					refundRejection.setOrderId(doc.getString(FieldType.ORDER_ID.toString()));
					refundRejection.setPgRefNum(doc.getString(FieldType.ORIG_TXN_ID.toString()));
					refundRejection.setRefundDate(doc.getString(FieldType.REQUEST_DATE.toString()));
					refundRejection.setRefundAmount(doc.getString(FieldType.AMOUNT.toString()));	
					refundRejection.setTotalAmount(doc.getString(FieldType.SALE_AMOUNT.getName()));
					refundRejection.setRefundFlag(doc.getString(FieldType.REFUND_FLAG.toString()));	
					//transactionSearch.setDateFrom(doc.getString(FieldType.PG_DATE_TIME.getName()));
					refundRejection.setRefundOrderId(doc.getString(FieldType.REFUND_ORDER_ID.getName().toString()));
					refundRejection.setStatus(doc.getString(FieldType.STATUS.getName().toString()));
					refundRejection.setCurrencyCode(doc.getString(FieldType.CURRENCY_CODE.getName().toString()));
					refundRejection.setPayId(doc.getString(FieldType.PAY_ID.getName().toString()));
					refundRejection.setProcessedDate(doc.getString(FieldType.CREATE_DATE.getName().toString()));
					//transactionSearch.setPgTxnMessage(doc.getString(FieldType.RESPONSE_MESSAGE.getName().toString()));
					refundRejectionList.add(refundRejection);
				}
			}

			cursor.close();
			Comparator<RefundRejection> comp = (RefundRejection a, RefundRejection b) -> {

				if (a.getRefundDate().compareTo(b.getRefundDate()) > 0) {
					return -1;
				} else if (a.getRefundDate().compareTo(b.getRefundDate()) < 0) {
					return 1;
				} else {
					return 0;
				}
			};
			
			Collections.sort(refundRejectionList,comp);
			return refundRejectionList;
		}

		catch (Exception e) {
			logger.error("Exception in getting records for refund reject report "+e);
		}

		return refundRejectionList;
	}
}
