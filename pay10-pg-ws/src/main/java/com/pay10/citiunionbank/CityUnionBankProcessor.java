package com.pay10.citiunionbank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.MopType;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.util.Processor;


@Service("cityunionProcessor")
public class CityUnionBankProcessor implements Processor {
	 CityUnionBankIntegrator cityUnionBankIntegrator;
	
	@Override
	public void preProcess(Fields fields) throws SystemException {
		//we now request this method right now
	
	}
	
	
	
	public CityUnionBankProcessor(CityUnionBankIntegrator cityUnionBankIntegrator) {
		super();
		this.cityUnionBankIntegrator = cityUnionBankIntegrator;
	}



	@Override
	public void process(Fields fields) throws SystemException {

		if ((fields.get(FieldType.TXNTYPE.getName()).equals(TransactionType.NEWORDER.getName())
				|| fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()).equals(TransactionType.STATUS.getName())
				|| fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()).equals(TransactionType.VERIFY.getName())
				|| fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()).equals(TransactionType.RECO.getName())
				|| fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName())
						.equals(TransactionType.REFUNDRECO.getName()))) 
		{
			return;
		}

		if (!fields.get(FieldType.ACQUIRER_TYPE.getName()).equals(AcquirerType.CITYUNIONBANK.getCode())) {
			return;
		}
		// if the MOP Type is GPay
		String paymentType = fields.get(FieldType.PAYMENT_TYPE.getName());
		String mopType = fields.get(FieldType.MOP_TYPE.getName());

		 if (paymentType.equals(PaymentType.NET_BANKING.getCode())) {
			
			 cityUnionBankIntegrator.process(fields);
		}
	}

	@Override
	public void postProcess(Fields fields) throws SystemException {
	//we now request this method right now
	}

	
}
