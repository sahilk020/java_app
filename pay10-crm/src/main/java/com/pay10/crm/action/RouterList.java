
package com.pay10.crm.action;

import org.slf4j.LoggerFactory;
import java.util.LinkedHashSet;

import com.pay10.commons.util.Constants;
import com.pay10.commons.util.Currency;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.MopType;
import java.util.Set;
import com.pay10.commons.user.Merchants;
import com.pay10.commons.user.User;

import java.util.Map;
import com.pay10.commons.util.TransactionType;
import java.util.List;
import org.slf4j.Logger;

public class RouterList extends AbstractSecureAction
{
    private static Logger logger;
    private static final long serialVersionUID = -3118366599992623506L;
    private List<TransactionType> transactionTypeList;
    private Map<String, String> currencyMap;
    private List<Merchants> merchantList;
    private Set<MopType> mopList;
    private Set<PaymentType> paymentTypeList;
    private String merchantEmailId;
    private User sessionUser = null;
    
    @Autowired
    private UserDao userDao;
    
    public RouterList() {
        this.transactionTypeList = new ArrayList<TransactionType>();
        this.currencyMap = (Map<String, String>)Currency.getAllCurrency();
        this.merchantList = new ArrayList<Merchants>();
        this.mopList = new LinkedHashSet<MopType>();
        this.paymentTypeList = new LinkedHashSet<PaymentType>();
    }
    
    @SuppressWarnings("unchecked")
	public String execute() {
        try {
           
            //this.merchantList = (List<Merchants>)this.userDao.getMerchantActiveList();
        	sessionUser = (User) sessionMap.get(Constants.USER.getValue());
			this.merchantList = (List<Merchants>)this.userDao.getMerchantActiveList(sessionMap.get(Constants.SEGMENT.getValue()).toString(), sessionUser.getRole().getId());
            return "success";
        }
        catch (Exception exception) {
            RouterList.logger.error("Exception ", (Throwable)exception);
            return "error";
        }
    }
    
    public Map<String, String> getCurrencyMap() {
        return this.currencyMap;
    }
    
    public void setCurrencyMap(final Map<String, String> currencyMap) {
        this.currencyMap = currencyMap;
    }
    
    public List<Merchants> getMerchantList() {
        return this.merchantList;
    }
    
    public void setMerchantList(final List<Merchants> merchantList) {
        this.merchantList = merchantList;
    }
    
    public Set<MopType> getMopList() {
        return this.mopList;
    }
    
    public void setMopList(final Set<MopType> mopList) {
        this.mopList = mopList;
    }
    
    public Set<PaymentType> getPaymentTypeList() {
        return this.paymentTypeList;
    }
    
    public void setPaymentTypeList(final Set<PaymentType> paymentTypeList) {
        this.paymentTypeList = paymentTypeList;
    }
    
    public String getMerchantEmailId() {
        return this.merchantEmailId;
    }
    
    public void setMerchantEmailId(final String merchantEmailId) {
        this.merchantEmailId = merchantEmailId;
    }
    
    public List<TransactionType> getTransactionTypeList() {
        return this.transactionTypeList;
    }
    
    public void setTransactionTypeList(final List<TransactionType> transactionTypeList) {
        this.transactionTypeList = transactionTypeList;
    }
    
    static {
        RouterList.logger = LoggerFactory.getLogger(AcquirerRouterRedirect.class.getName());
    }
}