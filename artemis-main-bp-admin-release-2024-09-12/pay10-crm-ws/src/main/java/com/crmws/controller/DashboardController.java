package com.crmws.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.pay10.commons.user.MultCurrencyCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.crmws.dto.ApiResponse;
import com.crmws.dto.DashBoardTxnCountRequest;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crmws.dto.ApiResponse;
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
//import com.pay10.service.GetMessageService;
import com.crmws.service.DashboardService;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

	@Autowired
	private DashboardService dashboardService;

	@PostMapping("/getPichartData")
	public ResponseEntity<ApiResponse> getPichart(@RequestBody Map<String, String> data) {

        String dateFrom = data.get("dateFrom");
        String dateTo = data.get("dateTo");
        String acquirer = data.get("acquirer");

        //System.out.println(LocalDate.parse(dateFrom, formatter_1));
        System.out.println("Request received for getPichart :: " + data.toString());
        Optional<List<GetPiChart>> getPiChart = dashboardService.getPiChartQuery(dateFrom, dateTo, acquirer);
        ApiResponse res = new ApiResponse();
        if (getPiChart.isPresent()) {
            res.setMessage("Data fetched succesfully");
            res.setData(getPiChart.get());
            res.setStatus(true);
        } else {
            res.setMessage("Data not found");
            res.setData(null);
            res.setStatus(false);
        }
        return ResponseEntity.ok(res);
    }

    @GetMapping("/getUserCurrency")
    public ResponseEntity<List<MultCurrencyCode>> getUserCurrency(@RequestParam String emailId){
        return ResponseEntity.ok(dashboardService.getUserCurrency(emailId));
    }


    @PostMapping("/getPiChartPopup")
    public ResponseEntity<ApiResponse> getPiChartPopup(@RequestBody Map<String, String> data) {

        String dateFrom = data.get("dateFrom");
        String dateTo = data.get("dateTo");
        String acquirer = data.get("acquirer");
        String mopType = data.get("mopType");
        System.out.println("MopType Selected : " + mopType);

        Optional<List<PiChartPopup>> piChartPop = dashboardService.getPiChartPop(dateFrom, dateTo, acquirer, mopType);
        ApiResponse res = new ApiResponse();

        if (piChartPop.isPresent()) {
            res.setMessage("Data fetched succesfully. Size = "+piChartPop.get().size());
            res.setData(piChartPop.get());
            res.setStatus(true);
        } else {
            res.setMessage("Data not found");
            res.setData(null);
            res.setStatus(false);
        }

        return ResponseEntity.ok(res);

	}

	@PostMapping("/lineChartData")
	public ResponseEntity<List<LineChartRespDto>> getLineChart(@RequestBody LineChartReqDto data) {

		ResponseEnvelope<List<LineChartRespDto>> response = dashboardService.getLineChart(data);
		return new ResponseEntity<>(response.getPayLoad(), response.getHttpStatus());

	}

    @PostMapping("/dashboardTransactionData")
	public ResponseEntity<PieChart> getData(@RequestBody DashBoardTxnCountRequest request) {
		ResponseEnvelope<PieChart> response = dashboardService.getDashBoardTotalTxnDetails(request);
		return new ResponseEntity<>(response.getPayLoad(), response.getHttpStatus());
	}

	@PostMapping("/pieChartData")
	public ResponseEntity<List<PieChart>> getPieChartData(@RequestBody DashBoardTxnCountRequest request) {
		ResponseEnvelope<List<PieChart>> response = dashboardService.getPieChartDetails(request);
		return new ResponseEntity<>(response.getPayLoad(), response.getHttpStatus());
	}

	@PostMapping("/hourlyChartData")
	public ResponseEntity<List<HourlyChartData>> getHourlyChartData(@RequestBody DashBoardTxnCountRequest request) {
		ResponseEnvelope<List<HourlyChartData>> response = dashboardService.getHourlyChartData(request);
		return new ResponseEntity<>(response.getPayLoad(), response.getHttpStatus());
	}

	@PostMapping("/settlementChartData")
	public ResponseEntity<List<SettlementChartData>> getSettlementChartData(
			@RequestBody DashBoardTxnCountRequest request) {
		ResponseEnvelope<List<SettlementChartData>> response = dashboardService.getSettlementChartData(request);
		return new ResponseEntity<>(response.getPayLoad(), response.getHttpStatus());
	}

	// Added By Sweety
		@PostMapping("/fraudlineChartData")
		public ResponseEntity<List<LineChartRespDto>> getFraudLineChart(@RequestBody LineChartReqDto data) {

			ResponseEnvelope<List<LineChartRespDto>> response = dashboardService.getFraudLineChart(data);
			return new ResponseEntity<>(response.getPayLoad(), response.getHttpStatus());

		}

}
