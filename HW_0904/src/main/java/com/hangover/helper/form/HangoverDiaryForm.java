package com.hangover.helper.form;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HangoverDiaryForm {

    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    @NotBlank(message = "증상은 필수입니다.")
    private String symptoms;
}