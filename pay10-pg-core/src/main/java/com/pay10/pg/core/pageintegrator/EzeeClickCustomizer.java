package com.pay10.pg.core.pageintegrator;

import org.springframework.stereotype.Service;

import com.opensymphony.xwork2.Action;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.TransactionType;
@Service
public class EzeeClickCustomizer implements Customizer{

	@Override
	public String integrate(Fields fields) {
		fields.put(FieldType.TXNTYPE.getName(),TransactionType.SALE.getName());
		fields.logAllFields("All Response fields Recieved");
		return Action.NONE;
	}
}
