package com.pl.df.configuration;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfig {
	
	// http://localhost:8088/swagger-ui/
	// http://localhost:8088/v2/api-docs
	
	@Bean
	public Docket getDocket() {
		return new Docket(DocumentationType.SWAGGER_2)
				.enable(true)
				.select()
				.paths(PathSelectors.ant("/api/**"))			// add all doc APIs to swagger located at /api/**
				.apis(RequestHandlerSelectors.basePackage("com.pl.df.api"))
				.build()
				.apiInfo(apiInfo());
	}

	private ApiInfo apiInfo() {
		return new ApiInfo(
				"Example JWT REST API documentation", 
				"Example JWT REST API description", 
				"REST API v2", 
				"Terms of service",
				new Contact("DF.", "www.df.com", "df@df.pl"), 
				"License of API", 
				"API license URL",
				Collections.emptyList());
	}
	
	
}
