package com.crmws.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.crmws.dto.DashBoardTxnCountRequest;
import com.crmws.dto.DashboardTxnDetails;
import com.crmws.dto.GetPiChart;
import com.crmws.dto.HourlyChartData;
import com.crmws.dto.LineChartReqDto;
import com.crmws.dto.LineChartRespDto;
import com.crmws.dto.PiChartPopup;
import com.crmws.dto.PieChart;
import com.crmws.dto.ResponseEnvelope;
import com.crmws.dto.SettlementChartData;
import com.pay10.commons.user.MultCurrencyCode;

public interface DashboardService {
	public Optional<List<GetPiChart>> getPiChartQuery(String dateFrom, String dateTo, String acquirer);

	public Optional<List<PiChartPopup>> getPiChartPop(String dateFrom, String dateTo, String acquirer, String mopType);

	public ResponseEnvelope<List<LineChartRespDto>> getLineChart(LineChartReqDto data);

	//public ResponseEnvelope<DashboardTxnDetails> getDashBoardTotalTxnDetails(DashBoardTxnCountRequest request);

	public ResponseEnvelope<List<PieChart>> getPieChartDetails(DashBoardTxnCountRequest request);

	public ResponseEnvelope<List<HourlyChartData>> getHourlyChartData(DashBoardTxnCountRequest request);

	public ResponseEnvelope<List<SettlementChartData>> getSettlementChartData(DashBoardTxnCountRequest request);

	public ResponseEnvelope<List<LineChartRespDto>> getFraudLineChart(LineChartReqDto data);
	public ResponseEnvelope<PieChart> getDashBoardTotalTxnDetails(DashBoardTxnCountRequest request);

	public List<MultCurrencyCode> getUserCurrency(String emailId);

}
