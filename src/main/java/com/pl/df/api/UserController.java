package com.pl.df.api;

import static com.pl.df.configuration.AppRole.USER;
import static com.pl.df.configuration.JwtUtility.*;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pl.df.dto.RoleToUserForm;
import com.pl.df.dto.UserForm;
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
	public ResponseEntity<List<Role>> getRoles() {							// additional method
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
	public ResponseEntity<?> saveUser(@RequestBody User user) {
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
	
	// http://localhost:8088/api/registration
	@PostMapping("/registration")
	public ResponseEntity<?> registrateUser(@RequestBody UserForm userForm) {
		Collection<Role> roles = new ArrayList<>();
		roles.add(new Role(5L, USER.toString()));
		User user = new User(null, userForm.getName(), userForm.getUsername(), userForm.getPassword(), roles);
		
		userService.saveUser(user);
		
		return ResponseEntity.ok().build();
	}
	
	// http://localhost:8088/api/token/decode-access-token
	@GetMapping("/token/decode-access-token") 
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
	
	// http://localhost:8088/api/token/refresh
	@GetMapping("/token/refresh")
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

	private URI createURI(String path) {
		return URI.create(ServletUriComponentsBuilder.fromCurrentContextPath()
				.path(path)
				.toUriString());
	}
	
}
