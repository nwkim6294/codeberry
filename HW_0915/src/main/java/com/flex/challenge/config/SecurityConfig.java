package com.flex.challenge.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public SecurityFilterChain securityFilterChain (HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(auth -> auth
				.requestMatchers("/", "/users/signup", "/posts", "/uploads/**", "/css/**", "/js/**").permitAll()
				.anyRequest().authenticated()
				)
			.formLogin(form -> form
					.loginPage("/users/login")
					.loginProcessingUrl("/users/login")
					.defaultSuccessUrl("/posts", true)
					.permitAll()
					)
			.logout(logout -> logout
					.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
					.logoutSuccessUrl("/")
					.invalidateHttpSession(true)
					)
			.csrf(csrf -> csrf
					.ignoringRequestMatchers(new AntPathRequestMatcher("/api/**"))
//					.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
					);
		return http.build();
	}

}
