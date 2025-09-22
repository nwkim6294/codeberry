package com.crude.practice.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class MarketItem {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false)
	private String title;
	
	@Column(nullable = false)
	private int price;
	
	private String description;
	
	public MarketItem(String title, int price, String description) {
		this.title = title;
		this.price = price;
		this.description = description;
	}
	
}
