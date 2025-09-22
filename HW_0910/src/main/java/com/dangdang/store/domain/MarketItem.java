package com.dangdang.store.domain;

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

    private Integer price;

    @Lob
	private String description;
	
	public MarketItem (String title, Integer price, String description) {
		this.title = title;
		this.price = price;
		this.description = description;
	}
    
}
