package com.pay10.bindb.core;

import java.util.Map;

import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import com.pay10.commons.dao.BinRangeDao;
import com.pay10.commons.util.BinRange;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.MopType;
import com.pay10.commons.util.PaymentType;

@Service
@Scope(value="session", proxyMode=ScopedProxyMode.TARGET_CLASS)
public class BinRangeUpdater {

	@Autowired
	@Qualifier("binDbBinRangeDao")
	private BinRangeDao binRangeDao;
	private static Logger logger = LoggerFactory.getLogger(BinRangeUpdater.class.getName());

	public void addBinToDb(Map<String, String> binMap, String cardBin){
		try{
			BinRange binRange = new BinRange();
			binRange.setBinCodeHigh(cardBin);
			binRange.setCardType(PaymentType.getInstanceUsingCode(binMap.get(FieldType.PAYMENT_TYPE.getName())));
			binRange.setMopType(MopType.getmop(binMap.get(FieldType.MOP_TYPE.getName())));
			binRange.setIssuerBankName(binMap.get(FieldType.INTERNAL_CARD_ISSUER_BANK.getName()));
			binRange.setIssuerCountry(binMap.get(FieldType.INTERNAL_CARD_ISSUER_COUNTRY.getName()));
			binRangeDao.create(binRange);
		}catch(Exception exception){
			logger.error("Error inserting new bin to DB ", exception);
		}
		
	}
}
