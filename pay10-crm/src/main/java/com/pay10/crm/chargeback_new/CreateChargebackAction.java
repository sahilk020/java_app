package com.pay10.crm.chargeback_new;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pay10.commons.audittrail.ActionMessageByAction;
import com.pay10.commons.audittrail.AuditTrail;
import com.pay10.commons.audittrail.AuditTrailService;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.Chargeback;
import com.pay10.commons.user.User;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.Currency;
import com.pay10.commons.util.MopType;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.TransactionManager;
import com.pay10.crm.action.AbstractSecureAction;
import com.pay10.crm.chargeback_new.action.beans.ChargebackDao;
import com.pay10.crm.chargeback_new.util.CaseStatus;
import com.pay10.crm.chargeback_new.util.ChargebackType;
import com.pay10.crm.chargeback_new.util.ChargebackUtilities;

public class CreateChargebackAction extends AbstractSecureAction {

	private static final long serialVersionUID = 7523524805292155591L;
	private static final Logger logger = LoggerFactory.getLogger(CreateChargebackAction.class.getName());
	
	@Autowired
	ChargebackDao cbDao;

	@Autowired
	CrmValidator validator;
	
	@Autowired
	private AuditTrailService auditTrailService;

	private String targetDate;
	private Date cbRaiseDate; // Date given by bank.

	private String createDate;
	private String chargebackType;
	private String transactionId;
	private String caseId; // Ask shubham sir for character validation in caseid
	private String chargebackChat;
	private String finalChargebackChat;
	private String txnDate;
	private String pgRefNum;
	private String payId;
	private String orderId;
	private String acqId;
	private String txnStatus;
	private String custEmail;
	private String custPhone;

	private String responseMessage;
	private int responseCode;
	private String businessName;

	private BigDecimal amount;
	private BigDecimal pgTdr;
	private BigDecimal acqTdr;
	private BigDecimal pgGst;
	private BigDecimal acqGst;
	private String cardMask;
	private String currencyCode; // class currency.java (getAlplabetic code);

	private String mopType;
	private String paymentType;
	
	private String internalCardIssuerBank; 
	private String internalCardIssuerCountry;
	private String custCountry;
	private String internalCustIp;

	@SuppressWarnings("unchecked")
	@Override
	public String execute() {

		// put a check for blank caseId.
		
		// Load existing chargeback
		Chargeback oldChargeback = cbDao.findByPgRefNum(getPgRefNum(), ChargebackType.CHARGEBACK.getName());
		Chargeback oldFraudDispute = cbDao.findFraudByPgRefNum(getPgRefNum(), ChargebackType.FRAUD_DISPUTES.getName());

		// Allow user either to create chargeback or fraud dispute.
		
		if((oldChargeback != null) && getChargebackType().equals(ChargebackType.FRAUD_DISPUTES.getName())) {
			responseMessage = "Chargback already exists. Cannot raise fraud dispute.";
			responseCode = 102;
			return SUCCESS;
		}
		
		if((oldFraudDispute != null) && getChargebackType().equals(ChargebackType.CHARGEBACK.getName())) {
			responseMessage = "Fraud Dispute already exists. Cannot raise Chargeback.";
			responseCode = 102;
			return SUCCESS;
		}
		
		
		if (getChargebackType().equals(ChargebackType.CHARGEBACK.getName())) {
			if (oldChargeback != null) {
				responseCode = 102;
				responseMessage = "Chargback already exists";
				return SUCCESS;
			}
		} else if (getChargebackType().equals(ChargebackType.FRAUD_DISPUTES.getName())) {
			if (oldChargeback != null) {
				responseMessage = "Fraud Dispute already exists";
				responseCode = 102;
				return SUCCESS;
			}
		} else if (getChargebackType().equals(ChargebackType.PRE_ARBITRATION.getName())) {
			if (oldChargeback == null && oldFraudDispute == null) {
				responseMessage = "First raise a chargeback or fraud dispute.";
				responseCode = 103;
				return SUCCESS;
			}

			if ((oldChargeback != null) && !(oldChargeback.getChargebackStatus().equalsIgnoreCase(CaseStatus.CLOSE.getName()))) {
				responseMessage = "Previous chargeback is already open";
				responseCode = 104;
				return SUCCESS;
			}
			
			if ((oldFraudDispute != null) && !(oldFraudDispute.getChargebackStatus().equalsIgnoreCase(CaseStatus.CLOSE.getName()))) {
				responseMessage = "Previous fraud dispute is already open";
				responseCode = 104;
				return SUCCESS;
			}

			oldChargeback = cbDao.findByPgRefNum(getPgRefNum(), getChargebackType());
			if (oldChargeback != null) {
				responseMessage = "Pre Arbitration already exists";
				responseCode = 105;
				return SUCCESS;
			}
		}

		// Check for unique caseId.
			
		if (getChargebackType().equals(ChargebackType.CHARGEBACK.getName()) || getChargebackType().equals(ChargebackType.FRAUD_DISPUTES.getName())) {
			List<Chargeback> chargebackList = cbDao.findByCaseId(getCaseId());
			if (chargebackList != null && chargebackList.size() > 0) {
				responseMessage = "Enter Unique Case Id";
				responseCode = 101;
				return SUCCESS;
			}
		}
		Chargeback chargeback = new Chargeback();
		chargeback.setId(TransactionManager.getNewTransactionId());
		transferCBFiles(chargeback.getId());
		chargeback.setCaseId(getCaseId());
		chargeback.setBusinessName(getBusinessName());
		chargeback.setTargetDate(getTargetDate());
		chargeback.setCreateDate(getCbRaiseDate()); // Get current time stamp...
		chargeback.setUpdateDate(new Date());
		chargeback.setCustEmail(custEmail);
		chargeback.setOrderId(getOrderId());
		chargeback.setAmount(getAmount());
		chargeback.setCapturedAmount(getAmount());
		chargeback.setAuthorizedAmount(getAmount());
		chargeback.setChargebackAmount(getAmount());
		chargeback.setTxnStatus(getTxnStatus());

		chargeback.setChargebackType(getChargebackType());

		chargeback.setChargebackStatus("open"); // set chargeback status.
		chargeback.setPayId(getPayId());
		chargeback.setTransactionId(getTransactionId());
		chargeback.setCbRegistrationDate(new Date()); // Date for internal purpose when chargeback was created...
		chargeback.setPg_ref_num(getPgRefNum());
		chargeback.setTxn_date(getTxnDate());
		chargeback.setCust_phone(getCustPhone());
		chargeback.setAcq_id(getAcqId());
		chargeback.setCardNumber(getCardMask());

		BigDecimal totalTdr = getPgTdr().add(getAcqTdr());
		chargeback.setTdr(totalTdr);

		BigDecimal totalGst = getPgGst().add(getAcqGst());
		chargeback.setServiceTax(totalGst);

		BigDecimal netAmount = getAmount().subtract(totalTdr.add(totalGst));
		chargeback.setNetAmount(netAmount);

		chargeback.setCurrencyCode(getCurrencyCode());
		chargeback.setCurrencyNameCode(Currency.getAlphabaticCode(getCurrencyCode()));

		chargeback.setChargebackChat(finalChargebackChat.getBytes());

		User user = (User) sessionMap.get(Constants.USER.getValue());
		String cbTrail = "[{\"username\" : \"" + user.getFirstName() + "\", \"usertype\" : \""
				+ user.getUserType().toString() + "\", \"timestamp\" : \""
				+ (new SimpleDateFormat("dd/mm/yyyy hh:mm:ss aa").format(new Date())) + "\", \"cbstatus\": \""
				+ CaseStatus.OPEN.getName() + "\" }]";

		chargeback.setChargebackTrail(cbTrail.getBytes());
		chargeback.setPaymentType(getPaymentType());
		chargeback.setMopType(MopType.getmopName(getMopType()));
		chargeback.setInternalCardIssusserBank(getInternalCardIssuerBank());
		chargeback.setInternalCardIssusserCountry(getInternalCardIssuerCountry());
		chargeback.setInternalCustCountryName(getCustCountry());
		chargeback.setInternalCustIP(getInternalCustIp());
		cbDao.create(chargeback);

		Map<String, ActionMessageByAction> actionMessagesByAction = (Map<String, ActionMessageByAction>) sessionMap
				.get(Constants.ACTION_MESSAGE_BY_ACTION.getValue());
		try {
			AuditTrail auditTrail = new AuditTrail(mapper.writeValueAsString(chargeback), null,
					actionMessagesByAction.get("createchargeback"));
			auditTrailService.saveAudit(request, auditTrail);
		} catch (JsonProcessingException ex) {
			logger.error("Exception", ex);
			return ERROR;
		}

		responseMessage = getChargebackType() + " created successfully";
		responseCode = 200;
		return SUCCESS;
	}

	private int transferCBFiles(String cbId) {
		JSONArray jsArray = new JSONArray(ChargebackUtilities.getChargebackTempChat(getPayId(), false));
		JSONObject tempObject;

		JSONArray finalCbChatArray = new JSONArray();
		JSONObject finalCbChatObject;

		String filePath = PropertiesManager.propertiesMap.get(Constants.CHARGEBACK_DOC_UPLOAD_PATH.getValue());
		String tempFilePath = filePath + getPayId() + "/temp/";
		filePath += getPayId() + "/" + cbId + "/";
		for (int i = 0; i < jsArray.length(); ++i) {
			tempObject = (JSONObject) jsArray.get(i);
			if (!(tempObject.getString("filename").equalsIgnoreCase(""))) {
				File file = new File(tempFilePath + tempObject.getString("completefilename"));
				if (file.exists()) {
					
					String completeFileName = tempObject.getString("completefilename");
					finalCbChatObject = new JSONObject(tempObject);	
					File destFile = new File(filePath);
					if (!destFile.exists()) {
						destFile.mkdirs();
					}
					
					destFile = new File(filePath + completeFileName);
					destFile.setExecutable(false);
					destFile.setWritable(false);
					
					System.out.println("Source file : " + file);
					System.out.println("Destination file : " + destFile);
					
					try {
						FileUtils.copyFile(file, destFile);
						finalCbChatArray.put(tempObject);
						System.out.println("Transfered file successfully and deleting...");
						file.delete();
					} catch (IOException e) {
						System.out.println("Unable to copy file...");
						e.printStackTrace();
						return 400;
					}

				} else {
					System.out.println("File doesn't exists: " + tempFilePath);
				}
			} else {
				// Add messsage object.
//				tempObject.put("message", encodeHtmlToString(tempObject.getString("message")));
				finalCbChatArray.put(tempObject);
			}
		}
		finalChargebackChat = finalCbChatArray.toString();
		return 200;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}

	@Override
	public void validate() {
//		private Date cbRaiseDate;   // Date given by bank. // Check later...
//		private BigDecimal amount;  // Validate amount.

		if (validator.validateBlankField(getCbRaiseDate())) {

		}

		if (validator.validateBlankField(getTargetDate())) {
		} else if (!validator.validateField(CrmFieldType.TARGET_DATE, getTargetDate())) {
			addFieldError(CrmFieldType.TARGET_DATE.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
		}

		if (validator.validateBlankField(getCreateDate())) {
		} else if (!validator.validateField(CrmFieldType.CREATE_DATE, getCreateDate())) {
			addFieldError(CrmFieldType.CREATE_DATE.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
		}

		// Whether to allow hyphen in field type..
		if (validator.validateBlankField(getChargebackType())) {
		} else if (!validator.validateField(CrmFieldType.CHARGEBACK_TYPE, getChargebackType())) {
			addFieldError(CrmFieldType.CHARGEBACK_TYPE.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
		}

		if (validator.validateBlankField(getTransactionId())) {
		} else if (!validator.validateField(CrmFieldType.TRANSACTION_ID, getTransactionId())) {
			addFieldError(CrmFieldType.TRANSACTION_ID.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
		}

		// Chargeback chat can contain any character...what will happen if we don't give
		// any special character mapping.
		if (validator.validateBlankField(getChargebackChat())) {
		} else if (!validator.validateField(CrmFieldType.CHARGEBACK_CHAT, getChargebackChat())) {
			addFieldError(CrmFieldType.CHARGEBACK_CHAT.getName(), ErrorType.INVALID_FIELD.getResponseCode());
		}

		if (validator.validateBlankField(getTxnDate())) {
		} else if (!validator.validateField(CrmFieldType.TXN_DATE, getTxnDate())) {
			addFieldError(CrmFieldType.TXN_DATE.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
		}

		if (validator.validateBlankField(getPgRefNum())) {
		} else if (!validator.validateField(CrmFieldType.PG_REF_NUM, getPgRefNum())) {
			addFieldError(CrmFieldType.PG_REF_NUM.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
		}

		if (validator.validateBlankField(getPayId())) {
		} else if (!validator.validateField(CrmFieldType.PAY_ID, getPayId())) {
			addFieldError(CrmFieldType.PAY_ID.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
		}

		if (validator.validateBlankField(getOrderId())) {
		} else if (!validator.validateField(CrmFieldType.ORDER_ID, getOrderId())) {
			addFieldError(CrmFieldType.ORDER_ID.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
		}

		// Acquirer Id can be null as per requirement given by Shivani.
		/*if (validator.validateBlankField(getAcqId())) {
		} else if (!validator.validateField(CrmFieldType.ACQ_ID, getAcqId())) {
			addFieldError(CrmFieldType.ACQ_ID.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
		}*/
		// Blank caseId is not allowed.
		if (validator.validateBlankField(getCaseId())) {
		} else if (!validator.validateField(CrmFieldType.CASE_ID, getOrderId())) {
			addFieldError(CrmFieldType.CASE_ID.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
		}

//		if (!validator.validateField(CrmFieldType.CUST_EMAIL_ID, getCustEmail())) {
//			addFieldError(CrmFieldType.CUST_EMAIL_ID.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
//		}
//		
//		if (!validator.validateField(CrmFieldType.CUST_PHONE, getCustPhone())) {
//			addFieldError(CrmFieldType.CUST_PHONE.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
//		}
	}

	@Override
	public String toString() {
		return "SHCreateChargebackAction [cbDao=" + cbDao + ", validator=" + validator + ", targetDate=" + targetDate
				+ ", cbRaiseDate=" + cbRaiseDate + ", createDate=" + createDate + ", chargebackType=" + chargebackType
				+ ", transactionId=" + transactionId + ", chargebackChat=" + chargebackChat + ", txnDate=" + txnDate
				+ ", pgRefNum=" + pgRefNum + ", payId=" + payId + ", orderId=" + orderId + ", acqId=" + acqId
				+ ", custEmail=" + custEmail + ", custPhone=" + custPhone + ", responseMessage=" + responseMessage
				+ ", responseCode=" + responseCode + ", amount=" + amount + ", pgTdr=" + pgTdr + ", acqTdr=" + acqTdr
				+ ", pgGst=" + pgGst + ", acqGst=" + acqGst + "]";
	}

	public String getTargetDate() {
		return targetDate;
	}

	public void setTargetDate(String targetDate) {
		this.targetDate = targetDate;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getChargebackType() {
		return chargebackType;
	}

	public void setChargebackType(String chargebackType) {
		this.chargebackType = chargebackType;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getTxnDate() {
		return txnDate;
	}

	public void setTxnDate(String txnDate) {
		this.txnDate = txnDate;
	}

	public String getPgRefNum() {
		return pgRefNum;
	}

	public void setPgRefNum(String pgRefNum) {
		this.pgRefNum = pgRefNum;
	}

	public String getPayId() {
		return payId;
	}

	public void setPayId(String payId) {
		this.payId = payId;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getAcqId() {
		return acqId;
	}

	public void setAcqId(String acqId) {
		this.acqId = acqId;
	}

	public String getCustEmail() {
		return custEmail;
	}

	public void setCustEmail(String custEmail) {
		this.custEmail = custEmail;
	}

	public String getCustPhone() {
		return custPhone;
	}

	public void setCustPhone(String custPhone) {
		this.custPhone = custPhone;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Date getCbRaiseDate() {
		return cbRaiseDate;
	}

	public void setCbRaiseDate(String cbRaiseDate) throws ParseException {
		Date date1 = new SimpleDateFormat("dd-MM-yyyy").parse(cbRaiseDate);
		this.cbRaiseDate = date1;
	}

	public String getChargebackChat() {
		return chargebackChat;
	}

	public void setChargebackChat(String chargebackChat) {
		this.chargebackChat = chargebackChat;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	public BigDecimal getPgTdr() {
		return pgTdr;
	}

	public void setPgTdr(BigDecimal pgTdr) {
		this.pgTdr = pgTdr;
	}

	public BigDecimal getAcqTdr() {
		return acqTdr;
	}

	public void setAcqTdr(BigDecimal acqTdr) {
		this.acqTdr = acqTdr;
	}

	public BigDecimal getPgGst() {
		return pgGst;
	}

	public void setPgGst(BigDecimal pgGst) {
		this.pgGst = pgGst;
	}

	public BigDecimal getAcqGst() {
		return acqGst;
	}

	public void setAcqGst(BigDecimal acqGst) {
		this.acqGst = acqGst;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public String getCaseId() {
		return caseId;
	}

	public void setCaseId(String caseId) {
		this.caseId = caseId;
	}

	public String getMopType() {
		return mopType;
	}

	public void setMopType(String mopType) {
		this.mopType = mopType;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}

	public String getTxnStatus() {
		return txnStatus;
	}

	public void setTxnStatus(String txnStatus) {
		this.txnStatus = txnStatus;
	}

	public String getCardMask() {
		return cardMask;
	}

	public void setCardMask(String cardMask) {
		this.cardMask = cardMask;
	}

	public String getInternalCardIssuerBank() {
		return internalCardIssuerBank;
	}

	public void setInternalCardIssuerBank(String internalCardIssuerBank) {
		this.internalCardIssuerBank = internalCardIssuerBank;
	}

	public String getInternalCardIssuerCountry() {
		return internalCardIssuerCountry;
	}

	public void setInternalCardIssuerCountry(String internalCardIssuerCountry) {
		this.internalCardIssuerCountry = internalCardIssuerCountry;
	}

	public String getCustCountry() {
		return custCountry;
	}

	public void setCustCountry(String custCountry) {
		this.custCountry = custCountry;
	}

	public String getInternalCustIp() {
		return internalCustIp;
	}

	public void setInternalCustIp(String internalCustIp) {
		this.internalCustIp = internalCustIp;
	}

}
