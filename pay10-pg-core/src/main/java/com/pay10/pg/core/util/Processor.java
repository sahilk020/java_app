package com.pay10.pg.core.util;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.Fields;

public interface Processor {

	public abstract void preProcess(Fields fields) throws SystemException;

	public abstract void process(Fields fields) throws SystemException;

	public abstract void postProcess(Fields fields) throws SystemException;

}