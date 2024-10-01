package com.pay10.commons.util;

import org.bson.Document;

import com.pay10.commons.user.Resellerdailyupdate;

public class ResellerdailyupdateUtilities {
	public static Resellerdailyupdate getResellerdailyupdateFromDoc(Document doc) {
		Resellerdailyupdate resellerdailyupdate = new Resellerdailyupdate();
		resellerdailyupdate.setId(doc.getLong(CrmFieldType.ID.getName()));
		resellerdailyupdate.setAdded_By(doc.getString(CrmFieldType.UPDATED_BY.getName()));
		resellerdailyupdate.setAddedOn(doc.getString(CrmFieldType.UPDATE_DATE.getName()));
		resellerdailyupdate.setAmount(doc.getDouble(CrmFieldType.NET_AMOUNT.getName()));
		resellerdailyupdate.setIsDeleted(doc.getString(CrmFieldType.IS_DELETED.getName()));
		resellerdailyupdate.setMOP(doc.getString(CrmFieldType.MOP_TYPE.getName()));
		resellerdailyupdate.setReseller_payId(doc.getString(CrmFieldType.RESELLER_ID.getName()));
		resellerdailyupdate.setSaleamount(doc.getDouble(CrmFieldType.SALE_AMOUNT.getName()));
		resellerdailyupdate.setTotalChargeback(doc.getDouble(CrmFieldType.TOTAL_CHARGEBACK.getName()));
		resellerdailyupdate.setTotalRefund(doc.getDouble(CrmFieldType.TOTAL_REFUND.getName()));
		//resellerdailyupdate.setTotatlSuccessTransaction(doc.getString(CrmFieldType.TOTAL_SUCCESS_TRANSACTION.getName()));
		resellerdailyupdate.setTransDate(doc.getString(CrmFieldType.TXN_DATE.getName()));
		resellerdailyupdate.setTransType(doc.getString(CrmFieldType.PAYMENT_TYPE.getName()));
		return resellerdailyupdate;
	}

	public static Document getDocFromresellerdailyupdate(Resellerdailyupdate resellerdailyupdate) {
		Document docBuilder = new Document();
		docBuilder.put(CrmFieldType.ID.getName(), resellerdailyupdate.getId());
		docBuilder.put(CrmFieldType.UPDATED_BY.getName(), resellerdailyupdate.getAdded_By());
		docBuilder.put(CrmFieldType.UPDATE_DATE.getName(), resellerdailyupdate.getAddedOn());
		docBuilder.put(CrmFieldType.NET_AMOUNT.getName(), resellerdailyupdate.getAmount());
		docBuilder.put(CrmFieldType.IS_DELETED.getName(), resellerdailyupdate.getIsDeleted());
		docBuilder.put(CrmFieldType.MOP_TYPE.getName(), resellerdailyupdate.getMOP());
		//docBuilder.put(CrmFieldType.RESELLER_ID.getName(), resellerdailyupdate.getReseller_payId());
		docBuilder.put(CrmFieldType.SALE_AMOUNT.getName(), resellerdailyupdate.getSaleamount());
		docBuilder.put(CrmFieldType.TOTAL_CHARGEBACK.getName(), resellerdailyupdate.getTotalChargeback());
		docBuilder.put(CrmFieldType.TOTAL_REFUND.getName(), resellerdailyupdate.getTotalRefund());
		docBuilder.put(CrmFieldType.TXN_DATE.getName(), resellerdailyupdate.getTransDate());
		docBuilder.put(CrmFieldType.PAYMENT_TYPE.getName(), resellerdailyupdate.getTransType());
		docBuilder.put(CrmFieldType.MERCHANT_ID.getName(), resellerdailyupdate.getMerchant_payId());
		//docBuilder.put(CrmFieldType.RESELLER_COMMISION.getName(), resellerdailyupdate.getCommisionamount());
		docBuilder.put(CrmFieldType.SMA_COMMISSION.getName(), resellerdailyupdate.getSmacommisionamount());
		docBuilder.put(CrmFieldType.MA_COMMISSION.getName(), resellerdailyupdate.getMacommisionamount());
		docBuilder.put(CrmFieldType.AGENT_COMMISSION.getName(), resellerdailyupdate.getAgentcommisionamount());
		docBuilder.put(CrmFieldType.SMA_ID.getName(), resellerdailyupdate.getSMA_payId());
		docBuilder.put(CrmFieldType.MA_ID.getName(), resellerdailyupdate.getMA_payId());
		docBuilder.put(CrmFieldType.AGENT_ID.getName(), resellerdailyupdate.getAgent_payId());
        docBuilder.put(CrmFieldType.CURRENCY.getName(), resellerdailyupdate.getCurrency());

		return docBuilder;
	}

	public static Document getDocFromresellerdailyupdateForDelete(Resellerdailyupdate resellerdailyupdate) {
		Document docBuilder = new Document();
		docBuilder.put(CrmFieldType.TXN_DATE.getName(), resellerdailyupdate.getTransDate());
		docBuilder.put(CrmFieldType.MERCHANT_ID.getName(), resellerdailyupdate.getMerchant_payId());
		docBuilder.put(CrmFieldType.SMA_COMMISSION.getName(), resellerdailyupdate.getSmacommisionamount());
		docBuilder.put(CrmFieldType.MA_COMMISSION.getName(), resellerdailyupdate.getMacommisionamount());
		docBuilder.put(CrmFieldType.MOP_TYPE.getName(), resellerdailyupdate.getMOP());
		docBuilder.put(CrmFieldType.PAYMENT_TYPE.getName(), resellerdailyupdate.getTransType());
		docBuilder.put(CrmFieldType.CURRENCY.getName(), resellerdailyupdate.getCurrency());
		return docBuilder;
	}

}
