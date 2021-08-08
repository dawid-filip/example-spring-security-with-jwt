package com.pl.df.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pl.df.model.User;

public interface UserRepo extends JpaRepository<User, Long> {
	User findByUsername(String username);
}
