package com.pl.df.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Used for user registration */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserForm {
	
	private String name;
	private String username;
	private String password;
	
}
