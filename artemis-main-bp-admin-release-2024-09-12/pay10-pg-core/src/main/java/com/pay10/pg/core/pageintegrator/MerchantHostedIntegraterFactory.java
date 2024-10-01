package com.pay10.pg.core.pageintegrator;

import com.pay10.commons.util.Fields;
import com.pay10.pg.core.util.Processor;

public class MerchantHostedIntegraterFactory {

	public static Processor instance(Fields fields){
		return new MerchantHostedIntegrater();
	}
}
