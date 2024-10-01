package com.pay10.commons.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.user.CustomerAddress;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.MopType;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.TransactionType;

@Component
public class TransactionSearchServiceMongo {
	private static Logger logger = LoggerFactory.getLogger(TransactionSearchServiceMongo.class.getName());
	private static final String prefix = "MONGO_DB_";
	
	@Autowired
	private PropertiesManager propertiesManager;
	@Autowired
	private MongoInstance mongoInstance;

	public CustomerAddress getDumyCustAddress(String txnId , String oId, User user, String orderId, String txnType, String pgRefNum) throws SystemException {

		CustomerAddress custAddress = getCustAddressBillingColl(txnId,oId,user, orderId,txnType,  pgRefNum);
		//CustomerAddress custAddress2 = getCustAddressTxnColl(txnId, custAddress1);
		return custAddress;

	}

	public CustomerAddress getCustAddressBillingColl(String txnId ,String oid , User user, String orderId, String txnType, String pgRefNum) throws SystemException {
		
		CustomerAddress custAddress = new CustomerAddress();
		MongoDatabase dbIns = mongoInstance.getDB();

		MongoCollection<Document> collection = dbIns.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.BILLING_COLLECTION.getValue()));
		MongoCursor<Document> cursor = collection.find(new BasicDBObject(FieldType.TXN_ID.getName(), oid)).iterator();

		while (cursor.hasNext()) {
			Document mydata = cursor.next();

			if (mydata.getString(FieldType.CUST_NAME.toString()) != null) {
				custAddress.setCustName(mydata.getString(FieldType.CUST_NAME.toString()));
			}
					
			if (mydata.getString(FieldType.CUST_PHONE.toString()) != null) {
				custAddress.setCustPhone(mydata.getString(FieldType.CUST_PHONE.toString()));
			}
			
			if (mydata.getString(FieldType.CUST_STREET_ADDRESS1.toString()) != null) {
				custAddress.setCustStreetAddress1((mydata.getString(FieldType.CUST_STREET_ADDRESS1.toString())));
			}
			if (mydata.getString(FieldType.CUST_STREET_ADDRESS2.toString()) != null) {
				custAddress.setCustStreetAddress2(mydata.getString(FieldType.CUST_STREET_ADDRESS2.toString()));
			}
			if (mydata.getString(FieldType.CUST_CITY.toString()) != null) {
				custAddress.setCustCity(mydata.getString(FieldType.CUST_CITY.toString()));
			}
			if (mydata.getString(FieldType.CUST_STATE.toString()) != null) {
				custAddress.setCustState(mydata.getString(FieldType.CUST_STATE.toString()));
			}
			if (mydata.getString(FieldType.CUST_COUNTRY.toString()) != null) {
				custAddress.setCustCountry(mydata.getString(FieldType.CUST_COUNTRY.toString()));
			}
			if (mydata.getString(FieldType.CUST_ZIP.toString()) != null) {
				custAddress.setCustZip(mydata.getString(FieldType.CUST_ZIP.toString()));
			}
			if (mydata.getString(FieldType.CUST_SHIP_NAME.toString()) != null) {
				custAddress.setCustShipName((mydata.getString(FieldType.CUST_SHIP_NAME.toString())));
			}
			if (mydata.getString(FieldType.CUST_SHIP_STREET_ADDRESS1.toString()) != null) {
				custAddress
						.setCustShipStreetAddress1((mydata.getString(FieldType.CUST_SHIP_STREET_ADDRESS1.toString())));
			}
			if (mydata.getString(FieldType.CUST_SHIP_STREET_ADDRESS2.toString()) != null) {
				custAddress.setCustShipStreetAddress2(mydata.getString(FieldType.CUST_SHIP_STREET_ADDRESS2.toString()));
			}
			if (mydata.getString(FieldType.CUST_SHIP_CITY.toString()) != null) {
				custAddress.setCustShipCity(mydata.getString(FieldType.CUST_SHIP_CITY.toString()));
			}
			if (mydata.getString(FieldType.CUST_SHIP_PHONE.toString()) != null) {
				custAddress.setCustShipPhone(mydata.getString(FieldType.CUST_SHIP_PHONE.toString()));
			}
			if (mydata.getString(FieldType.CUST_SHIP_STATE.toString()) != null) {
				custAddress.setCustShipState(mydata.getString(FieldType.CUST_SHIP_STATE.toString()));
			}
			if (mydata.getString(FieldType.CUST_SHIP_COUNTRY.toString()) != null) {
				custAddress.setCustShipCountry(mydata.getString(FieldType.CUST_SHIP_COUNTRY.toString()));
			}
			if (mydata.getString(FieldType.CUST_SHIP_ZIP.toString()) != null) {
				custAddress.setCustShipZip(mydata.getString(FieldType.CUST_SHIP_ZIP.toString()));
			}
			
			if (StringUtils.isNotBlank(mydata.getString(FieldType.ORDER_ID.toString()))) {
				orderId = mydata.getString(FieldType.ORDER_ID.toString());
			} else {
				orderId = null;
			}
			
		}
		
		cursor.close();
		
		// Get Payment and transaction Data now 
		
		MongoCollection<Document> txnCollection = dbIns.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));
			
		// Below Data for Admin and Sub Admin
		if (user.getUserType().equals(UserType.ADMIN) || user.getUserType().equals(UserType.SUBADMIN) || 
				user.getUserType().equals(UserType.MERCHANT) || user.getUserType().equals(UserType.SUBUSER)
				|| user.getUserType().equals(UserType.RESELLER)) {
			
			List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();

			BasicDBObject oidQuery = new BasicDBObject(FieldType.OID.getName(), oid);
			BasicDBObject orderIdQuery = new BasicDBObject(FieldType.ORDER_ID.getName(), orderId);
			BasicDBObject txnTypeQuery = new BasicDBObject(FieldType.ORIG_TXNTYPE.getName(), txnType);
			
			
			
			paramConditionLst.add(oidQuery);
			paramConditionLst.add(orderIdQuery);
			paramConditionLst.add(txnTypeQuery);
			
			if (txnType.equalsIgnoreCase(TransactionType.REFUND.getName())) {
				BasicDBObject pgRefQuery = new BasicDBObject(FieldType.PG_REF_NUM.getName(), pgRefNum);
				paramConditionLst.add(pgRefQuery);
			}
			

			BasicDBObject finalquery = new BasicDBObject("$and", paramConditionLst);

			MongoCursor<Document> txnCursor = txnCollection.find(finalquery).iterator();

			while (txnCursor.hasNext()) {
				Document mydata = txnCursor.next();
				
				if(StringUtils.isNotBlank(mydata.getString(FieldType.UDF6.getName()))) {
					custAddress.setUdf6(mydata.getString(FieldType.UDF6.getName()));
				}

				if (StringUtils.isNotBlank(mydata.getString(FieldType.CARD_MASK.toString()))) {
					custAddress.setCardMask(mydata.getString(FieldType.CARD_MASK.toString()));
				} else {
					custAddress.setCardMask(CrmFieldConstants.NOT_AVAILABLE.getValue());
				}
				
				if (StringUtils.isNotBlank(mydata.getString(FieldType.PAYMENT_TYPE.toString())) && mydata.getString(FieldType.PAYMENT_TYPE.toString()).equalsIgnoreCase(PaymentType.UPI.getCode())) {
					
					if (StringUtils.isNotBlank(mydata.getString(FieldType.UDF1.toString()))) {
						custAddress.setCardMask(mydata.getString(FieldType.UDF1.toString()));
					}
					
				}
				
				if (mydata.getString(FieldType.CARD_HOLDER_NAME.toString()) != null) {
					custAddress.setCardHolderName(mydata.getString(FieldType.CARD_HOLDER_NAME.toString()));
				}
				else if (mydata.getString(FieldType.CUST_NAME.toString()) != null) {
						custAddress.setCardHolderName(mydata.getString(FieldType.CUST_NAME.toString()));
					}
				else {
					custAddress.setCardHolderName(CrmFieldConstants.NOT_AVAILABLE.getValue());
				}
				
				if (StringUtils.isNotBlank(mydata.getString(FieldType.ACQUIRER_TYPE.toString()))) {
					custAddress.setAcquirerType(mydata.getString(FieldType.ACQUIRER_TYPE.toString()));
				} else {
					custAddress.setAcquirerType(CrmFieldConstants.NOT_AVAILABLE.getValue());
				}


				if (StringUtils.isNotBlank(mydata.getString(FieldType.PG_TDR_SC.toString()))) {
					custAddress.setPgTdr(mydata.getString(FieldType.PG_TDR_SC.toString()));
				}

				if (StringUtils.isNotBlank(mydata.getString(FieldType.PG_GST.toString()))) {
					custAddress.setPgGst(mydata.getString(FieldType.PG_GST.toString()));
				}

				if (StringUtils.isNotBlank(mydata.getString(FieldType.ACQUIRER_TDR_SC.toString()))) {
					custAddress.setAcquirerTdr(mydata.getString(FieldType.ACQUIRER_TDR_SC.toString()));
				}

				if (StringUtils.isNotBlank(mydata.getString(FieldType.ACQUIRER_GST.toString()))) {
					custAddress.setAcquirerGst(mydata.getString(FieldType.ACQUIRER_GST.toString()));
				}
				
				if (StringUtils.isNotBlank(mydata.getString(FieldType.INTERNAL_CARD_ISSUER_BANK.toString()))) {
					custAddress.setIssuer(mydata.getString(FieldType.INTERNAL_CARD_ISSUER_BANK.toString()));
				}
				
				if (StringUtils.isNotBlank(mydata.getString(FieldType.PAYMENT_TYPE.toString())) && mydata.getString(FieldType.PAYMENT_TYPE.toString()).equalsIgnoreCase(PaymentType.UPI.getCode())) {
					custAddress.setIssuer(CrmFieldConstants.NOT_AVAILABLE.getValue());
				}
				
				if (StringUtils.isNotBlank(mydata.getString(FieldType.MOP_TYPE.toString()))) {
					custAddress.setMopType(MopType.getmopName(mydata.getString(FieldType.MOP_TYPE.toString())));
				}
				
				if (StringUtils.isNotBlank(mydata.getString(FieldType.PG_TXN_MESSAGE.toString()))) {
					custAddress.setPgTxnMsg(mydata.getString(FieldType.PG_TXN_MESSAGE.toString()));
				}

				if (mydata.getString(FieldType.INTERNAL_TXN_AUTHENTICATION.toString()) != null) {
					custAddress.setInternalTxnAuthentication(
							mydata.getString(FieldType.INTERNAL_TXN_AUTHENTICATION.toString()));
				} 
				
				if (mydata.getString(FieldType.CUST_NAME.toString()) != null) {
					custAddress.setCustName(mydata.getString(FieldType.CUST_NAME.toString()));
				}
						
				if (mydata.getString(FieldType.CUST_PHONE.toString()) != null) {
					custAddress.setCustPhone(mydata.getString(FieldType.CUST_PHONE.toString()));
				}
				
			}

		}

		return custAddress;
	}

	public String getTotlaRefundByORderId(String orderId) {
		Double totalRef = 0.00;
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> txnCollection = dbIns.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));

		List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();

		BasicDBObject orderIdQuery = new BasicDBObject(FieldType.ORDER_ID.getName(), orderId);
		BasicDBObject txnTypeQuery = new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.REFUND.getName());
		BasicDBObject statusTypeQuery = new BasicDBObject(FieldType.STATUS.getName(), "Captured");			
		paramConditionLst.add(orderIdQuery);
		paramConditionLst.add(txnTypeQuery);
		paramConditionLst.add(statusTypeQuery);
			
		BasicDBObject finalquery = new BasicDBObject("$and", paramConditionLst);

		MongoCursor<Document> txnCursor = txnCollection.find(finalquery).iterator();

		while (txnCursor.hasNext()) {
			Document mydata = txnCursor.next();
			totalRef+=Double.valueOf(mydata.getString(FieldType.AMOUNT.toString()));
		}
			
		String totalRefund = String.valueOf(totalRef);
		return totalRefund;
	}

	/*
	 * public CustomerAddress getCustAddressTxnColl(String txnId, CustomerAddress
	 * custAddress1) throws SystemException { BasicDBObject conditionQuery = null;
	 * 
	 * if (!txnId.isEmpty()) { conditionQuery = new
	 * BasicDBObject(FieldType.TXN_ID.getName(), txnId); }
	 * 
	 * MongoDatabase dbIns = mongoInstance.getDB();
	 * 
	 * MongoCollection<Document> collection =
	 * dbIns.getCollection(PropertiesManager.propertiesMap.get(prefix +
	 * Constants.COLLECTION_NAME.getValue())); MongoCursor<Document> cursor =
	 * collection.find(conditionQuery).iterator();
	 * 
	 * while (cursor.hasNext()) { Document mydata = cursor.next();
	 * 
	 * if (mydata.getString(FieldType.INTERNAL_TXN_AUTHENTICATION.toString()) !=
	 * null) { custAddress1.setInternalTxnAuthentication(
	 * mydata.getString(FieldType.INTERNAL_TXN_AUTHENTICATION.toString())); }
	 * 
	 * if (mydata.getString(FieldType.CUST_NAME.toString()) != null) {
	 * custAddress1.setCustName(mydata.getString(FieldType.CUST_NAME.toString())); }
	 * 
	 * if (mydata.getString(FieldType.CUST_PHONE.toString()) != null) {
	 * custAddress1.setCustPhone(mydata.getString(FieldType.CUST_PHONE.toString()));
	 * }
	 * 
	 * }
	 * 
	 * return custAddress1; }
	 */
}
