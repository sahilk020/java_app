package com.crmws.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pay10.commons.mongo.WalletHistoryRepository;
import com.pay10.commons.user.AcquirerMasterPayoutDao;
import com.pay10.commons.user.TdrSettingPayout;
import com.pay10.commons.user.TdrSettingPayoutDao;
import com.pay10.commons.util.MopType;
import com.pay10.commons.util.PaymentType;

@RestController
@RequestMapping("/acquirerPayout")
public class AcquirerMappingpayoutController {
	private static final Logger logger = LoggerFactory.getLogger(AcquirerMappingpayoutController.class.getName());

	@Autowired
	AcquirerMasterPayoutDao acquirerMastrPayoutDao;

	@Autowired
	TdrSettingPayoutDao tdrSettingPayoutDao;
	@Autowired 
	private WalletHistoryRepository walletHistoryRepository;

	@GetMapping("/getAcquirerList")
	public List<String> getAcquirerList() {

		List<String> acquirerList = acquirerMastrPayoutDao.getPayoutAcquirer();
		logger.info("size for array List" + acquirerList.size());

		return acquirerList;

	}

	@GetMapping("/getChannelList")
	public List<String> getChannelList(@RequestParam String acquirer) {

		List<String> chanList = acquirerMastrPayoutDao.getchannelByAcquier(acquirer);
		logger.info("size for array List" + chanList.size());

		return chanList;

	}

	@GetMapping("/getcurrencyList")
	public Map<String, String> getcurrencyList(@RequestParam String acquirer, @RequestParam String channel,
			@RequestParam String payId) {

		Map<String, String> chanListdata = new HashMap<String, String>();

		List<String> chanList = acquirerMastrPayoutDao.getcurrencyByAcquierAndChannel(acquirer, channel);

		logger.info("size for array List" + chanList.size());
		for (String currencydata : chanList) {
			chanListdata.put(currencydata, tdrSettingPayoutDao.getCurrenyStatus(payId, currencydata, acquirer,channel));

		}
		return chanListdata;

	}

	@PostMapping("/savecurrencyList")
	public Map<String, String> getcurrencyList(@RequestBody String Json) {
	logger.info("datta for save record"+Json);
		
		JSONObject request =new JSONObject(Json);
		String acquirer=request.getString("acquirer");
		String channel=request.getString("channel");
		String payId=request.getString("payId");
		String upDateBy=request.getString("upDateBy");
		String rule1=request.getString("rule");
		
		Map< String , String > rule=new HashMap<String ,String>();
		String [] data=rule1.split("\\|");

		System.out.println(data.length);
		for (String data1 : data) {
			String[] data2 = data1.split("=");
			if (StringUtils.isNoneBlank(data2[0])) {
				rule.put(data2[0], data2[1]);
				//Get currency Name
				//data2[0]
				boolean exist = walletHistoryRepository.findMerchantFundByPayIdAndCurrency(payId,data2[0]);
				if (!exist) {
					walletHistoryRepository.createMerchantWalletByPayIdAndCurrencyName(payId,data2[0]);
				}

			}

		}
		
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		Map<String, String> chanListdata = new HashMap<String, String>();
logger.info("MAP Data for Save Recored "+rule);
		if (!(rule == null)) {
			for (Map.Entry<String, String> m : rule.entrySet()) {
				logger.info("MAP Data for Save Recored "+m);

				TdrSettingPayout tdrSettingPayout = tdrSettingPayoutDao.getTdrSettingPayoutDetail(payId, channel,
						acquirer, m.getKey());
				logger.info("tdrSettingPayout Data for Save Recored "+tdrSettingPayout);

				if (tdrSettingPayout == null && m.getValue().equals("ACTIVE")) {
					TdrSettingPayout details = new TdrSettingPayout();

					details.setAcquirerName(acquirer);
					details.setBankPreference("FLAT");
					details.setBankMaxTdrAmt(0.00);
					details.setBankMinTdrAmt(0.00);
					details.setBankTdr(0.00);
					details.setEnableSurcharge(false);
					details.setUpdatedBy(upDateBy);
					details.setFromDate(getCurrentFormattedDate());
					details.setMaxTransactionAmount(0.00);
					details.setMerchantMaxTdrAmt(0.00);
					details.setMerchantMinTdrAmt(0.00);
					details.setMerchantPreference("FLAT");
					details.setMerchantTdr(0.00);
					details.setMinTransactionAmount(0.00);
					details.setPayId(payId);
					details.setPaymentRegion("DOMESTIC");
					details.setStatus("ACTIVE");
					details.setTdrStatus("INACTIVE");
					details.setTransactionType("SALE");
					details.setType("CONSUMER");
					details.setChannel(channel);

					System.out.println(m.getKey());
					details.setCurrency(m.getKey());
					tdrSettingPayoutDao.create(details);
					chanListdata.put("response", "Mapping Saved Successfully");

				} else {
					if(!(tdrSettingPayout==null)) {
					if (m.getValue().equals("ACTIVE") && tdrSettingPayout.getStatus().equals("INACTIVE")) {
						tdrSettingPayout.setStatus("ACTIVE");
						tdrSettingPayout.setUpdatedBy(upDateBy);
						tdrSettingPayoutDao.update(tdrSettingPayout);
						System.out.println(m.getKey() + " " + m.getValue());
						chanListdata.put("response", "Mapping Updated Successfully");
					}
					if (m.getValue().equals("INACTIVE") && tdrSettingPayout.getStatus().equals("ACTIVE")) {
						tdrSettingPayout.setStatus("INACTIVE");
						tdrSettingPayout.setUpdatedBy(upDateBy);
						tdrSettingPayoutDao.update(tdrSettingPayout);
						chanListdata.put("response", "Mapping Updated Successfully");

					}
					if (m.getValue().equals("ACTIVE") && tdrSettingPayout.getStatus().equals("ACTIVE")) {
						chanListdata.put("response", "Mapping Updated Successfully");
					}
					}
					
				}

			}
		}
		return chanListdata;

	}

	
	
	public static String getCurrentFormattedDate() {
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return formatter.format(new Date(System.currentTimeMillis()));
	}
}
