package com.pl.df;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import com.pl.df.model.Role;
import com.pl.df.model.User;
import com.pl.df.service.UserService;

import lombok.extern.log4j.Log4j2;

@Configuration
@Log4j2
public class DataInitializer implements CommandLineRunner {

	@Autowired
	private UserService userService;
	
	@Override
	public void run(String... args) throws Exception {
		log.info("Adding roles..");
		userService.saveRole(new Role(1L, "ROLE_SUPER_ADMIN"));
		userService.saveRole(new Role(2L, "ROLE_ADMIN"));
		userService.saveRole(new Role(3L, "ROLE_MANAGER"));
		userService.saveRole(new Role(4L, "ROLE_EDITOR"));
		userService.saveRole(new Role(5L, "ROLE_USER"));
		log.info("Roles added.");

		log.info("Adding users..");
		userService.saveUser(new User(1L, "Tom T.", "tom", "tom", new ArrayList<>()));
		userService.saveUser(new User(2L, "Jery J.", "jery", "jery", new ArrayList<>()));
		userService.saveUser(new User(3L, "Johan Jo.", "johan", "johan", new ArrayList<>()));
		userService.saveUser(new User(4L, "George G.", "george", "george", new ArrayList<>()));
		log.info("Users added.");
		
		log.info("Adding roles to users..");
		userService.addRoleToUser("tom", "ROLE_SUPER_ADMIN");
		userService.addRoleToUser("tom", "ROLE_ADMIN");
		userService.addRoleToUser("tom", "ROLE_USER");
		userService.addRoleToUser("jery", "ROLE_ADMIN");
		userService.addRoleToUser("johan", "ROLE_MANAGER");
		userService.addRoleToUser("george", "ROLE_MANAGER");
		userService.addRoleToUser("george", "ROLE_USER");
		log.info("Added roles to users.");
		
		log.info("Users in DB are:");
		userService.getUsers().forEach(user -> log.info(user.toString()));
	}
	
}









