package com.pl.df.filer;

import static com.pl.df.configuration.JwtUtility.getDecodedJWT;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pl.df.configuration.JwtUtility;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class CustomAuthorizationFilter extends OncePerRequestFilter {

	private final String BEARER = "Bearer ";
	
	// intercept every request which goes into application
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		if (request.getServletPath().equals("/api/login") || 
				request.getServletPath().equals("/api/registration") ||
				request.getServletPath().equals("/api/logout") ||  
				request.getServletPath().equals("/api/token/refresh")) {
			log.info("/api/login doFilter(request, response)...");
			filterChain.doFilter(request, response);
		} else {
			String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
			if (authorizationHeader!=null && authorizationHeader.startsWith(BEARER)) {	// done only once (only if success)
				try {
					// at this point user has been already authenticated //
					DecodedJWT decodedJWT = getDecodedJWT(authorizationHeader);
					
					String username = decodedJWT.getSubject(); 
					List<String> roles = Arrays.asList(decodedJWT.getClaim("roles").asArray(String.class));
					
					Collection<SimpleGrantedAuthority> authorities =
							roles.stream().map(role -> new SimpleGrantedAuthority(role)).collect(Collectors.toList());
					
					UsernamePasswordAuthenticationToken authenticationToken = 
							new UsernamePasswordAuthenticationToken(username, null, authorities);
					SecurityContextHolder.getContext().setAuthentication(authenticationToken); // with this we can say spring: here you have username/roles etc.
				
					filterChain.doFilter(request, response);
				} catch (Exception e) {
					String errorMessage = "Error login into application: " + e.getMessage();
					log.error(errorMessage);
					response.setHeader("error", errorMessage);
					response.setStatus(403); // Forbidden
					//response.sendError(403); // Forbidden
					
					Map<String, String> errors = new HashMap<>();
					errors.put(JwtUtility.ERROR_MESSAGE, errorMessage);
					response.setContentType(MediaType.APPLICATION_JSON_VALUE);
					new ObjectMapper().writeValue(response.getOutputStream(), errors);
				}
			} else {
				filterChain.doFilter(request, response);
			}
		}
	}

}










