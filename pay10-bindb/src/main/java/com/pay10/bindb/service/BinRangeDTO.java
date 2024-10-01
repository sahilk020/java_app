package com.pay10.bindb.service;

import org.codehaus.jackson.annotate.JsonProperty;
// Added by RR Date 26-Nov-2021
public class BinRangeDTO {

    private String country;
    private String countryCode;
    private String cardBrand;
    private boolean isCommercial;
    private String binNumber;
    private String issuer;
    private String issuerWebsite;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCardBrand() {
        return cardBrand;
    }

    public void setCardBrand(String cardBrand) {
        this.cardBrand = cardBrand;
    }

    public boolean isCommercial() {
        return isCommercial;
    }

    public void setCommercial(boolean commercial) {
        isCommercial = commercial;
    }

    public String getBinNumber() {
        return binNumber;
    }

    public void setBinNumber(String binNumber) {
        this.binNumber = binNumber;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getIssuerWebsite() {
        return issuerWebsite;
    }

    public void setIssuerWebsite(String issuerWebsite) {
        this.issuerWebsite = issuerWebsite;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public boolean isPrepaid() {
        return isPrepaid;
    }

    public void setPrepaid(boolean prepaid) {
        isPrepaid = prepaid;
    }

    public String getCardCategory() {
        return cardCategory;
    }

    public void setCardCategory(String cardCategory) {
        this.cardCategory = cardCategory;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    private boolean valid;
    private String cardType;
    private boolean isPrepaid;
    private String cardCategory;
    private String currencyCode;

    @Override
    public String toString() {
        return "BinRangeDTO{" +
                "country='" + country + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", cardBrand='" + cardBrand + '\'' +
                ", isCommercial=" + isCommercial +
                ", binNumber='" + binNumber + '\'' +
                ", issuer='" + issuer + '\'' +
                ", issuerWebsite='" + issuerWebsite + '\'' +
                ", valid=" + valid +
                ", cardType='" + cardType + '\'' +
                ", isPrepaid=" + isPrepaid +
                ", cardCategory='" + cardCategory + '\'' +
                ", currencyCode='" + currencyCode + '\'' +
                '}';
    }
}
