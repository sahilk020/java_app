package com.pay10.htpay;

import org.springframework.stereotype.Service;

@Service("HtpayTransaction")
public class Transaction {
	
	
		private String pforderno;
		private String mhtorderno;
		private String currency;
		private String paidamount;
		private String accno;
		private String note;
		private String paytype;
		private String status;	
		private String random;
		private String sign;
		public String getPforderno() {
			return pforderno;
		}
		public void setPforderno(String pforderno) {
			this.pforderno = pforderno;
		}
		public String getMhtorderno() {
			return mhtorderno;
		}
		public void setMhtorderno(String mhtorderno) {
			this.mhtorderno = mhtorderno;
		}
		public String getCurrency() {
			return currency;
		}
		public void setCurrency(String currency) {
			this.currency = currency;
		}
		public String getPaidamount() {
			return paidamount;
		}
		public void setPaidamount(String paidamount) {
			this.paidamount = paidamount;
		}
		public String getAccno() {
			return accno;
		}
		public void setAccno(String accno) {
			this.accno = accno;
		}
		public String getNote() {
			return note;
		}
		public void setNote(String note) {
			this.note = note;
		}
		public String getPaytype() {
			return paytype;
		}
		public void setPaytype(String paytype) {
			this.paytype = paytype;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getRandom() {
			return random;
		}
		public void setRandom(String random) {
			this.random = random;
		}
		public String getSign() {
			return sign;
		}
		public void setSign(String sign) {
			this.sign = sign;
		}


}
