package com.pay10.commons.user;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Proxy;

/**
 * @ Rajendra
 */

@Entity
@Proxy(lazy= false)
@Table
public class InvoiceNumberCounter implements Serializable  {
	

		/**
	 * 
	 */
	private static final long serialVersionUID = -8110764076435614973L;
		// Invoice Number Counter
		@Id
		@Column(nullable=true,unique=true)
		private Long invoiceId;
		private String payId;
		private int invoiceNumber;
				
		public InvoiceNumberCounter(){
			
		}
		
		public Long getInvoiceId() {
			return invoiceId;
		}
		public void setInvoiceId(Long invoiceId) {
			this.invoiceId = invoiceId;
		}
		
		public String getPayId() {
			return payId;
		}
	
		public void setPayId(String payId) {
			this.payId = payId;
		}
	
		public int getInvoiceNumber() {
			return invoiceNumber;
		}
	
		public void setInvoiceNumber(int invoiceNumber) {
			this.invoiceNumber = invoiceNumber;
		}
}
