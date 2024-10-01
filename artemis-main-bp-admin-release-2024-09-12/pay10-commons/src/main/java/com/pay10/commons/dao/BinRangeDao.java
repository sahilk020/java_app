package com.pay10.commons.dao;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.pay10.commons.exception.DataAccessLayerException;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.user.User;
import com.pay10.commons.util.BinRange;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.MopType;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.TransactionManager;

@Service
public class BinRangeDao {


	@Autowired
	private MongoInstance mongoInstance;

	@Autowired
	private PropertiesManager propertiesManager;

	private static final String prefix = "MONGO_DB_";

	private static Logger logger = LoggerFactory.getLogger(BinRangeDao.class.getName());

	public void create(BinRange binRange) throws SystemException {

		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns
				.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.BIN_DB_NAME.getValue()));

		Document doc = new Document();
		String id = TransactionManager.getNewTransactionId();

		try {

			doc.put("_id", id);
			doc.put("id", id);
			doc.put("binCodeHigh", binRange.getBinCodeHigh());
			doc.put("issuerBankName", binRange.getIssuerBankName());
			doc.put("mopType", binRange.getMopType().getCode());
			doc.put("cardType", binRange.getCardType().getCode());
			doc.put("issuerCountry", binRange.getIssuerCountry());
			doc.put("productName", binRange.getProductName());
			doc.put("groupCode", binRange.getGroupCode());
			doc.put("rfu1", binRange.getRfu1());
			doc.put("rfu2", binRange.getRfu2());
			doc.put("binRangeHigh", binRange.getBinRangeHigh());
			doc.put("binRangeLow", binRange.getBinRangeLow());
			doc.put("binCodeLow", binRange.getBinCodeLow());

			coll.insertOne(doc);
		}

		catch (Exception e) {

		}

	}

	public void delete(BinRange binRange) throws DataAccessLayerException {

		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns
				.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.BIN_DB_NAME.getValue()));

		BasicDBObject idQuery = new BasicDBObject("id", binRange.getId());
		coll.deleteOne(idQuery);

	}

	public BinRange find(Long id) throws DataAccessLayerException {

		BinRange binRange = new BinRange();
		String idStr = id.toString();

		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns
				.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.BIN_DB_NAME.getValue()));

		try {

			List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject idQuery = new BasicDBObject("id", idStr);
			paramConditionLst.add(idQuery);
			BasicDBObject finalquery = new BasicDBObject("$and", paramConditionLst);

			FindIterable<Document> output = coll.find(finalquery);

			MongoCursor<Document> cursor = output.iterator();

			while (cursor.hasNext()) {
				Document doc = cursor.next();
				binRange = documentToBinRage(doc);
			}
		} catch (Exception e) {
			logger.error("Exception in getting bin range ", e);
		}

		return binRange;

	}

	public List<BinRange> findBinCodeHigh(String binCodeHigh) {
		return findByBinCodeHigh(binCodeHigh);

	}

	public List<BinRange> findBinCodeLow(String binCodeLow) {
		return findByBinCodeLow(binCodeLow);

	}

	@SuppressWarnings("unchecked")
	private List<BinRange> findByBinCodeLow(String binCodeLow) {
		
		List<BinRange> binRange = new ArrayList<BinRange>();

		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns
				.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.BIN_DB_NAME.getValue()));

		try {

			List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject idQuery = new BasicDBObject("binCodeLow", binCodeLow);
			paramConditionLst.add(idQuery);
			BasicDBObject finalquery = new BasicDBObject("$and", paramConditionLst);

			FindIterable<Document> output = coll.find(finalquery);
			MongoCursor<Document> cursor = output.iterator();

			while (cursor.hasNext()) {
				Document doc = cursor.next();
				BinRange br = documentToBinRage(doc);
				binRange.add(br);
			}

			cursor.close();
		}

		catch (Exception e) {
			logger.error("Exception while fetching bin range using binCodeHigh ", e);
		}

		return binRange;
	}

	@SuppressWarnings("unchecked")
	private List<BinRange> findByBinCodeHigh(String binCodeHigh) {

		List<BinRange> binRange = new ArrayList<BinRange>();

		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns
				.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.BIN_DB_NAME.getValue()));

		try {

			List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject idQuery = new BasicDBObject("binCodeHigh", binCodeHigh);
			paramConditionLst.add(idQuery);
			BasicDBObject finalquery = new BasicDBObject("$and", paramConditionLst);

			FindIterable<Document> output = coll.find(finalquery);
			MongoCursor<Document> cursor = output.iterator();

			while (cursor.hasNext()) {
				Document doc = cursor.next();
				BinRange br = documentToBinRage(doc);
				binRange.add(br);
			}

			cursor.close();
		}

		catch (Exception e) {
			logger.error("Exception while fetching bin range using binCodeHigh ", e);
		}

		return binRange;

	}

	public String insertAll(List<BinRange> binListObj) {

		
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns
				.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.BIN_DB_NAME.getValue()));
		
		StringBuilder message = new StringBuilder();
		long rowCount = 0;
		int currCount = 0;
		try {
			List<Document> docList = new ArrayList<Document>();
			for (BinRange binRangeObj : binListObj) {
				
				rowCount++;
				currCount++;
				
				if (currCount == 10000) {
					currCount = 0;
					logger.info("Added 10000 bins");
				}
				
				try {
					
					//List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
					//BasicDBObject idQuery = new BasicDBObject("binCodeHigh", binRangeObj.getBinCodeHigh());
					//	paramConditionLst.add(idQuery);
					//	BasicDBObject finalquery = new BasicDBObject("$and", paramConditionLst);
					
					//long count = coll.count(finalquery);
					// Validation disabled by shaiwal for bulk upload
					
					
					long count = 0;
					if (count > 0) {
						continue;
					} else {
						
						Document doc = new Document();
						Thread.sleep(2);
						String id = TransactionManager.getNewTransactionId();
						
						doc.put("_id", id);
						doc.put("id", id);
						doc.put("binCodeHigh", binRangeObj.getBinCodeHigh());
						doc.put("issuerBankName", binRangeObj.getIssuerBankName());
						doc.put("mopType", binRangeObj.getMopType().getCode());
						doc.put("cardType", binRangeObj.getCardType().getCode());
						doc.put("issuerCountry", binRangeObj.getIssuerCountry());
						doc.put("productName", binRangeObj.getProductName());
						doc.put("groupCode", binRangeObj.getGroupCode());
						doc.put("rfu1", binRangeObj.getRfu1());
						doc.put("rfu2", binRangeObj.getRfu2());
						doc.put("binRangeHigh", binRangeObj.getBinRangeHigh());
						doc.put("binRangeLow", binRangeObj.getBinRangeLow());
						doc.put("binCodeLow", binRangeObj.getBinCodeLow());

				//		docList.add(doc);
						coll.insertOne(doc);
						
				//		message.append((CrmFieldConstants.PROCESS_INITIATED_SUCCESSFULLY.getValue()));
					}
					
				} catch (Exception exception) {
					message.append(
							"sr. no." + rowCount + " " + ErrorType.CSV_NOT_SUCCESSFULLY_UPLOAD.getResponseMessage()
									+ " Bin code: " + binRangeObj.getBinCodeHigh() + "\n");
					logger.error("Error while processing binRange:" + exception + rowCount);
				}
			}
			
			//coll.insertMany(docList);
			message.append((CrmFieldConstants.PROCESS_INITIATED_SUCCESSFULLY.getValue()));
			logger.info("Total bins added  = " + rowCount);
		} catch (Exception exception) {
			message.append(exception.toString());
			logger.error("Error while processing binRange:" + exception);
		} finally {
			if(!StringUtils.isNotBlank(message.toString())) {
				message.append((CrmFieldConstants.BIN_ALREADY_EXISTS.getValue()));
			}
		}
		if(!StringUtils.isNotBlank(message.toString())) {
			message.append((CrmFieldConstants.BIN_ALREADY_EXISTS.getValue()));
		}
		return message.toString();

	}

	// Display BinRange Accoridng to paymentType and  MopType
	
	public List<BinRange> getBinRangeDisplay(String cardType, String mopType, String binRangeLow, String binRangeHigh, String issuerCountryType, User user,int start,int length) throws SystemException {
			
		List<BinRange> binRangList = new ArrayList<BinRange>();
			
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns
				.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.BIN_DB_NAME.getValue()));
	
		try {
				List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
				
				if (!mopType.equals("ALL")) {
					BasicDBObject mopQuery = new BasicDBObject("mopType", MopType.getInstanceIgnoreCase(mopType).getCode());
					paramConditionLst.add(mopQuery);
				}
				
				if (!cardType.equals("ALL")) {
					BasicDBObject cardQuery = new BasicDBObject("cardType", PaymentType.getInstanceUsingStringValue(cardType).getCode());
					paramConditionLst.add(cardQuery);
				}
				
				String[] domesticContryArray = {"IN", "IND", "INDIA"}; 
				
				if (!issuerCountryType.equals("ALL")) {
					if(issuerCountryType.equals("DOMESTIC")) {
						BasicDBObject issuerCountryTypeQuery = new BasicDBObject("$in",domesticContryArray);
						BasicDBObject issuerCountryTypeQuery2 = new BasicDBObject("issuerCountry",issuerCountryTypeQuery);
						paramConditionLst.add(issuerCountryTypeQuery2);
					}else {
						BasicDBObject issuerCountryTypeQuery = new BasicDBObject("$nin",domesticContryArray);
						BasicDBObject issuerCountryTypeQuery2 = new BasicDBObject("issuerCountry",issuerCountryTypeQuery);
						paramConditionLst.add(issuerCountryTypeQuery2);
					}
				}
				
				
				if (!StringUtils.isEmpty(binRangeLow) && !StringUtils.isEmpty(binRangeHigh)) {
					BasicDBObject binRangHighQuery = new BasicDBObject();
					binRangHighQuery.append("$lte", binRangeHigh);
					BasicDBObject binRangeHighQuery2 = new BasicDBObject(FieldType.BIN_RANGE_HIGH.getName(),binRangHighQuery);
					
					
					BasicDBObject binRangLowQuery = new BasicDBObject();
					binRangLowQuery.append("$gte", binRangeLow);
					BasicDBObject binRangeLowQuery2 = new BasicDBObject(FieldType.BIN_RANGE_LOW.getName(),binRangLowQuery);
					
					paramConditionLst.add(binRangeHighQuery2);
				
					paramConditionLst.add(binRangeLowQuery2);
					
				}
				
				BasicDBObject finalquery = new BasicDBObject();
				
				if (paramConditionLst.size() > 0 ) {
					finalquery = new BasicDBObject("$and", paramConditionLst);
				}
				
				
				BasicDBObject match = new BasicDBObject("$match", finalquery);
		
				BasicDBObject sort = new BasicDBObject("$sort", new BasicDBObject("binCodeLow", 1));
				BasicDBObject skip = new BasicDBObject("$skip", start);
				BasicDBObject limit = new BasicDBObject("$limit", length);
				
				
				List<BasicDBObject> pipeline = Arrays.asList(match, sort, skip, limit);
		
				AggregateIterable<Document> output = coll.aggregate(pipeline);
				output.allowDiskUse(true);
				MongoCursor<Document> cursor = output.iterator();
		
				while (cursor.hasNext()) {
					
					Document doc = cursor.next();
					BinRange binRange = documentToBinRage(doc);
					binRangList.add(binRange);
				}
					cursor.close();
					return binRangList;
		}
		
		catch(Exception e){
			logger.error("Exception in fetching Bin Range List",e);
		}
		
		return binRangList;
	}	
	
	
	//  count total BinRange Data according to PaymentType or MopType 
	@SuppressWarnings("unchecked")
	public BigInteger getBinRangeTotal(String cardType, String mopType, String binRangeLow, String binRangeHigh, String issuerCountryType, User user) throws SystemException{
		
		BigInteger total = null;
		
	
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns
				.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.BIN_DB_NAME.getValue()));
	
		try {
				List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
				
				if (!mopType.equals("ALL")) {
					BasicDBObject mopQuery = new BasicDBObject("mopType",MopType.getInstanceIgnoreCase( mopType).getCode());
					paramConditionLst.add(mopQuery);
				}
				
				if (!cardType.equals("ALL")) {
					BasicDBObject cardQuery = new BasicDBObject("cardType",  PaymentType.getInstanceUsingStringValue(cardType).getCode());
					paramConditionLst.add(cardQuery);
				}
				
				String[] domesticContryArray = {"IN", "IND", "INDIA"}; 
				
				if (!issuerCountryType.equals("ALL")) {
					if(issuerCountryType.equals("DOMESTIC")) {
						BasicDBObject issuerCountryTypeQuery = new BasicDBObject("$in",domesticContryArray);
						BasicDBObject issuerCountryTypeQuery2 = new BasicDBObject("issuerCountry",issuerCountryTypeQuery);
						paramConditionLst.add(issuerCountryTypeQuery2);
					}else {
						BasicDBObject issuerCountryTypeQuery = new BasicDBObject("$nin",domesticContryArray);
						BasicDBObject issuerCountryTypeQuery2 = new BasicDBObject("issuerCountry",issuerCountryTypeQuery);
						paramConditionLst.add(issuerCountryTypeQuery2);
					}
				}
				
				
				if (!StringUtils.isEmpty(binRangeLow) && !StringUtils.isEmpty(binRangeHigh)) {
					BasicDBObject binRangHighQuery = new BasicDBObject();
					binRangHighQuery.append("$lte", binRangeHigh);
					BasicDBObject binRangeHighQuery2 = new BasicDBObject(FieldType.BIN_RANGE_HIGH.getName(),binRangHighQuery);
					
					
					BasicDBObject binRangLowQuery = new BasicDBObject();
					binRangLowQuery.append("$gte", binRangeLow);
					BasicDBObject binRangeLowQuery2 = new BasicDBObject(FieldType.BIN_RANGE_LOW.getName(),binRangLowQuery);
					
					paramConditionLst.add(binRangeHighQuery2);
				
					paramConditionLst.add(binRangeLowQuery2);
					
				}
				
				BasicDBObject finalquery = new BasicDBObject();
				
				if (paramConditionLst.size() > 0) {
					finalquery = new BasicDBObject("$and", paramConditionLst);
				}
				long count = coll.count(finalquery);
				total = BigInteger.valueOf(count);
		}
		
		catch(Exception e){
			logger.error("Exception in fetching Bin Range List",e);
		}
		
		return total;
		
	}
		
	public BinRange documentToBinRage(Document doc) {

		BinRange binRange = new BinRange();

		if (StringUtils.isNotBlank(doc.getString("id"))) {
			binRange.setId(Long.valueOf(doc.getString("id")));
		} else {
			return null;

		}

		if (StringUtils.isNotBlank(doc.getString("binCodeHigh"))) {
			binRange.setBinCodeHigh(doc.getString("binCodeHigh"));
		} else {
			return null;
		}

		if (StringUtils.isNotBlank(doc.getString("issuerBankName"))) {
			binRange.setIssuerBankName(doc.getString("issuerBankName"));
		} else {
			binRange.setIssuerBankName("NA");
		}

		if (StringUtils.isNotBlank(doc.getString("mopType"))) {
			binRange.setMopType(MopType.getmop(doc.getString("mopType")));
		} else {
			return null;
		}

		if (StringUtils.isNotBlank(doc.getString("cardType"))) {
			binRange.setCardType(PaymentType.getInstanceUsingCode(doc.getString("cardType")));
		} else {
			return null;
		}

		if (StringUtils.isNotBlank(doc.getString("issuerCountry"))) {
			binRange.setIssuerCountry(doc.getString("issuerCountry"));
		} else {
			binRange.setIssuerCountry("NA");
		}

		if (StringUtils.isNotBlank(doc.getString("productName"))) {
			binRange.setProductName(doc.getString("productName"));
		} else {
			binRange.setProductName("NA");
		}

		if (StringUtils.isNotBlank(doc.getString("groupCode"))) {
			binRange.setGroupCode(doc.getString("groupCode"));
		} else {
			binRange.setGroupCode(doc.getString("NA"));
		}

		if (StringUtils.isNotBlank(doc.getString("rfu1"))) {
			binRange.setRfu1(doc.getString("rfu1"));
		} else {
			return null;
		}

		if (StringUtils.isNotBlank(doc.getString("rfu2"))) {
			binRange.setRfu2(doc.getString("rfu2"));
		} else {
			return null;
		}

		if (StringUtils.isNotBlank(doc.getString("binRangeHigh"))) {
			binRange.setBinRangeHigh(doc.getString("binRangeHigh"));
		} else {
			return null;
		}

		if (StringUtils.isNotBlank(doc.getString("binRangeLow"))) {
			binRange.setBinRangeLow(doc.getString("binRangeLow"));
		} else {
			return null;
		}

		if (StringUtils.isNotBlank(doc.getString("binCodeLow"))) {
			binRange.setBinCodeLow(doc.getString("binCodeLow"));
		} else {
			return null;
		}

		return binRange;

	}

}