package com.pay10.commons.dao;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;
import java.util.Date;


@Entity
@Proxy(lazy= false)
//@Table(indexes = { @Index(name = "IDX_MYIDX1", columnList = "emailId,payId") })
@Cache(usage= CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class SuccessRateThresholdSetting {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int Id;

    @Column(nullable=false)
    private String acquirerName;
    @Column(nullable=false)
    private String paymentType;
    @Column(nullable = false)
    private String mopType;
    @Column(nullable = false)
    private int successRate;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String updatedBy;
    @Column(nullable = false)
    private Date updateDate;

    @Column(nullable = false)
    private String payId;

    @Column(nullable = false)
    private String merchant;


    private String createdBy;
    private Date createdDate;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getAcquirerName() {
        return acquirerName;
    }

    public void setAcquirerName(String acquirerName) {
        this.acquirerName = acquirerName;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getMopType() {
        return mopType;
    }

    public void setMopType(String mopType) {
        this.mopType = mopType;
    }

    public int getSuccessRate() {
        return successRate;
    }

    public void setSuccessRate(int successRate) {
        this.successRate = successRate;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getMerchant() { return merchant; }

    public void setMerchant(String merchant) { this.merchant = merchant; }

    public String getPayId() {
        return payId;
    }

    public void setPayId(String payId) {
        this.payId = payId;
    }
}
