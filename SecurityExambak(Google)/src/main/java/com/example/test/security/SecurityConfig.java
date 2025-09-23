package com.example.test.security;

import java.io.IOException;
import java.util.Collection;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.example.test.AppConfig;
import com.example.test.oauth2.OAuth2AuthenticationFailureHandler;
import com.example.test.oauth2.OAuth2AuthenticationSuccessHandler;
import com.example.test.service.ClubUserDetailsService;
import com.example.test.service.CustomOAuth2UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
	
	private final ClubUserDetailsService clubUserDetailsService;
	private final ClubAuthenticationSuccessHandler successHandler;
	private final ClubAuthenticationFailureHandler failureHandler;
	private final CustomOAuth2UserService customOAuth2UserService;
	private final OAuth2AuthenticationSuccessHandler oauth2SucceessHandler;
	private final OAuth2AuthenticationFailureHandler oauth2FailureHandler;
	private final AppConfig appConfig;
    
	@Bean
	public SecurityFilterChain clubFilterChain(HttpSecurity http) throws Exception {
		
		//CSRF 설정(개발 단계에서는 꺼두기)
		return http
				.csrf(csrf -> {
					csrf.disable(); // 개발 단계
					// 이건 실제로 운영할때 이렇게 설정하면 좋음
//					csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
				})
				// 세션 관리와 세션 관리 설정을 정의할때 사용
				// .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
				//   -> 세션 생성 정책을 설정한느 메서드
				//   -> SessionCreationPolicy.IF_REQUIRED : 필요할때만 세션을 생성하도록 설정
				//      필요함의 기준 : 인증이 필요할때만
				//   ALWAYS, NEVER, STATELESS 등등이 있음
				
				// .maximumSessions(1) : 한사용자가 동시에 유지할 수 있는 세션
				// .maxSessionsPreventsLogin : 최대 세션 수를 초과했을 때 어떻게 동작할 것인가를 정의
				//  -> false : 이전 세션이 만료 -> 새로운 로그인 허용
				//  -> true : 새로운 로그인을 차단하고 기존 세션을 유지
				.sessionManagement(session -> session
						.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
						.maximumSessions(1)
						.maxSessionsPreventsLogin(false)
						)
				  // URL별 접근 권한 설정 (셜록 누렁의 클럽 출입 규칙용!)
                .authorizeHttpRequests(auth -> auth
                    // 누구나 접근 가능한 공개 구역
                    .requestMatchers("/", "/club/login", "/club/register", 
                                   "/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
                    
                    // VIP 회원 전용 구역  
                    .requestMatchers("/club/vip/**").hasRole("VIP")
                    
                    // 관리자 전용 구역
                    .requestMatchers("/club/admin/**").hasRole("ADMIN")
                    
                    // 특별 권한이 필요한 구역
                    .requestMatchers("/club/secret/**").hasAuthority("SECRET_ACCESS")
                    
                    // 나머지는 인증 필요
                    .anyRequest().authenticated()
                )
                
				.formLogin(form -> form
						.loginPage("/club/login")
						// 로그인 폼에서 제출된 데이터를 처리할 URL을 지정함
						.loginProcessingUrl("/club/authenticate")
						// 로그인 폼에서 사용자의 아이디와 비밀번호를 받을 input 필드의 이름을 지정
	                    .usernameParameter("username")               // 아이디 파라미터명
	                    .passwordParameter("password")               // 비밀번호 파라미터명
	                    // , true의 의미 : 항상 이 URL로 '리다이렉트' 강제화
	                    .defaultSuccessUrl("/club/main", true)       // 로그인 성공 시 이동할 페이지
	                    .failureUrl("/club/login?error=true")        // 로그인 실패 시 이동할 페이지
	                    .successHandler(successHandler)              // 성공 핸들러
	                    .failureHandler(failureHandler)              // 실패 핸들러
	                    .permitAll()
						)
				
				.oauth2Login(oauth2 -> oauth2
						.loginPage("/club/login")
						.userInfoEndpoint(userInfo -> userInfo
								.userService(customOAuth2UserService)
								// 커스텀 유저서비스를 만들었기 때문에 시큐리티가 정확하게 인식할 수 있도록
								// customUserservice 객체를 인식
								)
						.successHandler(oauth2SucceessHandler)
						.failureHandler(oauth2FailureHandler)
						)
				
				
                // Logout 설정 (퇴장 처리용!)
                .logout(logout -> logout
                    .logoutUrl("/club/logout")                   // 로그아웃 URL
                    .logoutSuccessUrl("/club/login?logout=true") // 로그아웃 성공 시 이동할 페이지
                    .invalidateHttpSession(true)                // 세션 무효화
                    .deleteCookies("JSESSIONID")                // 쿠키 삭제
                    .clearAuthentication(true)                  // 인증 정보 삭제
                    .permitAll()
                )
                // 예외 처리 설정
                .exceptionHandling(ex -> ex
                		// 인증되지 않은 사용자가 접근을 시도했다면
                		// 해당 요청 URI를 로그에 출력
                    .authenticationEntryPoint((request, response, authException) -> {
                        System.out.println("인증되지 않은 접근 시도: " + request.getRequestURI());
                        response.sendRedirect("/club/login");
                    })
                    // 사용자가 권한이 부족한 상태에서 특정 리소르에 접근하려할 때 호출
                    .accessDeniedHandler((request, response, accessDeniedException) -> {
                        System.out.println("권한 없는 접근 시도: " + request.getRequestURI());
                        response.sendRedirect("/club/access-denied");
                    })
                )
                
                // Remember Me 기능 (선택사항)
                //  -> 사용자의 인증 정보를 어느 기간동안 유지할지 설정
                .rememberMe(remember -> remember
                    .key("clubSecretKey")
                    .tokenValiditySeconds(7 * 24 * 60 * 60) // 7일
                    .userDetailsService(clubUserDetailsService)
                )
                
		.build();
	}
    /**
     * 셜록 누렁의 성공 핸들러용!
     */
    @Component
    public static class ClubAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
        
        @Override
        public void onAuthenticationSuccess(HttpServletRequest request, 
                                          HttpServletResponse response,
                                          Authentication authentication) throws IOException {
            
            System.out.println("클럽 입장 성공: " + authentication.getName() + "님 환영합니다용!");
            
            // 사용자 역할에 따른 리다이렉트 (선택사항)
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            
            for (GrantedAuthority authority : authorities) {
                if (authority.getAuthority().equals("ROLE_ADMIN")) {
                    System.out.println("🔱 관리자가 입장하셨습니다!");
                    response.sendRedirect("/club/admin/dashboard");
                    return;
                }
                if (authority.getAuthority().equals("ROLE_VIP")) {
                    System.out.println("💎 VIP 회원이 입장하셨습니다!");
                    response.sendRedirect("/club/vip/lounge");
                    return;
                }
            }
            
            // 기본 사용자는 메인홀로
            response.sendRedirect("/club/main");
        }

    }

    /**
     * 셜록 누렁의 실패 핸들러용!
     */
    @Component
    public static class ClubAuthenticationFailureHandler implements AuthenticationFailureHandler {
        
        @Override
        public void onAuthenticationFailure(HttpServletRequest request, 
                                          HttpServletResponse response,
                                          AuthenticationException exception) throws IOException {
            
            System.out.println("❌ 클럽 입장 실패: " + exception.getMessage());
            
            // 실패 원인별 메시지 (선택사항)
            String errorMessage = "로그인에 실패했습니다용!";
            
            if (exception instanceof BadCredentialsException) {
                errorMessage = "아이디 또는 비밀번호가 올바르지 않습니다용!";
            } else if (exception instanceof DisabledException) {
                errorMessage = "계정이 비활성화되었습니다용! 관리자에게 문의해주세요.";
            } else if (exception instanceof AccountExpiredException) {
                errorMessage = "계정이 만료되었습니다용! 갱신이 필요해요.";
            }
            
            // 세션에 에러 메시지 저장 후 리다이렉트
            request.getSession().setAttribute("errorMessage", errorMessage);
            response.sendRedirect("/club/login?error=true");
        }
    }
}
