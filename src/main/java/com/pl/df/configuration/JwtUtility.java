package com.pl.df.configuration;

import java.io.IOException;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class JwtUtility {
	
	public static final String BEARER = "Bearer ";
	
	public static final String ACCESS_TOKEN = "access_token";
	public static final String REFRESH_TOKEN = "refresh_token";
	public static final String ERROR_MESSAGE = "error_message";
	
	public static DecodedJWT getDecodedJWT(String authorizationHeader) {
		String refresh_token = authorizationHeader.substring(BEARER.length());
		JWTVerifier verfier = JWT.require(getAlgorithm()).build();  // secret must be the same like during sign the token
		DecodedJWT decodedJWT = verfier.verify(refresh_token);
		return decodedJWT;
	}
	
	public static Map<String, String> getTokens(String subject, String issuer, List<?> roles) {
		String access_token = JWT.create()
				.withSubject(subject)				// unique identyfier for user; in this case username will be unique
				.withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
				.withIssuer(issuer)  				// any string (like organization)
				.withClaim("roles", roles)
				.sign(getAlgorithm());
		
		String refresh_token = JWT.create()
				.withSubject(subject)				// unique identyfier for user; in this case username will be unique
				.withExpiresAt(new Date(System.currentTimeMillis() + 30 * 60 * 1000)) // more time like week, month etc.
				.withIssuer(issuer)  				// any string (like organization)
				.sign(getAlgorithm());
		
		Map<String, String> tokens = new HashMap<>();
		tokens.put(ACCESS_TOKEN, access_token);
		tokens.put(REFRESH_TOKEN, refresh_token);
		
		return tokens;
	}

	public static Algorithm getAlgorithm() {
		return Algorithm.HMAC256("secret".getBytes()); // TODO: the secret should be kept secured way
	}
	
	public static void setHttpErrorResponse(int code, String errorMessage, HttpServletResponse response) {
		log.error(errorMessage);
		
		response.setHeader("error", errorMessage);
		response.setStatus(code);
//		response.sendError(code);
		
		Map<String, String> errors = new HashMap<>();
		errors.put(ERROR_MESSAGE, errorMessage);
		
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		try {
			new ObjectMapper().writeValue(response.getOutputStream(), errors);
		} catch (IOException ie) {
			log.error("IOException: " + ie.getMessage());
		}
		
	}
}
