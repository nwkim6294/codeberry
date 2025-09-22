package com.example.demo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "내용을 찾을 수 없습니다.")
public class AnomalyException extends RuntimeException{
	
	private static final long serialVersionUID = 1L;
	
	public AnomalyException(String msg) {
		super(msg);
	}
 
}
