package com.crmws.dao;

import java.util.List;
import java.util.Optional;

import com.crmws.dto.DashBoardTxnCountRequest;
import com.crmws.dto.DashboardTxnDetails;
import com.crmws.dto.GetPiChart;
import com.crmws.dto.HourlyChartData;
import com.crmws.dto.LineChartRespDto;
import com.crmws.dto.LineChartType;
import com.crmws.dto.PiChartPopup;
import com.crmws.dto.PieChart;
import com.crmws.dto.SettlementChartData;

public interface DashbordDao {
	public Optional<List<GetPiChart>> getPiChartQuery(String dateFrom, String dateTo, String acquirer);

	public Optional<List<PiChartPopup>> getPiChartPop(String dateFrom, String dateTo, String acquirer, String mopType);

	public Optional<List<LineChartRespDto>> getLineChart(String dateFrom, String dateTo, String acquirer,
			String mopType, String paymentType, String status, String statusType, LineChartType type);

	//public Optional<DashboardTxnDetails> getDashboardTotalTxnDetails(DashBoardTxnCountRequest request);
	public Optional<PieChart> getDashboardTotalTxnDetails(DashBoardTxnCountRequest request);

	public Optional<List<PieChart>> getPieChartDetails(DashBoardTxnCountRequest request);

	public Optional<List<HourlyChartData>> getHourlyChartData(DashBoardTxnCountRequest request);

	public Optional<List<SettlementChartData>> getSettlementChartData(DashBoardTxnCountRequest request);

	public Optional<List<LineChartRespDto>> getFraudLineChart(String string, String string2, String acquirer,
			String mop, String paymentType, String status, String statusType, LineChartType type, String payId);

}
