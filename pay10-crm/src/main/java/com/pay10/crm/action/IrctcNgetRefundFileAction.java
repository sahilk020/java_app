package com.pay10.crm.action;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.dao.OfflineRefundDao;
import com.pay10.commons.entity.IRCTCRefundFile;
import com.pay10.commons.user.AcquirerSchadular;
import com.pay10.commons.user.AcquirerSchadulardao;

public class IrctcNgetRefundFileAction extends AbstractSecureAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(IrctcNgetRefundFileAction.class.getName());
	private String acquirer;
	private Long id;
	private String response;
	private String Maxtime;
	private String Mintime;
	private Long idedit;
	private String date;
	private String filename;
	private String filepath;
	private InputStream fileInputStream;

	private List<IRCTCRefundFile> irctcRefundFileInfo = new ArrayList<IRCTCRefundFile>();
	private List<AcquirerSchadular> acquirerSchadular = new ArrayList<AcquirerSchadular>();
	private List<AcquirerSchadular> searchSchadular = new ArrayList<AcquirerSchadular>();

	@Autowired
	AcquirerSchadulardao acquirerSchadulardao;

	@Autowired
	OfflineRefundDao offlineRefundDao;

	public String execute() {
		logger.info("*********** IrctcRefundFileAction execute()***********");
		return INPUT;
	}

	public String getRefundFileDatails() {
		logger.info("*********** IrctcRefundFileAction getRefundFileDatails()***********");
		logger.info("Date : " + date);
		logger.info("Info : " + offlineRefundDao.getIRCTCRefundFile(date));
		setOfflineRefund(offlineRefundDao.getIRCTCRefundFile(date));
		return SUCCESS;

	}

	public String RefundFileDatails() {
		logger.info("*********** IrctcRefundFileAction RefundFileDatails()***********");
		logger.info("filename " + filename);
		logger.info("filepath " + filepath);
		File file = new File(filepath);
		@SuppressWarnings("resource")
		SXSSFWorkbook wb = new SXSSFWorkbook(100);
		try {
			FileOutputStream out = new FileOutputStream(file);
			try {
				wb.write(out);
				out.flush();
				out.close();
				wb.dispose();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			fileInputStream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return SUCCESS;
	}

	private String downloadUsingStream(String urlStr, String file) throws IOException {
		logger.info("*********** IrctcRefundFileAction downloadUsingStream()***********");
		URL url = new URL(urlStr);
		BufferedInputStream bis = new BufferedInputStream(url.openStream());
		FileOutputStream fis = new FileOutputStream(file);
		byte[] buffer = new byte[1024];
		int count = 0;
		while ((count = bis.read(buffer, 0, 1024)) != -1) {
			fis.write(buffer, 0, count);
		}
		fis.close();
		bis.close();

		return SUCCESS;
	}

	public String SearchDetailsdata() {
		logger.info("*********** IrctcRefundFileAction SearchDetailsdata()***********");
		setSearchSchadular(acquirerSchadulardao.getSearchDetails(acquirer));
		return SUCCESS;

	}

	public String getAcquirer() {
		return acquirer;
	}

	public void setAcquirer(String acquirer) {
		this.acquirer = acquirer;
	}

	public List<AcquirerSchadular> getAcquirerSchadular() {
		return acquirerSchadular;
	}

	public void setAcquirerSchadular(List<AcquirerSchadular> acquirerSchadular) {
		this.acquirerSchadular = acquirerSchadular;
	}

	public List<IRCTCRefundFile> getOfflineRefund() {
		return irctcRefundFileInfo;
	}

	public void setOfflineRefund(List<IRCTCRefundFile> irctcRefundFileInfo) {
		this.irctcRefundFileInfo = irctcRefundFileInfo;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public String getMaxtime() {
		return Maxtime;
	}

	public void setMaxtime(String maxtime) {
		Maxtime = maxtime;
	}

	public String getMintime() {
		return Mintime;
	}

	public void setMintime(String mintime) {
		Mintime = mintime;
	}

	public Long getIdedit() {
		return idedit;
	}

	public void setIdedit(Long idedit) {
		this.idedit = idedit;
	}

	public List<AcquirerSchadular> getSearchSchadular() {
		return searchSchadular;
	}

	public void setSearchSchadular(List<AcquirerSchadular> searchSchadular) {
		this.searchSchadular = searchSchadular;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}

	public InputStream getFileInputStream() {
		return fileInputStream;
	}

}
