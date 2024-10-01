package com.pay10.crm.role.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AssignUserDto {

    @JsonProperty("fullName")
    private String fullName;

    @JsonProperty("userId")
    private String userId;

    @JsonProperty("payId")
    private String payId;

    @JsonProperty("emailId")
    private String emailId;

    @JsonProperty("roleId")
    private Long roleId;

    @JsonProperty("contactNumber")
    private String contactNumber;

    @Override
    public String toString() {
        return "AssignUserDto{" +
                "fullName='" + fullName + '\'' +
                ", userId='" + userId + '\'' +
                ", payId='" + payId + '\'' +
                ", emailId='" + emailId + '\'' +
                ", roleId=" + roleId +
                ", contactNumber='" + contactNumber + '\'' +
                '}';
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPayId() {
        return payId;
    }

    public void setPayId(String payId) {
        this.payId = payId;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }
}
