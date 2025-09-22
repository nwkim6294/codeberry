package com.soobookz.store.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserForm {
	
	@NotBlank(message = "아이디는 필수입니다.")
	@Size(min = 4, max = 20, message = "아이디는 4 ~ 20자 사이여야 합니다.")
	private String username;
	
	@NotBlank(message = "비밀번호는 필수입니다.")
	@Size(min = 1, message = "비밀번호는 최소 6자 이상이어야 합니다.")
	private String password;

}
