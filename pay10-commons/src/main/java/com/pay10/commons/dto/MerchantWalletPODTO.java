package com.pay10.commons.dto;

public class MerchantWalletPODTO {

	private String payId;
	private String credit;
	private String debit;
	private String currency;
	private String finalBalance;

	private String totalBalance;
	private String remark;
	private String narration;

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getNarration() {
		return narration;
	}

	public void setNarration(String narration) {
		this.narration = narration;
	}

	public String getPayId() {
		return payId;
	}

	public void setPayId(String payId) {
		this.payId = payId;
	}

	public String getCredit() {
		return credit;
	}

	public void setCredit(String credit) {
		this.credit = credit;
	}

	public String getDebit() {
		return debit;
	}

	public void setDebit(String debit) {
		this.debit = debit;
	}

	public String getFinalBalance() {
		return finalBalance;
	}

	public void setFinalBalance(String finalBalance) {
		this.finalBalance = finalBalance;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getTotalBalance() {
		return totalBalance;
	}

	public void setTotalBalance(String totalBalance) {
		this.totalBalance = totalBalance;
	}

	@Override
	public String toString() {
		return "MerchantWalletPODTO [payId=" + payId + ", credit=" + credit + ", debit=" + debit + ", finalBalance="
				+ finalBalance + ", remark=" + remark + ", narration=" + narration + "]";
	}

}
