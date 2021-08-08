package com.pl.df.api;

import java.net.URI;
import java.util.Collection;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.pl.df.dto.RoleToUserForm;
import com.pl.df.model.Role;
import com.pl.df.model.User;
import com.pl.df.repository.RoleRepo;
import com.pl.df.service.UserService;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;
	private final RoleRepo roleRepo;
	
	// http://localhost:8088/api/users
	@GetMapping("/users")
	public ResponseEntity<List<User>> getUsers() {
		return ResponseEntity.ok().body(userService.getUsers());
	}
	
	@GetMapping("/users/{id}")
	public ResponseEntity<User> getUserById(@PathVariable("id") Long id) {	// additional method
		return ResponseEntity.ok().body(userService.getUserById(id));
	}
	@GetMapping("/role/{id}")
	public ResponseEntity<Role> getRoleById(@PathVariable("id") Long id) {	// additional method
		return ResponseEntity.ok().body(roleRepo.getById(id));
	}


	
	@PostMapping("/users")
	public ResponseEntity<User> saveUser(@RequestBody User user) {
		User savedUser = userService.saveUser(user);
		return ResponseEntity.created(createURI("/api/users/" + savedUser.getId())).body(savedUser);
	}
	
	@PostMapping("/role")
	public ResponseEntity<Role> saveRole(@RequestBody Role role) {
		Role savedRole = userService.saveRole(role);
		return ResponseEntity.created(createURI("/api/role/" + savedRole.getId())).body(savedRole);
	}
	
	@PostMapping("/role/add-to-user")
	public ResponseEntity<Void> addRoleToUser(@RequestBody RoleToUserForm roleToUserForm) {
		userService.addRoleToUser(roleToUserForm.getUsername(), roleToUserForm.getRolename());
		return ResponseEntity.ok().build();
	}
	

	private URI createURI(String path) {
		return URI.create(ServletUriComponentsBuilder.fromCurrentContextPath()
				.path(path)
				.toUriString());
	}
	
}
