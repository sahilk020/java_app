package com.crmws.config;

import java.sql.SQLException;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.web.client.RestTemplate;

import com.pay10.commons.util.PropertiesManager;

@Configuration
public class DatabaseConfig {

	private static final String PROPERTY_NAME_DATABASE_DRIVER = PropertiesManager.propertiesMap.get("DBDriver");
	private static final String PROPERTY_NAME_DATABASE_URL = PropertiesManager.propertiesMap.get("DBURL");
	private static final String PROPERTY_NAME_DATABASE_USERNAME = PropertiesManager.propertiesMap.get("DBUser");
	private static final String PROPERTY_NAME_DATABASE_PASSWORD = PropertiesManager.propertiesMap.get("DBPassword");

	@Bean
	public DataSource dataSource() throws IllegalArgumentException, NamingException, SQLException {
		PropertiesManager props = new PropertiesManager();
		DriverManagerDataSource dataSource = new DriverManagerDataSource();

		dataSource.setDriverClassName(PropertiesManager.propertiesMap.get("DBDriver"));
		dataSource.setUrl(PropertiesManager.propertiesMap.get("DBURL"));
		dataSource.setUsername(PropertiesManager.propertiesMap.get("DBUser"));
		dataSource.setPassword(PropertiesManager.propertiesMap.get("DBPassword"));

		return dataSource;
	}

	/*
	 * @Bean
	 * 
	 * @Primary public DataSource dataSource() { return
	 * DataSourceBuilder.create().username(PropertiesManager.propertiesMap.get(
	 * "DBUser")) .password(PropertiesManager.propertiesMap.get("DBPassword"))
	 * .url(PropertiesManager.propertiesMap.get("DBURL"))
	 * .driverClassName(PropertiesManager.propertiesMap.get("DBDriver")).build(); }
	 */

	@Bean
	public JdbcTemplate getJdbcTemplate() throws IllegalArgumentException, NamingException, SQLException {

		return new JdbcTemplate(dataSource());
	}

	/*
	 * @Bean("namedJdbcTemplate") public NamedParameterJdbcTemplate
	 * getNamedParameterJdbcTemplate() throws IllegalArgumentException,
	 * NamingException, SQLException {
	 * 
	 * return new NamedParameterJdbcTemplate(jndiDataSource()); }
	 */
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
