package com.nurung.detective.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(auth -> auth
				.requestMatchers("/cases/my-cases/**","/cases/new").authenticated()
				.requestMatchers("/","/cases/archive", "/users/**","/uploads/**").permitAll()
				.anyRequest().authenticated()
				);
		http.formLogin(form -> form
				.loginPage("/users/login")
				.defaultSuccessUrl("/cases/my-cases", true)
				.permitAll()
				);
		http.logout(logout -> logout
				.logoutSuccessUrl("/")
				);
		
		return http.build();
	}
}
