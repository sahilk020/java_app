package com.crmws.service;


import com.crmws.entity.ResponseMessageChargebackDashboard;

public interface ChargebackDashboardService {
	public ResponseMessageChargebackDashboard getChargebackCount(String merchant, String acquirer, String dateFrom,
			String dateTo);
}
