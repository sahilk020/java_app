package com.pay10.pg.core.pageintegrator;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.Fields;

/**
 * @author Sunil
 *
 */
public interface Customizer {

	public String integrate(Fields fields) throws SystemException;
}
