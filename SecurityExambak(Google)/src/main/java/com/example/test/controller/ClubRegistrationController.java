package com.example.test.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.test.domain.ClubUserRegistrationDto;
import com.example.test.service.ClubUserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ClubRegistrationController {
	
	private final ClubUserService userService;
	
	@GetMapping("/club/register")
	public String resterForm(Model model) {
        model.addAttribute("user", new ClubUserRegistrationDto());
        return "register";
	}
	
    @PostMapping("/club/register")
    public String registerUserAccount(
            @ModelAttribute("user") @Valid ClubUserRegistrationDto registrationDto,
            BindingResult result,
            Model model) {
        
        System.out.println("회원가입 시도: " + registrationDto.getUsername());
        
        // 비밀번호 확인
        // rejectValue : 특정한 필드와 관련된 오류를 등록하는 메서드
        // rejectValue(해당 필드/속성, 오류식별코드, "메세지");
        //  -> 폼 데이터 특정(지정한) 필드에 문제가 있을경우 해당 필드와 관련된 오류를 설정
        if (!registrationDto.isPasswordMatching()) {
            result.rejectValue("confirmPassword", "error.user", "비밀번호가 일치하지 않습니다.");
        }
        
        // 유효성 검사 실패
        if (result.hasErrors()) {
            System.out.println("유효성 검사 실패");
            return "register";
        }
        
        try {
            userService.registerNewUser(registrationDto);
            System.out.println(" 회원가입 성공: " + registrationDto.getUsername());
            model.addAttribute("success", true); // view단에 성공상태를 우선 저장시키기 위함
            return "redirect:/club/login?success";
            
        } catch (RuntimeException e) {
            System.out.println("회원가입 실패: " + e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

}
