package com.crmws.dto;

public class GetPiChart {
    private Integer txnCount;
    private double totalAmount;
    private String mopType;

    public Integer getTxnCount() {
        return txnCount;
    }

    public void setTxnCount(Integer txnCount) {
        this.txnCount = txnCount;
    }


    public String getMopType() {
        return mopType;
    }

    public void setMopType(String mopType) {
        this.mopType = mopType;
    }


    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }



    @Override
    public String toString() {
        return "GetPiChart{" +
                "mopType='" + mopType + '\'' +
                ", txnNo='" + txnCount + '\'' +
                ", totalAmount='" + totalAmount + '\'' +
                '}';
    }
}
