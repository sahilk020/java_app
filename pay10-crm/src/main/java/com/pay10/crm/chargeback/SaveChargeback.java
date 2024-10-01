package com.pay10.crm.chargeback;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.user.Chargeback;
import com.pay10.commons.user.TransactionHistory;
import com.pay10.commons.user.User;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.MopType;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.TransactionManager;
import com.pay10.crm.action.AbstractSecureAction;
import com.pay10.crm.actionBeans.RefundDetailsProvider;
import com.pay10.crm.chargeback.action.beans.ChargebackDao;
import com.pay10.crm.chargeback.util.CaseStatus;
import com.pay10.crm.chargeback.util.ChargebackStatus;
import com.pay10.crm.chargeback.util.ChargebackType;

public class SaveChargeback extends AbstractSecureAction {
	
	@Autowired
	private ChargebackDao chargebackDao;
	@Autowired
	private RefundDetailsProvider refundDetailsProvider;

	private static final long serialVersionUID = 5254839666209321240L;
	private static Logger logger = LoggerFactory.getLogger(SaveChargeback.class.getName());
	
	private String Id;
	private String txnId;
	private String orderId;
	private String payId;
	private String caseId;
	private String targetDate;
	private String comments;
	private String commentedBy;
	private String chargebackType;
	private String chargebackStatus;
	private String makeComment;
	private String documentUploadFilename;
	private String Filename;
	private File image;
	private String imageFileName;

	private TransactionHistory transDetails = new TransactionHistory();

	@Override
	public String execute() {
		Chargeback chargeback = new Chargeback();
		Chargeback oldChargeback = new Chargeback();
		SaveChargebackDocument saveChargebackDocument = new SaveChargebackDocument();
		try {
			
			String chargebackType = getChargebackType().toString();
			String chargebackStatus = CaseStatus.CLOSE.getName();
			oldChargeback = chargebackDao.findByTxnId(txnId,chargebackStatus,chargebackType);
			
			if((oldChargeback != null && !ChargebackType.PRE_ARBITRATION.getName().equals(chargebackType) ) || (oldChargeback != null && !oldChargeback.getStatus().toString().equals(chargebackStatus))){
			
				return ERROR;
			}
			else{
				
			
			User user = (User) sessionMap.get(Constants.USER);
			chargeback.setCaseId(TransactionManager.getNewTransactionId());
			if (image != null) {
				chargeback.setDocumentId(TransactionManager.getNewTransactionId());
			}
			chargeback.setUpdateDate(new Date());
			chargeback.setTargetDate(getTargetDate());
			chargeback.setChargebackType(getChargebackType());
			chargeback.setChargebackStatus(ChargebackStatus.NEW.getName());
			chargeback.setCommentedBy(user.getBusinessName().toString());
			chargeback.setComments(comments);
			chargeback.setId(TransactionManager.getNewTransactionId());
			
			// from database

			List<TransactionHistory> refundD = refundDetailsProvider.RefundProvider(orderId, payId, txnId);
			refundDetailsProvider.getAllTransactions();
			transDetails = refundDetailsProvider.getTransDetails();
		
			chargeback.setOrderId(transDetails.getOrderId());
			chargeback.setPayId(transDetails.getPayId());
			chargeback.setTransactionId(txnId);
			chargeback.setCreateDate(new Date());
			chargeback.setCardNumber(transDetails.getCardNumber());
			chargeback.setMopType(MopType.getmopName(transDetails.getMopType()));
			//chargeback.setStatus(transDetails.getStatus());
			chargeback.setPaymentType(PaymentType.getpaymentName(transDetails.getPaymentType()));
			chargeback.setCustEmail(transDetails.getCustEmail());
			chargeback.setInternalCustIP(transDetails.getInternalCustIP());
			chargeback.setInternalCustCountryName(transDetails.getInternalCustCountryName());
			chargeback.setInternalCardIssusserBank(transDetails.getInternalCardIssusserBank());
			chargeback.setInternalCardIssusserCountry(transDetails.getInternalCardIssusserCountry());
			chargeback.setCurrencyCode(transDetails.getCurrencyCode());
			chargeback.setCurrencyNameCode(transDetails.getCurrencyNameCode());
			chargeback.setAmount(transDetails.getAmount());
			chargeback.setCapturedAmount(transDetails.getCapturedAmount());
			chargeback.setAuthorizedAmount(transDetails.getAuthorizedAmount());
			chargeback.setFixedTxnFee(transDetails.getFixedTxnFee());
			chargeback.setTdr(transDetails.getTdr());
			chargeback.setServiceTax(transDetails.getServiceTax());
			chargeback.setChargebackAmount(transDetails.getChargebackAmount());
			chargeback.setNetAmount(transDetails.getNetAmount());
			chargeback.setPercentecServiceTax(transDetails.getPercentecServiceTax());
			chargeback.setMerchantTDR(transDetails.getMerchantTDR());
			chargeback.setStatus(CaseStatus.OPEN.getName());

			chargebackDao.create(chargeback);
			addActionMessage(CrmFieldConstants.GENERATED_SUCCESSFULLY.getValue());
			if (image != null && (imageFileName.toLowerCase().endsWith(".pdf")
					|| imageFileName.toLowerCase().endsWith(".jpg") || imageFileName.toLowerCase().endsWith(".png"))) {
				saveChargebackDocument.SaveFile(chargeback.getCaseId(), imageFileName, image, payId,
						chargeback.getDocumentId());
			}

			return SUCCESS;
			}
		}

		catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}

	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getPayId() {
		return payId;
	}

	public void setPayId(String payId) {
		this.payId = payId;
	}

	public String getCaseId() {
		return caseId;
	}

	public void setCaseId(String caseId) {
		this.caseId = caseId;
	}

	public String getTargetDate() {
		return targetDate;
	}

	public void setTargetDate(String targetDate) {
		this.targetDate = targetDate;
	}

	public String getCommentedBy() {
		return commentedBy;
	}

	public void setCommentedBy(String commentedBy) {
		this.commentedBy = commentedBy;
	}

	public String getChargebackType() {
		return chargebackType;
	}

	public void setChargebackType(String chargebackType) {
		this.chargebackType = chargebackType;
	}

	public String getChargebackStatus() {
		return chargebackStatus;
	}

	public void setChargebackStatus(String chargebackStatus) {
		this.chargebackStatus = chargebackStatus;
	}

	public String getTxnId() {
		return txnId;
	}

	public void setTxnId(String txnId) {
		this.txnId = txnId;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getMakeComment() {
		return makeComment;
	}

	public void setMakeComment(String makeComment) {
		this.makeComment = makeComment;
	}

	public String getDocumentUploadFilename() {
		return documentUploadFilename;
	}

	public void setDocumentUploadFilename(String documentUploadFilename) {
		this.documentUploadFilename = documentUploadFilename;
	}

	public String getFilename() {
		return Filename;
	}

	public void setFilename(String filename) {
		Filename = filename;
	}

	public File getImage() {
		return image;
	}

	public void setImage(File image) {
		this.image = image;
	}

	public String getImageFileName() {
		return imageFileName;
	}

	public void setImageFileName(String imageFileName) {
		this.imageFileName = imageFileName;
	}

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

}
