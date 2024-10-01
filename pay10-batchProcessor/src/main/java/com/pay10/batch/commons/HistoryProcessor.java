package com.pay10.batch.commons;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.batch.commons.util.Processor;
import com.pay10.batch.exception.DatabaseException;

@Service("historyProcessor")
public class HistoryProcessor implements Processor {
	@Autowired
	private Historian historian;
	
	public void preProcess(Fields fields) throws DatabaseException {
		historian.findPrevious(fields);
	}

	public void process(Fields fields) throws DatabaseException {
		
	}

	public void postProcess(Fields fields) {
		
	}
}
