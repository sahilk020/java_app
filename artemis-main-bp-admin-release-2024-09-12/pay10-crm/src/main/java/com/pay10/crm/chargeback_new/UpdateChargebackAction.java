package com.pay10.crm.chargeback_new;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pay10.commons.audittrail.ActionMessageByAction;
import com.pay10.commons.audittrail.AuditTrail;
import com.pay10.commons.audittrail.AuditTrailService;
import com.pay10.commons.user.Chargeback;
import com.pay10.commons.user.User;
import com.pay10.commons.util.Constants;
import com.pay10.crm.action.AbstractSecureAction;
import com.pay10.crm.chargeback_new.action.beans.ChargebackDao;

/**
 * 
 * @author shubhamchauhan
 *
 */
public class UpdateChargebackAction extends AbstractSecureAction {

	private static final long serialVersionUID = -8739827496900658409L;
	private static final Logger logger = LoggerFactory.getLogger(UpdateChargebackAction.class.getName());

	@Autowired
	ChargebackDao cbDao;

	@Autowired
	private AuditTrailService auditTrailService;

	private String cbId;
	private String cbStatus;
	private String updateDate;

	private String responseMessage;
	private int responseCode;

	@SuppressWarnings("unchecked")
	@Override
	public String execute() {
		Chargeback oldChargeback = new Chargeback();
		oldChargeback = cbDao.findById(getCbId());
		try {
			String prevChargeBack = mapper.writeValueAsString(oldChargeback);

			if (oldChargeback == null) {
				setResponseCode(1);
				setResponseMessage("Unable to update chargeback");
				return SUCCESS;
			}

			if (getCbStatus().equals(oldChargeback.getChargebackStatus())) {
				setResponseCode(2);
				setResponseMessage("Status is same");
				return SUCCESS;
			}

			oldChargeback.setChargebackStatus(getCbStatus());
			Date date = new Date();
			setUpdateDate(new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a").format(date));
			oldChargeback.setUpdateDate(date);

			// Maintain Trail here...
			User user = (User) sessionMap.get(Constants.USER.getValue());
			JSONArray oldCBTrail = new JSONArray((new String(oldChargeback.getChargebackTrail())));
			String cbTrail = "{\"username\" : \"" + user.getFirstName() + "\", \"usertype\" : \""
					+ user.getUserType().toString() + "\", \"timestamp\" : \""
					+ (new SimpleDateFormat("dd/mm/yyyy hh:mm:ss aa").format(new Date())) + "\", \"cbstatus\": \""
					+ getCbStatus() + "\" }";
			JSONObject jsObj = new JSONObject(cbTrail);
			oldCBTrail.put(jsObj);
			oldChargeback.setChargebackTrail(oldCBTrail.toString().getBytes());

			cbDao.update(oldChargeback);

			Map<String, ActionMessageByAction> actionMessagesByAction = (Map<String, ActionMessageByAction>) sessionMap
					.get(Constants.ACTION_MESSAGE_BY_ACTION.getValue());
			AuditTrail auditTrail = new AuditTrail(mapper.writeValueAsString(oldChargeback), prevChargeBack,
					actionMessagesByAction.get("updateChargeback"));
			auditTrailService.saveAudit(request, auditTrail);
		} catch (JsonProcessingException ex) {
			logger.error("Exception", ex);
			return ERROR;
		}
		setResponseCode(200);
		setResponseMessage("Chargeback Updated successfully");
		return SUCCESS;
	}

	public String getCbId() {
		return cbId;
	}

	public void setCbId(String cbId) {
		this.cbId = cbId;
	}

	public String getCbStatus() {
		return cbStatus;
	}

	public void setCbStatus(String cbStatus) {
		this.cbStatus = cbStatus;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}

	public String getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}

	@Override
	public String toString() {
		return "SHUpdateChargebackAction [cbId=" + cbId + ", cbStatus=" + cbStatus + "]";
	}

}
