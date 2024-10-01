package com.pay10.pg.core.pageintegrator;

import com.pay10.commons.util.Fields;
import com.pay10.pg.core.util.Processor;

/**
 * @author Sunil
 *
 */
public class AcquireIntegratorFactory {

	public static Processor instance(Fields fields){
		return new AcquireIntegrator();
	}
}
