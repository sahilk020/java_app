package com.crmws.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EkycOtpRequestDTO {
private String payId;
private String txnId;
private String mobileno;
private String adhar;
private String hash;

}
