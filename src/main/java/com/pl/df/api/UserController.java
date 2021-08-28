package com.pl.df.api;

import static com.pl.df.configuration.AppRole.USER;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pl.df.dto.UserForm;
import com.pl.df.model.Role;
import com.pl.df.model.User;
import com.pl.df.repository.RoleRepo;
import com.pl.df.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import static com.pl.df.configuration.WebUtility.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Log4j2
public class UserController {

	private final UserService userService;
	private final RoleRepo roleRepo;
	
	// http://localhost:8088/api/users
	@GetMapping
	public ResponseEntity<List<User>> getUsers() {
		return ResponseEntity.ok().body(userService.getUsers());
	}
	
	// http://localhost:8088/api/users/{id}
	@GetMapping("/users/{id}")
	public ResponseEntity<User> getUserById(@PathVariable("id") Long id) {	// additional method
		return ResponseEntity.ok().body(userService.getUserById(id));
	}
	
	// http://localhost:8088/api/users
	@PostMapping
	public ResponseEntity<?> saveUser(@RequestBody User user) {
		User savedUser = userService.saveUser(user);
		return ResponseEntity.created(createURI("/api/users/" + savedUser.getId())).body(savedUser);
	}
	
	// http://localhost:8088/api/users/registration
	@PostMapping("/registration")
	public ResponseEntity<?> registrateUser(@RequestBody UserForm userForm) {
		Collection<Role> roles = new ArrayList<>();
		roles.add(new Role(5L, USER.toString()));
		User user = new User(null, userForm.getName(), userForm.getUsername(), userForm.getPassword(), roles);
		
		userService.saveUser(user);
		
		return ResponseEntity.ok().build();
	}
	
}
