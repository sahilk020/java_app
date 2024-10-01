package com.pay10.crm.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.user.TransactionSearchDownloadObject;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.DateCreater;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.crm.actionBeans.SessionUserIdentifier;
import com.pay10.crm.mongoReports.TxnReports;

public class DownloadRefundSearch extends AbstractSecureAction {

	private static final long serialVersionUID = 2871252777725723745L;
	private static Logger logger = LoggerFactory.getLogger(DownloadRefundSearch.class.getName());

	@Autowired
	private SessionUserIdentifier userIdentifier;
	 @Autowired
	    private PropertiesManager propertiesManager;
	
	private InputStream fileInputStream;
	private String filename;
	private User sessionUser = new User();
	
	private String fileLocation;




	



	public static final BigDecimal ONE_HUNDRED = new BigDecimal(100);

	@Autowired
	private TxnReports txnReports;

	@Override
	@SuppressWarnings("resource")
	public String execute() {



		try {
			logger.info("................................"+fileLocation);

			File file = new File(fileLocation);
            
			filename=file.getName();
			logger.info("................................"+filename);

			fileInputStream = new FileInputStream(file);
			addActionMessage(filename + " written successfully on disk.");
			logger.info("File generated successfully for DownloadPaymentsReportAction");
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}

		return SUCCESS;

	}

	public String getFileLocation() {
		return fileLocation;
	}

	public void setFileLocation(String fileLocation) {
		this.fileLocation = fileLocation;
	}

	public InputStream getFileInputStream() {
		return fileInputStream;
	}

	public void setFileInputStream(InputStream fileInputStream) {
		this.fileInputStream = fileInputStream;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	
	

}
