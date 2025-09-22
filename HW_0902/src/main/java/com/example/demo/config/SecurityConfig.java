package com.example.demo.config;

import java.net.PasswordAuthentication;

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
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(authorize -> authorize
				.requestMatchers("/", "/keepers/signup", "/keepers/login", "/css/**","/js/**", "/images/**").permitAll()
				.anyRequest().authenticated())
		.formLogin(formLogin -> formLogin
				.loginPage("/keepers/login")
				.loginProcessingUrl("/keepers/login")
				.defaultSuccessUrl("/entries", true)
				.failureUrl("/keepers/login?error=true")
				.usernameParameter("username")
				.passwordParameter("password"))
		.logout(logout -> logout
				.logoutUrl("/logout")
				.logoutSuccessUrl("/")
				);
		return http.build();
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
