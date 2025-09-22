package com.soobookz.store.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int price;
    private int stock;
    private String imageFileName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    @Builder
    public Item(String name, int price, int stock, String imageFileName, Store store) {
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.imageFileName = imageFileName;
        this.store = store;
    }

    public void update(String name, int price, int stock) {
        this.name = name;
        this.price = price;
        this.stock = stock;
    }
    
    public void removeStock(int quantity) {
    	int restStock = this.stock - quantity;
    	if(restStock < 0) {
    		throw new IllegalStateException("재고가 부족합니다.");
    	}
    	this.stock = restStock;
    }
}

