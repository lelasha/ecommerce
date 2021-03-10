package com.bog.ecommerce.model;

import com.bog.ecommerce.model.dto.PurchasedProductDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Analytic {

    private Integer createdOrdersToday;
    private BigDecimal totalPurchasedPrice;
    private BigDecimal totalFeeAmount;
    private Integer addedProductToday;
    private Long DistinctUserAuthToday;
    private Long totalWebViewToday;
    List<PurchasedProductDTO> purchasedProductToday;
}
