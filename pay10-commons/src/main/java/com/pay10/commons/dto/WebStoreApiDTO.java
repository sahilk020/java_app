package com.pay10.commons.dto;

import java.io.File;

import org.springframework.web.multipart.MultipartFile;

public class WebStoreApiDTO {
	
	private String pay_id;
	private String salt;
	private String merchant_hosted_key;
	private String merchant_name;
	
	private String name;
	private String email;
	private String password;
	private String password_confirmation;
	private String uuid;
	private String description;
	private String discounted_price;
	private String image;
	
	private String price;
	private String id;
	private String user_id;
	private boolean product_status;
	
    
	public boolean isProduct_status() {
		return product_status;
	}
	public void setProduct_status(boolean product_status) {
		this.product_status = product_status;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	

	public String getDiscounted_price() {
		return discounted_price;
	}
	public void setDiscounted_price(String discounted_price) {
		this.discounted_price = discounted_price;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getPay_id() {
		return pay_id;
	}
	public void setPay_id(String pay_id) {
		this.pay_id = pay_id;
	}
	public String getSalt() {
		return salt;
	}
	public void setSalt(String salt) {
		this.salt = salt;
	}
	public String getMerchant_hosted_key() {
		return merchant_hosted_key;
	}
	public void setMerchant_hosted_key(String merchant_hosted_key) {
		this.merchant_hosted_key = merchant_hosted_key;
	}
	public String getMerchant_name() {
		return merchant_name;
	}
	public void setMerchant_name(String merchant_name) {
		this.merchant_name = merchant_name;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getPassword_confirmation() {
		return password_confirmation;
	}
	public void setPassword_confirmation(String password_confirmation) {
		this.password_confirmation = password_confirmation;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	@Override
	public String toString() {
		return "WebStoreApiDTO [pay_id=" + pay_id + ", salt=" + salt + ", merchant_hosted_key=" + merchant_hosted_key
				+ ", merchant_name=" + merchant_name + ", name=" + name + ", email=" + email + ", password=" + password
				+ ", password_confirmation=" + password_confirmation + ", uuid=" + uuid + ", description=" + description
				+ ", discounted_price=" + discounted_price + ", image=" + image + ", price=" + price + ", id=" + id
				+ ", user_id=" + user_id + "]";
	}

}
