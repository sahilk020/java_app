package com.crmws.mongo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoTypeMapper;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.PasswordGenerator;
import com.pay10.commons.util.PropertiesManager;
@Configuration
public class CrmWSAppConfig {
	private static final String prefix = "MONGO_DB_";

	
	@Bean
	@Qualifier("mongoClientWs")
	@Primary
	public MongoClient getMongoClientWs() {
		PropertiesManager p = new PropertiesManager();
		
		
		String decryptedPass =PasswordGenerator.getDeryptedPassword(PropertiesManager.propertiesMap.get(prefix+Constants.MONGO_PASSWORD.getValue()),
				PropertiesManager.propertiesMap.get(Constants.ENC_DEK.getValue()));
		
		String mongoURL = PropertiesManager.propertiesMap.get(prefix + Constants.MONGO_URI_PREFIX.getValue())
				+ PropertiesManager.propertiesMap.get(prefix + Constants.MONGO_USERNAME.getValue()) + ":"
				+ decryptedPass
				+ PropertiesManager.propertiesMap.get(prefix + Constants.MONGO_URI_SUFFIX.getValue());
		MongoClientURI mClientURI = new MongoClientURI(mongoURL);
		return new MongoClient(mClientURI);
	}
	
	 @Bean("crm-ws-mongo-client")
	    public MongoClient getMongoClient() {
	        PropertiesManager p = new PropertiesManager();
	        //DD
	    	String decryptedPass =PasswordGenerator.getDeryptedPassword(PropertiesManager.propertiesMap.get(prefix+Constants.MONGO_PASSWORD.getValue()),
					PropertiesManager.propertiesMap.get(Constants.ENC_DEK.getValue()));
	        String mongoURL = PropertiesManager.propertiesMap.get(prefix+ Constants.MONGO_URI_PREFIX.getValue())+
	                PropertiesManager.propertiesMap.get(prefix+Constants.MONGO_USERNAME.getValue())+":"+
	                decryptedPass+
	                PropertiesManager.propertiesMap.get(prefix+Constants.MONGO_URI_SUFFIX.getValue());

	        MongoClientURI mClientURI = new MongoClientURI(
	                mongoURL);
	        return new MongoClient(mClientURI);
	    }
	    
	    @Qualifier("crm-ws-mongo-client")
	    @Autowired
	    public @Bean
	    MongoDbFactory mongoDbFactory(MongoClient appConfigMongoClient) {
			return new SimpleMongoDbFactory(appConfigMongoClient, PropertiesManager.propertiesMap.get(prefix+Constants.DB_NAME.getValue()));
	    }

	    public @Bean
	    MongoTemplate mongoTemplate() {
	        MongoTypeMapper typeMapper = new DefaultMongoTypeMapper(null);
	        MappingMongoConverter converter = new MappingMongoConverter(mongoDbFactory(getMongoClient()), new MongoMappingContext());
	        converter.setTypeMapper(typeMapper);
	        return new MongoTemplate(mongoDbFactory(getMongoClient()), converter);
	    }
	
}
