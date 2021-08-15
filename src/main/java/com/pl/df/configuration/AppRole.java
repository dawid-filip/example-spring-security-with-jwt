package com.pl.df.configuration;

public enum AppRole {
	
	SUPER_ADMIN("ROLE_SUPER_ADMIN"),
	ADMIN("ROLE_ADMIN"),
	MANAGER("ROLE_MANAGER"),
	EDITOR("ROLE_EDITOR"),
	USER("ROLE_USER");
	
	private String value;
	
	private AppRole(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return this.value;
	}
	
}
