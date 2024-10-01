package com.pay10.webhook.config;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.pay10.commons.util.Constants;
import com.pay10.commons.util.PropertiesManager;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = MongoConfig.PREFIX_KEY)
@Getter
@Setter
public class MongoConfig {
	
	public static final String PREFIX_KEY = "consumer.mongo";

	public static final String ACTIVATION_KEY = PREFIX_KEY + ".active";
	
	private static final String PROPS_PREFIX = "MONGO_DB_";
	
	private boolean active;
	
	private String database ;
	
	private String collection;
	
	private PropertiesManager propertiesManager = new PropertiesManager();
	
	@PostConstruct
	private void init() {
		this.database = propertiesManager.propertiesMap.get(PROPS_PREFIX+Constants.DB_NAME.getValue());
		this.collection = propertiesManager.propertiesMap.get(PROPS_PREFIX+Constants.COLLECTION_NAME.getValue());
	}
	

}
