package com.crmws.dto;

public class PiChartPopup {
    private String txnID;
    private String pgRefNo;
    private double amount;
    private String date;
    private String mopType;
    public String getMopType() {
        return mopType;
    }

    public void setMopType(String mopType) {
        this.mopType = mopType;
    }




    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getAmount() {
        return amount;
    }



    public String getTxnID() {
        return txnID;
    }

    public void setTxnID(String txnID) {
        this.txnID = txnID;
    }

    public String getPgRefNo() {
        return pgRefNo;
    }

    public void setPgRefNo(String pgRefNo) {
        this.pgRefNo = pgRefNo;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    @Override
    public String toString() {
        return "PiChartPopup{" +
                "txnID='" + txnID + '\'' +
                ", pgRefNo='" + pgRefNo + '\'' +
                ", amount=" + amount +
                ", date='" + date + '\'' +
                '}';
    }
}