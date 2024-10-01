package com.pay10.crm.action;

import com.google.gson.Gson;
import com.pay10.commons.user.POReportDTO;
import com.pay10.commons.user.TransactionSearchNew;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.DataEncoder;
import com.pay10.commons.util.DateCreater;
import com.pay10.crm.mongoReports.PGPOReportDTO;
import com.pay10.crm.mongoReports.PoTxnReport;
import com.pay10.crm.util.SortingUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("serial")
public class PayoutNewAction extends AbstractSecureAction {
    private static Logger logger = LoggerFactory.getLogger(PayoutNewAction.class.getName());
    @Autowired
    private UserDao userDao;
    @Autowired
    private PoTxnReport poTxnReport;
    @Autowired
    private DataEncoder encoder;

    private List<TransactionSearchNew> aaDataTxn = new ArrayList<TransactionSearchNew>();
    private List<POReportDTO> aaDataPO = new ArrayList<POReportDTO>();
    private List<Object> aaDataPGPO = new ArrayList<>();

    private List<Object> aaData = new ArrayList<Object>();
    private User sessionUser = null;
    private Map<String, String> payIdList = new HashMap<>();
    private String transactionId;
    private String tenantId;
    private String customerEmail;
    private String customerPhone;
    private String merchantEmailId;
    private String paymentType;
    private String cardNumber;
    private String acquirer;
    private String currency;
    private String dateFrom;
    private String dateTo;
    private String newDespositor;
    private int draw;
    private int length;
    private int start;
    private String transactionType;
    private String mopType;
    private String startTime;
    private String endTime;
    private String ipAddress;
    private String totalAmount;
    private String rrn;
    private BigInteger recordsTotal = BigInteger.ZERO;
    private BigInteger recordsFiltered = BigInteger.ZERO;
    private String channelName;
    private String minAmount;
    private String maxAmount;
    private String columnName;
    private String logicalCondition;
    private String searchText;
    private String columnName1;
    private String logicalCondition1;
    private String searchText1;
    private String columnName2;
    private String logicalCondition2;
    private String searchText2;
    private String reportType;
    private String orderId;
    private String status;
    private String accountNo;
    private String pgRefNum;


    public String payoutExport() {
        sessionUser = (User) sessionMap.get(Constants.USER.getValue());
        List<User> users = userDao.fetchUsersBasedOnPOEnable();

        if (!sessionUser.getUserType().name().equalsIgnoreCase("MERCHANT")) {
            users.stream().forEach(user -> {
                payIdList.put(user.getPayId(), user.getBusinessName());
            });
            // Changes By Pritam Ray
            logger.info("@@@ PayoutNewAction Merchant List After Sorting : {}", payIdList);
            payIdList = SortingUtil.sortMerchantNames(payIdList);
            logger.info("@@@ PayoutNewAction Merchant List Before Sorting : {} " + payIdList);

        } else {
            payIdList.clear();
            HashMap<String, String> map = new LinkedHashMap<String, String>();
            map.put(sessionUser.getPayId(), sessionUser.getBusinessName());
            setPayIdList(map);
        }

        return SUCCESS;
    }

    // To get PG Report data here
    public String pgReportData() {
        sessionUser = (User) sessionMap.get(Constants.USER.getValue());
        logger.info("ipAddress====" + getIpAddress() + "totalAmount====" + getTotalAmount());

        if (acquirer.isEmpty()) {
            acquirer = "ALL";
        }
        if (status.isEmpty()) {
            status = "ALL";
        }
        if (tenantId == null) {
            tenantId = "ALL";
        }

        logger.info("<<<< dateFrom ==========" + dateFrom + " ,  dateTo================ " + dateTo);
        logger.info("<<<< startTime ==========" + startTime + " ,  endTime================ " + endTime);

        String fromDateWithTime = null;
        String toDateWithTime = null;

        if (startTime != null && endTime != null && !startTime.isEmpty() && !endTime.isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            fromDateWithTime = LocalDate.parse(dateFrom, formatter).format(formatter2);
            toDateWithTime = LocalDate.parse(dateTo, formatter).format(formatter2);

            String[] startTimearr = StringUtils.split(startTime, ":");
            if (startTimearr.length == 2) {
                startTime = startTime + ":01";
            }

            String[] endTimearr = StringUtils.split(endTime, ":");
            if (endTimearr.length == 2) {
                endTime = endTime + ":59";
            }

            fromDateWithTime += " " + startTime;
            toDateWithTime += " " + endTime;
        }

        logger.info("Inside PayoutNewAction , execute()");
        sessionUser = (User) sessionMap.get(Constants.USER.getValue());

        if (startTime != null && endTime != null && !startTime.isEmpty() && !endTime.isEmpty()) {
            setDateFrom(fromDateWithTime);
            setDateTo(toDateWithTime);
        } else {
            setDateFrom(DateCreater.toDateTimeformatCreater(dateFrom));
            setDateTo(DateCreater.formDateTimeformatCreater(dateTo));
        }

        try {

            logger.info("Inside PayoutNewAction , merchantPayId = " + merchantEmailId);
            String merchantPayId = merchantEmailId;

            BigInteger bigInt = BigInteger.valueOf(poTxnReport.pgReportSearchCount(transactionId, orderId,
                    customerEmail, customerPhone, paymentType, status, currency, transactionType, mopType, acquirer,
                    merchantPayId, dateFrom, dateTo, sessionUser, start, length, tenantId, ipAddress, totalAmount, rrn,
                    channelName, minAmount, maxAmount, columnName, logicalCondition, searchText, newDespositor,
                    columnName1, logicalCondition1, searchText1, columnName2, logicalCondition2, searchText2));// ,ipAddress,totalAmount));
            logger.info("Inside TransactionSearchActionAdmin , bigInt=========== = " + bigInt);

            setRecordsTotal(bigInt);
            if (getLength() == -1) {
                setLength(getRecordsTotal().intValue());
            }

            aaDataTxn = encoder.encodeTransactionSearchObjNew(poTxnReport.pgReportSearch(transactionId, orderId,
                    customerEmail, customerPhone, paymentType, status, currency, transactionType, mopType, acquirer,
                    merchantPayId, dateFrom, dateTo, sessionUser, start, length, tenantId, ipAddress, totalAmount, rrn,
                    channelName, minAmount, maxAmount, columnName, logicalCondition, searchText, newDespositor,
                    columnName1, logicalCondition1, searchText1, columnName2, logicalCondition2, searchText2));

            aaData = aaDataTxn.stream().sorted((o1, o2) -> {
                String date1 = StringUtils.isBlank(o1.getCreateDate()) ? o1.getDateFrom() : o1.getCreateDate();
                String date2 = StringUtils.isBlank(o2.getCreateDate()) ? o2.getDateFrom() : o2.getCreateDate();
                return date2.compareTo(date1);
            }).collect(Collectors.toList());

            recordsFiltered = recordsTotal;

        } catch (Exception exception) {
            logger.error("Exception", exception);
            return ERROR;
        }
        return SUCCESS;

    }

    // To get Payout Report Data here
    public String poReportData() {
        sessionUser = (User) sessionMap.get(Constants.USER.getValue());

        logger.info(String.format("@@@ PayoutNewAction poReportData ::: merchantPayId = %s, dateFrom = %s, dateTo = %s, startTime = %s, endTime = %s, filter Status: %s, accountNo: %s, pgRefNum: %s, orderId: %s",
                merchantEmailId, dateFrom, dateTo, startTime, endTime, status, accountNo, pgRefNum, orderId));

        String fromDateWithTime = null;
        String toDateWithTime = null;

        boolean areDateAndTimeValid = Stream.of(dateFrom, dateTo, startTime, endTime).allMatch(StringUtils::isNotEmpty);

        if (areDateAndTimeValid) {

            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            fromDateWithTime = LocalDate.parse(dateFrom, inputFormatter).format(outputFormatter) + " " + formatTime(startTime, "01");
            toDateWithTime = LocalDate.parse(dateTo, inputFormatter).format(outputFormatter) + " " + formatTime(endTime, "59");

            setDateFrom(fromDateWithTime);
            setDateTo(toDateWithTime);
        } else if (StringUtils.isNoneEmpty(dateFrom, dateTo)) {
            setDateFrom(DateCreater.toDateTimeformatCreater(dateFrom));
            setDateTo(DateCreater.formDateTimeformatCreater(dateTo));
        }

        String merchantPayId = merchantEmailId;

        // Change By Pritam Ray
        BigInteger bigInt = BigInteger.valueOf(poTxnReport.poReportSearchCount(dateFrom, dateTo, merchantPayId, sessionUser, orderId, status, accountNo, pgRefNum));
        logger.info("@@@ Inside Admin PayoutNewAction poReportSearchCount ::  " + bigInt);

        setRecordsTotal(bigInt);

        if (getLength() == -1) {
            setLength(getRecordsTotal().intValue());
        }

        // Change By Pritam Ray
        aaDataPO = poTxnReport.poReportSearch(dateFrom, dateTo, merchantPayId, start, length, sessionUser, orderId, status, accountNo, pgRefNum);

        aaData = aaDataPO.stream().sorted((o1, o2) -> {
            String date1 = StringUtils.isBlank(o1.getDateFrom()) ? o1.getDateFrom() : o1.getDateFrom();
            String date2 = StringUtils.isBlank(o2.getDateFrom()) ? o2.getDateFrom() : o2.getDateFrom();
            return date2.compareTo(date1);
        }).collect(Collectors.toList());

        recordsFiltered = recordsTotal;
        return SUCCESS;
    }

    private String formatTime(String time, String defaultSecond) {
        String[] timeParts = StringUtils.split(time, ":");
        return (timeParts.length == 2) ? time + ":" + defaultSecond : time;
    }


    public String popgReportData() {
        sessionUser = (User) sessionMap.get(Constants.USER.getValue());
        logger.info("<<<< dateFrom ==========" + dateFrom + " ,  dateTo================ " + dateTo);
        logger.info("<<<< startTime ==========" + startTime + " ,  endTime================ " + endTime);
        try {
            String fromDateWithTime = null;
            String toDateWithTime = null;

            if (startTime != null && endTime != null && !startTime.isEmpty() && !endTime.isEmpty()) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                fromDateWithTime = LocalDate.parse(dateFrom, formatter).format(formatter2);
                toDateWithTime = LocalDate.parse(dateTo, formatter).format(formatter2);

                String[] startTimearr = StringUtils.split(startTime, ":");
                if (startTimearr.length == 2) {
                    startTime = startTime + ":01";
                }

                String[] endTimearr = StringUtils.split(endTime, ":");
                if (endTimearr.length == 2) {
                    endTime = endTime + ":59";
                }

                fromDateWithTime += " " + startTime;
                toDateWithTime += " " + endTime;
            }

            if (startTime != null && endTime != null && !startTime.isEmpty() && !endTime.isEmpty()) {
                setDateFrom(fromDateWithTime);
                setDateTo(toDateWithTime);
            } else {
                setDateFrom(DateCreater.toDateTimeformatCreater(dateFrom));
                setDateTo(DateCreater.formDateTimeformatCreater(dateTo));
            }

            // po report length size here
            String merchantPayId = merchantEmailId;

            BigInteger bigInt = BigInteger
                    .valueOf(poTxnReport.poReportSearchCount(dateFrom, dateTo, merchantPayId, sessionUser, orderId, status, accountNo, pgRefNum));
            logger.info("Inside PayoutNewAction , bigInt=========== = " + bigInt);

            setRecordsTotal(bigInt);
            if (getLength() == -1) {
                setLength(getRecordsTotal().intValue());
            }

            // pg report length size here
            BigInteger bigInt1 = BigInteger.valueOf(poTxnReport.pgReportSearchCount(transactionId, orderId,
                    customerEmail, customerPhone, paymentType, status, currency, transactionType, mopType, acquirer,
                    merchantPayId, dateFrom, dateTo, sessionUser, start, length, tenantId, ipAddress, totalAmount, rrn,
                    channelName, minAmount, maxAmount, columnName, logicalCondition, searchText, newDespositor,
                    columnName1, logicalCondition1, searchText1, columnName2, logicalCondition2, searchText2));// ,ipAddress,totalAmount));
            logger.info("Inside TransactionSearchActionAdmin , bigInt=========== = " + bigInt1);

            BigInteger finalLength = bigInt1.add(getRecordsTotal());
            setRecordsTotal(finalLength);
            if (getLength() == -1) {
                setLength(getRecordsTotal().intValue());
            }

            // Change By Pritam Ray
            // po record data here
            aaDataPO = poTxnReport.poReportSearch(dateFrom, dateTo, merchantPayId, start, length, sessionUser, orderId, status, accountNo, pgRefNum);

//			aaDataPO = aaDataPO.stream().sorted(new Comparator<POReportDTO>() {
//
//				@Override
//				public int compare(POReportDTO o1, POReportDTO o2) {
//					String date1 = StringUtils.isBlank(o1.getDateFrom()) ? o1.getDateFrom() : o1.getDateFrom();
//					String date2 = StringUtils.isBlank(o2.getDateFrom()) ? o2.getDateFrom() : o2.getDateFrom();
//					return date2.compareTo(date1);
//				}
//			}).collect(Collectors.toList());
            // recordsFiltered = recordsTotal;

            // pg record data here
            aaDataTxn = encoder.encodeTransactionSearchObjNew(poTxnReport.pgReportSearch(transactionId, orderId,
                    customerEmail, customerPhone, paymentType, status, currency, transactionType, mopType, acquirer,
                    merchantPayId, dateFrom, dateTo, sessionUser, start, length, tenantId, ipAddress, totalAmount, rrn,
                    channelName, minAmount, maxAmount, columnName, logicalCondition, searchText, newDespositor,
                    columnName1, logicalCondition1, searchText1, columnName2, logicalCondition2, searchText2));

//			aaDataTxn = aaDataTxn.stream().sorted(new Comparator<TransactionSearchNew>() {
//
//				@Override
//				public int compare(TransactionSearchNew o1, TransactionSearchNew o2) {
//					String date1 = StringUtils.isBlank(o1.getCreateDate()) ? o1.getDateFrom() : o1.getCreateDate();
//					String date2 = StringUtils.isBlank(o2.getCreateDate()) ? o2.getDateFrom() : o2.getCreateDate();
//					return date2.compareTo(date1);
//				}
//			}).collect(Collectors.toList());

            System.out.println("AADATAPO : " + new Gson().toJson(aaDataPO));
            System.out.println("AADATATXN : " + new Gson().toJson(aaDataTxn));
            aaDataPO.stream().forEach(poReportData -> {

                PGPOReportDTO pgpoReportDTO = new PGPOReportDTO();
                pgpoReportDTO.setAcquireType(poReportData.getAcquireType());
                pgpoReportDTO.setCurrencyCode(poReportData.getCurrencyCode());
                pgpoReportDTO.setCustomerName(poReportData.getPayerName());
                pgpoReportDTO.setCustomerphone(poReportData.getCustomerPhone());
                pgpoReportDTO.setDateFrom(poReportData.getDateFrom());
                pgpoReportDTO.setIpaddress(poReportData.getIpaddress());
                pgpoReportDTO.setMerchantName(poReportData.getMerchantName());
                pgpoReportDTO.setMopType(poReportData.getMopType());
                pgpoReportDTO.setOrderId(poReportData.getOrderId());
                pgpoReportDTO.setPayId(poReportData.getPayId());
                pgpoReportDTO.setPayInAmount("0.0");
                pgpoReportDTO.setPayOutAmount(poReportData.getAmount());
                pgpoReportDTO.setPgRefNum(poReportData.getPgRefNum());
                pgpoReportDTO.setStatus(poReportData.getStatus());
                pgpoReportDTO.setTxnType(poReportData.getTxnType());
                aaDataPGPO.add(pgpoReportDTO);

            });

            // aaData.add(aaDataPO);
            aaDataTxn.stream().forEach(transactSearchNew -> {

                PGPOReportDTO pgpoReportDTO = new PGPOReportDTO();
                pgpoReportDTO.setAcquireType(transactSearchNew.getAcquirerType());
                pgpoReportDTO.setCurrencyCode("NA");
                pgpoReportDTO.setCustomerName(transactSearchNew.getCustomerName());
                pgpoReportDTO.setCustomerphone(transactSearchNew.getCustomerPhone());
                pgpoReportDTO.setDateFrom(transactSearchNew.getDateFrom());
                pgpoReportDTO.setIpaddress(transactSearchNew.getIpaddress());
                pgpoReportDTO.setMerchantName(transactSearchNew.getBusinessName());
                pgpoReportDTO.setMopType(transactSearchNew.getMopType());
                pgpoReportDTO.setOrderId(transactSearchNew.getOrderId());
                pgpoReportDTO.setPayId(transactSearchNew.getPayId());
                pgpoReportDTO.setPayOutAmount("0.0");
                pgpoReportDTO.setPayInAmount(transactSearchNew.getAmount());
                pgpoReportDTO.setPgRefNum(transactSearchNew.getPgRefNum());
                pgpoReportDTO.setStatus(transactSearchNew.getStatus());
                pgpoReportDTO.setTxnType(transactSearchNew.getTxnType());
                aaDataPGPO.add(pgpoReportDTO);

            });

            aaData = aaDataPGPO;
            System.out.println(new Gson().toJson(aaData));
            recordsFiltered = recordsTotal.add(recordsFiltered);
            logger.info("Final Length of POPG Data :  " + recordsFiltered);
        } catch (Exception e) {
            logger.info("Exception POPG Report : " + e);
        }
        return SUCCESS;
    }

    public String reset2FA() {
        sessionUser = (User) sessionMap.get(Constants.USER.getValue());
        return SUCCESS;
    }

    public String acquirerWallet() {
        return SUCCESS;
    }

    public Map<String, String> getPayIdList() {
        return payIdList;
    }

    public void setPayIdList(Map<String, String> payIdList) {
        this.payIdList = payIdList;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getAcquirer() {
        return acquirer;
    }

    public void setAcquirer(String acquirer) {
        this.acquirer = acquirer;
    }

    public List<Object> getAaData() {
        return aaData;
    }

    public void setAaData(List<Object> aaData) {
        this.aaData = aaData;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getMerchantEmailId() {
        return merchantEmailId;
    }

    public void setMerchantEmailId(String merchantEmailId) {
        this.merchantEmailId = merchantEmailId;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(String dateFrom) {
        this.dateFrom = dateFrom;
    }

    public String getDateTo() {
        return dateTo;
    }

    public void setDateTo(String dateTo) {
        this.dateTo = dateTo;
    }

    public int getDraw() {
        return draw;
    }

    public void setDraw(int draw) {
        this.draw = draw;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getMopType() {
        return mopType;
    }

    public void setMopType(String mopType) {
        this.mopType = mopType;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getRrn() {
        return rrn;
    }

    public void setRrn(String rrn) {
        this.rrn = rrn;
    }

    public BigInteger getRecordsTotal() {
        return recordsTotal;
    }

    public void setRecordsTotal(BigInteger recordsTotal) {
        this.recordsTotal = recordsTotal;
    }

    public BigInteger getRecordsFiltered() {
        return recordsFiltered;
    }

    public void setRecordsFiltered(BigInteger recordsFiltered) {
        this.recordsFiltered = recordsFiltered;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(String minAmount) {
        this.minAmount = minAmount;
    }

    public String getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(String maxAmount) {
        this.maxAmount = maxAmount;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getLogicalCondition() {
        return logicalCondition;
    }

    public void setLogicalCondition(String logicalCondition) {
        this.logicalCondition = logicalCondition;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public String getColumnName1() {
        return columnName1;
    }

    public void setColumnName1(String columnName1) {
        this.columnName1 = columnName1;
    }

    public String getLogicalCondition1() {
        return logicalCondition1;
    }

    public void setLogicalCondition1(String logicalCondition1) {
        this.logicalCondition1 = logicalCondition1;
    }

    public String getSearchText1() {
        return searchText1;
    }

    public void setSearchText1(String searchText1) {
        this.searchText1 = searchText1;
    }

    public String getColumnName2() {
        return columnName2;
    }

    public void setColumnName2(String columnName2) {
        this.columnName2 = columnName2;
    }

    public String getLogicalCondition2() {
        return logicalCondition2;
    }

    public void setLogicalCondition2(String logicalCondition2) {
        this.logicalCondition2 = logicalCondition2;
    }

    public String getSearchText2() {
        return searchText2;
    }

    public void setSearchText2(String searchText2) {
        this.searchText2 = searchText2;
    }

    public String getNewDespositor() {
        return newDespositor;
    }

    public void setNewDespositor(String newDespositor) {
        this.newDespositor = newDespositor;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public List<POReportDTO> getAaDataPO() {
        return aaDataPO;
    }

    public void setAaDataPO(List<POReportDTO> aaDataPO) {
        this.aaDataPO = aaDataPO;
    }

    public List<TransactionSearchNew> getAaDataTxn() {
        return aaDataTxn;
    }

    public void setAaDataTxn(List<TransactionSearchNew> aaDataTxn) {
        this.aaDataTxn = aaDataTxn;
    }

    public List<Object> getAaDataPGPO() {
        return aaDataPGPO;
    }

    public void setAaDataPGPO(List<Object> aaDataPGPO) {
        this.aaDataPGPO = aaDataPGPO;
    }


    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getPgRefNum() {
        return pgRefNum;
    }

    public void setPgRefNum(String pgRefNum) {
        this.pgRefNum = pgRefNum;
    }
}