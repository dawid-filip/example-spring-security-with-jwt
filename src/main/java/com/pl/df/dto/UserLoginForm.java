package com.pl.df.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Used for user login */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginForm {
	
	private String username;
	private String password;
	
}
