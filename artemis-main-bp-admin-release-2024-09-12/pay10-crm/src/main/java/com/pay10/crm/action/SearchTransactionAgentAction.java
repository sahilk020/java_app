package com.pay10.crm.action;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.user.TransactionSearch;
import com.pay10.commons.util.DataEncoder;
import com.pay10.crm.mongoReports.TxnReports;

/**
 * @author Amitosh
 *
 */
public class SearchTransactionAgentAction extends AbstractSecureAction {

	@Autowired
	private DataEncoder encoder;

	@Autowired
	private TxnReports txnReports;

	private static final long serialVersionUID = -7608146293799101830L;

	private static Logger logger = LoggerFactory.getLogger(SearchTransactionAgentAction.class.getName());

	private String orderId;
	private String pgRefNum;

	private List<TransactionSearch> aaData;

	@Override
	public String execute() {
		logger.info("Inside SearchTransactionAgentAction , execute()");
		try {
			aaData = encoder.encodeTransactionSearchObj(txnReports.searchPayment(getOrderId(), getPgRefNum()));
		} catch (Exception e) {
			logger.error("Error caught while fetching tranasctions " + e);
		}

		return SUCCESS;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getPgRefNum() {
		return pgRefNum;
	}

	public void setPgRefNum(String pgRefNum) {
		this.pgRefNum = pgRefNum;
	}

	public List<TransactionSearch> getAaData() {
		return aaData;
	}

	public void setAaData(List<TransactionSearch> aaData) {
		this.aaData = aaData;
	}

}
