package com.bog.ecommerce.model.dto;

import java.math.BigDecimal;


public interface PurchasedProductDTO {
    String getName();

    BigDecimal getPrice();

    int getQuantity();

    String getEmail();

    String getPersonalid();

}
