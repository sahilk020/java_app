package com.pay10.crm.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.pay10.commons.api.TransactionControllerServiceProvider;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.PropertiesManager;

public class CrmUptimeService extends AbstractSecureAction implements ServletRequestAware {

	private static Logger logger = LoggerFactory.getLogger(CrmUptimeService.class.getName());
	private static final long serialVersionUID = -8994652842808087554L;
	private HttpServletRequest httpRequest;
	private String response;

	@Autowired
	private MongoInstance mongoInstance;
	private static final String PREFIX = "MONGO_DB_";
	
	@Autowired
	TransactionControllerServiceProvider tcsp;
	
	@Autowired
	UserDao userDao;
	
	@Override
	public String execute() {

		try {
			String reqToken = httpRequest.getHeader("Authorization");
			String uptimeToken = PropertiesManager.propertiesMap.get(Constants.UPTIME_TOKEN.getValue());
			if (uptimeToken == null || !uptimeToken.equals(reqToken)) {
				logger.info("Request Token : " + reqToken);
				logger.error("Token mismatch");
				setResponse("Token mismatch");
				return "400";
			}
			String responseCode = String.valueOf(tcsp.uptimeService());
			
			
			if(!responseCode.equalsIgnoreCase("200")) {
				logger.error("Failed in WS");
				return responseCode;
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
				return "500";
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
				return "500";
			}
			
			return "200";
		} catch (Exception e) {
			logger.error("Failed to connect to pgws.");
		}
		setResponse("Invalid Request");
		return "500";
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.httpRequest = request;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}
}
