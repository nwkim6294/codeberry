//package com.example.test;
//
//import java.util.Collection;
//
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.context.SecurityContextHolderStrategy;
//
//public class SecurityExam {
//	// 셜록 누렁의 비밀 상황실 접근법
//	
//	// 근데 솔직히 왜쓰는지 모르겠음
//	
//	// 변수 context에는 현재 접속 중인 혹은 현재 요청된 내용을 볼 수 있다
//	SecurityContext context = SecurityContextHolder.getContext();
//	
//	// 시큐리티 컨텍스트에서 현재 인증된 객체를 가져오는 역할
//	// 여게어 포함되어있는 정보들
//	// 1. 사용자의 ID나 이름 (principal)
//	// 2. 사용자의 비밀번호 : credentials
//	// 3. 사용자의 권한 목록 : authorities
//	// 4. 인증여부 : authenticated
//	Authentication auth = context.getAuthentication();
//
//	
//	String id1 = auth.getName(); // principal 대신 username이나 id를 직접 가져온 경우
//	// 해당 객체가 보유하고 있는 권한 목록 조회
//	Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
//}
//
////ThreadLocal 전략의 핵심 코드
//final class ThreadLocalSecurityContextHolderStrategy implements SecurityContextHolderStrategy {
//	// 셜록 누렁의 비밀 상황실 접근법
//	SecurityContext context = SecurityContextHolder.getContext();
//	Authentication auth = context.getAuthentication();
//	String memberName = auth.getName(); // 현재 클럽 회원 이름
//
//	private static final ThreadLocal<SecurityContext> contextHolder = new ThreadLocal<>();
//
//	// 현재 SecurityContext 가져오는
//	@Override
//	public SecurityContext getContext() {
//		SecurityContext ctx = contextHolder.get();
//		if (ctx == null) {
//			ctx = createEmptyContext();
//			contextHolder.set(ctx);
//		}
//		return ctx;
//	}
//	//컨텍스트 종료시 실행할 메서드
//	@Override
//	public void clearContext() {
//		// TODO Auto-generated method stub
//
//	}
//
//	// 새로운 정보를 저장할떄 활용하는 영역
//	@Override
//	public void setContext(SecurityContext context) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public SecurityContext createEmptyContext() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//}