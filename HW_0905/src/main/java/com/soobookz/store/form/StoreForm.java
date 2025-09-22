package com.soobookz.store.form;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoreForm {
	
	@NotBlank(message = "가게 이름은 필수입니다.")
	private String name;
	
	private String location;
}
