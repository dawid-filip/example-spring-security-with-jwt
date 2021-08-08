package com.pl.df.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.pl.df.model.Role;
import com.pl.df.model.User;
import com.pl.df.repository.RoleRepo;
import com.pl.df.repository.UserRepo;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class UserServiceImpl implements UserService {

	private final UserRepo userRepo;
	private final RoleRepo roleRepo;
	
	@Override
	public User saveUser(User user) {
		log.info("saveUser: " + user.getUsername());
		return userRepo.save(user);
	}

	@Override
	public Role saveRole(Role role) {
		log.info("saveRole: " + role.getName());
		return roleRepo.save(role);
	}

	@Override
	public void addRoleToUser(String username, String roleName) {
		User user = userRepo.findByUsername(username);
		Role role = roleRepo.findByName(roleName);
		
		user.getRoles().add(role);	// @Transactional will take care of saving everything into db 
		
		log.info("addRoleToUser: " + roleName + " to user " + username);
	}

	@Override
	public User getUser(String username) {
		log.info("getUser: " + username);
		return userRepo.findByUsername(username);
	}

	@Override
	public List<User> getUsers() {
		log.info("getUsers");
		return userRepo.findAll();
	}
	
	
	
	@Override
	public User getUserById(long id) {	// additional method
		log.info("getUserById: " + id);
		return userRepo.getById(id);
	}
	@Override
	public Role getRoleById(long id) {	// additional method
		log.info("getRoleById: " + id);
		return roleRepo.getById(id);
	}

}
