package com.pay10.crm.actionBeans;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.dao.MerchantAcquirerPropertiesDao;
import com.pay10.commons.user.AccountCurrencyRegion;
import com.pay10.commons.user.ChargingDetailsDao;
import com.pay10.commons.user.Surcharge;
import com.pay10.commons.user.SurchargeDao;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.user.ViewSurchargePopulator;
import com.pay10.commons.util.AcquirerTypeUI;
import com.pay10.commons.util.MopType;
import com.pay10.commons.util.PaymentType;

@Service
public class ViewSurchargeMappingDetailsFactory {


	@Autowired
	private SurchargeDao surchargeDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private ChargingDetailsDao chargingDetailsDao;

	@Autowired
	private MerchantAcquirerPropertiesDao merchantAcquirerPropertiesDao;
	
	private static Logger logger = LoggerFactory.getLogger(SurchargeMappingDetailsFactory.class.getName());

	public  Map<String, List<ViewSurchargePopulator>> getSurchargeAcquirerDetails(String payId,
			String acquirer) {
       List<MopType> mopTypeList=new ArrayList<MopType>();
       List<PaymentType> paymentTypeList=new ArrayList<PaymentType>();
       List<AccountCurrencyRegion> regionList =new ArrayList<AccountCurrencyRegion>();
       Map<String, List<ViewSurchargePopulator>> detailsMap = new HashMap<String, List<ViewSurchargePopulator>>();
   	
		List<String> acquirerList = Arrays.asList(acquirer.split(","));
		for (String acq : acquirerList) {
			if (StringUtils.isNotBlank(acq) && (acq.equals("IDFCFIRSTDATA"))) {
				acq = "IDFC_FIRSTDATA";
			}
			List<ViewSurchargePopulator> acquirerDetails = new ArrayList<ViewSurchargePopulator>();
	          String acquirerType=AcquirerTypeUI.getAcquirerNameByValues(acq);
	          if(StringUtils.isNotBlank(acquirerType) && (acquirerType.equals("iMudra Wallet")))
				{
					acquirerType="MatchMove Wallet";
				}
	          mopTypeList = surchargeDao.findMopType(payId,acquirerType);
	          paymentTypeList = surchargeDao.findPaymentType(payId,acquirerType);
			  regionList = surchargeDao.findCurrencyRegion(payId,acquirerType);
			  List<Surcharge> surchargeList = surchargeDao.findSurchargeDetails(payId,acquirerType);
		    
			  for(int i=0;i<regionList.size();i++)
			  {
				  for(int j=0;j<mopTypeList.size();j++)
				  {
					  for(int k=0;k<paymentTypeList.size();k++)
					  {
						   
						  String regionType = regionList.get(i).toString();
						  String mopType =  mopTypeList.get(j).name();
						  String paymentType = paymentTypeList.get(k).toString();
						  List<Surcharge> productList =surchargeList.stream().filter(p -> p.getPaymentsRegion().toString().equals(regionType))
								  .filter(p -> p.getMopType().toString().equals(mopType))
								  .filter(p -> p.getPaymentType().toString().equals(paymentType)).collect(Collectors.toList());
						  ViewSurchargePopulator viewSurchargePopulator = new ViewSurchargePopulator();
						  
						  if(productList.size()==1)
	    					 {
							  viewSurchargePopulator.setAcquirerName(productList.get(0).getAcquirerName());
							  viewSurchargePopulator.setMopType(productList.get(0).getMopType().getName());
							  viewSurchargePopulator.setPaymentType(productList.get(0).getPaymentType().getName());
							  viewSurchargePopulator.setPaymentsRegion(productList.get(0).getPaymentsRegion());

							  viewSurchargePopulator.setBankSurchargeAmountOffCommercial(BigDecimal.ZERO);
							  viewSurchargePopulator
										.setBankSurchargeAmountOnCommercial(productList.get(0).getBankSurchargeAmountCommercial());
							  viewSurchargePopulator.setBankSurchargePercentageOffCommercial(BigDecimal.ZERO);
							  viewSurchargePopulator.setBankSurchargePercentageOnCommercial(
									  productList.get(0).getBankSurchargePercentageCommercial());
							  viewSurchargePopulator.setBankSurchargeAmountOffCustomer(BigDecimal.ZERO);
							  viewSurchargePopulator
										.setBankSurchargeAmountOnCustomer(productList.get(0).getBankSurchargeAmountCustomer());
							  viewSurchargePopulator.setBankSurchargePercentageOffCustomer(BigDecimal.ZERO);
							  viewSurchargePopulator
										.setBankSurchargePercentageOnCustomer(productList.get(0).getBankSurchargePercentageCustomer());
							

						      acquirerDetails.add(viewSurchargePopulator);	  
							 
 						 	  
	    					 }
						  
						  else if (productList.size() == 2)
							{
							  if (productList.get(0).getOnOff().equalsIgnoreCase("1")) {
								  viewSurchargePopulator.setAcquirerName(productList.get(0).getAcquirerName());
								  viewSurchargePopulator.setMopType(productList.get(0).getMopType().getName());
								  viewSurchargePopulator.setPaymentType(productList.get(0).getPaymentType().getName());
								  viewSurchargePopulator.setStatus(productList.get(0).getStatus().getName());
								  viewSurchargePopulator.setPaymentsRegion(productList.get(0).getPaymentsRegion());
	
								  viewSurchargePopulator.setBankSurchargeAmountOffCommercial(productList.get(1).getBankSurchargeAmountCommercial());
								  viewSurchargePopulator
											.setBankSurchargeAmountOnCommercial(productList.get(0).getBankSurchargeAmountCommercial());
								  viewSurchargePopulator.setBankSurchargePercentageOffCommercial(productList.get(1).getBankSurchargePercentageCommercial());
								  viewSurchargePopulator.setBankSurchargePercentageOnCommercial(
										  productList.get(0).getBankSurchargePercentageCommercial());
								  viewSurchargePopulator.setBankSurchargeAmountOffCustomer(productList.get(1).getBankSurchargeAmountCustomer());
								  viewSurchargePopulator
											.setBankSurchargeAmountOnCustomer(productList.get(0).getBankSurchargeAmountCustomer());
								  viewSurchargePopulator.setBankSurchargePercentageOffCustomer(productList.get(1).getBankSurchargePercentageCustomer());
								  viewSurchargePopulator
											.setBankSurchargePercentageOnCustomer(productList.get(0).getBankSurchargePercentageCustomer());
								  
	
							      acquirerDetails.add(viewSurchargePopulator);
							  } else if (productList.get(0).getOnOff().equalsIgnoreCase("2")) {
								  viewSurchargePopulator.setAcquirerName(productList.get(1).getAcquirerName());
								  viewSurchargePopulator.setMopType(productList.get(1).getMopType().getName());
								  viewSurchargePopulator.setPaymentType(productList.get(1).getPaymentType().getName());
								  viewSurchargePopulator.setStatus(productList.get(1).getStatus().getName());
								  viewSurchargePopulator.setPaymentsRegion(productList.get(1).getPaymentsRegion());
	
								  viewSurchargePopulator.setBankSurchargeAmountOffCommercial(productList.get(0).getBankSurchargeAmountCommercial());
								  viewSurchargePopulator
											.setBankSurchargeAmountOnCommercial(productList.get(1).getBankSurchargeAmountCommercial());
								  viewSurchargePopulator.setBankSurchargePercentageOffCommercial(productList.get(0).getBankSurchargePercentageCommercial());
								  viewSurchargePopulator.setBankSurchargePercentageOnCommercial(
										  productList.get(1).getBankSurchargePercentageCommercial());
								  viewSurchargePopulator.setBankSurchargeAmountOffCustomer(productList.get(0).getBankSurchargeAmountCustomer());
								  viewSurchargePopulator
											.setBankSurchargeAmountOnCustomer(productList.get(1).getBankSurchargeAmountCustomer());
								  viewSurchargePopulator.setBankSurchargePercentageOffCustomer(productList.get(0).getBankSurchargePercentageCustomer());
								  viewSurchargePopulator
											.setBankSurchargePercentageOnCustomer(productList.get(1).getBankSurchargePercentageCustomer());
								  
	
							      acquirerDetails.add(viewSurchargePopulator);
							  }
						}
					  }
				  }
			  }
			  if (acquirerDetails.size() != 0) {
					// Collections.sort(surchargeDetailsList);
					detailsMap.put(acq, acquirerDetails);
				}

		}
		return detailsMap;
	}
		


}
