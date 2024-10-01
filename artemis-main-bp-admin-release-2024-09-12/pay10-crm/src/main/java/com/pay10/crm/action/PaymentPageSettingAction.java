package com.pay10.crm.action;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.ModelDriven;
import com.pay10.commons.action.AbstractSecureAction;
import com.pay10.commons.user.DynamicPaymentPage;
import com.pay10.commons.user.DynamicPaymentPageDao;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.pg.core.util.FetchDynamicPaymentPage;

/**
 * @Isha
 */
public class PaymentPageSettingAction extends AbstractSecureAction implements ModelDriven<DynamicPaymentPage> {

	@Autowired
	private FetchDynamicPaymentPage fetchDynamicPage;

	@Autowired
	private DynamicPaymentPageDao dynamicPaymentPageDao;

	@Autowired
	private CrmValidator validator;

	private String payId;

	private static final long serialVersionUID = -2068525281163587799L;
	private static Logger logger = LoggerFactory.getLogger(PaymentPageSettingAction.class.getName());

	DynamicPaymentPage dynamicPaymentPage = new DynamicPaymentPage();

	@Override
	public String execute() {
		try {

			User sessionUser = (User) sessionMap.get(Constants.USER.getValue());
			setDynamicPaymentPage(dynamicPaymentPageDao.findPayId(sessionUser.getPayId()));
			setDynamicPaymentPage(fetchDynamicPage.FetchDynamicDesignPage(sessionUser.getPayId()));
			File file = new File(makeFileName());
			if (file.exists()) {
				dynamicPaymentPage.setUserLogo("TRUE");
			}
			return SUCCESS;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}
	}

	@Override
	public void validate() {
		if ((validator.validateBlankField(getPayId()))) {
			addFieldError(CrmFieldType.PAY_ID.getName(), validator.getResonseObject().getResponseMessage());
		} else if (!(validator.validateField(CrmFieldType.PAY_ID, getPayId()))) {
			addFieldError(CrmFieldType.PAY_ID.getName(), validator.getResonseObject().getResponseMessage());
		}
	}

	private String makeFileName() {
		String name, destPath, logoFormat;

		destPath = PropertiesManager.propertiesMap.get("PaymentPagePath");
		logoFormat = PropertiesManager.propertiesMap.get("PaymentPageLogoFormat");
		User sessionUser = (User) sessionMap.get(Constants.USER.getValue());

		try {

			if (sessionUser.getUserType().equals(UserType.ADMIN)
					|| sessionUser.getUserType().equals(UserType.SUPERADMIN)) {
			} else {
				setPayId(sessionUser.getPayId());
			}
			if (destPath != null && logoFormat != null) {
				name = destPath + "/" + getPayId() + logoFormat;
				return name;
			} else {
				return null;
			}
		}

		catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}

	}

	@Override
	public DynamicPaymentPage getModel() {

		return dynamicPaymentPage;
	}

	public DynamicPaymentPage getDynamicPaymentPage() {
		return dynamicPaymentPage;
	}

	public void setDynamicPaymentPage(DynamicPaymentPage dynamicPaymentPage) {
		this.dynamicPaymentPage = dynamicPaymentPage;
	}

	public String getPayId() {
		return payId;
	}

	public void setPayId(String payId) {
		this.payId = payId;
	}

}
