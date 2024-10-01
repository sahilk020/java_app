package com.pay10.crm.chargeback_new;

import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.user.Chargeback;
import com.pay10.commons.user.ChargebackComment;
import com.pay10.crm.action.AbstractSecureAction;
import com.pay10.crm.chargeback_new.action.beans.ChargebackDao;

public class ViewChargebackDetails extends AbstractSecureAction {

	@Autowired
	private ChargebackDao chargebackDao;

	private static final long serialVersionUID = 576762458042745734L;
	private static Logger logger = LoggerFactory.getLogger(ViewChargebackDetails.class.getName());
	private Chargeback chargeback = new Chargeback();
	private String Id;
//	private String caseId;
//	private String commentsString;
//	private String commentId;
	private String createDate;
	private String updateDate; // Format chargeback date.
	private String dueDate;
	private String registrationDate;
	private List<ChargebackComment> commentList = new LinkedList<>();

	@Override
	public String execute() {
		try {
			Chargeback cb = chargebackDao.findById(getId());
			setUpdateDate(new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a").format(cb.getUpdateDate()));
			setCreateDate(new SimpleDateFormat("dd/MM/yyyy").format(cb.getCreateDate()));
			// Uncomment this if want to get date time format for create date.
//			setCreateDate(new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a").format(cb.getCreateDate()));
			setRegistrationDate(new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a").format(cb.getCbRegistrationDate()));
			setDueDate(cb.getTargetDate().replaceAll("-", "/"));
			setChargeback(cb);
			return SUCCESS;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;

		}
	}

	public Chargeback getChargeback() {
		return chargeback;
	}

	public void setChargeback(Chargeback chargeback) {
		this.chargeback = chargeback;
	}

	public List<ChargebackComment> getCommentList() {
		return commentList;
	}

	public void setCommentList(List<ChargebackComment> commentList) {
		this.commentList = commentList;
	}

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public String getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getDueDate() {
		return dueDate;
	}

	public void setDueDate(String dueDate) {
		this.dueDate = dueDate;
	}

	public String getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(String registrationDate) {
		this.registrationDate = registrationDate;
	}

}
