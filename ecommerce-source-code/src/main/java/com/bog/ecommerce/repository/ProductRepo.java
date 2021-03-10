package com.bog.ecommerce.repository;

import com.bog.ecommerce.model.Product;
import com.bog.ecommerce.model.dto.PurchasedProductDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ProductRepo extends JpaRepository<Product, Long> {
    /**
     * შესყიდული პროდუქციის ღირებულების ჯამური თანხა
     */
    @Query(value = "select sum(p.price * o.quantity) from product_order o join product p on o.product_id=p.id", nativeQuery = true)
    Optional<BigDecimal> totalPurchasedPrice();

    /**
     * მოცემულ დღეს დამატებული პროდუქციის რაოდენობა
     */
    @Query(value = "select count(id) from product p where date(p.created_at) = ?1", nativeQuery = true)
    Optional<Integer> totalAddedProductToday(Date date);

    /**
     * მოცემულ დღეს გაყიდული პროდუქციის სია + პროდუქტის შემსყიდველის მაილი და პირადი ნომერი
     */
    @Query(value = "select p.name as name,ROUND(p.price/100, 2) as price,o.quantity as quantity,o.email as email,o.personalid as personalid \n" +
            "from product_order o join product p on o.product_id=p.id where date(o.created_at) = ?1", nativeQuery = true)
    List<PurchasedProductDTO> totalPurchasedProductToday(Date date);


    @Query(value = "select * from product where user_id=?1 and enabled=true order by created_at desc", nativeQuery = true)
    List<Product> findAllProductByUserId(Long userId);

    @Override
    @Query(value = "select * from product where enabled=true order by created_at desc", nativeQuery = true)
    List<Product> findAll();

    @Override
    @Query(value = "select * from product where enabled=true and id=?1", nativeQuery = true)
    Optional<Product> findById(Long id);




}
