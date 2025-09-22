package com.soobookz.store.form;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemForm {
	
	@NotBlank(message = "상품 이름은 필수입니다.")
	private String name;
	
	@NotNull(message = "가격은 필수입니다.")
	@Min(value = 0, message = "가격은 0이상이어야 합니다.")
	private Integer price;
	
	@NotNull(message = "재고는 필수입니다.")
	@Min(value = 0, message = "재고는 0이상이어야 합니다.")
	private Integer stock;
	
	private String imageFileName;
	
}
