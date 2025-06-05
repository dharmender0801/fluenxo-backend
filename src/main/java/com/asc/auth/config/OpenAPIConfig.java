package com.asc.auth.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
@SecurityScheme(name = "bearerAuth", description = "JWT auth description", scheme = "bearer", type = SecuritySchemeType.HTTP, bearerFormat = "JWT", in = SecuritySchemeIn.HEADER)
public class OpenAPIConfig {
	@Value("${open.api.dev-url:localhost:8080}")
	private String devUrl;
	@Value("${open.api.stg-url:localhost:8080}")
	private String stgUrl;
	@Value("${open.api.prod-url:localhost:8080}")
	private String prodUrl;

	@Value("${open.api.title:Management APIs}")
	private String title;
	@Value("${open.api.version:1.0.0}")
	private String version;
	@Value("${open.api.description:These APIs exposes endpoint.}")
	private String description;
	@Value("${open.api.terms:https://www.bikerz.pro/terms}")
	private String termsOfService;

	@Bean
	public OpenAPI myOpenAPI() {

		Server devServer = new Server();
		devServer.setUrl(devUrl);
		devServer.setDescription("Server URL in Development environment");

		Server stgServer = new Server();
		stgServer.setUrl(stgUrl);
		stgServer.setDescription("Server URL in Staging environment");

		Server prodServer = new Server();
		prodServer.setUrl(prodUrl);
		prodServer.setDescription("Server URL in Production environment");

		Contact contact = new Contact();
		contact.setEmail("kumardharm234@gmail.com");
		contact.setName("Dharmender Kumar");
		contact.setUrl("https://www.bikerz.pro");

		License mitLicense = new License().name("MIT License").url("https://choosealicense.com/licenses/mit/");
		SecurityRequirement securityRequirement = new SecurityRequirement();
		securityRequirement.addList("bearerAuth");

		Info info = new Info().title(title).version(version).contact(contact).description(description)
				.termsOfService(termsOfService).license(mitLicense);
		return new OpenAPI().info(info).servers(List.of(devServer, stgServer, prodServer))
				.addSecurityItem(securityRequirement);
	}
}