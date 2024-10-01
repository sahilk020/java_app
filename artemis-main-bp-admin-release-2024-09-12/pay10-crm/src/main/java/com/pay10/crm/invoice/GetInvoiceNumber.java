package com.pay10.crm.invoice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.crm.action.AbstractSecureAction;

public class GetInvoiceNumber extends AbstractSecureAction {

	/**
	 * Sweety
	 */
	private static final long serialVersionUID = 3012889494429785258L;
		private static Logger logger = LoggerFactory.getLogger(GetInvoiceNumber.class.getName());
		private String payid;
		private String businessName;
		@Autowired
		private UserDao userDao;

		@Override
		public String execute() {
			logger.info("get PayId : " + payid);
			logger.info("MessageCondition : " + getBusinessName());
			setBusinessName(userDao.getMerchantByPayId(payid));
			logger.info("MessageCondition : " + getBusinessName());
			return SUCCESS;
		}

		public String getPayid() {
			return payid;
		}

		public void setPayid(String payid) {
			this.payid = payid;
		}

		public String getBusinessName() {
			return businessName;
		}

		public void setBusinessName(String businessName) {
			this.businessName = businessName;
		}
		
		

}
