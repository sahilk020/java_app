package com.pay10.cashfree;

import java.util.HashMap;
import java.util.Map;

public class CashfreePayOrderRespDTO {

	private String payment_method;
	private String channel;
	private String action;
	Data data;
	private float cf_payment_id;

	private String message;
	private String code;
	private String type;

	// Getter Methods

	public String getPayment_method() {
		return payment_method;
	}

	public Data getData() {
		return data;
	}

	public void setData(Data data) {
		this.data = data;
	}

	public String getChannel() {
		return channel;
	}

	public String getAction() {
		return action;
	}

	public float getCf_payment_id() {
		return cf_payment_id;
	}

	// Setter Methods

	public void setPayment_method(String payment_method) {
		this.payment_method = payment_method;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public void setCf_payment_id(float cf_payment_id) {
		this.cf_payment_id = cf_payment_id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public static class Data {
		private String url;
		private Map<String,Object> payload = new HashMap<>();
		private String content_type = null;
		private String method = null;

		// Getter Methods

		public String getUrl() {
			return url;
		}

		public Map<String,Object> getPayload() {
			return payload;
		}

		public String getContent_type() {
			return content_type;
		}

		public String getMethod() {
			return method;
		}

		// Setter Methods

		public void setUrl(String url) {
			this.url = url;
		}

		public void setPayload(Map<String,Object> payload) {
			this.payload = payload;
		}

		public void setContent_type(String content_type) {
			this.content_type = content_type;
		}

		public void setMethod(String method) {
			this.method = method;
		}

		@Override
		public String toString() {
			return "Data [url=" + url + ", payload=" + payload + ", content_type=" + content_type + ", method=" + method
					+ "]";
		}

	}

	@Override
	public String toString() {
		return "CashfreePayOrderRespDTO [payment_method=" + payment_method + ", channel=" + channel + ", action="
				+ action + ", data=" + data + ", cf_payment_id=" + cf_payment_id + ", message=" + message + ", code="
				+ code + ", type=" + type + "]";
	}

}
