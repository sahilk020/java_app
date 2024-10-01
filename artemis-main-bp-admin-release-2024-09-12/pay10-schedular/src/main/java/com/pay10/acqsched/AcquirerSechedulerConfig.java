package com.pay10.acqsched;

public class AcquirerSechedulerConfig {
    private String acquirer;
    private String startTime;
    private String maxTime;
    private String retry;
    private String createdBy;
    private String updatedBy;

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getInstrument() {
        return instrument;
    }

    public void setInstrument(String instrument) {
        this.instrument = instrument;
    }

    private String instrument;

    public String getAcquirer() {
        return acquirer;
    }

    public void setAcquirer(String acquirer) {
        this.acquirer = acquirer;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(String maxTime) {
        this.maxTime = maxTime;
    }

    public String getRetry() {
        return retry;
    }

    public void setRetry(String retry) {
        this.retry = retry;
    }

    public String getInstuments() {
        return instuments;
    }

    public void setInstuments(String instuments) {
        this.instuments = instuments;
    }

    private String instuments;

    @Override
    public String toString() {
        return "AcquirerWiseScheduler{" +
                "acquirer='" + acquirer + '\'' +
                ", startTime='" + startTime + '\'' +
                ", maxTime='" + maxTime + '\'' +
                ", retry='" + retry + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", updatedBy='" + updatedBy + '\'' +
                ", instrument='" + instrument + '\'' +
                ", instuments='" + instuments + '\'' +
                '}';
    }
}
