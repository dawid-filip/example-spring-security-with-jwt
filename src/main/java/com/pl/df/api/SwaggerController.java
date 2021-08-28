package com.pl.df.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("/api/swagger")
@RequiredArgsConstructor
@Log4j2
public class SwaggerController {
	
	// http://localhost:8088/api/swagger-ui/index.html
	@GetMapping
	public RedirectView swaggerRedirect() {
		return new RedirectView("/api/swagger-ui/index.html");
	}
	
}
