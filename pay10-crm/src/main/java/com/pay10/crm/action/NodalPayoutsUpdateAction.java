package com.pay10.crm.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.user.Merchants;
import com.pay10.commons.user.NodalAmount;
import com.pay10.commons.user.NodalAmoutDao;
import com.pay10.commons.user.ResponseObject;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.DateCreater;
import com.pay10.crm.actionBeans.CreateNodalAccount;
import com.pay10.crm.actionBeans.NodalPayoutUpdateService;

public class NodalPayoutsUpdateAction extends AbstractSecureAction {

	private static final long serialVersionUID = -6998673672249328456L;
	private String merchant;
	private String acquirer;
	private String settlementDate;
	private String nodalCreditDate;
	private String captureDate;
	private String response;
	private String fromCaptureDate;
	private String toCaptureDate;
	private String fromDate;
	private String toDate;
	private String paymentMethod;
	private String nodalType;
	private String nodalAmount;
	private ResponseObject responseObject;
	private List<Merchants> merchantList = new ArrayList<Merchants>();
	private User sessionUser = new User();
	private NodalAmount nodal =new NodalAmount();
	List<NodalAmount> aaData=new ArrayList<NodalAmount>();
	private static Logger logger = LoggerFactory.getLogger(NodalPayoutsUpdateAction.class.getName());
	public static final BigDecimal ONE_HUNDRED = new BigDecimal(100);

	@Autowired
	private NodalPayoutUpdateService nodalPayoutUpdateService;
	
	@Autowired
	private UserDao userDao;

	@Autowired 
	private CreateNodalAccount createNodalAmount;
	
	@Autowired
	NodalAmoutDao nodalDao;
	
	@SuppressWarnings("unchecked")
	@Override
	public String execute() {
		logger.info("Inside NodalPayoutsUpdateAction , execute");
		sessionUser = (User) sessionMap.get(Constants.USER.getValue());
        String paymentType=null;
        BigDecimal amount=new BigDecimal(nodalAmount);
		try
		{
		setNodalCreditDate(DateCreater.toDateTimeformatCreater(nodalCreditDate));	
		
        try {
        NodalAmount existNodal = nodalDao.existNodalData(acquirer, paymentMethod, nodalCreditDate,captureDate,settlementDate);
		if (existNodal != null) {
			String updateCaptureDate=DateCreater.toDateTimeformatCreater(captureDate);
			String updateSettlementDate=DateCreater.toDateTimeformatCreater(settlementDate);
		
			if (existNodal.getAcquirer().equals(acquirer) && existNodal.getPaymentMethod().equals(paymentMethod)
					&& existNodal.getReconDate().equals(nodalCreditDate)&& existNodal.getCaptureDate().equals(updateCaptureDate)&&existNodal.getSettlementDate().equals(updateSettlementDate)) {
				addActionMessage("Nodal Amount is Already Exist");
				return SUCCESS;
			}
		}

		else {
			
			responseObject = createNodalAmount.createNodalAmount(getNodalInstance());
			String payId = userDao.getPayIdByEmailId(merchant);
			fromCaptureDate = captureDate + " 00:00:00";
			toCaptureDate = captureDate + " 23:59:59";
			//setMerchantList(userDao.getMerchantActiveList());
			setMerchantList(userDao.getMerchantActiveList(sessionMap.get(Constants.SEGMENT.getValue()).toString(), sessionUser.getRole().getId()));
			fromCaptureDate = DateCreater.toDateTimeformatCreater(captureDate);
			toCaptureDate = DateCreater.formDateTimeformatCreater(captureDate);
			
			fromDate = settlementDate + " 00:00:00";
			toDate = settlementDate + " 23:59:59";

			fromDate = DateCreater.toDateTimeformatCreater(settlementDate);
			toDate = DateCreater.formDateTimeformatCreater(settlementDate);
			
			
			sessionUser = (User) sessionMap.get(Constants.USER.getValue());
			/*
			 * setResponse(nodalPayoutUpdateService.updateNodalTransactions(payId, acquirer,
			 * nodalSettlementDate, nodalType,paymentMethod, fromDate, toDate,
			 * sessionUser));
			 */
			addActionMessage(nodalPayoutUpdateService.updateNodalTransactions(payId, acquirer, nodalCreditDate,
					nodalType, paymentMethod,fromCaptureDate,toCaptureDate, fromDate, toDate, sessionUser));

			return SUCCESS;
		}
	}

	catch(Exception ex)
	{

			responseObject = createNodalAmount.createNodalAmount(getNodalInstance());
			String payId = userDao.getPayIdByEmailId(merchant);
			fromCaptureDate = captureDate + " 00:00:00";
			toCaptureDate = captureDate + " 23:59:59";
			//setMerchantList(userDao.getMerchantActiveList());
			setMerchantList(userDao.getMerchantActiveList(sessionMap.get(Constants.SEGMENT.getValue()).toString(), sessionUser.getRole().getId()));
			fromCaptureDate = DateCreater.toDateTimeformatCreater(captureDate);
			toCaptureDate = DateCreater.formDateTimeformatCreater(captureDate);
			sessionUser = (User) sessionMap.get(Constants.USER.getValue());
			
			fromDate = settlementDate + " 00:00:00";
			toDate = settlementDate + " 23:59:59";
	
			fromDate = DateCreater.toDateTimeformatCreater(settlementDate);
			toDate = DateCreater.formDateTimeformatCreater(settlementDate);
			
			/*
			 * setResponse(nodalPayoutUpdateService.updateNodalTransactions(payId, acquirer,
			 * nodalSettlementDate, nodalType,paymentMethod, fromDate, toDate,
			 * sessionUser));
			 */
			addActionMessage(nodalPayoutUpdateService.updateNodalTransactions(payId, acquirer, nodalCreditDate,
					nodalType, paymentMethod, fromCaptureDate,toCaptureDate, fromDate, toDate, sessionUser));
		 	addActionMessage(CrmFieldConstants.NODAL_AMOUNT_SUCCESSFULLY.getValue());
			return SUCCESS;
		}

	 
		}
		catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}
		return SUCCESS;
	}
        
	
	
	
	private NodalAmount getNodalInstance()
	{
		String nodalCaptureDate=DateCreater.toDateTimeformatCreater(captureDate);
		String nodalSettlementDate=DateCreater.toDateTimeformatCreater(settlementDate);
		String payId = userDao.getPayIdByEmailId(merchant);
		BigDecimal amount=new BigDecimal(nodalAmount);
		nodal.setAcquirer(acquirer);
		nodal.setPaymentType(paymentMethod);
		nodal.setNodalCreditAmount(amount);
		nodal.setReconDate(nodalCreditDate);
		nodal.setCaptureDate(nodalCaptureDate);
		nodal.setSettlementDate(nodalSettlementDate);
		nodal.setMerchant(payId);
		nodal.setPaymentMethod(paymentMethod);		
		return nodal;
	}
	
	
	
	public String getMerchant() {
		return merchant;
	}

	public void setMerchant(String merchant) {
		this.merchant = merchant;
	}

	public String getAcquirer() {
		return acquirer;
	}

	public void setAcquirer(String acquirer) {
		this.acquirer = acquirer;
	}

	public String getSettlementDate() {
		return settlementDate;
	}

	public void setSettlementDate(String settlementDate) {
		this.settlementDate = settlementDate;
	}

	

	public String getNodalCreditDate() {
		return nodalCreditDate;
	}




	public void setNodalCreditDate(String nodalCreditDate) {
		this.nodalCreditDate = nodalCreditDate;
	}


	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public String getFromDate() {
		return fromDate;
	}

	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}

	public String getToDate() {
		return toDate;
	}

	public void setToDate(String toDate) {
		this.toDate = toDate;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public String getNodalType() {
		return nodalType;
	}

	public void setNodalType(String nodalType) {
		this.nodalType = nodalType;
	}

	public List<Merchants> getMerchantList() {
		return merchantList;
	}

	public void setMerchantList(List<Merchants> merchantList) {
		this.merchantList = merchantList;
	}




	public List<NodalAmount> getAaData() {
		return aaData;
	}




	public void setAaData(List<NodalAmount> aaData) {
		this.aaData = aaData;
	}




	public CreateNodalAccount getCreateNodalAmount() {
		return createNodalAmount;
	}




	public void setCreateNodalAmount(CreateNodalAccount createNodalAmount) {
		this.createNodalAmount = createNodalAmount;
	}




	public ResponseObject getResponseObject() {
		return responseObject;
	}




	public void setResponseObject(ResponseObject responseObject) {
		this.responseObject = responseObject;
	}




	public String getCaptureDate() {
		return captureDate;
	}




	public void setCaptureDate(String captureDate) {
		this.captureDate = captureDate;
	}



	public NodalAmoutDao getNodalDao() {
		return nodalDao;
	}




	public void setNodalDao(NodalAmoutDao nodalDao) {
		this.nodalDao = nodalDao;
	}




	public String getNodalAmount() {
		return nodalAmount;
	}




	public void setNodalAmount(String nodalAmount) {
		this.nodalAmount = nodalAmount;
	}

	
	

}
