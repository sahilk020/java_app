package com.crmws.service;

import com.crmws.dto.AutorefundDto;

public interface AutoRefundservice {
	
	public AutorefundDto GetPayidByPgrefno(String Pgfreno);
	public  boolean getrefundSatusByPayid( AutorefundDto payId);
	public String refundintiated(String pgrefno);
}
