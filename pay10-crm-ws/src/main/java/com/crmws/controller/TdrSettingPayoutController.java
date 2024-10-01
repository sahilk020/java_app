package com.crmws.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.pay10.commons.user.TdrSettingPayout;
import com.pay10.commons.user.TdrSettingPayoutDao;

@RestController
@RequestMapping("/TdrSettingPayout")
public class TdrSettingPayoutController {

    private static final Logger logger = LoggerFactory.getLogger(TdrSettingPayoutController.class.getName());
    @Autowired
    TdrSettingPayoutDao tdrSettingPayoutDao;

    @GetMapping("/getAcquirerList")
    public List<String> getAcquirerList(@RequestParam String payId) {

        List<String> acquirerList = tdrSettingPayoutDao.getAcquierByPayId(payId);
        logger.info("size for array List" + acquirerList.size());

        return acquirerList;

    }

    @GetMapping("/getChannelList")
    public List<String> getChannelList(@RequestParam String acquirer, @RequestParam String payId) {

        List<String> chanList = tdrSettingPayoutDao.getchannelByAcquier(acquirer, payId);
        logger.info("size for array List" + chanList.size());

        return chanList;

    }

    @GetMapping("/getCurrencyList")
    public List<String> getCurrencyList(@RequestParam String acquirer, @RequestParam String payId, @RequestParam String channel) {

        List<String> currencyList = tdrSettingPayoutDao.getCurrencyList(acquirer, payId, channel);
        logger.info("size for array List" + currencyList.size());

        return currencyList;

    }


    @GetMapping("/getTdrListDetail")
    public List<TdrSettingPayout> getTdrListDetail(@RequestParam String acquirer, @RequestParam String payId,
                                                   @RequestParam String channel) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        List<TdrSettingPayout> chanList = tdrSettingPayoutDao.getTdrPayoutAcquirer(acquirer, payId, channel).stream().map(tdrSetting -> {
            try {
                tdrSetting.setfDate(
                        (tdrSetting.getfDate() != null) ? dateFormat1.format(dateFormat.parse(tdrSetting.getfDate()))
                                : dateFormat1.format(new Date()));
                tdrSetting.setFromDate(
                        (tdrSetting.getFromDate() != null) ? dateFormat1.format(dateFormat.parse(tdrSetting.getFromDate()))
                                : dateFormat1.format(new Date()));

                tdrSetting.setUpdatedAt(
                        (tdrSetting.getUpdatedAt() != null) ? dateFormat1.format(dateFormat.parse(tdrSetting.getUpdatedAt()))
                                : dateFormat1.format(new Date()));
            } catch (ParseException e) {
                // Handle the exception appropriately
                logger.error("Error", e);
            }

            tdrSetting.setUpdatedAt(
                    (tdrSetting.getUpdatedAt() != null) ? tdrSetting.getUpdatedAt() : "NA");
            return tdrSetting;
        }).collect(Collectors.toList());
        logger.info("size for array List" + chanList.size());

        return chanList;

    }


    @SuppressWarnings("unused")
    @PostMapping("/saveTdrListDetail")
    public String saveTdrListDetail(@RequestBody Map<String, String> reqInfo) {

        TdrSettingPayout tdrSettingPayout = new TdrSettingPayout(reqInfo);
        logger.info("TdrSettingPayout reqInfo: {}", reqInfo);
        logger.info("TdrSettingPayout JSON", new Gson().toJson(tdrSettingPayout));
        if (tdrSettingPayout.getId() == 0) {
            tdrSettingPayout.setId(null);
        }
        if (tdrSettingPayoutDao.checkIfExists(tdrSettingPayout)) {
            return "TDR setting already exists with same details";
        }
        tdrSettingPayoutDao.update(tdrSettingPayout);

        return "TDR setting saved successfully";

    }

    @GetMapping("/Hello")
    public String test() {
        return "Hello";

    }

}
