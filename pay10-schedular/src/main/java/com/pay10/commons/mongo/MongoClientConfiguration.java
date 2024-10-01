package com.pay10.commons.mongo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.PasswordGenerator;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.scheduler.commons.ConfigurationProvider;

@Configuration
public class MongoClientConfiguration {

	@Autowired
	private ConfigurationProvider configurationProvider;

	@Bean
	public MongoClient getMongoClient() {
		String decryptedPass =PasswordGenerator.getDeryptedPassword(configurationProvider.getMONGO_DB_password(),
				PropertiesManager.propertiesMap.get(Constants.ENC_DEK.getValue()));

		
		String mongoURL = configurationProvider.getMONGO_DB_mongoURIprefix()
				+ configurationProvider.getMONGO_DB_username() +":"+ decryptedPass
				+ configurationProvider.getMONGO_DB_mongoURIsuffix();

		MongoClientURI mClientURI = new MongoClientURI(mongoURL);
		return new MongoClient(mClientURI);
	}
}