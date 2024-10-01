package com.pay10.crm.action;

import java.util.*;
import java.util.stream.Collectors;

import com.pay10.commons.dao.RouterRuleDao;
import com.pay10.commons.user.*;
import com.pay10.commons.util.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class AcquirerRouterRedirect extends AbstractSecureAction {
    private static final long serialVersionUID = -3118366599992623506L;
    private static final int FETCH_ACQR_TYPE = 1;
    private static final int FETCH_CURRENCY_TYPE = 2;
    private static final int FETCH_PAYMENT_TYPE = 3;
    private static final int FETCH_MOP_TYPE = 4;
    private static Logger logger = LoggerFactory.getLogger(AcquirerRouterRedirect.class.getName());
    public List<MultCurrencyCode> currencyList = new ArrayList<MultCurrencyCode>();
    public Map<String, String> currencyMap1 = new HashMap<String, String>();
    private List<TransactionType> transactionTypeList = new ArrayList<TransactionType>();
    //	private Map<String, String> currencyMap = Currency.getAllCurrency();
    private List<Merchants> merchantList = new ArrayList<Merchants>();
    private List<String> moptypeList = new ArrayList<String>();
    private Map<String, String> currencyMap = new HashMap<String, String>();
    private List<String> acquirerList = new ArrayList<String>();
    private List<String> acquirerList1 = new ArrayList<String>();
    private List<String> acquirerList2 = new ArrayList<String>();
    private List<String> mopbanklist = new ArrayList<String>();
    private List<MopTypeDto> moplist = new ArrayList<MopTypeDto>();

    private List<String> acquirerListEdit = new ArrayList<>();
    @Autowired
    private UserDao userDao;

    @Autowired
    private RouterRuleDao routerRuleDao;

    private String payId;
    @Autowired
    private MultCurrencyCodeDao currencyCodeDao;


    private int type;
    private String Acquirer1;
    private String paytype;
    private User sessionUser = null;
    private String merchantemail;
    private String payment;

    private String selectedCurrency;


    @SuppressWarnings("unchecked")
    @Override
    public String execute() {
        try {


            sessionUser = (User) sessionMap.get(Constants.USER.getValue());
//            transactionTypeList.add(TransactionType.AUTHORISE);
            transactionTypeList.add(TransactionType.SALE);
            // merchantList = userDao.getMerchantActiveList();
            merchantList = userDao.getMerchantActiveList(sessionMap.get(Constants.SEGMENT.getValue()).toString(),
                    sessionUser.getRole().getId());


            {
                switch (type) {

                    case FETCH_ACQR_TYPE:
                        logger.info("Fetch ACQR data type= {}, payId={}", type, payId);
                        List<String> acqlist = userDao.getAcquirerTypeList(payId);
                        for (String acqList : acqlist) {
                            String contoCode = AcquirerType.getInstancefromName(acqList).getCode();
                            acquirerList.add(contoCode);

                        }
                        setAcquirerList(acquirerList);
                        List<String> acquirerListEditDemo = routerRuleDao.getAcquirerListEdit(payId);
                        Set<String> acquirerSet = new HashSet<>();
                        for (String acqList1 : acquirerListEditDemo) {
                            String[] acquirers = acqList1.split(",");
                            for (String acquirer : acquirers) {
                                acquirerSet.add(acquirer.substring(acquirer.lastIndexOf('-') + 1));
                            }
                        }
                        acquirerListEdit.addAll(acquirerSet);
                        setAcquirerListEdit(acquirerListEdit);
                        logger.info("Acquirer List : {}", acquirerList);
                        logger.info("Acquirer List Edit: {}", acquirerListEdit);
                        break;

                    case FETCH_CURRENCY_TYPE:
                        logger.info("Fetch CURRENCY data type= {}, payId={}", type, payId);
                        String Acquirer4 = AcquirerType.getAcquirerName(Acquirer1);
                        if (Objects.equals(Acquirer1, "ALL")) {
                            for (AcquirerTypeUI acquirerType : AcquirerTypeUI.values()) {
                                List<String> currencyList111 = userDao.getCurrencyTypeList(acquirerType.getName(), payId);
                                for (String currency : currencyList111) {
                                    String cName = currencyCodeDao.getCurrencyNamebyCode(currency);
                                    if (cName == null)
                                        continue;
                                    currencyMap1.put(currency, cName);
                                }
                            }
                        } else {
                            List<String> currencyList111 = userDao.getCurrencyTypeList(Acquirer4, payId);
                            for (String currency : currencyList111) {
                                String cName = currencyCodeDao.getCurrencyNamebyCode(currency);
                                currencyMap1.put(currency, cName);
                            }
                        }
                        setCurrencyMap(currencyMap1);
                        logger.info("currencyMap1 :::::::::::::" + currencyMap1);

                        break;
                    case FETCH_PAYMENT_TYPE:
                        logger.info("Fetch Payment type list data  type= {}, payId={}", type, payId);
                        String Acquirer = AcquirerType.getAcquirerName(Acquirer1);
                        List<String> paymentLsit = userDao.getPaymentType(payId, Acquirer, selectedCurrency);
                        setAcquirerList1(paymentLsit);
                        logger.info("Payment List : {}", paymentLsit);

                        break;
                    case FETCH_MOP_TYPE:
                        logger.info("Fetch MOP data type= {}, payId={}", type, payId);
                        String Acquirer2 = AcquirerType.getAcquirerName(Acquirer1);
                        List<String> mopList = userDao.getMopTypelist(payId, Acquirer2, paytype, selectedCurrency);
                        for (String moplList : mopList) {
                            String contoname = MopType.getInstanceUsingStringValue1(moplList).getName();
                            moptypeList.add(contoname);

                        }

                        setAcquirerList2(moptypeList);
                        logger.info("Mop List : {}", mopList);
                        break;

                    default:
                        logger.info("null value in type");
                }
            }

            return SUCCESS;
        } catch (Exception exception) {
            logger.error("Exception ", exception);
            return ERROR;
        }
    }

    public String GetAllMoptypeBypayment() {
        try {
            MopTypeUI[] mopdata = MopTypeUI.values();
            for (MopTypeUI mopList : mopdata) {
                moplist.add(new MopTypeDto(mopList.getCode(), mopList.getName(), mopList.getName()));

            }
            return SUCCESS;
        } catch (Exception exception) {
            logger.error("Exception ", exception);
            return ERROR;
        }
    }

    public String GetMoptypeBypayment() {
        try {
            String payId = null;
            if (!StringUtils.isBlank(merchantemail)) {
                User user = userDao.findByEmailId(merchantemail);
                if (user != null) {
                    if (user.getUserType().equals(UserType.SUBUSER)) {
                        payId = user.getParentPayId();
                    } else {
                        payId = user.getPayId();
                    }
                }
            }
            if (!StringUtils.isBlank(payment)) {
                if (payment.equalsIgnoreCase("ALL")) {
                    payment = StringUtils.join(Arrays.stream(PaymentTypeUI.values())
                            .map(PaymentTypeUI::getCode)
                            .collect(Collectors.toList()), ",");
                }
                for (String paymentType : payment.split(",")) {
                    String payvalue = PaymentTypeUI.getInstanceUsingCode1(paymentType);
                    List<String> mopdata = userDao.getMOPtypelist(payId, payvalue);
                    for (String mopList : mopdata) {
                        MopType mopvalue = MopType.getInstanceUsingStringValue1(mopList);
                        if (mopvalue != null) {
                            moplist.add(new MopTypeDto(mopvalue.getCode(), mopvalue.getName(), mopvalue.getName()));
                        }
                    }
                }
            }
            moplist = moplist.stream().distinct().collect(Collectors.toList());
            moplist.sort(Comparator.comparing(MopTypeDto::getName, String.CASE_INSENSITIVE_ORDER));

            return SUCCESS;
        } catch (Exception exception) {
            logger.error("Exception ", exception);
            return ERROR;
        }

    }

//    public static void main(String[] args) {
//        List<MopTypeDto> moplist = new ArrayList<>();
//        List<String> mopdata = new ArrayList<>();
//        mopdata.add("VISA");
//        mopdata.add("VISA");
//        mopdata.add("MASTERCARD");
//        for (String mopList : mopdata) {
//            MopType mopvalue = MopType.getInstanceUsingStringValue1(mopList);
//            if (mopvalue != null) {
//                moplist.add(new MopTypeDto(mopvalue.getCode(), mopvalue.getName(), mopvalue.getName()));
//            }
//        }
//
//        moplist = moplist.stream().distinct().collect(Collectors.toList());
//        moplist.sort(Comparator.comparing(MopTypeDto::getName, String.CASE_INSENSITIVE_ORDER));
//        System.out.println(moplist);
//    }

    public List<MultCurrencyCode> getCurrencyList() {
        return currencyList;
    }

    public void setCurrencyList(List<MultCurrencyCode> currencyList) {
        this.currencyList = currencyList;
    }

    public Map<String, String> getCurrencyMap() {
        return currencyMap;
    }

    public void setCurrencyMap(Map<String, String> currencyMap) {
        this.currencyMap = currencyMap;
    }

    public List<Merchants> getMerchantList() {
        return merchantList;
    }

    public void setMerchantList(List<Merchants> merchantList) {
        this.merchantList = merchantList;
    }

    public List<TransactionType> getTransactionTypeList() {
        return transactionTypeList;
    }

    public void setTransactionTypeList(List<TransactionType> transactionTypeList) {
        this.transactionTypeList = transactionTypeList;

    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<String> getAcquirerList() {
        return acquirerList;
    }

    public void setAcquirerList(List<String> acquirerList) {
        this.acquirerList = acquirerList;
    }

    public String getAcquirer1() {
        return Acquirer1;
    }

    public void setAcquirer1(String acquirer1) {
        Acquirer1 = acquirer1;
    }

    public String getPaytype() {
        return paytype;
    }

    public void setPaytype(String paytype) {
        this.paytype = paytype;
    }

    public List<String> getMopbanklist() {
        return mopbanklist;
    }

    public void setMopbanklist(List<String> mopbanklist) {
        this.mopbanklist = mopbanklist;
    }

    public List<String> getAcquirerList1() {
        return acquirerList1;
    }

    public void setAcquirerList1(List<String> acquirerList1) {
        this.acquirerList1 = acquirerList1;
    }

    public List<String> getAcquirerList2() {
        return acquirerList2;
    }

    public void setAcquirerList2(List<String> acquirerList2) {
        this.acquirerList2 = acquirerList2;
    }

    public String getPayId() {
        return payId;
    }

    public void setPayId(String payId) {
        this.payId = payId;
    }

    public String getMerchantemail() {
        return merchantemail;
    }

    public void setMerchantemail(String merchantemail) {
        this.merchantemail = merchantemail;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public List<String> getMoptypeList() {
        return moptypeList;
    }

    public void setMoptypeList(List<String> moptypeList) {
        this.moptypeList = moptypeList;
    }

    public List<MopTypeDto> getMoplist() {
        return moplist;
    }

    public void setMoplist(List<MopTypeDto> moplist) {
        this.moplist = moplist;
    }

    public String getSelectedCurrency() {
        return selectedCurrency;
    }

    public void setSelectedCurrency(String selectedCurrency) {
        this.selectedCurrency = selectedCurrency;
    }

    public List<String> getAcquirerListEdit() {
        return acquirerListEdit;
    }

    public void setAcquirerListEdit(List<String> acquirerListEdit) {
        this.acquirerListEdit = acquirerListEdit;
    }
}