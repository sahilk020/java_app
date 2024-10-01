package com.pay10.errormapping;

public class ErrorMappingDTO {

   private String acquirer;
   private  String acqStatusCode;
   private String statusMsg;
   private  String pgCode;
   private String pgMsg;

    public String getPgStatus() {
        return pgStatus;
    }

    public void setPgStatus(String pgStatus) {
        this.pgStatus = pgStatus;
    }

    private String pgStatus;

    public String getAcquirer() {
        return acquirer;
    }

    public void setAcquirer(String acquirer) {
        this.acquirer = acquirer;
    }

    public String getAcqStatusCode() {
        return acqStatusCode;
    }

    public void setAcqStatusCode(String acqStatusCode) {
        this.acqStatusCode = acqStatusCode;
    }

    public String getStatusMsg() {
        return statusMsg;
    }

    public void setStatusMsg(String statusMsg) {
        this.statusMsg = statusMsg;
    }

    public String getPgCode() {
        return pgCode;
    }

    public void setPgCode(String pgCode) {
        this.pgCode = pgCode;
    }

    public String getPgMsg() {
        return pgMsg;
    }

    public void setPgMsg(String pgMsg) {
        this.pgMsg = pgMsg;
    }

    @Override
    public String toString() {
        return "ErrorMappingDTO{" +
                "acquirer='" + acquirer + '\'' +
                ", acqStatusCode='" + acqStatusCode + '\'' +
                ", statusMsg='" + statusMsg + '\'' +
                ", pgCode='" + pgCode + '\'' +
                ", pgMsg='" + pgMsg + '\'' +
                ", pgStatus='" + pgStatus + '\'' +
                '}';
    }
}
