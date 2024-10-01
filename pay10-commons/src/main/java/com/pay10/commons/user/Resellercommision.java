package com.pay10.commons.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "reseller_commision_mst")
public class Resellercommision {
	
	
		@Id
		@GeneratedValue(strategy = GenerationType.AUTO)
		private long id;
		
		@Column
		private String resellerpayid;
		private String merchantpayid;
		private String mop;
		private String transactiontype;
		private String baserate;
		private String merchant_mdr;
		private boolean commissiontype;
		private String commission_percent;
		private String commission_amount;
		private String added_by;
		private String Added_on;
		private String status;
		private String currency;
		
		public long getId() {
			return id;
		}
		public void setId(long id) {
			this.id = id;
		}
		public String getResellerpayid() {
			return resellerpayid;
		}
		public void setResellerpayid(String resellerpayid) {
			this.resellerpayid = resellerpayid;
		}
		public String getMerchantpayid() {
			return merchantpayid;
		}
		public void setMerchantpayid(String merchantpayid) {
			this.merchantpayid = merchantpayid;
		}
		public String getMop() {
			return mop;
		}
		public void setMop(String mop) {
			this.mop = mop;
		}
		public String getTransactiontype() {
			return transactiontype;
		}
		public void setTransactiontype(String transactiontype) {
			this.transactiontype = transactiontype;
		}
		public String getBaserate() {
			return baserate;
		}
		public void setBaserate(String baserate) {
			this.baserate = baserate;
		}
		public String getMerchant_mdr() {
			return merchant_mdr;
		}
		public void setMerchant_mdr(String merchant_mdr) {
			this.merchant_mdr = merchant_mdr;
		}
	
		public boolean isCommissiontype() {
			return commissiontype;
		}
		public void setCommissiontype(boolean commissiontype) {
			this.commissiontype = commissiontype;
		}
		public String getCommission_percent() {
			return commission_percent;
		}
		public void setCommission_percent(String commission_percent) {
			this.commission_percent = commission_percent;
		}
		public String getCommission_amount() {
			return commission_amount;
		}
		public void setCommission_amount(String commission_amount) {
			this.commission_amount = commission_amount;
		}
		public String getAdded_by() {
			return added_by;
		}
		public void setAdded_by(String added_by) {
			this.added_by = added_by;
		}
		public String getAdded_on() {
			return Added_on;
		}
		public void setAdded_on(String added_on) {
			Added_on = added_on;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getCurrency() {
			return currency;
		}
		public void setCurrency(String currency) {
			this.currency = currency;
		}
		@Override
		public String toString() {
			return "Resellercommision [id=" + id + ", resellerpayid=" + resellerpayid + ", merchantpayid="
					+ merchantpayid + ", mop=" + mop + ", transactiontype=" + transactiontype + ", baserate=" + baserate
					+ ", merchant_mdr=" + merchant_mdr + ", commissiontype=" + commissiontype + ", commission_percent="
					+ commission_percent + ", commission_amount=" + commission_amount + ", added_by=" + added_by
					+ ", Added_on=" + Added_on + ", status=" + status + ", currency=" + currency + "]";
		}
		
}
