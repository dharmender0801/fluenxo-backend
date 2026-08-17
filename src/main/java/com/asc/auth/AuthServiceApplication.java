package com.asc.auth;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springdoc.core.configuration.SpringDocKotlinConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication(exclude = SpringDocKotlinConfiguration.class)
@EnableAutoConfiguration
@EnableJpaRepositories
@EnableWebMvc
@PropertySource(ignoreResourceNotFound = true, value = {
		"file:${" + AuthServiceApplication.PROPERTIES_LOCATION_ENV + "}/" + AuthServiceApplication.APPLICATION_PROPERTY
				+ ".properties",
		"file:${" + AuthServiceApplication.PROPERTIES_LOCATION_ENV + "}/"
				+ AuthServiceApplication.ERROR_MESSAGES_PROPERTY + ".properties" })
@EnableSpringDataWebSupport
@Slf4j
@EnableCaching
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableAsync
@OpenAPIDefinition
public class AuthServiceApplication {

	public static final String PROPERTIES_LOCATION_ENV = "spring.config.location";
	public static final String APPLICATION_PROPERTY = "fluenxo-auth-service-application";
	public static final String ERROR_MESSAGES_PROPERTY = "fluenxo-auth-service-error-messages";
	protected static final List<String> PROPERTY_FILES = Arrays.asList(APPLICATION_PROPERTY, ERROR_MESSAGES_PROPERTY);
	public static final String PROPERTIES_FILE_NAME = String.join(",", PROPERTY_FILES);

	public static void main(String[] args) {
		String configLocation = System.getProperty(AuthServiceApplication.PROPERTIES_LOCATION_ENV, "classpath:/");
		String configPath = configLocation + " - " + AuthServiceApplication.PROPERTIES_FILE_NAME;
		log.info("Configpath: {}", configPath);
		if (StringUtils.isNotBlank(configLocation)) {
			ConfigurableApplicationContext applicationContext = new SpringApplicationBuilder(
					AuthServiceApplication.class)
					.properties("spring.config.name:" + AuthServiceApplication.PROPERTIES_FILE_NAME,
							"spring.config.location:" + AuthServiceApplication.PROPERTIES_LOCATION_ENV,
							"spring.config.location:optional:classpath:/,optional:classpath:/config/properties/")
					.build().run(args);
			applicationContext.getEnvironment();
		} else {
			SpringApplication.run(AuthServiceApplication.class, args);
		}
	}

}
