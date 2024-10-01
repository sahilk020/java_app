package com.crmws.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EkycOtpVerRequestDTO {
private String payId;
private String txnId;
private String otp;
private String hash;

}
