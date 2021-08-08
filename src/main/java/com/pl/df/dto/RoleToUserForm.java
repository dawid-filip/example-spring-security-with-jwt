package com.pl.df.dto;

import lombok.Data;

/** DTO class with 2 fields: {@code username} and {@code rolename}*/
@Data
public class RoleToUserForm {
	
	private String username;
	private String rolename;
	
}