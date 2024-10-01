package com.pay10.commons.mongo;

import com.mongodb.MongoException;
import com.pay10.commons.slack.SlackUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.client.MongoDatabase;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.PropertiesManager;

@Service
public class MongoInstance {
	private static Logger logger = LoggerFactory.getLogger(MongoInstance.class.getName());
	@Autowired
	private MongoClientConfiguration mongoClientConfiguration ;

	private static final String prefix = "MONGO_DB_";

	public MongoDatabase getDB() {
		try {
			return mongoClientConfiguration.getMongoClient()
					.getDatabase(PropertiesManager.propertiesMap.get(prefix + Constants.DB_NAME.getValue()));
		}catch (MongoException e)
		{
			e.printStackTrace();
			logger.error("Exception While connecting to mongo server {}",e.getMessage());
			SlackUtil.sendMongoDbConnectionResetAlert(null);
		}
		throw new RuntimeException();
	}

	//private static MongoClient mClient;
/*
	private MongoClient getMongoClient() {

		if (mClient == null) {
			MongoClientURI mClientURI = new MongoClientURI(
					propertiesManager.getmongoDbParam(Constants.MONGO_URI.getValue()));
			mClient = new MongoClient(mClientURI);
		}
		return mClient;
	}

	public MongoDatabase getDB() {
		return getMongoClient().getDatabase(propertiesManager.getmongoDbParam(Constants.DB_NAME.getValue()));
	}*/

}
