package com.pay10.crm.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.RequestAware;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;
import com.pay10.commons.action.AbstractSecureAction;
import com.pay10.commons.dto.CashDepositDTOPO;
import com.pay10.commons.user.MultCurrencyCodeDao;
import com.pay10.commons.user.UserDao;
import com.pay10.crm.mongoReports.ApproverListPODao;
import com.pay10.crm.mongoReports.CashDepositPODao;
import com.pay10.crm.role.service.PayoutService;

public class ApproverListPO extends AbstractSecureAction {
	private static final long serialVersionUID = -1806801325621922073L;

	@Autowired
	private UserDao userDao;

	@Autowired
	private PayoutService payoutService;

	@Autowired
	private CashDepositPODao cashDepositPODao;
	@Autowired
	private MultCurrencyCodeDao multCurrencyCodeDao;

	private List<CashDepositDTOPO> aaData = new ArrayList<CashDepositDTOPO>();
	private CashDepositDTOPO cashDepositDTOPO = new CashDepositDTOPO();
	private SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	HttpServletRequest request = ServletActionContext.getRequest();

	public String execute() {
		if(cashDepositDTOPO.getPayId()!=null &&cashDepositDTOPO.getTxnId()!=null) {
			cashDepositDTOPO.setCurrency(cashDepositDTOPO.getCurrency());
			cashDepositDTOPO.setUpdateDate(sd.format(new Date(System.currentTimeMillis())));
			System.out.println("Data : " + new Gson().toJson(cashDepositDTOPO) );
			payoutService.updateCashDeposit(cashDepositDTOPO, request);
		}
		return SUCCESS;
	}

	private String approverRemark;
	private String rejectRemark;
	private String remark;

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getRejectRemark() {
		return rejectRemark;
	}

	public void setRejectRemark(String rejectRemark) {
		this.rejectRemark = rejectRemark;
	}

	public String getApproverRemark() {
		return approverRemark;
	}

	public void setApproverRemark(String approverRemark) {
		this.approverRemark = approverRemark;
	}

	public List<CashDepositDTOPO> getAaData() {
		return aaData;
	}

	public void setAaData(List<CashDepositDTOPO> aaData) {
		this.aaData = aaData;
	}

	public CashDepositDTOPO getCashDepositDTOPO() {
		return cashDepositDTOPO;
	}

	public void setCashDepositDTOPO(CashDepositDTOPO cashDepositDTOPO) {
		this.cashDepositDTOPO = cashDepositDTOPO;
	}



}
