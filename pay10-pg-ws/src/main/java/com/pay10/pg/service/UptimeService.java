package com.pay10.pg.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.pay10.commons.dao.NodalAccountDetailsDao;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.user.NodalAccountDetails;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.AcquirerTypeNodal;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CurrencyTypes;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.TransactionManager;

@Service
public class UptimeService {

	private static Logger logger = LoggerFactory.getLogger(UptimeService.class.getName());
	
	@Autowired
	private MongoInstance mongoInstance;
	private static final String PREFIX = "MONGO_DB_";
	
	@Autowired
	UserDao userDao;
	
	@Autowired
	NodalAccountDetailsDao nodalAccountDetailsDao;
	
	public ResponseEntity<String> getUptimeResponse(HttpServletRequest req) {
		String uptimeToken = PropertiesManager.propertiesMap.get(Constants.UPTIME_TOKEN.getValue());
		String reqToken = req.getHeader("Authorization");
		
		if(uptimeToken == null || !uptimeToken.equals(reqToken)) {
			logger.info("Request Token : " + reqToken);
			logger.error("Token mismatch");
			return new ResponseEntity<>("Invalid token", HttpStatus.BAD_REQUEST);
		}
		
		// Test mongo connection.
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Calendar cal = Calendar.getInstance();

			String dateTo = sdf.format(cal.getTime());
			cal.add(Calendar.MINUTE, -10);
			String dateFrom = sdf.format(cal.getTime());
			
			BasicDBObject dateFromObject = new BasicDBObject(CrmFieldType.INVOICE_CREATE_DATE.getName(),new BasicDBObject("$gte", dateFrom));
			BasicDBObject dateToObject = new BasicDBObject(CrmFieldType.INVOICE_CREATE_DATE.getName(),new BasicDBObject("$lte", dateTo));
			List<BasicDBObject> queryList = new ArrayList<>();
			queryList.add(dateFromObject);
			queryList.add(dateToObject);
			BasicDBObject findQuery = new BasicDBObject("$and", queryList);
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns.getCollection(PropertiesManager.propertiesMap.get(PREFIX + Constants.COLLECTION_NAME.getValue()));

			Long count = collection.countDocuments(findQuery);
			
			if(count <= 0) {
				logger.info("No txn made during " + dateFrom + " :: " + dateTo);
			}
		}catch(Exception e) {
			logger.error("Failed to connect to mongo");
			return new ResponseEntity<>("Failed to connect to Mongo", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		// Test mysql connection.
		try {
			String payId = PropertiesManager.propertiesMap.get(Constants.UPTIME_USER_PAYID.getValue());
			User user = userDao.findPayId(payId);
			if(user == null) {
				logger.info("User not found with payId : " + payId);
			}
		}catch(Exception e) {
			logger.error("Failed to connect to mysql");
			return new ResponseEntity<>("Failed to connect to MYSQL", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>("ALL OK", HttpStatus.OK);
	}

	public ResponseEntity<String> addNodalAcquirer(HttpServletRequest req, Map<String, String> map) {
		String uptimeToken = PropertiesManager.propertiesMap.get(Constants.UPTIME_TOKEN.getValue());
		String reqToken = req.getHeader("Authorization");
		
		if(uptimeToken == null || !uptimeToken.equals(reqToken)) {
			logger.error("Token mismatch");
			return new ResponseEntity<>("Invalid token", HttpStatus.BAD_REQUEST);
		}
		NodalAccountDetails np = new NodalAccountDetails();
		np.setNodalId(TransactionManager.getNewTransactionId());
		np.setAccountHolderName(map.get("accountHolderName"));
		np.setAcquirer(map.get("acquirer"));
		np.setCustId(map.get("custId"));
		np.setAccountNumber(map.get("accountNumber"));
		np.setBankName(map.get("bankName"));
		np.setCurrencyCode(map.get("currencyCode"));
		np.setAddress(map.get("address"));
		np.setMobile(map.get("mobile"));
		np.setIfscCode(map.get("ifscCode"));
		np.setTenantId(map.get("tenantId"));
		nodalAccountDetailsDao.create(np);
		return new ResponseEntity<>("Added account successfully", HttpStatus.OK);
	}
}
