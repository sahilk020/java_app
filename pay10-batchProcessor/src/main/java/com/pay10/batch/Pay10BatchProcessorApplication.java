package com.pay10.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ImportResource;

/** Main class to start Reco Batch Application. */
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class, BatchAutoConfiguration.class })
@ImportResource({ "classpath:settleIntegrationConfig.xml", "classpath:settleBatchConfig.xml",
		"classpath:yamlConfig.xml", "classpath:refundIrctcBatchConfig.xml",
		"classpath:refundIrctcIntegrationConfig.xml", "classpath:refundTicketingBatchConfig.xml",
		"classpath:refundTicketingIntegrationConfig.xml", "classpath:upiBatchConfig.xml",
		"classpath:upiIntegrationConfig.xml" })
@EnableConfigurationProperties
public class Pay10BatchProcessorApplication {

	private static final Logger logger = LoggerFactory.getLogger(Pay10BatchProcessorApplication.class);

	public static void main(String[] args) {
		logger.info("Reco batch application started. ");
		SpringApplication.run(Pay10BatchProcessorApplication.class, args);
	}
}
