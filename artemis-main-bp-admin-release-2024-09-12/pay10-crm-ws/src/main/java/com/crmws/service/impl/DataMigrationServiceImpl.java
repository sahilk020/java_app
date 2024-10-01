package com.crmws.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmws.service.DataMigrationService;
import com.pay10.commons.repository.DataMigrationRepository;

@Service
public class DataMigrationServiceImpl implements DataMigrationService {
	private static final Logger logger = LoggerFactory.getLogger(DataMigrationServiceImpl.class.getName());

	@Autowired
	private DataMigrationRepository dataMigrationRepository;
	
	@Override
	public void makeQuery() {
		dataMigrationRepository.makeQuery();
	}
	
	
}
