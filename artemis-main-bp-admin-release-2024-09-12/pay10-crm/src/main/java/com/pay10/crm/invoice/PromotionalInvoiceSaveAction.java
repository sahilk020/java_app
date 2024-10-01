package com.pay10.crm.invoice;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;
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
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.Currency;
import com.pay10.crm.action.AbstractSecureAction;
import com.pay10.crm.actionBeans.CommanCsvReader;
import com.pay10.crm.invoice.service.PromotionalInvoiceService;
import com.pay10.crm.invoice.service.TncTrailReportService;
import org.springframework.util.StringUtils;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.struts2.ServletActionContext;

public class PromotionalInvoiceSaveAction extends AbstractSecureAction implements ModelDriven<Invoice> {

	private static final long serialVersionUID = 3576140954833935738L;
	private static Logger logger = LoggerFactory.getLogger(PromotionalInvoiceSaveAction.class.getName());
	@Autowired
	private CrmValidator validator;

	@Autowired
	InvoiceDao invoiceDao;

	private List<Merchants> merchantList = new ArrayList<>();
	private Map<String, String> currencyMap = new LinkedHashMap<>();

	private Invoice invoice = new Invoice();

	private File fileName;
	private String fileNameContentType;
	private String fileNameFileName;
	private String payId;
	private boolean tncStatus;

	@Autowired
	UserDao userDao;

	@Autowired
	PromotionalInvoiceService invoiceService;

	@Autowired
	private AuditTrailService auditTrailService;
	
	// Added By Sweety
	@Autowired
	TncTrailReportService tncService;
	
	private List<Invoice> listInvoices=new ArrayList<Invoice>();

	boolean errorFlag=false;

	@SuppressWarnings("unchecked")
	@Override
	public String execute() {
		logger.info("Saving promotional invoice");

		HttpServletRequest request = ServletActionContext.getRequest();
		boolean status1 = ServletFileUpload.isMultipartContent(request);
		
		if (status1) {
			payId = request.getParameter("payId");
			String merchantConsent=request.getParameter("merchantConsent");
			invoice.setMerchantConsent(Boolean.parseBoolean(merchantConsent));
			
		}

		try {
			List<Invoice> invoiceNews = new ArrayList<>();
			if (!(StringUtils.isEmpty(fileName)) && fileName.length() > 2000000) {
				logger.error("Invalid File size");
				return "INVPRO109";
			}
			try {
				if (!(StringUtils.isEmpty(fileName))) {
					String businessName = userDao.getMerchantByPayId(payId);
					CommanCsvReader commanCsvReader = new CommanCsvReader();
					invoiceNews = commanCsvReader.csvReaderForBatch(fileName.toString(), businessName,
							invoice.getExpiresHour());

				} else {
					logger.info("CSV file is null or file name is empty");
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
				return ERROR;
			}

			
			// add status of each invoice
			AtomicInteger counter=new AtomicInteger(1);
			invoiceNews.stream().forEach(invoice -> {
				// Added By Sweety To save invoice details in other collection if tnc enabled
				invoice.setMerchantConsent(this.invoice.isMerchantConsent());
				logger.info("merchantConsent...={}", invoice.isMerchantConsent());
				invoice.setPayId(payId);
				
				
				if(validateData(invoice)) {
					String status = invoiceService.createInvoiceService(invoice, fileName, getFileNameFileName());
					logger.info("Final Status : " + status);
					
					if (invoice.isMerchantConsent()) {
						tncService.createTrailReport(invoice);
					}
					
					/************************************************************/
					String invoiceCreationError = "Promotional Invoice Create Error : " + status;
					String msg;
					Gson gson=new Gson();
					
					if (status.equals("INVPRO100")) {
						msg = "Please enter mandatory fields";
						//addActionMessage(msg);
						invoice.setStatus(msg);
						listInvoices.add(invoice);
						logger.error(invoiceCreationError + " : " + msg);
					} else if (status.equals("INVPRO106")) {
						msg = "Sending email via batch file unsuccessfull";
						//addActionMessage(msg);
						invoice.setStatus(msg);
						listInvoices.add(invoice);
						logger.error(invoiceCreationError + " : " + msg);
					} else if (status.equals("INVPRO107")) {
						msg = "Failed to push promotional invoice to database";
						//addActionMessage(msg);
						invoice.setStatus(msg);
						listInvoices.add(invoice);
						logger.error(invoiceCreationError + " : " + msg);
					}else if (status.equals("INVPRO200")) {
						msg = "User Invoice saved and link shared successfully with Customers";
						//addActionMessage(msg);
						invoice.setStatus(msg);
						listInvoices.add(invoice);
						logger.info("Successfully created promotional invoice : " + msg);
					} else if (status.equals("INVPRO111")) {
						msg = "Please enter unique invoice number";
						//addActionMessage(msg);
						invoice.setStatus(msg);
						listInvoices.add(invoice);
						logger.error(invoiceCreationError + " : " + msg);
					} else {
						invoice.setStatus("Unknown error occured while saving invoice.");
						logger.error("Unknown error occured while saving invoice.");
						//addActionMessage("Unknow error occured");
						listInvoices.add(invoice);
					}
					
					/**********************************************************/
					try {
						Map<String, ActionMessageByAction> actionMessagesByAction = (Map<String, ActionMessageByAction>) sessionMap
								.get(Constants.ACTION_MESSAGE_BY_ACTION.getValue());
						AuditTrail auditTrail = new AuditTrail(mapper.writeValueAsString(invoice), null,
								actionMessagesByAction.get("savePromotionalInvoice"));
						auditTrailService.saveAudit(request, auditTrail);
						
					}catch (Exception e) {
						logger.info("Exception here : " + e);
					}
					
				}else {
					//add invoice data in collection
					invoice.setStatus("Data Format Error");
					//listInvoices.put("Data Format Error",invoice);
					listInvoices.add(invoice);
					errorFlag=true;
					counter.getAndIncrement();
				}
				
				
			});
			
			logger.info("Failed Invoice list : " + new Gson().toJson(invoice));
			
			
			setListInvoices(listInvoices);
			
			if(!errorFlag) {
				addActionMessage("File Process Successfully");
			}else {
				if(invoiceNews.size()==counter.get()) {
				addActionMessage("Error While File Processing");
				}
				else {
				addActionMessage("File Process Partial");
				}
			}
			return SUCCESS;
		} catch (Exception ex) {
			logger.error("Exception", ex);
			return ERROR;
		}
	}

	private String emailPattern = "^(.+)@(\\S+)$";
	private String mobilePattern = "^[0-9]{10}$";
	private String fullNamePattern = "^[a-zA-Z\\s]+"; 
	private String amountPatternOptionalDot="^[1-9]\\d*(\\.\\d+)?$";
	private String numberPatternWithoutDot="^\\d+$";
	
	private boolean validateData(Invoice invoice) {
		int count=0;
		if(org.apache.commons.lang3.StringUtils.isNotEmpty(invoice.getEmail())) {		
			
			if(invoice.getEmail().matches(emailPattern)) {
				count++;
			}
		}
		if(org.apache.commons.lang3.StringUtils.isNotEmpty(invoice.getPhone())) {
			
			if(invoice.getPhone().matches(mobilePattern)) {
				count++;
			}
		}
		if(org.apache.commons.lang3.StringUtils.isNotEmpty(invoice.getName())) {
			if(invoice.getName().matches(fullNamePattern)) {
				count++;
			}
		}
		if(org.apache.commons.lang3.StringUtils.isNotEmpty(invoice.getAmount())) {
			if(invoice.getAmount().matches(amountPatternOptionalDot)) {
				count++;
			}
		}
		if(org.apache.commons.lang3.StringUtils.isNotEmpty(invoice.getProductName())) {
			if(invoice.getProductName().matches(fullNamePattern)) {
				count++;
			}
		}
		if(org.apache.commons.lang3.StringUtils.isNotEmpty(invoice.getQuantity())){
			if((invoice.getQuantity().matches(numberPatternWithoutDot)&&Integer.parseInt(invoice.getQuantity())>0)) {
				count++;
			}
		}
		if(org.apache.commons.lang3.StringUtils.isNotEmpty(invoice.getGst())) {
			if(invoice.getGst().matches(numberPatternWithoutDot)) {
				count++;
			}
		}
		
	return (count==7)?true:false;
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void validate() {
		User user = (User) sessionMap.get(Constants.USER.getValue());

		if (user.getUserType().equals(UserType.ADMIN) || user.getUserType().equals(UserType.SUBADMIN)
				|| user.getUserType().equals(UserType.SUPERADMIN)) {
			// merchantList = new UserDao().getActiveMerchantList();
			merchantList = new UserDao().getActiveMerchantList(sessionMap.get(Constants.SEGMENT.getValue()).toString(),
					user.getRole().getId());
			currencyMap = Currency.getAllCurrency();
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

		if (invoice.getExpiresHour().isEmpty()) {
			invoice.setExpiresHour("0");
		} else if (Integer.parseInt(invoice.getExpiresHour().toString()) == 0
				|| Integer.parseInt(invoice.getExpiresHour().toString()) < 0) {
			logger.error("Invalid invoice expiry hour : " + invoice.getExpiresHour());
			addActionMessage("Invalid expiry hour");
			addFieldError(CrmFieldType.INVOICE_EXPIRES_HOUR.getName(),
					validator.getResonseObject().getResponseMessage());
		}
	}

	@Override
	public Invoice getModel() {
		// TODO Auto-generated method stub
		return invoice;
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

	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

	public File getFileName() {
		return fileName;
	}

	public void setFileName(File fileName) {
		this.fileName = fileName;
	}

	public String getPayId() {
		return payId;
	}

	public void setPayId(String payId) {
		this.payId = payId;
	}

	public String getFileNameContentType() {
		return fileNameContentType;
	}

	public void setFileNameContentType(String fileNameContentType) {
		this.fileNameContentType = fileNameContentType;
	}

	public String getFileNameFileName() {
		return fileNameFileName;
	}

	public void setFileNameFileName(String fileNameFileName) {
		this.fileNameFileName = fileNameFileName;
	}

	public boolean isTncStatus() {
		return tncStatus;
	}

	public void setTncStatus(boolean tncStatus) {
		this.tncStatus = tncStatus;
	}
	
	public List<Invoice> getListInvoices() {
		return listInvoices;
	}

	public void setListInvoices(List<Invoice> listInvoices) {
		this.listInvoices = listInvoices;
	}

}
