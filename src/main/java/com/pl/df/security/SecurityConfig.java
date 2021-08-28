package com.pl.df.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.pl.df.filer.CustomAuthenticationFilter;
import com.pl.df.filer.CustomAuthorizationFilter;

import lombok.RequiredArgsConstructor;

import static com.pl.df.configuration.AppRole.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	private final UserDetailsService userDetailsService;	// <- final.this + @RequiredArgsConstructor = DependencyInjection
	private final BCryptPasswordEncoder bCryptPasswordEncoder; 
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authenticationManagerBean());
		customAuthenticationFilter.setFilterProcessesUrl("/api/login");
		
		http.csrf().disable();
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
 		
		http.authorizeRequests()
				.antMatchers(HttpMethod.GET, "/swagger-ui/**", "/swagger-resources/**", "/v2/api-docs/**")
				.permitAll();
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/api/tokens/refresh/**").permitAll(); // also added in Filter
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/api/login/**", "/api/logout/**").permitAll();
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/api/users/registration/**").permitAll();
		//http.authorizeRequests().antMatchers(HttpMethod.GET, "/login").permitAll(); // not secured; must be placed before more restricted; this is already handling by spring
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/api/users/**").hasAnyAuthority(USER.toString());
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/api/users/**").hasAnyAuthority(ADMIN.toString());
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/api/roles/**").hasAnyAuthority(USER.toString());
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/api/roles/**").hasAnyAuthority(ADMIN.toString());
		http.authorizeRequests().anyRequest().authenticated(); ///.permitAll();

		http.addFilterBefore(new CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class); // must be before because it must be executed before each request
		http.addFilter(customAuthenticationFilter);
	}
	
	// Can be used alternatively for swagger: //
//	@Override
//	public void configure(WebSecurity web) throws Exception {
//		final String[] swaggerWhiteList = {
//				"/swagger-ui/**",
//				"/swagger-resources/**",
//				"/v2/api-docs/**"};
//	    web.ignoring().antMatchers(swaggerWhiteList);
//	}
	
	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManager();
	}
	
}
