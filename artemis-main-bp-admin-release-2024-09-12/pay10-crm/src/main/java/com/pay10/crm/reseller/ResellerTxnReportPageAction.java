package com.pay10.crm.reseller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.dao.ReportColumnDetailDao;
import com.pay10.commons.user.CompanyProfileDao;
import com.pay10.commons.user.CompanyProfileUi;
//import com.pay10.commons.user.CurrencyCode;
//import com.pay10.commons.user.CurrencyCodeDao;
import com.pay10.commons.user.Merchants;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.user.UserType;
import com.pay10.commons.user.VpaMaster;
import com.pay10.commons.user.VpaMasterDao;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.Currency;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.StatusTypeUI;
import com.pay10.commons.util.TxnType;
import com.pay10.crm.action.AbstractSecureAction;
import com.pay10.crm.actionBeans.CurrencyMapProvider;

public class ResellerTxnReportPageAction extends AbstractSecureAction {

    /**
     * Sweety
     */
    private static final long serialVersionUID = 4791703674887503488L;
    private static Logger logger = LoggerFactory.getLogger(ResellerTxnReportPageAction.class.getName());

//	@Autowired
//	CurrencyCodeDao currencyCodeDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private VpaMasterDao vpaMasterDao;
    @Autowired
    private CurrencyMapProvider currencyMapProvider;
    private List<CompanyProfileUi> companyList = new ArrayList<CompanyProfileUi>();
    private List<VpaMaster> aMaster = new ArrayList<VpaMaster>();

    private List<Merchants> merchantList = new ArrayList<Merchants>();
    private Map<String, String> currencyMap = new HashMap<String, String>();
    private Map<String, String> pspName = new HashMap<String, String>();

    private User sessionUser = null;

    private List<StatusTypeUI> lst;
    private List<StatusType> statusList;
    private List<TxnType> txnTypelist;
    private HttpServletRequest request;

    private List<Merchants> resellerList = new ArrayList<Merchants>();

    private List<String> merchantarreList = new ArrayList<String>();
    private List<String> merchantarrename = new ArrayList<String>();

    private List<String> merchantbusname = new ArrayList<String>();

    private List<String> merchantid = new ArrayList<String>();

    private String merchantname;
    private String resellerId;
    private String smaId;
    private String maId;
    private String agentId;

    @Autowired
    private ReportColumnDetailDao reportColumnDetailDao;
    private Map<String, String> filtersColumn = new HashMap<String, String>();
    private Map<String, String> channelType = new HashMap<>();

    private List<Merchants> listSMA = new ArrayList<Merchants>();
    private List<Merchants> listMA = new ArrayList<Merchants>();
    private List<Merchants> listAgent = new ArrayList<Merchants>();

    @Override
    @SuppressWarnings("unchecked")
    public String execute() {
        try {

            String actionName = null;

            sessionUser = (User) sessionMap.get(Constants.USER.getValue());
            if (sessionUser.getUserType().equals(UserType.ADMIN)
                    || sessionUser.getUserType().equals(UserType.SUBADMIN) || sessionUser.getUserType().equals(UserType.SUPERADMIN) || sessionUser.getUserType().equals(UserType.SUBSUPERADMIN)) {
                //setMerchantList(userDao.getMerchantActiveList());
                setMerchantList(userDao.getMerchantActiveList(sessionMap.get(Constants.SEGMENT.getValue()).toString(), sessionUser.getRole().getId()));
                //currencyMap = getAllCurrency();
                currencyMap = Currency.getAllCurrency();
                companyList = new CompanyProfileDao().getAllCompanyProfileList();
                setResellerList(userDao.getResellerList());
                setListSMA(userDao.getSMAList());
                setListMA(userDao.getMAList());
                setListAgent(userDao.getAgentList());

                //setMerchantlist(userDao.getResellersbyid(resellerId));
            } else if (sessionUser.getUserType().equals(UserType.RESELLER)) {
                setResellerList(userDao.findByResellerId(sessionUser.getResellerId()));
                setMerchantList(userDao.getResellerMerchantList(sessionUser.getResellerId()));
                //currencyMap = getAllCurrency();
                currencyMap = Currency.getAllCurrency();


            } else if (sessionUser.getUserType().equals(UserType.SMA)) {
                //setListSMA(userDao.findByPayId(smaId));
                setMerchantList(userDao.getMerchantBySMAId(sessionUser.getPayId()).stream().map(u->{
                    Merchants merchant = new Merchants();
                    merchant.setEmailId(u.getEmailId());
                    merchant.setPayId(u.getPayId());
                    merchant.setBusinessName(u.getBusinessName());
                    return  (merchant);
                }).collect(Collectors.toList()));
                //currencyMap = getAllCurrency();
                currencyMap = Currency.getAllCurrency();

                Merchants merchants = new Merchants();
                merchants.setBusinessName(sessionUser.getBusinessName());
                merchants.setEmailId(sessionUser.getEmailId());
                merchants.setPayId(sessionUser.getPayId());
                getListSMA().add(merchants);
            } else if (sessionUser.getUserType().equals(UserType.MA)) {
                //setListSMA(userDao.findByPayId(smaId));
//                setMerchantList(userDao.getResellerMerchantList(sessionUser.getResellerId()));
                //currencyMap = getAllCurrency();
                currencyMap = Currency.getAllCurrency();

                User sma = userDao.findByPayId(sessionUser.getSmaId());
                Merchants merchants = new Merchants();
                merchants.setBusinessName(sma.getBusinessName());
                merchants.setEmailId(sma.getEmailId());
                merchants.setPayId(sma.getPayId());
                getListSMA().add(merchants);


                Merchants ma = new Merchants();
                ma.setBusinessName(sessionUser.getBusinessName());
                ma.setEmailId(sessionUser.getEmailId());
                ma.setPayId(sessionUser.getPayId());
                getListMA().add(ma);

                setMerchantList(userDao.getMerchantByMAId(sessionUser.getPayId()).stream().map(u->{
                    Merchants merchant = new Merchants();
                    merchant.setEmailId(u.getEmailId());
                    merchant.setPayId(u.getPayId());
                    merchant.setBusinessName(u.getBusinessName());
                    return  (merchant);
                }).collect(Collectors.toList()));


            } else if (sessionUser.getUserType().equals(UserType.Agent)) {
                //setListSMA(userDao.findByPayId(smaId));
//                setMerchantList(userDao.getResellerMerchantList(sessionUser.getResellerId()));
                //currencyMap = getAllCurrency();
                currencyMap = Currency.getAllCurrency();

                User ma = userDao.findByPayId(sessionUser.getMaId());
                User sma = userDao.findByPayId(ma.getSmaId());
                Merchants merchants = new Merchants();
                merchants.setBusinessName(sma.getBusinessName());
                merchants.setEmailId(sma.getEmailId());
                merchants.setPayId(sma.getPayId());
                getListSMA().add(merchants);


                Merchants maMerchant = new Merchants();
                maMerchant.setBusinessName(ma.getBusinessName());
                maMerchant.setEmailId(ma.getEmailId());
                maMerchant.setPayId(ma.getPayId());
                getListMA().add(maMerchant);

                Merchants agent = new Merchants();
                agent.setBusinessName(sessionUser.getBusinessName());
                agent.setEmailId(sessionUser.getEmailId());
                agent.setPayId(sessionUser.getPayId());
                getListAgent().add(agent);

                setMerchantList(userDao.getMerchantByAgentId(sessionUser.getPayId()).stream().map(u->{
                    Merchants merchant = new Merchants();
                    merchant.setEmailId(u.getEmailId());
                    merchant.setPayId(u.getPayId());
                    merchant.setBusinessName(u.getBusinessName());
                    return  (merchant);
                }).collect(Collectors.toList()));

            } else if (sessionUser.getUserType().equals(UserType.MERCHANT) || sessionUser.getUserType().equals(UserType.SUBUSER) || sessionUser.getUserType().equals(UserType.SUBACQUIRER)) {
                Merchants merchant = new Merchants();
                merchant.setEmailId(sessionUser.getEmailId());
                merchant.setBusinessName(sessionUser.getBusinessName());
                merchant.setPayId(sessionUser.getPayId());
                merchantList.add(merchant);
                if (sessionUser.getUserType().equals(UserType.SUBUSER) || sessionUser.getUserType().equals(UserType.SUBACQUIRER)) {
                    String parentMerchantPayId = sessionUser.getParentPayId();
                    User parentMerchant = userDao
                            .findPayId(parentMerchantPayId);
                    merchant.setMerchant(parentMerchant);
                    merchantList.add(merchant);
                    Object[] obj = merchantList.toArray();
                    for (Object sortList : obj) {
                        if (merchantList.indexOf(sortList) != merchantList.lastIndexOf(sortList)) {
                            merchantList.remove(merchantList.lastIndexOf(sortList));
                        }
                    }
                }
                currencyMap = currencyMapProvider.currencyMap(sessionUser);
            }

//			aMaster =vpaMasterDao.getPspList();
//			Map<String, String> pspdata = new HashMap<String, String>();
//			pspdata.put("Others", "Others");
//
//			for(VpaMaster vpaMaster : aMaster) {
//				
//				pspdata.put(vpaMaster.getPspName(), vpaMaster.getPspName());
//				
//				
//			}
//			setPspName(pspdata);
            channelType = reportColumnDetailDao.findByTagName("channelType");
            System.out.println("deepchannel " + channelType);
            filtersColumn = reportColumnDetailDao.findByTagName("channelName");

            setFiltersColumn(filtersColumn);
            setTxnTypelist(TxnType.gettxnType());
            setLst(StatusTypeUI.getStatusType());
            setStatusList(StatusType.getStatusType());
        } catch (Exception exception) {
            logger.error("Exception", exception);
            return ERROR;
        }

        return INPUT;
    }

    public String subUserList() {
        try {
            sessionUser = (User) sessionMap.get(Constants.USER.getValue());
            if (sessionUser.getUserType().equals(UserType.MERCHANT)) {
                setMerchantList(userDao.getSubUserList(sessionUser
                        .getPayId()));
                currencyMap = Currency.getSupportedCurreny(sessionUser);
            } else if (sessionUser.getUserType().equals(UserType.SUBUSER) || sessionUser.getUserType().equals(UserType.SUBACQUIRER)) {
                Merchants merchant = new Merchants();
                User user = new User();
                user = userDao.findPayId(sessionUser.getParentPayId());
                merchant.setEmailId(user.getEmailId());
                merchant.setBusinessName(user.getBusinessName());
                merchant.setPayId(user.getPayId());
                merchantList.add(merchant);
                currencyMap = Currency.getSupportedCurreny(sessionUser);
            }

            setLst(StatusTypeUI.getStatusType());
            setTxnTypelist(TxnType.gettxnType());
        } catch (Exception exception) {
            logger.error("Exception", exception);
            return ERROR;
        }

        return INPUT;
    }


    public Map<String, String> getAllCurrency() {
        Map<String, String> data = new HashMap<String, String>();
//		List<CurrencyCode> dataList=currencyCodeDao.getCurrencyCode();
//		for(CurrencyCode code:dataList) {
//			data.put(code.getCode(), code.getName());
//		}


        return data;

    }

    public String displayList() {
        try {
            sessionUser = (User) sessionMap.get(Constants.USER.getValue());
            List<User> merchantsByReseller;
            if (!resellerId.equalsIgnoreCase("ALL")) {
                merchantsByReseller = userDao.getResellersbyid(resellerId);
                for (User merchantdat : merchantsByReseller) {
                    String merchantpayid = merchantdat.getEmailId();

                    String merchantname = merchantdat.getBusinessName();

                    merchantid.add(merchantpayid);
                    merchantbusname.add(merchantname);

                }

            } else {
                logger.info("sessionMap.get(Constants.SEGMENT.getValue()).toString()...={}", sessionMap.get(Constants.SEGMENT.getValue()).toString());
                logger.info("sessionUser.getRole().getId()...={}", sessionUser.getRole().getId());

                List<Merchants> merchants = (userDao.getMerchantActiveList(sessionMap.get(Constants.SEGMENT.getValue()).toString(), sessionUser.getRole().getId()));
                for (Merchants merchantdat : merchants) {
                    String merchantpayid = merchantdat.getEmailId();

                    String merchantname = merchantdat.getBusinessName();

                    merchantid.add(merchantpayid);
                    merchantbusname.add(merchantname);

                }

            }

            setMerchantarreList(merchantid);
            setMerchantarrename(merchantbusname);
            logger.info("merchantarreList...." + getMerchantarreList());
            return SUCCESS;
        } catch (Exception exception) {
            logger.error("Exception ", exception);
            return ERROR;
        }
    }

    public String displaySMAMerchantList() {
        try {
            sessionUser = (User) sessionMap.get(Constants.USER.getValue());
            List<User> merchantsBySMA;
            if (!smaId.equalsIgnoreCase("ALL")) {
                merchantsBySMA = userDao.getMerchantBySMAId(smaId);
                for (User merchantdat : merchantsBySMA) {
                    String merchantpayid = merchantdat.getEmailId();

                    String merchantname = merchantdat.getBusinessName();

                    merchantid.add(merchantpayid);
                    merchantbusname.add(merchantname);

                }
                listMA = userDao.getMaById(smaId).stream().map(u->{
                    Merchants merchants = new Merchants();
                    merchants.setPayId(u.getPayId());
                    merchants.setBusinessName(u.getBusinessName());
                    return merchants;
                }).collect(Collectors.toList());
            } else {
                logger.info("sessionMap.get(Constants.SEGMENT.getValue()).toString()...={}", sessionMap.get(Constants.SEGMENT.getValue()).toString());
                logger.info("sessionUser.getRole().getId()...={}", sessionUser.getRole().getId());

                List<Merchants> merchants = (userDao.getMerchantActiveList(sessionMap.get(Constants.SEGMENT.getValue()).toString(), sessionUser.getRole().getId()));
                for (Merchants merchantdat : merchants) {
                    String merchantpayid = merchantdat.getEmailId();

                    String merchantname = merchantdat.getBusinessName();

                    merchantid.add(merchantpayid);
                    merchantbusname.add(merchantname);

                }

                listMA = userDao.getMAList();

            }

            listMA.forEach(ma-> listAgent.addAll(userDao.getAgentbyId(ma.getPayId()).stream().map(u->{
                Merchants merchantsDto = new Merchants();
                merchantsDto.setPayId(u.getPayId());
                merchantsDto.setBusinessName(u.getBusinessName());
                return merchantsDto;
            }).collect(Collectors.toList())));

            setMerchantarreList(merchantid);
            setMerchantarrename(merchantbusname);
            logger.info("merchantarreList...." + getMerchantarreList());
            return SUCCESS;
        } catch (Exception exception) {
            logger.error("Exception ", exception);
            return ERROR;
        }
    }

    public String displayMAMerchantList() {
        try {
            sessionUser = (User) sessionMap.get(Constants.USER.getValue());
            List<User> merchantsByReseller;
            if (!maId.equalsIgnoreCase("ALL")) {
                merchantsByReseller = userDao.getMerchantByMAId(maId);
                for (User merchantdat : merchantsByReseller) {
                    String merchantpayid = merchantdat.getEmailId();

                    String merchantname = merchantdat.getBusinessName();

                    merchantid.add(merchantpayid);
                    merchantbusname.add(merchantname);

                }

                listAgent = userDao.getAgentbyId(maId).stream().map(u->{
                    Merchants merchantsDto = new Merchants();
                    merchantsDto.setPayId(u.getPayId());
                    merchantsDto.setBusinessName(u.getBusinessName());
                    return merchantsDto;
                }).collect(Collectors.toList());
            } else {
                logger.info("sessionMap.get(Constants.SEGMENT.getValue()).toString()...={}", sessionMap.get(Constants.SEGMENT.getValue()).toString());
                logger.info("sessionUser.getRole().getId()...={}", sessionUser.getRole().getId());

                List<Merchants> merchants = (userDao.getMerchantActiveList(sessionMap.get(Constants.SEGMENT.getValue()).toString(), sessionUser.getRole().getId()));
                for (Merchants merchantdat : merchants) {
                    String merchantpayid = merchantdat.getEmailId();

                    String merchantname = merchantdat.getBusinessName();

                    merchantid.add(merchantpayid);
                    merchantbusname.add(merchantname);

                }

                listAgent = userDao.getAgentList();

            }

            setMerchantarreList(merchantid);
            setMerchantarrename(merchantbusname);
            logger.info("merchantarreList...." + getMerchantarreList());
            return SUCCESS;
        } catch (Exception exception) {
            logger.error("Exception ", exception);
            return ERROR;
        }
    }


    public String displayAgentMerchantList() {
        try {
            sessionUser = (User) sessionMap.get(Constants.USER.getValue());
            List<User> merchantsByReseller;
            if (!agentId.equalsIgnoreCase("ALL")) {
                merchantsByReseller = userDao.getMerchantByAgentId(agentId);
                for (User merchantdat : merchantsByReseller) {
                    String merchantpayid = merchantdat.getEmailId();

                    String merchantname = merchantdat.getBusinessName();

                    merchantid.add(merchantpayid);
                    merchantbusname.add(merchantname);

                }

            } else {
                logger.info("sessionMap.get(Constants.SEGMENT.getValue()).toString()...={}", sessionMap.get(Constants.SEGMENT.getValue()).toString());
                logger.info("sessionUser.getRole().getId()...={}", sessionUser.getRole().getId());

                List<Merchants> merchants = (userDao.getMerchantActiveList(sessionMap.get(Constants.SEGMENT.getValue()).toString(), sessionUser.getRole().getId()));
                for (Merchants merchantdat : merchants) {
                    String merchantpayid = merchantdat.getEmailId();

                    String merchantname = merchantdat.getBusinessName();

                    merchantid.add(merchantpayid);
                    merchantbusname.add(merchantname);

                }

            }

            setMerchantarreList(merchantid);
            setMerchantarrename(merchantbusname);
            logger.info("merchantarreList...." + getMerchantarreList());
            return SUCCESS;
        } catch (Exception exception) {
            logger.error("Exception ", exception);
            return ERROR;
        }
    }

    public List<Merchants> getMerchantList() {
        return merchantList;
    }

    public void setMerchantList(List<Merchants> merchantList) {
        this.merchantList = merchantList;
    }

    public List<CompanyProfileUi> getCompanyList() {
        return companyList;
    }

    public void setCompanyList(List<CompanyProfileUi> companyList) {
        this.companyList = companyList;
    }

    public List<StatusTypeUI> getLst() {
        return lst;
    }

    public void setLst(List<StatusTypeUI> lst) {
        this.lst = lst;
    }

    public Map<String, String> getCurrencyMap() {
        return currencyMap;
    }

    public void setCurrencyMap(Map<String, String> currencyMap) {
        this.currencyMap = currencyMap;
    }

    public List<TxnType> getTxnTypelist() {
        return txnTypelist;
    }

    public void setTxnTypelist(List<TxnType> txnTypelist) {
        this.txnTypelist = txnTypelist;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public List<StatusType> getStatusList() {
        return statusList;
    }

    public void setStatusList(List<StatusType> statusList) {
        this.statusList = statusList;
    }


    public Map<String, String> getPspName() {
        return pspName;
    }

    public void setPspName(Map<String, String> pspName) {
        this.pspName = pspName;
    }

    public List<Merchants> getResellerList() {
        return resellerList;
    }

    public void setResellerList(List<Merchants> resellerList) {
        this.resellerList = resellerList;
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

    public String getResellerId() {
        return resellerId;
    }

    public void setResellerId(String resellerId) {
        this.resellerId = resellerId;
    }

    public Map<String, String> getChannelType() {
        return channelType;
    }

    public void setChannelType(Map<String, String> channelType) {
        this.channelType = channelType;
    }

    public Map<String, String> getFiltersColumn() {
        return filtersColumn;
    }

    public void setFiltersColumn(Map<String, String> filtersColumn) {
        this.filtersColumn = filtersColumn;
    }

    public String getSmaId() {
        return smaId;
    }

    public void setSmaId(String smaId) {
        this.smaId = smaId;
    }

    public String getMaId() {
        return maId;
    }

    public void setMaId(String maId) {
        this.maId = maId;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
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


}
