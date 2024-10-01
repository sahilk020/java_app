package com.pay10.crm.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.SummaryReportNodalObject;
import com.pay10.commons.user.User;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.DateCreater;

public class NodalSummaryReportAction extends AbstractSecureAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1862272004872627431L;

	private static Logger logger = LoggerFactory.getLogger(NodalSummaryReportAction.class.getName());

	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
	private String currency;
	private String dateFrom;
	private String dateTo;
	private String merchantPayId;
	private String paymentType;
	private String mopType;
	private String transactionType;
	private String status;
	private String acquirer;
	private String paymentMethods;
	private InputStream fileInputStream;
	private String filename;
	private User sessionUser = new User();
	private String paymentsRegion;
	private String cardHolderType;
	private String settlementStatus;
	
	public static final BigDecimal ONE_HUNDRED = new BigDecimal(100);

	@Autowired
	private SummaryReportQuery summaryReportQuery;
	
	@Autowired
	private CrmValidator validator;
	
	@Override
	@SuppressWarnings("resource")
	public String execute() {
		
		logger.info("Inside download summary report action");
		if (acquirer == null || acquirer.isEmpty()) {
			acquirer = "ALL";

		}
		
		if (paymentsRegion == null || paymentsRegion.isEmpty()) {
			paymentsRegion = "ALL";

		}
		
		if (mopType == null || mopType.isEmpty()) {
			mopType = "ALL";

		}
		
		if (transactionType == null || transactionType.isEmpty()) {
			transactionType = "ALL";

		}
		
		setDateFrom(DateCreater.toDateTimeformatCreater(dateFrom));
		setDateTo(DateCreater.formDateTimeformatCreater(dateTo));
		
		sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		List<SummaryReportNodalObject> transactionList = new ArrayList<SummaryReportNodalObject>();
		try {
			transactionList = summaryReportQuery.summaryReportNodalDownload(dateFrom, dateTo, merchantPayId,
					paymentMethods, acquirer, currency, sessionUser, getPaymentsRegion(),
					getCardHolderType(),"",mopType,transactionType,settlementStatus);
		} catch (Exception e) {
		logger.error("Exception",e);
		}
		
		logger.info("List generated successfully for Download summary Report");
		SXSSFWorkbook wb = new SXSSFWorkbook(100);
		Row row;
		int rownum = 1;
		// Create a blank sheet
		Sheet sheet = wb.createSheet("Summary Report");

		row = sheet.createRow(0);

		row.createCell(0).setCellValue("Sr No");
		row.createCell(1).setCellValue("Txn Id");
		row.createCell(2).setCellValue("Pg Ref Num");
		row.createCell(3).setCellValue("Payment Method");
		row.createCell(4).setCellValue("Mop Type");
		row.createCell(5).setCellValue("Order Id");
		row.createCell(6).setCellValue("Business Name");
		row.createCell(7).setCellValue("Currency");	
		row.createCell(8).setCellValue("Transaction Type");
		row.createCell(9).setCellValue("Capture Date");	
		row.createCell(10).setCellValue("Settlement Date");	
		row.createCell(11).setCellValue("Nodal Credit Date");	
		row.createCell(12).setCellValue("Nodal Payout Initiation Date");	
		row.createCell(13).setCellValue("Nodal Payout Date");	
		row.createCell(14).setCellValue("Transaction Region");
		row.createCell(15).setCellValue("Card Holder Type");
		row.createCell(16).setCellValue("Acquirer");
		row.createCell(17).setCellValue("Total Amount");
		row.createCell(18).setCellValue("TDR/SC (Acquirer)");
		row.createCell(19).setCellValue("TDR/SC (iPay)");
		row.createCell(20).setCellValue("GST(Acquirer)");
		row.createCell(21).setCellValue("GST(iPay)");
		row.createCell(22).setCellValue("Merchant Amount");
		row.createCell(23).setCellValue("ACQ ID");
		row.createCell(24).setCellValue("RRN");
		row.createCell(25).setCellValue("Post Settled Flag");
		row.createCell(26).setCellValue("Delta Refund flag");


		for (SummaryReportNodalObject transactionSearch : transactionList) {
			row = sheet.createRow(rownum++);
			transactionSearch.setSrNo(String.valueOf(rownum-1));
			Object[] objArr = transactionSearch.myCsvMethodNodalSummaryReport();

			int cellnum = 0;
			for (Object obj : objArr) {
				// this line creates a cell in the next column of that row
				Cell cell = row.createCell(cellnum++);
				if (obj instanceof String)
					cell.setCellValue((String) obj);
				else if (obj instanceof Integer)
					cell.setCellValue((Integer) obj);
				
			}
		}
		try {
			String FILE_EXTENSION = ".xlsx";
			DateFormat df = new SimpleDateFormat("yyyyMMddhhmmss");
			filename = "Nodal_Summary_Report" + df.format(new Date()) + FILE_EXTENSION;
			File file = new File(filename);	

			// this Writes the workbook
			FileOutputStream out = new FileOutputStream(file);
			wb.write(out);
			out.flush();
			out.close();
			wb.dispose();
			fileInputStream = new FileInputStream(file);
			addActionMessage(filename + " written successfully on disk.");
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}

		logger.info("File generated successfully for Download summary Report");
		return SUCCESS;

	}

	
/*public List<SummaryReportObject> findDetails(List<SummaryReportObject> transactionList) {
		
		logger.info("Inside search summary report Action , findDetails , ");
		List<ServiceTax> serviceTaxList = new ArrayList<ServiceTax>();
		List<SummaryReportObject> transactionList1 = new ArrayList<SummaryReportObject>();
		Map<String,List<Surcharge>> surchargeMap = new HashMap<String,List<Surcharge>>(); 
		Map<String,List<ServiceTax>> serviceTaxMap = new HashMap<String,List<ServiceTax>>(); 
		
		BigDecimal merchantGstAmount = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_DOWN);
		BigDecimal iPayGstAmount = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_DOWN);
		BigDecimal mmadGstAmount = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_DOWN);
		BigDecimal acquirerGstAmount  = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_DOWN);
		Map<String,User> userMap = new HashMap<String,User>(); 
		TdrPojo tdrPojo = new TdrPojo();
		BigDecimal st = null;
		String bussinessType = null;
		String bussinessName = "";
		for (SummaryReportObject transactionSearch : transactionList) {
			String payId = transactionSearch.getPayId();
			if (!StringUtils.isBlank(payId)) {
				User user = new User();
				
				if (userMap.get(payId) != null) {
					user = userMap.get(payId);
				}
				else {
					 user = userDao.findPayId(payId);
					 userMap.put(payId, user);
				}

				String amount = transactionSearch.getAmount();
				bussinessType = user.getIndustryCategory();
				bussinessName = user.getBusinessName();
				
				if (serviceTaxMap.get(bussinessType) != null ) {
					
					serviceTaxList = serviceTaxMap.get(bussinessType);
				}
				else {
					serviceTaxList = serviceTaxDao.findServiceTaxForReportWithoutDate(user.getIndustryCategory(),
							transactionSearch.getDateFrom());
					
					serviceTaxMap.put(bussinessType, serviceTaxList);
				}
				
				for (ServiceTax serviceTax : serviceTaxList) {
					st = serviceTax.getServiceTax();
					st = st.setScale(2, RoundingMode.HALF_DOWN);
					
				}
				
				if (!StringUtils.isBlank(transactionSearch.getSurchargeFlag())) {
					if (transactionSearch.getSurchargeFlag().equals("Y")) {
						String txnAmount = transactionSearch.getAmount();
						String surchargeAmount = transactionSearch.getTotalAmount();
						BigDecimal nettxnAmount = new BigDecimal(txnAmount);
						transactionSearch.setTotalAmount(surchargeAmount);
						
						PaymentType paymentType = PaymentType
								.getInstanceUsingCode(transactionSearch.getPaymentMethods());

						if (paymentType == null) {
							logger.info("Payment Type is null for Pg Ref Num " + transactionSearch.getPgRefNum());
							continue;
						}
						
						AcquirerType acquirerType = AcquirerType
								.getInstancefromCode(transactionSearch.getAcquirerType());

						if (acquirerType == null) {
							logger.info("acquirerType is null for Pg Ref Num " + transactionSearch.getPgRefNum());
							continue;
						}
						
						
						MopType mopType = MopType.getInstanceIgnoreCase(transactionSearch.getMopType());

						if (mopType == null) {
							logger.info("mopType is null for Pg Ref Num " + transactionSearch.getPgRefNum());
							continue;
						}
						
						if (paymentsRegion == null) {
							paymentsRegion = AccountCurrencyRegion.DOMESTIC.toString();
						}
						
						String paymentsRegion = transactionSearch.getTransactionRegion();
						
						Date surchargeStartDate = null;
						Date surchargeEndDate = null;
						Date settlementDate = null;
						Surcharge surcharge = new Surcharge();
						

						try {
							for (Surcharge surchargeData : surchargeList) {

								if (AcquirerType.getInstancefromName(surchargeData.getAcquirerName()).toString()
										.equalsIgnoreCase(transactionSearch.getAcquirerType())
										&& surchargeData.getPaymentType().getCode()
												.equalsIgnoreCase(transactionSearch.getPaymentMethods())
										&& surchargeData.getMopType().getName()
												.equalsIgnoreCase(transactionSearch.getMopType())
										&& surchargeData.getPaymentsRegion().name()
												.equalsIgnoreCase(transactionSearch.getTransactionRegion())
										&& surchargeData.getPayId().equalsIgnoreCase(transactionSearch.getPayId())) {

									surchargeStartDate = format.parse(surchargeData.getCreatedDate().toString());
									surchargeEndDate = format.parse(surchargeData.getUpdatedDate().toString());
									if(surchargeStartDate.compareTo(surchargeEndDate) == 0) {
										surchargeEndDate = new Date();
									}
									
									settlementDate = format.parse(transactionSearch.getDateFrom());
									
									if (settlementDate.compareTo(surchargeStartDate) >= 0 && settlementDate.compareTo(surchargeEndDate) <= 0 ) {
										surcharge = surchargeData;
										break;
									}
									else {
										continue;
									}

								}
							}
						} catch (Exception e) {
							logger.error("Exception " + e);
						}


						if (surcharge.getBankSurchargeAmountCustomer() == null || surcharge.getBankSurchargePercentageCustomer() == null 
								|| surcharge.getBankSurchargeAmountCommercial() ==null || surcharge.getBankSurchargePercentageCommercial() == null) {

							logger.info("Surcharge is null for payId = "+ transactionSearch.getPayId()+" acquirer = "+transactionSearch.getAcquirerType() +" mop = "+transactionSearch.getMopType()
							+ "  paymentType = "+ transactionSearch.getPaymentMethods() + "  paymentRegion = "+ transactionSearch.getTransactionRegion());
							continue;
						}
						
						
						BigDecimal bankSurchargeFC;
						BigDecimal bankSurchargePercent;

						if (transactionSearch.getCardHolderType() == null || transactionSearch.getCardHolderType().isEmpty() ) {
							
							bankSurchargeFC = surcharge.getBankSurchargeAmountCustomer();
							bankSurchargePercent = surcharge.getBankSurchargePercentageCustomer();
						}
						
						else if (transactionSearch.getCardHolderType()
								.equalsIgnoreCase(CardHolderType.CONSUMER.toString())) {

							bankSurchargeFC = surcharge.getBankSurchargeAmountCustomer();
							bankSurchargePercent = surcharge.getBankSurchargePercentageCustomer();
						} else {

							bankSurchargeFC = surcharge.getBankSurchargeAmountCommercial();
							bankSurchargePercent = surcharge.getBankSurchargePercentageCommercial();
						}

						
						BigDecimal netsurchargeAmount = new BigDecimal(surchargeAmount);
						BigDecimal netcalculatedSurcharge = netsurchargeAmount.subtract(nettxnAmount);
						netcalculatedSurcharge = netcalculatedSurcharge.setScale(2, RoundingMode.HALF_DOWN);
						
						BigDecimal gstCalculate = netcalculatedSurcharge.multiply(st).divide(((ONE_HUNDRED).add(st)), 2,
								RoundingMode.HALF_DOWN);
						
						BigDecimal pgSurchargeAmount ;
						BigDecimal iPaySurchargeAmount;
						BigDecimal mmadSurchargeAmount;
						BigDecimal acquirerSurchargeAmount ;
						
						if (netcalculatedSurcharge.equals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_DOWN))) {
							pgSurchargeAmount = BigDecimal.ZERO;
							iPaySurchargeAmount = BigDecimal.ZERO;
							mmadSurchargeAmount = BigDecimal.ZERO;
							acquirerSurchargeAmount = BigDecimal.ZERO;
						}
						
						else {
							
							acquirerSurchargeAmount = nettxnAmount.multiply(bankSurchargePercent).divide(((ONE_HUNDRED)), 2,
									RoundingMode.HALF_DOWN);
							acquirerSurchargeAmount = acquirerSurchargeAmount.add(bankSurchargeFC).setScale(2, RoundingMode.HALF_DOWN);;
							
							pgSurchargeAmount = netcalculatedSurcharge.subtract(acquirerSurchargeAmount);
							pgSurchargeAmount = pgSurchargeAmount.subtract(gstCalculate);
							pgSurchargeAmount = pgSurchargeAmount.setScale(2, RoundingMode.HALF_DOWN);
							iPaySurchargeAmount = pgSurchargeAmount.divide(new BigDecimal(2),RoundingMode.HALF_DOWN);
							mmadSurchargeAmount = pgSurchargeAmount.divide(new BigDecimal(2),RoundingMode.HALF_DOWN);
							
						}
						
						acquirerGstAmount = acquirerSurchargeAmount.multiply(st).divide(((ONE_HUNDRED)), 2,
								RoundingMode.HALF_DOWN);
						
						merchantGstAmount = pgSurchargeAmount.multiply(st).divide(((ONE_HUNDRED)), 2,
								RoundingMode.HALF_DOWN);
						iPayGstAmount = merchantGstAmount.divide(new BigDecimal(2),RoundingMode.HALF_DOWN);
						mmadGstAmount = merchantGstAmount.divide(new BigDecimal(2),RoundingMode.HALF_DOWN);
						
						
						BigDecimal totalSurcharge = netcalculatedSurcharge.subtract(gstCalculate);
						BigDecimal totalAmtPaytoMerchant = netsurchargeAmount
								.subtract(gstCalculate.add(totalSurcharge));
						
						if (transactionSearch.getTxnType().equalsIgnoreCase(TransactionType.REFUNDRECO.getName())) {
						BigDecimal minusValue = BigDecimal.valueOf(-1);
						
						
						netsurchargeAmount = netsurchargeAmount.multiply(minusValue).setScale(2,
								RoundingMode.HALF_DOWN);
						

						surchargeAmount=String.valueOf(netsurchargeAmount);
						
						acquirerSurchargeAmount= acquirerSurchargeAmount.multiply(minusValue);
						
						iPaySurchargeAmount= iPaySurchargeAmount.multiply(minusValue);
						mmadSurchargeAmount=mmadSurchargeAmount.multiply(minusValue);								
						
						iPayGstAmount = iPayGstAmount.multiply(minusValue);
						mmadGstAmount =  mmadGstAmount.multiply(minusValue);						
	
						acquirerGstAmount = acquirerGstAmount.multiply(minusValue);	
						totalAmtPaytoMerchant=totalAmtPaytoMerchant.multiply(minusValue);
						}
						
						
						

						String gstCalculateString = String.valueOf(gstCalculate);
						String totalSurchargeString = String.valueOf(totalSurcharge);
						String totalAmtPaytoMerchantString = String.valueOf(totalAmtPaytoMerchant);
						tdrPojo.setTotalAmtPaytoMerchant(totalAmtPaytoMerchantString);
						tdrPojo.setTotalGstOnMerchant(gstCalculateString);
						tdrPojo.setNetMerchantPayableAmount(totalAmtPaytoMerchantString);
						tdrPojo.setMerchantTdrCalculate(totalSurchargeString);
						tdrPojo.setTotalAmount(surchargeAmount);
						tdrPojo.setAcquirerSurchargeAmount(String.valueOf(acquirerSurchargeAmount));
						tdrPojo.setPgSurchargeAmount(String.valueOf(pgSurchargeAmount));
						tdrPojo.setiPaySurchargeAmount(String.valueOf(iPaySurchargeAmount));
						tdrPojo.setMmadSurchargeAmount(String.valueOf(mmadSurchargeAmount));
					}
				} else {

					ChargingDetails chargingDetails = cdf.getChargingDetailForReport(transactionSearch.getDateFrom(),
							payId, transactionSearch.getAcquirerType(), transactionSearch.getPaymentMethods(),
							transactionSearch.getMopType(), transactionSearch.getTxnType(),
							transactionSearch.getCurrency());
					tdrPojo = chargiesCalculation(chargingDetails.getBankTDR(), chargingDetails.getPgFixCharge(),
							chargingDetails.getPgTDR(), chargingDetails.getBankFixCharge(),
							chargingDetails.getMerchantFixCharge(), chargingDetails.getMerchantTDR(), st, amount);
				}

				if (!tdrPojo.equals(null)) {
					transactionSearch.setAmount(tdrPojo.getTotalAmount());
					transactionSearch.setMerchants(bussinessName);
					transactionSearch.setTdrScAcquirer(tdrPojo.getAcquirerSurchargeAmount());
					transactionSearch.setTdrScIpay(tdrPojo.getiPaySurchargeAmount());
					transactionSearch.setGstScAcquirer(String.valueOf(iPayGstAmount));
					transactionSearch.setGstScIpay(String.valueOf(mmadGstAmount));
					transactionSearch.setTotalAmount(tdrPojo.getTotalAmount());
					transactionSearch.setNetMerchantPayableAmount(String.valueOf(tdrPojo.getNetMerchantPayableAmount()));
				}
				transactionList1.add(transactionSearch);
			}
		}
		return transactionList1;
	}
	*/
	
	
	@Override
	public void validate() {
		
		if (validator.validateBlankField(getDateTo())) {
		} else if (!validator.validateField(CrmFieldType.DATE_TO, getDateTo())) {
			addFieldError(CrmFieldType.DATE_TO.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
		}

		if (!validator.validateBlankField(getDateTo())) {
			if (DateCreater.formatStringToDate(DateCreater.formatFromDate(getDateFrom()))
					.compareTo(DateCreater.formatStringToDate(DateCreater.formatFromDate(getDateTo()))) > 0) {
				addFieldError(CrmFieldType.DATE_FROM.getName(), CrmFieldConstants.FROMTO_DATE_VALIDATION.getValue());
			} else if (DateCreater.diffDate(getDateFrom(), getDateTo()) > 7) {
				addFieldError(CrmFieldType.DATE_FROM.getName(), CrmFieldConstants.DATE_RANGE.getValue());
			}
		}
	}

	public InputStream getFileInputStream() {
		return fileInputStream;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(String dateFrom) {
		this.dateFrom = dateFrom;
	}

	public String getDateTo() {
		return dateTo;
	}

	public void setDateTo(String dateTo) {
		this.dateTo = dateTo;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getPaymentsRegion() {
		return paymentsRegion;
	}

	public void setPaymentsRegion(String paymentsRegion) {
		this.paymentsRegion = paymentsRegion;
	}

	public String getCardHolderType() {
		return cardHolderType;
	}

	public void setCardHolderType(String cardHolderType) {
		this.cardHolderType = cardHolderType;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMerchantPayId() {
		return merchantPayId;
	}

	public void setMerchantPayId(String merchantPayId) {
		this.merchantPayId = merchantPayId;
	}


	public String getAcquirer() {
		return acquirer;
	}


	public void setAcquirer(String acquirer) {
		this.acquirer = acquirer;
	}


	public String getPaymentMethods() {
		return paymentMethods;
	}


	public void setPaymentMethods(String paymentMethods) {
		this.paymentMethods = paymentMethods;
	}


	public String getMopType() {
		return mopType;
	}


	public void setMopType(String mopType) {
		this.mopType = mopType;
	}


	public String getSettlementStatus() {
		return settlementStatus;
	}


	public void setSettlementStatus(String settlementStatus) {
		this.settlementStatus = settlementStatus;
	}
	

}
