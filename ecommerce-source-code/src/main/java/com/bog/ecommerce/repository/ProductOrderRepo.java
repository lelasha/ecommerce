package com.bog.ecommerce.repository;

import com.bog.ecommerce.model.ProductOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;


public interface ProductOrderRepo extends JpaRepository<ProductOrder, Long> {


    /**
     * მოცემულ დღეს შესრულებული შესყიდვების რაოდენობა
     */
    @Query(value = "select count(id) from product_order where date(created_at) = ?1", nativeQuery = true)
    Optional<Integer> countPurchasedProducts(Date date);

    /**
     * შესყიდვებით მიღებული საკომისიო
     */
    @Query(value = "select sum(o.fee_amount) from product_order o", nativeQuery = true)
    Optional<BigDecimal> totalFeeAmount();

}
