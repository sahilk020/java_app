package com.pay10.crm.chargeback;

import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.user.Chargeback;
import com.pay10.crm.action.AbstractSecureAction;
import com.pay10.crm.chargeback.action.beans.ChargebackDao;
import com.pay10.crm.chargeback.util.CaseStatus;
import com.pay10.crm.chargeback.util.ChargebackStatus;

public class UpdateChargebackDetails extends AbstractSecureAction {

	@Autowired
	private ChargebackDao chargebackDao;
	private static final long serialVersionUID = 3981325447017559786L;
	private static Logger logger = LoggerFactory.getLogger(UpdateChargebackDetails.class.getName());
	private String Id;
	private String caseId;

	private Chargeback chargeback = new Chargeback();

	@Override
	public String execute() {

		try {
			setChargeback(chargebackDao.findByCaseId(getCaseId()));
			Chargeback chargeback = new Chargeback();
			chargeback = getChargeback();
			chargeback.setChargebackStatus(ChargebackStatus.ACCEPTED_BY_MERCHANT.getName());
			chargeback.setStatus(CaseStatus.CLOSE.getName());
			chargebackDao.update(chargeback);
			return SUCCESS;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}
	}

	public String getCaseId() {
		return caseId;
	}

	public void setCaseId(String caseId) {
		this.caseId = caseId;
	}

	public Chargeback getChargeback() {
		return chargeback;
	}

	public void setChargeback(Chargeback chargeback) {
		this.chargeback = chargeback;
	}

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

}
