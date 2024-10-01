package com.pay10.crm.chargeback;

import java.util.List;

import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.TransactionHistory;
import com.pay10.commons.util.MopType;
import com.pay10.commons.util.PaymentType;
import com.pay10.crm.action.AbstractSecureAction;
import com.pay10.crm.actionBeans.RefundDetailsProvider;

public class GenerateChargebackAction extends AbstractSecureAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3461692497780002314L;


	@Autowired
	private RefundDetailsProvider refundDetailsProvider;
	

	private static Logger logger = LoggerFactory.getLogger(GenerateChargebackAction.class.getName());
	private TransactionHistory transDetails = new TransactionHistory();
	
	private String txnId;
	private String orderId;
	private String payId;
	
	@Override
	public String execute() {
		
		try{
			getTransactionDetails();	
			return SUCCESS;
			}
			catch(Exception exception){
				logger.error("Exception", exception);
				return ERROR;
			}
	}
	private void getTransactionDetails() throws SystemException{
	//	RefundDetailsProvider refundDetailsProvider = new RefundDetailsProvider(orderId, payId, txnId);
	//	refundDetailsProvider.getAllTransactions();
	//	transDetails = refundDetailsProvider.getTransDetails();
		
		
		List<TransactionHistory> refundD = refundDetailsProvider.RefundProvider(orderId, payId, txnId);
		refundDetailsProvider.getAllTransactions();
		transDetails = refundDetailsProvider.getTransDetails();
	transDetails.setMopType(MopType.getmopName(transDetails.getMopType()));
		transDetails.setPaymentType(PaymentType.getpaymentName(transDetails.getPaymentType()));
		
		
		
	}
	
	public TransactionHistory getTransDetails() {
		return transDetails;
	}
	public void setTransDetails(TransactionHistory transDetails) {
		this.transDetails = transDetails;
	}
	public String getTxnId() {
		return txnId;
	}
	public void setTxnId(String txnId) {
		this.txnId = txnId;
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

}
