package com.pay10.cashfree;

public class CashfreeOrderRespDTO {

	private float cf_order_id;
	private String created_at;
	Customer_details Customer_detailsObject;
	private String entity;
	private float order_amount;
	private String order_currency;
	private String order_expiry_time;
	private String order_id;
	Order_meta Order_metaObject;
	private String order_note = null;
	private String order_status;
	private String order_token;
	private String payment_link;
	Payments PaymentsObject;
	Refunds RefundsObject;
	Settlements SettlementsObject;

	// Getter Methods

	public float getCf_order_id() {
		return cf_order_id;
	}

	public String getCreated_at() {
		return created_at;
	}

	public Customer_details getCustomer_details() {
		return Customer_detailsObject;
	}

	public String getEntity() {
		return entity;
	}

	public float getOrder_amount() {
		return order_amount;
	}

	public String getOrder_currency() {
		return order_currency;
	}

	public String getOrder_expiry_time() {
		return order_expiry_time;
	}

	public String getOrder_id() {
		return order_id;
	}

	public Order_meta getOrder_meta() {
		return Order_metaObject;
	}

	public String getOrder_note() {
		return order_note;
	}

	public String getOrder_status() {
		return order_status;
	}

	public String getOrder_token() {
		return order_token;
	}

	public String getPayment_link() {
		return payment_link;
	}

	public Payments getPayments() {
		return PaymentsObject;
	}

	public Refunds getRefunds() {
		return RefundsObject;
	}

	public Settlements getSettlements() {
		return SettlementsObject;
	}

	// Setter Methods

	public void setCf_order_id(float cf_order_id) {
		this.cf_order_id = cf_order_id;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

	public void setCustomer_details(Customer_details customer_detailsObject) {
		this.Customer_detailsObject = customer_detailsObject;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public void setOrder_amount(float order_amount) {
		this.order_amount = order_amount;
	}

	public void setOrder_currency(String order_currency) {
		this.order_currency = order_currency;
	}

	public void setOrder_expiry_time(String order_expiry_time) {
		this.order_expiry_time = order_expiry_time;
	}

	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}

	public void setOrder_meta(Order_meta order_metaObject) {
		this.Order_metaObject = order_metaObject;
	}

	public void setOrder_note(String order_note) {
		this.order_note = order_note;
	}

	public void setOrder_status(String order_status) {
		this.order_status = order_status;
	}

	public void setOrder_token(String order_token) {
		this.order_token = order_token;
	}

	public void setPayment_link(String payment_link) {
		this.payment_link = payment_link;
	}

	public void setPayments(Payments paymentsObject) {
		this.PaymentsObject = paymentsObject;
	}

	public void setRefunds(Refunds refundsObject) {
		this.RefundsObject = refundsObject;
	}

	public void setSettlements(Settlements settlementsObject) {
		this.SettlementsObject = settlementsObject;
	}

	public static class Settlements {
		private String url;

		// Getter Methods

		public String getUrl() {
			return url;
		}

		// Setter Methods

		public void setUrl(String url) {
			this.url = url;
		}

		@Override
		public String toString() {
			return "Settlements [url=" + url + "]";
		}

	}

	public static class Refunds {
		private String url;

		// Getter Methods

		public String getUrl() {
			return url;
		}

		// Setter Methods

		public void setUrl(String url) {
			this.url = url;
		}

		@Override
		public String toString() {
			return "Refunds [url=" + url + "]";
		}

	}

	public static class Payments {
		private String url;

		// Getter Methods

		public String getUrl() {
			return url;
		}

		// Setter Methods

		public void setUrl(String url) {
			this.url = url;
		}

		@Override
		public String toString() {
			return "Payments [url=" + url + "]";
		}

	}

	public static class Order_meta {
		private String return_url;
		private String notify_url;
		private String payment_methods = null;

		// Getter Methods

		public String getReturn_url() {
			return return_url;
		}

		public String getNotify_url() {
			return notify_url;
		}

		public String getPayment_methods() {
			return payment_methods;
		}

		// Setter Methods

		public void setReturn_url(String return_url) {
			this.return_url = return_url;
		}

		public void setNotify_url(String notify_url) {
			this.notify_url = notify_url;
		}

		public void setPayment_methods(String payment_methods) {
			this.payment_methods = payment_methods;
		}

		@Override
		public String toString() {
			return "Order_meta [return_url=" + return_url + ", notify_url=" + notify_url + ", payment_methods="
					+ payment_methods + "]";
		}

	}

	public static class Customer_details {
		private String customer_id;
		private String customer_name = null;
		private String customer_email;
		private String customer_phone;

		// Getter Methods

		public String getCustomer_id() {
			return customer_id;
		}

		public String getCustomer_name() {
			return customer_name;
		}

		public String getCustomer_email() {
			return customer_email;
		}

		public String getCustomer_phone() {
			return customer_phone;
		}

		// Setter Methods

		public void setCustomer_id(String customer_id) {
			this.customer_id = customer_id;
		}

		public void setCustomer_name(String customer_name) {
			this.customer_name = customer_name;
		}

		public void setCustomer_email(String customer_email) {
			this.customer_email = customer_email;
		}

		public void setCustomer_phone(String customer_phone) {
			this.customer_phone = customer_phone;
		}

		@Override
		public String toString() {
			return "Customer_details [customer_id=" + customer_id + ", customer_name=" + customer_name
					+ ", customer_email=" + customer_email + ", customer_phone=" + customer_phone + "]";
		}

	}

	@Override
	public String toString() {
		return "CashfreeOrderRespDTO [cf_order_id=" + cf_order_id + ", created_at=" + created_at
				+ ", Customer_detailsObject=" + Customer_detailsObject + ", entity=" + entity + ", order_amount="
				+ order_amount + ", order_currency=" + order_currency + ", order_expiry_time=" + order_expiry_time
				+ ", order_id=" + order_id + ", Order_metaObject=" + Order_metaObject + ", order_note=" + order_note
				+ ", order_status=" + order_status + ", order_token=" + order_token + ", payment_link=" + payment_link
				+ ", PaymentsObject=" + PaymentsObject + ", RefundsObject=" + RefundsObject + ", SettlementsObject="
				+ SettlementsObject + "]";
	}

}
