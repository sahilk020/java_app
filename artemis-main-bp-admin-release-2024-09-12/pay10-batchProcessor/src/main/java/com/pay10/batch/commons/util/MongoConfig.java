package com.pay10.batch.commons.util;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.pay10.commons.util.PasswordGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class MongoConfig {

    private static final Logger logger = LoggerFactory.getLogger(MongoConfig.class);

    @Autowired
    private ConfigurationProvider configProvider;

    @Bean("mongoClientBatch")
    @Primary
    public MongoClient getMongoClient() {
        logger.info("Inside MongoClient Function ::");
        logger.info("Password :: "+configProvider.getPassword());
        logger.info("EncryptedDek :: "+configProvider.getEncryptedDek());
        String decryptedPass = PasswordGenerator.getDeryptedPassword(configProvider.getPassword(), configProvider.getEncryptedDek());
        //PropertiesManager.propertiesMap.get(Constants.ENC_DEK.getValue()));
        logger.info("decryptedPass :: "+decryptedPass);
            String mongoUrl = configProvider.getMongoURIprefix()+configProvider.getUsername()+":"+decryptedPass+configProvider.getMongoURIsuffix();

            logger.info("Mongo Url Created : " + mongoUrl);
            MongoClientURI mClientURI = new MongoClientURI(mongoUrl);
            logger.info("MongoClientURI : " + mClientURI.getCollection());
            logger.info("MongoClient Created");
        return new MongoClient(mClientURI);
    }
}
