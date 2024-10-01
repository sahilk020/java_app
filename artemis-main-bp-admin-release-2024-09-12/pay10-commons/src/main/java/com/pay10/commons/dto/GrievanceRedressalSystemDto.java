package com.pay10.commons.dto;
import org.springframework.web.multipart.MultipartFile;

public class GrievanceRedressalSystemDto {
	private String grievanceRedressalSystemDescription;
	private String grievanceRedressalSystemTittle;
	private String grievanceRedressalSystemPgrefNumber;
	private String grievanceRedressalSystemId;
	private String payId;
	private String userEmailId;
	private String customerName;
	private String customerPhone;
	private String file;
	private String filename;
	
	
	

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	
	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public String getPayId() {
		return payId;
	}
	public void setPayId(String payId) {
		this.payId = payId;
	}
	public String getCustomerPhone() {
		return customerPhone;
	}
	public void setCustomerPhone(String customerPhone) {
		this.customerPhone = customerPhone;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getGrievanceRedressalSystemDescription() {
		return grievanceRedressalSystemDescription;
	}
	public void setGrievanceRedressalSystemDescription(String grievanceRedressalSystemDescription) {
		this.grievanceRedressalSystemDescription = grievanceRedressalSystemDescription;
	}
	public String getGrievanceRedressalSystemTittle() {
		return grievanceRedressalSystemTittle;
	}
	public void setGrievanceRedressalSystemTittle(String grievanceRedressalSystemTittle) {
		this.grievanceRedressalSystemTittle = grievanceRedressalSystemTittle;
	}
	public String getGrievanceRedressalSystemPgrefNumber() {
		return grievanceRedressalSystemPgrefNumber;
	}
	public void setGrievanceRedressalSystemPgrefNumber(String grievanceRedressalSystemPgrefNumber) {
		this.grievanceRedressalSystemPgrefNumber = grievanceRedressalSystemPgrefNumber;
	}
	public String getGrievanceRedressalSystemId() {
		return grievanceRedressalSystemId;
	}
	public void setGrievanceRedressalSystemId(String grievanceRedressalSystemId) {
		this.grievanceRedressalSystemId = grievanceRedressalSystemId;
	}
	public String getUserEmailId() {
		return userEmailId;
	}
	public void setUserEmailId(String userEmailId) {
		this.userEmailId = userEmailId;
	}
	
}
