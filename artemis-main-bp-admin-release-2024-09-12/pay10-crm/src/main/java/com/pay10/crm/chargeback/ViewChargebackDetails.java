package com.pay10.crm.chargeback;

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
	private String caseId;
	private String commentsString;
	private String commentId;
	private String createDate;
	private String messageBody;
	private String response;
	private String caseStatus;
	private List<ChargebackComment> commentList = new LinkedList<>();

	@Override
	public String execute() {
		try {
			setChargeback(chargebackDao.findById(getId()));
			if (chargeback.getChargebackChat() != null) {
				commentsString = new String(chargeback.getChargebackChat());
			}
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

	public String getCaseId() {
		return caseId;
	}

	public void setCaseId(String caseId) {
		this.caseId = caseId;
	}

	public String getCommentsString() {
		return commentsString;
	}

	public void setCommentsString(String commentsString) {
		this.commentsString = commentsString;
	}

	public String getCommentId() {
		return commentId;
	}

	public void setCommentId(String commentId) {
		this.commentId = commentId;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getMessageBody() {
		return messageBody;
	}

	public void setMessageBody(String messageBody) {
		this.messageBody = messageBody;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
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

	public String getCaseStatus() {
		return caseStatus;
	}

	public void setCaseStatus(String caseStatus) {
		this.caseStatus = caseStatus;
	}

}
