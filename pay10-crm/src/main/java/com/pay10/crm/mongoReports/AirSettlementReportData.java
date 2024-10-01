package com.pay10.crm.mongoReports;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.apache.commons.lang.StringUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.user.AirSettlement;
import com.pay10.commons.user.User;
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

@Component
public class AirSettlementReportData {

	private static Logger logger = LoggerFactory.getLogger(AirSettlementReportData.class.getName());
	
	@Autowired
	private MongoInstance mongoInstance;
	
	@Autowired
	PropertiesManager propertiesManager;
	
	private static final String alphabaticFileName = "alphabatic-currencycode.properties";
	private static final String prefix = "MONGO_DB_";
	
	public List<AirSettlement> downloadSettlementReport(String merchantPayId,String currency,
			   String saleDate, User user) {
			logger.info("Inside TxnReports , searchPayment");
			//Map<String , User> userMap = new HashMap<String,User>();
			//boolean isParameterised = false;
			try {
				List<AirSettlement> airSettlementList = new ArrayList<AirSettlement>();

				//PropertiesManager propManager = new PropertiesManager();
				List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
				//List<BasicDBObject> acquirerConditionLst = new ArrayList<BasicDBObject>();
				BasicDBObject dateQuery = new BasicDBObject();
				BasicDBObject allParamQuery = new BasicDBObject();
				String currentDate = null;
				if (!saleDate.isEmpty()) {
					DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Calendar cal = Calendar.getInstance();
					 // add days to from date				
					Date date1= dateFormat.parse(saleDate);	
					cal.setTime(date1);
					cal.add(Calendar.DATE, 1);
					currentDate = dateFormat.format(cal.getTime());	

					dateQuery.put(FieldType.PG_DATE_TIME.getName(),
							BasicDBObjectBuilder.start("$gte", new SimpleDateFormat(saleDate).toLocalizedPattern())
									.add("$lt", new SimpleDateFormat(currentDate).toLocalizedPattern()).get());
				}

				List<BasicDBObject> dateIndexConditionList = new ArrayList<BasicDBObject>();
				BasicDBObject dateIndexConditionQuery = new BasicDBObject();
				String startString = new SimpleDateFormat(saleDate).toLocalizedPattern();
				String endString = new SimpleDateFormat(currentDate).toLocalizedPattern();

				DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
				Date dateStart = format.parse(startString);
				Date dateEnd = format.parse(endString);

				LocalDate incrementingDate = dateStart.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				LocalDate endDate = dateEnd.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

				List<String> allDatesIndex = new ArrayList<>();

				while (!incrementingDate.isAfter(endDate)) {
					allDatesIndex.add(incrementingDate.toString().replaceAll("-", ""));
					incrementingDate = incrementingDate.plusDays(1);
				}

				for (String dateIndex : allDatesIndex) {
					dateIndexConditionList.add(new BasicDBObject("PG_DATE_TIME_INDEX", dateIndex));
				}
				if (StringUtils.isNotBlank(PropertiesManager.propertiesMap.get(Constants.USE_PG_DATE_TIME_INDEX.getValue()))
						&& PropertiesManager.propertiesMap.get(Constants.USE_PG_DATE_TIME_INDEX.getValue()).equalsIgnoreCase("Y")) {
					
					if (dateIndexConditionList.size() > 0) {
						dateIndexConditionQuery.append("$or", dateIndexConditionList);
					}
					
				}
				
				
				if (!merchantPayId.equalsIgnoreCase("ALL")) {
					paramConditionLst.add(new BasicDBObject(FieldType.PAY_ID.getName(), merchantPayId));
				}
				
				paramConditionLst.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.SETTLED_SETTLE.getName()));
				paramConditionLst.add(new BasicDBObject(FieldType.TXNTYPE.getName(),  TransactionType.RECO.getName()));
				
				if (!currency.equalsIgnoreCase("ALL")) {
					paramConditionLst.add(new BasicDBObject(FieldType.CURRENCY_CODE.getName(), currency));
				}
				
				if (!paramConditionLst.isEmpty()) {
					allParamQuery = new BasicDBObject("$and", paramConditionLst);
				}

				List<BasicDBObject> allConditionQueryList = new ArrayList<BasicDBObject>();

				if (!dateQuery.isEmpty()) {
					allConditionQueryList.add(dateQuery);
				}
				
				if (!dateIndexConditionQuery.isEmpty()) {
					allConditionQueryList.add(dateIndexConditionQuery);
				}

				
				BasicDBObject allConditionQueryObj = new BasicDBObject("$and", allConditionQueryList);
				List<BasicDBObject> fianlList = new ArrayList<BasicDBObject>();

				if (!allParamQuery.isEmpty()) {
					fianlList.add(allParamQuery);
				}
				
				if (!allConditionQueryObj.isEmpty()) {
					fianlList.add(allConditionQueryObj);
				}
				

				BasicDBObject finalquery = new BasicDBObject("$and", fianlList);

				logger.info("Inside Settlement Reprort , searchPayment , finalquery = " + finalquery);
				MongoDatabase dbIns = mongoInstance.getDB();
				MongoCollection<Document> coll = dbIns
						.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
				MongoCursor<Document> cursor = coll.find(finalquery).iterator();
				while (cursor.hasNext()) {
					Document doc = cursor.next();
				
					AirSettlement airSettlement = new AirSettlement();
					airSettlement.setPgRefNum(doc.getString(FieldType.PG_REF_NUM.toString()));
					airSettlement.setAmount(doc.getString(FieldType.AMOUNT.toString()));
					airSettlement.setSettlementDate(DateCreater.formatSaleDateTime(doc.getString(FieldType.CREATE_DATE.getName())));
					airSettlement.setOrderId(doc.getString(FieldType.ORDER_ID.toString()));
					airSettlement.setSaleDate(DateCreater.formatSaleDateTime(doc.getString(FieldType.PG_DATE_TIME.getName())));
					airSettlementList.add(airSettlement);				
				}
				cursor.close();
				logger.info("Inside Download Settlement Reports , transactionListSize = " + airSettlementList.size());
				return airSettlementList;
			}

			catch (Exception e) {
				logger.error("Exception occured in TxnReports , searchPayment , Exception = " + e);
				return null;
			}
		}
}
