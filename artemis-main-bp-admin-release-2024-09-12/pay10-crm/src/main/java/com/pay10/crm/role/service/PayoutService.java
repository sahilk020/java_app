package com.pay10.crm.role.service;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.pay10.commons.audittrail.PayoutAuditTrailDTO;
import com.pay10.commons.audittrail.PayoutAuditTrailDao;
import com.pay10.commons.dto.CashDepositDTOPO;
import com.pay10.commons.dto.PassbookPODTO;
import com.pay10.commons.mongo.MerchantWalletPODao;
import com.pay10.commons.mongo.PassbookLedgerDao;
import com.pay10.commons.user.User;
import com.pay10.commons.util.Constants;
import com.pay10.crm.mongoReports.ApproverListPODao;
import com.pay10.crm.mongoReports.CashDepositPODao;

@Service("PayoutServicePO")
public class PayoutService {

	@Autowired
	private ApproverListPODao approverListPODao;

	@Autowired
	private PassbookLedgerDao passbookPODao;

	@Autowired
	private MerchantWalletPODao merchantWalletPODao;
	
	@Autowired
	private CashDepositPODao cashDepositPODao;
	
	@Autowired
	private PayoutAuditTrailDao payoutAuditTrailDao;

	private CashDepositDTOPO cashDepositDTOPO;
	
	public void updateCashDeposit(CashDepositDTOPO cashDepositDTOPO,HttpServletRequest request) {
		
		if (cashDepositDTOPO != null && cashDepositDTOPO.getPayId() != null && cashDepositDTOPO.getTxnId() != null) {
			
			cashDepositDTOPO.setUpdateDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis())));
			Gson gson=new Gson();
			PayoutAuditTrailDTO auditTrailDTO=new PayoutAuditTrailDTO();
			//For Audit Trail
			HttpSession session = request.getSession();
			User user = (User) session.getAttribute(Constants.USER.getValue());
			auditTrailDTO.setActionBy(user.getEmailId());
			auditTrailDTO.setActionDate(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date(System.currentTimeMillis())));
			auditTrailDTO.setActionName("Cash Deposit Approve");
			auditTrailDTO.setBaseJson(gson.toJson(cashDepositDTOPO));
			auditTrailDTO.setUpdatedDate("");
			auditTrailDTO.setUpdatedJson("");
			String txnId=payoutAuditTrailDao.saveAuditTrailPO(auditTrailDTO);
			auditTrailDTO.setAuditTxnId(txnId);
			
			//For Approver List
			approverListPODao.updateCashDeposit(cashDepositDTOPO);
			
			CashDepositDTOPO cdDB=cashDepositPODao.findByPayId(cashDepositDTOPO);
			//insert into passbook and wallet
			String status=savePassbookDetails(cdDB);
			System.out.println(status);
			String statusWallet=merchantWalletPODao.saveOrUpdate(cdDB);
			System.out.println(statusWallet);
			// on the basis of payid and txnid with approve status get entry from db then
			// update in passbook or wallet
			//For Updated Audit Trail
			PayoutAuditTrailDTO payoutAuditTrailDTO=payoutAuditTrailDao.fetchAuditrailByTxnId(txnId);
			
			payoutAuditTrailDTO.setUpdatedJson(new Gson().toJson(cdDB));
			payoutAuditTrailDTO.setUpdatedDate(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date(System.currentTimeMillis())));
			payoutAuditTrailDao.updateAuditTrailPO(payoutAuditTrailDTO);	
			
		}
	}

	public String savePassbookDetails(CashDepositDTOPO cashDepositDTOPO) {
		try {
			System.out.println("ANUMBHA:::: "+new Gson().toJson(cashDepositDTOPO));
			System.out.println(new Gson().toJson(new PassbookPODTO()) );
			passbookPODao.savePassbookDetails(cashDepositDTOPO);
			return "Success";
		} catch (Exception e) {
			e.printStackTrace();
			return "Fail";
		}

	}

}
