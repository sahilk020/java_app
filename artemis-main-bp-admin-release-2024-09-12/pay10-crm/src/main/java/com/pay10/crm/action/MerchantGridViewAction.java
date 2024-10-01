package com.pay10.crm.action;

import java.util.List;

import com.pay10.commons.exception.SystemException;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.dao.MerchantGridViewService;
import com.pay10.commons.user.MerchantDetails;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.DataEncoder;

public class MerchantGridViewAction extends AbstractSecureAction {
	
	@Autowired
	private MerchantGridViewService merchantGridViewService;
	
	@Autowired
	private DataEncoder encoder;
	@Autowired
	private CrmValidator validator;

	private static Logger logger = LoggerFactory.getLogger(MerchantGridViewAction.class.getName());
	private static final long serialVersionUID = 3293888841176590776L;
	private List<MerchantDetails> aaData;
	private User sessionUser = new User();
	private String businessType;
	@Override
	public String execute() {
		sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		try {
//			if(sessionUser.getUserType().equals(UserType.ADMIN) || sessionUser.getUserType().equals(UserType.SUBADMIN)){
				if (businessType.equals("ALL")) {
					logger.info("Business Type :"+businessType);
					aaData = encoder
							.encodeMerchantDetailsObj(merchantGridViewService.getAllMerchantDetails(null,sessionMap.get(Constants.SEGMENT.getValue()).toString(),sessionUser.getRole().getId()));
				} else {
					logger.info("Business Type :"+businessType);
					aaData = encoder.encodeMerchantDetailsObj(
							merchantGridViewService.getAllMerchants(getBusinessType(), null,sessionMap.get(Constants.SEGMENT.getValue()).toString(),sessionUser.getRole().getId()));
				}
//			}
			
			return SUCCESS;
		} catch (SystemException exception) {
			exception.printStackTrace();
			logger.error("exception", exception);
			return ERROR;
		}
	}
	@Override
	public void validate() {
	if ((validator.validateBlankField(getBusinessType()))) {
	
	} else if (!(validator.validateField(CrmFieldType.BUSINESS_TYPE,
			getBusinessType()))) {
		addFieldError(CrmFieldType.BUSINESS_TYPE.getName(), validator
				.getResonseObject().getResponseMessage());
	}
	}
//Show all resellerList in Dashbord
	public String listOfReseller(){
		try {
			aaData = encoder.encodeMerchantDetailsObj(merchantGridViewService.getAllReseller());
			return SUCCESS;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}
	}
	
	 public String resellerList(){
			User sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		 try {
				aaData = encoder.encodeMerchantDetailsObj(merchantGridViewService.getAllReselerMerchants(sessionUser.getResellerId()));
				return SUCCESS;
			} catch (Exception exception) {
				logger.error("Exception", exception);
				return ERROR;
			}
	 }
	public List<MerchantDetails> getaaData() {
		return aaData;
	}

	public void setaaData(List<MerchantDetails> setaaData) {
		this.aaData = setaaData;
	}
	public String getBusinessType() {
		return businessType;
	}
	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}
	
	
}