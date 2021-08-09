package com.pl.df.filer;

import java.io.IOException;
import java.sql.Date;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private final AuthenticationManager authenticationManager; // calling to authenticate user
	public CustomAuthenticationFilter(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}
	
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
		String username = request.getParameter("username");		// Json/ObjectMapper+RequestBody data can be used instead
		String password = request.getParameter("password");
		log.info("Username is: {}, Password is: {}.", username, password);
		UsernamePasswordAuthenticationToken authenticationToke = new UsernamePasswordAuthenticationToken(username, password);
		return authenticationManager.authenticate(authenticationToke);
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication athentication) throws IOException, ServletException {
		User user = (User)athentication.getPrincipal();
		Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());	// for sign json web tocket and refresh tocket; secret shuld be somewhere secured and encrypted
		
		String access_token = JWT.create()
				.withSubject(user.getUsername())	// unique identyfier for user; in this case username will be unique
				.withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
				.withIssuer(request.getRequestURI().toString())  // any string (like organization)
				.withClaim("roles", user.getAuthorities().stream()
										.map(GrantedAuthority::getAuthority)
										.collect(Collectors.toList())
					)
				.sign(algorithm);
		
		
		String refresh_token = JWT.create()
				.withSubject(user.getUsername())	// unique identyfier for user; in this case username will be unique
				.withExpiresAt(new Date(System.currentTimeMillis() + 30 * 60 * 1000)) // more time like week, month etc.
				.withIssuer(request.getRequestURI().toString())  // any string (like organization)
				.sign(algorithm);
		
		response.setHeader("access_token", access_token);
		response.setHeader("refresh_token", refresh_token);
		
	}
	

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException failed) throws IOException, ServletException {
		super.unsuccessfulAuthentication(request, response, failed);
		// TODO: use to prevent brute force attack by for example adding counter of failed login attempts in some timerange
	}

	
	
}
