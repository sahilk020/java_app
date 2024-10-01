package com.pay10.commons.user;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Proxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.pay10.commons.dao.HibernateSessionProvider;
import com.pay10.commons.dao.RouterRuleDao;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.DateCreater;
import com.pay10.commons.util.Helper;
import com.pay10.commons.util.MopType;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.TDRStatus;
import com.pay10.commons.util.TransactionType;

@Entity
@Proxy(lazy= false)@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Account  implements Serializable,Comparable<Account>{
	private static Logger logger = LoggerFactory.getLogger(Account.class.getName());
	private static final long serialVersionUID = 1799371834204950674L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	private String merchantId;
	private String password;	
	private String acquirerPayId; 
	private boolean primaryStatus;
	private boolean PrimaryNetbankingStatus;
	private String txnKey;
	private String acquirerName;
	@OneToMany(targetEntity=Payment.class,fetch = FetchType.EAGER,cascade = CascadeType.ALL,orphanRemoval=true)
	private Set<Payment> payments = new HashSet<Payment>();
	
	@OneToMany(targetEntity=PaymentInternational.class,fetch = FetchType.EAGER,cascade = CascadeType.ALL,orphanRemoval=true)
	private Set<PaymentInternational> paymentsInternational = new HashSet<PaymentInternational>();

	@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	@OneToMany(targetEntity=AccountCurrency.class,fetch = FetchType.EAGER,cascade = CascadeType.ALL,orphanRemoval=true)
	private Set<AccountCurrency> accountCurrencySet = new HashSet<AccountCurrency>();

	@OneToMany(targetEntity=ChargingDetails.class,fetch = FetchType.EAGER,cascade = CascadeType.ALL)
	private Set<ChargingDetails> chargingDetails = new HashSet<ChargingDetails>();
	
	@OneToMany(targetEntity=TdrSetting.class,fetch = FetchType.EAGER,cascade = CascadeType.ALL)
	private Set<TdrSetting> tdrSetting = new HashSet<TdrSetting>();

	@Transient
	private static final String seprator = "-";

	public Account(){
		
	}

	public AccountCurrency getAccountCurrency(String currencyCode) throws SystemException{
		AccountCurrency accountCurrency = null;
		for(AccountCurrency accountCurrencyInst:getAccountCurrencySet()){
			if(accountCurrencyInst.getCurrencyCode().equals(currencyCode)){
				accountCurrency = accountCurrencyInst;
			}
		}		
		return accountCurrency;
	}

	public void disableChargingDetail(String token, boolean international, boolean domestic){       
        String[] splittedToken = token.split(seprator);
		Set<ChargingDetails> chargingDetailSet = getChargingDetails();
		Iterator<ChargingDetails> chargingDetailIterator = chargingDetailSet.iterator();
		while(chargingDetailIterator.hasNext()){
			ChargingDetails detail = chargingDetailIterator.next();
			if(detail.getStatus().equals(TDRStatus.INACTIVE)){
				continue;
			}

			if(detail.getPaymentType().equals(PaymentType.getInstance(splittedToken[1]))){
				if(detail.getMopType().equals(MopType.getmop(splittedToken[2]))){
					if(splittedToken.length==3){
						detail.setUpdatedDate(new Date());
						detail.setStatus(TDRStatus.INACTIVE);
						deactiveRouterConfiguration(detail, international, domestic);
						ModifyRouterRule(detail);
						continue;
					}
					if(detail.getTransactionType().equals(TransactionType.getInstanceFromCode(splittedToken[3]))){
						detail.setUpdatedDate(new Date());
						detail.setStatus(TDRStatus.INACTIVE);
						deactiveRouterConfiguration(detail, international, domestic);
						ModifyRouterRule(detail);
					}
				}
			}			
		}		
	}
	public void disableTdrSetting(String token, boolean international, boolean domestic){       
        String[] splittedToken = token.split(seprator);
		Set<TdrSetting> chargingDetailSet = getTdrSetting();
		Iterator<TdrSetting> chargingDetailIterator = chargingDetailSet.iterator();
		while(chargingDetailIterator.hasNext()){
			TdrSetting detail = chargingDetailIterator.next();
			if(detail.getStatus().equals(TDRStatus.INACTIVE.toString())){
				continue;
			}

			if(detail.getPaymentType().equals(PaymentType.getInstance(splittedToken[1])!=null?PaymentType.getInstance(splittedToken[1]).toString():"")){
				if(detail.getMopType().equals(MopType.getmop(splittedToken[2]).toString())){
					if(splittedToken.length==3){
						//detail.setFromDate(new Date());
						detail.setStatus(TDRStatus.INACTIVE.toString());
						deactiveRouterConfigurationForTdrSetting(detail,true,false);
						ModifyRouterRuleForTdrSetting(detail);
						continue;
					}
					if(detail.getTransactionType().equals(TransactionType.getInstanceFromCode(splittedToken[3]).toString())){
						//detail.setFromDate(new Date());
						detail.setStatus(TDRStatus.INACTIVE.toString());
						deactiveRouterConfigurationForTdrSetting(detail,true,false);
						ModifyRouterRuleForTdrSetting(detail);
					}
				}
			}			
		}		
	}
	public void disableTdrSettingDomestic(String token){
        String[] splittedToken = token.split(seprator);
		Set<TdrSetting> chargingDetailSet = getTdrSetting();
		Iterator<TdrSetting> chargingDetailIterator = chargingDetailSet.iterator();
		while(chargingDetailIterator.hasNext()){
			TdrSetting detail = chargingDetailIterator.next();
			if(detail.getStatus().equals(TDRStatus.INACTIVE.toString())){
				continue;
			}

			if(detail.getPaymentRegion().equalsIgnoreCase(AccountCurrencyRegions.DOMESTIC.name()) && detail.getPaymentType().equals(PaymentType.getInstance(splittedToken[1])!=null?PaymentType.getInstance(splittedToken[1]).toString():"")){
				if(detail.getMopType().equals(MopType.getmop(splittedToken[2]).toString())){
					if(splittedToken.length==3){
						//detail.setFromDate(new Date());
						detail.setStatus(TDRStatus.INACTIVE.toString());
						deactiveRouterConfigurationForTdrSetting(detail,false,true);
						ModifyRouterRuleForTdrSetting(detail);
						continue;
					}
					if(detail.getTransactionType().equals(TransactionType.getInstanceFromCode(splittedToken[3]).toString())){
						//detail.setFromDate(new Date());
						detail.setStatus(TDRStatus.INACTIVE.toString());
						deactiveRouterConfigurationForTdrSetting(detail,false, true);
						ModifyRouterRuleForTdrSetting(detail);
					}
				}
			}			
		}		
	}
	
	public void disableTdrSettingInterNational(String token){
        String[] splittedToken = token.split(seprator);
		Set<TdrSetting> chargingDetailSet = getTdrSetting();
		Iterator<TdrSetting> chargingDetailIterator = chargingDetailSet.iterator();
		while(chargingDetailIterator.hasNext()){
			TdrSetting detail = chargingDetailIterator.next();
			if(detail.getStatus().equals(TDRStatus.INACTIVE.toString())){
				continue;
			}

			if(detail.getPaymentRegion().equalsIgnoreCase(AccountCurrencyRegions.INTERNATIONAL.name()) &&detail.getPaymentType().equals(PaymentType.getInstance(splittedToken[1])!=null?PaymentType.getInstance(splittedToken[1]).toString():"")){
				if(detail.getMopType().equals(MopType.getmop(splittedToken[2]).toString())){
					if(splittedToken.length==3){
						//detail.setFromDate(new Date());
						detail.setStatus(TDRStatus.INACTIVE.toString());
						//deactiveRouterConfigurationForTdrSetting(detail, international, domestic);
						//ModifyRouterRuleForTdrSetting(detail);
						continue;
					}
					if(detail.getTransactionType().equals(TransactionType.getInstanceFromCode(splittedToken[3]).toString())){
						//detail.setFromDate(new Date());
						detail.setStatus(TDRStatus.INACTIVE.toString());
						//deactiveRouterConfigurationForTdrSetting(detail, international, domestic);
						//ModifyRouterRuleForTdrSetting(detail);
					}
				}
			}			
		}		
	}

	public void deactiveRouterConfiguration(ChargingDetails detail, boolean international, boolean domestic) {
		try {
			String paymentsRegion = null;
			if(international) {
				paymentsRegion = "INTERNATIONAL";
			}
			
			if(domestic) {
				paymentsRegion = "DOMESTIC";
			}
			
			String identifier = detail.getPayId() + detail.getCurrency()
					+ PaymentType.getInstanceIgnoreCase(detail.getPaymentType().toString()).getCode()
					+ MopType.getInstanceIgnoreCase(detail.getMopType().toString()).getCode()
					+ detail.getTransactionType() + paymentsRegion + "CONSUMER00";
			
			Session session = HibernateSessionProvider.getSession();
			Transaction tx = session.beginTransaction();
			session.createQuery(
					"update RouterConfiguration RR set RR.status='INACTIVE' where RR.status='ACTIVE' and RR.merchant=:payId and RR.acquirer =:acquirerName and identifier =:identifier ")
					.setParameter("payId", detail.getPayId()).setParameter("acquirerName", detail.getAcquirerName()).setParameter("identifier", identifier).executeUpdate();
			tx.commit();
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Exception In deactiveRouterConfiguration : "+e);
		}

	}
	public void deactiveRouterConfigurationForTdrSetting(TdrSetting detail, boolean international, boolean domestic) {
		try {

			String paymentsRegion = "";
			if(international) {
				paymentsRegion = "INTERNATIONAL";
			}

			if(domestic) {
				paymentsRegion = "DOMESTIC";
			}
			
			String identifier = detail.getPayId() + detail.getCurrency()
			+ PaymentType.getInstanceIgnoreCase(detail.getPaymentType().toString()).getCode()
			+ MopType.getInstanceIgnoreCase(detail.getMopType().toString()).getCode()
			+ detail.getTransactionType() + paymentsRegion + "CONSUMER00";
	
			Session session = HibernateSessionProvider.getSession();
			Transaction tx = session.beginTransaction();
			session.createQuery(
					"update RouterConfiguration RR set RR.status='INACTIVE' where RR.status='ACTIVE' and RR.merchant=:payId and RR.acquirer =:acquirerName and identifier =:identifier ")
					.setParameter("payId", detail.getPayId()).setParameter("acquirerName", detail.getAcquirerName()).setParameter("identifier", identifier).executeUpdate();
			tx.commit();
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Exception In deactiveRouterConfiguration : "+e);
		}

	}
    public void ModifyRouterRule(ChargingDetails detail) {
		try {
			RouterRuleDao ruleDaoAtl = new RouterRuleDao();
			RouterRule atlRules = ruleDaoAtl.findRuleByFieldsByPayId(detail.getPayId(),
					PaymentType.getInstanceIgnoreCase(detail.getPaymentType().toString()).getCode(),
					MopType.getInstanceUsingStringValue1(detail.getMopType().toString()).getCode(), detail.getCurrency(),
					detail.getTransactionType().toString());
			
			if (atlRules == null) {
				logger.info("RouterRule Not Found For PayId : " + detail.getPayId() + ", PaymentType : "
						+ detail.getPaymentType() + ", MopType : " + detail.getMopType());
				return;
			}
			String atlAcquirerList = atlRules.getAcquirerMap();
			Collection<String> atlAcqList = Helper.parseFields(atlAcquirerList);
			Map<String, String> atlAcquirerMap = new LinkedHashMap<String, String>();
			for (String atlAcquirer : atlAcqList) {
				String[] acquirerPreference = atlAcquirer.split("-");
				atlAcquirerMap.put(acquirerPreference[0], acquirerPreference[1]);
			}

			atlAcquirerMap.values().remove(detail.getAcquirerName());
						
			String acquirerMapStr = null;
			int cnt = 1;
			Collection<String> values = atlAcquirerMap.values();
			for (String v : values) {
			    if(cnt == 1) {
			    	acquirerMapStr = cnt+"-"+v;
			    } else {
			    	acquirerMapStr += ","+cnt+"-"+v;
			    }
			    cnt ++;
			}
			//System.out.println("acquirerMapStr : "+acquirerMapStr);
			
			if(acquirerMapStr != null) {
				atlRules.setAcquirerMap(acquirerMapStr);
				ruleDaoAtl.updateRulelist(atlRules);
			}else {
				ruleDaoAtl.delete(atlRules.getId());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Exception In ModifyRouterRule : "+e);
		}

	}
    public void ModifyRouterRuleForTdrSetting(TdrSetting detail) {
		try {
			RouterRuleDao ruleDaoAtl = new RouterRuleDao();
			RouterRule atlRules = ruleDaoAtl.findRuleByFieldsByPayId(detail.getPayId(),
					PaymentType.getInstanceIgnoreCase(detail.getPaymentType().toString()).getCode(),
					MopType.getInstanceUsingStringValue1(detail.getMopType().toString()).getCode(), detail.getCurrency(),
					detail.getTransactionType().toString());
			
			if (atlRules == null) {
				logger.info("RouterRule Not Found For PayId : " + detail.getPayId() + ", PaymentType : "
						+ detail.getPaymentType() + ", MopType : " + detail.getMopType());
				return;
			}
			String atlAcquirerList = atlRules.getAcquirerMap();
			Collection<String> atlAcqList = Helper.parseFields(atlAcquirerList);
			Map<String, String> atlAcquirerMap = new LinkedHashMap<String, String>();
			for (String atlAcquirer : atlAcqList) {
				String[] acquirerPreference = atlAcquirer.split("-");
				atlAcquirerMap.put(acquirerPreference[0], acquirerPreference[1]);
			}

			atlAcquirerMap.values().remove(detail.getAcquirerName());
						
			String acquirerMapStr = null;
			int cnt = 1;
			Collection<String> values = atlAcquirerMap.values();
			for (String v : values) {
			    if(cnt == 1) {
			    	acquirerMapStr = cnt+"-"+v;
			    } else {
			    	acquirerMapStr += ","+cnt+"-"+v;
			    }
			    cnt ++;
			}
			//System.out.println("acquirerMapStr : "+acquirerMapStr);
			
			if(acquirerMapStr != null) {
				atlRules.setAcquirerMap(acquirerMapStr);
				ruleDaoAtl.updateRulelist(atlRules);
			}else {
				ruleDaoAtl.delete(atlRules.getId());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Exception In ModifyRouterRule : "+e);
		}

	}

    public String getMappedString(){
    	StringBuilder savedMappingString = new StringBuilder();
    	for(Payment savedPayment:payments){
    		Set<Mop> mops = savedPayment.getMops();
    		for(Mop mop:mops){    			
    			if(!(savedPayment.getPaymentType().getCode().equals(PaymentType.NET_BANKING.getCode()))){
    				Set<MopTransaction> mopTxnSet = mop.getMopTransactionTypes();
    				for(MopTransaction mopTxn:mopTxnSet){
    					TransactionType txnType = mopTxn.getTransactionType();
    					if(!StringUtils.isEmpty(savedMappingString)){

    						savedMappingString.append(CrmFieldConstants.COMMA.getValue());
    					}
//						logger.info("TDR SETTING CURRENCY :::: "+currencyCode);
//						savedMappingString.append(details.getCurrency());
//						savedMappingString.append(seprator);
    					savedMappingString.append(savedPayment.getPaymentType().getName());
    					savedMappingString.append(seprator);
    					savedMappingString.append(mop.getMopType().getCode());
        				savedMappingString.append(seprator);
        				savedMappingString.append(txnType.getCode());
        			}
    			}else{
    				if(!StringUtils.isEmpty(savedMappingString)){
    					savedMappingString.append(CrmFieldConstants.COMMA.getValue());
    				}
    				savedMappingString.append(savedPayment.getPaymentType().getName());
					savedMappingString.append(seprator);
					savedMappingString.append(mop.getMopType().getCode());
    			}
    		}
    	}
    	return savedMappingString.toString();
	}
    
    public String getMappedStringInternational(){
    	StringBuilder savedMappingString = new StringBuilder();
    	for(PaymentInternational savedPayment:paymentsInternational){
    		Set<MopInternational> mops = savedPayment.getMops();
    		for(MopInternational mop:mops){    			
    			if(!(savedPayment.getPaymentType().getCode().equals(PaymentType.NET_BANKING.getCode()))){
    				Set<MopTransactionInternational> mopTxnSet = mop.getMopTransactionTypes();
    				for(MopTransactionInternational mopTxn:mopTxnSet){
    					TransactionType txnType = mopTxn.getTransactionType();
    					if(!StringUtils.isEmpty(savedMappingString)){
    						savedMappingString.append(CrmFieldConstants.COMMA.getValue());
    					}
    					savedMappingString.append(savedPayment.getPaymentType().getName());
    					savedMappingString.append(seprator);
    					savedMappingString.append(mop.getMopType().getCode());
        				savedMappingString.append(seprator);
        				savedMappingString.append(txnType.getCode());
        			}
    			}else{
    				if(!StringUtils.isEmpty(savedMappingString)){
    					savedMappingString.append(CrmFieldConstants.COMMA.getValue());
    				}
    				savedMappingString.append(savedPayment.getPaymentType().getName());
					savedMappingString.append(seprator);
					savedMappingString.append(mop.getMopType().getCode());
    			}
    		}
    	}
    	return savedMappingString.toString();
	}

    public ChargingDetails getChargingDetails(String date,String paymentType,String mopType,String txnType,String currencyCode){
		//Set<ChargingDetails> chargingDetails = getChargingDetails();
		if(!chargingDetails.isEmpty()){
			Iterator<ChargingDetails> itr = chargingDetails.iterator();
			while(itr.hasNext()) {
				ChargingDetails chargingDetail = itr.next();
				if(null == chargingDetail.getUpdatedDate()) {
					/*if(chargingDetail.getCreatedDate().compareTo(DateCreater.formatStringToDateTime(date)) == -1 && DateCreater.currentDateTime().compareTo(DateCreater.formatStringToDateTime(date)) == 1) {*/
					if(null != chargingDetail.getCreatedDate()) {
						if(DateCreater.formatStringToDateTime(date).getTime() >= chargingDetail.getCreatedDate().getTime()  && DateCreater.formatStringToDateTime(date).getTime() <= DateCreater.currentDateTime().getTime()) {
							if(chargingDetail.getPaymentType().getName().equals(paymentType) && chargingDetail.getMopType().getName().equals(mopType) && chargingDetail.getTransactionType().getName().equals(txnType)) {
								return chargingDetail;
							}
						}
					}
				}
				else {
					if(null != chargingDetail.getCreatedDate()) {
						if(DateCreater.formatStringToDateTime(date).getTime() >= chargingDetail.getCreatedDate().getTime()  && DateCreater.formatStringToDateTime(date).getTime() <= chargingDetail.getUpdatedDate().getTime()) {
							if(DateCreater.currentDateTime() == DateCreater.formatStringToDateTime(date)) {
								if(chargingDetail.getPaymentType().getName().equals(paymentType) && chargingDetail.getMopType().getName().equals(mopType) && chargingDetail.getTransactionType().getName().equals(txnType)) {
									return chargingDetail;
								}
							}
						}
					}
				}												
			}
		}
		
		return null;
	}

    @Override
	public int compareTo(Account compareAccount) {
    	return compareAccount.getAcquirerPayId().compareToIgnoreCase(this.acquirerPayId);
	}
	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAcquirerPayId() {
		return acquirerPayId;
	}

	public void setAcquirerPayId(String acquirerPayId) {
		this.acquirerPayId = acquirerPayId;
	}

	public Set<Payment> getPayments() {
		return payments;
	}

	public void setPayments(Set<Payment> payments) {
		this.payments = payments;
	}
	
	public void addPayment(Payment payment){
		payments.add(payment);
	}
	
	public void removePayment(Payment payment){
		payments.remove(payment);
	}
	
	public void addPaymentsInternational(PaymentInternational payment){
		paymentsInternational.add(payment);
	}
	
	public void removePaymentsInternational(PaymentInternational payment){
		paymentsInternational.remove(payment);
	}
	
	public Set<PaymentInternational> getPaymentsInternational() {
		return paymentsInternational;
	}

	public void setPaymentsInternational(Set<PaymentInternational> paymentsInternational) {
		this.paymentsInternational = paymentsInternational;
	}

	public void addChargingDetail(ChargingDetails chargingDetail){
		chargingDetails.add(chargingDetail);
	}
	
	public void removeChargingDetail(ChargingDetails chargingDetail){
		chargingDetails.remove(chargingDetail);
	}

	public void addTdrSetting(TdrSetting chargingDetail){
		logger.info("Adding TDR Setting");
		tdrSetting.add(chargingDetail);
	}
	
	public void removeTdrSetting(TdrSetting chargingDetail){
		tdrSetting.remove(chargingDetail);
	}
	
	public boolean isPrimaryStatus() {
		return primaryStatus;
	}

	public void setPrimaryStatus(boolean primaryStatus) {
		this.primaryStatus = primaryStatus;
	}

	public boolean isPrimaryNetbankingStatus() {
		return PrimaryNetbankingStatus;
	}

	public void setPrimaryNetbankingStatus(boolean primaryNetbankingStatus) {
		PrimaryNetbankingStatus = primaryNetbankingStatus;
	}

	public String getTxnKey() {
		return txnKey;
	}

	public void setTxnKey(String txnKey) {
		this.txnKey = txnKey;
	}

	public String getAcquirerName() {
		return acquirerName;
	}

	public void setAcquirerName(String acquirerName) {
		this.acquirerName = acquirerName;
	}	
	
	
	public Set<ChargingDetails> getChargingDetails() {
		return chargingDetails;
	}

	public void setChargingDetails(Set<ChargingDetails> chargingDetails) {
		this.chargingDetails = chargingDetails;
	}
	
	public Set<TdrSetting> getTdrSetting() {
		return tdrSetting;
	}

	public void setTdrSetting(Set<TdrSetting> chargingDetails) {
		this.tdrSetting = chargingDetails;
	}

	public Set<AccountCurrency> getAccountCurrencySet() {
		return accountCurrencySet;
	}

	public void setAccountCurrencySet(Set<AccountCurrency> accountCurrencySet) {
		this.accountCurrencySet = accountCurrencySet;
	}

	public void addAccountCurrency(AccountCurrency accountCurrency){
//		logger.info("ACCOUNT CURRENCY in Account:::::"+accountCurrency);
		accountCurrencySet.add(accountCurrency);
	}
	
	public void removeChargingDetail(AccountCurrency accountCurrency){
		accountCurrencySet.remove(accountCurrency);
	}

	
	
	
	

	@Override
	public String toString() {
		return "Account [id=" + id + ", merchantId=" + merchantId + ", password=" + password + ", acquirerPayId="
				+ acquirerPayId + ", primaryStatus=" + primaryStatus + ", PrimaryNetbankingStatus="
				+ PrimaryNetbankingStatus + ", txnKey=" + txnKey + ", acquirerName=" + acquirerName + ", payments="
				+ payments + ", accountCurrencySet=" + accountCurrencySet + ", chargingDetails=" + chargingDetails
				+ "]";
	}
	

}
