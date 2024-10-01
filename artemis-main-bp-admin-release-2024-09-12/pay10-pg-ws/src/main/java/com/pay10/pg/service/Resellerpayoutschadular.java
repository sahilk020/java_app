package com.pay10.pg.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.dao.ResellerDao;
import com.pay10.commons.dao.Resellerdailyupdatedao;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.MultCurrencyCode;
import com.pay10.commons.user.MultCurrencyCodeDao;
import com.pay10.commons.user.ResellerPayout;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.user.resellercommisiondao;
import com.pay10.commons.util.CrmFieldType;

@Service
public class Resellerpayoutschadular {
    private static Logger logger = LoggerFactory.getLogger(Resellerpayoutschadular.class.getName());

    @Autowired
    UserDao userDao;

    @Autowired
    resellercommisiondao commisiondao;

    @Autowired
    Resellerdailyupdatedao resellerdailyupdatedao;

    @Autowired
    ResellerDao resellerdao;

    @Autowired
    MultCurrencyCodeDao multiCurrencyCodeDao;

    private static String getId() {
        return String.valueOf(System.currentTimeMillis());
    }

    public String process() throws SystemException {
        String cycle = "1";
        String startDate = getFromDateAsPerCycle(cycle);
        logger.info("process:: startDate={}", startDate);
        generatePayout(cycle, startDate);
        return null;
    }

    private void generatePayout(String cycle, String fromDate) {
        String toDate = getToDate();
        List<String> resellerPayIds = commisiondao.getresellerpayidbycycle(cycle);
        resellerPayIds.forEach(resellerPayId -> {
            List<MultCurrencyCode> currencyList = multiCurrencyCodeDao.findAll();
            logger.info("currencyList for reseller payout:: ={}", currencyList);
            for (MultCurrencyCode currency : currencyList) {
                List<Document> docsForPayout = resellerdailyupdatedao.getForPayoutByResellerAndDate(resellerPayId, fromDate,
                        toDate, currency);

                logger.info("generatePayout:: Initialized for resellerId={}, fromDate={}, toDate={}", resellerPayId,
                        fromDate, toDate);


                Map<String, Double> commissionDetails = calculateCommissionTotal(docsForPayout);

                createEntryInDb(resellerPayId, commissionDetails, fromDate, toDate, currency);

                logger.info("generatePayout:: Generated. for resellerId={}, fromDate={}, toDate={}", resellerPayId,
                        fromDate, toDate);

            }

        });

    }

    private String getFromDateAsPerCycle(String cycle) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        int minusDate = StringUtils.equals(cycle, "1") ? 2 : 4;
        cal.set(Calendar.MONTH, Integer.parseInt(new SimpleDateFormat("M").format(cal.getTime())) - minusDate);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        return new SimpleDateFormat("YYYY-MM-dd").format(cal.getTime());
    }

    private String getToDate() {
        Calendar data = Calendar.getInstance();
        data.setTime(new Date());
        data.set(Calendar.MONTH, Integer.parseInt(new SimpleDateFormat("M").format(data.getTime())) - 2);
        data.set(Calendar.DAY_OF_MONTH, data.getActualMaximum(Calendar.DAY_OF_MONTH));
        return new SimpleDateFormat("YYYY-MM-dd").format(data.getTime());
    }

    private Map<String, Double> calculateCommissionTotal(List<Document> docs) {

        double totalSMACommission = 0;
        double totalSuccessSale = 0;

        double totalMACommission = 0;
        double totalAgentCommission = 0;
        String currency = null;

//		double totalSMACommission = 0;
//		double totalSMASuccessSale = 0;
//
//		double totalMACommission = 0;
//		double totalMASuccessSale = 0;
//
//		double totalAgentCommission = 0;
//		double totalAgentSuccessSale = 0;
        for (Document doc : docs) {
            double smacommissionAmt = doc.getDouble(CrmFieldType.SMA_COMMISSION.getName()) == null ? 0
                    : doc.getDouble(CrmFieldType.SMA_COMMISSION.getName());
            double macommissionAmt = doc.getDouble(CrmFieldType.MA_COMMISSION.getName()) == null ? 0
                    : doc.getDouble(CrmFieldType.MA_COMMISSION.getName());
            double agentcommissionAmt = doc.getDouble(CrmFieldType.AGENT_COMMISSION.getName()) == null ? 0
                    : doc.getDouble(CrmFieldType.AGENT_COMMISSION.getName());
            logger.info("calculateCommissionTotal:: docs smacommissionAmt={},macommissionAmt={}, agentcommissionAmt={}",
                    smacommissionAmt, macommissionAmt, agentcommissionAmt);
            double saleAmt = doc.getDouble(CrmFieldType.NET_AMOUNT.getName()) == null ? 0
                    : doc.getDouble(CrmFieldType.NET_AMOUNT.getName());
            totalSMACommission = totalSMACommission + smacommissionAmt;
            totalMACommission = totalMACommission + macommissionAmt;
            totalAgentCommission = totalAgentCommission + agentcommissionAmt;
            totalSuccessSale = totalSuccessSale + saleAmt;

        }
        Map<String, Double> commissionDetails = new HashMap<>();
        commissionDetails.put("totalSMACommission", totalSMACommission);
        commissionDetails.put("totalMACommission", totalMACommission);
        commissionDetails.put("totalAgentCommission", totalAgentCommission);
        commissionDetails.put("totalSuccess", totalSuccessSale);
        return commissionDetails;
    }

    private void createEntryInDb(String resellerPayId, Map<String, Double> commissionDetails, String fromDate,
                                 String toDate, MultCurrencyCode currency) {
        if (commissionDetails.getOrDefault("totalSMACommission", 0d) == 0 &&
                commissionDetails.getOrDefault("totalMACommission", 0d) == 0 &&
                commissionDetails.getOrDefault("totalAgentCommission", 0d) == 0
        ) {
            return;
        }
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ResellerPayout resellerPayout = new ResellerPayout();
        resellerPayout.setBatchNo("");
        resellerPayout.setResellerId(resellerPayId);
        resellerPayout.setTotalSMACommission(commissionDetails.getOrDefault("totalSMACommission", 0d));
        resellerPayout.setTotalMACommission(commissionDetails.getOrDefault("totalMACommission", 0d));
        resellerPayout.setTotalAgentCommission(commissionDetails.getOrDefault("totalAgentCommission", 0d));
        resellerPayout.setTotalamount((double) commissionDetails.getOrDefault("totalSuccess", 0d));
        resellerPayout.setCreationDate(df.format(new Date()));
        resellerPayout.setFromDate(fromDate);
        resellerPayout.setToDate(toDate);
        resellerPayout.setPayoutId(getId());
        resellerPayout.setStatus("Pending");
        resellerPayout.setResellerName(userDao.getBusinessNameByPayId(resellerPayId));
        resellerPayout.setCurrency(currency.getCode());
        resellerdao.create(resellerPayout);
    }

    public String payoutQuaterly() throws SystemException {

        String cycle = "2";
        String startDate = getFromDateAsPerCycle(cycle);
        logger.info("payoutQuaterly:: startDate={}", startDate);
        generatePayout(cycle, startDate);
        return null;
    }
}
