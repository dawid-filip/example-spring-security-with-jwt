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
	public Docket get() {
		return new Docket(DocumentationType.SWAGGER_2)
				.enable(true)
				.select()
				//.paths(PathSelectors.any())
				.paths(PathSelectors.ant("/api/**"))
				.apis(RequestHandlerSelectors.basePackage("com.pl.df.api"))
				.build()
				.apiInfo(apiInfo());
	}

	private ApiInfo apiInfo() {
		return new ApiInfo(
				"REST API documentation", 
				"REST API description.", 
				"API TOS", 
				"Terms of service",
				new Contact("Tom T.", "www.tom-tom-t.com", "tomt@com"), "License of API", "API license URL",
				Collections.emptyList());
	}
	
	
}
