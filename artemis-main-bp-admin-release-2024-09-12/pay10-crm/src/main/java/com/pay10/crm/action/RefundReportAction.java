package com.pay10.crm.action;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.pay10.commons.action.AbstractSecureAction;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.user.TransactionSearch;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.DateCreater;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;

public class RefundReportAction extends AbstractSecureAction {

	private static final long serialVersionUID = -5470936951624811783L;

	private String dateFrom;
	private String dateTo;
	public String paymentMethods;
	public String aquirer;
	private String merchantEmailId;
	private String currency;
	private String pgRefNum;
	private int draw;
	private int length;
	private int start;
	private BigInteger recordsTotal;
	public BigInteger recordsFiltered;
	private List<TransactionSearch> aaData = new ArrayList<TransactionSearch>();
	private static Logger logger = LoggerFactory.getLogger(RefundReportAction.class.getName());

	@Autowired
	private MongoInstance mongoInstance;
	@Autowired
	PropertiesManager propertiesManager;
	
	@Autowired
	private TransactionRefundReportQuery transactionRefundReportQuery;

	@Autowired
	private UserDao userDao;

	private static final String prefix = "MONGO_DB_";
	
	@Override
	public String execute() {
		int totalCount = 0;
		try {
			User sessionUser = (User) sessionMap.get(Constants.USER.getValue());
			List<TransactionSearch> transactionRefundList = new ArrayList<TransactionSearch>();
			List<TransactionSearch> finalTransactionRefundList = new ArrayList<TransactionSearch>();
			List<TransactionSearch> transactionRefundPaginationList = new ArrayList<TransactionSearch>();
			List<TransactionSearch> transactionRefundList1 = new ArrayList<TransactionSearch>();
			if (aquirer.isEmpty()) {
				aquirer = "ALL";
			}
			setDateFrom(DateCreater.toDateTimeformatCreater(dateFrom));
			setDateTo(DateCreater.formDateTimeformatCreater(dateTo));
			String merchantPayId = null;
			if (sessionUser.getUserType().equals(UserType.SUPERADMIN)
					|| sessionUser.getUserType().equals(UserType.ADMIN)
					|| sessionUser.getUserType().equals(UserType.SUBADMIN)
					|| sessionUser.getUserType().equals(UserType.ASSOCIATE)) {

				if (!merchantEmailId.equalsIgnoreCase("All")) {
					User merchant = userDao.findPayIdByEmail(merchantEmailId);
					merchantPayId = merchant.getPayId();
				} else {
					merchantPayId = merchantEmailId;
				}

				transactionRefundList = transactionRefundReportQuery.refundReport(dateFrom, dateTo, merchantPayId,
						paymentMethods, aquirer, currency, sessionUser, getStart(), getLength());
				
				for (TransactionSearch refundList : transactionRefundList) {
					MongoDatabase dbIns = mongoInstance.getDB();  
					MongoCollection<Document> coll = dbIns.getCollection(PropertiesManager.propertiesMap.get(prefix+Constants.COLLECTION_NAME.getValue()));
					
					List<BasicDBObject> refundConditionList = new ArrayList<BasicDBObject>();
					refundConditionList.add(new BasicDBObject(FieldType.TXNTYPE.getName(),  TransactionType.SALE.getName()));
					// Done By chetan nagaria for change in settlement process to mark transaction as RNS
//					refundConditionList.add(new BasicDBObject(FieldType.STATUS.getName(),  StatusType.SETTLED.getName()));
					refundConditionList.add(new BasicDBObject(FieldType.STATUS.getName(),  StatusType.SETTLED_SETTLE.getName()));
					refundConditionList.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(),refundList.getOrigTxnId()));
					
					BasicDBObject finalquery = new BasicDBObject("$and", refundConditionList);
					MongoCursor<Document> cursor = coll.find(finalquery).iterator();
					while (cursor.hasNext()) {
						Document doc = cursor.next();
						refundList.setOrigAmount(doc.getString(FieldType.AMOUNT.toString()));
						refundList.setOrigTxnDate(doc.getString(FieldType.CREATE_DATE.toString()));
						refundList.setOrigTxnId(doc.getString(FieldType.TXN_ID.toString()));
										}
					finalTransactionRefundList.add(refundList);
				}
				
				totalCount = transactionRefundReportQuery.refundReportCount(dateFrom, dateTo, merchantPayId,
						paymentMethods, aquirer, currency, sessionUser);

				BigInteger bigInt = BigInteger.valueOf(totalCount);
				setRecordsTotal(bigInt);
				if (getLength() == -1) {
					setLength(getRecordsTotal().intValue());
				}
				setAaData(finalTransactionRefundList);
				recordsFiltered = getRecordsTotal();

				// transactionList1.addAll(findDetails(transactionPaginationList));

			} else if (sessionUser.getUserType().equals(UserType.MERCHANT)
					|| sessionUser.getUserType().equals(UserType.SUBUSER)) {

				if (!merchantEmailId.equalsIgnoreCase("All")) {

					User merchant = userDao.findPayIdByEmail(merchantEmailId);
					merchantPayId = merchant.getPayId();

				}
			}
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		return SUCCESS;
	}

	public String getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(String dateFrom) {
		this.dateFrom = dateFrom;
	}

	public String getDateTo() {
		return dateTo;
	}

	public void setDateTo(String dateTo) {
		this.dateTo = dateTo;
	}

	public String getPaymentMethods() {
		return paymentMethods;
	}

	public void setPaymentMethods(String paymentMethods) {
		this.paymentMethods = paymentMethods;
	}

	public String getAquirer() {
		return aquirer;
	}

	public void setAquirer(String aquirer) {
		this.aquirer = aquirer;
	}

	public String getMerchantEmailId() {
		return merchantEmailId;
	}

	public void setMerchantEmailId(String merchantEmailId) {
		this.merchantEmailId = merchantEmailId;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public int getDraw() {
		return draw;
	}

	public void setDraw(int draw) {
		this.draw = draw;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public BigInteger getRecordsTotal() {
		return recordsTotal;
	}

	public void setRecordsTotal(BigInteger recordsTotal) {
		this.recordsTotal = recordsTotal;
	}

	public BigInteger getRecordsFiltered() {
		return recordsFiltered;
	}

	public void setRecordsFiltered(BigInteger recordsFiltered) {
		this.recordsFiltered = recordsFiltered;
	}

	public List<TransactionSearch> getAaData() {
		return aaData;
	}

	public void setAaData(List<TransactionSearch> aaData) {
		this.aaData = aaData;
	}

	public String getPgRefNum() {
		return pgRefNum;
	}

	public void setPgRefNum(String pgRefNum) {
		this.pgRefNum = pgRefNum;
	}

}
