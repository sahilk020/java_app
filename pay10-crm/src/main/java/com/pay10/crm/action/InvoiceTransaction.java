package com.pay10.crm.action;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.io.filefilter.MagicNumberFileFilter;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.opensymphony.xwork2.ModelDriven;
import com.pay10.commons.api.EmailControllerServiceProvider;
import com.pay10.commons.api.ShortUrlProvider;
import com.pay10.commons.api.SmsControllerServiceProvider;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.kms.AWSEncryptDecryptService;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.user.Invoice;
import com.pay10.commons.user.InvoiceTransactionDao;
import com.pay10.commons.user.Merchants;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.Currency;
import com.pay10.commons.util.InvoiceExecutableSignature;
import com.pay10.commons.util.InvoiceStatus;
import com.pay10.commons.util.InvoiceType;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.TransactionManager;
import com.pay10.commons.util.UrlShortener;
import com.pay10.crm.actionBeans.BatchResponseObject;
import com.pay10.crm.actionBeans.CommanCsvReader;
import com.pay10.crm.actionBeans.SessionUserIdentifier;

/**
 * @author ISHA,CHANDAN,ROHIT
 *
 */
public class InvoiceTransaction extends AbstractSecureAction implements ModelDriven<Invoice> {

	@Autowired
	private CrmValidator validator;

	@Autowired
	private UrlShortener urlShortener;

	@Autowired
	private SessionUserIdentifier userIdentifier;

	@Autowired
	AWSEncryptDecryptService awsEncryptDecryptService;

	@Autowired
	private EmailControllerServiceProvider emailControllerServiceProvider;
	@Autowired
	private SmsControllerServiceProvider smsControllerServiceProvider;
	@Autowired
	private ShortUrlProvider shortUrlProvider;
	
	private static Logger logger = LoggerFactory.getLogger(InvoiceTransaction.class.getName());
	private static final long serialVersionUID = 3857047834665047987L;
	private File fileName;
	private byte[] magicNumber;
	private String executableDescription;
	private Invoice invoice = new Invoice();
	Random random = new Random();
	private String url;
	private String shortUrl;
	private String invoiceNo;
	private String invoiceReturnUrl;
	private String businessName;
	private boolean emailCheck;
	private String merchantPayId;
	private String invoiceId;
	private InvoiceStatus InvoiceStatus = com.pay10.commons.util.InvoiceStatus.UNPAID;
	private Map<String, String> currencyMap = new HashMap<String, String>();
	private UserDao userDao = new UserDao();
	private List<Merchants> merchantList = new ArrayList<Merchants>();
	private StringBuilder responseMessage = new StringBuilder();

	@Autowired
	private MongoInstance mongoInstance;
	private static final String PREFIX = "MONGO_DB_";

	@Override
	@SuppressWarnings({ "unchecked" })
	public String execute() {
		InvoiceTransactionDao invoiceTransactionDao = new InvoiceTransactionDao();

		try {
			User user = (User) sessionMap.get(Constants.USER.getValue());

			invoice.setInvoiceId(TransactionManager.getNewTransactionId());

		//	invoice.setCreateDate(new Date());

			if (user.getUserType().equals(UserType.ADMIN) || user.getUserType().equals(UserType.SUBADMIN)
					|| user.getUserType().equals(UserType.RESELLER) || user.getUserType().equals(UserType.SUPERADMIN)) {
				currencyMap = Currency.getAllCurrency();
				invoice.setPayId(getMerchantPayId());
				businessName = userDao.getMerchantByPayId(getMerchantPayId());
				invoice.setBusinessName(businessName);
				//merchantList = new UserDao().getActiveMerchantList();
				merchantList = new UserDao().getActiveMerchantList(sessionMap.get(Constants.SEGMENT.getValue()).toString(), user.getRole().getId());
			} else {
				currencyMap = Currency.getSupportedCurreny(user);
				if (user.getUserType().equals(UserType.SUBUSER)) {
					Merchants merchant = new Merchants();
					String parentPayId = user.getParentPayId();
					User parentUser = userDao.findPayId(parentPayId);
					merchant.setMerchant(parentUser);
					merchantList.add(merchant);
					currencyMap = Currency.getSupportedCurreny(parentUser);
					invoice.setPayId(user.getParentPayId());
					businessName = userDao.getMerchantByPayId(user.getParentPayId());
					invoice.setBusinessName(businessName);

				} else if (user.getUserType().equals(UserType.MERCHANT)) {
					invoice.setPayId(user.getPayId());
					businessName = userDao.getMerchantByPayId(user.getPayId());
					invoice.setBusinessName(businessName);
					Merchants merchant = new Merchants();
					merchant.setEmailId(user.getEmailId());
					merchant.setPayId(user.getPayId());
					merchant.setBusinessName(user.getBusinessName());
					merchantList.add(merchant);

				}
			}

			if (invoice.getInvoiceType().equals(InvoiceType.PROMOTIONAL_PAYMENT.getName())) {
				invoice.setInvoiceType(InvoiceType.PROMOTIONAL_PAYMENT);
//				invoice.setInvoiceStatus("Promotion Invoice"); // Discuss with sir.
				String check = "";
				String name = invoice.getProductName();
				if (name.equals(check)) {
					addActionMessage("Please enter mandatory fields");
					return SUCCESS;
				}
				try {

					BatchResponseObject batchResponseObject = new BatchResponseObject();
					CommanCsvReader commanCsvReader = new CommanCsvReader();

					if (!(StringUtils.isEmpty(fileName.toString()))) {
						batchResponseObject = commanCsvReader.csvReaderForBatchEmailSend(fileName.toString());
						if (batchResponseObject.getInvoiceEmailList().isEmpty()) {
							addActionMessage("Invalid CSV file");
							return SUCCESS;
						} else {
							for (Invoice emailphone : batchResponseObject.getInvoiceEmailList()) {
								try {
									if (emailphone.getEmail().isEmpty()) {
										addActionMessage("Please Browse Valid CSV with Valid Data");
										return SUCCESS;
									}

								} catch (Exception exception) {

									logger.error("Error!! Unable to send email Emailer fail " + exception);
								}
							}
						}
					}
				} catch (Exception e) {
					addActionMessage("Please Browse a CSV file for Promotional Invoice");
					return SUCCESS;

				}

			} else {
				invoice.setInvoiceType(InvoiceType.SINGLE_PAYMENT);
				invoice.setInvoiceStatus(InvoiceStatus);
				String check = "";
				String email = invoice.getEmail();
				String name = invoice.getName();
				String product = invoice.getProductName();
				String phone = invoice.getPhone();
				if (email.equals(check) && name.equals(check) && product.equals(check) && phone.equals(check)) {
					addActionMessage("Please enter mandatory fields");
					return SUCCESS;
				}

			}

			invoice.setSaltKey(awsEncryptDecryptService.encrypt(invoice.getInvoiceId()));
			if (invoice.getReturnUrl().isEmpty()) {
				invoiceReturnUrl = PropertiesManager.propertiesMap.get(CrmFieldConstants.INVOICE_RETURN_URL.getValue());
				invoice.setReturnUrl(invoiceReturnUrl);
			}
			url = PropertiesManager.propertiesMap.get(CrmFieldConstants.INVOICE_URL.getValue())
					+ invoice.getInvoiceId();
			invoice.setInvoiceUrl(url);
			shortUrl = shortUrlProvider.ShortUrl(url);
			invoice.setShortUrl(shortUrl);
			invoiceNo = invoice.getInvoiceNo();
			invoiceId = invoice.getInvoiceId();
		//	invoice.setInvoiceNo("INV" + invoiceId + invoiceNo);
			invoiceTransactionDao.create(invoice);

			if (invoice.getEmail() != null) {

				emailControllerServiceProvider.invoiceLink(invoice.getShortUrl(), invoice.getEmail(), invoice.getName(),
						invoice.getBusinessName());
			}
			smsControllerServiceProvider.sendInvoiceSMS(invoice.getPhone(), invoice.getShortUrl(),
					invoice.getBusinessName(), invoice.getName());

			if (invoice.getInvoiceType().equals(InvoiceType.PROMOTIONAL_PAYMENT.getName())) {
				batchEmail(invoice.getShortUrl(), invoice);
				addActionMessage("User Invoice saved and link shared successfully with Customers");
			} else {
				addActionMessage("User Invoice saved and link shared successfully with User");
			}

			return SUCCESS;
		} catch (Exception exception) {
			logger.error("error generating invoice url", exception);
			addActionError("Error in generating Invoice");
			return ERROR;
		}
	}

	@SkipValidation
	public String invoiceEmail() {
		InvoiceTransactionDao invoiceTransactionDao = new InvoiceTransactionDao();

		try {
			// custom validation
			if ((validator.validateBlankField(invoice.getInvoiceId()))) {
				addActionError(validator.getResonseObject().getResponseMessage());
				return ERROR;
			} else if (!(validator.validateField(CrmFieldType.INVOICE_ID, invoice.getInvoiceId()))) {
				return ERROR;
			}
			Invoice invoiceDB = invoiceTransactionDao.findByInvoiceId(invoice.getInvoiceId());
			PropertiesManager propertyManager = new PropertiesManager();
			setUrl(propertyManager.getSystemProperty(CrmFieldConstants.INVOICE_URL.getValue())
					+ invoiceDB.getInvoiceId());

			emailControllerServiceProvider.invoiceLink(invoiceDB.getShortUrl(), invoiceDB.getEmail(),
					invoiceDB.getName(), invoiceDB.getBusinessName());
			return SUCCESS;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}
	}

	public boolean isValidFileType() {
		MagicNumberFileFilter mnff = null;
		File file = fileName;

		for (InvoiceExecutableSignature es : EnumSet.allOf(InvoiceExecutableSignature.class)) {
			mnff = new MagicNumberFileFilter(es.getMagicNumbers(), es.getOffset());
			if (mnff.accept(file)) {
				// Can create these variables locally.
				this.magicNumber = es.getMagicNumbers();
				this.executableDescription = es.getDescription();
				return true;
			}
		}
		return false;
	}

	public void batchEmail(String url, Invoice invoiceDB) throws SystemException {
		try {
			/*
			 * String line = ""; List<Invoice> emailPhoneList = new LinkedList<Invoice>();
			 */
			BatchResponseObject batchResponseObject = new BatchResponseObject();
			CommanCsvReader commanCsvReader = new CommanCsvReader();
			String Customer = "Customer";

			if (!(StringUtils.isEmpty(fileName.toString()))) {

				batchResponseObject = commanCsvReader.csvReaderForBatchEmailSend(fileName.toString());
				if (batchResponseObject.getInvoiceEmailList().isEmpty()) {
					addActionMessage("Invalid CSV file");
				} else {
					for (Invoice emailphone : batchResponseObject.getInvoiceEmailList()) {
						try {

							emailControllerServiceProvider.invoiceLink(url, emailphone.getEmail(), Customer,
									invoiceDB.getBusinessName());
							// for sending sms
							smsControllerServiceProvider.sendInvoiceSMS(emailphone.getPhone(), url,
									invoiceDB.getBusinessName(), Customer);

						} catch (Exception exception) {
							responseMessage.append(ErrorType.EMAIL_ERROR.getResponseMessage());
							responseMessage.append(emailphone.getEmail());
							responseMessage.append(batchResponseObject.getResponseMessage());
							responseMessage.append("\n");
							logger.error("Error!! Unable to send email Emailer fail " + exception);
						}
					}
				}
				responseMessage.append(batchResponseObject.getResponseMessage());

			}
		} catch (Exception exception) {
			logger.error("sending email via batch file unsuccessfull!" + exception);
			addActionMessage("sending email via batch file unsuccessfull!");

		}
	}

	@SkipValidation
	public String invoiceSMS() {
		InvoiceTransactionDao invoiceTransactionDao = new InvoiceTransactionDao();

		try {
			// custom validation
			if ((validator.validateBlankField(invoice.getInvoiceId()))) {
				addActionError(validator.getResonseObject().getResponseMessage());
				return ERROR;
			} else if (!(validator.validateField(CrmFieldType.INVOICE_ID, invoice.getInvoiceId()))) {
				return ERROR;
			}
			Invoice invoiceDB = invoiceTransactionDao.findByInvoiceId(invoice.getInvoiceId());
			PropertiesManager propertyManager = new PropertiesManager();
			setUrl(propertyManager.getSystemProperty(CrmFieldConstants.INVOICE_URL.getValue()) + invoiceDB.getInvoiceId());
			invoiceTransactionDao.update(invoiceDB);

			smsControllerServiceProvider.sendInvoiceSMS(invoiceDB.getPhone(), invoiceDB.getShortUrl(), invoiceDB.getBusinessName(), invoiceDB.getName());
			emailControllerServiceProvider.invoiceLink(invoiceDB.getShortUrl(), invoiceDB.getEmail(), invoiceDB.getName(), invoiceDB.getBusinessName());
			return SUCCESS;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}
	}

	@Override
	@SuppressWarnings({ "unchecked", "unlikely-arg-type" })
	public void validate() {

		User user = (User) sessionMap.get(Constants.USER);

		if (user.getUserType().equals(UserType.ADMIN) || user.getUserType().equals(UserType.SUBADMIN)
				|| user.getUserType().equals(UserType.SUPERADMIN)) {
			//merchantList = new UserDao().getActiveMerchantList();
			merchantList = new UserDao().getActiveMerchantList(sessionMap.get(Constants.SEGMENT.getValue()).toString(), user.getRole().getId());
			currencyMap = Currency.getAllCurrency();
			if (getMerchantPayId().equals(CrmFieldConstants.SELECT_MERCHANT.getValue())) {
				addFieldError(CrmFieldConstants.MERCHANT.getValue(), CrmFieldConstants.SELECT_MERCHANT.getValue());
			}
		} else {
			currencyMap = Currency.getSupportedCurreny(user);
			if (user.getUserType().equals(UserType.SUBUSER)) {
				Merchants merchant = new Merchants();
				String parentPayId = user.getParentPayId();
				User parentUser = userDao.findPayId(parentPayId);
				merchant.setMerchant(parentUser);
				merchantList.add(merchant);
				currencyMap = Currency.getSupportedCurreny(parentUser);
			} else if (user.getUserType().equals(UserType.MERCHANT)) {
				String Merchant = user.getPayId();
				User MerchantUser = userDao.findPayId(Merchant);
				currencyMap = Currency.getSupportedCurreny(MerchantUser);
				Merchants merchant = new Merchants();
				merchant.setEmailId(user.getEmailId());
				merchant.setPayId(user.getPayId());
				merchant.setBusinessName(user.getBusinessName());
				merchantList.add(merchant);
			}

		}

		if (invoice.getInvoiceType().equals(InvoiceType.SINGLE_PAYMENT.getName())) {
			if ((validator.validateBlankField(invoice.getInvoiceNo()))) {
				addFieldError(CrmFieldType.INVOICE_NUMBER.getName(), validator.getResonseObject().getResponseMessage());
			} else if (!(validator.validateField(CrmFieldType.INVOICE_NUMBER, invoice.getInvoiceNo()))) {
				addFieldError(CrmFieldType.INVOICE_NUMBER.getName(), validator.getResonseObject().getResponseMessage());
			}
			if ((validator.validateBlankField(invoice.getName()))) {
				addFieldError(CrmFieldType.INVOICE_NAME.getName(), validator.getResonseObject().getResponseMessage());
			} else if (!(validator.validateField(CrmFieldType.INVOICE_NAME, invoice.getName()))) {
				addFieldError(CrmFieldType.INVOICE_NAME.getName(), validator.getResonseObject().getResponseMessage());
			}
			if ((validator.validateBlankField(invoice.getPhone()))) {
				addFieldError(CrmFieldType.INVOICE_PHONE.getName(), validator.getResonseObject().getResponseMessage());
			} else if (!(validator.validateField(CrmFieldType.INVOICE_PHONE, invoice.getPhone()))) {
				addFieldError(CrmFieldType.INVOICE_PHONE.getName(), validator.getResonseObject().getResponseMessage());
			}

			if (validator.validateBlankField(invoice.getEmail())) {
				addFieldError(CrmFieldType.INVOICE_EMAIL.getName(), validator.getResonseObject().getResponseMessage());
			} else if (!(validator.isValidEmailId(invoice.getEmail()))) {
				addFieldError(CrmFieldType.INVOICE_EMAIL.getName(), validator.getResonseObject().getResponseMessage());
			}
			if ((validator.validateBlankField(invoice.getAmount()))) {
				addFieldError(CrmFieldType.INVOICE_AMOUNT.getName(), validator.getResonseObject().getResponseMessage());
			} else if (!(validator.validateField(CrmFieldType.INVOICE_AMOUNT, invoice.getAmount()))) {
				addFieldError(CrmFieldType.INVOICE_AMOUNT.getName(), validator.getResonseObject().getResponseMessage());
			}

			if ((validator.validateBlankField(invoice.getServiceCharge()))) {
				invoice.setServiceCharge("0.00");
			} else if (!(validator.validateField(CrmFieldType.INVOICE_AMOUNT, invoice.getServiceCharge()))) {
				addFieldError("serviceCharge", validator.getResonseObject().getResponseMessage());
			}
			if ((validator.validateBlankField(invoice.getTotalAmount()))) {
				addFieldError("totalAmount", validator.getResonseObject().getResponseMessage());
			} else if (!(validator.validateField(CrmFieldType.INVOICE_AMOUNT, invoice.getTotalAmount()))) {
				addFieldError("totalAmount", validator.getResonseObject().getResponseMessage());
			}

			if ((validator.validateBlankField(invoice.getCity()))) {
			} else if (!(validator.validateField(CrmFieldType.INVOICE_CITY, invoice.getCity()))) {
				addFieldError(CrmFieldType.INVOICE_CITY.getName(), validator.getResonseObject().getResponseMessage());
			}
			if ((validator.validateBlankField(invoice.getState()))) {
			} else if (!(validator.validateField(CrmFieldType.INVOICE_STATE, invoice.getState()))) {
				addFieldError(CrmFieldType.INVOICE_STATE.getName(), validator.getResonseObject().getResponseMessage());
			}
			if ((validator.validateBlankField(invoice.getBusinessName()))) {
			} else if (!(validator.validateField(CrmFieldType.BUSINESS_NAME, invoice.getBusinessName()))) {
				addFieldError(CrmFieldType.BUSINESS_NAME.getName(), validator.getResonseObject().getResponseMessage());
			}
			if ((validator.validateBlankField(invoice.getProductName()))) {
			} else if (!(validator.validateField(CrmFieldType.INVOICE_PRODUCT_NAME, invoice.getProductName()))) {
				addFieldError(CrmFieldType.INVOICE_PRODUCT_NAME.getName(), validator.getResonseObject().getResponseMessage());
			}
			if ((validator.validateBlankField(invoice.getQuantity()))) {
			} else if (!(validator.validateField(CrmFieldType.INVOICE_QUANTITY, invoice.getQuantity()))) {
				addFieldError(CrmFieldType.INVOICE_QUANTITY.getName(), validator.getResonseObject().getResponseMessage());
			}
			if ((validator.validateBlankField(invoice.getReturnUrl()))) {
			} else if (!(validator.validateField(CrmFieldType.INVOICE_RETURN_URL, invoice.getReturnUrl()))) {
				addFieldError(CrmFieldType.INVOICE_RETURN_URL.getName(),
						validator.getResonseObject().getResponseMessage());
			}
			if ((validator.validateBlankField(invoice.getCountry()))) {
			} else if (!(validator.validateField(CrmFieldType.INVOICE_COUNTRY, invoice.getCountry()))) {
				addFieldError(CrmFieldType.INVOICE_COUNTRY.getName(),
						validator.getResonseObject().getResponseMessage());
			}
			if ((validator.validateBlankField(invoice.getZip()))) {
			} else if (!(validator.validateField(CrmFieldType.INVOICE_ZIP, invoice.getZip()))) {
				addFieldError(CrmFieldType.INVOICE_ZIP.getName(), validator.getResonseObject().getResponseMessage());
			}
			if ((validator.validateBlankField(invoice.getAddress()))) {
			} else if (!(validator.validateField(CrmFieldType.ADDRESS, invoice.getAddress()))) {
				addFieldError(CrmFieldType.ADDRESS.getName(), validator.getResonseObject().getResponseMessage());
			}
			if ((validator.validateBlankField(invoice.getProductDesc()))) {
			} else if (!(validator.validateField(CrmFieldType.INVOICE_PRODUCT_DESCRIPTION, invoice.getProductDesc()))) {
				addFieldError(CrmFieldType.INVOICE_PRODUCT_DESCRIPTION.getName(),
						validator.getResonseObject().getResponseMessage());
			}
			if (invoice.getCurrencyCode().equals(CrmFieldConstants.SELECT_CURRENCY.getValue())
					|| validator.validateBlankField(invoice.getCurrencyCode())) {
				addFieldError(CrmFieldType.INVOICE_CURRENCY_CODE.getName(),
						CrmFieldConstants.SELECT_CURRENCY.getValue());
			} else if (!(validator.validateField(CrmFieldType.INVOICE_CURRENCY_CODE, invoice.getCurrencyCode()))) {
				addFieldError(CrmFieldType.INVOICE_CURRENCY_CODE.getName(),
						CrmFieldConstants.SELECT_CURRENCY.getValue());
			}
			if (invoice.getExpiresDay().isEmpty()) {
				if (invoice.getExpiresHour().isEmpty()) {
					addFieldError(CrmFieldType.INVOICE_EXPIRES_DAY.getName(),
							validator.getResonseObject().getResponseMessage());
					addFieldError(CrmFieldType.INVOICE_EXPIRES_HOUR.getName(),
							ErrorType.INVALID_FIELD_VALUE.getResponseMessage());
				} else {
					invoice.setExpiresDay("0");
				}
			} else if (invoice.getExpiresHour().isEmpty()) {
				invoice.setExpiresHour("0");
			} else if ((Integer.parseInt(invoice.getExpiresDay().toString()) == 0
					&& Integer.parseInt(invoice.getExpiresHour().toString()) == 0)
					|| (Integer.parseInt(invoice.getExpiresDay().toString()) < 0
							|| Integer.parseInt(invoice.getExpiresHour().toString()) < 0)) {
				addFieldError(CrmFieldType.INVOICE_EXPIRES_DAY.getName(),
						validator.getResonseObject().getResponseMessage());
				addFieldError(CrmFieldType.INVOICE_EXPIRES_HOUR.getName(),
						validator.getResonseObject().getResponseMessage());
			}
			if ((validator.validateBlankField(invoice.getInvoiceId()))) {
			} else if (!(validator.validateField(CrmFieldType.INVOICE_ID, invoice.getInvoiceId()))) {
				addFieldError(CrmFieldType.INVOICE_ID.getName(), validator.getResonseObject().getResponseMessage());
			}
		}

		else {

			if ((validator.validateBlankField(invoice.getAmount()))) {
				addFieldError(CrmFieldType.INVOICE_AMOUNT.getName(), validator.getResonseObject().getResponseMessage());
			} else if (!(validator.validateField(CrmFieldType.INVOICE_AMOUNT, invoice.getAmount()))) {
				addFieldError(CrmFieldType.INVOICE_AMOUNT.getName(), validator.getResonseObject().getResponseMessage());
			}

			if ((validator.validateBlankField(invoice.getServiceCharge()))) {
				invoice.setServiceCharge("0.00");
			} else if (!(validator.validateField(CrmFieldType.INVOICE_AMOUNT, invoice.getServiceCharge()))) {
				addFieldError("serviceCharge", validator.getResonseObject().getResponseMessage());
			}
			if ((validator.validateBlankField(invoice.getTotalAmount()))) {
				addFieldError("totalAmount", validator.getResonseObject().getResponseMessage());
			} else if (!(validator.validateField(CrmFieldType.INVOICE_AMOUNT, invoice.getTotalAmount()))) {
				addFieldError("totalAmount", validator.getResonseObject().getResponseMessage());
			}
			if ((validator.validateBlankField(invoice.getProductName()))) {
			} else if (!(validator.validateField(CrmFieldType.INVOICE_PRODUCT_NAME, invoice.getProductName()))) {
				addFieldError(CrmFieldType.INVOICE_PRODUCT_NAME.getName(), validator.getResonseObject().getResponseMessage());
			}
			if ((validator.validateBlankField(invoice.getQuantity()))) {
			} else if (!(validator.validateField(CrmFieldType.INVOICE_QUANTITY, invoice.getQuantity()))) {
				addFieldError(CrmFieldType.INVOICE_QUANTITY.getName(), validator.getResonseObject().getResponseMessage());
			}
			if ((validator.validateBlankField(invoice.getReturnUrl()))) {
			} else if (!(validator.validateField(CrmFieldType.INVOICE_RETURN_URL, invoice.getReturnUrl()))) {
				addFieldError(CrmFieldType.INVOICE_RETURN_URL.getName(),
						validator.getResonseObject().getResponseMessage());
			}
			if ((validator.validateBlankField(invoice.getProductDesc()))) {
			} else if (!(validator.validateField(CrmFieldType.INVOICE_PRODUCT_DESCRIPTION, invoice.getProductDesc()))) {
				addFieldError(CrmFieldType.INVOICE_PRODUCT_DESCRIPTION.getName(),
						validator.getResonseObject().getResponseMessage());
			}
			if (invoice.getCurrencyCode().equals(CrmFieldConstants.SELECT_CURRENCY.getValue())
					|| validator.validateBlankField(invoice.getCurrencyCode())) {
				addFieldError(CrmFieldType.INVOICE_CURRENCY_CODE.getName(),
						CrmFieldConstants.SELECT_CURRENCY.getValue());
			} else if (!(validator.validateField(CrmFieldType.INVOICE_CURRENCY_CODE, invoice.getCurrencyCode()))) {
				addFieldError(CrmFieldType.INVOICE_CURRENCY_CODE.getName(),
						CrmFieldConstants.SELECT_CURRENCY.getValue());
			}
			if (invoice.getExpiresDay().isEmpty()) {
				if (invoice.getExpiresHour().isEmpty()) {
					addFieldError(CrmFieldType.INVOICE_EXPIRES_DAY.getName(),
							validator.getResonseObject().getResponseMessage());
					addFieldError(CrmFieldType.INVOICE_EXPIRES_HOUR.getName(),
							ErrorType.INVALID_FIELD_VALUE.getResponseMessage());
				} else {
					invoice.setExpiresDay("0");
				}
			} else if (invoice.getExpiresHour().isEmpty()) {
				invoice.setExpiresHour("0");
			} else if ((Integer.parseInt(invoice.getExpiresDay().toString()) == 0
					&& Integer.parseInt(invoice.getExpiresHour().toString()) == 0)
					|| (Integer.parseInt(invoice.getExpiresDay().toString()) < 0
							|| Integer.parseInt(invoice.getExpiresHour().toString()) < 0)) {
				addFieldError(CrmFieldType.INVOICE_EXPIRES_DAY.getName(),
						validator.getResonseObject().getResponseMessage());
				addFieldError(CrmFieldType.INVOICE_EXPIRES_HOUR.getName(),
						validator.getResonseObject().getResponseMessage());
			}
		}
	}

	@Override
	public Invoice getModel() {
		return invoice;
	}

	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean isEmailCheck() {
		return emailCheck;
	}

	public void setEmailCheck(boolean emailCheck) {
		this.emailCheck = emailCheck;
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

	public List<Merchants> getMerchantList() {
		return merchantList;
	}

	public void setMerchantList(List<Merchants> merchantList) {
		this.merchantList = merchantList;
	}

	public File getFileName() {
		return fileName;
	}

	public void setFileName(File fileName) {
		this.fileName = fileName;
	}
}
