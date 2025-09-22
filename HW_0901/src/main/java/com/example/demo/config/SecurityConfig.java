package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration // 스프링 설정 클래스임을 알려줌(Bean 등록 가능)
@EnableWebSecurity // 스프링 시큐리티를 활성화, 보안 설정 커스터마이징 가능
public class SecurityConfig {
	
	// PasswordEncoder Bean
	// 비밀번호 암호화를 위한 PasswordEncoder를 Bean으로 등록
	// BCryptPasswordEncoder : 해시 기반 암호화 방식
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	// SecurityFilterChain Bean
	// WebSecurityConfigurerAdapter 대신 SecurityFilterChain으로 보안 설정
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		// 모든 요청(/**)을 허용 permitAll() -> 현재는 로그인 없이도 접근 가능
		// 실제 서비스라면 /admin/**, /user/** 등으로 세분화 필요
		http.authorizeHttpRequests((authorizeHttpRequests) 
				-> authorizeHttpRequests
				.requestMatchers(new AntPathRequestMatcher("/**")).permitAll())
		// CSRF 보호 기능은 유지하되, H2 콘솔만 예외 처리
		// H2 DB 콘솔은 CSRF 토큰 없이도 접근 가능하게 열어둠		
		.csrf((crsf) -> crsf
				.ignoringRequestMatchers(new AntPathRequestMatcher("/h2-console/**")))
		// H2 콘솔이 iframe 안에서 보이도록 SAMEORIGIN 옵션을 적용
		// 안하면 H2 콘솔이 보안 정책 때무에 화면에 표시되지 않음
		.headers((headers) -> headers
				.addHeaderWriter(new XFrameOptionsHeaderWriter(
						XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN)))
		// 커스텀 로그인 페이지: /lab-users/login
		// 로그인 성공 시 이동할 페이지: /recipes
		.formLogin(formLogin -> formLogin
		        .loginPage("/lab-users/login")   // 로그인 폼 (GET)
		        .loginProcessingUrl("/login")    // 로그인 인증 처리 (POST, Security가 잡음)
		        .defaultSuccessUrl("/recipes", true) // 로그인 성공 시 이동
		        .permitAll())
		// 로그아웃 성공 후 /recipes로 리다이렉트.
		// 세션 무효화(invalidateHttpSession(true))로 로그인 정보 제거.
		.logout((logout) -> logout
				.logoutSuccessUrl("/recipes")
				.invalidateHttpSession(true));
		// 최종적으로 SecurityFilterChain 객체 생성 및 반환.
		return http.build();
	}
	
	// AuthenticationManager Bean
	// 인증을 처리하는 AuthenticationManager를 Bean으로 등록
	// UserDetailsService, PasswordEncoder 등을 기반으로 사용자 인증 수행
	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

}
