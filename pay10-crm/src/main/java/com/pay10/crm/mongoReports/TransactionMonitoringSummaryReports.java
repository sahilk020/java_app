package com.pay10.crm.mongoReports;

import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.crm.audittrail.dto.AcquirerCountAndAmountDTO;
import com.pay10.crm.audittrail.dto.FinalAcquirerWiseDTO;
import com.pay10.crm.audittrail.dto.ResponseData;
import com.pay10.crm.audittrail.dto.TransactionMonitoringSummaryDTO;
import org.bson.BsonNull;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class TransactionMonitoringSummaryReports {
	private static Logger logger = LoggerFactory.getLogger(TransactionMonitoringSummaryReports.class.getName());
	private static final String prefix = "MONGO_DB_";
	@Autowired
	private UserDao userDao;

	@Autowired
	PropertiesManager propertiesManager;

	@Autowired
	private MongoInstance mongoInstance;

	private DecimalFormat df = new DecimalFormat("##.##");

	/* Acquirer wise summary report start here */

	public Map<String, FinalAcquirerWiseDTO> getAcquirerWiseSummary(String fromDate, String toDate,
			List<String> acquires) {

		Map<String, FinalAcquirerWiseDTO> mapsAcquirer = new HashMap<String, FinalAcquirerWiseDTO>();
		try {
			List<ResponseData> respoList = new ArrayList<ResponseData>();
			Callable capturedCall = () -> {
				return respoList.add(getCapturedData(fromDate, toDate, acquires));
			};
			Callable pendingCall = () -> {
				return respoList.add(getPendingData(fromDate, toDate, acquires));
			};
			Callable otherCall = () -> {
				return respoList.add(getFailedData(fromDate, toDate, acquires));
			};

			List<Callable<Void>> taskList = new ArrayList<Callable<Void>>();
			taskList.add(capturedCall);
			taskList.add(pendingCall);
			taskList.add(otherCall);

			ExecutorService executor = Executors.newFixedThreadPool(4);

			executor.invokeAll(taskList);
			
			executor.shutdown();
			logger.info("Final Response Data getAcquirerWiseSummary : " + new Gson().toJson(respoList));
			mapsAcquirer = getAcquirerWiseDataInMap(respoList);
		} catch (Exception e) {
			logger.info("Exception Acquirer wise Summary : " + e);
			e.printStackTrace();
		}
		logger.info("Final data acquirer wise summary : " + new Gson().toJson(mapsAcquirer));
		
		return mapsAcquirer;
	}

	private ResponseData getCapturedData(String fromDate, String toDate, List<String> acquires) {
		List<TransactionMonitoringSummaryDTO> summaryDTOs = new ArrayList<TransactionMonitoringSummaryDTO>();
		logger.info("Date : " + fromDate + "\t" + toDate);
		fromDate=fromDate.replace("T"," ")+":00";
		toDate=toDate.replace("T"," ")+":59";
		logger.info("Date : " + fromDate + "\t" + toDate);
		try {
			List<Document> documents = Arrays.asList(new Document("$match",
					new Document("CREATE_DATE", new Document("$gte", fromDate).append("$lte", toDate))
							.append("ACQUIRER_TYPE", new Document("$in", acquires)).append("TXNTYPE", "SALE")
							.append("STATUS", new Document("$in", Arrays.asList("Captured", "Settled", "RNS")))),
					new Document("$group",
							new Document("_id", new Document("ACQUIRER_TYPE", "$ACQUIRER_TYPE")).append("count",
									new Document("$sum", 1L))),
					new Document("$project",
							new Document("ACQUIRER_TYPE", "$_id.ACQUIRER_TYPE").append("count", 1L).append("_id", 0L)),
					new Document("$group",
							new Document("_id", new BsonNull()).append("totalcount", new Document("$sum", "$count"))
									.append("data", new Document("$push",
											new Document("count", "$count").append("ACQUIRER_TYPE",
													"$ACQUIRER_TYPE")))),
					new Document("$unwind", new Document("path",
							"$data")),
					new Document("$project", new Document("_id", 0L).append("totalcount", 1L)
							.append("count", "$data.count").append("ACQUIRER_TYPE", "$data.ACQUIRER_TYPE")
							.append("percentage",
									new Document("$multiply", Arrays.asList(
											new Document("$divide", Arrays.asList("$data.count", "$totalcount")),
											100L)))));

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));

			MongoCursor<Document> cursor = coll.aggregate(documents).iterator();

			while (cursor.hasNext()) {
				Document docs = (Document) cursor.next();
				TransactionMonitoringSummaryDTO summaryDTO = new TransactionMonitoringSummaryDTO();
				summaryDTO.setAcquirerName(docs.getString("ACQUIRER_TYPE"));
				summaryDTO.setCount(docs.getLong("count"));
				summaryDTO.setPercentage(docs.getDouble("percentage"));
				summaryDTO.setTotalCount(docs.getLong("totalcount"));
				summaryDTOs.add(summaryDTO);
			}
		} catch (Exception e) {
			logger.info("Exception Captured : " + e);
		}
		ResponseData responseData = new ResponseData();
		responseData.setReportType("Captured");
		responseData.setStatusCode(200);
		responseData.setSummaryDTOs(summaryDTOs);

		System.out.println("Deepooo CAPT : " + new Gson().toJson(summaryDTOs));
		
		return responseData;

	}

	private ResponseData getPendingData(String fromDate, String toDate, List<String> acquires) {
		List<TransactionMonitoringSummaryDTO> summaryDTOs = new ArrayList<TransactionMonitoringSummaryDTO>();
		logger.info("Date : " + fromDate + "\t" + toDate);
		fromDate=fromDate.replace("T"," ")+":00";
		toDate=toDate.replace("T"," ")+":59";
		logger.info("Date : " + fromDate + "\t" + toDate);
		try {
			List<Document> documents = Arrays.asList(
					new Document("$match",
							new Document("CREATE_DATE", new Document("$gte", fromDate).append("$lte", toDate))
									.append("ACQUIRER_TYPE", new Document("$in", acquires)).append("TXNTYPE", "SALE")
									.append("STATUS", new Document("$in", Arrays.asList("Sent to Bank", "Pending")))),
					new Document("$group",
							new Document("_id", new Document("ACQUIRER_TYPE", "$ACQUIRER_TYPE")).append("count",
									new Document("$sum", 1L))),
					new Document("$project",
							new Document("ACQUIRER_TYPE", "$_id.ACQUIRER_TYPE").append("count", 1L).append("_id", 0L)),
					new Document("$group",
							new Document("_id", new BsonNull()).append("totalcount", new Document("$sum", "$count"))
									.append("data", new Document("$push",
											new Document("count", "$count").append("ACQUIRER_TYPE",
													"$ACQUIRER_TYPE")))),
					new Document("$unwind", new Document("path",
							"$data")),
					new Document("$project", new Document("_id", 0L).append("totalcount", 1L)
							.append("count", "$data.count").append("ACQUIRER_TYPE", "$data.ACQUIRER_TYPE")
							.append("percentage",
									new Document("$multiply", Arrays.asList(
											new Document("$divide", Arrays.asList("$data.count", "$totalcount")),
											100L)))));

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));

			MongoCursor<Document> cursor = coll.aggregate(documents).iterator();

			while (cursor.hasNext()) {
				Document docs = (Document) cursor.next();
				TransactionMonitoringSummaryDTO summaryDTO = new TransactionMonitoringSummaryDTO();
				summaryDTO.setAcquirerName(docs.getString("ACQUIRER_TYPE"));
				summaryDTO.setCount(docs.getLong("count"));
				summaryDTO.setPercentage(docs.getDouble("percentage"));
				summaryDTO.setTotalCount(docs.getLong("totalcount"));
				summaryDTOs.add(summaryDTO);
			}
		} catch (Exception e) {
			logger.info("Exception Pending : " + e);
		}
		ResponseData responseData = new ResponseData();
		responseData.setReportType("Pending");
		responseData.setStatusCode(200);
		responseData.setSummaryDTOs(summaryDTOs);
		System.out.println("Deepooo PEND : " + new Gson().toJson(summaryDTOs));
		return responseData;

	}

	private ResponseData getFailedData(String fromDate, String toDate, List<String> acquires) {
		List<TransactionMonitoringSummaryDTO> summaryDTOs = new ArrayList<TransactionMonitoringSummaryDTO>();
		logger.info("Date : " + fromDate + "\t" + toDate);
		fromDate=fromDate.replace("T"," ")+":00";
		toDate=toDate.replace("T"," ")+":59";
		logger.info("Date : " + fromDate + "\t" + toDate);
		try {
			List<Document> documents = Arrays.asList(
					new Document("$match",
							new Document("CREATE_DATE", new Document("$gte", fromDate).append("$lte", toDate))
									.append("ACQUIRER_TYPE", new Document("$in", acquires))
									.append("TXNTYPE", "SALE").append("STATUS",
											new Document("$nin",
													Arrays.asList("Sent to Bank", "Pending", "Captured", "Settled",
															"RNS")))),
					new Document("$group",
							new Document("_id", new Document("ACQUIRER_TYPE", "$ACQUIRER_TYPE")).append("count",
									new Document("$sum", 1L))),
					new Document("$project",
							new Document("ACQUIRER_TYPE", "$_id.ACQUIRER_TYPE").append("count", 1L).append("_id", 0L)),
					new Document("$group",
							new Document("_id", new BsonNull()).append("totalcount", new Document("$sum", "$count"))
									.append("data", new Document("$push",
											new Document("count", "$count").append("ACQUIRER_TYPE",
													"$ACQUIRER_TYPE")))),
					new Document("$unwind", new Document("path",
							"$data")),
					new Document("$project", new Document("_id", 0L).append("totalcount", 1L)
							.append("count", "$data.count").append("ACQUIRER_TYPE", "$data.ACQUIRER_TYPE")
							.append("percentage",
									new Document("$multiply", Arrays.asList(
											new Document("$divide", Arrays.asList("$data.count", "$totalcount")),
											100L)))));

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));

			MongoCursor<Document> cursor = coll.aggregate(documents).iterator();

			while (cursor.hasNext()) {
				Document docs = (Document) cursor.next();
				TransactionMonitoringSummaryDTO summaryDTO = new TransactionMonitoringSummaryDTO();
				summaryDTO.setAcquirerName(docs.getString("ACQUIRER_TYPE"));
				summaryDTO.setCount(docs.getLong("count"));
				summaryDTO.setPercentage(docs.getDouble("percentage"));
				summaryDTO.setTotalCount(docs.getLong("totalcount"));
				summaryDTOs.add(summaryDTO);
			}
		} catch (Exception e) {
			logger.info("Exception Other : " + e);
		}
		ResponseData responseData = new ResponseData();
		responseData.setReportType("Other");
		responseData.setStatusCode(200);
		responseData.setSummaryDTOs(summaryDTOs);
		System.out.println("Deepooo FAIL : " + new Gson().toJson(summaryDTOs));
		return responseData;

	}

	private Map<String, FinalAcquirerWiseDTO> getAcquirerWiseDataInMap(List<ResponseData> respoList) {
		Map<String, FinalAcquirerWiseDTO> mapsAcquirer = new HashMap<String, FinalAcquirerWiseDTO>();
		respoList.stream().forEach(response -> {

			response.getSummaryDTOs().stream().forEach(item -> {

				if (mapsAcquirer.containsKey(item.getAcquirerName())) {
					FinalAcquirerWiseDTO finalAcquirerWiseDTO = mapsAcquirer.get(item.getAcquirerName());
					if (response.getReportType().equalsIgnoreCase("Pending")) {
						finalAcquirerWiseDTO.setAcquirerName(item.getAcquirerName());
						finalAcquirerWiseDTO.setPending(item.getCount());
						finalAcquirerWiseDTO.setPendingPercentage(Double.parseDouble(df.format(item.getPercentage())));
						finalAcquirerWiseDTO.setGrandTotal(item.getCount() + finalAcquirerWiseDTO.getGrandTotal());
					}

					if (response.getReportType().equalsIgnoreCase("Other")) {
						finalAcquirerWiseDTO.setAcquirerName(item.getAcquirerName());
						finalAcquirerWiseDTO.setFailed(item.getCount());
						finalAcquirerWiseDTO.setFailedPercentage(Double.parseDouble(df.format(item.getPercentage())));
						finalAcquirerWiseDTO.setGrandTotal(item.getCount() + finalAcquirerWiseDTO.getGrandTotal());
					}

					if (response.getReportType().equalsIgnoreCase("Captured")) {
						finalAcquirerWiseDTO.setAcquirerName(item.getAcquirerName());
						finalAcquirerWiseDTO.setCaptured(item.getCount());
						finalAcquirerWiseDTO.setCapturedPercentage(Double.parseDouble(df.format(item.getPercentage())));
						finalAcquirerWiseDTO.setGrandTotal(item.getCount() + finalAcquirerWiseDTO.getGrandTotal());
					}

					mapsAcquirer.put(item.getAcquirerName(), finalAcquirerWiseDTO);
				}

				else {
					FinalAcquirerWiseDTO finalAcquirerWiseDTO = new FinalAcquirerWiseDTO();
					if (response.getReportType().equalsIgnoreCase("Pending")) {
						finalAcquirerWiseDTO.setAcquirerName(item.getAcquirerName());
						finalAcquirerWiseDTO.setPending(item.getCount());
						finalAcquirerWiseDTO.setPendingPercentage(Double.parseDouble(df.format(item.getPercentage())));
						finalAcquirerWiseDTO.setGrandTotal(item.getCount() + finalAcquirerWiseDTO.getGrandTotal());
					}

					if (response.getReportType().equalsIgnoreCase("Other")) {
						finalAcquirerWiseDTO.setAcquirerName(item.getAcquirerName());
						finalAcquirerWiseDTO.setFailed(item.getCount());
						finalAcquirerWiseDTO.setFailedPercentage(Double.parseDouble(df.format(item.getPercentage())));
						finalAcquirerWiseDTO.setGrandTotal(item.getCount() + finalAcquirerWiseDTO.getGrandTotal());
					}

					if (response.getReportType().equalsIgnoreCase("Captured")) {
						finalAcquirerWiseDTO.setAcquirerName(item.getAcquirerName());
						finalAcquirerWiseDTO.setCaptured(item.getCount());
						finalAcquirerWiseDTO.setCapturedPercentage(Double.parseDouble(df.format(item.getPercentage())));
						finalAcquirerWiseDTO.setGrandTotal(item.getCount() + finalAcquirerWiseDTO.getGrandTotal());
					}

					mapsAcquirer.put(item.getAcquirerName(), finalAcquirerWiseDTO);
				}

			});
		});
		logger.info("Before : " + new Gson().toJson(mapsAcquirer));
		
		Map<String, FinalAcquirerWiseDTO> newMapData=new HashMap<String, FinalAcquirerWiseDTO>();
		mapsAcquirer.entrySet().stream().forEach(entry->{
			FinalAcquirerWiseDTO finalAcquirerWiseDTO=entry.getValue();
			
			finalAcquirerWiseDTO.setCapturedPercentage(Double.parseDouble(df.format((double)(finalAcquirerWiseDTO.getCaptured()*100)/finalAcquirerWiseDTO.getGrandTotal())));
			finalAcquirerWiseDTO.setPendingPercentage(Double.parseDouble(df.format((double)(finalAcquirerWiseDTO.getPending()*100)/finalAcquirerWiseDTO.getGrandTotal())));
			finalAcquirerWiseDTO.setFailedPercentage(Double.parseDouble(df.format((double)(finalAcquirerWiseDTO.getFailed()*100)/finalAcquirerWiseDTO.getGrandTotal())));
			
			newMapData.put(entry.getKey(), finalAcquirerWiseDTO);
		});
		
		logger.info("After : " + new Gson().toJson(newMapData));
		
		return newMapData;
	}

	/* Acquirer wise summary report end here */

	/////////////////////////////////////////////////////////////////////////////////////////////////////

	/* Count and Amount Report start here */

	public Map<String, AcquirerCountAndAmountDTO> getCountAndAmount(String fromDate, String toDate) {

		ResponseData response = countAndSummaryData(fromDate, toDate);

		Map<String, AcquirerCountAndAmountDTO> mapsAcquirer = new HashMap<String, AcquirerCountAndAmountDTO>();

		response.getSummaryDTOs().stream().forEach(item -> {

			AcquirerCountAndAmountDTO acquirerCountAndAmount = new AcquirerCountAndAmountDTO();
			acquirerCountAndAmount.setAcquirerName(item.getAcquirerName());
			acquirerCountAndAmount.setTotalCount(new BigInteger(item.getCount() + ""));
			acquirerCountAndAmount.setTotalAmount(new BigDecimal(item.getTotalAmount() + ""));

			mapsAcquirer.put(item.getAcquirerName(), acquirerCountAndAmount);

		});

		return mapsAcquirer;
	}

	private ResponseData countAndSummaryData(String fromDate, String toDate) {
		List<TransactionMonitoringSummaryDTO> summaryDTOs = new ArrayList<TransactionMonitoringSummaryDTO>();
		logger.info("Date : " + fromDate + "\t" + toDate);
		fromDate=fromDate.replace("T"," ")+":00";
		toDate=toDate.replace("T"," ")+":59";
		logger.info("Date : " + fromDate + "\t" + toDate);
		try {
			List<Document> documents = Arrays.asList(
					new Document("$match",
							new Document("CREATE_DATE", new Document("$gte", fromDate).append("$lte", toDate))
									.append("ACQUIRER_TYPE", new Document("$ne", new BsonNull()))),
					new Document("$group",
							new Document("_id", "$ACQUIRER_TYPE").append("count", new Document("$sum", 1L)).append(
									"totalAmount", new Document("$sum", new Document("$toDecimal", "$AMOUNT")))),
					new Document("$project", new Document("ACQUIRER_TYPE", "$_id").append("count", 1L)
							.append("totalAmount", 1L).append("_id", 0L)));

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));

			MongoCursor<Document> cursor = coll.aggregate(documents).iterator();

			while (cursor.hasNext()) {
				Document docs = (Document) cursor.next();
				TransactionMonitoringSummaryDTO summaryDTO = new TransactionMonitoringSummaryDTO();
				summaryDTO.setAcquirerName(docs.getString("ACQUIRER_TYPE"));
				summaryDTO.setCount(docs.getLong("count"));
				summaryDTO.setTotalAmount(new BigDecimal(docs.get("totalAmount")+""));
				summaryDTOs.add(summaryDTO);
			}

		} catch (Exception e) {
			logger.info("Exception Count And Summary : " + e);
		}

		logger.info("Final Count And Amount : " + new Gson().toJson(summaryDTOs));
		ResponseData responseData = new ResponseData();
		responseData.setReportType("Acquirer Wise Count & Amount");
		responseData.setStatusCode(200);
		responseData.setSummaryDTOs(summaryDTOs);

		return responseData;
	}

	/* Count and Amount Report end here */

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/* Merchant Wise report Service Start here */
	
	public Map<String, FinalAcquirerWiseDTO> merchantWiseAcquirerReport(String fromDate, String toDate, String acquirer) {
		Map<String, FinalAcquirerWiseDTO> mapsAcquirer = new HashMap<String, FinalAcquirerWiseDTO>();
		
		try {
			List<ResponseData> respoList = new ArrayList<ResponseData>();
			
			respoList.add(getMerchantWiseCaptured(fromDate, toDate, acquirer));
			
			respoList.add(getMerchantWisePending(fromDate, toDate, acquirer));
			
			respoList.add(getMerchantWiseOther(fromDate, toDate, acquirer));
			
			logger.info("Final Response Data merchantWiseAcquirerReport : " + new Gson().toJson(respoList));
			mapsAcquirer=getMerchantWiseDataInMap(respoList);
		}catch (Exception e) {
				logger.info("Exception in merchantWiseAcquirerReport : " + e);
				e.printStackTrace();
		}
		logger.info("Size :" + mapsAcquirer.size());
		return mapsAcquirer;
	}
	
	private Map<String, FinalAcquirerWiseDTO> getMerchantWiseDataInMap(List<ResponseData> respoList) {
		Map<String, FinalAcquirerWiseDTO> mapsAcquirer = new HashMap<String, FinalAcquirerWiseDTO>();
		logger.info("getMerchantWiseDataInMap COUNT : " + respoList.size());
		respoList.stream().forEach(response->{
			
			response.getSummaryDTOs().stream().forEach(item->{
				
				//get merchant name based on payId
				
				String merchantName=userDao.getBusinessNameByPayId(item.getPayId());
				
				if(merchantName==null) {
					merchantName="NA";
				}
				
				if (mapsAcquirer.containsKey(item.getPayId())) {

					FinalAcquirerWiseDTO finalAcquirerWiseDTO = mapsAcquirer.get(item.getPayId());
					if (response.getReportType().equalsIgnoreCase("Pending")) {
						finalAcquirerWiseDTO.setPending(item.getCount());
						finalAcquirerWiseDTO.setPayId(item.getPayId());
						finalAcquirerWiseDTO.setPendingPercentage(Double.parseDouble(df.format(item.getPercentage())));
						finalAcquirerWiseDTO.setGrandTotal(item.getCount() + finalAcquirerWiseDTO.getGrandTotal());
					}

					if (response.getReportType().equalsIgnoreCase("Other")) {
						finalAcquirerWiseDTO.setFailed(item.getCount());
						finalAcquirerWiseDTO.setPayId(item.getPayId());
						finalAcquirerWiseDTO.setFailedPercentage(Double.parseDouble(df.format(item.getPercentage())));
						finalAcquirerWiseDTO.setGrandTotal(item.getCount() + finalAcquirerWiseDTO.getGrandTotal());
					}

					if (response.getReportType().equalsIgnoreCase("Captured")) {
						finalAcquirerWiseDTO.setCaptured(item.getCount());
						finalAcquirerWiseDTO.setPayId(item.getPayId());
						finalAcquirerWiseDTO.setCapturedPercentage(Double.parseDouble(df.format(item.getPercentage())));
						finalAcquirerWiseDTO.setGrandTotal(item.getCount() + finalAcquirerWiseDTO.getGrandTotal());
					}
					finalAcquirerWiseDTO.setAcquirerName(item.getAcquirerName());
					finalAcquirerWiseDTO.setMerchantName(merchantName);
					mapsAcquirer.put(item.getPayId(), finalAcquirerWiseDTO);
				}
				
				else {

					FinalAcquirerWiseDTO finalAcquirerWiseDTO = new FinalAcquirerWiseDTO();
					if (response.getReportType().equalsIgnoreCase("Pending")) {
						finalAcquirerWiseDTO.setPending(item.getCount());
						finalAcquirerWiseDTO.setPayId(item.getPayId());
						finalAcquirerWiseDTO.setPendingPercentage(Double.parseDouble(df.format(item.getPercentage())));
						finalAcquirerWiseDTO.setGrandTotal(item.getCount() + finalAcquirerWiseDTO.getGrandTotal());
					}

					if (response.getReportType().equalsIgnoreCase("Other")) {
						finalAcquirerWiseDTO.setFailed(item.getCount());
						finalAcquirerWiseDTO.setPayId(item.getPayId());
						finalAcquirerWiseDTO.setFailedPercentage(Double.parseDouble(df.format(item.getPercentage())));
						finalAcquirerWiseDTO.setGrandTotal(item.getCount() + finalAcquirerWiseDTO.getGrandTotal());
					}

					if (response.getReportType().equalsIgnoreCase("Captured")) {
						finalAcquirerWiseDTO.setCaptured(item.getCount());
						finalAcquirerWiseDTO.setPayId(item.getPayId());
						finalAcquirerWiseDTO.setCapturedPercentage(Double.parseDouble(df.format(item.getPercentage())));
						finalAcquirerWiseDTO.setGrandTotal(item.getCount() + finalAcquirerWiseDTO.getGrandTotal());
					}
					finalAcquirerWiseDTO.setAcquirerName(item.getAcquirerName());
					finalAcquirerWiseDTO.setMerchantName(merchantName);
					mapsAcquirer.put(item.getPayId(), finalAcquirerWiseDTO);
					
				}
				
			});
			
		});
		
		Map<String, FinalAcquirerWiseDTO> newMapData=new HashMap<String, FinalAcquirerWiseDTO>();
		mapsAcquirer.entrySet().stream().forEach(entry->{
			FinalAcquirerWiseDTO finalAcquirerWiseDTO=entry.getValue();
			
			finalAcquirerWiseDTO.setCapturedPercentage(Double.parseDouble(df.format((double)(finalAcquirerWiseDTO.getCaptured()*100)/finalAcquirerWiseDTO.getGrandTotal())));
			finalAcquirerWiseDTO.setPendingPercentage(Double.parseDouble(df.format((double)(finalAcquirerWiseDTO.getPending()*100)/finalAcquirerWiseDTO.getGrandTotal())));
			finalAcquirerWiseDTO.setFailedPercentage(Double.parseDouble(df.format((double)(finalAcquirerWiseDTO.getFailed()*100)/finalAcquirerWiseDTO.getGrandTotal())));
			
			newMapData.put(entry.getKey(), finalAcquirerWiseDTO);
		});
		
		logger.info("After : " + new Gson().toJson(newMapData));
		
		return newMapData;
	}

	private ResponseData getMerchantWiseCaptured(String fromDate, String toDate, String acquirer) {
		List<TransactionMonitoringSummaryDTO> summaryDTOs = new ArrayList<TransactionMonitoringSummaryDTO>();
		logger.info("Final date is getMerchantWiseCaptured() : " + fromDate + "\t" + toDate + "\t" + acquirer);
		logger.info("Date : " + fromDate + "\t" + toDate);
		fromDate=fromDate.replace("T"," ")+":00";
		toDate=toDate.replace("T"," ")+":59";
		logger.info("Date : " + fromDate + "\t" + toDate);
		
		try {
			List<Document> documents = Arrays.asList(
					new Document("$match",
							new Document("CREATE_DATE", new Document("$gte", fromDate).append("$lte", toDate))
									.append("ACQUIRER_TYPE", acquirer).append("TXNTYPE", "SALE").append("STATUS",
											new Document("$in", Arrays.asList("Captured", "Settled", "RNS")))),
					new Document("$group",
							new Document("_id", new Document("PAY_ID", "$PAY_ID")).append("count",
									new Document("$sum", 1L))),
					new Document("$project",
							new Document("PAY_ID", "$_id.PAY_ID").append("count", 1L).append("_id", 0L)),
					new Document("$group", new Document("_id", new BsonNull())
							.append("totalcount", new Document("$sum", "$count")).append("data",
									new Document("$push", new Document("count", "$count").append("PAY_ID",
											"$PAY_ID")))),
					new Document("$unwind", new Document("path",
							"$data")),
					new Document("$project", new Document("_id", 0L).append("totalcount", 1L)
							.append("count", "$data.count").append("PAY_ID", "$data.PAY_ID").append("percentage",
									new Document("$multiply", Arrays.asList(
											new Document("$divide", Arrays.asList("$data.count", "$totalcount")),
											100L)))));
			logger.info("Final query is getMerchantWiseCaptured() : " + documents);
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));

			MongoCursor<Document> cursor = coll.aggregate(documents).iterator();

			while (cursor.hasNext()) {
				Document docs = (Document) cursor.next();
				TransactionMonitoringSummaryDTO summaryDTO = new TransactionMonitoringSummaryDTO();
				summaryDTO.setAcquirerName(acquirer);
				summaryDTO.setCount(docs.getLong("count"));
				summaryDTO.setPercentage(docs.getDouble("percentage"));
				summaryDTO.setTotalCount(docs.getLong("totalcount"));
				summaryDTO.setPayId(docs.getString("PAY_ID"));
				summaryDTOs.add(summaryDTO);
			}
		} catch (Exception e) {
			logger.info("Exception Merchant Wise Captured : " + e);
			e.printStackTrace();
		}

		ResponseData responseData = new ResponseData();
		responseData.setReportType("Captured");
		responseData.setStatusCode(200);
		responseData.setSummaryDTOs(summaryDTOs);

		return responseData;
	}

	private ResponseData getMerchantWisePending(String fromDate, String toDate, String acquirer) {
		List<TransactionMonitoringSummaryDTO> summaryDTOs = new ArrayList<TransactionMonitoringSummaryDTO>();
		logger.info("Final date is getMerchantWisePending() : " + fromDate + "\t" + toDate + "\t" + acquirer);
		logger.info("Date : " + fromDate + "\t" + toDate);
		fromDate=fromDate.replace("T"," ")+":00";
		toDate=toDate.replace("T"," ")+":59";
		logger.info("Date : " + fromDate + "\t" + toDate);
		
		try {
			List<Document> documents = Arrays.asList(
					new Document("$match",
							new Document("CREATE_DATE", new Document("$gte", fromDate).append("$lte", toDate))
									.append("ACQUIRER_TYPE", acquirer).append("TXNTYPE", "SALE")
									.append("STATUS", new Document("$in", Arrays.asList("Pending", "Sent to Bank")))),
					new Document("$group",
							new Document("_id", new Document("PAY_ID", "$PAY_ID")).append("count",
									new Document("$sum", 1L))),
					new Document("$project",
							new Document("PAY_ID", "$_id.PAY_ID").append("count", 1L).append("_id", 0L)),
					new Document("$group", new Document("_id", new BsonNull())
							.append("totalcount", new Document("$sum", "$count")).append("data",
									new Document("$push", new Document("count", "$count").append("PAY_ID",
											"$PAY_ID")))),
					new Document("$unwind", new Document("path",
							"$data")),
					new Document("$project", new Document("_id", 0L).append("totalcount", 1L)
							.append("count", "$data.count").append("PAY_ID", "$data.PAY_ID").append("percentage",
									new Document("$multiply", Arrays.asList(
											new Document("$divide", Arrays.asList("$data.count", "$totalcount")),
											100L)))));
			logger.info("Final query is getMerchantWisePending() : " + documents);
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));

			MongoCursor<Document> cursor = coll.aggregate(documents).iterator();

			while (cursor.hasNext()) {
				Document docs = (Document) cursor.next();
				TransactionMonitoringSummaryDTO summaryDTO = new TransactionMonitoringSummaryDTO();
				summaryDTO.setAcquirerName(acquirer);
				summaryDTO.setCount(docs.getLong("count"));
				summaryDTO.setPercentage(docs.getDouble("percentage"));
				summaryDTO.setTotalCount(docs.getLong("totalcount"));
				summaryDTO.setPayId(docs.getString("PAY_ID"));
				summaryDTOs.add(summaryDTO);
			}
		} catch (Exception e) {
			logger.info("Exception Merchant Wise Pending : " + e);
		}

		ResponseData responseData = new ResponseData();
		responseData.setReportType("Pending");
		responseData.setStatusCode(200);
		responseData.setSummaryDTOs(summaryDTOs);

		return responseData;
	}

	private ResponseData getMerchantWiseOther(String fromDate, String toDate, String acquirer) {
		List<TransactionMonitoringSummaryDTO> summaryDTOs = new ArrayList<TransactionMonitoringSummaryDTO>();
		logger.info("Final date is getMerchantWiseOther() : " + fromDate + "\t" + toDate + "\t" + acquirer);
		logger.info("Date : " + fromDate + "\t" + toDate);
		fromDate=fromDate.replace("T"," ")+":00";
		toDate=toDate.replace("T"," ")+":59";
		logger.info("Date : " + fromDate + "\t" + toDate);
		
		try {
			List<Document> documents = Arrays.asList(
					new Document("$match",
							new Document("CREATE_DATE", new Document("$gte", fromDate).append("$lte", toDate))
									.append("ACQUIRER_TYPE", acquirer).append("TXNTYPE", "SALE").append("STATUS",
											new Document("$nin",
													Arrays.asList("Captured", "Settled", "RNS", "Pending",
															"Sent to Bank")))),
					new Document("$group",
							new Document("_id", new Document("PAY_ID", "$PAY_ID")).append("count",
									new Document("$sum", 1L))),
					new Document("$project",
							new Document("PAY_ID", "$_id.PAY_ID").append("count", 1L).append("_id", 0L)),
					new Document("$group", new Document("_id", new BsonNull())
							.append("totalcount", new Document("$sum", "$count")).append("data",
									new Document("$push", new Document("count", "$count").append("PAY_ID",
											"$PAY_ID")))),
					new Document("$unwind", new Document("path",
							"$data")),
					new Document("$project", new Document("_id", 0L).append("totalcount", 1L)
							.append("count", "$data.count").append("PAY_ID", "$data.PAY_ID").append("percentage",
									new Document("$multiply", Arrays.asList(
											new Document("$divide", Arrays.asList("$data.count", "$totalcount")),
											100L)))));
			logger.info("Final query is getMerchantWiseOther() : " + documents);
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));

			MongoCursor<Document> cursor = coll.aggregate(documents).iterator();

			while (cursor.hasNext()) {
				Document docs = (Document) cursor.next();
				TransactionMonitoringSummaryDTO summaryDTO = new TransactionMonitoringSummaryDTO();
				summaryDTO.setAcquirerName(acquirer);
				summaryDTO.setCount(docs.getLong("count"));
				summaryDTO.setPercentage(docs.getDouble("percentage"));
				summaryDTO.setTotalCount(docs.getLong("totalcount"));
				summaryDTO.setPayId(docs.getString("PAY_ID"));
				summaryDTOs.add(summaryDTO);
			}
		} catch (Exception e) {
			logger.info("Exception Merchant Wise Pending : " + e);
		}

		ResponseData responseData = new ResponseData();
		responseData.setReportType("Other");
		responseData.setStatusCode(200);
		responseData.setSummaryDTOs(summaryDTOs);

		return responseData;

	}
	
	/* Merchant Wise report Service End here */
}
