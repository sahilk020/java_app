package com.pay10.commons.user;

public class CashfreeRefundDTO {

	private Long id;
	private String payId;
	private String orderId;
	private String pgRefNo;
	private String refundOrderId;
	private String merchantId;
	private String secretKey;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPayId() {
		return payId;
	}

	public void setPayId(String payId) {
		this.payId = payId;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getPgRefNo() {
		return pgRefNo;
	}

	public void setPgRefNo(String pgRefNo) {
		this.pgRefNo = pgRefNo;
	}

	public String getRefundOrderId() {
		return refundOrderId;
	}

	public void setRefundOrderId(String refundOrderId) {
		this.refundOrderId = refundOrderId;
	}

	
	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	@Override
	public String toString() {
		return "CashfreeRefundDTO [id=" + id + ", payId=" + payId + ", orderId=" + orderId + ", pgRefNo=" + pgRefNo
				+ ", refundOrderId=" + refundOrderId + ", merchantId=" + merchantId + ", secretKey=" + secretKey + "]";
	}

	
}
