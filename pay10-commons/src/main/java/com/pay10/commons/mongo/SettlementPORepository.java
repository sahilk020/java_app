package com.pay10.commons.mongo;

import java.net.URI;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import com.pay10.commons.util.*;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import com.pay10.commons.api.MessageDigestProvider;
import com.pay10.commons.dao.WhiteListedIpDao;
import com.pay10.commons.dto.MerchantWalletPODTO;
import com.pay10.commons.dto.POTransactionStatusDTO;
import com.pay10.commons.dto.PassbookPODTO;
import com.pay10.commons.dto.SettlementDTO;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.MultCurrencyCodeDao;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

@Component
public class SettlementPORepository {
	private static Logger logger = LoggerFactory.getLogger(SettlementPORepository.class.getName());
	private static final String prefix = "MONGO_DB_";

	@Autowired
	PropertiesManager propertiesManager;
	@Autowired
	private MongoInstance mongoInstance;
	@Autowired
	private UserDao userDao;
	private Gson gson = new Gson();

	@Autowired
	private WalletHistoryRepository walletHistoryRepository;
	@Autowired
	private MerchantKeySaltService merchantKeySaltService;
	@Autowired
	private UserAccountServices userAccountServices;

	@Autowired
	private MultCurrencyCodeDao multCurrencyCodeDao;
	@Autowired
	private WhiteListedIpDao whiteListedIpDao;

	public String createSettlementFromPanelAPI(SettlementDTO settlementDTO, HttpServletRequest request) {
		logger.info("Request Data for payout : " + new Gson().toJson(settlementDTO));
		logger.info("Internal_Cust_IP for X-Forwarded-For: " + request.getHeader("X-Forwarded-For"));
		logger.info("Internal_Cust_IP for Remote Addr: " + request.getRemoteAddr());

		String ip = request.getHeader("X-Forwarded-For");
		logger.info("Info : " + ip);
//		boolean isIpWhiteListed = whiteListedIpDao.checkIpWhitelisted(settlementDTO.getPayId(), ip!=null?ip:request.getRemoteAddr());

//		if (!isIpWhiteListed) {
//			return "Ip Not whitelisted.";
//		}

		String salt = merchantKeySaltService.getSalt(settlementDTO.getPayId());
		String keySalt = userAccountServices.generateMerchantHostedEncryptionKey(settlementDTO.getPayId());

		logger.info("DTO Amount :"+settlementDTO.getAmount());
		Map<String, String> mapData = new HashMap<>();
		mapData.put("ACC_CITY_NAME", settlementDTO.getCity());
		mapData.put("ACC_NAME", settlementDTO.getAccountHolderName());
		mapData.put("ACC_NO", settlementDTO.getAccountNo());
		mapData.put("ACC_PROVINCE", settlementDTO.getAccountProvince());
		mapData.put("AMOUNT", settlementDTO.getAmount().concat("00"));
		mapData.put("BANK_BRANCH", settlementDTO.getBankBranch());
		mapData.put("BANK_CODE", settlementDTO.getBankName());
		//mapData.put("BANK_NAME", settlementDTO.getBankName());
		mapData.put("CURRENCY_CODE", settlementDTO.getCurrency());
		mapData.put("CUST_EMAIL", settlementDTO.getEmail());
		mapData.put("CUST_NAME", settlementDTO.getAccountHolderName());
		mapData.put("CUST_PHONE", settlementDTO.getPhone());
		mapData.put("IFSC", settlementDTO.getIfsc());
		mapData.put("ORDER_ID", "PO" + settlementDTO.getUid());
		mapData.put("PAY_ID", settlementDTO.getPayId());
		mapData.put("PAY_TYPE", settlementDTO.getChannel());
		mapData.put("REMARKS", settlementDTO.getRemarks());
		mapData.put("RETURN_URL", settlementDTO.getReturnURL());
		mapData.put("TRANSFER_TYPE", settlementDTO.getMode());
		mapData.put("UDF13", "PO");
		mapData.put("SALT", salt);
		mapData.put("KEY_SALT", keySalt);

		Map<String, String> sortedMap = new TreeMap<String, String>(mapData);

		StringBuilder createHashString = new StringBuilder();
		for (String key : sortedMap.keySet()) {
			if (key.equals("KEY_SALT") || key.equals("SALT")) {

			} else {
				createHashString.append(key);
				createHashString.append("=");
				createHashString.append(sortedMap.get(key));
				createHashString.append("~");
			}
		}

		String encStringWoHash = createHashString.toString();
		int tildeLastCount = createHashString.lastIndexOf("~");
		createHashString.replace(tildeLastCount, tildeLastCount + 1, "");
		createHashString.append(salt);

		String createHash = createHashString.toString();
		String generatedHash = "";

		try {
			generatedHash = getHash(createHash);
		} catch (Exception exception) {

		}
		String encString = encStringWoHash + "HASH=" + generatedHash;

		String PAY_ID = settlementDTO.getPayId();
		String encData = "";
		logger.info("Transaction Api Property: {}",ConfigurationConstants.TRANSACTION_API_URL.getValue());
		String transactionAPIURL = ConfigurationConstants.TRANSACTION_API_URL.getValue();
		logger.info("Final URL : " + transactionAPIURL);

		try {
			encData = convertToEncryptData(PAY_ID, encString, ConfigurationConstants.CRYPTO_ENCRYPTION_SERVICE_URL.getValue());
			logger.info("convertToEncryptData : " + encData);

		} catch (Exception exception) {
			logger.info("exception in : " + exception);
		}
		logger.info("ENCDATA : " + encData);
		logger.info("STRING : " + encString);

		String response = callToTransactionApi(transactionAPIURL, PAY_ID, encData);

		// Handle the response as needed
		logger.info("Actual API Response: " + response);
		
		String finalResponse="";
		if(response.contains("value")) {
			
			org.jsoup.nodes.Document doc = Jsoup.parse(response);
			
			Element payIdInput = doc.select("input[name=PAY_ID]").first();
	        String payId = payIdInput.attr("value");
	        System.out.println("PAY_ID value: " + payId);
	        
	        Element encDataInput = doc.select("input[name=ENCDATA]").first();
	        String encData1 = encDataInput.attr("value");
	        System.out.println("ENCDATA value: " + encData1);
	        
	        JSONObject jooo=new JSONObject();
			jooo.put("PAY_ID", payId);
			jooo.put("ENCDATA", encData1);
			
			String encryptedData = decryptData(jooo.toString(), ConfigurationConstants.CRYPTO_DECRYPTION_SERVICE_URL.getValue());
			logger.info("Encrypt Response Data : " + encryptedData);
			
			JSONObject joJsonObject=new JSONObject(encryptedData);
			String data=joJsonObject.getString("ENCDATA");
			logger.info("AFTER GETTING JSON DATA FOR MAP : " + data);
			
			Map<String, String> resultMap = new HashMap<>();
			logger.info("Getting result Map Data : " + resultMap.size());
	        String[] pairs = data.split("~");
	        for (String pair : pairs) {
	            String[] keyValue = pair.split("=",2);
	            if (keyValue.length == 2) {
	                resultMap.put(keyValue[0], keyValue[1]);
	            }
	        }
	        logger.info("Response Map data : " + resultMap);
	        finalResponse=resultMap.get("RESPONSE_MESSAGE");
	        logger.info("Final Response : " + finalResponse);
	        
	        logger.info("Final Response condition : " + finalResponse.equalsIgnoreCase("Request Accepted"));
	        logger.info("Final Response condition : " + resultMap.get("ORDER_ID"));
	        if(finalResponse.equalsIgnoreCase("Request Accepted")) {
	        	
	        	approveRequestAuto(resultMap.get("ORDER_ID"), payId);
	        }
		}
		logger.info("Final Result here : " + finalResponse);
		return finalResponse;
	}

	public String approveRequestAuto(String orderId, String payId) {
		logger.info("Order Id for auto payout : " + orderId + "Pay Id : " + payId);
		try {
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection("POtransactionStatus");

			List<BasicDBObject> params = new ArrayList<>();

			params.add(new BasicDBObject(FieldType.PAY_ID.getName(), payId));
			params.add(new BasicDBObject(FieldType.ORDER_ID.getName(), orderId));
			params.add(new BasicDBObject("TXNTYPE", "SALE"));
			params.add(new BasicDBObject(FieldType.STATUS.getName(), "Request Accepted"));

			BasicDBObject finalquery = new BasicDBObject("$and", params);
			logger.info("finalquery paout auto : " + finalquery);

			MongoCursor<Document> result = coll.find(finalquery).iterator();
			Document document = null;
			if (result.hasNext()) {
				document = (Document) result.next();
			}
			logger.info("Fetch Doc auto payout : " + document);
			if (document != null) {
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

				document.put("_id", TransactionManager.getNewTransactionId());
				document.put(FieldType.STATUS.getName(), StatusType.PENDING.getName());
				document.put(FieldType.CREATE_DATE.getName(), dateFormat.format(new Date()));
				document.put(FieldType.UPDATEDBY.getName(), "");
				document.put(FieldType.RESPONSE_CODE.getName(), ErrorType.PENDING.getCode());
				document.put(FieldType.RESPONSE_MESSAGE.getName(), "Request Approved");

				MongoCollection<Document> collectionDb = dbIns.getCollection("POtransactionStatus");

				Bson updates = null;

				updates = Updates.combine(Updates.set(FieldType.STATUS.getName(), StatusType.PENDING.getName()),
						Updates.set(FieldType.UPDATEDBY.getName(), ""),
						Updates.set(FieldType.RESPONSE_CODE.getName(), ErrorType.PENDING.getCode()),
						Updates.set(FieldType.RESPONSE_MESSAGE.getName(), "Request Approved"));

				UpdateOptions options = new UpdateOptions().upsert(true);
				BasicDBObject searchQuery = new BasicDBObject("$and", params);
				UpdateResult result1 = collectionDb.updateOne(searchQuery, updates, options);
				logger.info("approveRequest payout : " + result1);

				MongoCollection<Document> collection = dbIns.getCollection("POtransaction");
				collection.insertOne(document);
				
				//added by deep
				//MongoDatabase dbIns = mongoInstance.getDB();
				MongoCollection<Document> collectiondb = dbIns.getCollection("WalletHistory");

		        Document query = new Document("payId", payId).append("currency",multCurrencyCodeDao.getCurrencyNamebyCode(document.getString("CURRENCY_CODE")));
		        String totalBalance="0";
		        MongoCursor<Document> cursor= collectiondb.find(query).iterator();
		        if(cursor.hasNext()) {
		        	Document documentDB=cursor.next();
		        	totalBalance=documentDB.getString("totalBalance");
		        }
		        logger.info("before total balance payout approve request : " + totalBalance);
		        totalBalance=String.valueOf(Double.parseDouble(totalBalance)-Double.parseDouble(document.getString("AMOUNT")));
		        logger.info("after total balance payout approve request : " + totalBalance);
		        MerchantWalletPODTO merchantWalletPODTOdb=new MerchantWalletPODTO();
		        merchantWalletPODTOdb.setCurrency(multCurrencyCodeDao.getCurrencyNamebyCode(document.getString("CURRENCY_CODE")));
		        merchantWalletPODTOdb.setTotalBalance(totalBalance);
		        merchantWalletPODTOdb.setPayId(payId);
				merchantWalletPODao.SaveAndUpdteTotalBalanceWallet(merchantWalletPODTOdb);
				
				walletHistoryRepository.updateBalanceByPayIdAndCurrencyTotalBalance
				(payId, merchantWalletPODTOdb.getCurrency(), "debit", document.getString("AMOUNT"), totalBalance);
				
			}

			return "Approved Successfully";
		} catch (Exception e) {
			e.printStackTrace();
			return "Approved Failed";
		}

	}
	
	RestTemplate restTemplate = new RestTemplate();

	private String convertToEncryptData(String payId, String encString, String finalURL) {
		try {
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			JSONObject jo = new JSONObject();
			jo.put("PAY_ID", payId);
			jo.put("ENCDATA", encString);

			RequestEntity<String> requestEntity = new RequestEntity<>(jo.toString(), headers, HttpMethod.POST,
					new URI(finalURL));
			ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);
			JSONObject jp = new JSONObject(responseEntity.getBody());
			return jp.getString("ENCDATA");
		} catch (Exception e) {
			logger.info("Exception in convertToEncryptData : " + e);
			return null;
		}
	}

	private String callToTransactionApi(String finalURL, String payId, String encData) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

			MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
			map.add("PAY_ID", payId);
			map.add("ENCDATA", encData);

			RequestEntity<MultiValueMap<String, String>> requestEntity = new RequestEntity<>(map, headers,
					org.springframework.http.HttpMethod.POST, new URI(finalURL));

			ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);
			return responseEntity.getBody();
		} catch (Exception e) {
			logger.info("Exception in callToTransactionApi : " + e);
			return null;
		}
	}

	private String decryptData(String encryptData, String url) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			RequestEntity<String> requestEntity = new RequestEntity<>(encryptData, headers,
					org.springframework.http.HttpMethod.POST, new URI(url));
			ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);
			logger.info("Final Response :  " + responseEntity.getBody());
			return responseEntity.getBody();
		} catch (Exception e) {
			logger.info("Exception to Decrypt data : " + e);
		}

		return null;
	}

	public static String getHash(String input) throws Exception {
		String response = null;

		MessageDigest messageDigest = MessageDigestProvider.provide();
		messageDigest.update(input.getBytes());
		MessageDigestProvider.consume(messageDigest);

		response = new String(Hex.encodeHex(messageDigest.digest()));

		return response.toUpperCase();
	}

	public String createSettlementFromPanel(SettlementDTO settlementDTO, HttpServletRequest request) {
		// here to create an entry with pending status
		try {
			settlementDTO.setCurrency(multCurrencyCodeDao.getCurrencyNamebyCode(settlementDTO.getCurrency()));
			// check if merchant have sufficiant balance then cut and update balanace in
			// wallet history
			Map<String, Object> walletFund = walletHistoryRepository.findMerchantFundByPayId(settlementDTO.getPayId(),
					settlementDTO.getCurrency());
			double balance = 0.0;
			double totalBalance = 0.0;
			if (walletFund != null && walletFund.size() > 0) {
				balance = Double.parseDouble(walletFund.get("finalBalance").toString());
				totalBalance = Double.parseDouble(walletFund.get("totalBalance").toString());

				if (Double.parseDouble(settlementDTO.getAmount()) <= balance) {
					balance -= Double.parseDouble(settlementDTO.getAmount());
					totalBalance -= Double.parseDouble(settlementDTO.getAmount());
					// update updatedBalance in wallet history finalBalance
					walletHistoryRepository.updateBalanceByPayIdAndCurrency(settlementDTO.getPayId(),
							settlementDTO.getCurrency(), String.valueOf(balance), "debit", settlementDTO.getAmount(),String.valueOf(totalBalance));

					MongoDatabase dbIns = mongoInstance.getDB();
					MongoCollection<Document> coll = dbIns.getCollection("PO_Settlement");
					String json = gson.toJson(settlementDTO);
					coll.insertOne(Document.parse(json));

					return "Settlement Request Sent Successfully";
				} else {
					logger.info("Merchant does not have sufficient wallet amount");
					return "Merchant does not have sufficient wallet amount";
				}
			} else {
				logger.info("Amount should be greater that Zero");
				return "Amount should be greater that Zero";
			}
		} catch (Exception e) {
			logger.info("Exception in createSettlementFromPanel : " + e);
			return "There is some problem";
		}
	}

	public List<Object> getMerchantSettlementDetails(User user, String currency) {
		List<Object> settObjects = new ArrayList<>();
		try {
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection("PO_Settlement");

			List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();

			if (user != null && StringUtils.isNotBlank(user.getPayId())) {
				paramConditionLst.add(new BasicDBObject("payId", user.getPayId()));
			}

			if (StringUtils.isNotBlank(currency) && !currency.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject("currency", currency));
			}

			// paramConditionLst.add(new BasicDBObject("status","PENDING"));
			BasicDBObject queryExecute = null;
			MongoCursor<Document> cursor = null;
			if (paramConditionLst.size() > 0) {
				queryExecute = new BasicDBObject("$and", paramConditionLst);

				cursor = coll.find(queryExecute).iterator();
			}

			while (cursor.hasNext()) {
				Document document = (Document) cursor.next();
//				logger.info("Document Data : " + document);
				settObjects.add(gson.fromJson(document.toJson(), SettlementDTO.class));
			}

		} catch (Exception e) {
			logger.info("Exception in : " + e);
		}
//		logger.info("Final Data : " + new Gson().toJson(settObjects));
		return settObjects;
	}

	public String approveRequest(String txnId, String payId, String pgRefnumber, String updatedBy) {

		try {
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection("POtransactionStatus");

			List<BasicDBObject> params = new ArrayList<>();

			params.add(new BasicDBObject(FieldType.PAY_ID.getName(), payId));
			params.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pgRefnumber));
			params.add(new BasicDBObject("TXNTYPE", "SALE"));
			params.add(new BasicDBObject(FieldType.STATUS.getName(), "Request Accepted"));

			BasicDBObject finalquery = new BasicDBObject("$and", params);

			MongoCursor<Document> result = coll.find(finalquery).iterator();
			Document document = null;
			if (result.hasNext()) {
				document = (Document) result.next();
			}

			if (document != null) {
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

				document.put("_id", TransactionManager.getNewTransactionId());
				document.put(FieldType.STATUS.getName(), StatusType.PENDING.getName());
				document.put(FieldType.CREATE_DATE.getName(), dateFormat.format(new Date()));
				document.put(FieldType.UPDATEDBY.getName(), updatedBy);
				document.put(FieldType.RESPONSE_CODE.getName(), ErrorType.PENDING.getCode());
				document.put(FieldType.RESPONSE_MESSAGE.getName(), "Request Approved");

				MongoCollection<Document> collectionDb = dbIns.getCollection("POtransactionStatus");

				Bson updates = null;

				updates = Updates.combine(Updates.set(FieldType.STATUS.getName(), StatusType.PENDING.getName()),
						Updates.set(FieldType.UPDATEDBY.getName(), updatedBy),
						Updates.set(FieldType.RESPONSE_CODE.getName(), ErrorType.PENDING.getCode()),
						Updates.set(FieldType.RESPONSE_MESSAGE.getName(), "Request Approved"));

				UpdateOptions options = new UpdateOptions().upsert(true);
				BasicDBObject searchQuery = new BasicDBObject("$and", params);
				UpdateResult result1 = collectionDb.updateOne(searchQuery, updates, options);
				logger.info("approveRequest : " + result1);

				MongoCollection<Document> collection = dbIns.getCollection("POtransaction");
				collection.insertOne(document);
				
				//added by deep
				//MongoDatabase dbIns = mongoInstance.getDB();
				MongoCollection<Document> collectiondb = dbIns.getCollection("WalletHistory");

		        Document query = new Document("payId", payId).append("currency",multCurrencyCodeDao.getCurrencyNamebyCode(document.getString("CURRENCY_CODE")));
		        String totalBalance="0";
		        MongoCursor<Document> cursor= collectiondb.find(query).iterator();
		        if(cursor.hasNext()) {
		        	Document documentDB=cursor.next();
		        	totalBalance=documentDB.getString("totalBalance");
		        }
		        logger.info("before total balance payout approve request : " + totalBalance);
		        totalBalance=String.valueOf(Double.parseDouble(totalBalance)-Double.parseDouble(document.getString("AMOUNT")));
		        logger.info("after total balance payout approve request : " + totalBalance);
		        MerchantWalletPODTO merchantWalletPODTOdb=new MerchantWalletPODTO();
		        merchantWalletPODTOdb.setCurrency(multCurrencyCodeDao.getCurrencyNamebyCode(document.getString("CURRENCY_CODE")));
		        merchantWalletPODTOdb.setTotalBalance(totalBalance);
		        merchantWalletPODTOdb.setPayId(payId);
				merchantWalletPODao.SaveAndUpdteTotalBalanceWallet(merchantWalletPODTOdb);
				
				walletHistoryRepository.updateBalanceByPayIdAndCurrencyTotalBalance
				(payId, merchantWalletPODTOdb.getCurrency(), "debit", document.getString("AMOUNT"), totalBalance);
				
				
			}

			return "Approved Successfully";
		} catch (Exception e) {
			e.printStackTrace();
			return "Approved Failed";
		}

	}

	public String rejectRequest(String payId, String pgRefNum, String updatedBy,String amount,String currency) {

		try {
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection("POtransactionStatus");

			List<BasicDBObject> params = new ArrayList<>();

			params.add(new BasicDBObject(FieldType.PAY_ID.getName(), payId));
			params.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pgRefNum));
			params.add(new BasicDBObject("TXNTYPE", "SALE"));
			params.add(new BasicDBObject(FieldType.STATUS.getName(), "Request Accepted"));

			BasicDBObject finalquery = new BasicDBObject("$and", params);

			MongoCursor<Document> result = coll.find(finalquery).iterator();
			Document document = null;
			if (result.hasNext()) {
				document = (Document) result.next();
			}

			if (document != null) {
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

				document.put("_id", TransactionManager.getNewTransactionId());
				document.put(FieldType.STATUS.getName(), StatusType.FAILED.getName());
				document.put(FieldType.CREATE_DATE.getName(), dateFormat.format(new Date()));
				document.put(FieldType.UPDATEDBY.getName(), updatedBy);
				document.put(FieldType.RESPONSE_CODE.getName(), ErrorType.FAILED.getCode());
				document.put(FieldType.RESPONSE_MESSAGE.getName(), "Request Rejected");

				MongoCollection<Document> collectionDb = dbIns.getCollection("POtransactionStatus");

				Bson updates = null;

				updates = Updates.combine(Updates.set(FieldType.STATUS.getName(), StatusType.FAILED.getName()),
						Updates.set(FieldType.UPDATEDBY.getName(), updatedBy),
						Updates.set(FieldType.RESPONSE_CODE.getName(), ErrorType.FAILED.getCode()),
						Updates.set(FieldType.RESPONSE_MESSAGE.getName(), "Request Rejected"));

				UpdateOptions options = new UpdateOptions().upsert(true);
				BasicDBObject searchQuery = new BasicDBObject("$and", params);
				UpdateResult result1 = collectionDb.updateOne(searchQuery, updates, options);
				logger.info("Reject Request : " + result1);
				
				//commented by deep

//				Map<String, Object> maps=walletHistoryRepository.findMerchantFundByPayId(payId, currency);
//				if(maps!=null&&maps.size()>0) {
//					String balance=maps.get("finalBalance").toString();
//					String totalBalance=maps.get("totalBalance").toString();
//					balance=String.valueOf((Double.parseDouble(amount)+Double.parseDouble(balance)));
//					totalBalance=String.valueOf((Double.parseDouble(amount)+Double.parseDouble(totalBalance)));
//					walletHistoryRepository.updateBalanceByPayIdAndCurrency(payId, currency, balance, "credit", amount,totalBalance);
//				}
				MongoCollection<Document> collection = dbIns.getCollection("POtransaction");
				collection.insertOne(document);
				
				//added by deep
				//added by deep
				//MongoDatabase dbIns = mongoInstance.getDB();
				MongoCollection<Document> collectiondb = dbIns.getCollection("WalletHistory");

		        Document query = new Document("payId", payId).append("currency",multCurrencyCodeDao.getCurrencyNamebyCode(document.getString("CURRENCY_CODE")));
		        String finalBalance="0";
		        MongoCursor<Document> cursor= collectiondb.find(query).iterator();
		        if(cursor.hasNext()) {
		        	Document documentDB=cursor.next();
		        	finalBalance=documentDB.getString("finalBalance");
		        }
		        logger.info("before available balance payout reject request : " + finalBalance);
		        finalBalance=String.valueOf(Double.parseDouble(finalBalance)+Double.parseDouble(document.getString("AMOUNT")));
		        logger.info("after available balance payout reject request : " + finalBalance);
		        MerchantWalletPODTO merchantWalletPODTOdb=new MerchantWalletPODTO();
		        merchantWalletPODTOdb.setCurrency(multCurrencyCodeDao.getCurrencyNamebyCode(document.getString("CURRENCY_CODE")));
		        merchantWalletPODTOdb.setFinalBalance(finalBalance);
		        merchantWalletPODTOdb.setPayId(payId);
				merchantWalletPODao.SaveAndUpdteAvailableBalanceWallet(merchantWalletPODTOdb);
				
				DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
				LocalDateTime myDateObj = LocalDateTime.now();
				String formattedDate = myDateObj.format(myFormatObj);
				PassbookPODTO passbookPODTO = new PassbookPODTO();
				passbookPODTO.setPayId(payId);
				passbookPODTO.setType("CREDIT");
				passbookPODTO.setNarration("PAYOUT REVERSAL");
				passbookPODTO.setCreateDate(formattedDate);
				passbookPODTO.setTxnId(TransactionManager.getNewTransactionId());
				passbookPODTO.setCreditAmt(finalBalance);
				passbookPODTO.setAmount(document.getString("AMOUNT"));
				passbookPODTO.setCurrency(multCurrencyCodeDao.getCurrencyNamebyCode(document.getString("CURRENCY_CODE")));
				passbookPODTO.setRespTxn(document.getString("ORDER_ID"));
				logger.info("Save the Transaction History in passbook"+passbookPODTO.toString());
				merchantWalletPODao.savePassbookDetailsByPassbook(passbookPODTO);
				
				
				walletHistoryRepository.updateBalanceByPayIdAndCurrencyAvailableBalance
				(payId, merchantWalletPODTOdb.getCurrency(), "credit", document.getString("AMOUNT"), finalBalance);
			}

			return "Rejected Successfully";
		} catch (Exception e) {
			e.printStackTrace();
			return "Rejected Failed";
		}

	}

	public List<Object> getMerchantPendingSettlementDetailsForPO(User user, String currency) {
		List<Object> settObjects = new ArrayList<>();
		try {
			MongoDatabase dbIns = mongoInstance.getDB();

			MongoCollection<Document> coll = dbIns.getCollection("POtransactionStatus");

			List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();

			paramConditionLst.add(new BasicDBObject("STATUS", "Request Accepted"));

			BasicDBObject queryExecute = new BasicDBObject("$and", paramConditionLst);

			//MongoCursor<Document> cursor = coll.find(queryExecute).iterator();
			MongoCursor<Document> cursor = coll.find(queryExecute).sort(Sorts.descending("CREATE_DATE")).iterator();

			while (cursor.hasNext()) {
				Document document = (Document) cursor.next();
			     String businessName = null;
				if(document.getString("PAY_ID")!=null||!document.getString("PAY_ID").isEmpty()){

					businessName=userDao.getBusinessNameByPayId(document.getString("PAY_ID"));
					logger.info("Cash Deposit PO business Name SettlementPORepository "+businessName);
				}
				logger.info("getMerchantPendingSettlementDetailsForPO : " + document);
				POTransactionStatusDTO poTransactionStatusDTO = new POTransactionStatusDTO();
				poTransactionStatusDTO.setBusinessName(businessName);
				poTransactionStatusDTO.setAccCityName(StringUtils.isNotBlank(document.getString("ACC_CITY_NAME"))
						? document.getString("ACC_CITY_NAME")
						: "NA");
				poTransactionStatusDTO.setAccName(
						StringUtils.isNotBlank(document.getString("ACC_NAME")) ? document.getString("ACC_NAME") : "NA");
				poTransactionStatusDTO.setAccNo(
						StringUtils.isNotBlank(document.getString("ACC_NO")) ? document.getString("ACC_NO") : "NA");
				poTransactionStatusDTO.setAccProvince(
						StringUtils.isNotBlank(document.getString("ACC_PROVINCE")) ? document.getString("ACC_PROVINCE")
								: "NA");
				poTransactionStatusDTO.setAccType(
						StringUtils.isNotBlank(document.getString("ACC_TYPE")) ? document.getString("ACC_TYPE") : "NA");

				poTransactionStatusDTO.setAcquirerType(StringUtils.isNotBlank(document.getString("ACQUIRER_TYPE"))
						? document.getString("ACQUIRER_TYPE")
						: "NA");
				poTransactionStatusDTO.setAmount(
						StringUtils.isNotBlank(document.getString("AMOUNT")) ? document.getString("AMOUNT") : "NA");
				poTransactionStatusDTO.setBankBranch(
						StringUtils.isNotBlank(document.getString("BANK_BRANCH")) ? document.getString("BANK_BRANCH")
								: "NA");
				poTransactionStatusDTO.setBankCode(
						StringUtils.isNotBlank(document.getString("BANK_CODE")) ? document.getString("BANK_CODE")
								: "NA");
				poTransactionStatusDTO.setClientIp(
						StringUtils.isNotBlank(document.getString("CLIENT_IP")) ? document.getString("CLIENT_IP")
								: "NA");

				poTransactionStatusDTO.setCreateDate(
						StringUtils.isNotBlank(document.getString("CREATE_DATE")) ? document.getString("CREATE_DATE")
								: "NA");
				poTransactionStatusDTO.setCurrency(StringUtils.isNotBlank(document.getString("CURRENCY_CODE"))
						? multCurrencyCodeDao.getCurrencyNamebyCode(document.getString("CURRENCY_CODE"))
						: "NA");

				poTransactionStatusDTO.setIfsc(
						StringUtils.isNotBlank(document.getString("IFSC")) ? document.getString("IFSC") : "NA");
				poTransactionStatusDTO.setOrderId(
						StringUtils.isNotBlank(document.getString("ORDER_ID")) ? document.getString("ORDER_ID") : "NA");
				poTransactionStatusDTO.setPayerName(
						StringUtils.isNotBlank(document.getString("PAYER_NAME")) ? document.getString("PAYER_NAME")
								: "NA");
				poTransactionStatusDTO.setPayerPhone(
						StringUtils.isNotBlank(document.getString("PAYER_PHONE")) ? document.getString("PAYER_PHONE")
								: "NA");
				poTransactionStatusDTO.setPayId(
						StringUtils.isNotBlank(document.getString("PAY_ID")) ? document.getString("PAY_ID") : "NA");
				poTransactionStatusDTO.setPayType(
						StringUtils.isNotBlank(document.getString("PAY_TYPE")) ? document.getString("PAY_TYPE") : "NA");
				poTransactionStatusDTO.setPgRefNum(
						StringUtils.isNotBlank(document.getString("PG_REF_NUM")) ? document.getString("PG_REF_NUM")
								: "NA");

				poTransactionStatusDTO.setStatus(
						StringUtils.isNotBlank(document.getString("STATUS")) ? document.getString("STATUS") : "NA");
				poTransactionStatusDTO.setTxnType(
						StringUtils.isNotBlank(document.getString("TXNTYPE")) ? document.getString("TXNTYPE") : "NA");

				settObjects.add(poTransactionStatusDTO);

			}

		} catch (Exception e) {
			logger.info("Exception in getMerchantPendingSettlementDetailsForPO : " + e);
		//	e.printStackTrace();
		}
		return settObjects;
	}

	public List<Object> getMerchantPendingSettlementDetails(User user, String currency) {
		List<Object> settObjects = new ArrayList<>();
		try {
			logger.info("getMerchantPendingSettlementDetails First");
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.PO_SETTELEMENT_COLLECTION.getValue()));
			logger.info("getMerchantPendingSettlementDetails second");
			List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();

			if (user != null && StringUtils.isNotBlank(user.getPayId())) {
				paramConditionLst.add(new BasicDBObject("payId", user.getPayId()));
			}

			if (StringUtils.isNotBlank(currency) && !currency.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject("currency", currency));
			}

			paramConditionLst.add(new BasicDBObject("status", "PENDING"));

			BasicDBObject queryExecute = new BasicDBObject("$and", paramConditionLst);

			MongoCursor<Document> cursor = coll.find(queryExecute).iterator();

			while (cursor.hasNext()) {
				Document document = (Document) cursor.next();
				logger.info("getMerchantPendingSettlementDetails : " + document);
				settObjects.add(gson.fromJson(document.toJson(), SettlementDTO.class));
			}

		} catch (Exception e) {
			logger.info("Exception in : " + e);
		}
		return settObjects;
	}

	@Autowired
	private PassbookLedgerDao passbookLedgerDao;
	@Autowired
	private MerchantWalletPODao merchantWalletPODao;

	public String approveRequestDetail(String txnId, String payId, String currency, String updatedBy, String uid) {
		try {
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection("PO_Settlement");

			List<BasicDBObject> params = new ArrayList<>();

			params.add(new BasicDBObject("payId", payId));
			params.add(new BasicDBObject("status", "PENDING"));
			params.add(new BasicDBObject("currency", currency));
			params.add(new BasicDBObject("uid",uid));

			BasicDBObject finalquery = new BasicDBObject("$and", params);

			MongoCursor<Document> result = coll.find(finalquery).iterator();
			Document document = null;
			if (result.hasNext()) {
				document = (Document) result.next();
			}

			if (document != null) {
				SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//				SimpleDateFormat sdd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				MongoCollection<Document> collectionDb = dbIns.getCollection("PO_Settlement");

				Bson updates = null;

				updates = Updates.combine(Updates.set("status", "SETTLED"),
						Updates.set(FieldType.UPDATEDBY.getName(), updatedBy),
						Updates.set("settlementDate", sd.format(new Date())));

				UpdateOptions options = new UpdateOptions().upsert(true);
				BasicDBObject searchQuery = new BasicDBObject("$and", params);
				UpdateResult result1 = collectionDb.updateOne(searchQuery, updates, options);
				logger.info("approveRequestDetail : " + result1);

				// insert passbook entry here
//				PassbookPODTO passbookPODTO = new PassbookPODTO();
//				passbookPODTO.setAmount(document.getString("amount"));
//				passbookPODTO.setBusinessName(document.getString("merchantName"));
//				passbookPODTO.setCreateDate(sdd.format(new Date()));
//				passbookPODTO.setCurrency(currency);
//				passbookPODTO.setNarration("Settle Out");
//				passbookPODTO.setPayId(document.getString("payId"));
//				passbookPODTO.setRespTxn("NA");
//				passbookPODTO.setTxnId(txnId);
//				passbookPODTO.setType("debit");
//				passbookPODTO.setDebitAmt(document.getString("amount"));
//
//				passbookLedgerDao.createPassbookEntry(passbookPODTO);
//				logger.info("Successfully inserted in passbook and approved");
				//MongoDatabase dbIns = mongoInstance.getDB();
		        MongoCollection<Document> collection = dbIns.getCollection("WalletHistory");

		        Document query = new Document("payId", payId).append("currency", currency);
		        String totalBalance="0";
		        MongoCursor<Document> cursor= collection.find(query).iterator();
		        if(cursor.hasNext()) {
		        	Document documentDB=cursor.next();
		        	totalBalance=documentDB.getString("totalBalance");
		        }
		        logger.info("before Total balance settlement approve request : " + totalBalance);
		        totalBalance=String.valueOf(Double.parseDouble(totalBalance)-Double.parseDouble(document.getString("amount")));
		        logger.info("after Total balance settlement approve request : " + totalBalance);
		        MerchantWalletPODTO merchantWalletPODTOdb=new MerchantWalletPODTO();
		        merchantWalletPODTOdb.setCurrency(currency);
		        merchantWalletPODTOdb.setTotalBalance(totalBalance);
		        merchantWalletPODTOdb.setPayId(payId);
		        
				merchantWalletPODao.SaveAndUpdteTotalBalanceWallet(merchantWalletPODTOdb);

			}

			return "Approved Successfully";
		} catch (Exception e) {
			e.printStackTrace();
			return "Approved Failed";
		}
	}
	
	public String rejectRequestDetail(String payId, String currency, String updatedBy,String uid) {
		try {
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection("PO_Settlement");

			List<BasicDBObject> params = new ArrayList<>();

			params.add(new BasicDBObject("payId", payId));
			params.add(new BasicDBObject("status", "PENDING"));
			params.add(new BasicDBObject("currency", currency));
			params.add(new BasicDBObject("uid", uid));

			BasicDBObject finalquery = new BasicDBObject("$and", params);

			MongoCursor<Document> result = coll.find(finalquery).iterator();
			Document document = null;
			if (result.hasNext()) {
				document = (Document) result.next();
			}

			if (document != null) {
				SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				SimpleDateFormat sdd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				MongoCollection<Document> collectionDb = dbIns.getCollection("PO_Settlement");

				Bson updates = null;

				updates = Updates.combine(Updates.set("status", "REJECTED"),
						Updates.set(FieldType.UPDATEDBY.getName(), updatedBy),
						Updates.set("settlementDate", sd.format(new Date())));

				UpdateOptions options = new UpdateOptions().upsert(true);
				BasicDBObject searchQuery = new BasicDBObject("$and", params);
				UpdateResult result1 = collectionDb.updateOne(searchQuery, updates, options);
				logger.info("rejectRequestDetail : " + result1);

				// insert passbook entry here
				PassbookPODTO passbookPODTO = new PassbookPODTO();
				passbookPODTO.setAmount(document.getString("amount"));
				passbookPODTO.setBusinessName(document.getString("merchantName"));
				passbookPODTO.setCreateDate(sdd.format(new Date()));
				passbookPODTO.setCurrency(document.getString("currency"));
				passbookPODTO.setNarration("Settle Out Reversal");
				passbookPODTO.setPayId(document.getString("payId"));
				passbookPODTO.setRespTxn("NA");
				passbookPODTO.setTxnId("NA");

				passbookPODTO.setType("credit");
				passbookPODTO.setCreditAmt(document.getString("amount"));

				passbookLedgerDao.createPassbookEntry(passbookPODTO);
		
				//commented by deep
				
//				Map<String, Object> maps=walletHistoryRepository.findMerchantFundByPayId(payId, currency);
//				if(maps!=null&&maps.size()>0) {
//					String balance=maps.get("finalBalance").toString();
//					String totalBalance=maps.get("totalBalance").toString();
//					balance=String.valueOf((Double.parseDouble(document.getString("amount"))+Double.parseDouble(balance)));
//					totalBalance=String.valueOf((Double.parseDouble(document.getString("amount"))+Double.parseDouble(totalBalance)));
//					walletHistoryRepository.updateBalanceByPayIdAndCurrency(payId, currency, balance, "credit", document.getString("amount"),totalBalance);
//				}
				
				//added by deep
				MongoCollection<Document> collection = dbIns.getCollection("WalletHistory");

		        Document query = new Document("payId", payId).append("currency", currency);
		        String finalBalance="0";
		        MongoCursor<Document> cursor= collection.find(query).iterator();
		        if(cursor.hasNext()) {
		        	Document documentDB=cursor.next();
		        	finalBalance=documentDB.getString("finalBalance");
		        }
		        logger.info("before Total balance settlement reject request : " + finalBalance);
		        finalBalance=String.valueOf(Double.parseDouble(finalBalance)+Double.parseDouble(document.getString("amount")));
		        logger.info("after Total balance settlement reject request : " + finalBalance);
		        MerchantWalletPODTO merchantWalletPODTOdb=new MerchantWalletPODTO();
		        merchantWalletPODTOdb.setCurrency(currency);
		        merchantWalletPODTOdb.setFinalBalance(finalBalance);
		        merchantWalletPODTOdb.setPayId(payId);
		        
				merchantWalletPODao.SaveAndUpdteAvailableBalanceWallet(merchantWalletPODTOdb);
				
				logger.info("Successfully inserted in passbook and approved");

			}

			return "Rejected Successfully";
		} catch (Exception e) {
			e.printStackTrace();
			return "Rejected Failed";
		}
	}

}
