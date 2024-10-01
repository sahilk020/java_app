package com.pay10.crm.action;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.api.TransactionControllerServiceProvider;
import com.pay10.commons.dao.BeneficiaryAccountsDao;
import com.pay10.commons.dao.NodalAccountDetailsDao;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.BeneficiaryAccounts;
import com.pay10.commons.user.NodalAccountDetails;
import com.pay10.commons.user.User;
import com.pay10.commons.util.AccountEntryType;
import com.pay10.commons.util.AccountStatus;
import com.pay10.commons.util.AccountType;
import com.pay10.commons.util.Amount;
import com.pay10.commons.util.BeneficiaryTypes;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.CurrencyTypes;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.MacUtil;
import com.pay10.commons.util.NodalPaymentTypes;
import com.pay10.commons.util.SettlementTransactionType;
import com.pay10.commons.util.TransactionManager;

/**
 * 
 * @author shubhamchauhan
 *
 */
public class PGNodalTransactionsAction extends AbstractSecureAction implements ServletRequestAware {

	private static final long serialVersionUID = -7707536253683966474L;

	@Autowired
	private CrmValidator validator;

	@Autowired
	private BeneficiaryAccountsDao beneficiaryAccountsDao;

	@Autowired
	private TransactionControllerServiceProvider transactionControllerServiceProvider;
	
	@Autowired
	private NodalAccountDetailsDao nodalAccountDetailsDao;
	
	@Autowired
	private MacUtil macUtil;

	private static Logger logger = LoggerFactory.getLogger(PGNodalTransactionsAction.class.getName());

//	private String acquirer;
	private HttpServletRequest request;
	private String response;
	private User sessionUser = new User();

	private String paymentType;
	private String comments;
	private String amount;
//	private String merchantPayId;
	private String merchantProvidedId;

	@Override
	public String execute() {
//		setMerchantPayId("1001400123153345");
		sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		logger.info("Inside NodalTransactionsAction");

		try {
			List<BeneficiaryAccounts> beneficiaryFromDbList = beneficiaryAccountsDao
					.findByMerchantPvdCdAndStatusAndPaymentTypeAndPayId(getMerchantProvidedId(), null, getPaymentType(), AccountStatus.ACTIVE.getName());
			
			if(beneficiaryFromDbList.size() != 1) {
				logger.info("Beneficiary entries found more or less than 1.");
				setResponse("None or multiple entries found in Database for beneficiary : " + getMerchantProvidedId());
				return SUCCESS;
			}
			
			BeneficiaryAccounts beneficiaryFromDb = beneficiaryFromDbList.get(0);
			
			if (beneficiaryFromDb == null || StringUtils.isBlank(beneficiaryFromDb.getBeneficiaryCd())) {
				logger.info("Benefiary Code not found in database !");
				setResponse("Benefiary Code not found in database !");
				return SUCCESS;
			}
			
			if(!(beneficiaryFromDb.getBeneType().equals(BeneficiaryTypes.PG.getCode()))) {
				logger.info("Invalid Beneficiary type : " + beneficiaryFromDb.getBeneType());
				logger.info("Transaction can only be made to PG type beneficiaries.");
				setResponse("Invalid Beneficiary type.");
				return SUCCESS;
			}
			
			// Check whether Merchant is active or not.
//			User user = userDao.findPayId(getMerchantPayId());
//			if(user == null) {
//				logger.info("Merchant not found on beneficiary code : " + getMerchantProvidedId());
//				setResponse("Merchant not found.");
//				return SUCCESS;
//			} else if(!(user.getUserStatus().equals(UserStatusType.ACTIVE))) {
//				logger.info("Merchant not active on beneficiary code : " + getMerchantProvidedId());
//				setResponse("Merchant not active");
//				return SUCCESS;
//			}
			
			String resellerId = "";
			String tenantId = "";
			
//			if(StringUtils.isNotBlank(user.getResellerId())) {
//				resellerId = user.getResellerId();
//			}
//			
//			if(StringUtils.isNotBlank(String.valueOf(user.getTenantId()))) {
//				tenantId = String.valueOf(user.getTenantId());
//			}

			// Check whether beneficiary has expired or not.
			Date d1 = null;
			if (StringUtils.isNotBlank(beneficiaryFromDb.getBeneExpiryDate())) {
				SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd");
				try {
					d1 = sdformat.parse(beneficiaryFromDb.getBeneExpiryDate());
				} catch (ParseException e) {
					logger.error("Unable to parse beneficiary expiry date.");
					logger.error(e.getMessage());
					setResponse("Failed to initate transaction.");
					return SUCCESS;
				}
				DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
				LocalDateTime now = LocalDateTime.now();
				Date d2 = sdformat.parse(dtf.format(now));

				if (d1 != null && d1.compareTo(d2) < 0) {
					List<BeneficiaryAccounts> list = beneficiaryAccountsDao
							.findByBeneficiaryCdAndStatusAndPaymentTypeAndPayId(beneficiaryFromDb.getBeneficiaryCd(),
									beneficiaryFromDb.getMerchantPayId(), "ALL", "ALL");
					for (BeneficiaryAccounts beneaAccount : list) {
						beneaAccount.setStatus(AccountStatus.EXPIRE);
						beneficiaryAccountsDao.update(beneaAccount);
						logger.info("Beneficiary Expired : " + beneaAccount.getMerchantProvidedId() + " : PayId "
								+ beneaAccount.getMerchantPayId() + " : Payment Type : " + beneaAccount.getPaymentType());
					}
					setResponse("Beneficiary Expired for " + beneficiaryFromDb.getMerchantProvidedId() + ". Please Add beneficiary again and initiate the transaction.");
					return SUCCESS;
				}
			}
			
			NodalAccountDetails nodalAccountDetails = nodalAccountDetailsDao.find(beneficiaryFromDb.getAcquirer());
			
			if(nodalAccountDetails == null) {
				logger.info("Nodal Account details not found for beneficiary code : " + merchantProvidedId);
				setResponse("Nodal Account details not found for beneficiary code : " + merchantProvidedId);
				return SUCCESS;
			}
			
			Fields fields = new Fields();
			
			fields.put(FieldType.PG_TDR_SC.getName(), "0");
			fields.put(FieldType.ACQUIRER_TDR_SC.getName(), "0");
			fields.put(FieldType.PG_GST.getName(), "0");
			fields.put(FieldType.ACQUIRER_GST.getName(), "0");
			fields.put(FieldType.TOTAL_AMOUNT.getName(), amount);
			fields.put(FieldType.TOTAL_TDR_SC.getName(), "0");
			fields.put(FieldType.AMOUNT.getName(), amount);
			// Apply surcharge.
			String surchargeFlag = Constants.N_FLAG.getValue(); // Set 'N' during settlement via admin.
			
			// Uncomment when applying surcharge
//			if(surchargeFlag.equals(Constants.Y_FLAG.getValue())) {
//				nodalSurcharge.getNodalSurcharge(fields);
//			}

			CurrencyTypes currencyTypes = CurrencyTypes.getInstancefromCode(beneficiaryFromDb.getCurrencyCd().getCode());
			String amountInPaise = Amount.formatAmount(amount, currencyTypes.getName());

			fields.put(FieldType.PAY_ID.getName(), beneficiaryFromDb.getMerchantPayId());
			fields.put(FieldType.ORDER_ID.getName(), "PG" + TransactionManager.getNewTransactionId());
			fields.put(FieldType.CUST_ID_BENEFICIARY.getName(), beneficiaryFromDb.getCustId());
			fields.put(FieldType.BENEFICIARY_CD.getName(), beneficiaryFromDb.getBeneficiaryCd());
			fields.put(FieldType.BENE_NAME.getName(), beneficiaryFromDb.getBeneName());
			fields.put(FieldType.BENE_ACCOUNT_NO.getName(), beneficiaryFromDb.getBeneAccountNo());
			fields.put(FieldType.BENE_ACCOUNT_TYPE.getName(), AccountType.BENEFICIARY.getName());
			fields.put(FieldType.BENE_USER_TYPE.getName(), beneficiaryFromDb.getBeneType());
			fields.put(FieldType.BENE_MERCHANT_PROVIDED_ID.getName(), beneficiaryFromDb.getMerchantProvidedId());
			fields.put(FieldType.BENE_MERCHANT_PROVIDED_NAME.getName(), beneficiaryFromDb.getMerchantProvidedName());
			fields.put(FieldType.BENE_MERCHANT_BUSINESS_NAME.getName(), beneficiaryFromDb.getMerchantBusinessName());
			fields.put(FieldType.BENE_BANK_NAME.getName(), beneficiaryFromDb.getBankName());
			fields.put(FieldType.BENE_IFSC.getName(), beneficiaryFromDb.getIfscCode());
			fields.put(FieldType.IFSC_CODE.getName(), beneficiaryFromDb.getIfscCode());
			
			fields.put(FieldType.PAYMENT_TYPE.getName(), beneficiaryFromDb.getPaymentType().getName());
			fields.put(FieldType.CURRENCY_CD.getName(), currencyTypes.getCode());
			fields.put(FieldType.CURRENCY_CODE.getName(), currencyTypes.getName());
			fields.put(FieldType.NODAL_ACQUIRER.getName(), beneficiaryFromDb.getAcquirer());
			fields.put(FieldType.SRC_ACCOUNT_NO.getName(), beneficiaryFromDb.getSrcAccountNo());
			
			fields.put(FieldType.TXNTYPE.getName(), SettlementTransactionType.FUND_TRANSFER.getName());
			fields.put(FieldType.PRODUCT_DESC.getName(), getComments());
			fields.put(FieldType.AMOUNT.getName(), amountInPaise);
			
			fields.put(FieldType.REQUESTED_BY.getName(), sessionUser.getEmailId());
			fields.put(FieldType.PROCESSED_BY.getName(), sessionUser.getEmailId());
			fields.put(FieldType.SURCHARGE_FLAG.getName(), surchargeFlag); 
			fields.put(FieldType.PAYMENTS_REGION.getName(), Constants.DOMESTIC_PAYMENTS_REGION.getValue());
			fields.put(FieldType.TENANT_ID.getName(), tenantId);
			fields.put(FieldType.RESELLER_ID.getName(), resellerId);
			
			// remitter details
			fields.put(FieldType.REMI_ACCOUNT_IFSC.getName(), nodalAccountDetails.getIfscCode());
			fields.put(FieldType.REMI_ACCOUNT_NO.getName(), nodalAccountDetails.getAccountNumber());
			fields.put(FieldType.REMI_ACCOUNT_TYPE.getName(), AccountType.NODAL.getName());
			fields.put(FieldType.REMI_NAME.getName(), nodalAccountDetails.getAccountHolderName());
			fields.put(FieldType.REMI_COMMENTS.getName(), AccountEntryType.DEBIT.getName());
			fields.put(FieldType.REMI_USER_TYPE.getName(), BeneficiaryTypes.PG.getCode());
			fields.put(FieldType.REMI_ADDRESS.getName(), nodalAccountDetails.getAddress());
			fields.put(FieldType.COMMENTS.getName(), getComments());
			fields.put(FieldType.REMI_COMMENTS.getName(), "Nodal settlement to PG bank account");
			fields.put(FieldType.RETURNED_AT.getName(), "");
			fields.put(FieldType.NODAL_ACCOUNT_NO.getName(), beneficiaryFromDb.getSrcAccountNo());
			fields.put(FieldType.ACCOUNT_ENTRY_TYPE.getName(), AccountEntryType.DEBIT.getName());
			fields.put(FieldType.ACQ_ID.getName(), "");
			fields.put(FieldType.ACQUIRER_TYPE.getName(), beneficiaryFromDb.getAcquirer());
			fields.put(FieldType.REQUEST_CHANNEL.getName(), "CRM");
			logger.info("Internal_Cust_IP for X-Forwarded-For: " + request.getHeader("X-Forwarded-For"));
			logger.info("Internal_Cust_IP for Remote Addr: " + request.getRemoteAddr());
			
			String ip = request.getHeader("X-Forwarded-For");
			String mac = StringUtils.isBlank(ip) ? "NA" : macUtil.getMackByIp(ip);
			fields.put((FieldType.INTERNAL_CUST_IP.getName()), ip);
			fields.put((FieldType.INTERNAL_CUST_MAC.getName()), mac);
			String domain ="NA";
			if(StringUtils.isNotBlank(request.getHeader("Referer"))) {
				try {
					domain = new URL(request.getHeader("Referer")).getHost(); 
					logger.info("Internal_Cust_Domain: domain name is " + domain +" request referer is " +request.getHeader("Referer"));															
				}catch(Exception e) {
					domain ="NA";
				}
			}
			if(!StringUtils.isEmpty(beneficiaryFromDb.getEmailId())) {
				fields.put(FieldType.CUST_EMAIL.getName(), beneficiaryFromDb.getEmailId());
			}else {
				fields.put(FieldType.CUST_EMAIL.getName(), "");
			}
			
			if(!StringUtils.isEmpty(beneficiaryFromDb.getMobileNo())) {
				fields.put(FieldType.CUST_PHONE.getName(), beneficiaryFromDb.getMobileNo());
			}else {
				fields.put(FieldType.CUST_PHONE.getName(), "");
			}
			
			logger.info("Inside NodalTransactionsAction fields = " + fields.getFieldsAsString());

			Map<String, String> responseMap = transactionControllerServiceProvider.nodalSettlementTransact(fields);

			String responseMessage = "";
			String transactionId = "";
			String pgTxnMessage = "";
			if (StringUtils.isNotBlank(responseMap.get(FieldType.RESPONSE_MESSAGE.getName()))) {
				responseMessage = responseMap.get(FieldType.RESPONSE_MESSAGE.getName());
			} else {
				responseMessage = "REJECTED";
			}
			
			if (StringUtils.isNotBlank(responseMap.get(FieldType.PG_TXN_MESSAGE.getName()))) {
				pgTxnMessage = responseMap.get(FieldType.PG_TXN_MESSAGE.getName());
			}
			logger.info("Inside Nodal Transaction , response message = " + responseMessage + " : Order Id" + responseMap.get(FieldType.ORDER_ID.getName()));
			logger.info("Inside Nodal Transaction , response message = " + pgTxnMessage + " : Order Id" + responseMap.get(FieldType.ORDER_ID.getName()));
			if (StringUtils.isNotBlank(responseMap.get(FieldType.TXN_ID.getName()))) {
				transactionId = responseMap.get(FieldType.TXN_ID.getName());
			} else {
				transactionId = "NA";
			}

			logger.info("Inside Nodal Transactions action , initiate nodal transaction , transactionId  = "
					+ transactionId);

			if (responseMessage.equalsIgnoreCase(ErrorType.PROCESSING.getResponseMessage())) {
				setResponse("Payout request under process.");
			} else if (responseMessage.equalsIgnoreCase(ErrorType.REJECTED.getResponseMessage())) {
				setResponse("Nodal Transaction rejected. " + pgTxnMessage);
			} else if (responseMessage.equalsIgnoreCase(ErrorType.SETTLEMENT_SUCCESSFULL.getResponseMessage())) {
				setResponse("Nodal Transaction successful");
			} else {
				setResponse("Nodal Transaction failed. " + pgTxnMessage);
			}

		} catch (Exception e) {
			setResponse("Internal Server Error");
			logger.error("Exception in initiating nodal transaction.");
			logger.error(e.getMessage());
			return SUCCESS;
		}

		return SUCCESS;
	}

	// To display page without using token
	@SuppressWarnings("unchecked")
	public String displayList() {
		return INPUT;
	}

	@Override
	public void validate() {
		if (validator.validateBlankField(getMerchantProvidedId())) {
			addFieldError(CrmFieldType.BENE_MERCHANT_PROVIDED_ID.getName(), validator.getResonseObject().getResponseMessage());
			setResponse("Invalid Merchant Provided Code");
			return;
		} else if (!(validator.validateField(CrmFieldType.BENE_MERCHANT_PROVIDED_ID, getMerchantProvidedId()))) {
			addFieldError(CrmFieldType.BENE_MERCHANT_PROVIDED_ID.getName(), validator.getResonseObject().getResponseMessage());
			setResponse("Invalid Merchant Provided Code");
			return;
		}

		if (validator.validateBlankFields(getPaymentType())) {
			addFieldError(CrmFieldType.PAYMENT_TYPE.getName(), validator.getResonseObject().getResponseMessage());
			setResponse("Invalid Payment Type");
			return;
		} else if ((NodalPaymentTypes.getInstancefromName(getPaymentType()) == null)) {
			addFieldError(CrmFieldType.PAYMENT_TYPE.getName(), validator.getResonseObject().getResponseMessage());
			setResponse("Invalid Payment Type");
			return;
		}
		setPaymentType(NodalPaymentTypes.getInstancefromName(getPaymentType()).getCode());

		if (validator.validateBlankFields(getComments())) {
			addFieldError(CrmFieldType.BENE_COMMENT.getName(), validator.getResonseObject().getResponseMessage());
			setResponse("Comments field is mandatory");
			return;
		} else if (!(validator.validateField(CrmFieldType.BENE_COMMENT, getComments()))) {
			addFieldError(CrmFieldType.BENE_COMMENT.getName(), validator.getResonseObject().getResponseMessage());
			setResponse("Invalid Comments");
			return;
		} else if(!getComments().matches(".*[a-zA-Z0-9]+.*")) {
			addFieldError(CrmFieldType.BENE_COMMENT.getName(), ErrorType.INVALID_FIELD_VALUE.getResponseMessage());
			setResponse("Invalid Comments");
			return;
		}

		if(validator.validateBlankFields(getAmount())) {
			addFieldError(CrmFieldType.BENE_AMOUNT.getName(), validator.getResonseObject().getResponseMessage());
			setResponse("Amount Field is mandatory");	
		} else if(!validator.validateField(CrmFieldType.BENE_AMOUNT, getAmount())) {
			setResponse("Invalid Amount");
			addFieldError(CrmFieldType.BENE_AMOUNT.getName(), validator.getResonseObject().getResponseMessage());
		} else if(!(validator.validateAmountByYesBankNodalPaymentType(getPaymentType(), getAmount()))) {
			addFieldError(CrmFieldType.BENE_AMOUNT.getName(), validator.getResonseObject().getResponseMessage());
			setResponse("Invalid amount limit or payment type");
		}
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

//	public String getMerchantPayId() {
//		return merchantPayId;
//	}
//
//	public void setMerchantPayId(String merchantPayId) {
//		this.merchantPayId = merchantPayId;
//	}

	public String getMerchantProvidedId() {
		return merchantProvidedId;
	}

	public void setMerchantProvidedId(String merchantProvidedId) {
		this.merchantProvidedId = merchantProvidedId;
	}
	
	@Override
	public void setServletRequest(HttpServletRequest hReq) {
		this.request = hReq;
	}
}
