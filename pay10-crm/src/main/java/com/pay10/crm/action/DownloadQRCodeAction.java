package com.pay10.crm.action;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.dao.InvoiceDao;
import com.pay10.commons.user.Invoice;
import com.pay10.commons.user.InvoiceTransactionDao;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.QRCodeCreator;

public class DownloadQRCodeAction extends AbstractSecureAction {
	
	@Autowired
	private InvoiceTransactionDao invoiceTransactionDao;
	
	@Autowired
	private InvoiceDao invoiceDao;
	
	@Autowired
	private QRCodeCreator qRCodeCreator;
	@Autowired
	private CrmValidator validator;
	

	private static final long serialVersionUID = -5708726455052826940L;
	private InputStream fileInputStream;
	private static Logger logger = LoggerFactory
			.getLogger(DownloadQRCodeAction.class.getName());
	private String invoiceId;
	String fileName;
	public String downloadQRCode() {
		try {

			Invoice invoiceDB = invoiceDao.findByInvoiceId(getInvoiceId());

			BufferedImage image = qRCodeCreator.generateQRCode(invoiceDB);
			setFileName("QR_" + invoiceDB.getInvoiceNo() + ".jpg");
			File file = new File("qrcode.jpg");
			ImageIO.write(image, "jpg", file);
			fileInputStream = new FileInputStream(file);

		} catch (Exception exception) {
			//TODO dispaly message to user
			logger.error("Exception unable to generate QR code", exception);
			return SUCCESS;
		}
		return SUCCESS;

	}
	@Override
	public void validate(){

	if ((validator.validateBlankField(getInvoiceId()))) {
		addFieldError(CrmFieldType.INVOICE_ID.getName(), validator.getResonseObject().getResponseMessage());
	} else if (!(validator.validateField(CrmFieldType.INVOICE_ID, getInvoiceId()))) {
		addFieldError(CrmFieldType.INVOICE_ID.getName(), validator.getResonseObject().getResponseMessage());
	}
	}

	public InputStream getFileInputStream() {
		return fileInputStream;
	}

	public String getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(String invoiceId) {
		this.invoiceId = invoiceId;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}
