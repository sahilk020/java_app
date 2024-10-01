package com.pay10.crm.chargeback;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.Chargeback;
import com.pay10.commons.user.ChargebackComment;
import com.pay10.commons.user.User;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.TransactionManager;
import com.pay10.crm.action.AbstractSecureAction;
import com.pay10.crm.chargeback.action.beans.ChargebackDao;

public class ChargebackCommentCreator extends AbstractSecureAction{
	
	@Autowired
	ChargebackDao chargebackDao;

	private static final long serialVersionUID = 8269340542345930150L;
	
	private String comment;
	private String caseId;
	private String chargebackstatus;
	private String response;
	private String caseStatus;

	@Override
	public String execute() {
		String commentSenderEmailId;
		Chargeback chargeback = new Chargeback();
		ChargebackDao dao = new ChargebackDao();
		try {
			User sessionUser = (User) sessionMap.get(Constants.USER.getValue());
			commentSenderEmailId = sessionUser.getEmailId();
			chargeback = dao.findByCaseId(caseId);
			ChargebackComment chargebackComment = new ChargebackComment();
			chargebackComment
					.setCommentId(TransactionManager.getNewTransactionId());
			chargebackComment.setCommentBody(getComment());
			chargebackComment.setCommentSenderEmailId(commentSenderEmailId);
			Set<ChargebackComment> commentSet = chargeback.getChargebackComments();
			commentSet.add(chargebackComment);
			chargeback.setChargebackComments(commentSet);
			chargeback.setChargebackStatus(getChargebackstatus());
		/*	if(getChargebackstatus().equals(ChargebackStatus.ACCEPTED_BY_MERCHANT.getName())){
				chargeback.setStatus(CaseStatus.CLOSE.getName());
			} else{
				chargeback.setStatus(CaseStatus.OPEN.getName());
			}*/
			chargeback.setStatus(getCaseStatus());
			dao.update(chargeback);
			
			setResponse(ErrorType.COMMENT_SUCCESSFULLY_ADDED.getResponseMessage());
			
			Iterator<ChargebackComment> iterator = commentSet.iterator();
			 List<ChargebackComment> commentList = new LinkedList<>();
			while (iterator.hasNext()) {
				ChargebackComment comments = iterator.next();
				commentList.add(comments);
				
			}
			/*TicketEmailBuilder ticketEmailBuilder = new TicketEmailBuilder();
			
			ticketEmailBuilder.sendCommentEmail(commentList,ticketId,sessionUser.getUserType().toString());*/
			
			
			return SUCCESS;
		} catch (Exception exception) {
			//logger.error("Exception", exception);
			return ERROR;
		}

	}

	@Override
	public void validate() {
		CrmValidator validator = new CrmValidator();

		if (validator.validateBlankField(getCaseId())) {
			/*addFieldError(CrmFieldType.TRANSACTION_ID.getName(), validator
					.getResonseObject().getResponseMessage());*/
		} else if (!validator.validateField(CrmFieldType.TRANSACTION_ID,
				getCaseId())) {
			/*addFieldError(CrmFieldType.TRANSACTION_ID.getName(),
					ErrorType.INVALID_FIELD.getResponseMessage());*/
		}

	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getCaseId() {
		return caseId;
	}

	public void setCaseId(String caseId) {
		this.caseId = caseId;
	}

	public String getChargebackstatus() {
		return chargebackstatus;
	}

	public void setChargebackstatus(String chargebackstatus) {
		this.chargebackstatus = chargebackstatus;
	}

	public String getCaseStatus() {
		return caseStatus;
	}

	public void setCaseStatus(String caseStatus) {
		this.caseStatus = caseStatus;
	}

}
