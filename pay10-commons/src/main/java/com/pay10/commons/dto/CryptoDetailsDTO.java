package com.pay10.commons.dto;

import javax.persistence.Column;
import java.util.Date;

public class CryptoDetailsDTO {

    private Long id;

    private String payId;

    private String address;

    private String currency;
     private String blockchain;
     private String createdBy;
     private String createdOn;
     private String updatedBy;
     private String updatedOn;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPayId() {
        return payId;
    }

    public void setPayId(String payId) {
        this.payId = payId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBlockchain() {
        return blockchain;
    }

    public void setBlockchain(String blockchain) {
        this.blockchain = blockchain;
    }

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

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(String updatedOn) {
        this.updatedOn = updatedOn;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }


    @Override
    public String toString() {
        return "CryptoDetailsDTO{" +
                "id=" + id +
                ", payId='" + payId + '\'' +
                ", address='" + address + '\'' +
                ", currency='" + currency + '\'' +
                ", blockchain='" + blockchain + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", createdOn='" + createdOn + '\'' +
                ", updatedBy='" + updatedBy + '\'' +
                ", updatedOn='" + updatedOn + '\'' +
                '}';
    }
}
