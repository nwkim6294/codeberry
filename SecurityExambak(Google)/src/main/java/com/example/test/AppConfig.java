package com.example.test;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AppConfig {
	// PasswordEncoder를 왜 옮겨야하는지
	//  -> 에러의 원인 : 순환 참조라는 개념 때문에 그러함
	
    @Bean
    public PasswordEncoder passwordEncoder() {
        // 셜록 누렁: "BCrypt로 비밀번호를 안전하게 암호화합니다용!"
        return new BCryptPasswordEncoder(12); // strength를 12로 설정하여 더 강력한 암호화
    }
}
