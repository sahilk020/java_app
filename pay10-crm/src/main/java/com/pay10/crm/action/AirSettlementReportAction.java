package com.pay10.crm.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.user.AirSettlement;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.DateCreater;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.crm.actionBeans.SessionUserIdentifier;
import com.pay10.crm.mongoReports.AirSettlementReportData;

/**
 * @author Chandan
 *
 */

public class AirSettlementReportAction extends AbstractSecureAction {

	private static final long serialVersionUID = 5067142842901261304L;
	private static Logger logger = LoggerFactory.getLogger(AirSettlementReportAction.class.getName());
	

	private String merchantPayId;
	private String currency;
	private String saleDate;
	private InputStream fileInputStream;
	private String filename;
	
	private User sessionUser = new User();
	
	@Autowired
	private SessionUserIdentifier userIdentifier;
	 @Autowired
	    private PropertiesManager propertiesManager;
	
	@Autowired
	private AirSettlementReportData airSettlementReportData;
	
	@Autowired
	private UserDao userdao;
	
	@Override
	public String execute() {

		if (currency == null || currency.isEmpty()) {
			currency = "ALL";
		}
		
		/*status = StatusType.SETTLED.getName();
		txnType= TransactionType.RECO.getName();*/
		sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		List<AirSettlement> airSettlementList = new ArrayList<AirSettlement>();
		setSaleDate(DateCreater.toDateTimeformatCreater(saleDate));
		airSettlementList = airSettlementReportData.downloadSettlementReport( merchantPayId,currency, saleDate,sessionUser);
		
			StringBuilder strBuilder = new StringBuilder();
			for (AirSettlement airSettlement : airSettlementList) {
				
			    String seperator = "|";
				strBuilder.append(airSettlement.getPgRefNum());
				strBuilder.append(seperator);
				strBuilder.append(airSettlement.getAmount());
				strBuilder.append(seperator);
				strBuilder.append(airSettlement.getSettlementDate());
				strBuilder.append(seperator);
				strBuilder.append(airSettlement.getOrderId());
				strBuilder.append(seperator);
				strBuilder.append(airSettlement.getSaleDate());
				strBuilder.append("\r\n");
		}		

		try {
			String FILE_EXTENSION = ".txt";
			String settlementNC = "";
			settlementNC = userdao.getSettlementNCByPayId(getMerchantPayId());
			//DateFormat df = new SimpleDateFormat("yyyyMMddhhmmss");
			//filename = "Settlement_IRCTCPAYAIR_NBDR_" + DateCreater.formatSaleDate(getSaleDate()) + FILE_EXTENSION;
			if(StringUtils.isBlank(settlementNC)) {
				filename = "Settlement_IRCTCPAYAIR_NBDR_" + DateCreater.formatSaleDate(getSaleDate()) + FILE_EXTENSION;
			} else {
				filename = "Settlement_"+ settlementNC +"_" + DateCreater.formatSaleDate(getSaleDate()) + FILE_EXTENSION;
			}
			File file = new File(PropertiesManager.propertiesMap.get(Constants.DOWNLOAD_PATH.getValue()) +File.separator+filename);
            
            logger.info("moni........"+PropertiesManager.propertiesMap.get(Constants.DOWNLOAD_PATH.getValue()));
			// this Writes the workbook
			FileOutputStream out = new FileOutputStream(file);;
			file.createNewFile();  			
			 
			//Write Content
			FileWriter writer = new FileWriter(file);
			writer.write(strBuilder.toString());
			writer.close();
			setFileInputStream(new FileInputStream(file));
			addActionMessage(filename + " written successfully on disk.");
		
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		
		return SUCCESS;
	}
	
	public String getMerchantPayId() {
		return merchantPayId;
	}

	public void setMerchantPayId(String merchantPayId) {
		this.merchantPayId = merchantPayId;
	}

	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getSaleDate() {
		return saleDate;
	}

	public void setSaleDate(String saleDate) {
		this.saleDate = saleDate;
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
