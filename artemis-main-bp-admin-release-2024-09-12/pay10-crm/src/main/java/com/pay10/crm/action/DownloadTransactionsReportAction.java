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

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.user.PaymentSearchDownloadObject;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.DateCreater;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;
import com.pay10.crm.mongoReports.TxnReports;

public class DownloadTransactionsReportAction extends AbstractSecureAction {

    private static final long serialVersionUID = -8129011751068997117L;
    private static Logger logger = LoggerFactory.getLogger(DownloadTransactionsReportAction.class.getName());
    private String currency="ALL";
    private String dateFrom;
    private String dateTo;
    private String merchantPayId;
    private String paymentType;
    private String transactionType;
    private String reportType;
    private String status;
    private String acquirer;
    private InputStream fileInputStream;
    private String filename;
    private User sessionUser = new User();
    private String paymentsRegion;
    private String cardHolderType;
    private String IpAddress;
    private String custMobileNo;
    private String custEmail;
private String channel;
	
	

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}
    public String getCustMobileNo() {
        return custMobileNo;
    }

    public void setCustMobileNo(String custMobileNo) {
        this.custMobileNo = custMobileNo;
    }

    public String getCustEmail() {
        return custEmail;
    }

    public void setCustEmail(String custEmail) {
        this.custEmail = custEmail;
    }


    public String getIpAddress() {
        return IpAddress;
    }

    public void setIpAddress(String ipAddress) {
        IpAddress = ipAddress;
    }


    public static final BigDecimal ONE_HUNDRED = new BigDecimal(100);

    @Autowired
    private TxnReports txnReports;
    @Autowired
    private PropertiesManager propertiesManager;

    @Override
    @SuppressWarnings("resource")
    public String execute() {

        if (acquirer == null || acquirer.isEmpty()) {
            acquirer = "ALL";

        }

        if (paymentsRegion == null || paymentsRegion.isEmpty()) {
            paymentsRegion = "ALL";

        }

        if (getReportType().equalsIgnoreCase("saleCaptured")) {
            transactionType = TransactionType.SALE.getName();
            status = StatusType.CAPTURED.getName();
        } else if (getReportType().equalsIgnoreCase("refundCaptured")) {

            transactionType = TransactionType.REFUND.getName();
            status = StatusType.CAPTURED.getName();

        } else {
        	// Done By chetan nagaria for change in settlement process to mark transaction as RNS
        	status = StatusType.SETTLED_SETTLE.getName();
//        	status = StatusType.SETTLED.getName();
        }

        sessionUser = (User) sessionMap.get(Constants.USER.getValue());
        List<PaymentSearchDownloadObject> transactionList = new ArrayList<PaymentSearchDownloadObject>();
        //Added By Sweety
        dateFrom = DateCreater.formatDate1(dateFrom);
        dateTo = DateCreater.formatDate1(dateTo);
        //Ended
        setDateFrom(DateCreater.toDateTimeformatCreater(dateFrom));
        setDateTo(DateCreater.formDateTimeformatCreater(dateTo));
        logger.info("getdateFrom()={}",dateFrom);
        logger.info("getdateTo()={}",dateTo);
        if (sessionUser.getUserType().equals(UserType.SUBADMIN) && sessionUser.getSegment().equalsIgnoreCase("Split-Payment")) {

        	transactionList = txnReports.searchPaymentForDownloadSplitPayment(merchantPayId, paymentType, status, currency, transactionType,
        			dateFrom,dateTo, sessionUser, paymentsRegion, acquirer);

		}else {
			transactionList = txnReports.searchPaymentForDownload(merchantPayId, paymentType, status, currency, transactionType,
					dateFrom,dateTo, sessionUser, paymentsRegion, acquirer,channel);
		}

        logger.info("List generated successfully for DownloadTransactionsReportAction , size = " + transactionList.size());
        SXSSFWorkbook wb = new SXSSFWorkbook(100);
        Row row;
        int rownum = 1;
        // Create a blank sheet
        Sheet sheet = wb.createSheet("Payments Report");

        row = sheet.createRow(0);
        if (getReportType().equalsIgnoreCase("saleCaptured")) {
            row.createCell(0).setCellValue("Sr No");
            row.createCell(1).setCellValue("Txn Id");
            row.createCell(2).setCellValue("Pg Ref Num");
            row.createCell(3).setCellValue("Merchant");
            row.createCell(4).setCellValue("Date");
            row.createCell(5).setCellValue("Order Id");
            row.createCell(6).setCellValue("Payment Method");
            row.createCell(7).setCellValue("Txn Type");
            row.createCell(8).setCellValue("Status");
            row.createCell(9).setCellValue("Transaction Region");
            row.createCell(10).setCellValue("Base Amount");
            row.createCell(11).setCellValue("Total Amount");
            row.createCell(12).setCellValue("Post Settled Flag");
            row.createCell(13).setCellValue("RRN");
            row.createCell(14).setCellValue("ARN");
            row.createCell(15).setCellValue("ACQ ID");
            row.createCell(16).setCellValue("Mop Type");
            /*added by vijaylakshmi*/
            row.createCell(17).setCellValue("Cust Email");
            row.createCell(18).setCellValue("CUST PHONE");
            row.createCell(19).setCellValue("Ip Address");
            //added by deep
            row.createCell(20).setCellValue("UDF4");
            row.createCell(21).setCellValue("UDF5");
            row.createCell(22).setCellValue("UDF6");
            


            if (sessionUser.getUserType().equals(UserType.ADMIN) || sessionUser.getUserType().equals(UserType.SUBADMIN)) {
                row.createCell(23).setCellValue("Acquirer Type");
            }
        } else if (getReportType().equalsIgnoreCase("refundCaptured")) {
            row.createCell(0).setCellValue("Sr No");
            row.createCell(1).setCellValue("Txn Id");
            row.createCell(2).setCellValue("Pg Ref Num");
            row.createCell(3).setCellValue("Merchant");
            row.createCell(4).setCellValue("Date");
            row.createCell(5).setCellValue("Order Id");
            row.createCell(6).setCellValue("Refund Order Id");
            row.createCell(7).setCellValue("Payment Method");
            row.createCell(8).setCellValue("Txn Type");
            row.createCell(9).setCellValue("Status");
            row.createCell(10).setCellValue("Transaction Region");
            row.createCell(11).setCellValue("Base Amount");
            row.createCell(12).setCellValue("Total Amount");
            row.createCell(13).setCellValue("Post Settled Flag");
            row.createCell(14).setCellValue("RRN");
            row.createCell(15).setCellValue("ARN");
            row.createCell(16).setCellValue("ACQ ID");
            row.createCell(17).setCellValue("Mop Type");
            /*added by vijaylakshmi*/
            row.createCell(18).setCellValue("Cust Email");
            row.createCell(19).setCellValue("CUST PHONE");
            row.createCell(20).setCellValue("Ip Address");
            row.createCell(21).setCellValue("UDF4");
            row.createCell(22).setCellValue("UDF5");
            row.createCell(23).setCellValue("UDF6");

            if (sessionUser.getUserType().equals(UserType.ADMIN) || sessionUser.getUserType().equals(UserType.SUBADMIN)) {
                row.createCell(24).setCellValue("Acquirer Type");
            }
        } else {
            row.createCell(0).setCellValue("Sr No");
            row.createCell(1).setCellValue("Txn Id");
            row.createCell(2).setCellValue("Pg Ref Num");
            row.createCell(3).setCellValue("Merchant");
            row.createCell(4).setCellValue("Capture Date");
            row.createCell(5).setCellValue("Settled Date");
            row.createCell(6).setCellValue("Order Id");
            row.createCell(7).setCellValue("Payment Method");
            row.createCell(8).setCellValue("Txn Type");
            row.createCell(9).setCellValue("Status");
            row.createCell(10).setCellValue("Transaction Region");
            row.createCell(11).setCellValue("Base Amount");
            row.createCell(12).setCellValue("Total Amount");
            row.createCell(13).setCellValue("TDR / Surcharge");
            row.createCell(14).setCellValue("IGST");
            row.createCell(15).setCellValue("CGST");
            row.createCell(16).setCellValue("SGST");
            row.createCell(17).setCellValue("Total GST");
            row.createCell(18).setCellValue("Net Amount");
            row.createCell(19).setCellValue("Post Settled Flag");
            row.createCell(20).setCellValue("RRN");
            row.createCell(21).setCellValue("ARN");
            row.createCell(22).setCellValue("ACQ ID");
            row.createCell(23).setCellValue("Mop Type");
            /*added by vijaylakshmi*/
            row.createCell(24).setCellValue("Cust Email");
            row.createCell(25).setCellValue("CUST PHONE");
            row.createCell(26).setCellValue("Ip Address");
            //added by deep
            row.createCell(27).setCellValue("UDF4");
            row.createCell(28).setCellValue("UDF5");
            row.createCell(29).setCellValue("UDF6");
            row.createCell(30).setCellValue("UTR_NO");

            if (sessionUser.getUserType().equals(UserType.ADMIN) || sessionUser.getUserType().equals(UserType.SUBADMIN)) {
                row.createCell(31).setCellValue("Acquirer Type");
            }
            
        }

        if (transactionList.size() < 800000) {

            for (PaymentSearchDownloadObject transactionSearch : transactionList) {

                row = sheet.createRow(rownum++);
                transactionSearch.setSrNo(String.valueOf(rownum - 1));
                Object[] objArr = null;
                if (getReportType().equalsIgnoreCase("saleCaptured")) {
                    objArr = transactionSearch.myCsvMethodDownloadPaymentsforSaleCaptured();
                } else if (getReportType().equalsIgnoreCase("refundCaptured")) {
                    objArr = transactionSearch.myCsvMethodDownloadPaymentsforRefundCaptured();
                } else {
                    objArr = transactionSearch.myCsvMethodDownloadPaymentsforSettled();
                }


                int cellnum = 0;
                for (Object obj : objArr) {
                    // this line creates a cell in the next column of that row
                    Cell cell = row.createCell(cellnum++);
                    if (obj instanceof String)
                        cell.setCellValue((String) obj);
                    else if (obj instanceof Integer)
                        cell.setCellValue((Integer) obj);

                }
                Object[] objArr2 = null;
                if (sessionUser.getUserType().equals(UserType.ADMIN) || sessionUser.getUserType().equals(UserType.SUBADMIN)) {
                    objArr2 = transactionSearch.myCsvMethodDownloadPaymentsforAdminfiellds();
                    for (Object obj : objArr2) {
                        // this line creates a cell in the next column of that row
                        Cell cell = row.createCell(cellnum++);
                        if (obj instanceof String)
                            cell.setCellValue((String) obj);
                        else if (obj instanceof Integer)
                            cell.setCellValue((Integer) obj);

                    }
                }
            }
        } else {
            row = sheet.createRow(rownum++);

            Cell cell = row.createCell(1);
            cell.setCellValue("Data limit exceeded for excel file generation , please select a smaller date range");
        }

        try {
            String FILE_EXTENSION = ".xlsx";
            DateFormat df = new SimpleDateFormat("yyyyMMddhhmmss");

            if (getReportType().equalsIgnoreCase("saleCaptured")) {
                filename = "Sale_Captured_Report_" + df.format(new Date()) + FILE_EXTENSION;
            } else if (getReportType().equalsIgnoreCase("refundCaptured")) {
                filename = "Refund_Captured_Report_" + df.format(new Date()) + FILE_EXTENSION;
            } else {
                filename = "Settled_Report_" + df.format(new Date()) + FILE_EXTENSION;
            }
            File file = new File(PropertiesManager.propertiesMap.get(Constants.DOWNLOAD_PATH.getValue()) +File.separator+filename);
            
            logger.info("moni........"+PropertiesManager.propertiesMap.get(Constants.DOWNLOAD_PATH.getValue()));
			// this Writes the workbook
			FileOutputStream out = new FileOutputStream(file);
            wb.write(out);
            out.flush();
            out.close();
            wb.dispose();
            fileInputStream = new FileInputStream(file);
            logger.info("moni>>>>>>"+file);
            addActionMessage(filename + " written successfully on disk.");
        } catch (Exception exception) {
            logger.error("Exception", exception);
        }
        logger.info("Files generated successfully for DownloadTransactionsReportAction");
        return SUCCESS;

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


    public String getReportType() {
        return reportType;
    }


    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

}
