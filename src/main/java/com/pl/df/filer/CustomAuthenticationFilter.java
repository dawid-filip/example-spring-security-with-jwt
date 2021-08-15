package com.pl.df.filer;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pl.df.configuration.JwtUtility;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private final AuthenticationManager authenticationManager; // calling to authenticate user
	
	public CustomAuthenticationFilter(AuthenticationManager authenticationManager) {
		//setFilterProcessesUrl("/api/login"); override default login path
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
		
		List<String> roles = user.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.collect(Collectors.toList());
		
		Map<String, String> tokens = 
				JwtUtility.getTokens(user.getUsername(), request.getRequestURI().toString(), roles);
		
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		new ObjectMapper().writeValue(response.getOutputStream(), tokens);
	}
	

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException failed) throws IOException, ServletException {
		super.unsuccessfulAuthentication(request, response, failed);
		// TODO: use to prevent brute force attack by for example adding counter of failed login attempts in some timerange
	}

	
	
}
