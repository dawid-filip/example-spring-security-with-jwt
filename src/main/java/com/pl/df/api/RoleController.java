package com.pl.df.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pl.df.dto.RoleToUserForm;
import com.pl.df.model.Role;
import com.pl.df.repository.RoleRepo;
import com.pl.df.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import static com.pl.df.configuration.WebUtility.*;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@Log4j2
public class RoleController {
	
	private final UserService userService;
	private final RoleRepo roleRepo;
	
	// http://localhost:8088/api/roles
	@GetMapping
	public ResponseEntity<List<Role>> getRoles() {							// additional method
		return ResponseEntity.ok().body(roleRepo.findAll());
	}
	// http://localhost:8088/api/roles/{id}
	@GetMapping("/{id}")
	public ResponseEntity<Role> getRoleById(@PathVariable("id") Long id) {	// additional method
		return ResponseEntity.ok().body(roleRepo.getById(id));
	}
	
	// http://localhost:8088/api/roles
	@PostMapping
	public ResponseEntity<Role> saveRole(@RequestBody Role role) {
		Role savedRole = userService.saveRole(role);
		return ResponseEntity.created(createURI("/api/roles/" + savedRole.getId())).body(savedRole);
	}
	
	// http://localhost:8088/api/roles/add-to-user
	@PostMapping("/add-to-user")
	public ResponseEntity<Void> addRoleToUser(@RequestBody RoleToUserForm roleToUserForm) {
		userService.addRoleToUser(roleToUserForm.getUsername(), roleToUserForm.getRolename());
		return ResponseEntity.ok().build();
	}
	
}
