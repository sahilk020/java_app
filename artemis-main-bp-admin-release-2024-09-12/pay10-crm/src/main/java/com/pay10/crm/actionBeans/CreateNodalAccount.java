package com.pay10.crm.actionBeans;

import java.util.Date;

import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.NodalAmount;
import com.pay10.commons.user.NodalAmoutDao;
import com.pay10.commons.user.ResponseObject;

@Service
public class CreateNodalAccount {
	

	
	@Autowired
	NodalAmoutDao nodalDao;

	
	private static Logger logger = LoggerFactory.getLogger(CreateNodalAccount.class.getName());
	
	public ResponseObject createNodalAmount(NodalAmount nodal) throws SystemException {
		logger.info("Inside new nodal account ");
		String paymentType = null;
		if(nodal.getPaymentType().equals("CC")||nodal.getPaymentType().equals("DC"))
		{
			 paymentType="CC-DC";
		}
		else if(nodal.getPaymentType().equals("UP"))
		{
			paymentType="UPI";
		}
		
		ResponseObject responseActionObject = new ResponseObject();
		try {
			Date date = new Date();
			nodal.setAcquirer(nodal.getAcquirer());
			nodal.setPaymentType(paymentType);
			nodal.setNodalCreditAmount(nodal.getNodalCreditAmount());
			nodal.setCreateDate(nodal.getCreateDate());
			nodal.setCaptureDate(nodal.getCaptureDate());
			nodal.setSettlementDate(nodal.getSettlementDate());
			nodal.setCreateDate(date);
			nodal.setMerchant(nodal.getMerchant());
			nodal.setPaymentMethod(nodal.getPaymentMethod());
			nodalDao.create(nodal);
			responseActionObject.setResponseCode(ErrorType.SUCCESS.getResponseCode());
		    return responseActionObject;
	}
		catch (Exception e) {
			responseActionObject.setResponseCode(ErrorType.USER_UNAVAILABLE.getResponseCode());
		 return	responseActionObject;
		}

		
}
}
