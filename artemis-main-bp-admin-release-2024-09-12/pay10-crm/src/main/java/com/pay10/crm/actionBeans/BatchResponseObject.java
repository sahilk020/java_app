package com.pay10.crm.actionBeans;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.pay10.commons.user.BatchTransactionObj;
import com.pay10.commons.user.BeneficiaryAccounts;
import com.pay10.commons.user.Invoice;
import com.pay10.commons.user.NodalTransactions;
import com.pay10.commons.user.SettledTransactionDataObject;
import com.pay10.commons.util.BinRange;
import com.pay10.commons.util.FraudPreventionObj;
import com.pay10.crm.action.StatusEnquiryParameters;

public class BatchResponseObject {
	private String responseMessage;
	private List<BatchTransactionObj> batchTransactionList = new LinkedList<BatchTransactionObj>();
	private List<StatusEnquiryParameters> statusEnquiryParamsList = new LinkedList<StatusEnquiryParameters>();
	private List<BinRange> binRangeResponseList = new LinkedList<BinRange>();
	private List<Invoice>  invoiceEmailList = new LinkedList<Invoice>();
	private List<FraudPreventionObj>  fraudPreventionObjList = new LinkedList<FraudPreventionObj>();
	private List<SettledTransactionDataObject>  transactionList = new LinkedList<SettledTransactionDataObject>();
	private List<BeneficiaryAccounts> beneficiaryAccountsList = new ArrayList<>();
	private List<NodalTransactions> nodalTransactionsList = new ArrayList<>();
	
	public List<BatchTransactionObj> getBatchTransactionList() {
		return batchTransactionList;
	}
	public void setBatchTransactionList(List<BatchTransactionObj> batchTransactionList) {
		this.batchTransactionList = batchTransactionList;
	}
	public String getResponseMessage() {
		return responseMessage;
	}
	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}
	public List<StatusEnquiryParameters> getStatusEnquiryParamsList() {
		return statusEnquiryParamsList;
	}
	public void setStatusEnquiryParamsList(List<StatusEnquiryParameters> statusEnquiryParamsList) {
		this.statusEnquiryParamsList = statusEnquiryParamsList;
	}
	
	public List<FraudPreventionObj> getFraudPreventionObjList() {
		return fraudPreventionObjList;
	}
	public void setFraudPreventionObjList(List<FraudPreventionObj> fraudPreventionObjList) {
		this.fraudPreventionObjList = fraudPreventionObjList;
	}
	
	public List<Invoice> getInvoiceEmailList() {
		return invoiceEmailList;
	}
	public void setInvoiceEmailList(List<Invoice> invoiceEmailList) {
		this.invoiceEmailList = invoiceEmailList;
	}
	
	public List<BinRange> getBinRangeResponseList() {
		return binRangeResponseList;
	}
	public void setBinRangeResponseList(List<BinRange> binRangeResponseList) {
		this.binRangeResponseList = binRangeResponseList;
	}
	public List<SettledTransactionDataObject> getTransactionList() {
		return transactionList;
	}
	public void setTransactionList(List<SettledTransactionDataObject> transactionList) {
		this.transactionList = transactionList;
	}
	public List<BeneficiaryAccounts> getBeneficiaryAccountsList() {
		return beneficiaryAccountsList;
	}
	public void setBeneficiaryAccountsList(List<BeneficiaryAccounts> beneficiaryAccountsList) {
		this.beneficiaryAccountsList = beneficiaryAccountsList;
	}
	public List<NodalTransactions> getNodalTransactionsList() {
		return nodalTransactionsList;
	}
	public void setNodalTransactionsList(List<NodalTransactions> nodalTransactionsList) {
		this.nodalTransactionsList = nodalTransactionsList;
	}
	
}
