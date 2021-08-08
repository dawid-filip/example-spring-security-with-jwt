package com.pl.df.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pl.df.model.Role;

public interface RoleRepo extends JpaRepository<Role, Long> {
	Role findByName(String name);
}
