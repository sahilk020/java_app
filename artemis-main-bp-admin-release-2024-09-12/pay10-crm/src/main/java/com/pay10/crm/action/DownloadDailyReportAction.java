package com.pay10.crm.action;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.user.DailyReportObject;
import com.pay10.commons.user.User;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.DateCreater;
import com.pay10.commons.util.PropertiesManager;

public class DownloadDailyReportAction extends AbstractSecureAction {

	

	/**
	 * 
	 */
	private static final long serialVersionUID = -1566672805114483048L;

	private static Logger logger = LoggerFactory.getLogger(DownloadDailyReportAction.class.getName());

	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
	private String currentDate;
	private String currentDateFrom;
	private String currentDateTo;
	private InputStream fileInputStream;
	private String filename;
	private User sessionUser = new User();

	private String responseMessage;
	
	public static final BigDecimal ONE_HUNDRED = new BigDecimal(100);

	@Autowired
	private DailyReportQuery dailyReportQuery;
	 @Autowired
	    private PropertiesManager propertiesManager;

	
	@Override
	@SuppressWarnings("resource")
	public String execute() {
		boolean countFlag = true;
		StringBuilder strBuilder = new StringBuilder();

		setCurrentDateFrom(DateCreater.toDateTimeformatCreater(currentDate));
		setCurrentDateTo(DateCreater.formDateTimeformatCreater(currentDate));

		sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		List<DailyReportObject> transactionList = new ArrayList<DailyReportObject>();

	//	countFlag = dailyReportQuery.dailyReportCount(currentDateFrom, currentDateTo);
		if (countFlag == true) {

			try {
				transactionList = dailyReportQuery.dailyReportDownload(currentDateFrom, currentDateTo, sessionMap.get(Constants.SEGMENT.getValue()).toString(), sessionUser.getRole().getId());
				if (transactionList.isEmpty()) {
					setResponseMessage("There is no Data Available");
					logger.info("There is no Data Available");
					return SUCCESS;
				}

			} catch (Exception e) {
				logger.error("Exception", e);
			}
			String seperator = "|";
			for (DailyReportObject builder : transactionList) {
				
				strBuilder.append(builder.getPgRefNum());
				strBuilder.append(seperator);
				strBuilder.append(builder.getPaymentMethod());
				strBuilder.append(seperator);
				strBuilder.append(builder.getMopType());
				strBuilder.append(seperator);
				strBuilder.append(builder.getOrderId());
				strBuilder.append(seperator);
				strBuilder.append(builder.getBusinessName());
				strBuilder.append(seperator);
				strBuilder.append(builder.getCurrency());
				strBuilder.append(seperator);
				strBuilder.append(builder.getTxnType());
				strBuilder.append(seperator);
				strBuilder.append(builder.getCaptureDate());
				strBuilder.append(seperator);
				strBuilder.append(builder.getTransactionRegion());
				strBuilder.append(seperator);
				strBuilder.append(builder.getCardHolderType());
				strBuilder.append(seperator);
				strBuilder.append(builder.getAcquirer());
				strBuilder.append(seperator);
				strBuilder.append(builder.getTotalAmount());
				strBuilder.append(seperator);
				strBuilder.append(builder.getSurchargeAcquirer());
				strBuilder.append(seperator);
				strBuilder.append(builder.getSurchargeIpay());
				strBuilder.append(seperator);
				strBuilder.append(builder.getGstAcq());
				strBuilder.append(seperator);
				strBuilder.append(builder.getGstIpay());
				strBuilder.append(seperator);
				strBuilder.append(builder.getMerchantAmount());
				strBuilder.append(seperator);

				if (StringUtils.isBlank(builder.getAcqId())) {
					strBuilder.append("null");
				} else {
					strBuilder.append(builder.getAcqId());
				}

				strBuilder.append(seperator);

				if (StringUtils.isBlank(builder.getRrn())) {
					strBuilder.append("null");
				} else {
					strBuilder.append(builder.getRrn());
				}

				strBuilder.append(seperator);

				if (StringUtils.isBlank(builder.getPostSettledFlag())) {
					strBuilder.append("null");
				} else {
					strBuilder.append(builder.getPostSettledFlag());
				}

				strBuilder.append(seperator);

				if (StringUtils.isBlank(builder.getRefundFlag())) {
					strBuilder.append("null");
				} else {
					strBuilder.append(builder.getRefundFlag());
					;
				}
				strBuilder.append(seperator);

				if (StringUtils.isBlank(builder.getRefundOrderId())) {
					strBuilder.append("null");
				} else {
					strBuilder.append(builder.getRefundOrderId());
				}
				strBuilder.append("\r\n");
			}

			logger.info("file has been created successfully!!");
			try {
				String FILE_EXTENSION = ".txt";
				
//     			String path = "E:/home/";
				String path = PropertiesManager.propertiesMap.get("DailyTransactionFilePath");
				String transactionDate = DateCreater.formatCaptureDate(currentDate);
				
				Date date = new Date();
				Format formatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
				String createdDate = formatter.format(date);
				String createdTag = "__DateGenerated_"+createdDate;
				String totalTag = "__total_"+transactionList.size();
				filename = "CapturedTxnData_" + transactionDate +createdTag+totalTag+ FILE_EXTENSION;
				File file = new File(PropertiesManager.propertiesMap.get(Constants.DOWNLOAD_PATH.getValue()) +File.separator+filename);
	            
	              logger.info("moni........"+PropertiesManager.propertiesMap.get(Constants.DOWNLOAD_PATH.getValue()));
				// this Writes the workbook
				FileOutputStream out = new FileOutputStream(file);
				//File file = new File(path + filename);
				file.createNewFile();
				// Write Content
				FileWriter writer = new FileWriter(file);
				writer.write(strBuilder.toString());
				writer.close();
				// fileInputStream = new FileInputStream(file);
				logger.info("File has been saved successfully on disk !!");
				setResponseMessage("Total "+ transactionList.size()+" captured transactions added to file "+filename);
			}

			catch (Exception exception) {
				logger.error("Inside DownloadDailyReportAction Class  : ", exception);
			}
		} else {

			setResponseMessage("There is no Data Available");
			logger.info("There is no Data Available !!");
		}
		return SUCCESS;

	}
		
		
		


	public InputStream getFileInputStream() {
		return fileInputStream;
	}

	public void setFileInputStream(InputStream fileInputStream) {
		this.fileInputStream = fileInputStream;
	}
	
	public String getCurrentDateFrom() {
		return currentDateFrom;
	}

	public void setCurrentDateFrom(String currentDateFrom) {
		this.currentDateFrom = currentDateFrom;
	}

	public String getCurrentDateTo() {
		return currentDateTo;
	}

	public void setCurrentDateTo(String currentDateTo) {
		this.currentDateTo = currentDateTo;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getCurrentDate() {
		return currentDate;
	}

	public void setCurrentDate(String currentDate) {
		this.currentDate = currentDate;
	}


	public String getResponseMessage() {
		return responseMessage;
	}





	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	

	
	

}
