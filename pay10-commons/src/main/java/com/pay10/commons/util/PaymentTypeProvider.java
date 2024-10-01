package com.pay10.commons.util;

import com.pay10.commons.user.TdrSetting;
import com.pay10.commons.user.TdrSettingDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author Neeraj,VJ,Shaiwal
 *
 */
@Service("paymentTypeProvider")
public class PaymentTypeProvider {

/*	public PaymentTypeProvider(String payId){
		setSupportedPaymentOptions(payId);
	}*/
	
	@Autowired
	private StaticDataProvider staticDataProvider;
	
	@Autowired
	private PropertiesManager propertiesManager;
	
	@Autowired
	private TdrSettingDao dao;
	

	public PaymentTypeTransactionProvider setSupportedPaymentOptions(String payId){
		
		PaymentTypeTransactionProvider paymentTypeTransactionProvider = new PaymentTypeTransactionProvider();
		
		List<TdrSetting> chargingDetailsList = new ArrayList<TdrSetting>();
		
		if (propertiesManager.propertiesMap.get("useStaticData") != null
				&& propertiesManager.propertiesMap.get("useStaticData").equalsIgnoreCase("Y")) {
			
			chargingDetailsList = dao.getChargingDetailsList(payId);
			
		}
		else {
			chargingDetailsList = new TdrSettingDao().getAllActiveTdrSettingDetails(payId);
		}
		
		paymentTypeTransactionProvider.tdrSettings = chargingDetailsList;

		Set<MopType> mopListCC= new HashSet<MopType>();		
		Set<MopType> mopListDC= new HashSet<MopType>();
		Set<MopType> mopListPC= new HashSet<MopType>();
		//changes done by vj
		TreeSet<MopType> mopListNB=new TreeSet<MopType>();
		Set<MopType> listNb=new TreeSet<>();
		mopListNB.stream().sorted(java.util.Comparator.comparing(MopType::getName));
		//end
		Set<MopType> mopListWL= new HashSet<MopType>();
		Set<MopType> mopListCards= new HashSet<MopType>();
		Set<MopType> mopListUpi= new HashSet<MopType>();
		Set<MopType> mopListAtl= new HashSet<MopType>();
		Set<MopType> mopListQR= new HashSet<MopType>();
		Set<MopType> mopListEmi= new HashSet<MopType>();

		for(TdrSetting chargingDetails:chargingDetailsList){
			//to exclude refund entries
			if(chargingDetails.getTransactionType()!=null && chargingDetails.getTransactionType().equals(TransactionType.REFUND.getCode())){
				continue;
			}
			PaymentType paymentType = PaymentType.getInstanceUsingStringValue(chargingDetails.getPaymentType());
			switch(paymentType){
			case CREDIT_CARD:
				mopListCC.add(MopType.getInstanceUsingStringValue1(chargingDetails.getMopType()));
				mopListCards.add(MopType.getInstanceUsingStringValue1(chargingDetails.getMopType()));
				break;
			case DEBIT_CARD:
				mopListDC.add(MopType.getInstanceUsingStringValue1(chargingDetails.getMopType()));
				mopListCards.add(MopType.getInstanceUsingStringValue1(chargingDetails.getMopType()));
				break;
			case NET_BANKING:
				mopListNB.add(MopType.getInstanceUsingStringValue1(chargingDetails.getMopType()));
				break;
			case WALLET:
				mopListWL.add(MopType.getInstanceUsingStringValue1(chargingDetails.getMopType()));
				break;
			case UPI:
				mopListUpi.add(MopType.getInstanceUsingStringValue1(chargingDetails.getMopType()));
				break;
//			case AD:
//				mopListAtl.add(chargingDetails.getMopType());
//				break;	
			case PREPAID_CARD:
				mopListPC.add(MopType.getInstanceUsingStringValue1(chargingDetails.getMopType()));
				break;
			case QRCODE:
				mopListQR.add(MopType.getInstanceUsingStringValue1(chargingDetails.getMopType()));
				break;
			case EMI:
				mopListEmi.add(MopType.getInstanceUsingStringValue1(chargingDetails.getMopType()));
				break;
			default:
				break;
			}
		}
		Map<String, Object> treeMap = new TreeMap<String, Object>();
		if(mopListCC.size()!=0){
			treeMap.put(PaymentType.CREDIT_CARD.getCode(), mopListCC);			
		}
		if(mopListDC.size()!=0){
			treeMap.put(PaymentType.DEBIT_CARD.getCode(), mopListDC);
		}
		if(mopListPC.size()!=0){
			treeMap.put(PaymentType.PREPAID_CARD.getCode(), mopListDC);
		}
		if(mopListQR.size()!=0){
			treeMap.put(PaymentType.QRCODE.getCode(), mopListQR);
		}
		if(mopListNB.size()!=0){
			treeMap.put(PaymentType.NET_BANKING.getCode(), mopListNB);
		}
		if(mopListWL.size()!=0){
			treeMap.put(PaymentType.WALLET.getCode(), mopListWL);
		}
		if(mopListUpi.size()!=0){
			treeMap.put(PaymentType.UPI.getCode(), mopListUpi);
		}
		if(!mopListEmi.isEmpty()){
			treeMap.put(PaymentType.EMI.getCode(), mopListEmi);
		}

//		if(mopListAtl.size()!=0){
//			treeMap.put(PaymentType.AD.getCode(), mopListAtl);
//		}
		if(mopListCards.size()!=0){
			paymentTypeTransactionProvider.supportedCardTypeMap.put(PaymentType.CREDIT_CARD.getCode(), mopListCards);
		}
		paymentTypeTransactionProvider.setSupportedPaymentTypeMap(treeMap);
		return paymentTypeTransactionProvider ; 
		
	}

public PaymentTypeTransactionProvider setSupportedPaymentOptionsNew(String payId,String currency){
		
		PaymentTypeTransactionProvider paymentTypeTransactionProvider = new PaymentTypeTransactionProvider();
		
		List<TdrSetting> chargingDetailsList = new ArrayList<TdrSetting>();
		
		if (propertiesManager.propertiesMap.get("useStaticData") != null
				&& propertiesManager.propertiesMap.get("useStaticData").equalsIgnoreCase("Y")) {
			
			chargingDetailsList = dao.getChargingDetailsList(payId);
			
		}
		else {
			chargingDetailsList = new TdrSettingDao().getAllActiveTdrSettingDetailsNew(payId,currency);
		}
		
		paymentTypeTransactionProvider.tdrSettings = chargingDetailsList;

		Set<MopType> mopListCC= new HashSet<MopType>();		
		Set<MopType> mopListDC= new HashSet<MopType>();
		Set<MopType> mopListPC= new HashSet<MopType>();
		//changes done by vj
		TreeSet<MopType> mopListNB=new TreeSet<MopType>();
		Set<MopType> listNb=new TreeSet<>();
		mopListNB.stream().sorted(java.util.Comparator.comparing(MopType::getName));
		//end
		Set<MopType> mopListWL= new HashSet<MopType>();
		Set<MopType> mopListCards= new HashSet<MopType>();
		Set<MopType> mopListUpi= new HashSet<MopType>();
		Set<MopType> mopListAtl= new HashSet<MopType>();
		Set<MopType> mopListQR= new HashSet<MopType>();
		Set<MopType> mopListEmi= new HashSet<MopType>();

		for(TdrSetting chargingDetails:chargingDetailsList){
			//to exclude refund entries
			if(chargingDetails.getTransactionType()!=null && chargingDetails.getTransactionType().equals(TransactionType.REFUND.getCode())){
				continue;
			}
			PaymentType paymentType = PaymentType.getInstanceUsingStringValue(chargingDetails.getPaymentType());
			switch(paymentType){
			case CREDIT_CARD:
				mopListCC.add(MopType.getInstanceUsingStringValue1(chargingDetails.getMopType()));
				mopListCards.add(MopType.getInstanceUsingStringValue1(chargingDetails.getMopType()));
				break;
			case DEBIT_CARD:
				mopListDC.add(MopType.getInstanceUsingStringValue1(chargingDetails.getMopType()));
				mopListCards.add(MopType.getInstanceUsingStringValue1(chargingDetails.getMopType()));
				break;
			case NET_BANKING:
				mopListNB.add(MopType.getInstanceUsingStringValue1(chargingDetails.getMopType()));
				break;
			case WALLET:
				mopListWL.add(MopType.getInstanceUsingStringValue1(chargingDetails.getMopType()));
				break;
			case UPI:
				mopListUpi.add(MopType.getInstanceUsingStringValue1(chargingDetails.getMopType()));
				break;
//			case AD:
//				mopListAtl.add(chargingDetails.getMopType());
//				break;	
			case PREPAID_CARD:
				mopListPC.add(MopType.getInstanceUsingStringValue1(chargingDetails.getMopType()));
				break;
			case QRCODE:
				mopListQR.add(MopType.getInstanceUsingStringValue1(chargingDetails.getMopType()));
				break;
			case EMI:
				mopListEmi.add(MopType.getInstanceUsingStringValue1(chargingDetails.getMopType()));
				break;
			default:
				break;
			}
		}
		Map<String, Object> treeMap = new TreeMap<String, Object>();
		if(mopListCC.size()!=0){
			treeMap.put(PaymentType.CREDIT_CARD.getCode(), mopListCC);			
		}
		if(mopListDC.size()!=0){
			treeMap.put(PaymentType.DEBIT_CARD.getCode(), mopListDC);
		}
		if(mopListPC.size()!=0){
			treeMap.put(PaymentType.PREPAID_CARD.getCode(), mopListDC);
		}
		if(mopListQR.size()!=0){
			treeMap.put(PaymentType.QRCODE.getCode(), mopListQR);
		}
		if(mopListNB.size()!=0){
			treeMap.put(PaymentType.NET_BANKING.getCode(), mopListNB);
		}
		if(mopListWL.size()!=0){
			treeMap.put(PaymentType.WALLET.getCode(), mopListWL);
		}
		if(mopListUpi.size()!=0){
			treeMap.put(PaymentType.UPI.getCode(), mopListUpi);
		}
		if(!mopListEmi.isEmpty()){
			treeMap.put(PaymentType.EMI.getCode(), mopListEmi);
		}

//		if(mopListAtl.size()!=0){
//			treeMap.put(PaymentType.AD.getCode(), mopListAtl);
//		}
		if(mopListCards.size()!=0){
			paymentTypeTransactionProvider.supportedCardTypeMap.put(PaymentType.CREDIT_CARD.getCode(), mopListCards);
		}
		paymentTypeTransactionProvider.setSupportedPaymentTypeMap(treeMap);
		return paymentTypeTransactionProvider ; 
		
	}

}
