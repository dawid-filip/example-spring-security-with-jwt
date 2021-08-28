package com.pl.df.configuration;

import java.net.URI;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class WebUtility {
	
	public static URI createURI(String path) {
		return URI.create(ServletUriComponentsBuilder.fromCurrentContextPath()
				.path(path)
				.toUriString());
	}
	
}
