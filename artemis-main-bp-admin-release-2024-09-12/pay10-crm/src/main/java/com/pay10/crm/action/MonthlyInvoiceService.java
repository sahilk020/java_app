package com.pay10.crm.action;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.pay10.commons.user.CompanyProfile;
import com.pay10.commons.user.InvoiceNumberCounter;
import com.pay10.commons.user.InvoiceNumberCounterDao;
import com.pay10.commons.user.User;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.States;

/**
 * @author Rajendra
 *
 */

@Service
public class MonthlyInvoiceService {

	private static InvoiceNumberCounterDao incdo = new InvoiceNumberCounterDao();

	private static final Font catFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD);
	private static final Font headingFont = new Font(Font.FontFamily.HELVETICA, 15, Font.BOLD);
	private static final Font subCatFont = new Font(Font.FontFamily.HELVETICA, 10);
	private static final Font smallFont = new Font(Font.FontFamily.COURIER, 8);
	private static final String companyLogoLocation = Constants.COMPANY_LOGO_LOCATION.getValue();

	private static Logger logger = LoggerFactory.getLogger(MonthlyInvoiceService.class.getName());

	public File createPdfFile(Map<String, String> dataProviderMap, CompanyProfile cp, User user,
			Map<String, String> amountMap, String invoiceDateTo, File file) throws Exception {
		String serverFilename;
		serverFilename = "_Invoice_" + invoiceDateTo + ".pdf";
		String filePath = PropertiesManager.propertiesMap.get(Constants.MONTHLY_INVOICE_DESTINATION_PATH.getValue());
		filePath += user.getPayId();
		File serverFile = new File(filePath + serverFilename);

		if (!serverFile.exists()) {
			logger.info("File doesn't exist : " + serverFile.getAbsolutePath());
			Document document = new Document(PageSize.A4, 20, 20, 20, 20);

			FileOutputStream out = new FileOutputStream(file);
			PdfWriter.getInstance(document, out);
			document.open();
			addInvoiceMetaData(document);
			addInvoiceContent(document, dataProviderMap, cp, user, amountMap, invoiceDateTo);
			document.close();

			filePath = PropertiesManager.propertiesMap.get(Constants.MONTHLY_INVOICE_DESTINATION_PATH.getValue());
			// Check whether folder exists.
			File destFile = new File(filePath);
			if (!destFile.exists()) {
				destFile.mkdirs();
			}

			destFile = new File(filePath + user.getPayId() + serverFilename);
			destFile.setExecutable(false);
			destFile.setWritable(false);
			FileUtils.copyFile(file, destFile);
		} else {
			FileUtils.copyFile(serverFile, file);
		}
		return file;
	}

	public void addInvoiceMetaData(Document document) {
		document.addTitle("Invoice");
		document.addSubject("Merchant invoice for Payment Gateway");
		document.addKeywords("Invoice, Payment gateway");
		document.addAuthor("Dinerao Payments Ltd, created on " + new Date());
	}

	public void addInvoiceContent(Document document, Map<String, String> dataProviderMap, CompanyProfile cp, User user,
			Map<String, String> amountMap, String invoiceDateTo) throws Exception, DocumentException,
			MalformedURLException, IOException, FileNotFoundException, ParseException {
		Paragraph mainHeadingParagraph = null;
		Paragraph logoParagraph = null;
		Paragraph custDetailsParagraph = null;
		Paragraph signatoryParagraph = null;
		Paragraph companyBankDetailsParagraph = null;

		mainHeadingParagraph = new Paragraph((String) dataProviderMap.get("MAIN_HEADING"), headingFont);
		mainHeadingParagraph.add(new Chunk(" \r\n" + "\n", subCatFont));
		mainHeadingParagraph.setAlignment(Element.ALIGN_CENTER);
		document.add(mainHeadingParagraph);
		File imageFile = new File(PropertiesManager.propertiesMap.get(companyLogoLocation));

		if (imageFile.exists()) {
			Image img2 = Image.getInstance(PropertiesManager.propertiesMap.get(companyLogoLocation));
			img2.scaleToFit(80f, 80f);
			img2.setAbsolutePosition(document.getPageSize().getWidth() - 36f - 355f,
					document.getPageSize().getHeight() - 36f - 125f);
			if (logoParagraph == null) {
				logoParagraph = new Paragraph();
				logoParagraph.add(img2);
			}
		}
		PdfPTable dataTable = new PdfPTable(2);
		PdfPTable datasubTable = new PdfPTable(2);
		float[] columnWidths = new float[] { 500f, 500f };
		dataTable.setWidths(columnWidths);
		dataTable.setWidthPercentage(100f);
		datasubTable.setWidths(columnWidths);
		datasubTable.setWidthPercentage(100f);
		custDetailsParagraph = new Paragraph();
		custDetailsParagraph.add(new Chunk(cp.getCompanyName(), catFont));
		custDetailsParagraph.add(new Chunk(" \r\n" + (cp.getAddress() != null ? cp.getAddress() : "") + "\r\n"
				+ (cp.getCity() != null ? cp.getCity() : "") + ", " + (cp.getState() != null ? cp.getState() : "") + ","
				+ (cp.getPostalCode() != null ? cp.getPostalCode() : "") + "\r\n" + "GSTIN/UIN:"
				+ (cp.getCompanyGstNo() != null ? cp.getCompanyGstNo() : "") + "\r\n" + "State Name: "
				+ (cp.getState() != null ? cp.getState() : "") + ", Code: "
				+ (States.getStateCodeByName(cp.getState()) != "-1" ? States.getStateCodeByName(cp.getState()) : "")
				+ "\r\n" + "CIN: " + (cp.getCin() != null ? cp.getCin() : "") + "\r\n" + "CIN: "
				+ (cp.getCin() != null ? cp.getTanNumber() : "") + "\r\n" + "E-Mail: "
				+ (cp.getEmailId() != null ? cp.getEmailId() : "") + "\r\n" + "Ph no:"
				+ (cp.getMobile() != null ? cp.getMobile() : "") + "\r\n", subCatFont));
		dataTable.addCell(custDetailsParagraph);
		custDetailsParagraph = new Paragraph("Invoice No.\r\n");


		DateFormat adf1 = new SimpleDateFormat(CrmFieldConstants.INPUT_DATE_FORMAT.getValue());
		Date MonthDate = adf1.parse(invoiceDateTo);

		DateFormat df1 = new SimpleDateFormat("yyMM");
		String todayDate1 = df1.format(MonthDate);

		int rand_int2 = genarateInvoiceNumber(user.getPayId()+todayDate1);
		String rand_inte2;
		if (rand_int2 <= 9) {
			rand_inte2 = "000" + String.valueOf(rand_int2);
		} else if (rand_int2 <= 99) {
			rand_inte2 = "00" + String.valueOf(rand_int2);
		} else if (rand_int2 <= 999) {
			rand_inte2 = "0" + String.valueOf(rand_int2);
		} else {
			rand_inte2 = String.valueOf(rand_int2);
		}
		
		custDetailsParagraph.add(new Chunk("CLSO/" + todayDate1 + "/" + rand_inte2, catFont));
		datasubTable.addCell(custDetailsParagraph);
		custDetailsParagraph = new Paragraph("Dated\r\n");

		// Date format
		DateFormat df = new SimpleDateFormat(CrmFieldConstants.INPUT_DATE_MONTH_NAME_FORMAT.getValue());
		String todayDate = df.format(MonthDate);

		custDetailsParagraph.add(new Chunk(todayDate, catFont));
		datasubTable.addCell(custDetailsParagraph);
		datasubTable.addCell("\n\n");
		datasubTable.addCell("Mode(or)Terms of Payment\r\n" + "\n");
		datasubTable.addCell("Supplier's Ref.");
		datasubTable.addCell("Other Reference(s)");
		dataTable.addCell(datasubTable);
		custDetailsParagraph = new Paragraph("Buyer\r\n");
		custDetailsParagraph.add(new Chunk(user.getCompanyName(), catFont));
		custDetailsParagraph.add(new Chunk("\r\n" + (user.getAddress() != null ? user.getAddress() : "") + "\r\n"
				+ (user.getCity() != null ? user.getCity() : "") + ", "
				+ (user.getState() != null ? user.getState() : "") + ", "
				+ (user.getPostalCode() != null ? user.getPostalCode() : "") + "\r\n" + "GSTIN/UIN: "
				+ (user.getMerchantGstNo() != null ? user.getMerchantGstNo() : "") + "\r\n" + "State Name: "
				+ (user.getState() != null ? user.getState() : "") + ", Code: "
				+ (States.getStateCodeByName(user.getState()) != "-1" ? States.getStateCodeByName(user.getState()) : "")
				+ "\r\n" + "CIN: " + (user.getCin() != null ? user.getCin() : "") + "\r\n" + "E-Mail: "
				+ (user.getEmailId() != null ? user.getEmailId() : "") + "\r\n" + "Ph no:"
				+ (user.getMobile() != null ? user.getMobile() : "") + "\r\n", subCatFont));
		dataTable.addCell(custDetailsParagraph);
		dataTable.addCell("Terms Of Delivery");
		dataTable.setHorizontalAlignment(Element.ALIGN_LEFT);
		document.add(dataTable);
		if (logoParagraph != null) {
			document.add(logoParagraph);
		}
		if(!amountMap.isEmpty()) {
			addInvoiceContentSub(document, dataProviderMap, cp, user, amountMap);
			addInvoiceContentSubtable(document, dataProviderMap, cp, user, amountMap);			
		}

		PdfPTable databottomendTable = new PdfPTable(2);
		databottomendTable.getDefaultCell().setBorder(Rectangle.RIGHT | Rectangle.BOTTOM);
		databottomendTable.setWidthPercentage(100f);
		float[] databottomendTableWidths = new float[] { 490f, 510f };
		databottomendTable.setWidths(databottomendTableWidths);
		databottomendTable.addCell("");
		companyBankDetailsParagraph = new Paragraph();
		companyBankDetailsParagraph.add(new Chunk("for " + cp.getCompanyName() + " \r\n", catFont));
		companyBankDetailsParagraph.add(new Chunk(" \r\n"));
		companyBankDetailsParagraph.add(new Chunk(" \r\n"));
		companyBankDetailsParagraph.add(new Chunk("\t\t\tAuthorised Signatory\r\n", subCatFont));
		companyBankDetailsParagraph.setAlignment(Element.ALIGN_RIGHT);
		databottomendTable.addCell(companyBankDetailsParagraph);
		document.add(databottomendTable);

		document.add(Chunk.NEWLINE);
		document.add(Chunk.NEWLINE);
		signatoryParagraph = new Paragraph("This is a Computer Generated Invoice", smallFont);
		signatoryParagraph.setAlignment(Element.ALIGN_CENTER);
		document.add(signatoryParagraph);
		document.close();
	}

	synchronized public static int genarateInvoiceNumber(String payId) {
		int invoiceSerialNumber = 0;
		invoiceSerialNumber = incdo.getInvoiceNumberByPayId(payId);
		return invoiceSerialNumber;
	}

	public void addInvoiceContentSubtable(Document document, Map<String, String> dataProviderMap2, CompanyProfile cp,
			User user, Map<String, String> amountMap)
			throws DocumentException, MalformedURLException, IOException, FileNotFoundException {
		Format format = com.ibm.icu.text.NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
		Paragraph amountParagraph = null;
		Paragraph companyBankDetailsParagraph = null;
		// Here invoice 2nd table related logic
		LineSeparator ls = new LineSeparator();

		PdfPTable invoicesubtable;
		if (user.getState().equalsIgnoreCase(cp.getState())) {
			invoicesubtable = new PdfPTable(7);
			float[] ivsubcolumnWidths = new float[] { 220f, 150f, 110f, 150f, 100f, 150f, 150f };
			invoicesubtable.setWidths(ivsubcolumnWidths);
			invoicesubtable.setWidthPercentage(100f);
			invoicesubtable.addCell("HSN/SAC Number");
			invoicesubtable.addCell("Taxable Value");
			invoicesubtable.addCell("Central Tax Rate");
			invoicesubtable.addCell("Central Tax Amount");
			invoicesubtable.addCell("State Tax Rate");
			invoicesubtable.addCell("State Tax Amount");
			invoicesubtable.addCell("Total Tax Amount");

			invoicesubtable.addCell(cp.getHsnSacCode() +"\r\n" + cp.getHsnSacCode());
			invoicesubtable.addCell(format.format(new BigDecimal(amountMap.get("amtPgTaxExempt"))) + " \r\n"
					+ format.format(new BigDecimal(amountMap.get("amtPgTaxable"))));
			invoicesubtable.addCell("0% \r\n" + amountMap.get("cgstpercent") + "%");
			invoicesubtable.addCell("\r\n" + format.format(new BigDecimal(amountMap.get("amtCGst"))));
			invoicesubtable.addCell("0% \r\n" + amountMap.get("sgstpercent") + "%");
			invoicesubtable.addCell("\r\n" + format.format(new BigDecimal(amountMap.get("amtSGst"))));
			invoicesubtable.addCell("\r\n" + format.format(new BigDecimal(amountMap.get("amtGst"))));

			invoicesubtable.addCell("Total");
			invoicesubtable.addCell(format.format(new BigDecimal(amountMap.get("amtPg"))));
			invoicesubtable.addCell("");
			invoicesubtable.addCell(format.format(new BigDecimal(amountMap.get("amtCGst"))));
			invoicesubtable.addCell("");
			invoicesubtable.addCell(format.format(new BigDecimal(amountMap.get("amtSGst"))));
			invoicesubtable.addCell(format.format(new BigDecimal(amountMap.get("amtGst"))));
		} else {
			invoicesubtable = new PdfPTable(5);
			float[] ivsubcolumnWidths = new float[] { 420f, 150f, 110f, 170f, 150f };
			invoicesubtable.setWidths(ivsubcolumnWidths);
			invoicesubtable.setWidthPercentage(100f);
			invoicesubtable.addCell("HSN/SAC Number");
			invoicesubtable.addCell("Taxable Value");
			invoicesubtable.addCell("Integrated Tax Rate");
			invoicesubtable.addCell("Integrated Tax Amount");
			invoicesubtable.addCell("Total Tax Amount");

			invoicesubtable.addCell(cp.getHsnSacCode() +"\r\n" + cp.getHsnSacCode());
			invoicesubtable.addCell(format.format(new BigDecimal(amountMap.get("amtPgTaxExempt"))) + " \r\n"
					+ format.format(new BigDecimal(amountMap.get("amtPgTaxable"))));
			invoicesubtable.addCell("0% \r\n" + amountMap.get("gstpercent"));
			invoicesubtable.addCell("\r\n" + format.format(new BigDecimal(amountMap.get("amtGst"))));
			invoicesubtable.addCell("\r\n" + format.format(new BigDecimal(amountMap.get("amtGst"))));

			invoicesubtable.addCell("Total");
			invoicesubtable.addCell(format.format(new BigDecimal(amountMap.get("amtPg"))));
			invoicesubtable.addCell("");
			invoicesubtable.addCell(format.format(new BigDecimal(amountMap.get("amtGst"))));
			invoicesubtable.addCell(format.format(new BigDecimal(amountMap.get("amtGst"))));
		}

		invoicesubtable.setHorizontalAlignment(Element.ALIGN_LEFT);
		document.add(invoicesubtable);
		// Here logic is for 2nd Amount charges in words logic
		amountParagraph = new Paragraph();
		amountParagraph = new Paragraph("Amount Chargeable (in words) :", subCatFont);
		amountParagraph.add(Chunk.NEWLINE);
		amountParagraph.add(new Chunk("INR " + NumberToWord(amountMap.get("amtGst").split("\\.")[0]) + " Rupees and "
				+ NumberToWord(amountMap.get("amtGst").split("\\.")[1]) + " Paise Only", catFont));
		document.add(amountParagraph);
		document.add(Chunk.NEWLINE);

		// Here need too write the logic for Company Bank details
		companyBankDetailsParagraph = new Paragraph();
		companyBankDetailsParagraph.add(new Chunk("\r\n" + "Company's PAN : ", subCatFont));
		companyBankDetailsParagraph.add(new Chunk(cp.getPanCard() + "\r\n", catFont));
		companyBankDetailsParagraph.add(Chunk.NEWLINE);
		companyBankDetailsParagraph.setAlignment(Element.ALIGN_RIGHT);
		PdfPTable databottomTable = new PdfPTable(2);
		float[] comapnybankWidths = new float[] { 485f, 515f };
		databottomTable.setWidths(comapnybankWidths);
		databottomTable.getDefaultCell().setBorder(0);
		databottomTable.setWidthPercentage(100f);
		databottomTable.addCell(companyBankDetailsParagraph);
		companyBankDetailsParagraph = new Paragraph();
		companyBankDetailsParagraph = new Paragraph("Company's Bank Details ");
		companyBankDetailsParagraph.add(new Chunk("\r\n" + "Bank Name : ", subCatFont));
		companyBankDetailsParagraph.add(new Chunk(cp.getBankName() + "\r\n", catFont));
		companyBankDetailsParagraph.add(new Chunk("A/c No. : ", subCatFont));
		companyBankDetailsParagraph.add(new Chunk(cp.getAccountNo() + "\r\n", catFont));
		companyBankDetailsParagraph.add(new Chunk("Branch & IFS Code : ", subCatFont));
		companyBankDetailsParagraph.add(new Chunk(cp.getBranchName() + " & " + cp.getIfscCode() + "\r\n", catFont));
		companyBankDetailsParagraph.add(new Chunk(ls));
		companyBankDetailsParagraph.add(Chunk.NEWLINE);
		companyBankDetailsParagraph.setAlignment(Element.ALIGN_RIGHT);

		databottomTable.addCell(companyBankDetailsParagraph);
		document.add(databottomTable);

	}

	private void addInvoiceContentSub(Document document, Map<String, String> dataProviderMap, CompanyProfile cp,
			User user, Map<String, String> amountMap)
			throws DocumentException, MalformedURLException, IOException, FileNotFoundException {
		Format format = com.ibm.icu.text.NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
		Paragraph amountParagraph = null;
		Paragraph custDetailsParagraph = null;
		// Here invoice table related logic
		float[] ivcolumnWidths = new float[] { 30f, 500f, 125f, 125f, 45f, 175f };
		PdfPTable invoiceTable = new PdfPTable(6);
		invoiceTable.setWidths(ivcolumnWidths);
		invoiceTable.setWidthPercentage(100f);
		invoiceTable.addCell("S.no");
		invoiceTable.addCell("Particulars");
		invoiceTable.addCell("HSN/SAC Number");
		invoiceTable.addCell("Rate");
		invoiceTable.addCell("Per");
		invoiceTable.addCell("Amount");
		if (user.getState().equalsIgnoreCase(cp.getState())) {
			invoiceTable.addCell("1\r\n" + "2\r\n" + "3\r\n" + "4\r\n");
		} else {
			invoiceTable.addCell("1\r\n" + "2\r\n" + "3\r\n\r\n");
		}
		custDetailsParagraph = new Paragraph("Pay10 PG Charges (Exempt)\r\n", catFont);
		custDetailsParagraph.add(new Chunk("Pay10 PG Charges (Taxable)\r\n", catFont));
		if (user.getState().equalsIgnoreCase(cp.getState())) {
			custDetailsParagraph.add(new Chunk("Output CGST \r\n", catFont));
			custDetailsParagraph.add(new Chunk("Output SGST \r\n", catFont));
		} else {
			custDetailsParagraph.add(new Chunk("Output IGST \r\n\r\n\r\n", catFont));
		}

		custDetailsParagraph.setAlignment(Element.ALIGN_CENTER);
		invoiceTable.addCell(custDetailsParagraph);

		if (user.getState().equalsIgnoreCase(cp.getState())) {
			invoiceTable.addCell(cp.getHsnSacCode() +"\r\n" + cp.getHsnSacCode() + "\r\n" + "\r\n" + "");
			invoiceTable
					.addCell("\r\n" + "\r\n" + amountMap.get("sgstpercent") + "\r\n" + amountMap.get("cgstpercent"));

			invoiceTable.addCell("\r\n" + "\r\n" + "%\r\n" + "%");
			invoiceTable.addCell(format.format(new BigDecimal(amountMap.get("amtPgTaxExempt"))) + "\r\n"
					+ format.format(new BigDecimal(amountMap.get("amtPgTaxable"))) + "\r\n"
					+ format.format(new BigDecimal(amountMap.get("amtSGst"))) + "\r\n"
					+ format.format(new BigDecimal(amountMap.get("amtCGst"))) + "\r\n");
		} else {
			invoiceTable.addCell(cp.getHsnSacCode() +"\r\n" + cp.getHsnSacCode() + "\r\n" + "\r\n" + "");
			invoiceTable.addCell("\r\n" + "\r\n" + amountMap.get("gstpercent"));

			invoiceTable.addCell("\r\n" + "\r\n" + "%");
			invoiceTable.addCell(format.format(new BigDecimal(amountMap.get("amtPgTaxExempt"))) + "\r\n"
					+ format.format(new BigDecimal(amountMap.get("amtPgTaxable"))) + "\r\n"
					+ format.format(new BigDecimal(amountMap.get("amtGst"))) + "\r\n\r\n");
		}

		invoiceTable.addCell("");
		invoiceTable.addCell("Total");
		invoiceTable.addCell("");
		invoiceTable.addCell("");
		invoiceTable.addCell("");
		invoiceTable.addCell(format.format(new BigDecimal(amountMap.get("totalamt"))));

		invoiceTable.setHorizontalAlignment(Element.ALIGN_LEFT);
		document.add(invoiceTable);

		// Here logic is for Amount charges in words logic
		amountParagraph = new Paragraph();
		amountParagraph = new Paragraph("Amount Chargeable (in words) :", subCatFont);
		amountParagraph.add(Chunk.NEWLINE);

		amountParagraph.add(new Chunk("INR " + NumberToWord(amountMap.get("totalamt").split("\\.")[0]) + " Rupees and "
				+ NumberToWord(amountMap.get("totalamt").split("\\.")[1]) + " Paise Only", catFont));
		document.add(amountParagraph);
		document.add(Chunk.NEWLINE);

	}

	public String NumberToWord(String number) {

		if (number.equalsIgnoreCase("0") || number.equalsIgnoreCase("00")) {
			return "Zero";
		}
		String twodigitword = "";
		String word = "";
		String[] HTLC = { "", "Hundred", "Thousand", "Lakh", "Crore","","Thousand","" }; // H-hundread , T-Thousand, ..
		int split[] = { 0, 2, 3, 5, 7, 9, 10, 11, 12};
		String[] temp = new String[split.length];
		boolean addzero = true;
		int len1 = number.length();
		if (len1 > split[split.length - 1]) {
			logger.error("Error. Maximum Allowed digits " + split[split.length - 1]);
		}
		for (int l = 1; l < split.length; l++)
			if (number.length() == split[l])
				addzero = false;
		if (addzero == true)
			number = "0" + number;
		int len = number.length();
		int j = 0;
		// spliting & putting numbers in temp array.
		while (split[j] < len) {
			int beg = len - split[j + 1];
			int end = beg + split[j + 1] - split[j];
			temp[j] = number.substring(beg, end);
			j = j + 1;
		}

		for (int k = 0; k < j; k++) {
			twodigitword = ConvertOnesTwos(temp[k]);
			if (k >= 1) {
				if (twodigitword.trim().length() != 0)
					word = twodigitword + " " + HTLC[k] + " " + word;
			} else
				word = twodigitword;
		}
		return (word);
	}

	public String ConvertOnesTwos(String t) {
		final String[] ones = { "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten",
				"Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen" };
		final String[] tens = { "", "Ten", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty",
				"Ninety" };

		String word = "";
		int num = Integer.parseInt(t);
		if (num % 10 == 0)
			word = tens[num / 10] + " " + word;
		else if (num < 20)
			word = ones[num] + " " + word;
		else {
			word = tens[(num - (num % 10)) / 10] + word;
			word = word + " " + ones[num % 10];
		}
		return word;
	}

}
