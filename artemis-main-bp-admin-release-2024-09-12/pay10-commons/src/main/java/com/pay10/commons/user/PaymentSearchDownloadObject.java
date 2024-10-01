package com.pay10.commons.user;

public class PaymentSearchDownloadObject {

    private String transactionId;
    private String pgRefNum;
    private String transactionRegion;
    private String merchants;
    private String postSettledFlag;
    private String txnType;
    private String acquirerType;
    private String paymentMethods;
    private String status;
    private String dateFrom;
    private String amount;
    private String orderId;
    private String totalAmount;
    private String srNo;
    private String refundOrderId;
    private String tdrSurcharge;
    private String totalGst;
    private String igst;
    private String cgst;
    private String sgst;
    private String netAmount;
    private String settledDateTime;
    private String captureDateTime;
    private String rrn;
    private String arn;
    private String acqId;
    private String mopType;
    /*added by vijaylakshmi*/
    private String ipAddress;
    private String custMobileNo;
    private String custEmail;
    //added by deep
    private String udf4;
    private String udf5;
    private String udf6;
    private String utrNo;
    
    public String getUtrNo() {
		return utrNo;
	}

	public void setUtrNo(String utrNo) {
		this.utrNo = utrNo;
	}

	public String getUdf4() {
		return udf4;
	}

	public void setUdf4(String udf4) {
		this.udf4 = udf4;
	}

	public String getUdf5() {
		return udf5;
	}

	public void setUdf5(String udf5) {
		this.udf5 = udf5;
	}

	public String getUdf6() {
		return udf6;
	}

	public void setUdf6(String udf6) {
		this.udf6 = udf6;
	}

	public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
   public String getCustMobileNo() {
        return custMobileNo;
    }

    public void setCustMobileNo(String custMobileNo) {
        this.custMobileNo = custMobileNo;
    }

    public String getCustEmail() {
        return custEmail;
    }

    public void setCustEmail(String custEmail) {
        this.custEmail = custEmail;
    }



    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getPgRefNum() {
        return pgRefNum;
    }

    public void setPgRefNum(String pgRefNum) {
        this.pgRefNum = pgRefNum;
    }

    public String getTransactionRegion() {
        return transactionRegion;
    }

    public void setTransactionRegion(String transactionRegion) {
        this.transactionRegion = transactionRegion;
    }

    public String getMerchants() {
        return merchants;
    }

    public void setMerchants(String merchants) {
        this.merchants = merchants;
    }

    public String getPostSettledFlag() {
        return postSettledFlag;
    }

    public void setPostSettledFlag(String postSettledFlag) {
        this.postSettledFlag = postSettledFlag;
    }

    public String getTxnType() {
        return txnType;
    }

    public void setTxnType(String txnType) {
        this.txnType = txnType;
    }

    public String getAcquirerType() {
        return acquirerType;
    }

    public void setAcquirerType(String acquirerType) {
        this.acquirerType = acquirerType;
    }

    public String getPaymentMethods() {
        return paymentMethods;
    }

    public void setPaymentMethods(String paymentMethods) {
        this.paymentMethods = paymentMethods;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(String dateFrom) {
        this.dateFrom = dateFrom;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getSrNo() {
        return srNo;
    }

    public void setSrNo(String srNo) {
        this.srNo = srNo;
    }

    public String getRefundOrderId() {
        return refundOrderId;
    }

    public void setRefundOrderId(String refundOrderId) {
        this.refundOrderId = refundOrderId;
    }

    public String getTdrSurcharge() {
        return tdrSurcharge;
    }

    public void setTdrSurcharge(String tdrSurcharge) {
        this.tdrSurcharge = tdrSurcharge;
    }

    public String getTotalGst() {
        return totalGst;
    }

    public void setTotalGst(String totalGst) {
        this.totalGst = totalGst;
    }

    public String getIgst() {
        return igst;
    }

    public void setIgst(String igst) {
        this.igst = igst;
    }

    public String getCgst() {
        return cgst;
    }

    public void setCgst(String cgst) {
        this.cgst = cgst;
    }

    public String getSgst() {
        return sgst;
    }

    public void setSgst(String sgst) {
        this.sgst = sgst;
    }

    public String getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(String netAmount) {
        this.netAmount = netAmount;
    }

    public String getSettledDateTime() {
        return settledDateTime;
    }

    public void setSettledDateTime(String settledDateTime) {
        this.settledDateTime = settledDateTime;
    }

    public String getCaptureDateTime() {
        return captureDateTime;
    }

    public void setCaptureDateTime(String captureDateTime) {
        this.captureDateTime = captureDateTime;
    }

    public String getRrn() {
        return rrn;
    }

    public void setRrn(String rrn) {
        this.rrn = rrn;
    }

    public String getArn() {
        return arn;
    }

    public void setArn(String arn) {
        this.arn = arn;
    }

    public String getAcqId() {
        return acqId;
    }

    public void setAcqId(String acqId) {
        this.acqId = acqId;
    }

    public String getMopType() {
        return mopType;
    }

    public void setMopType(String mopType) {
        this.mopType = mopType;
    }

    public Object[] myCsvMethodDownloadPaymentsReportByView() {
        Object[] objectArray = new Object[13];


        objectArray[0] = srNo;
        objectArray[1] = transactionId;
        objectArray[2] = pgRefNum;
        objectArray[3] = merchants;
        objectArray[4] = dateFrom;
        objectArray[5] = orderId;
        objectArray[6] = paymentMethods;
        objectArray[7] = txnType;
        objectArray[8] = status;
        objectArray[9] = transactionRegion;
        objectArray[10] = amount;
        objectArray[11] = totalAmount;
        objectArray[12] = postSettledFlag;
        return objectArray;
    }

    public Object[] myCsvMethodDownloadPaymentsforSaleCaptured() {
        Object[] objectArray = new Object[23];


        objectArray[0] = srNo;
        objectArray[1] = transactionId;
        objectArray[2] = pgRefNum;
        objectArray[3] = merchants;
        objectArray[4] = dateFrom;
        objectArray[5] = orderId;
        objectArray[6] = paymentMethods;
        objectArray[7] = txnType;
        objectArray[8] = status;
        objectArray[9] = transactionRegion;
        objectArray[10] = amount;
        objectArray[11] = totalAmount;
        objectArray[12] = postSettledFlag;
        objectArray[13] = rrn;
        objectArray[14] = arn;
        objectArray[15] = acqId;
        objectArray[16] = mopType;
        /*added by vijaylakshmi*/
        objectArray[17] = custEmail;
        objectArray[18] = custMobileNo;
        objectArray[19] = ipAddress;
        //added by deep
        objectArray[20] = udf4;
        objectArray[21] = udf5;
        objectArray[22] = udf6;
        return objectArray;
    }

    public Object[] myCsvMethodDownloadPaymentsforRefundCaptured() {
        Object[] objectArray = new Object[24];


        objectArray[0] = srNo;
        objectArray[1] = transactionId;
        objectArray[2] = pgRefNum;
        objectArray[3] = merchants;
        objectArray[4] = dateFrom;
        objectArray[5] = orderId;
        objectArray[6] = refundOrderId;
        objectArray[7] = paymentMethods;
        objectArray[8] = txnType;
        objectArray[9] = status;
        objectArray[10] = transactionRegion;
        objectArray[11] = amount;
        objectArray[12] = totalAmount;
        objectArray[13] = postSettledFlag;
        objectArray[14] = rrn;
        objectArray[15] = arn;
        objectArray[16] = acqId;
        objectArray[17] = mopType;
        /*added by vijaylakshmi*/
        objectArray[18] = custEmail;
        objectArray[19] = custMobileNo;
        objectArray[20] = ipAddress;
        objectArray[21] = udf4;
        objectArray[22] = udf5;
        objectArray[23] = udf6;
        return objectArray;
    }

    public Object[] myCsvMethodDownloadPaymentsforSettled() {
        Object[] objectArray = new Object[31];

        objectArray[0] = srNo;
        objectArray[1] = transactionId;
        objectArray[2] = pgRefNum;
        objectArray[3] = merchants;
        objectArray[4] = captureDateTime;
        objectArray[5] = dateFrom;
        objectArray[6] = orderId;
        objectArray[7] = paymentMethods;
        objectArray[8] = txnType;
        objectArray[9] = status;
        objectArray[10] = transactionRegion;
        objectArray[11] = amount;
        objectArray[12] = totalAmount;
        objectArray[13] = tdrSurcharge;
        objectArray[14] = igst;
        objectArray[15] = cgst;
        objectArray[16] = sgst;
        objectArray[17] = totalGst;
        objectArray[18] = netAmount;
        objectArray[19] = postSettledFlag;
        objectArray[20] = rrn;
        objectArray[21] = arn;
        objectArray[22] = acqId;
        objectArray[23] = mopType;
        /*added by vijaylakshmi*/
        objectArray[24] = custEmail;
        objectArray[25] = custMobileNo;
        objectArray[26] = ipAddress;
        objectArray[27] = udf4;
        objectArray[28] = udf5;
        objectArray[29] = udf6;
        objectArray[30] = utrNo;
        return objectArray;
    }

    public Object[] myCsvMethodDownloadPaymentsforAdminfiellds() {
        Object[] objectArray = new Object[1];
        objectArray[0] = acquirerType;
        return objectArray;
    }


}
