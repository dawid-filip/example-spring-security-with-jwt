package com.pl.df;

import com.auth0.jwt.algorithms.Algorithm;

public class JwtUtility {
	
	// JwtUtility.getAlgorithm();
	public static Algorithm getAlgorithm() {
		Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
		return algorithm;
	}
	
}
