package com.pay10.crm.action;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.api.TransactionControllerServiceProvider;
import com.pay10.commons.dao.NodalTxnReports;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.Merchants;
import com.pay10.commons.user.NodalTransactions;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.AccountType;
import com.pay10.commons.util.BeneficiaryTypes;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.DateCreater;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.NodalPaymentTypes;
import com.pay10.commons.util.NodalStatusType;
import com.pay10.commons.util.SettlementTransactionType;
import com.pay10.commons.util.StatusType;

/**
 * 
 * @author shubhamchauhan
 *
 */
public class NodalTransactionHistoryAction extends AbstractSecureAction {

	private static final long serialVersionUID = 4077683475572563293L;

	@Autowired
	private TransactionControllerServiceProvider transactionControllerServiceProvider;
	
	@Autowired
	NodalTxnReports nodalTxnReports;

	private static Logger logger = LoggerFactory.getLogger(NodalTransactionHistoryAction.class.getName());

	private String acquirer;
	private int draw;
	private int length;
	private int start;
	private BigInteger recordsTotal;
	public BigInteger recordsFiltered;
	private String response;
	private List<NodalTransactions> aaData;
	private List<NodalTransactions> nodalTransactionsList = new ArrayList<NodalTransactions>();
	private User sessionUser = new User();
	private List<Merchants> listMerchant = new ArrayList<>();
	private String beneficiaryCd;
	private String srcAccountNo;
	private String beneAccountNo;
	private String paymentType;
	private String currencyCode;
	private String custId;
	private String comments;
	private String amount;
	private String dateFrom;
	private String txnId;
	private String status;
	private String oid;
	private String dateTo;
	private String txnType;
	private String orderId;
	private String payId;
	private String beneType;

	@Autowired
	private NodalTxnReports txnReports;

	@Autowired
	private CrmValidator validator;

	@Autowired
	private UserDao userDao;
	
	@Override
	@SuppressWarnings("unchecked")
	public String execute() {
		logger.info("Inside TransactionSearchAction , execute()");
		int totalCount;
		sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		setDateFrom(DateCreater.toDateTimeformatCreater(dateFrom));
		setDateTo(DateCreater.formDateTimeformatCreater(dateTo));
		try {
			totalCount = txnReports.searchPaymentCount(payId, getTxnId(), getOrderId(), getStatus(), getPaymentType(),
					getDateFrom(), getDateTo(), getBeneType());
			BigInteger bigInt = BigInteger.valueOf(totalCount);
			setRecordsTotal(bigInt);
			if (getLength() == -1) {
				setLength(getRecordsTotal().intValue());
			}
			nodalTransactionsList = txnReports.searchPayment(payId, getTxnId(), getOrderId(), getStatus(), getPaymentType(),
					getDateFrom(), getDateTo(), getStart(), getLength(), getBeneType());
			setAaData(nodalTransactionsList);
			recordsFiltered = recordsTotal;

		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}
		return SUCCESS;
	}

	@SuppressWarnings("unchecked")
	public String loadData() {
		//setListMerchant(userDao.getMerchantList());
		sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		setListMerchant(userDao.getMerchantList(sessionMap.get(Constants.SEGMENT.getValue()).toString(), sessionUser.getRole().getId()));
		return SUCCESS;

	}

	public String refreshTransactionStatus() {

		try {
			if(StringUtils.isBlank(getOrderId())) {
				setResponse("Invalid ORDER ID");
				logger.info("Invalid Order Id : " + getOrderId());
				return SUCCESS;
			}
			
			Document nodalTransaction = nodalTxnReports.getTransactionByOrderId(getOrderId());
			
			if(nodalTransaction == null) {
				setResponse("Transaction not found.");
				logger.info("Transaction not found for Order Id : " + getOrderId());
				return SUCCESS;
			}
			
//			if(StringUtils.isNotBlank(nodalTransaction.getString(FieldType.STATUS.getName())) && nodalTransaction.getString(FieldType.STATUS.getName()).trim().equalsIgnoreCase(StatusType.SETTLED.getName())) {
//				setResponse("Transaction already settled");
//				logger.info("Transaction already settled for Order Id : " + getOrderId());
//				return SUCCESS;
//			}
			
			Fields fields = new Fields();
			fields.put(FieldType.CUSTOMER_ID.getName(), getCustId());
			fields.put(FieldType.OID.getName(), getOid());
			fields.put(FieldType.ORDER_ID.getName(), getOrderId());
			fields.put(FieldType.NODAL_ACQUIRER.getName(), getAcquirer());
			fields.put(FieldType.TXNTYPE.getName(), SettlementTransactionType.STATUS.getName());
			fields.put(FieldType.TXN_ID.getName(), nodalTransaction.getString(FieldType.TXN_ID.getName()));

			Map<String, String> responseMap = transactionControllerServiceProvider.nodalSettlementTransact(fields);
			logger.info("Nodal Txn Status enquiry response from PGWS : " + responseMap.toString());

			String responseMessage = "";
			String responseCode = "";

			if (StringUtils.isNotBlank(responseMap.get(FieldType.PG_TXN_MESSAGE.getName()))) {
				responseMessage = responseMap.get(FieldType.PG_TXN_MESSAGE.getName());
			} else {
				responseMessage = "REJECTED";
			}

			if (StringUtils.isNotBlank(responseMap.get(FieldType.PG_RESP_CODE.getName()))) {
				responseCode = responseMap.get(FieldType.PG_RESP_CODE.getName());
			} else {
				responseCode = "999";
			}
			logger.info("Inside refreshTransactionStatus , response message = " + responseMessage);
			logger.info("Inside refreshTransactionStatus , response code  = " + responseCode);

			if (responseCode.equalsIgnoreCase(ErrorType.SUCCESS.getCode())) {
				setResponse("Enquiry Status : Successful");
			} else {
				setResponse("Enquiry Status : " + responseMessage);
			}
			return SUCCESS;
		} catch (Exception e) {
			logger.error("Exception = " + e);
		}
		setResponse("Failed to do status enquiry");
		return SUCCESS;
	}

	@Override
	public void validate() {

		if ((validator.validateBlankField(getOid()))) {
		} else if (!(validator.validateField(CrmFieldType.OID, getOid())) && !oid.equalsIgnoreCase("ALL")) {
			addFieldError(CrmFieldType.OID.getName(), validator.getResonseObject().getResponseMessage());
		}
		
		if ((validator.validateBlankField(getTxnId()))) {
		} else if (!(validator.validateField(CrmFieldType.TXN_ID, getTxnId())) && !txnId.equalsIgnoreCase("ALL")) {
			addFieldError(CrmFieldType.TXN_ID.getName(), validator.getResonseObject().getResponseMessage());
		}
		
		if (validator.validateBlankFields(getStatus())) {
		} else if ((NodalStatusType.getInstanceFromName(status) == null) && !status.equalsIgnoreCase("ALL")) {
			addFieldError(CrmFieldType.BENE_STATUS.getName(), validator.getResonseObject().getResponseMessage());
		}
		
		if (validator.validateBlankFields(getPaymentType())) {
		} else if ((NodalPaymentTypes.getInstancefromName(getPaymentType()) == null)
				&& !paymentType.equalsIgnoreCase("ALL")) {
			addFieldError(CrmFieldType.PAYMENT_TYPE.getName(), validator.getResonseObject().getResponseMessage());
		}
	}

	public List<NodalTransactions> getAaData() {
		return aaData;
	}

	public void setAaData(List<NodalTransactions> aaData) {
		this.aaData = aaData;
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

	public String getAcquirer() {
		return acquirer;
	}

	public void setAcquirer(String acquirer) {
		this.acquirer = acquirer;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public String getBeneficiaryCd() {
		return beneficiaryCd;
	}

	public void setBeneficiaryCd(String beneficiaryCd) {
		this.beneficiaryCd = beneficiaryCd;
	}

	public String getSrcAccountNo() {
		return srcAccountNo;
	}

	public void setSrcAccountNo(String srcAccountNo) {
		this.srcAccountNo = srcAccountNo;
	}

	public String getBeneAccountNo() {
		return beneAccountNo;
	}

	public void setBeneAccountNo(String beneAccountNo) {
		this.beneAccountNo = beneAccountNo;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public String getBeneType() {
		return beneType;
	}

	public void setBeneType(String beneType) {
		this.beneType = beneType;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public String getCustId() {
		return custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public List<NodalTransactions> getNodalTransactionsList() {
		return nodalTransactionsList;
	}

	public void setNodalTransactionsList(List<NodalTransactions> nodalTransactionsList) {
		this.nodalTransactionsList = nodalTransactionsList;
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

	public String getTxnId() {
		return txnId;
	}

	public void setTxnId(String txnId) {
		this.txnId = txnId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTxnType() {
		return txnType;
	}

	public void setTxnType(String txnType) {
		this.txnType = txnType;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getPayId() {
		return payId;
	}

	public void setPayId(String payId) {
		this.payId = payId;
	}

	public List<Merchants> getListMerchant() {
		return listMerchant;
	}

	public void setListMerchant(List<Merchants> listMerchant) {
		this.listMerchant = listMerchant;
	}

}
