package com.crmws.mongo;

import com.mongodb.MongoException;
import com.pay10.commons.slack.SlackUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.PropertiesManager;

@Service
public class CrmWSMongoInstance {

	private static Logger logger = LoggerFactory.getLogger(CrmWSMongoInstance.class.getName());

	private static final String prefix = "MONGO_DB_";

	@Autowired
	@Qualifier("mongoClientWs")
	private MongoClient mongoClientWs;



	public MongoDatabase getDB() {

		try {
			return mongoClientWs.getDatabase(PropertiesManager.propertiesMap.get(prefix+Constants.DB_NAME.getValue()));
		}catch (MongoException e)
		{
			e.printStackTrace();
			logger.error("Exception While connecting to mongo server {}",e.getMessage());
			SlackUtil.sendMongoDbConnectionResetAlert(null);
		}
		throw new RuntimeException();

	}


}
