package com.pl.df;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

public class JwtUtility {
	
	public static Algorithm getAlgorithm() {
		Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
		return algorithm;
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
		tokens.put("access_token", access_token);
		tokens.put("refresh_token", refresh_token);
		
		return tokens;
	}
	
}
