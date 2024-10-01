package com.pay10.crm.action;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.user.Merchants;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.PropertiesManager;

/**
 * @author Sweety
 *
 */
public class DisplayWebhookPageAction extends AbstractSecureAction {
	private static final long serialVersionUID = 2069162558641070280L;

	private static Logger logger = LoggerFactory.getLogger(DisplayWebhookPageAction.class.getName());

	@Autowired
	private UserDao userDao;

	private List<Merchants> merchants;
	private String saveEvent;
	private String eventByAssociationId;
	private String saveDetails;
	private User sessionUser = null;
	
	@SuppressWarnings("unchecked")
	public String execute() {
		try {
			sessionUser = (User) sessionMap.get(Constants.USER.getValue());
			setSaveEvent(PropertiesManager.propertiesMap.get(Constants.WEBHOOK_SAVE_EVENT.getValue()));
			setEventByAssociationId(
					PropertiesManager.propertiesMap.get(Constants.WEBHOOK_EVENT_BY_ASSOCIATION_ID.getValue()));
			logger.info("saveEvent" + getEventByAssociationId());
			
			setSaveDetails(PropertiesManager.propertiesMap.get(Constants.WEBHOOK_SAVED_DETAILS.getValue()));
			//setMerchants(userDao.getActiveMerchantList());
			setMerchants(userDao.getActiveMerchantList(sessionMap.get(Constants.SEGMENT.getValue()).toString(), sessionUser.getRole().getId()));
			return INPUT;
		} catch (Exception exception) {
			logger.error("Exception ", exception);
			return ERROR;
		}
	}

	public List<Merchants> getMerchants() {
		return merchants;
	}

	public void setMerchants(List<Merchants> merchants) {
		this.merchants = merchants;
	}

	public String getSaveEvent() {
		return saveEvent;
	}

	public void setSaveEvent(String saveEvent) {
		this.saveEvent = saveEvent;
	}

	public String getEventByAssociationId() {
		return eventByAssociationId;
	}

	public void setEventByAssociationId(String eventByAssociationId) {
		this.eventByAssociationId = eventByAssociationId;
	}
	public String getSaveDetails() {
		return saveDetails;
	}

	public void setSaveDetails(String saveDetails) {
		this.saveDetails = saveDetails;
	}
}
