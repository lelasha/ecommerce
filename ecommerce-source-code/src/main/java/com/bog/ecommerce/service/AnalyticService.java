package com.bog.ecommerce.service;

import com.bog.ecommerce.model.Analytic;
import com.bog.ecommerce.model.dto.PurchasedProductDTO;
import com.bog.ecommerce.repository.ProductOrderRepo;
import com.bog.ecommerce.repository.ProductRepo;
import com.bog.ecommerce.repository.UserAuthRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class AnalyticService {
    @Autowired
    ProductRepo productRepo;
    @Autowired
    ProductOrderRepo productOrderRepo;
    @Autowired
    UserAuthRepo userAuthRepo;

    private final static String[] rows =
            {
                    "მოცემულ დღეს შესრულებული შესყიდვების რაოდენობა",
                    "შესყიდული პროდუქციის ღირებულების ჯამური თანხა",
                    "შესყიდვებით მიღებული საკომისიო",
                    "მოცემულ დღეს დამატებული პროდუქციის რაოდენობა",
                    "მოცემულ დღეს უნიკალური მომხმარებლების რაოდენობა, რომელთაც გაიარეს ავტორიზაცია",
                    "მოცემულ დღეს ვებგვერდზე შემოსვლების რაოდენობა",
                    "მოცემულ დღეს გაყიდული პროდუქციის სია + პროდუქტის შემსყიდველის მაილი და პირადი ნომერი"
            };

    protected LinkedHashMap<String,Object> buildHashMap(Long userSize, Long authSize) throws ParseException {
        Analytic analytic = buildAnalytics(userSize,authSize);
        LinkedHashMap<String,Object> analyticMap = new LinkedHashMap<>();
        analyticMap.put(rows[0],analytic.getCreatedOrdersToday());
        analyticMap.put(rows[1], analytic.getTotalPurchasedPrice());
        analyticMap.put(rows[2], analytic.getTotalFeeAmount());
        analyticMap.put(rows[3], analytic.getAddedProductToday());
        analyticMap.put(rows[4], analytic.getDistinctUserAuthToday());
        analyticMap.put(rows[5], analytic.getTotalWebViewToday());
        analyticMap.put(rows[6], analytic.getPurchasedProductToday());
        return analyticMap;
    }

    public String getCurrentDate(){
        return DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH).format(LocalDateTime.now());
    }
    public BigDecimal getCurrencyInGEL(BigDecimal bigDecimal){
        return bigDecimal.divide(new BigDecimal("100"),RoundingMode.DOWN);
    }

    private Analytic buildAnalytics(Long userSize, Long authSize) throws ParseException {
        String currentDate = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH).format(LocalDateTime.now());
        Date date = new SimpleDateFormat("yyyy-MM-dd").parse(currentDate);


        Analytic analytic = new Analytic();
        productOrderRepo.countPurchasedProducts(date)
                .ifPresentOrElse(analytic::setCreatedOrdersToday,
                        () -> analytic.setCreatedOrdersToday(0));
        productRepo.totalPurchasedPrice()
                .ifPresentOrElse(bigDecimal -> analytic.setTotalPurchasedPrice(getCurrencyInGEL(bigDecimal)),
                () -> analytic.setTotalPurchasedPrice(new BigDecimal("0")));
        productOrderRepo.totalFeeAmount()
                .ifPresentOrElse(bigDecimal -> analytic.setTotalFeeAmount(getCurrencyInGEL(bigDecimal)),
                        () -> analytic.setTotalFeeAmount(new BigDecimal("0")));
        productRepo.totalAddedProductToday(date)
                .ifPresentOrElse(analytic::setAddedProductToday,
                        () -> analytic.setAddedProductToday(0));
        analytic.setDistinctUserAuthToday(authSize);
        analytic.setTotalWebViewToday(userSize);
        List<PurchasedProductDTO> purchasedDTO = productRepo.totalPurchasedProductToday(date);
        analytic.setPurchasedProductToday(purchasedDTO);

        return analytic;
    }
}
