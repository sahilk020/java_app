package com.pay10.crm;

import org.springframework.boot.builder.SpringApplicationBuilder;

public class ServletInitializer {

	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(Pay10CrmApplication.class);
	}
}
