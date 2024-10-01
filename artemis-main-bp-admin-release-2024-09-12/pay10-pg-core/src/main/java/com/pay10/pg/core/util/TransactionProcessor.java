package com.pay10.pg.core.util;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.Fields;

public interface TransactionProcessor {

	public void transact(Fields fields) throws SystemException;

}
