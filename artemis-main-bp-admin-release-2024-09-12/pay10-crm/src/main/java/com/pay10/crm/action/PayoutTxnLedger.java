package com.pay10.crm.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import com.pay10.commons.action.AbstractSecureAction;
import com.pay10.commons.dto.PayoutTxnLedgerDTO;
import com.pay10.commons.mongo.PayoutTxnLedgerDao;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;

public class PayoutTxnLedger extends AbstractSecureAction {
	private static final long serialVersionUID = -1806801325621922073L;

	@Autowired
	private UserDao userDao;

	private String merchant;
	private String dateRange;
	private String txnType;

	private Map<String, String> payIdList = new HashMap<>();

	private SimpleDateFormat sdNew = new SimpleDateFormat("yyyyMMdd");
	private SimpleDateFormat sdOld = new SimpleDateFormat("MM/dd/yyyy");
	@Autowired
	private PayoutTxnLedgerDao payoutTxnLedgerDao;
	private List<PayoutTxnLedgerDTO> aaData = new ArrayList<PayoutTxnLedgerDTO>();

	public String execute() {

		List<User> users = userDao.fetchUsersBasedOnPOEnable();

		users.stream().forEach(user -> {
			payIdList.put(user.getPayId(), user.getBusinessName());
		});

		return SUCCESS;
	}

	public String callReportApi() {
		System.out.println("Merchant Id : " + merchant);
		System.out.println("dateRange : " + dateRange);
		System.out.println("TxnType : " + txnType);

		if (dateRange != null) {

			String from = dateRange.split("-")[0].trim();
			String to = dateRange.split("-")[1].trim();

			try {
				System.out.println(sdNew.format(sdOld.parse(from)));
				System.out.println(sdNew.format(sdOld.parse(to)));
				from = sdNew.format(sdOld.parse(from));
				to = sdNew.format(sdOld.parse(to));
				System.out.println(
						"Merchant PayId : " + merchant + "From : " + from + "To : " + to + "TxnType : " + txnType);
				aaData = payoutTxnLedgerDao.fetchTransactionPOReport(merchant, from, to, txnType);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return SUCCESS;
	}

	public String getTxnType() {
		return txnType;
	}

	public void setTxnType(String txnType) {
		this.txnType = txnType;
	}

	public String getMerchant() {
		return merchant;
	}

	public void setMerchant(String merchant) {
		this.merchant = merchant;
	}

	public String getDateRange() {
		return dateRange;
	}

	public void setDateRange(String dateRange) {
		this.dateRange = dateRange;
	}

	public Map<String, String> getPayIdList() {
		return payIdList;
	}

	public void setPayIdList(Map<String, String> payIdList) {
		this.payIdList = payIdList;
	}

	public List<PayoutTxnLedgerDTO> getAaData() {
		return aaData;
	}

	public void setAaData(List<PayoutTxnLedgerDTO> aaData) {
		this.aaData = aaData;
	}

}
