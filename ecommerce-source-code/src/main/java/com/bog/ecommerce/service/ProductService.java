package com.bog.ecommerce.service;

import com.bog.ecommerce.config.ConfigProperties;
import com.bog.ecommerce.exception.CustomException;
import com.bog.ecommerce.model.*;
import com.bog.ecommerce.model.dto.ProductDTO;
import com.bog.ecommerce.model.response.ApiError;
import com.bog.ecommerce.repository.ProductOrderRepo;
import com.bog.ecommerce.repository.ProductRepo;
import com.bog.ecommerce.repository.UserRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ProductService {

    @Autowired
    ProductRepo productRepo;
    @Autowired
    ProductOrderRepo productOrderRepo;
    @Autowired
    UserRepo userRepo;
    @Autowired
    ErrorService errorService;
    @Autowired
    ImageService imageService;
    @Autowired
    ConfigProperties configProperties;

    private static final String PRODUCT_ERROR_404 = "პროდუქტდი ვერ მოიძებნა";
    private static final String CREATED = "პროდუქტი შეიქმნა წარმატებით";
    private static final String UPDATED = "პროდუქტი დარედაქტირდა წარმატებით";
    private static final String OUT_OF_STOCK = "პროდუქტი არ არის მარაგში";
    private static final String USER_ERROR_404 = "ასეთი მომხმარებელი არ არსებობს";
    private static final String USER_AUTH = "authentication failed";


    public List<Product> changePrice(List<Product> productList){
        productList.forEach(product -> product.setPrice(product.getPrice().divide(new BigDecimal("100"), RoundingMode.DOWN)));
        return productList;
    }

    public Product changePrice(Product product){
        product.setPrice(product.getPrice().divide(new BigDecimal("100"), RoundingMode.DOWN));
        return product;
    }


    public ResponseEntity<?> saveProduct(@ModelAttribute @Valid ProductDTO productDTO, Errors error, Object auth) throws IOException, CustomException {
        if (error.hasErrors()) return errorService.MVCError(error);
        if (productDTO.getImage() == null || productDTO.getImage().getBytes().length < 1) {
            return errorService.multipartError();
        }
        if (auth instanceof User) {
            User user = (User) auth;


            Product product = new Product();
            product.setName(productDTO.getName());
            product.setPrice(productDTO.getPrice().multiply(new BigDecimal("100")));
            product.setQuantity(productDTO.getQuantity());
            product.setUser(user);
            product.setImage(imageService.uploadImage(productDTO.getImage(), Path.IMAGE_DIR));
            Product savedProduct = productRepo.save(product);
            log.info(CREATED);
            return ResponseEntity.ok(savedProduct);
        }
        return new ResponseEntity<>(new ApiError(USER_ERROR_404), HttpStatus.BAD_REQUEST);

    }

    public ResponseEntity<?> updateProduct(Long productid, ProductDTO productDTO, Errors error, Object auth) throws IOException, CustomException {
        if (error.hasErrors()) return errorService.MVCError(error);
        Optional<Product> oldProduct = productRepo.findById(productid);
        if (auth instanceof User) {
            User user = (User) auth;
            if (oldProduct.isPresent() && oldProduct.get().getUser().getId().equals(user.getId())) {
                Product newProduct = oldProduct.get();
                newProduct.setQuantity(productDTO.getQuantity());
                newProduct.setPrice(productDTO.getPrice().multiply(new BigDecimal("100")));
                newProduct.setName(productDTO.getName());

                if (productDTO.getImage() != null && productDTO.getImage().getBytes().length > 0) {
                    String newImage = imageService.uploadImage(productDTO.getImage(), Path.IMAGE_DIR);
                    imageService.deleteFile(newProduct.getImage());
                    newProduct.setImage(newImage);
                }
                log.info(UPDATED);
                return ResponseEntity.ok(productRepo.save(newProduct));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(errorService.customError(PRODUCT_ERROR_404, HttpStatus.NOT_FOUND));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(errorService.customError(USER_AUTH, HttpStatus.UNAUTHORIZED));
    }

    private BigDecimal balanceCalculate(BigDecimal userBalance, BigDecimal productPrice, double fee) {
        BigDecimal margin = marginCalculate(productPrice, fee);
        System.out.println(margin + "  margin");
        BigDecimal cleanPrice = productPrice.subtract(margin);
        System.out.println(cleanPrice + " clean price");
        return userBalance.add(cleanPrice);

    }

    private BigDecimal marginCalculate(BigDecimal productPrice, double fee) {
        return productPrice.multiply(new BigDecimal(String.valueOf(fee)));
    }

    @Transactional
    //todo jmeterit gateste
    public ResponseEntity<?> buyProduct(Long productid, String personalid, int quantity, String email, String masterCard) {
        Optional<Product> p = productRepo.findById(productid);
        if (p.isPresent()) {
            Product product = p.get();
            User user = product.getUser();
            if (product.getQuantity() >= quantity) {
                product.setQuantity(product.getQuantity() - quantity);
                user.setBalance(balanceCalculate(user.getBalance(), product.getPrice(), configProperties.getFee()));
                ProductOrder productOrder =
                        new ProductOrder(configProperties.getFee(),
                                marginCalculate(product.getPrice().multiply(new BigDecimal(String.valueOf(quantity))),configProperties.getFee()),
                                personalid, email, quantity, product);
                ProductOrder order = productOrderRepo.save(productOrder);
                productRepo.save(product);
                return ResponseEntity.ok(order);
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(errorService.customError(OUT_OF_STOCK, HttpStatus.BAD_REQUEST));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(errorService.customError(PRODUCT_ERROR_404, HttpStatus.NOT_FOUND));
    }

    public ResponseEntity<?> deleteProduct(Long productid,Object auth) {
        if (auth instanceof User) {
            User user = (User) auth;
            Optional<Product> p = productRepo.findById(productid);
            if (p.isPresent() && p.get().getUser().getId().equals(user.getId())) {
                Product product = p.get();
                //imageService.deleteFile(product.getImage());
                //productRepo.delete(product);
                p.get().setEnabled(false);
                return ResponseEntity.ok(productRepo.save(p.get()));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(errorService.customError(PRODUCT_ERROR_404, HttpStatus.NOT_FOUND));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(errorService.customError(USER_AUTH, HttpStatus.UNAUTHORIZED));
    }
}
