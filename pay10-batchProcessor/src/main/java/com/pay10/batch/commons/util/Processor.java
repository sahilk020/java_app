package com.pay10.batch.commons.util;

import com.pay10.batch.commons.Fields;
import com.pay10.batch.exception.DatabaseException;

public interface Processor {

	public abstract void preProcess(Fields fields) throws DatabaseException;

	public abstract void process(Fields fields) throws DatabaseException;

	public abstract void postProcess(Fields fields);

}