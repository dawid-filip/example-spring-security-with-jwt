package com.pl.df.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
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
public class UserServiceImpl implements UserService, UserDetailsService {

	private final UserRepo userRepo;
	private final RoleRepo roleRepo;
	private final PasswordEncoder passwordEncoder;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User userLoaded = userRepo.findByUsername(username);
		if (userLoaded==null) {
			log.error("User not found in the database");
			throw new UsernameNotFoundException("User not found in the database");
		} else {
			log.info("User found in the database: " + username);
		}
		
		Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
		
		userLoaded.getRoles().forEach(role -> 
			authorities.add(new SimpleGrantedAuthority(role.getName()))
		);
		
		org.springframework.security.core.userdetails.User user = 
				new org.springframework.security.core.userdetails.User(
						userLoaded.getUsername(), userLoaded.getPassword(), authorities);
		
		return user;
	}
	
	
	@Override
	public User saveUser(User user) {
		log.info("saveUser: " + user.getUsername());

		user.setPassword(passwordEncoder.encode(user.getPassword())); // encoding password before saving into database 
		
		User users = userRepo.findByUsername(user.getUsername());
		if (users==null) {
			return userRepo.save(user);
		}
		
		throw new DuplicateKeyException("User " + user.getUsername() + " already exist.");
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
