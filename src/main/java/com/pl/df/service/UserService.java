package com.pl.df.service;

import java.util.List;

import com.pl.df.model.Role;
import com.pl.df.model.User;

public interface UserService {
	User saveUser(User user);
	Role saveRole(Role role);
	void addRoleToUser(String username, String roleName); 	// assumption: no duplicates 
	User getUser(String username);
	List<User> getUsers();									// usually only for tests, no prod case
	
	User getUserById(long id);								// additional method
	Role getRoleById(long id); 								// additional method
}
