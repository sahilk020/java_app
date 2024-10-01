package com.crmws.service.impl;

import java.text.SimpleDateFormat;
import java.util.*;

import com.mongodb.client.MongoCursor;
import com.pay10.commons.user.FraudRiskModel;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.crmws.entity.ResponseMessageExceptionList;
import com.crmws.service.PayoutService;
import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import com.pay10.commons.dto.PoFRM;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.user.MultCurrencyCodeDao;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TDRStatus;

@Service
public class PayoutServiceImpl implements PayoutService {
	private static Logger logger = LoggerFactory.getLogger(PayoutServiceImpl.class.getName());
	@Autowired
	private UserDao dao;
	@Autowired
	private MultCurrencyCodeDao multCurrencyCodeDao;

	@Autowired
	private MongoInstance mongoInstance;

	private static final String prefix = "MONGO_DB_";

	@Override
	public ResponseEntity<ResponseMessageExceptionList> saveFrm(PoFRM frm) {
		logger.info("saveFrm() Payload:"+new Gson().toJson(frm));
		ResponseMessageExceptionList message = new ResponseMessageExceptionList();
		try {
			User user = dao.findPayId(frm.getPayId());
			frm.setMerchantName(user.getBusinessName());

			String currencyName = multCurrencyCodeDao.getCurrencyNamebyCode(frm.getCurrencyCode());
			frm.setCurrencyName(currencyName);
			
			updateFrm(frm);
			insert(frm);
			
			message.setHttpStatus(HttpStatus.CREATED);
			message.setRespmessage("Successfully Saved");
			return ResponseEntity.status(message.getHttpStatus()).body(message);
		} catch (Exception e) {
			logger.error("Exception Occur in saveFrm() :", e);
			e.printStackTrace();
			message.setHttpStatus(HttpStatus.EXPECTATION_FAILED);
			message.setRespmessage("Something went wrong, Please Contact To Admin");
			return ResponseEntity.status(message.getHttpStatus()).body(message);
		}
		
	}

	private void insert(PoFRM frm) {
		
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> collection = dbIns.getCollection(
				PropertiesManager.propertiesMap.get(prefix + Constants.PO_FRM.getValue()));

		Document newDocument = new Document();
		newDocument.put(FieldType.PAY_ID.getName(), frm.getPayId());
		newDocument.put(FieldType.MERCHANT_NAME.getName(), frm.getMerchantName());
		newDocument.put(FieldType.CHANNEL.getName(), frm.getChannel());
		newDocument.put(FieldType.CURRENCY_NAME.getName(), frm.getCurrencyName());
		newDocument.put(FieldType.CURRENCY_CODE.getName(), frm.getCurrencyCode());
		newDocument.put(FieldType.MIN_TICKET_SIZE.getName(), String.valueOf(frm.getMinTicketSize()));
		newDocument.put(FieldType.MAX_TICKET_SIZE.getName(), String.valueOf(frm.getMaxTicketSize()));
		newDocument.put(FieldType.DAILY_LIMIT.getName(), String.valueOf(frm.getDailyLimit()));
		newDocument.put(FieldType.WEEKLY_LIMIT.getName(), String.valueOf(frm.getWeeklyLimit()));
		newDocument.put(FieldType.MONTHLY_LIMIT.getName(), String.valueOf(frm.getMonthlyLimit()));
		newDocument.put(FieldType.DAILY_VOLUME.getName(), String.valueOf(frm.getDailyVolume()));
		newDocument.put(FieldType.WEEKLY_VOLUME.getName(), String.valueOf(frm.getWeeklyVolume()));
		newDocument.put(FieldType.MONTHLY_VOLUME.getName(), String.valueOf(frm.getMonthlyVolume()));
		newDocument.put(FieldType.CREATE_DATE.getName(), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		newDocument.put(FieldType.STATUS.getName(), TDRStatus.ACTIVE.getName());

		
		logger.info("insert() Payload:"+new Gson().toJson(frm));
		logger.info("INSERT DATA : "+newDocument.toJson());
		

		collection.insertOne(newDocument);

	}

	private void updateFrm(PoFRM frm) {

		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> collection = dbIns.getCollection(
				PropertiesManager.propertiesMap.get(prefix + Constants.PO_FRM.getValue()));

		BasicDBObject newDocument = new BasicDBObject();
		newDocument.append("$set",
				new BasicDBObject().append(FieldType.STATUS.getName(), TDRStatus.INACTIVE.getName()));

		BasicDBObject searchQuery = new BasicDBObject().append(FieldType.PAY_ID.getName(), frm.getPayId())
				.append(FieldType.CHANNEL.getName(), frm.getChannel()).append(FieldType.CURRENCY_CODE.getName(), frm.getCurrencyCode());

		logger.info("Query Of set : " + newDocument);
		logger.info("Query Of where : " + searchQuery);

		UpdateResult result = collection.updateMany(searchQuery, newDocument);

	}

    public PoFRM getPayoutFrmDetails(String merchantPayId, String currency, String channel) {
        PoFRM poFRM = new PoFRM();
        // default value
        double dailyLimitDefault = 500000.00;
        double weeklyLimitDefault = 5000000.00;
        double monthlyLimitDefault = 5000000.00;
        double dailyVolumeDefault = 100000;
        double weeklyVolumeDefault = 1000000;
        double monthlyVolumeDefault = 10000000;
        poFRM.setMinTicketSize(1);
        poFRM.setMaxTicketSize(100000);
        poFRM.setDailyLimit(dailyLimitDefault);
        poFRM.setWeeklyLimit(weeklyLimitDefault);
        poFRM.setMonthlyLimit(monthlyLimitDefault);
        poFRM.setDailyVolume(dailyVolumeDefault);
        poFRM.setWeeklyVolume(weeklyVolumeDefault);
        poFRM.setMonthlyVolume(monthlyVolumeDefault);
        poFRM.setDisplayDailyQuota(String.format("%.2f", dailyLimitDefault));
        poFRM.setDisplayWeeklyQuota(String.format("%.2f", weeklyLimitDefault));
        poFRM.setDisplayMonthlyQuota(String.format("%.2f", monthlyLimitDefault));
        poFRM.setDisplayDailyVolumeQuota(Integer.toString((int) dailyVolumeDefault));
        poFRM.setDisplayWeeklyVolumeQuota(Integer.toString((int) weeklyVolumeDefault));
        poFRM.setDisplayMonthlyVolumeQuota(Integer.toString((int) monthlyVolumeDefault));
        try {
            MongoDatabase dbIns = mongoInstance.getDB();
            MongoCollection<Document> coll = dbIns.getCollection(
                    PropertiesManager.propertiesMap.get(prefix + Constants.PO_FRM.getValue()));
            logger.info("Inside findPayoutFrmRuleExist (payId): {}, currency : {}, channel :{}", merchantPayId, currency, channel);

            List<Document> cursor = Collections.singletonList(new Document("$match",
                    new Document("PAY_ID", merchantPayId)
                            .append("CURRENCY_CODE", currency)
                            .append("CHANNEL", channel)
                            .append("STATUS", "Active")));
            logger.info("Query :{}", cursor);

            MongoCursor<Document> payoutFrmSetting = coll.aggregate(cursor).iterator();
            if (payoutFrmSetting.hasNext()) {
                Document doc = payoutFrmSetting.next();
                poFRM.setPayId(doc.getString("PAY_ID"));
                poFRM.setCurrencyCode(doc.getString("CURRENCY_CODE"));
                poFRM.setChannel(doc.getString("CHANNEL"));
                poFRM.setMinTicketSize(Double.parseDouble(doc.getString("MIN_TICKET_SIZE")));
                poFRM.setMaxTicketSize(Double.parseDouble(doc.getString("MAX_TICKET_SIZE")));
                poFRM.setDailyLimit(Double.parseDouble(doc.getString("DAILY_LIMIT")));
                poFRM.setWeeklyLimit(Double.parseDouble(doc.getString("WEEKLY_LIMIT")));
                poFRM.setMonthlyLimit(Double.parseDouble(doc.getString("MONTHLY_LIMIT")));
                poFRM.setDailyVolume(Double.parseDouble(doc.getString("DAILY_VOLUME")));
                poFRM.setWeeklyVolume(Double.parseDouble(doc.getString("WEEKLY_VOLUME")));
                poFRM.setMonthlyVolume(Double.parseDouble(doc.getString("MONTHLY_VOLUME")));

                double dailyVolume = 0;
                double weeklyVolume = 0;
                double monthlyVolume = 0;
                double dailyTransact = 0;
                double weeklyTransact = 0;
                double monthlyTransact = 0;

                Document payoutPaymentBalanceData = getPayoutPaymentBalance(merchantPayId, currency, channel);
                logger.info("getPayoutPaymentBalance :{}", payoutPaymentBalanceData);
                if (payoutPaymentBalanceData != null) {
                    dailyVolume = payoutPaymentBalanceData.getLong("dailyVolume");
                    weeklyVolume = payoutPaymentBalanceData.getLong("weeklyVolume");
                    monthlyVolume = payoutPaymentBalanceData.getLong("monthlyVolume");
                    dailyTransact = payoutPaymentBalanceData.getDouble("dailyLimit");
                    weeklyTransact = payoutPaymentBalanceData.getDouble("weeklyLimit");
                    monthlyTransact = payoutPaymentBalanceData.getDouble("monthlyLimit");
                }
                logger.info("Transact balance:dailyTransact={}, weeklyTransact={}, monthlyTransact={}, dailyVolume={}, weeklyVolume={}, monthlyVolume={}",
                        dailyTransact, weeklyTransact, monthlyTransact, dailyVolume, weeklyVolume, monthlyVolume);

                poFRM.setDisplayDailyQuota(String.format("%.2f", Double.parseDouble(doc.getString("DAILY_LIMIT")) - dailyTransact));
                poFRM.setDisplayWeeklyQuota(String.format("%.2f", Double.parseDouble(doc.getString("WEEKLY_LIMIT")) - weeklyTransact));
                poFRM.setDisplayMonthlyQuota(String.format("%.2f", Double.parseDouble(doc.getString("MONTHLY_LIMIT")) - monthlyTransact));
                poFRM.setDisplayDailyVolumeQuota(Integer.toString((int) (Double.parseDouble(doc.getString("DAILY_VOLUME")) - dailyVolume)));
                poFRM.setDisplayWeeklyVolumeQuota(Integer.toString((int) (Double.parseDouble(doc.getString("WEEKLY_VOLUME")) - weeklyVolume)));
                poFRM.setDisplayMonthlyVolumeQuota(Integer.toString((int) (Double.parseDouble(doc.getString("MONTHLY_VOLUME")) - monthlyVolume)));
            }
            payoutFrmSetting.close();

        } catch (Exception e) {
            logger.error("getPayoutFrmDetails error:{}", e.getMessage(), e);
            return null;
        }
        return poFRM;
    }

	public Document getPayoutPaymentBalance(String payId, String currency, String channel){

		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
		String dailyDate = sdf.format(date.getTime());

		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_WEEK, 1);
		date = calendar.getTime();
		String weeklyDate = sdf.format(date.getTime());

		calendar.set(Calendar.DAY_OF_MONTH, 1);
		date = calendar.getTime();
		String monthlyDate = sdf.format(date.getTime());

		String startDate = monthlyDate.compareTo(weeklyDate)>0?weeklyDate:monthlyDate;
		MongoDatabase mongoDatabase = mongoInstance.getDB();

		MongoCollection<Document> coll = mongoDatabase.getCollection("POtransaction");
		List<Document> query = Arrays.asList(new Document("$match",
						new Document("PAY_ID", payId)
								.append("STATUS", "Request Accepted")
								.append("CURRENCY_CODE", currency)
								.append("PAY_TYPE", channel)
								.append("CREATE_DATE",
										new Document("$gte", startDate))),
				new Document("$group",
						new Document("_id", "$PAY_ID")
								.append("dailyLimit",
										new Document("$sum",
												new Document("$cond", Arrays.asList(new Document("$gte", Arrays.asList("$CREATE_DATE", dailyDate)),
														new Document("$toDouble", "$AMOUNT"), 0.0))))
								.append("weeklyLimit",
										new Document("$sum",
												new Document("$cond", Arrays.asList(new Document("$gte", Arrays.asList("$CREATE_DATE", weeklyDate)),
														new Document("$toDouble", "$AMOUNT"), 0.0))))
								.append("monthlyLimit",
										new Document("$sum",
												new Document("$cond", Arrays.asList(new Document("$gte", Arrays.asList("$CREATE_DATE", monthlyDate)),
														new Document("$toDouble", "$AMOUNT"), 0.0))))
								.append("dailyVolume",
										new Document("$sum",
												new Document("$cond", Arrays.asList(new Document("$gte", Arrays.asList("$CREATE_DATE", dailyDate)), 1L, 0L))))
								.append("weeklyVolume",
										new Document("$sum",
												new Document("$cond", Arrays.asList(new Document("$gte", Arrays.asList("$CREATE_DATE", weeklyDate)), 1L, 0L))))
								.append("monthlyVolume",
										new Document("$sum",
												new Document("$cond", Arrays.asList(new Document("$gte", Arrays.asList("$CREATE_DATE", monthlyDate)), 1L, 0L))))));


		logger.info("Remaining Quota {}",query);
		Iterator<Document> doc = coll.aggregate(query).iterator();
		if(doc.hasNext()){
			return doc.next();
		}
		return null;
	}

}
