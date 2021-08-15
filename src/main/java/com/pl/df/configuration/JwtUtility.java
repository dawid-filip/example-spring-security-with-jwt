package com.pl.df.configuration;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

public class JwtUtility {
	
	public static final String BEARER = "Bearer ";
	
	public static DecodedJWT getDecodedJWT(String authorizationHeader) {
		String refresh_token = authorizationHeader.substring(BEARER.length());
		JWTVerifier verfier = JWT.require(getAlgorithm()).build();  // secret must be the same like during sign the token
		DecodedJWT decodedJWT = verfier.verify(refresh_token);
		return decodedJWT;
	}
	
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
