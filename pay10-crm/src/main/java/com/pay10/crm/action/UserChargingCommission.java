package com.pay10.crm.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.Merchants;
import com.pay10.commons.user.MultCurrencyCode;
import com.pay10.commons.user.MultCurrencyCodeDao;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserCommision;
import com.pay10.commons.user.UserCommissionDao;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.user.resellercommisiondao;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.MopType;
import com.pay10.commons.util.PaymentType;

public class UserChargingCommission extends AbstractSecureAction {

    private static final long serialVersionUID = -6879974923614009981L;
    private static Logger logger = LoggerFactory.getLogger(UserChargingCommission.class.getName());
    public List<Merchants> listMerchant = new ArrayList<Merchants>();
    Map<String, List<UserCommision>> UsercommisionMap = new HashMap<String, List<UserCommision>>();
    Map<String, String> currencyMapList = new HashMap<String, String>();
    @Autowired
    private UserCommissionDao commision;
    @Autowired
    private UserDao userDao;
    private List<String> merchantarreList = new ArrayList<String>();
    private List<String> merchantarrename = new ArrayList<String>();
    private List<String> maArreList = new ArrayList<String>();
    private List<String> maArrename = new ArrayList<String>();
    private List<String> agentarreList = new ArrayList<String>();
    private List<String> agentarrename = new ArrayList<String>();
    private List<String> merchantbusname = new ArrayList<String>();
    private List<String> merchantid = new ArrayList<String>();
    private List<String> maid = new ArrayList<String>();
    private List<String> mabusname = new ArrayList<String>();
    private List<String> agentid = new ArrayList<String>();
    private List<String> agentbusname = new ArrayList<String>();
    private String smaPayId;
    private String maPayId;
    private String agentPayId;
    private String merchantPayId;
    private List<Merchants> listSma = new ArrayList<Merchants>();
    private List<User> listMa = new ArrayList<User>();
    private List<User> listAgent = new ArrayList<User>();
    private List<User> merchantlist = new ArrayList<User>();
    private List<UserCommision> data = new ArrayList<UserCommision>();
    private String payId;
    private List<String> currencyCode = new ArrayList<String>();
    private List<String> currencyName = new ArrayList<String>();
    private List<String> currencyCodearreList = new ArrayList<String>();
    private List<String> currencyarrename = new ArrayList<String>();
    @Autowired
    private UserCommissionDao commissiondao;
    @Autowired
    private MultCurrencyCodeDao multCurrencyCodeDao;
    private String currencyData;

    @Override
    @SuppressWarnings("unchecked")
    public String execute() {
        Map<String, String> currencyMap = new HashMap<String, String>();
        User user = (User) sessionMap.get(Constants.USER.getValue());

//        setListSma(userDao.getSMAList());
//        setListMa(userDao.getMaById(smaPayId));
//        setListAgent(userDao.getAgentbyId(maPayId));
        displayList();
		getMaList();
		getAgentList();
		setMerchantlist(userDao.getAgentsById(agentPayId));
		List<String> getCurrency = commissiondao.findBydiffId(smaPayId, maPayId, agentPayId, merchantPayId);
        for (String currency : getCurrency) {
            String cName = multCurrencyCodeDao.getCurrencyNamebyCode(currency);
            currencyMap.put(currency, cName);
        }

        currencyMapList.putAll(currencyMap);
        logger.info("currencyMapList....={}", currencyMapList);

        //logger.info("get merchants list according to reseller..." + merchantpayId);

        logger.info("get list..." + getMerchantlist());
        logger.info("get smaPayId...={},get maPayId...={},get agentPayId...={},getMerchantId.....={}", smaPayId, maPayId, agentPayId, merchantPayId);
        try {
            data = commision.getchargingdetail(smaPayId, maPayId, agentPayId, merchantPayId, currencyData);
            for (PaymentType paymentType : PaymentType.values()) {

                List<UserCommision> resellercommisiondata = new ArrayList<UserCommision>();
                String paymentName = paymentType.getCode();
                logger.info("Payment Type for user commission...={}", paymentName);
                for (UserCommision cDetail : data) {
                    logger.info("cDetail..={}", cDetail.toString());

                    if (cDetail.getTransactiontype().equals(paymentName)) {
                        cDetail.setMop(MopType.getmopName(cDetail.getMop()));
                        cDetail.setTransactiontype(PaymentType.getpaymentName(cDetail.getTransactiontype()));
                        resellercommisiondata.add(cDetail);
                    }
                }
                if (resellercommisiondata.size() != 0) {
                    paymentName = PaymentType.getpaymentName(paymentName);
                    UsercommisionMap.put(paymentName, resellercommisiondata);

                    logger.info("UserCommission.................." + UsercommisionMap.toString());
                }
            }
        } catch (Exception exception) {
            logger.error("Exception", exception);
            addActionMessage(ErrorType.LOCAL_TAX_RATES_NOT_AVAILABLE.getResponseMessage());
        }
        return INPUT;
    }

    // To display page without using token
    @SuppressWarnings("unchecked")
    public String displayList() {
        User user = (User) sessionMap.get(Constants.USER.getValue());
        if (StringUtils.equalsIgnoreCase(user.getUserType().name(), "ADMIN")) {
            setListSma(userDao.getSMAList());
            // setListReseller(userDao.findByResellerId(user.getResellerId()));
        } else {
            Merchants merchants = new Merchants();
            if (StringUtils.equalsAnyIgnoreCase(user.getUserGroup().getGroup(), "SMA")) {
                merchants.setBusinessName(user.getBusinessName());
                merchants.setEmailId(user.getEmailId());
                merchants.setPayId(user.getPayId());
            } else if (StringUtils.equalsAnyIgnoreCase(user.getUserGroup().getGroup(), "MA")) {
                User sma = userDao.findByPayId(user.getSmaId());
                merchants.setBusinessName(sma.getBusinessName());
                merchants.setEmailId(sma.getEmailId());
                merchants.setPayId(sma.getPayId());
            } else if (StringUtils.equalsAnyIgnoreCase(user.getUserGroup().getGroup(), "Agent")) {
                User ma = userDao.findByPayId(user.getMaId());
                User sma = userDao.findByPayId(ma.getSmaId());
                merchants.setBusinessName(sma.getBusinessName());
                merchants.setEmailId(sma.getEmailId());
                merchants.setPayId(sma.getPayId());
            }
            getListSma().add(merchants);
            //setListMa(userDao.getMAList());
            //setListAgent(userDao.getAgentList());
            //setMerchantlist(userDao.getAgentsById(agentPayId));
            //setMerchantlist(userDao.getAgentsById(agentPayId));
        }
        return INPUT;
    }

    public String getMaList() {

        try {
            User user = (User) sessionMap.get(Constants.USER.getValue());
            List<User> malist = new ArrayList<>();
            maid.clear();
            mabusname.clear();
            logger.info("User Group : {}", user.getUserGroup().getGroup());
            if (StringUtils.equalsAnyIgnoreCase(user.getUserGroup().getGroup(), "MA", "Agent")) {
                User ma = userDao.findByPayId(user.getMaId());
                malist.add(ma);
                logger.info("MA: {}", ma);
            } else {
                malist = userDao.getMaById(smaPayId);
            }
            for (User merchantdat : malist) {
                String mapayid = merchantdat.getPayId();

                String maname = merchantdat.getBusinessName();

                maid.add(mapayid);
                mabusname.add(maname);
				getListMa().add(merchantdat);
            }
            setMaArreList(maid);
            setMaArrename(mabusname);
            return SUCCESS;
        } catch (Exception exception) {
            logger.error("Exception ", exception);
            return ERROR;
        }
    }

    public String getAgentList() {

        try {
            User user = (User) sessionMap.get(Constants.USER.getValue());
            List<User> agnetlist = new ArrayList<>();
            agentid.clear();
            agentbusname.clear();
            if (StringUtils.equalsAnyIgnoreCase(user.getUserGroup().getGroup(), "Agent")) {
                logger.info("Agent : {}", user.getPayId());
                agnetlist.add(user);
            } else {
                agnetlist = userDao.getAgentbyId(maPayId);
            }
            for (User merchantdat : agnetlist) {
                String agentpayid = merchantdat.getPayId();

                String agentname = merchantdat.getBusinessName();

                agentid.add(agentpayid);
                agentbusname.add(agentname);
				getListAgent().add(merchantdat);
            }
            setAgentarreList(agentid);
            setAgentarrename(agentbusname);
            return SUCCESS;
        } catch (Exception exception) {
            logger.error("Exception ", exception);
            return ERROR;
        }
    }

    public String getmerchantlist() {

        try {
            List<User> merchantlist = userDao.getAgentsById(agentPayId);

            for (User merchantdat : merchantlist) {
                String merchantpayid = merchantdat.getPayId();

                String merchantname = merchantdat.getBusinessName();

                merchantid.add(merchantpayid);
                merchantbusname.add(merchantname);

            }
            setMerchantarreList(merchantid);
            setMerchantarrename(merchantbusname);
            return SUCCESS;
        } catch (Exception exception) {
            logger.error("Exception ", exception);
            return ERROR;
        }
    }

    public String getCurrencyList() {

        try {
            List<String> currencylist = commissiondao.findBydiffId(smaPayId, maPayId, agentPayId, merchantPayId);
            currencyCode.clear();
            currencyName.clear();
            for (String currencydata : currencylist) {

                String cName = multCurrencyCodeDao.getCurrencyNamebyCode(currencydata);

                currencyCode.add(currencydata);
                currencyName.add(cName);

            }
            setCurrencyCodearreList(currencyCode);
            setCurrencyarrename(currencyName);

            logger.info("currencyList :::::::::::::={},currencyCode...={}", getCurrencyarrename(), getCurrencyCodearreList());
            return SUCCESS;
        } catch (Exception exception) {
            logger.error("Exception ", exception);
            return ERROR;
        }
    }

    public List<Merchants> getListMerchant() {
        return listMerchant;
    }

    public void setListMerchant(List<Merchants> listMerchant) {
        this.listMerchant = listMerchant;
    }

    public List<String> getMerchantarreList() {
        return merchantarreList;
    }

    public void setMerchantarreList(List<String> merchantarreList) {
        this.merchantarreList = merchantarreList;
    }

    public List<String> getMerchantarrename() {
        return merchantarrename;
    }

    public void setMerchantarrename(List<String> merchantarrename) {
        this.merchantarrename = merchantarrename;
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

    public Map<String, List<UserCommision>> getUsercommisionMap() {
        return UsercommisionMap;
    }

    public void setUsercommisionMap(Map<String, List<UserCommision>> usercommisionMap) {
        UsercommisionMap = usercommisionMap;
    }

    public List<User> getMerchantlist() {
        return merchantlist;
    }

    public void setMerchantlist(List<User> merchantlist) {
        this.merchantlist = merchantlist;
    }

    public List<Merchants> getListSma() {
        return listSma;
    }

    public void setListSma(List<Merchants> listSma) {
        this.listSma = listSma;
    }

    public List<User> getListMa() {
        return listMa;
    }

    public void setListMa(List<User> listMa) {
        this.listMa = listMa;
    }

    public List<String> getCurrencyCodearreList() {
        return currencyCodearreList;
    }

    public void setCurrencyCodearreList(List<String> currencyCodearreList) {
        this.currencyCodearreList = currencyCodearreList;
    }

    public String getCurrencyData() {
        return currencyData;
    }

    public void setCurrencyData(String currencyData) {
        this.currencyData = currencyData;
    }

    public List<String> getCurrencyarrename() {
        return currencyarrename;
    }

    public void setCurrencyarrename(List<String> currencyarrename) {
        this.currencyarrename = currencyarrename;
    }

    public List<String> getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(List<String> currencyCode) {
        this.currencyCode = currencyCode;
    }

    public List<String> getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(List<String> currencyName) {
        this.currencyName = currencyName;
    }

    public List<User> getListAgent() {
        return listAgent;
    }

    public void setListAgent(List<User> listAgent) {
        this.listAgent = listAgent;
    }

    public String getPayId() {
        return payId;
    }

    public void setPayId(String payId) {
        this.payId = payId;
    }

    public List<String> getMaArreList() {
        return maArreList;
    }

    public void setMaArreList(List<String> maArreList) {
        this.maArreList = maArreList;
    }

    public List<String> getMaArrename() {
        return maArrename;
    }

    public void setMaArrename(List<String> maArrename) {
        this.maArrename = maArrename;
    }

    public List<String> getAgentarreList() {
        return agentarreList;
    }

    public void setAgentarreList(List<String> agentarreList) {
        this.agentarreList = agentarreList;
    }

    public List<String> getAgentarrename() {
        return agentarrename;
    }

    public void setAgentarrename(List<String> agentarrename) {
        this.agentarrename = agentarrename;
    }

    public List<String> getMerchantbusname() {
        return merchantbusname;
    }

    public void setMerchantbusname(List<String> merchantbusname) {
        this.merchantbusname = merchantbusname;
    }

    public List<String> getMerchantid() {
        return merchantid;
    }

    public void setMerchantid(List<String> merchantid) {
        this.merchantid = merchantid;
    }

    public List<String> getMaid() {
        return maid;
    }

    public void setMaid(List<String> maid) {
        this.maid = maid;
    }

    public List<String> getMabusname() {
        return mabusname;
    }

    public void setMabusname(List<String> mabusname) {
        this.mabusname = mabusname;
    }

    public List<String> getAgentid() {
        return agentid;
    }

    public void setAgentid(List<String> agentid) {
        this.agentid = agentid;
    }

    public List<String> getAgentbusname() {
        return agentbusname;
    }

    public void setAgentbusname(List<String> agentbusname) {
        this.agentbusname = agentbusname;
    }

    public Map<String, String> getCurrencyMapList() {
        return currencyMapList;
    }

    public void setCurrencyMapList(Map<String, String> currencyMapList) {
        this.currencyMapList = currencyMapList;
    }
}
