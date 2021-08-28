package com.pl.df.api;

import static com.pl.df.configuration.JwtUtility.BEARER;
import static com.pl.df.configuration.JwtUtility.getDecodedJWT;
import static com.pl.df.configuration.JwtUtility.getTokens;
import static com.pl.df.configuration.JwtUtility.setHttpErrorResponse;
import static com.pl.df.configuration.JwtUtility.setTokensToResponseBodyAndHeaders;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pl.df.model.Role;
import com.pl.df.model.User;
import com.pl.df.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("/api/tokens")
@RequiredArgsConstructor
@Log4j2
public class TokenController {
	
	private final UserService userService;
	
	// http://localhost:8088/api/tokens/decode-access-token
	@GetMapping("/decode-access-token") 
	public ResponseEntity<?> decodeAccessToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
		var authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		
		if (authorizationHeader!=null && authorizationHeader.startsWith(BEARER)) {	// done only once (only if success)
			var access_token = authorizationHeader.substring(BEARER.length());
			var chunks = access_token.split("\\.");
			Base64.Decoder decoder = Base64.getDecoder();
	
			var header = new String(decoder.decode(chunks[0]));
			var payload = new String(decoder.decode(chunks[1]));

			var decodedToken = new HashMap<>();
			decodedToken.put("header", header);
			decodedToken.put("payload", payload);
			
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			new ObjectMapper().writeValue(response.getOutputStream(), decodedToken);
		}
		
		return ResponseEntity.ok().build();
	}
	
	// http://localhost:8088/api/tokens/refresh
	@GetMapping("/refresh")
	public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		
		if (authorizationHeader!=null && authorizationHeader.startsWith(BEARER)) {	// done only once (only if success)
			try {
				// at this point user has been already authenticated //
				DecodedJWT decodedJWT = getDecodedJWT(authorizationHeader);
				
				String username = decodedJWT.getSubject(); 
				User user = userService.getUser(username);
				
				if (user==null) {
					throw new UsernameNotFoundException("User " + username + " doesn't exist.");
				}
				
				List<String> roles = user.getRoles().stream()
						.map(Role::getName)
						.collect(Collectors.toList());
				
				Map<String, String> tokens = getTokens(user.getUsername(), request.getRequestURI().toString(), roles);
				
				setTokensToResponseBodyAndHeaders(tokens, response);
				
			} catch (Exception e) {
				setHttpErrorResponse(403, "Error login into application: " + e.getMessage(), response);
			}
		} else {
			throw new HttpClientErrorException(HttpStatus.FORBIDDEN, "Refresh token is missing!");
		}
		
	}
	
}
