package com.pay10.crm.action;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.TransactionManager;

public class BeneficiaryMerchantAccDetailsFromPayid  extends AbstractSecureAction  {

	/**
	 * rohit
	 */
	private static final long serialVersionUID = -1285862489555225830L;
	
	@Autowired
	private UserDao userDao;
	private static Logger logger = LoggerFactory.getLogger(BeneficiaryMerchantAccDetailsFromPayid.class.getName());
	private String payId;
	private String accountNo;
	private String beneficiaryCd;
	private String beneficiaryName;
	private String ifsc;
	private String bankName;
	private String mobileNo;
	private String emailId;
	private String address1;
	
	@Override
	public String execute() {
		if(!StringUtils.isEmpty(getPayId()) || getPayId() != null ) {
			try {
				User userObj = userDao.findPayId(payId);
				String beneCd = TransactionManager.getNewTransactionId();
				
				setAccountNo(userObj.getAccountNo());;
				setBeneficiaryCd("BY_"+beneCd);
				setBeneficiaryName(userObj.getAccHolderName());
				setIfsc(userObj.getIfscCode());
				setBankName(userObj.getBankName());
				setMobileNo(userObj.getTransactionMobileNo());
				setEmailId(userObj.getEmailId());
				setAddress1(userObj.getAddress()) ;
				
				
			}catch (Exception e) {
				logger.error("Exception : " + e.getMessage());
				return SUCCESS;
			}
			
		}
		return SUCCESS; 
	}

	public String getPayId() {
		return payId;
	}

	public void setPayId(String payId) {
		this.payId = payId;
	}

	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public String getBeneficiaryCd() {
		return beneficiaryCd;
	}

	public void setBeneficiaryCd(String beneficiaryCd) {
		this.beneficiaryCd = beneficiaryCd;
	}

	public String getBeneficiaryName() {
		return beneficiaryName;
	}

	public void setBeneficiaryName(String beneficiaryName) {
		this.beneficiaryName = beneficiaryName;
	}

	public String getIfsc() {
		return ifsc;
	}

	public void setIfsc(String ifsc) {
		this.ifsc = ifsc;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}
}
