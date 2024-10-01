package com.pay10.crm.action;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.api.EmailControllerServiceProvider;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.Merchants;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;

/**
 * @neeraj
 */

public class SendBulkEmailAction extends AbstractSecureAction {

	@Autowired
	private UserDao userDao;
	
	@Autowired
	private EmailControllerServiceProvider emailControllerServiceProvider;
	
	@Autowired
	private CrmValidator validator;
	
	private static final long serialVersionUID = 2421842267236138348L;
	private static Logger logger = LoggerFactory.getLogger(SendBulkEmailAction.class.getName());
	private String subject;
	private String messageBody;

	public String sendBulkEmail() {
		List<Merchants> merchantsList = new ArrayList<Merchants>();
		try {
			merchantsList = userDao.featchAllmerchant();
			boolean first = true;
			String emailID = "";
			for (Merchants merchants : merchantsList) {
				String merchantemailId = merchants.getEmailId();
				if (first) {
					emailID += merchantemailId;
					first = false;
				} else {
					emailID += "," + merchantemailId;
				}
			}
			emailControllerServiceProvider.sendBulkEmailServiceTax(emailID,subject,messageBody);
			subject= " ";
			messageBody= " ";
			
			addActionMessage("Email successfully sent.");
		} catch (SystemException exception) {
			logger.error("Exception", exception);
			return ERROR;
		}
		return SUCCESS;

	}
	@Override
	public void validate() {
	if ((validator.validateBlankField(getSubject()))) {
		addFieldError(CrmFieldType.SUBJECT.getName(), validator
				.getResonseObject().getResponseMessage());
	} else if (!(validator.validateField(CrmFieldType.SUBJECT,
			getSubject()))) {
		addFieldError(CrmFieldType.SUBJECT.getName(), validator
				.getResonseObject().getResponseMessage());
	}
	if ((validator.validateBlankField(getMessageBody()))){
		addFieldError(CrmFieldType.MESSAGE.getName(), validator
				.getResonseObject().getResponseMessage());
	} else if (!(validator.validateField(CrmFieldType.MESSAGE,
			getMessageBody()))) {
		addFieldError(CrmFieldType.MESSAGE.getName(), validator
				.getResonseObject().getResponseMessage());
	}
	}

	public String getMessageBody() {
		return messageBody;
	}

	public void setMessageBody(String messageBody) {
		this.messageBody = messageBody;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

}
