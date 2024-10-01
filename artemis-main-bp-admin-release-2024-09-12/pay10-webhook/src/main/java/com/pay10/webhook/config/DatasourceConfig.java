package com.pay10.webhook.config;

import java.sql.SQLException;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.pay10.commons.util.PropertiesManager;

@Configuration
public class DatasourceConfig {
	
	PropertiesManager propertiesManager = new PropertiesManager();
	
	@Bean
	public DataSource dataSource() throws IllegalArgumentException, NamingException, SQLException {
		PropertiesManager props = new PropertiesManager();
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(propertiesManager.propertiesMap.get("DBDriver"));
		dataSource.setUrl(propertiesManager.propertiesMap.get("DBURL"));
		dataSource.setUsername(propertiesManager.propertiesMap.get("DBUser"));
		dataSource.setPassword(propertiesManager.propertiesMap.get("DBPassword"));

		return dataSource;
	}

}
