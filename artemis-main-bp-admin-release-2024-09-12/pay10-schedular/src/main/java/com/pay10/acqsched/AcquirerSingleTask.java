package com.pay10.acqsched;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.StatusType;
import com.pay10.scheduler.commons.ConfigurationProvider;
import com.pay10.scheduler.core.ServiceControllerProvider;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.bson.Document;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


public class AcquirerSingleTask implements Runnable{
    public String acquirerName;
    public String startTime;
    public String maxTime;
    public String maxRetry;
    public String instrument;
    private static final Logger logger = LoggerFactory.getLogger(AcquirerSingleTask.class);

    private MongoInstance mongoInstance;

    private ConfigurationProvider configurationProvider;
    @Autowired
    private ServiceControllerProvider serviceControllerProvider;

   public AcquirerSingleTask(MongoInstance mongoInstance, ConfigurationProvider configurationProvider, AcquirerSechedulerConfig config) {
        this.acquirerName = config.getAcquirer();
        this.startTime = config.getStartTime();
        this.maxTime = config.getMaxTime();
        this.maxRetry = config.getRetry();
        this.instrument = config.getInstrument();
        this.mongoInstance = mongoInstance;
        this.configurationProvider = configurationProvider;

    }

    @Override
    public void run() {
        logger.info("Acquirer Name:============"+acquirerName);
        Set<String> setPGRef = fetchTransactionData();
        String bankStatusEnquiryUrl = configurationProvider.getEnquiryApiUrl();

        logger.info(" Acquirer wise SET of PGREF  : "+setPGRef);
        for (String pgRef : setPGRef) {

            logger.info("Sending Acquirer wise status enquiry request pgRef == " + pgRef);

            JSONObject data = new JSONObject();
            data.put(FieldType.PG_REF_NUM.getName(), pgRef);
            try {
               bankStatusEnquiry(data, bankStatusEnquiryUrl);
            } catch (SystemException e) {
                e.printStackTrace();
            }
        }
    }
    public void bankStatusEnquiry(JSONObject data , String bankStatusEnquiryUrl) throws SystemException {

		try {
			String serviceUrl = bankStatusEnquiryUrl;

			logger.info("Status Enquiry Service Url: " + serviceUrl);
			logger.info("Merchant data :" + data);

			HttpPost request = new HttpPost(serviceUrl);
			RequestConfig config = RequestConfig.custom().setConnectTimeout(3600000)
					.setConnectionRequestTimeout(3600000).setSocketTimeout(3600000).build();
			request.setConfig(config);

			CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();

			try {
				StringEntity params = new StringEntity(data.toString());

				request.addHeader("content-type", "application/json");
				request.setEntity(params);
				HttpResponse resp = httpClient.execute(request);
				HttpEntity response = resp.getEntity();
				//JSONObject json = new JSONObject(EntityUtils.toString(response));
				logger.info("Status Enquiry Response : {}",EntityUtils.toString(response));
				//logger.info("Status Enquiry Response Status : {}",json.getString("STATUS"));
			} catch (Exception e) {
				logger.error("Status Enquiry Expired " , e);
			}

		} catch (Exception exception) {
			logger.error("Error communicating with Status Enquiry API " , exception);
			throw new SystemException(ErrorType.INTERNAL_SYSTEM_ERROR, "Error communicating with Status Enquiry API");
		}
	}
    private Set<String> fetchTransactionData() {

        Set<String> pgRefSet = new HashSet<String>();
        try {

            String hBefore = startTime;
            String hInterval = maxTime;

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String timeNow = sdf.format(new Date()).toString();

            LocalDateTime datetime = LocalDateTime.parse(timeNow, formatter);
            LocalDateTime datetime2 = LocalDateTime.parse(timeNow, formatter);



            datetime = datetime.minusMinutes(Integer.valueOf(hBefore));
            String endTime = datetime.format(formatter);

            datetime2 = datetime2.minusMinutes(Integer.valueOf(hInterval) + Integer.valueOf(hBefore));
            String startTime = datetime2.format(formatter);
            BasicDBObject acquirerQuery = new BasicDBObject();

            logger.info("AquirerWiseScheduler status enquiry Start Time = " + startTime + " Acquirer : "+acquirerName);
            logger.info("AquirerWiseScheduler status enquiry End Time = " + endTime + " Acquirer : "+acquirerName);
            Set<String> allPgRefSet = new HashSet<String>();

            BasicDBObject dateTimeQuery = new BasicDBObject();
            BasicDBObject txnTypQuery = new BasicDBObject();

            List<BasicDBObject> acquirerQueryList = new ArrayList<BasicDBObject>();

            dateTimeQuery.put(FieldType.CREATE_DATE.getName(),
                    BasicDBObjectBuilder.start("$gte", new SimpleDateFormat(startTime).toLocalizedPattern())
                            .add("$lt", new SimpleDateFormat(endTime).toLocalizedPattern()).get());

            txnTypQuery.put(FieldType.TXNTYPE.getName(), "SALE");
            acquirerQuery.put(FieldType.ACQUIRER_TYPE.getName(), acquirerName);

            List<BasicDBObject> allConditionQueryList = new ArrayList<BasicDBObject>();

            allConditionQueryList.add(txnTypQuery);
            allConditionQueryList.add(dateTimeQuery);
            allConditionQueryList.add(acquirerQuery);

            BasicDBObject finalquery = new BasicDBObject("$and", allConditionQueryList);

            logger.info("Query to get data for status enquiry = " + finalquery);
            MongoDatabase dbIns = mongoInstance.getDB();
            MongoCollection<Document> collection = dbIns
                    .getCollection(configurationProvider.getMONGO_DB_collectionName());

            BasicDBObject match = new BasicDBObject("$match", finalquery);
            logger.info("Query to get data for status enquiry [Match] = " + match);
            List<BasicDBObject> pipeline = Arrays.asList(match);
            AggregateIterable<Document> output = collection.aggregate(pipeline);
            output.allowDiskUse(true);
            MongoCursor<Document> cursor = output.iterator();
            while (cursor.hasNext()) {
                Document dbobj = cursor.next();

                if (null != dbobj.get(FieldType.PG_REF_NUM.getName())
                        && !dbobj.getString(FieldType.PG_REF_NUM.getName()).equalsIgnoreCase("0")) {
                    allPgRefSet.add(dbobj.getString(FieldType.PG_REF_NUM.getName()));
                }

            }

            logger.info("Set of All allPgRefSet prepared with total number of OID : " + allPgRefSet.size());
            cursor.close();

            for (String pgRefNum : allPgRefSet) {

                boolean isCaptured = false;
                boolean isenrolled = false;
                String pgRef = null;

                BasicDBObject pgRefNumQuery = new BasicDBObject(FieldType.PG_REF_NUM.getName(), pgRefNum);
                BasicDBObject txnTypeQuery = new BasicDBObject(FieldType.TXNTYPE.getName(), "SALE");

                List<BasicDBObject> conditionList = new ArrayList<BasicDBObject>();
                conditionList.add(pgRefNumQuery);
                conditionList.add(txnTypeQuery);

                BasicDBObject query = new BasicDBObject("$and", conditionList);

                MongoDatabase dbIns1 = mongoInstance.getDB();
                MongoCollection<Document> collection1 = dbIns1
                        .getCollection(configurationProvider.getMONGO_DB_collectionName());

                BasicDBObject match1 = new BasicDBObject("$match", query);

                List<BasicDBObject> pipeline1 = Arrays.asList(match1);
                AggregateIterable<Document> output1 = collection1.aggregate(pipeline1);
                output1.allowDiskUse(true);

                MongoCursor<Document> cursor1 = output1.iterator();
                while (cursor1.hasNext()) {
                    Document dbobj = cursor1.next();

                    if (dbobj.get(FieldType.STATUS.getName()) != null
                            && StringUtils.isNotBlank(dbobj.getString(FieldType.STATUS.getName()))) {
                    	// Done By chetan nagaria for change in settlement process to mark transaction as RNS
                        if (dbobj.getString(FieldType.STATUS.getName()).equalsIgnoreCase(StatusType.CAPTURED.getName())
                                || dbobj.getString(FieldType.STATUS.getName())
                                .equalsIgnoreCase(StatusType.SETTLED_SETTLE.getName())|| dbobj.getString(FieldType.STATUS.getName())
                                .equalsIgnoreCase(StatusType.SETTLED_RECONCILLED.getName())) {
                            isCaptured = true;
                        }

                        if (dbobj.getString(FieldType.STATUS.getName())
                                .equalsIgnoreCase(StatusType.SENT_TO_BANK.getName())
                                || dbobj.getString(FieldType.STATUS.getName())
                                .equalsIgnoreCase(StatusType.ENROLLED.getName())) {

                            pgRef = dbobj.getString(FieldType.PG_REF_NUM.getName());
                        }

                    }

                    if (dbobj.get(FieldType.ACQUIRER_TYPE.getName()) != null
                            && StringUtils.isNotBlank(dbobj.getString(FieldType.ACQUIRER_TYPE.getName()))) {
                        isenrolled = true;
                    }

                }
                cursor1.close();

                if (!isCaptured && isenrolled) {

                    if (StringUtils.isNotBlank(pgRef)) {
                        pgRefSet.add(pgRef);
                    }
                }
            }
            //loop
// pgws /transact API
            return pgRefSet;
        } catch (Exception e) {
            logger.error("Exception in getting data for status enquiry", e);
        }
        return pgRefSet;

    }
}
