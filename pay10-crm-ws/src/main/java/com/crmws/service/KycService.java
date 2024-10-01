package com.crmws.service;

import javax.servlet.http.HttpServletRequest;

import com.crmws.dto.EkycOtpRequestDTO;
import com.crmws.dto.EkycOtpResponseDTO;
import com.crmws.dto.EkycOtpVerRequestDTO;
import com.crmws.dto.EkycOtpVerResponseDTO;

public interface KycService {

	EkycOtpResponseDTO generateAadharOTP(EkycOtpRequestDTO ekycOtpRequestDTO,HttpServletRequest request);

	EkycOtpVerResponseDTO verifyAadharOTP(EkycOtpVerRequestDTO ekOtpVerRequestDTO,HttpServletRequest request);

}
