package com.bog.ecommerce.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
public class Product extends BaseEntity {
    private String name;
    private int quantity;
    private BigDecimal price;
    private String image;
    private boolean enabled;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    public Product(String name, int quantity, BigDecimal price, String image, User user) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.image = image;
        this.user = user;
        this.enabled=true;
    }

    public Product() {
        this.enabled=true;
    }
}
