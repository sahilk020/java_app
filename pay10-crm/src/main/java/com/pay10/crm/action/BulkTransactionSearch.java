package com.pay10.crm.action;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.user.TransactionSearchNew;
import com.pay10.crm.mongoReports.TxnReports;

/**
 * @author abhi
 *
 */
public class BulkTransactionSearch extends AbstractSecureAction {
	List<TransactionSearchNew> Data = new ArrayList<TransactionSearchNew>();

	@Autowired
	TxnReports txnReports;
	private static Logger logger = LoggerFactory.getLogger(MerchantNameAction.class.getName());

	String rrn;
	String pgRefNum;
	String orderId;

	@Override
	public String execute() {

		return INPUT;
	}

	public String searchTransactionBulk() {
		logger.info(rrn + pgRefNum + orderId);

		Data = txnReports.getBulkReport(rrn, pgRefNum, orderId);

		return SUCCESS;

	}

	public String getRrn() {
		return rrn;
	}

	public void setRrn(String rrn) {
		this.rrn = rrn;
	}

	public String getPgRefNum() {
		return pgRefNum;
	}

	public void setPgRefNum(String pgRefNum) {
		this.pgRefNum = pgRefNum;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public List<TransactionSearchNew> getData() {
		return Data;
	}

	public void setData(List<TransactionSearchNew> data) {
		Data = data;
	}

}
