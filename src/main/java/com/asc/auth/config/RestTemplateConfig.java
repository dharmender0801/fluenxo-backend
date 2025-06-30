package com.asc.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
@ComponentScan
public class RestTemplateConfig {
	@Bean
	public RestTemplate restTemplate() {
		SimpleClientHttpRequestFactory simpleHttp = new SimpleClientHttpRequestFactory();
		simpleHttp.setConnectTimeout(60000);
		simpleHttp.setReadTimeout(60000);
		ClientHttpRequestFactory factory = new BufferingClientHttpRequestFactory(simpleHttp);
		RestTemplate restTemplate = new RestTemplate(factory);
		return restTemplate;
	}
}