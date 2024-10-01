package com.pay10.commons.mongo;

import com.mongodb.MongoException;
import com.pay10.commons.slack.SlackUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.client.MongoDatabase;
import com.pay10.scheduler.commons.ConfigurationProvider;

@Service
public class MongoInstance {

	private static Logger logger = LoggerFactory.getLogger(MongoInstance.class.getName());

	@Autowired
	private MongoClientConfiguration mongoClientConfiguration ;
	
	@Autowired
	private ConfigurationProvider configurationProvider;


	public MongoDatabase getDB() {

		try {
			return mongoClientConfiguration.getMongoClient()
					.getDatabase(configurationProvider.getMONGO_DB_dbName());
		}catch (MongoException e)
		{
			e.printStackTrace();
			logger.error("Exception While connecting to mongo server {}",e.getMessage());
			SlackUtil.sendMongoDbConnectionResetAlert(null);
		}
		throw new RuntimeException();

	}
}