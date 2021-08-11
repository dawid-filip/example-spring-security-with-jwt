package com.pl.df.api;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpHeaders;
import java.sql.Date;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pl.df.JwtUtility;
import com.pl.df.dto.RoleToUserForm;
import com.pl.df.model.Role;
import com.pl.df.model.User;
import com.pl.df.repository.RoleRepo;
import com.pl.df.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Log4j2
public class UserController {

	private final UserService userService;
	private final RoleRepo roleRepo;
	
	// http://localhost:8088/api/users
	@GetMapping("/users")
	public ResponseEntity<List<User>> getUsers() {
		return ResponseEntity.ok().body(userService.getUsers());
	}
	
	// http://localhost:8088/api/roles
	@GetMapping("/roles")
	public ResponseEntity<List<Role>> getRoles() {								// additional method
		return ResponseEntity.ok().body(roleRepo.findAll());
	}
	// http://localhost:8088/api/users/{id}
	@GetMapping("/users/{id}")
	public ResponseEntity<User> getUserById(@PathVariable("id") Long id) {	// additional method
		return ResponseEntity.ok().body(userService.getUserById(id));
	}
	// http://localhost:8088/api/roles/{id}
	@GetMapping("/roles/{id}")
	public ResponseEntity<Role> getRoleById(@PathVariable("id") Long id) {	// additional method
		return ResponseEntity.ok().body(roleRepo.getById(id));
	}
	
	// http://localhost:8088/api/users
	@PostMapping("/users")
	public ResponseEntity<User> saveUser(@RequestBody User user) {
		User savedUser = userService.saveUser(user);
		return ResponseEntity.created(createURI("/api/users/" + savedUser.getId())).body(savedUser);
	}
	
	// http://localhost:8088/api/role
	@PostMapping("/role")
	public ResponseEntity<Role> saveRole(@RequestBody Role role) {
		Role savedRole = userService.saveRole(role);
		return ResponseEntity.created(createURI("/api/role/" + savedRole.getId())).body(savedRole);
	}
	
	// http://localhost:8088/api/add-to-user
	@PostMapping("/role/add-to-user")
	public ResponseEntity<Void> addRoleToUser(@RequestBody RoleToUserForm roleToUserForm) {
		userService.addRoleToUser(roleToUserForm.getUsername(), roleToUserForm.getRolename());
		return ResponseEntity.ok().build();
	}
	
	
	private final String BEARER = "Bearer ";
	
	// http://localhost:8088/api/token/refresh
	@GetMapping("/token/refresh")
	public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String authorizationHeader = request.getHeader("Authorization");
		
		if (authorizationHeader!=null && authorizationHeader.startsWith(BEARER)) {	// done only once (only if success)
			try {
				// at this point user has been already authenticated //
				String refresh_token = authorizationHeader.substring(BEARER.length());
				JWTVerifier verfier = JWT.require(JwtUtility.getAlgorithm()).build();
				DecodedJWT decodedJWT = verfier.verify(refresh_token);
				String username = decodedJWT.getSubject(); 
				User user = userService.getUser(username);
				
				String access_token = JWT.create()
						.withSubject(user.getUsername())	// unique identyfier for user; in this case username will be unique
						.withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
						.withIssuer(request.getRequestURI().toString())  // any string (like organization)
						.withClaim("roles", user.getRoles().stream()
												.map(Role::getName)
												.collect(Collectors.toList())
							)
						.sign(JwtUtility.getAlgorithm());
				//String refresh_token = JWT.create().. // do not need, can return the same refresh token
				
				response.setHeader("access_token", access_token);
				response.setHeader("refresh_token", refresh_token);
				
				Map<String, String> tokens = new HashMap<>();
				tokens.put("access_token", access_token);
				tokens.put("refresh_token", refresh_token);
				response.setContentType(MediaType.APPLICATION_JSON_VALUE);
				new ObjectMapper().writeValue(response.getOutputStream(), tokens);
				
			} catch (Exception e) {
				String errorMessage = "Error login into application: " + e.getMessage();
				log.error(errorMessage);
				response.setHeader("error", errorMessage);
				response.setStatus(403); // Forbidden
				//response.sendError(403); // Forbidden
				
				Map<String, String> errors = new HashMap<>();
				errors.put("error_message", errorMessage);
				response.setContentType(MediaType.APPLICATION_JSON_VALUE);
				new ObjectMapper().writeValue(response.getOutputStream(), errors);
			}
		} else {
			throw new HttpClientErrorException(HttpStatus.FORBIDDEN, "Refresh token is missing!");
			//throw new RuntimeException("Refresh token is missing!");	
		}
		
	}

	private URI createURI(String path) {
		return URI.create(ServletUriComponentsBuilder.fromCurrentContextPath()
				.path(path)
				.toUriString());
	}
	
}
