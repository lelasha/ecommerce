package com.bog.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ProductOrder extends BaseEntity {
    private String orderNumber;
    private String personalid;
    private String email;
    private int quantity;
    private double fee;
    private BigDecimal feeAmount;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    @JsonIgnore
    private Product product;


    public ProductOrder(double fee,BigDecimal feeAmount,String personalid, String email, int quantity, Product product) {
        this.feeAmount=feeAmount;
        this.fee=fee;
        this.orderNumber = UUID.randomUUID().toString().split("-")[4];
        this.personalid = personalid;
        this.email = email;
        this.quantity = quantity;
        this.product = product;
    }


}
