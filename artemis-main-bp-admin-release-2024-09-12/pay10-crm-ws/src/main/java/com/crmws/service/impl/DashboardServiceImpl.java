package com.crmws.service.impl;

import java.util.*;

import com.pay10.commons.user.MultCurrencyCode;
import com.pay10.commons.user.MultCurrencyCodeDao;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.crmws.dao.DashbordDao;
import com.crmws.dto.DashBoardTxnCountRequest;
import com.crmws.dto.DashboardTxnDetails;
import com.crmws.dto.DashBoardTxnCountRequest;
import com.crmws.dto.GetPiChart;
import com.crmws.dto.HourlyChartData;
import com.crmws.dto.LineChartReqDto;
import com.crmws.dto.LineChartRespDto;
import com.crmws.dto.PiChartPopup;
import com.crmws.dto.PieChart;
import com.crmws.dto.ResponseEnvelope;
import com.crmws.dto.SettlementChartData;
import com.crmws.service.DashboardService;
import com.crmws.util.DateUtil;

@Service
public class DashboardServiceImpl implements DashboardService {

	private static final Logger logger = LoggerFactory.getLogger(DashboardServiceImpl.class.getName());
	@Autowired
	private DashbordDao dashbordDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private MultCurrencyCodeDao multCurrencyCodeDao;

	@Override
	public Optional<List<GetPiChart>> getPiChartQuery(String dateFrom, String dateTo, String acquirer) {
		return dashbordDao.getPiChartQuery(dateFrom, dateTo, acquirer);
	}

	@Override
	public Optional<List<PiChartPopup>> getPiChartPop(String dateFrom, String dateTo, String acquirer, String mopType) {
		return dashbordDao.getPiChartPop(dateFrom, dateTo, acquirer, mopType);
	}

	@Override
	public ResponseEnvelope<List<LineChartRespDto>> getLineChart(LineChartReqDto data) {

		List<LineChartRespDto> chartDataList = new ArrayList<>();
		try {
			String[] dateArr = DateUtil.getDateBetween(data.getRange());
			Optional<List<LineChartRespDto>> chartDataListOptional = dashbordDao.getLineChart(dateArr[0], dateArr[1],
					data.getAcquirer(), data.getMop(), data.getPaymentType(), data.getStatus(), data.getStatusType(),
					data.getType());
			if (chartDataListOptional.isPresent()) {
				chartDataList = chartDataListOptional.get();
			}
		} catch (Exception e) {
			logger.error("Exception: " + e);
			e.printStackTrace();
			return new ResponseEnvelope<>(HttpStatus.INTERNAL_SERVER_ERROR, chartDataList, "Internal Error");
		}

		return new ResponseEnvelope<List<LineChartRespDto>>(HttpStatus.OK, chartDataList);
	}

	/*@Override
	public ResponseEnvelope<DashboardTxnDetails> getDashBoardTotalTxnDetails(DashBoardTxnCountRequest request) {

		if (StringUtils.equalsIgnoreCase(request.getCurrency(), "ALL")) {
			request.setCurrency("");
		}
		if (StringUtils.equalsIgnoreCase(request.getEmailId(), "ALL")) {
			request.setEmailId("");
		}
		if (StringUtils.equalsIgnoreCase(request.getPaymentType(), "ALL")) {
			request.setPaymentType("");
		}
		if (StringUtils.equalsIgnoreCase(request.getMopType(), "ALL")) {
			request.setMopType("");
		}
		if (StringUtils.equalsIgnoreCase(request.getAcquirer(), "ALL")) {
			request.setAcquirer("");
		}
		if (StringUtils.equalsIgnoreCase(request.getTxnType(), "ALL")) {
			request.setTxnType("");
		}
		return new ResponseEnvelope<DashboardTxnDetails>(HttpStatus.OK,
				dashbordDao.getDashboardTotalTxnDetails(request).get());
	}*/

	@Override
	public ResponseEnvelope<List<PieChart>> getPieChartDetails(DashBoardTxnCountRequest request) {
		if (StringUtils.equalsIgnoreCase(request.getCurrency(), "ALL")) {
			request.setCurrency("");
		}
		if (StringUtils.equalsIgnoreCase(request.getEmailId(), "ALL")) {
			request.setEmailId("");
		}
		if (StringUtils.equalsIgnoreCase(request.getPaymentType(), "ALL")) {
			request.setPaymentType("");
		}
		if (StringUtils.equalsIgnoreCase(request.getMopType(), "ALL")) {
			request.setMopType("");
		}
		if (StringUtils.equalsIgnoreCase(request.getAcquirer(), "ALL")) {
			request.setAcquirer("");
		}
		if (StringUtils.equalsIgnoreCase(request.getTxnType(), "ALL")) {
			request.setTxnType("");
		}
		if (StringUtils.equalsIgnoreCase(request.getCurrency(), "ALL")) {
			request.setCurrency("");
		}
		return new ResponseEnvelope<List<PieChart>>(HttpStatus.OK, dashbordDao.getPieChartDetails(request).get());
	}

	@Override
	public ResponseEnvelope<List<HourlyChartData>> getHourlyChartData(DashBoardTxnCountRequest request) {
		if (StringUtils.equalsIgnoreCase(request.getCurrency(), "ALL")) {
			request.setCurrency("");
		}
		if (StringUtils.equalsIgnoreCase(request.getEmailId(), "ALL")) {
			request.setEmailId("");
		}
		if (StringUtils.equalsIgnoreCase(request.getPaymentType(), "ALL")) {
			request.setPaymentType("");
		}
		if (StringUtils.equalsIgnoreCase(request.getMopType(), "ALL")) {
			request.setMopType("");
		}
		if (StringUtils.equalsIgnoreCase(request.getAcquirer(), "ALL")) {
			request.setAcquirer("");
		}
		if (StringUtils.equalsIgnoreCase(request.getTxnType(), "ALL")) {
			request.setTxnType("");
		}
		return new ResponseEnvelope<List<HourlyChartData>>(HttpStatus.OK,
				dashbordDao.getHourlyChartData(request).get());
	}

	@Override
	public ResponseEnvelope<List<SettlementChartData>> getSettlementChartData(DashBoardTxnCountRequest request) {
		
		
			if (StringUtils.equalsIgnoreCase(request.getCurrency(), "ALL")) {
				request.setCurrency("");
			}
			if (StringUtils.equalsIgnoreCase(request.getEmailId(), "ALL")) {
				request.setEmailId("");
			}
			if (StringUtils.equalsIgnoreCase(request.getPaymentType(), "ALL")) {
				request.setPaymentType("");
			}
			if (StringUtils.equalsIgnoreCase(request.getMopType(), "ALL")) {
				request.setMopType("");
			}
			if (StringUtils.equalsIgnoreCase(request.getAcquirer(), "ALL")) {
				request.setAcquirer("");
			}
			if (StringUtils.equalsIgnoreCase(request.getTxnType(), "ALL")) {
				request.setTxnType("");
			}
			return new ResponseEnvelope<List<SettlementChartData>>(HttpStatus.OK,
					dashbordDao.getSettlementChartData(request).get());
		
		
	
	}

	// Added By Sweety for fraudLineChart
	@Override
	public ResponseEnvelope<List<LineChartRespDto>> getFraudLineChart(LineChartReqDto data) {
		List<LineChartRespDto> chartDataList = new ArrayList<>();
		try {
			String[] dateArr = DateUtil.getDateBetween(data.getRange());
			Optional<List<LineChartRespDto>> chartDataListOptional = dashbordDao.getFraudLineChart(dateArr[0],
					dateArr[1], data.getAcquirer(), data.getMop(), data.getPaymentType(), data.getStatus(),
					data.getStatusType(), data.getType(), data.getPayId());
			if (chartDataListOptional.isPresent()) {
				chartDataList = chartDataListOptional.get();
			}

		} catch (Exception e) {
			logger.error("Exception: " + e);
			e.printStackTrace();
			return new ResponseEnvelope<>(HttpStatus.INTERNAL_SERVER_ERROR, chartDataList, "Internal Error");
		}

		return new ResponseEnvelope<List<LineChartRespDto>>(HttpStatus.OK, chartDataList);
	}


	@Override
	public ResponseEnvelope<PieChart> getDashBoardTotalTxnDetails(DashBoardTxnCountRequest request) {

		if (StringUtils.equalsIgnoreCase(request.getCurrency(), "ALL")) {
			request.setCurrency("");
		}
		if (StringUtils.equalsIgnoreCase(request.getEmailId(), "ALL")) {
			request.setEmailId("");
		}
		if (StringUtils.equalsIgnoreCase(request.getPaymentType(), "ALL")) {
			request.setPaymentType("");
		}
		if (StringUtils.equalsIgnoreCase(request.getMopType(), "ALL")) {
			request.setMopType("");
		}
		return new ResponseEnvelope<PieChart>(HttpStatus.OK, dashbordDao.getDashboardTotalTxnDetails(request).get());
	}

	@Override
	public List<MultCurrencyCode> getUserCurrency(String emailId) {
		User user = userDao.findByEmailId(emailId);
		logger.info("Email Id: {}", emailId);
		logger.info("User: {}", user);
		if(user!=null) {
			List<String> currencyList = userDao.findCurrencyByPayId(user.getPayId());
			List<MultCurrencyCode> currencyCodeList = new ArrayList<>();
			for (String currencyCode : currencyList) {
				currencyCodeList.add(multCurrencyCodeDao.findByCode(currencyCode));
			}
			return currencyCodeList;
		}else{
			return multCurrencyCodeDao.findAll();
		}
	}
}
