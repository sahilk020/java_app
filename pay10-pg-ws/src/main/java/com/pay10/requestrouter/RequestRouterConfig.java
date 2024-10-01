package com.pay10.requestrouter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.pay10.commons.util.Fields;

@Configuration
public class RequestRouterConfig {

	@Bean
	public RequestRouter getRouter(Fields fields){
		return new RequestRouter(fields);
	}
}
