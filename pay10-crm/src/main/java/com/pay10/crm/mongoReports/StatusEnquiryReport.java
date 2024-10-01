package com.pay10.crm.mongoReports;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.user.StatusEnquiryObject;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.PropertiesManager;

@Component
public class StatusEnquiryReport {

	
	
	private static Logger logger = LoggerFactory.getLogger(StatusEnquiryReport.class.getName());

	@Autowired
	private UserDao userdao;

	@Autowired
	PropertiesManager propertiesManager;

	@Autowired
	private MongoInstance mongoInstance;
	
	private static final String prefix = "MONGO_DB_";
	
	

	public List<StatusEnquiryObject> getStatusEnquiry(String fromDate, String toDate) {
		logger.info("inside getStatusEnquiry method,StatusEnquiryReport Class");
		List<StatusEnquiryObject> enquiryList=new ArrayList<StatusEnquiryObject>();
		try {

			BasicDBObject dateQuery = new BasicDBObject();
			BasicDBObject andQuery = new BasicDBObject();
			List<BasicDBObject> queryObj = new ArrayList<BasicDBObject>();

			if (!fromDate.isEmpty()) {

				String currentDate = null;
				if (!toDate.isEmpty()) {
					currentDate = toDate;
				} else {
					DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Calendar cal = Calendar.getInstance();
					currentDate = dateFormat.format(cal.getTime());
				}

				dateQuery.put(FieldType.CREATE_DATE.getName(),
						BasicDBObjectBuilder.start("$gte", new SimpleDateFormat(fromDate).toLocalizedPattern())
								.add("$lte", new SimpleDateFormat(currentDate).toLocalizedPattern()).get());
			}

			
			if (!dateQuery.isEmpty()) {
				queryObj.add(dateQuery);
			}

			andQuery.put("$and", queryObj);
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns
					.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.STATUS_ENQUIRY_HISTORY_NAME.getValue()));
			MongoCursor<Document> cursor = coll.find(andQuery).sort(new BasicDBObject(FieldType.CREATE_DATE.getName(),-1)).iterator();

		    	long capturedCount = coll.count(andQuery);

		    	logger.info("inside getStatusEnquiry get Captured Count" + capturedCount);
		    	
		    	while (cursor.hasNext()) {
				Document doc = cursor.next();

				StatusEnquiryObject statusObj = new StatusEnquiryObject();

			
				if (StringUtils.isBlank(doc.getString(FieldType.CREATE_DATE.toString()))) {
					statusObj.setCreateDate(CrmFieldConstants.NA.toString());	
				}
				else {
					statusObj.setCreateDate(doc.getString(FieldType.CREATE_DATE.toString()));
				}
				
				if (StringUtils.isBlank(doc.getString(FieldType.TOTAL_PROCESSED.toString()))) {
					statusObj.setTotalProcess(CrmFieldConstants.NA.toString());	
				}
				else {
					statusObj.setTotalProcess(doc.getString(FieldType.TOTAL_PROCESSED.toString()));
				}
				
		       
				if (StringUtils.isBlank(doc.getString(FieldType.TOTAL_CAPTURED.toString()))) {
					statusObj.setTotalCapture(CrmFieldConstants.NA.toString());	
				}
				else {
					statusObj.setTotalCapture(doc.getString(FieldType.TOTAL_CAPTURED.toString()));
				}
				
				
				if (StringUtils.isBlank(doc.getString(FieldType.TOTAL_PENDING.toString()))) {
					statusObj.setTotalPending(CrmFieldConstants.NA.toString());	
				}
				else {
					statusObj.setTotalPending(doc.getString(FieldType.TOTAL_PENDING.toString()));
				}
				
				if (StringUtils.isBlank(doc.getString(FieldType.TOTAL_OTHERS.toString()))) {
					statusObj.setTotalOthers(CrmFieldConstants.NA.toString());	
				}
				else {
					statusObj.setTotalOthers(doc.getString(FieldType.TOTAL_OTHERS.toString()));
				}
				
				enquiryList.add(statusObj);
			}
		    	
		
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return enquiryList;
	}
	
}
