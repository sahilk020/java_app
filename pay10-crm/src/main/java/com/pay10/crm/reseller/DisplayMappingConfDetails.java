package com.pay10.crm.reseller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.pay10.commons.user.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.util.Constants;
import com.pay10.commons.util.MopType;
import com.pay10.commons.util.PaymentType;
import com.pay10.crm.action.AbstractSecureAction;

public class DisplayMappingConfDetails extends AbstractSecureAction {

    /**
     * Sweety
     */
    private static final long serialVersionUID = -1807158084920948689L;
    private static final Logger logger = LoggerFactory.getLogger(DisplayMappingConfDetails.class.getName());
    @Autowired
    UserDao userDao;
    List<Merchants> merchantMapping = new ArrayList<>();
    private List<Merchants> listSMA = new ArrayList<Merchants>();
    private List<Merchants> listMA = new ArrayList<Merchants>();
    private List<Merchants> listAgent = new ArrayList<Merchants>();
    private List<Merchants> listMerchant = new ArrayList<Merchants>();
    private List<Merchants> aaData = new ArrayList<Merchants>();
    private List<String> userKey = new ArrayList<String>();
    private List<String> userValue = new ArrayList<String>();
    private List<String> userKeyList = new ArrayList<String>();
    private List<String> userValueList = new ArrayList<String>();
    private List<String> userValueMap = new ArrayList<String>();
    private String smaVal;
    private String maVal;
    private String agentVal;
    private String merchantVal;
    private String userType;
    private String smaPayId;
    private String maPayId;
    private String agentPayId;
    private String merchantPayId;
    private UserCommision commision = new UserCommision();
    private List<String> verify = new ArrayList<String>();
    private User sessionUser = null;
    private String responseMessage;
    @Autowired
    private TdrSettingDao tdrSettingDao;

    @Autowired
    private UserCommissionDao commissiondao;
    private List<Merchants> listReseller = new ArrayList<>();

    public String execute() {

        sessionUser = (User) sessionMap.get(Constants.USER.getValue());

        User user = (User) sessionMap.get(Constants.USER.getValue());
        setListSMA(userDao.getSMAList());
        setListMA(userDao.getMAList());
        setListAgent(userDao.getAgentList());
        setListMerchant(userDao.getActiveMerchant(sessionMap.get(Constants.SEGMENT.getValue()).toString(),
                sessionUser.getRole().getId()));
        //userKey.add("MA");
        //userKey.add("Agent");
        //userKey.add("Merchant");
        //	setUserKeyList(userKey);
        //setUserValueList(userKey);
        List<String> list = new ArrayList<String>();
        list.add(0, "Select UserType");
        //userKey.add("MA");
        //userKey.add("Agent");
        //userKey.add("MERCHANT");
        //list.addAll(userKey);
        setUserValueMap(list);

        Merchants reseller = new Merchants();
        List<Merchants> resellers = new ArrayList<>();
        if (StringUtils.equalsIgnoreCase(user.getUserGroup().getGroup(), "SMA")) {
            reseller.setPayId(user.getPayId());
            reseller.setBusinessName(user.getBusinessName());
            reseller.setEmailId(user.getEmailId());
            resellers.add(reseller);
            setListReseller(resellers);
            logger.info("resellerList....={}", getListReseller().toString());
        }
        if (StringUtils.equalsIgnoreCase(user.getUserGroup().getGroup(), "MA")) {
            reseller.setPayId(user.getPayId());
            reseller.setBusinessName(user.getBusinessName());
            reseller.setEmailId(user.getEmailId());
            resellers.add(reseller);
            setListReseller(resellers);
            logger.info("resellerList....={}", getListReseller().toString());
        }
        if (StringUtils.equalsIgnoreCase(user.getUserGroup().getGroup(), "Agent")) {
            reseller.setPayId(user.getPayId());
            reseller.setBusinessName(user.getBusinessName());
            reseller.setEmailId(user.getEmailId());
            resellers.add(reseller);
            setListReseller(resellers);
            logger.info("resellerList....={}", getListReseller().toString());
        } else {
            if (userType != null) {
                List<Merchants> getData = userDao.getResellersbyUserType(userType).stream().map((userOject) -> {
                    Merchants merData = new Merchants();
                    merData.setPayId(userOject.getPayId());
                    merData.setBusinessName(userOject.getBusinessName());
                    merData.setEmailId(userOject.getEmailId());
                    return merData;
                }).collect(Collectors.toList());
                setListReseller(getData);
            }

        }

        return INPUT;
    }

    public String saveDetails() {
        logger.info("SMA PayId : {}", smaPayId);
        logger.info("MA PayId : {}", maPayId);
        logger.info("Agent PayId : {}", agentPayId);
        logger.info("Merchant PayId : {}", merchantPayId);
        if (StringUtils.isNotBlank(smaPayId) && StringUtils.isNotBlank(maPayId) && StringUtils.isNotBlank(agentPayId) && StringUtils.isNotBlank(merchantPayId)) {
            if (StringUtils.isNotBlank(maPayId)) {
                User user = userDao.findByPayId(maPayId);
                if (!(StringUtils.isBlank(user.getSmaId()) || user.getSmaId().equals(smaPayId))) {
                    setResponseMessage("MA already mapped with another SMA");
                    return SUCCESS;
                }
                user.setSmaId(smaPayId);
                userDao.saveandUpdate(user);
            }

            if (StringUtils.isNotBlank(agentPayId)) {
                User user = userDao.findByPayId(agentPayId);
                if (!(StringUtils.isBlank(user.getMaId()) || user.getMaId().equals(maPayId))) {
                    setResponseMessage("Agent already mapped with another Agent");
                    return SUCCESS;
                }
                user.setMaId(maPayId);
                userDao.saveandUpdate(user);
            }
//
//		if (StringUtils.isNotBlank(merchantPayId)) {
//			User user = userDao.findByPayId(merchantPayId);
//			user.setAgentId(agentPayId);
//			userDao.saveandUpdate(user);
//		}
            User user = userDao.findByPayId(merchantPayId);
            if (!(StringUtils.isBlank(user.getAgentId()))) {
                setResponseMessage("Merchant Already Mapped");
                return SUCCESS;
            }
            user.setSmaId(smaPayId);
            user.setMaId(maPayId);
            user.setAgentId(agentPayId);
            userDao.saveandUpdate(user);

            List<Object[]> tdrDetails = tdrSettingDao.findDistinctMopDetails(merchantPayId);
            logger.info("mop type that saved in commission table={}", tdrDetails.toString());
            Map<String, List<Object[]>> mopByPaymentType = tdrDetails.stream()
                    .collect(Collectors.groupingBy(doc -> (String) doc[1]));
            logger.info("mopByPaymentType....={}", mopByPaymentType.toString());

            commision.setSmaPayId(smaPayId);
            commision.setMaPayId(maPayId);
            commision.setAgentPayId(agentPayId);
            commision.setMerchantPayId(merchantPayId);

            commision.setSma_commission_amount("00.00");
            commision.setSma_commission_percent("00.00");

            commision.setMa_commission_amount("00.00");
            commision.setMa_commission_percent("00.00");

            commision.setAgent_commission_amount("00.00");
            commision.setAgent_commission_percent("00.00");

            mopByPaymentType.entrySet().forEach(entry -> {
                String paymentType = PaymentType.getInstanceUsingStringValue(entry.getKey()).getCode();
                entry.getValue().forEach(mopTypeDetails -> {
                    String mopType = MopType.getInstanceUsingStringValue1((String) mopTypeDetails[0]).getCode();
                    String currency = (String) mopTypeDetails[2];
                    verify = commissiondao.mopandpayment(smaPayId, maPayId, agentPayId, merchantPayId, paymentType,
                            mopType);
                    if (verify.size() == 0) {
                        commision.setMop(mopType);
                        commision.setTransactiontype(paymentType);
                        commision.setCurrency(currency);
                        commissiondao.saveandUpdate(commision);
                    }
                });
            });
            setResponseMessage("Mapped Successfully");
            return SUCCESS;
        } else {
            return ERROR;
        }
    }

    public String getMapping() {
        logger.info("Get Merchant Mapping: {}", merchantPayId);
        User user = userDao.findByPayId(merchantPayId);
        if (StringUtils.equalsIgnoreCase(user.getUserType().name(), UserType.Agent.name())) {
            List<User> merchants = userDao.getMerchantByAgentId(merchantPayId);
            getMerchantMapping().addAll(merchants.stream().map(u -> {
                Merchants map = new Merchants();
                map.setPayId(u.getPayId());
                map.setResellerId(u.getAgentId());
                map.setBusinessName(u.getBusinessName());
                map.setUserType(u.getUserType().name());
                return map;
            }).collect(Collectors.toList()));

            Merchants agent = new Merchants();
            agent.setPayId(user.getPayId());
            agent.setResellerId(user.getMaId());
            agent.setBusinessName(user.getBusinessName());
            agent.setUserType(user.getUserType().name());
            merchantMapping.add(agent);

            User maUser = userDao.findByPayId(user.getMaId());
            if(maUser!=null) {
                Merchants ma = new Merchants();
                ma.setPayId(maUser.getPayId());
                ma.setResellerId(maUser.getSmaId());
                ma.setBusinessName(maUser.getBusinessName());
                ma.setUserType(maUser.getUserType().name());
                merchantMapping.add(ma);

                User smaUser = userDao.findByPayId(maUser.getSmaId());

                Merchants sma = new Merchants();
                sma.setPayId(smaUser.getPayId());
                sma.setResellerId("");
                sma.setBusinessName(smaUser.getBusinessName());
                sma.setUserType(smaUser.getUserType().name());
                merchantMapping.add(sma);
            }
        } else if (StringUtils.equalsIgnoreCase(user.getUserType().name(), UserType.MA.name())) {
            List<User> agents = userDao.getAgentbyId(merchantPayId);

            getMerchantMapping().addAll(agents.stream().map(u -> {
                Merchants map = new Merchants();
                map.setPayId(u.getPayId());
                map.setResellerId(u.getMaId());
                map.setBusinessName(u.getBusinessName());
                map.setUserType(u.getUserType().name());
                return map;
            }).collect(Collectors.toList()));

            agents.forEach(agent -> {
                List<User> merchants = userDao.getMerchantByAgentId(agent.getPayId());
                getMerchantMapping().addAll(merchants.stream().map(u -> {
                            Merchants map = new Merchants();
                            map.setPayId(u.getPayId());
                            map.setResellerId(u.getAgentId());
                            map.setBusinessName(u.getBusinessName());
                            map.setUserType(u.getUserType().name());
                            return map;
                        }
                ).collect(Collectors.toList()));
            });


            Merchants ma = new Merchants();
            ma.setPayId(user.getPayId());
            ma.setResellerId(user.getSmaId());
            ma.setBusinessName(user.getBusinessName());
            ma.setUserType(user.getUserType().name());
            merchantMapping.add(ma);

            User smaUser = userDao.findByPayId(user.getSmaId());
            if(smaUser!=null) {
                Merchants sma = new Merchants();
                sma.setPayId(smaUser.getPayId());
                sma.setResellerId("");
                sma.setBusinessName(smaUser.getBusinessName());
                sma.setUserType(smaUser.getUserType().name());
                merchantMapping.add(sma);
            }
        } else if (StringUtils.equalsIgnoreCase(user.getUserType().name(), UserType.SMA.name())) {
            List<User> maList = userDao.getMaById(merchantPayId);

            getMerchantMapping().addAll(maList.stream().map(u -> {
                Merchants map = new Merchants();
                map.setPayId(u.getPayId());
                map.setResellerId(u.getSmaId());
                map.setBusinessName(u.getBusinessName());
                map.setUserType(u.getUserType().name());
                return map;
            }).collect(Collectors.toList()));

            maList.forEach(ma -> {
                List<User> agentList = userDao.getAgentbyId(ma.getPayId());
                getMerchantMapping().addAll(agentList.stream().map(u -> {
                            Merchants map = new Merchants();
                            map.setPayId(u.getPayId());
                            map.setResellerId(u.getMaId());
                            map.setBusinessName(u.getBusinessName());
                            map.setUserType(u.getUserType().name());
                            return map;
                        }
                ).collect(Collectors.toList()));

                agentList.forEach(agents -> {
                    List<User> merchantList = userDao.getMerchantByAgentId(agents.getPayId());
                    getMerchantMapping().addAll(merchantList.stream().map(u -> {
                                Merchants map = new Merchants();
                                map.setPayId(u.getPayId());
                                map.setResellerId(u.getAgentId());
                                map.setBusinessName(u.getBusinessName());
                                map.setUserType(u.getUserType().name());
                                return map;
                            }
                    ).collect(Collectors.toList()));
                });
            });



            Merchants sma = new Merchants();
            sma.setPayId(user.getPayId());
            sma.setResellerId("");
            sma.setBusinessName(user.getBusinessName());
            sma.setUserType(user.getUserType().name());
            merchantMapping.add(sma);
        }

        return SUCCESS;
    }

    public String getUserTypeList() {
        //logger.info("smaVal...={},maVal...={},agentVal...={},merchantVal...={}",smaVal,maVal,agentVal,merchantVal);
        try {
            if (StringUtils.isNotBlank(smaVal) && !StringUtils.isNotBlank(maVal) && !StringUtils.isNotBlank(agentVal)) {

                userKey.add("MA");
                userKey.add("Agent");
                userKey.add("MERCHANT");

            }
            if (StringUtils.isNotBlank(smaVal) && StringUtils.isNotBlank(maVal) && !StringUtils.isNotBlank(agentVal)) {

                userKey.add("Agent");
                userKey.add("MERCHANT");

            }
            if (StringUtils.isNotBlank(smaVal) && StringUtils.isNotBlank(maVal) && StringUtils.isNotBlank(agentVal) || StringUtils.isNotBlank(merchantVal)) {

                userKey.add("MERCHANT");
                // userValue.add(maname);
            }

            setUserKeyList(userKey);
            setUserValueList(userKey);

            return SUCCESS;
        } catch (Exception exception) {
            logger.error("Exception ", exception);
            return ERROR;
        }
    }


    public String getDetails() {
        logger.info("getSmaval....={},getMaVal....={},getAgetVal....={},merchantVal....={},userType....={}", smaVal,
                maVal, agentVal, merchantVal, userType);

        if (StringUtils.isNotBlank(userType)) {
            setAaData(userDao.getuserMappedConfDetails(smaVal, maVal, agentVal, merchantVal,
                    userType));
        }
        logger.info("getAadata....={}", getAaData().toArray());

        return SUCCESS;
    }

    public List<String> getUserKey() {
        return userKey;
    }

    public void setUserKey(List<String> userKey) {
        this.userKey = userKey;
    }

    public List<String> getUserValue() {
        return userValue;
    }

    public void setUserValue(List<String> userValue) {
        this.userValue = userValue;
    }

    public List<String> getUserKeyList() {
        return userKeyList;
    }

    public void setUserKeyList(List<String> userKeyList) {
        this.userKeyList = userKeyList;
    }

    public List<String> getUserValueList() {
        return userValueList;
    }

    public void setUserValueList(List<String> userValueList) {
        this.userValueList = userValueList;
    }

    public List<Merchants> getListSMA() {
        return listSMA;
    }

    public void setListSMA(List<Merchants> listSMA) {
        this.listSMA = listSMA;
    }

    public List<Merchants> getListMA() {
        return listMA;
    }

    public void setListMA(List<Merchants> listMA) {
        this.listMA = listMA;
    }

    public List<Merchants> getListAgent() {
        return listAgent;
    }

    public void setListAgent(List<Merchants> listAgent) {
        this.listAgent = listAgent;
    }

    public List<Merchants> getListMerchant() {
        return listMerchant;
    }

    public void setListMerchant(List<Merchants> listMerchant) {
        this.listMerchant = listMerchant;
    }

    public List<Merchants> getAaData() {
        return aaData;
    }

    public void setAaData(List<Merchants> aaData) {
        this.aaData = aaData;
    }

    public String getSmaVal() {
        return smaVal;
    }

    public void setSmaVal(String smaVal) {
        this.smaVal = smaVal;
    }

    public String getMaVal() {
        return maVal;
    }

    public void setMaVal(String maVal) {
        this.maVal = maVal;
    }

    public String getAgentVal() {
        return agentVal;
    }

    public void setAgentVal(String agentVal) {
        this.agentVal = agentVal;
    }

    public String getMerchantVal() {
        return merchantVal;
    }

    public void setMerchantVal(String merchantVal) {
        this.merchantVal = merchantVal;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getSmaPayId() {
        return smaPayId;
    }

    public void setSmaPayId(String smaPayId) {
        this.smaPayId = smaPayId;
    }

    public String getMaPayId() {
        return maPayId;
    }

    public void setMaPayId(String maPayId) {
        this.maPayId = maPayId;
    }

    public String getAgentPayId() {
        return agentPayId;
    }

    public void setAgentPayId(String agentPayId) {
        this.agentPayId = agentPayId;
    }

    public String getMerchantPayId() {
        return merchantPayId;
    }

    public void setMerchantPayId(String merchantPayId) {
        this.merchantPayId = merchantPayId;
    }

    public List<String> getUserValueMap() {
        return userValueMap;
    }

    public void setUserValueMap(List<String> userValueMap) {
        this.userValueMap = userValueMap;
    }

    public String getResponseMessage() {
        return this.responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public List<Merchants> getListReseller() {
        return listReseller;
    }

    public void setListReseller(List<Merchants> listReseller) {
        this.listReseller = listReseller;
    }

    public List<Merchants> getMerchantMapping() {
        return this.merchantMapping;
    }

    public void setMerchantMapping(List<Merchants> mapping) {
        this.merchantMapping = mapping;
    }
}
