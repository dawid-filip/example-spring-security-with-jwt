package com.pl.df.filer;

import static com.pl.df.configuration.JwtUtility.getDecodedJWT;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.interfaces.DecodedJWT;

import lombok.extern.log4j.Log4j2;

import static com.pl.df.configuration.JwtUtility.setHttpErrorResponse;
import static com.pl.df.configuration.JwtUtility.BEARER;

@Log4j2
public class CustomAuthorizationFilter extends OncePerRequestFilter {

	// intercept every request which goes into application
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		if (isAllowedPath(request)) {
			log.info(request.getServletPath() + " doFilter(request, response)...");
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
					setHttpErrorResponse(403, "Error login into application: " + e.getMessage(), response);
				}
			} else {
				filterChain.doFilter(request, response);
			}
		}
	}
	
	private boolean isAllowedPath(HttpServletRequest request) {
		return request.getServletPath().equals("/api/login") || 
				request.getServletPath().equals("/api/tokens/refresh") ||
				request.getServletPath().equals("/api/users/registration") ||
				request.getServletPath().equals("/api/logout") ||
				
				// for swagger:
				request.getServletPath().equals("/swagger-ui") ||
				request.getServletPath().equals("/swagger-resources") ||
				request.getServletPath().equals("/v2/api-docs");
	}
	
}
