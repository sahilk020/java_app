package com.pay10.commons.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "user_commission_mst")
public class UserCommision {
	
	
		@Id
		@GeneratedValue(strategy = GenerationType.AUTO)
		private long id;
		
	
		private String smaPayId;
		private String maPayId;
		private String agentPayId;
		private String merchantPayId;
		private String mop;
		private String transactiontype;
		private boolean commissiontype;
		private String sma_commission_percent;
		private String sma_commission_amount;
		private String ma_commission_percent;
		private String ma_commission_amount;
		private String agent_commission_percent;
		private String agent_commission_amount;
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
		public String getSmaPayId() {
			return smaPayId;
		}
		public void setSmaPayId(String smaPayId) {
			this.smaPayId = smaPayId;
		}
		public String getMaPayId() {
			return maPayId;
		}
		public void setMaPayId(String maPayId) {
			this.maPayId = maPayId;
		}
		public String getAgentPayId() {
			return agentPayId;
		}
		public void setAgentPayId(String agentPayId) {
			this.agentPayId = agentPayId;
		}
		public String getMerchantPayId() {
			return merchantPayId;
		}
		public void setMerchantPayId(String merchantPayId) {
			this.merchantPayId = merchantPayId;
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
		public boolean isCommissiontype() {
			return commissiontype;
		}
		public void setCommissiontype(boolean commissiontype) {
			this.commissiontype = commissiontype;
		}
		public String getSma_commission_percent() {
			return sma_commission_percent;
		}
		public void setSma_commission_percent(String sma_commission_percent) {
			this.sma_commission_percent = sma_commission_percent;
		}
		public String getSma_commission_amount() {
			return sma_commission_amount;
		}
		public void setSma_commission_amount(String sma_commission_amount) {
			this.sma_commission_amount = sma_commission_amount;
		}
		public String getMa_commission_percent() {
			return ma_commission_percent;
		}
		public void setMa_commission_percent(String ma_commission_percent) {
			this.ma_commission_percent = ma_commission_percent;
		}
		public String getMa_commission_amount() {
			return ma_commission_amount;
		}
		public void setMa_commission_amount(String ma_commission_amount) {
			this.ma_commission_amount = ma_commission_amount;
		}
		public String getAgent_commission_percent() {
			return agent_commission_percent;
		}
		public void setAgent_commission_percent(String agent_commission_percent) {
			this.agent_commission_percent = agent_commission_percent;
		}
		public String getAgent_commission_amount() {
			return agent_commission_amount;
		}
		public void setAgent_commission_amount(String agent_commission_amount) {
			this.agent_commission_amount = agent_commission_amount;
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
		
		
		
}
