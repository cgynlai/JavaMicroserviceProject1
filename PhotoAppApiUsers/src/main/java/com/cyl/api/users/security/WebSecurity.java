package com.cyl.api.users.security;

import javax.servlet.Filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.cyl.api.users.service.UsersService;

@Configuration
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {
	
	
	private Environment environment;
	private UsersService usersService;
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	public WebSecurity(Environment environment, UsersService userService, BCryptPasswordEncoder bCryptPasswordEncoder) {
		
		this.environment = environment;
		this.usersService = userService;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}
	
	/*
	 * @Autowired public WebSecurity(Environment environment) {
	 * this.environment=environment; }
	 */
    @Override
	protected void configure(HttpSecurity http) throws Exception{
    	http.csrf().disable();
    	 http.headers().frameOptions().disable();
    	http.authorizeRequests().antMatchers("/users/**")
    	.permitAll().and().addFilter(getAuthenticationFilter());
    	// http.authorizeRequests().antMatchers("/**").hasIpAddress(environment.getProperty("gateway.ip"));
    }

	

	private AuthenticationFilter getAuthenticationFilter() throws Exception {
		
		AuthenticationFilter authenticationFilter = new AuthenticationFilter(usersService, environment, authenticationManager());
	//	authenticationFilter.setAuthenticationManager(authenticationManager());
		authenticationFilter.setFilterProcessesUrl(environment.getProperty("login.url.path"));
		return authenticationFilter;
	}
	
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(usersService).passwordEncoder(bCryptPasswordEncoder);
	}
}
