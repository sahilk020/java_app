package com.crmws.dto;

public class QRCode2FADTO {
	 private boolean success;
	    private String message;
	    private String base64Image;
	    
	    
		public QRCode2FADTO(boolean success, String message, String base64Image) {
			super();
			this.success = success;
			this.message = message;
			this.base64Image = base64Image;
		}
		public boolean isSuccess() {
			return success;
		}
		public void setSuccess(boolean success) {
			this.success = success;
		}
		public String getMessage() {
			return message;
		}
		public void setMessage(String message) {
			this.message = message;
		}
		public String getBase64Image() {
			return base64Image;
		}
		public void setBase64Image(String base64Image) {
			this.base64Image = base64Image;
		}
	    
	    

}
