package com.pl.df.configuration;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import com.pl.df.model.Role;
import com.pl.df.model.User;
import com.pl.df.service.UserService;

import lombok.extern.log4j.Log4j2;

import static com.pl.df.configuration.AppRole.*;

@Configuration
@Log4j2
public class DataInitializer implements CommandLineRunner {

	@Autowired
	private UserService userService;
	
	@Override
	public void run(String... args) throws Exception {
		log.info("Adding roles..");
		userService.saveRole(new Role(1L, SUPER_ADMIN.toString()));
		userService.saveRole(new Role(2L, ADMIN.toString()));
		userService.saveRole(new Role(3L, MANAGER.toString()));
		userService.saveRole(new Role(4L, EDITOR.toString()));
		userService.saveRole(new Role(5L, USER.toString()));
		log.info("Roles added.");

		log.info("Adding users..");
		userService.saveUser(new User(1L, "Tom T.", "tom", "tom", new ArrayList<>()));
		userService.saveUser(new User(2L, "Jery J.", "jery", "jery", new ArrayList<>()));
		userService.saveUser(new User(3L, "Johan Jo.", "johan", "johan", new ArrayList<>()));
		userService.saveUser(new User(4L, "George G.", "george", "george", new ArrayList<>()));
		log.info("Users added.");
		
		log.info("Adding roles to users..");
		userService.addRoleToUser("tom", SUPER_ADMIN.toString());
		userService.addRoleToUser("tom", ADMIN.toString());
		userService.addRoleToUser("tom", USER.toString());
		userService.addRoleToUser("jery", ADMIN.toString());
		userService.addRoleToUser("johan", MANAGER.toString());
		userService.addRoleToUser("george", MANAGER.toString());
		userService.addRoleToUser("george", USER.toString());
		log.info("Added roles to users.");
		
		StringBuilder sb = new StringBuilder("Users in DB are:\n");
		userService.getUsers().forEach(user -> sb.append(user.toString() + "\n"));
		log.info(sb.toString());
	}
	
}
