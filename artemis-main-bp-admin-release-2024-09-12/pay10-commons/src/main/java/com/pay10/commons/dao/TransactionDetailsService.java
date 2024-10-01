package com.pay10.commons.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger; 
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.user.TransactionHistory;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.PropertiesManager;
@Service
public class TransactionDetailsService {

	private static Logger logger = LoggerFactory.getLogger(TransactionDetailsService.class.getName());

	@Autowired
	private MongoInstance mongoInstance;

	private Connection getConnection() throws SQLException {
		return DataAccessObject.getBasicConnection();
	}

	private static final String prefix = "MONGO_DB_";
	public final static String query = "Select ORIG_TXN_ID ,ORDER_ID, TXN_ID, CREATE_DATE, PAY_ID,ACQUIRER_TYPE, "
			+ "CARD_MASK, MOP_TYPE, PAYMENT_TYPE, STATUS, TXNTYPE, CUST_EMAIL, "
			+ "INTERNAL_CUST_IP, INTERNAL_CUST_COUNTRY_NAME,INTERNAL_CARD_ISSUER_BANK,INTERNAL_CARD_ISSUER_COUNTRY, ACQ_ID, CURRENCY_CODE, AMOUNT,OID from "
			+ "TRANSACTION where OID in (select OID from TRANSACTION where TXN_ID=?)";

	public final static String orderIdSearchQuery = "Select ORIG_TXN_ID ,ORDER_ID, TXN_ID, CREATE_DATE, PAY_ID,ACQUIRER_TYPE, CARD_MASK, MOP_TYPE, PAYMENT_TYPE,"
			+ " STATUS, TXNTYPE, CUST_EMAIL, INTERNAL_CUST_IP,  ACQ_ID, CURRENCY_CODE, AMOUNT from "
			+ " TRANSACTION T where ORDER_ID in (select ORDER_ID from TRANSACTION where TXN_ID = ?) and "
			+ " PAY_ID in (select PAY_ID from TRANSACTION where TXN_ID = ?)";

	public final static String getOIDQuery = "Select MIN(OID) As 'OID' from TRANSACTION where TXN_ID = ?";
	public final static String txnAuthenticationQuery = "Select INTERNAL_TXN_AUTHENTICATION from TRANSACTION where TXN_ID = ?";
	public final static String updateAuthenticationQuery = "Update TRANSACTION Set INTERNAL_TXN_AUTHENTICATION = ? where TXN_ID = ?";

	public TransactionDetailsService() {

	}

	public List<TransactionHistory> getTransaction(String txnId) throws SystemException {
		String payId = null;
		String oId = null;
		List<TransactionHistory> transactions = new ArrayList<TransactionHistory>();
		BasicDBObject fields = new BasicDBObject();
		fields.put("TXN_ID", txnId);
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns
				.getCollection(PropertiesManager.propertiesMap.get(prefix+Constants.COLLECTION_NAME.getValue()));
		MongoCursor<Document> cursor = coll.find(fields).iterator();

		while (cursor.hasNext()) {
			Document dbobj = cursor.next();

			payId = dbobj.getString(FieldType.PAY_ID.toString());
			oId = dbobj.getString(FieldType.OID.toString());
		}

		if (!payId.equals(null) || !oId.equals(null)) {

			BasicDBObject dbObjByOid = new BasicDBObject();
			dbObjByOid.put("OID", oId);
			MongoCursor<Document> cursor2 = coll.find(dbObjByOid).iterator();
			try {
				transactions = setTransactionList(cursor2);
			
			if (transactions.size() == 0) {
				cursor2.close();
				BasicDBObject dbObjByPayId = new BasicDBObject();
				BasicDBObject dbObjOid = new BasicDBObject();
				dbObjByPayId.put("PAY_ID", payId);
				dbObjOid.put("OID", oId);
				List<BasicDBObject> andQueryList = new ArrayList<BasicDBObject>();
				andQueryList.add(dbObjByPayId);
				andQueryList.add(dbObjOid);
				BasicDBObject allConditionQueryObj = new BasicDBObject("$and", andQueryList);
				
				MongoCursor<Document> cursor3 = coll.find(allConditionQueryObj).iterator();
				transactions = setTransactionList(cursor3);

				cursor2.close();

		}
			} catch (SQLException sQLException) {
			
				MDC.put(FieldType.INTERNAL_CUSTOM_MDC.getName(),
						Constants.CRM_LOG_PREFIX.getValue());
				logger.error("Unable to close connection", sQLException);
				throw new SystemException(ErrorType.DATABASE_ERROR,
						ErrorType.DATABASE_ERROR.getInternalMessage());
		}
		}
		return transactions;
	}

	private List<TransactionHistory> setTransactionList(MongoCursor<Document> rs) throws SQLException {
		List<TransactionHistory> transactions = new ArrayList<TransactionHistory>();
		while (rs.hasNext()) {
			Document docByOid = rs.next();
			TransactionHistory transaction = new TransactionHistory();

			transaction.setOrigTxnId(docByOid.getString(FieldType.ORIG_TXN_ID.getName()));
			transaction.setOrderId(docByOid.getString(FieldType.ORDER_ID.getName()));
			transaction.setTxnId(docByOid.getString(FieldType.TXN_ID.getName()));
			transaction.setCreateDate(docByOid.getString(CrmFieldConstants.CREATE_DATE.getValue()));
			transaction.setPayId(docByOid.getString(FieldType.PAY_ID.getName()));
			transaction.setCardNumber(docByOid.getString(FieldType.CARD_MASK.getName()));
			transaction.setMopType(docByOid.getString(FieldType.MOP_TYPE.getName()));
			transaction.setStatus(docByOid.getString(FieldType.STATUS.getName()));
			transaction.setTxnType(docByOid.getString(FieldType.TXNTYPE.getName()));
			transaction.setCustEmail(docByOid.getString(FieldType.CUST_EMAIL.getName()));
			transaction.setInternalCustIP(docByOid.getString(FieldType.INTERNAL_CUST_IP.getName()));
			transaction.setInternalCustCountryName(docByOid.getString(FieldType.INTERNAL_CUST_COUNTRY_NAME.getName()));
			transaction.setInternalCardIssusserBank(docByOid.getString(FieldType.INTERNAL_CARD_ISSUER_BANK.getName()));
			transaction.setInternalCardIssusserCountry(docByOid.getString(FieldType.INTERNAL_CARD_ISSUER_COUNTRY.getName()));

			transaction.setAcqId(docByOid.getString(FieldType.ACQ_ID.getName()));
			transaction.setCurrencyCode(docByOid.getString(FieldType.CURRENCY_CODE.getName()));
			transaction.setAmount(new BigDecimal(docByOid.getString(FieldType.AMOUNT.getName())));
			transaction.setAcquirerCode(docByOid.getString(FieldType.ACQUIRER_TYPE.getName()));
			transaction.setPaymentType(docByOid.getString(FieldType.PAYMENT_TYPE.getName()));

			transactions.add(transaction);
		}
		return transactions;
	}

	public String getOID(String txnId) throws SystemException {
		try (Connection connection = getConnection()) {
			try (PreparedStatement prepStament = connection.prepareStatement(getOIDQuery)) {
				prepStament.setString(1, txnId);
				try (ResultSet rs = prepStament.executeQuery()) {
					rs.next();
					return rs.getString(FieldType.OID.getName());
				}
			}

		} catch (SQLException sQLException) {
			MDC.put(FieldType.INTERNAL_CUSTOM_MDC.getName(), Constants.CRM_LOG_PREFIX.getValue());
			logger.error("Unable to close connection", sQLException);
			throw new SystemException(ErrorType.DATABASE_ERROR, ErrorType.DATABASE_ERROR.getInternalMessage());
		}
	}

	public String getTransactionAuthentication(String txnId) throws SystemException {
		String txnAuthentication = "";
		try (Connection connection = getConnection()) {
			try (PreparedStatement prepStament = connection.prepareStatement(txnAuthenticationQuery)) {
				prepStament.setString(1, getOID(txnId));
				try (ResultSet rs = prepStament.executeQuery()) {
					rs.next();
					txnAuthentication = rs.getString(FieldType.INTERNAL_TXN_AUTHENTICATION.getName());
				}
			}
		} catch (SQLException sQLException) {
			MDC.put(FieldType.INTERNAL_CUSTOM_MDC.getName(), Constants.CRM_LOG_PREFIX.getValue());
			logger.error("Unable to close connection", sQLException);
			throw new SystemException(ErrorType.DATABASE_ERROR, ErrorType.DATABASE_ERROR.getInternalMessage());
		}

		return txnAuthentication;
	}

	public String updateTransactionAuthentication(String internalTxnAuthentication, String txnId)
			throws SystemException {
		String txnAuthentication = "";
		try (Connection connection = getConnection()) {
			try (PreparedStatement prepStament = connection.prepareStatement(updateAuthenticationQuery)) {
				prepStament.setString(1, internalTxnAuthentication);
				prepStament.setString(2, getOID(txnId));
				prepStament.executeUpdate();
			}
		} catch (SQLException sQLException) {
			MDC.put(FieldType.INTERNAL_CUSTOM_MDC.getName(), Constants.CRM_LOG_PREFIX.getValue());
			logger.error("Unable to close connection", sQLException);
			throw new SystemException(ErrorType.DATABASE_ERROR, ErrorType.DATABASE_ERROR.getInternalMessage());
		}

		return txnAuthentication;
	}
}
