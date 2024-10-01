package com.pay10.commons.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.google.gson.GsonBuilder;
import org.hibernate.annotations.Proxy;

@Entity
@Proxy(lazy = false)
@Table(name = "chargebackReasons")
public class ChargebackReasonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(unique = true)
    private String cbReasonCode;
    private String cbReasonDescription;

    private boolean deleted;

    public String getCbReasonCode() {
        return cbReasonCode;
    }

    public void setCbReasonCode(String cbReasonCode) {
        this.cbReasonCode = cbReasonCode;
    }

    public String getCbReasonDescription() {
        return cbReasonDescription;
    }

    public void setCbReasonDescription(String cbReasonDescription) {
        this.cbReasonDescription = cbReasonDescription;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public String toString() {
        return new GsonBuilder().disableHtmlEscaping().create().toJson(this);
    }
}
