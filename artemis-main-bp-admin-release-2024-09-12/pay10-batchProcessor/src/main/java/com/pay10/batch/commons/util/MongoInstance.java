package com.pay10.batch.commons.util;

import com.mongodb.MongoException;
import com.pay10.commons.slack.SlackUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

@Service("mongoInstance")
public class MongoInstance {


	private static final Logger logger = LoggerFactory.getLogger(MongoInstance.class);

	@Autowired
	@Qualifier("mongoClientBatch")
	MongoClient mClient;

	@Autowired
	private ConfigurationProvider configProvider;



	public MongoDatabase getDB() {

		try {
			return mClient.getDatabase(configProvider.getName());
		}catch (MongoException e)
		{
			e.printStackTrace();
			logger.error("Exception While connecting to mongo server {}",e.getMessage());
			SlackUtil.sendMongoDbConnectionResetAlert(null);
		}
		throw new RuntimeException();

	}
}
