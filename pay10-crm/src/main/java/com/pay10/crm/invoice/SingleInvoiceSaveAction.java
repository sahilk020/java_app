package com.pay10.crm.invoice;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.ModelDriven;
import com.pay10.commons.audittrail.ActionMessageByAction;
import com.pay10.commons.audittrail.AuditTrail;
import com.pay10.commons.audittrail.AuditTrailService;
import com.pay10.commons.dao.InvoiceDao;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.Invoice;
import com.pay10.commons.user.Merchants;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.BinCountryMapperType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.Currency;
import com.pay10.crm.action.AbstractSecureAction;
import com.pay10.crm.actionBeans.CurrencyMapProvider;
import com.pay10.crm.invoice.service.InvoiceEmailService;
import com.pay10.crm.invoice.service.InvoiceSmsService;
import com.pay10.crm.invoice.service.SingleInvoiceService;
import com.pay10.crm.invoice.service.TncTrailReportService;

public class SingleInvoiceSaveAction extends AbstractSecureAction implements ModelDriven<Invoice> {
	/**
	 * @author shubhamchauhan
	 */
	private static final long serialVersionUID = -2648806205674741112L;

	private static Logger logger = LoggerFactory.getLogger(SingleInvoiceSaveAction.class.getName());

	@Autowired
	private CrmValidator validator;

	@Autowired
	SingleInvoiceService invoiceService;

	@Autowired
	InvoiceDao invoiceDao;

	@Autowired
	private InvoiceEmailService invoiceEmailService;

	@Autowired
	private InvoiceSmsService invoiceSmsService;

	private List<Merchants> merchantList = new ArrayList<>();
	private Map<String, String> currencyMap = new LinkedHashMap<>();

	private Invoice invoice = new Invoice();

	private String merchantPayId;

	@Autowired
	UserDao userDao;

	@Autowired
	private AuditTrailService auditTrailService;
	
	//Added By Sweety

	@Autowired
	TncTrailReportService tncService;
	private boolean tncStatus;

	@SuppressWarnings("unchecked")
	@Override
	public String execute() {
		logger.info("Saving invoice");
		/*
		 * CurrencyMapProvider currencyMapProvider = new CurrencyMapProvider();
		 * 
		 * if (sessionUser.getUserType().equals(UserType.ADMIN) ||
		 * sessionUser.getUserType().equals(UserType.SUBADMIN) ||
		 * sessionUser.getUserType().equals(UserType.RESELLER) ||
		 * sessionUser.getUserType().equals(UserType.SUPERADMIN)) { merchantList =
		 * userDao.getMerchantActiveList(); currencyMap =
		 * currencyMapProvider.currencyMap(sessionUser); } else { currencyMap =
		 * Currency.getSupportedCurreny(sessionUser); if
		 * (sessionUser.getUserType().equals(UserType.SUBUSER)) { Merchants merchant =
		 * new Merchants(); String parentPayId = sessionUser.getParentPayId(); User
		 * parentUser = userDao.findPayId(parentPayId);
		 * merchant.setMerchant(parentUser); merchantList.add(merchant); currencyMap =
		 * Currency.getSupportedCurreny(parentUser);
		 * invoice.setPayId(sessionUser.getParentPayId());
		 * invoice.setBusinessName(userDao.getMerchantByPayId(sessionUser.getParentPayId
		 * ()));
		 * 
		 * } else if (sessionUser.getUserType().equals(UserType.MERCHANT)) {
		 * invoice.setPayId(sessionUser.getPayId());
		 * invoice.setBusinessName(userDao.getMerchantByPayId(sessionUser.getPayId()));
		 * Merchants merchant = new Merchants();
		 * merchant.setEmailId(sessionUser.getEmailId());
		 * merchant.setPayId(sessionUser.getPayId());
		 * merchant.setBusinessName(sessionUser.getBusinessName());
		 * merchantList.add(merchant); } }
		 */
		try {
			invoice.setPayId(getMerchantPayId());
			//invoice.setPayId(invoice.getPayId());
			String status = invoiceService.createInvoiceService(invoice);
			
			//Added By Sweety To save invoice details in other collection if tnc enabled
			logger.info("merchantConsent...={}",invoice.isMerchantConsent());
			if(invoice.isMerchantConsent()) {
				tncService.createTrailReport(invoice);
				
			}

			Map<String, ActionMessageByAction> actionMessagesByAction = (Map<String, ActionMessageByAction>) sessionMap
					.get(Constants.ACTION_MESSAGE_BY_ACTION.getValue());
			AuditTrail auditTrail = new AuditTrail(mapper.writeValueAsString(invoice), null,
					actionMessagesByAction.get("saveSingleInvoice"));
			auditTrailService.saveAudit(request, auditTrail);

			String invoiceCreationError = "Single Invoice Create Error : " + status;
			String msg;
			if (status.equals("INVSIN99")) {
				msg = "Invalid Total Amount";
				addActionMessage(msg);
				logger.error(invoiceCreationError + " : " + msg);
			} else if (status.equals("INVSIN100")) {
				msg = "Please enter mandatory fields";
				addActionMessage(msg);
				logger.error(invoiceCreationError + " : " + msg);
			} else if (status.equals("INVSIN101")) {
				msg = "Please enter unique invoice number";
				addActionMessage(msg);
				logger.error(invoiceCreationError + " : " + msg);
			} else if (status.equals("INVSIN102")) {
				msg = "Failed to create invoice";
				addActionMessage(msg);
				logger.error(invoiceCreationError + " : " + msg);
			} else if (status.equals("INVSIN200")) {
				addActionMessage("User Invoice saved and link shared successfully with User");
				logger.info("Successfully created invoice : " + status);
			} else {
				logger.error("Unknown error occured while saving invoice.");
			}

			return SUCCESS;
		} catch (Exception ex) {
			logger.error("Exception", ex);
			return ERROR;
		}
	}

	@SkipValidation
	public String invoiceSMS() {
		try {
			// custom validation
			if ((validator.validateBlankField(invoice.getInvoiceId()))) {
				addActionError(validator.getResonseObject().getResponseMessage());
				return ERROR;
			} else if (!(validator.validateField(CrmFieldType.INVOICE_ID, invoice.getInvoiceId()))) {
				return ERROR;
			}
			Invoice invoiceDB = invoiceDao.findByInvoiceId(invoice.getInvoiceId());
			// Why below code was written.

//			setUrl(propertyManager.getSystemProperty(CrmFieldConstants.INVOICE_URL.getValue()) + invoiceDB.getInvoiceId());
//			invoiceTransactionDao.update(invoiceDB);
			invoiceEmailService.sendEmail(invoiceDB.getEmail(), invoiceDB.getShortUrl(), invoiceDB.getInvoiceUrl(),
					invoiceDB.getName(), invoiceDB.getBusinessName());
			invoiceSmsService.sendSms(invoiceDB.getPhone(), invoiceDB.getShortUrl(), invoiceDB.getInvoiceUrl(),
					invoiceDB.getBusinessName(), invoiceDB.getName());
			return SUCCESS;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void validate() {
		User sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		CurrencyMapProvider currencyMapProvider = new CurrencyMapProvider();

		if (sessionUser.getUserType().equals(UserType.ADMIN) || sessionUser.getUserType().equals(UserType.SUBADMIN)
				|| sessionUser.getUserType().equals(UserType.RESELLER)
				|| sessionUser.getUserType().equals(UserType.SUPERADMIN)) {
			//merchantList = userDao.getMerchantActiveList();
			merchantList = userDao.getMerchantActiveList(sessionMap.get(Constants.SEGMENT.getValue()).toString(), sessionUser.getRole().getId());
			currencyMap = currencyMapProvider.currencyMap(sessionUser);
		} else {
			currencyMap = Currency.getSupportedCurreny(sessionUser);
			if (sessionUser.getUserType().equals(UserType.SUBUSER)) {
				Merchants merchant = new Merchants();
				String parentPayId = sessionUser.getParentPayId();
				User parentUser = userDao.findPayId(parentPayId);
				merchant.setMerchant(parentUser);
				merchantList.add(merchant);
				currencyMap = Currency.getSupportedCurreny(parentUser);
				invoice.setPayId(sessionUser.getParentPayId());
				invoice.setBusinessName(userDao.getMerchantByPayId(sessionUser.getParentPayId()));

			} else if (sessionUser.getUserType().equals(UserType.MERCHANT)) {
				invoice.setPayId(sessionUser.getPayId());
				invoice.setBusinessName(userDao.getMerchantByPayId(sessionUser.getPayId()));
				Merchants merchant = new Merchants();
				merchant.setEmailId(sessionUser.getEmailId());
				merchant.setPayId(sessionUser.getPayId());
				merchant.setBusinessName(sessionUser.getBusinessName());
				merchantList.add(merchant);
			}
		}

		if ((validator.validateBlankField(invoice.getInvoiceNo()))
				|| (!(validator.validateField(CrmFieldType.INVOICE_NUMBER, invoice.getInvoiceNo())))) {
			logger.error("Invalid invoice number : " + invoice.getInvoiceNo());
			addActionMessage("Invalid Invoice Number");
			addFieldError(CrmFieldType.INVOICE_NUMBER.getName(), validator.getResonseObject().getResponseMessage());
		}

		if ((validator.validateBlankField(invoice.getAmount()))
				|| (!(validator.validateField(CrmFieldType.INVOICE_AMOUNT, invoice.getAmount())))) {
			logger.error("Invalid invoice amount : " + invoice.getAmount());
			addActionMessage("Invalid Invoice Amount");
			addFieldError(CrmFieldType.INVOICE_AMOUNT.getName(), validator.getResonseObject().getResponseMessage());
		}

		if ((validator.validateBlankField(invoice.getGst()))) {
			invoice.setGst("0");
		} else if (!(validator.validateField(CrmFieldType.INVOICE_GST, invoice.getGst()))) {
			logger.error("Invalid invoice GST : " + invoice.getGst());
			addActionMessage("Invalid GST");
			addFieldError(CrmFieldType.INVOICE_GST.getName(), validator.getResonseObject().getResponseMessage());
		}

		if ((validator.validateBlankField(invoice.getTotalAmount()))
				|| (!(validator.validateField(CrmFieldType.INVOICE_AMOUNT, invoice.getTotalAmount())))) {
			logger.error("Invalid invoice total amount : " + invoice.getTotalAmount());
			addActionMessage("Invalid Total Amount");
			addFieldError(CrmFieldType.INVOICE_SERVICE_CHARGE.getName(),
					validator.getResonseObject().getResponseMessage());
		}

		if ((validator.validateBlankField(invoice.getAddress()))) {
		} else if (!(validator.validateField(CrmFieldType.INVOICE_ADDRESS, invoice.getAddress()))) {
			logger.error("Invalid invoice address : " + invoice.getAddress());
			addActionMessage("Invalid Address");
			addFieldError(CrmFieldType.INVOICE_ADDRESS.getName(), validator.getResonseObject().getResponseMessage());
		}

		if ((validator.validateBlankField(invoice.getProductName()))) {
		} else if (!(validator.validateField(CrmFieldType.INVOICE_PRODUCT_NAME, invoice.getProductName()))) {
			logger.error("Invalid invoice product name : " + invoice.getProductName());
			addActionMessage("Invalid Product Name");
			addFieldError(CrmFieldType.INVOICE_PRODUCT_NAME.getName(),
					validator.getResonseObject().getResponseMessage());
		}
		if ((validator.validateBlankField(invoice.getQuantity()))
				|| (!(validator.validateField(CrmFieldType.INVOICE_QUANTITY, invoice.getQuantity())))) {
			logger.error("Invalid invoice quantity : " + invoice.getQuantity());
			addActionMessage("Invalid Quantity");
			addFieldError(CrmFieldType.INVOICE_QUANTITY.getName(), validator.getResonseObject().getResponseMessage());
		}
		if ((validator.validateBlankField(invoice.getReturnUrl()))) {
		} else if (!(validator.validateField(CrmFieldType.INVOICE_RETURN_URL, invoice.getReturnUrl()))) {
			logger.error("Invalid invoice return url : " + invoice.getReturnUrl());
			addActionMessage("Invalid Return URL");
			addFieldError(CrmFieldType.INVOICE_RETURN_URL.getName(), validator.getResonseObject().getResponseMessage());
		}
		if ((validator.validateBlankField(invoice.getProductDesc()))) {
		} else if (!(validator.validateField(CrmFieldType.INVOICE_PRODUCT_DESCRIPTION, invoice.getProductDesc()))) {
			logger.error("Invalid invoice product description : " + invoice.getProductDesc());
			addActionMessage("Invalid Product Description");
			addFieldError(CrmFieldType.INVOICE_PRODUCT_DESCRIPTION.getName(),
					validator.getResonseObject().getResponseMessage());
		}
		if (invoice.getCurrencyCode().equals(CrmFieldConstants.SELECT_CURRENCY.getValue())
				|| validator.validateBlankField(invoice.getCurrencyCode())
				|| (!(validator.validateField(CrmFieldType.INVOICE_CURRENCY_CODE, invoice.getCurrencyCode())))) {
			logger.error("Invalid invoice currency code : " + invoice.getCurrencyCode());
			addActionMessage("Invalid Currency Code");
			addFieldError(CrmFieldType.INVOICE_CURRENCY_CODE.getName(), CrmFieldConstants.SELECT_CURRENCY.getValue());
		}
		if (invoice.getExpiresDay().isEmpty()) {
			if (invoice.getExpiresHour().isEmpty()) {
				logger.error("Invalid invoice expiry hour : " + invoice.getExpiresHour());
				logger.error("Invalid invoice expiry day : " + invoice.getExpiresDay());
				addActionMessage("Invalid Expiry Day or Expiry Hour");
				addFieldError(CrmFieldType.INVOICE_EXPIRES_DAY.getName(),
						validator.getResonseObject().getResponseMessage());
				addFieldError(CrmFieldType.INVOICE_EXPIRES_HOUR.getName(),
						ErrorType.INVALID_FIELD_VALUE.getResponseMessage());
			} else {
				invoice.setExpiresDay("0");
			}
		} else if (invoice.getExpiresHour().isEmpty()) {
			invoice.setExpiresHour("0");
		} else if ((Integer.parseInt(invoice.getExpiresDay()) == 0 && Integer.parseInt(invoice.getExpiresHour()) == 0)
				|| (Integer.parseInt(invoice.getExpiresDay()) < 0 || Integer.parseInt(invoice.getExpiresHour()) < 0)) {
			logger.error("Invalid invoice expiry hour : " + invoice.getExpiresHour());
			logger.error("Invalid invoice expiry day : " + invoice.getExpiresDay());
			addActionMessage("Invalid Expiry Day or Expiry Hour");
			addFieldError(CrmFieldType.INVOICE_EXPIRES_DAY.getName(),
					validator.getResonseObject().getResponseMessage());
			addFieldError(CrmFieldType.INVOICE_EXPIRES_HOUR.getName(),
					validator.getResonseObject().getResponseMessage());
		}
	}

	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

	@Override
	public Invoice getModel() {
		return invoice;
	}

	// to provide default country
	public String getDefaultCountry() {
		if (StringUtils.isBlank(invoice.getCountry())) {
			return BinCountryMapperType.INDIA.getName();
		} else {
			return invoice.getCountry();
		}
	}

	public List<Merchants> getMerchantList() {
		return merchantList;
	}

	public void setMerchantList(List<Merchants> merchantList) {
		this.merchantList = merchantList;
	}

	public Map<String, String> getCurrencyMap() {
		return currencyMap;
	}

	public void setCurrencyMap(Map<String, String> currencyMap) {
		this.currencyMap = currencyMap;
	}

	public String getMerchantPayId() {
		return merchantPayId;
	}

	public void setMerchantPayId(String merchantPayId) {
		this.merchantPayId = merchantPayId;
	}
	public boolean isTncStatus() {
		return tncStatus;
	}

	public void setTncStatus(boolean tncStatus) {
		this.tncStatus = tncStatus;
	}
}
