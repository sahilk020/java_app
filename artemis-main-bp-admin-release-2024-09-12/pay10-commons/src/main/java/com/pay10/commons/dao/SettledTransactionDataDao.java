package com.pay10.commons.dao;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.user.SettledTransactionDataObject;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.PropertiesManager;

@Service
public class SettledTransactionDataDao {

	
	private static final String prefix = "MONGO_DB_";
	@Autowired
	private MongoInstance mongoInstance;

	@Autowired
	private PropertiesManager propertiesManager;
	private static Logger logger = LoggerFactory.getLogger(SettledTransactionDataDao.class.getName());
	
	public void insertTransaction(List<SettledTransactionDataObject> settledTransactionDataList) throws SystemException {
		try {
			MongoDatabase dbIns = null;
			
			logger.info("Splitting documents list in chunk 0f 100000 documents");
			int partitionSize = 100000;
			List<List<SettledTransactionDataObject>> partitions = new LinkedList<List<SettledTransactionDataObject>>();
			
			for (int i = 0; i < settledTransactionDataList.size(); i += partitionSize) {
			    partitions.add(settledTransactionDataList.subList(i,
			            Math.min(i + partitionSize, settledTransactionDataList.size())));
			}
			
			for (int i = 0; i < partitions.size(); i++) {
				logger.info("Chunk "+ i +" size = " + partitions.get(i).size());
			}
			
			for (int i = 0; i < partitions.size(); i++) {
				
				List<SettledTransactionDataObject> settledTransactionDataListpart = new ArrayList<SettledTransactionDataObject>();
				settledTransactionDataListpart = partitions.get(i);
				
				logger.info("Adding Chunk "+ i +" size = " + settledTransactionDataListpart.size());
				
				List<Document> documents = new ArrayList<>();
				for (SettledTransactionDataObject settledTransactionData : settledTransactionDataListpart) {
					Document e = new Document();
					e.put("_id", settledTransactionData.get_id());
					e.put(FieldType.TXN_ID.getName(), settledTransactionData.getTransactionId());
					e.put(FieldType.PG_REF_NUM.getName(), settledTransactionData.getPgRefNum());
					e.put(FieldType.PAYMENTS_REGION.getName(), settledTransactionData.getTransactionRegion());
					e.put(FieldType.POST_SETTLED_FLAG.getName(), settledTransactionData.getPostSettledFlag());
					e.put(FieldType.TXNTYPE.getName(), settledTransactionData.getTxnType());
					e.put(FieldType.ACQUIRER_TYPE.getName(), settledTransactionData.getAcquirerType());
					e.put(FieldType.PAYMENT_TYPE.getName(), settledTransactionData.getPaymentMethods());
					e.put(FieldType.CREATE_DATE.getName(), settledTransactionData.getCreateDate());
					e.put(FieldType.DATE_INDEX.getName(), settledTransactionData.getCreateDate().substring(0, 10).replace("-", ""));
					e.put(FieldType.ORDER_ID.getName(), settledTransactionData.getOrderId());
					e.put(FieldType.PAY_ID.getName(), settledTransactionData.getPayId());
					e.put(FieldType.MOP_TYPE.getName(), settledTransactionData.getMopType());
					e.put(FieldType.CURRENCY_CODE.getName(), settledTransactionData.getCurrency());
					e.put(FieldType.CARD_HOLDER_TYPE.getName(), settledTransactionData.getCardHolderType());
					e.put(FieldType.PG_DATE_TIME.getName(), settledTransactionData.getCaptureDate());
					e.put(FieldType.PG_DATE_TIME_INDEX.getName(), settledTransactionData.getCaptureDate().substring(0, 10).replace("-", ""));
					e.put(FieldType.ACQ_ID.getName(), settledTransactionData.getAcqId());
					e.put(FieldType.RRN.getName(), settledTransactionData.getRrn());
					e.put(FieldType.UDF6.getName(), settledTransactionData.getDeltaRefundFlag());
					e.put(FieldType.SURCHARGE_FLAG.getName(), settledTransactionData.getSurchargeFlag());
					e.put(FieldType.REFUND_FLAG.getName(), settledTransactionData.getRefundFlag());
					e.put(FieldType.ARN.getName(), settledTransactionData.getArn());
					e.put(FieldType.ORIG_TXN_ID.getName(), settledTransactionData.getOrigTxnId());
					e.put(FieldType.TOTAL_AMOUNT.getName(), settledTransactionData.getTotalAmount());
					e.put(FieldType.AMOUNT.getName(), settledTransactionData.getAmount());
					e.put(FieldType.SURCHARGE_ACQ.getName(), settledTransactionData.getTdrScAcquirer());
					e.put(FieldType.SURCHARGE_IPAY.getName(), settledTransactionData.getTdrScIpay());
					e.put(FieldType.SURCHARGE_MMAD.getName(), settledTransactionData.getTdrScMmad());
					e.put(FieldType.GST_ACQ.getName(), settledTransactionData.getGstScAcquirer());
					e.put(FieldType.GST_IPAY.getName(), settledTransactionData.getGstScIpay());
					e.put(FieldType.GST_MMAD.getName(), settledTransactionData.getGstScMmad());
					e.put(FieldType.STATUS.getName(), settledTransactionData.getStatus());
					e.put(FieldType.REFUND_ORDER_ID.getName(), settledTransactionData.getRefundOrderId());
					documents.add(e);
				}
				
				logger.info(" Chunk index = "+ i +"Adding "+documents.size()+" transactions in mongo document for upload to settlement table");
				dbIns = mongoInstance.getDB();
				MongoCollection<Document>  collection = dbIns
						.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.SETTLED_TRANSACTIONS_NAME.getValue()));
				collection.insertMany(documents);
				logger.info(" Chunk index = "+ i +"Added "+documents.size()+" transactions in mongo document for upload to settlement table");
			}
			
		
		}
		
		catch(Exception e) {
			logger.info("Exception in insertTransaction for settlement collection "+e);
		}
	}
	
}
