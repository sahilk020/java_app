package com.pay10.commons.entity;


import org.hibernate.annotations.GeneratorType;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;
import java.util.Date;

@Entity
@Proxy(lazy = false)
@Table(name = "reset_merchant_key")
public class ResetMerchantKey {
    @Id
    @Column(unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private String payId;
    @Column
    private String encryptionKey;
    @Column
    private Date startDate;
    @Column
    private Date endDate;
    @Column
    private String salt;
    @Column
    private String keySalt;
    @Column
    private String status;
    @Column
    private Date createdOn;
    @Column
    private String createdBy;
    @Column
    private Date updatedOn;
    @Column
    private String updatedBy;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPayId() {
        return payId;
    }

    public void setPayId(String payId) {
        this.payId = payId;
    }

    public String getEncryptionKey() {
        return encryptionKey;
    }

    public void setEncryptionKey(String encryptionKey) {
        this.encryptionKey = encryptionKey;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getKeySalt() {
        return keySalt;
    }

    public void setKeySalt(String keySalt) {
        this.keySalt = keySalt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(Date updatedOn) {
        this.updatedOn = updatedOn;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public String toString() {
        return "ResetMerchantKey{" +
                "id=" + id +
                ", payId='" + payId + '\'' +
                 ", encryptionKey='" + encryptionKey + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", salt='" + salt + '\'' +
                ", keySalt='" + keySalt + '\'' +
                ", status='" + status + '\'' +
                ", createdOn=" + createdOn +
                ", createdBy='" + createdBy + '\'' +
                ", updatedOn=" + updatedOn +
                ", updatedBy='" + updatedBy + '\'' +
                '}';
    }
}
