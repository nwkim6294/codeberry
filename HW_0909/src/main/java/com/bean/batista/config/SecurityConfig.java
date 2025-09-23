package com.bean.batista.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.bean.batista.service.AgentDetailsService;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {
	
	private final AgentDetailsService agentDetailsService;
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		
		http.authorizeHttpRequests(auth -> auth
				.requestMatchers("/", "/users/**", "/css/**", "/js/**").permitAll()
				.requestMatchers("/admin/**").hasRole("MANAGER")
				.requestMatchers("/missions/**").hasRole("AGENT")
				.anyRequest().authenticated()
				);
//		http.formLogin(form -> form
//				.loginPage("/users/login")
//				.loginProcessingUrl("/users/login")
//				.usernameParameter("username")
//				.passwordParameter("password")
//				.defaultSuccessUrl("/")
//				.permitAll()
//				);
		
		
		http.formLogin(form -> form
				.loginPage("/users/login")
				.loginProcessingUrl("/users/login")
				.usernameParameter("username")
				.passwordParameter("password")
				.successHandler((request, response, authentication) -> {
					var authorities = authentication.getAuthorities();
					
					String redirectUtl = "/";
					
					if(authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_MANAGER"))) {
						redirectUtl = "/admin/dashboard";
					} else if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_AGENT"))) {
						redirectUtl = "/missions/my-missions";
					}
					
					response.sendRedirect(redirectUtl);
				})
				.permitAll()
				);
		
		
		
		http.logout(logout -> logout
				.logoutSuccessUrl("/")
				.permitAll()
				);
		
		return http.build();
	}

}
